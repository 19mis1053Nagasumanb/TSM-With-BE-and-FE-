import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  isAuthPage: boolean = false;


  constructor(private router: Router, private authService: AuthService) {}


  ngOnInit(): void {
    this.checkCurrentRoute();



    // Subscribe to route changes to dynamically update the state
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.checkCurrentRoute();
      }
    });
  }

  private checkCurrentRoute(): void {
    const authRoutes = ['/login', '/signup'];
    this.isAuthPage = authRoutes.includes(this.router.url);
  } 

   // Logout method
   logout(): void {
    const confirmation = window.confirm('Are you sure you want to logout?');
    if (confirmation) {
      this.authService.logout();
      this.router.navigate(['/login']);  // Navigate to the login page
    }
  }
}

  

