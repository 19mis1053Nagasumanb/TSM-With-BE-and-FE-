import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm: FormGroup;

  constructor(
    private http: HttpClient, 
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('jwtToken');
    return {
      Authorization: `Bearer ${token}`,
    };
  }
  
  onLogin() {
    if (this.loginForm.valid) {
      const formData = this.loginForm.value;
  
      this.http.post('http://localhost:8090/api/auth/login', formData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('Received response:', response);
  
            // Assuming the response is a JWT token
            if (response) {
              // Save the JWT token in localStorage
              localStorage.setItem('jwtToken', response);
  
              // Decode the token to extract the username
              const tokenParts = response.split('.');
              const payload = JSON.parse(atob(tokenParts[1])); // Decode the JWT payload
              const username = payload.sub; // Assuming "sub" contains the email/username
              alert(`Login successful! Welcome, ${username}`);
  
              // Fetch the role from the backend
              this.http.get<{ role: string}>('http://localhost:8090/api/auth/user-info', { headers: this.getAuthHeaders() })
                .subscribe({
                  next: (response) => {
                    const role = response.role ;
                    localStorage.setItem('userRole', role); // Store role in localStorage
                    this.authService.setUsername(username);
                    this.router.navigate(['/task-form']); // Navigate to task form
                  },
                  error: (err) => {
                    alert('Error fetching role: ' + err.message);
                  }
                });
            } else {
              alert('Login failed: No token received'); // Display failure message if the response is not a token
            }
          },
          error: (err) => {
            alert('Login failed: ' + (err.error.message || err.error)); // Display error message if login fails
          }
        });
    } else {
      alert('Please fill all required fields correctly.');
    }
  }
  
}
