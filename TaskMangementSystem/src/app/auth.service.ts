import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}

  private usernameKey = 'username'; // Key for localStorage
  
  setUsername(username: string): void {
    localStorage.setItem(this.usernameKey, username); // Save to localStorage
  }

  getUsername(): string | null {
    return localStorage.getItem(this.usernameKey); // Retrieve from localStorage
  }

  clearUsername(): void {
    localStorage.removeItem(this.usernameKey); // Clear from localStorage (optional)
  } 

    // Example of getting user ID from localStorage (adjust based on your auth method)
    getCurrentUserId(): string {
      const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
      return user ? user.id : '';  // Adjust based on your user data structure
    } 
    logout(): void {
      // Remove token from localStorage or sessionStorage
      localStorage.removeItem('jwtToken');  // or sessionStorage.removeItem('jwtToken');
    }

    
}
