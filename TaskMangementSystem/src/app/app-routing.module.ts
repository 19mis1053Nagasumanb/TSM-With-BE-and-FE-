import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaskFormComponent } from './task-form/task-form.component';
import { TaskListComponent } from './task-list/task-list.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  {path:'register',component:RegisterComponent},
  { path: '', redirectTo: 'register', pathMatch: 'full' }, // Redirect root to /register


  {path:'task-form', component:TaskFormComponent},
  {path:'task-list',component:TaskListComponent},


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { } 


