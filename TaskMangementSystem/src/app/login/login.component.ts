import { HttpClient } from '@angular/common/http';
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

  onLogin() {
    if (this.loginForm.valid) {
      const formData = this.loginForm.value;

      this.http.post('http://localhost:8090/api/auth/login', formData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('the response', response);
            if (response && response.toLowerCase().includes('login successful')) {
              const username = response.split('welcome')[1];
              alert('Login successful! Welcome '); // Optional success message
              this.authService.setUsername(username);
              this.router.navigate(['/task-form']); // Navigate to the dashboard page
            } else {
              alert('Login failed: ' + response); // Display failure message if the response is not successful
            }
          },
          error: (err) => {
            // Handle errors and display a meaningful error message
            alert('Login failed: ' + (err.error.message || err.error)); // Display error message if login fails
          }
        });
    } else {
      alert('Please fill all required fields correctly.');
    }
  }

}
