import { Component, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  isRegisterMode = false;
  name = '';
  email = '';
  password = '';

  toggleForm(isRegister: boolean): void {
    this.isRegisterMode = isRegister;
  }

  onRegister(): void {
    // Handle registration logic
    console.log('Registering with:', this.name, this.email, this.password);
  }

  onLogin(): void {
    // Handle login logic
    console.log('Logging in with:', this.email, this.password);
  }

  

  

  
}
