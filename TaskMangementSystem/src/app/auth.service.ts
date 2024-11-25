import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

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
}
