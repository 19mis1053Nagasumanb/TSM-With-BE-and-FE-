import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  isAuthPage: boolean = false;

  constructor(private router: Router) {}

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
}

  

