import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  isRegisterPage: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Listen for route changes to update `isRegisterPage`
    this.router.events.subscribe(() => {
      this.isRegisterPage = this.router.url.includes('/register');
    });
  }
}
