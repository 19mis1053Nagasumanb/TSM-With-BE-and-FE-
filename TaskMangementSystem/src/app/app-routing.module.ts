import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaskFormComponent } from './task-form/task-form.component';
import { TaskListComponent } from './task-list/task-list.component';
import { SignupComponent } from './signup/signup.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  {path:'register',component:RegisterComponent},
  // { path: '', redirectTo: 'register', pathMatch: 'full' }, // Redirect root to /register
  {path: '',redirectTo: 'signup', pathMatch: 'full'},
  {path:'signup', component:SignupComponent},
  {path:'login', component:LoginComponent},
  {path:'task-form', component:TaskFormComponent},
  {path:'task-list',component:TaskListComponent},


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { } 


