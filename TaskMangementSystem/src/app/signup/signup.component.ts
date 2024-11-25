import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { log } from 'console';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  signupForm: FormGroup;

  constructor(private http: HttpClient,
     private fb: FormBuilder,
    private router:Router) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.pattern(/.+@(gmail\.com|techatcore\.com)$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSignup() {
    if (this.signupForm.valid) {
      const formData = this.signupForm.value;
      console.log('the data', formData);

      this.http.post('http://localhost:8090/api/auth/signup', formData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            if(response === 'User registered successfully') {
              alert(response); // Display success message
               this.router.navigate(['/login']);
            } else {
              alert(response);
            }
          },
          error: (err) => {
            alert('Signup failed: ' + err.error); // Display error message
          }
        });
    } else {
      alert('Please fill all required fields correctly.');
    }
  }

}
