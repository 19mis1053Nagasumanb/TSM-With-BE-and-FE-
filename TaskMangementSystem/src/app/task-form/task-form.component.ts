import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Task } from '../Task';
import { TaskService } from '../task.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']
})
export class TaskFormComponent {
  constructor(private taskService: TaskService , private router:Router) {}
  maxDate: string = new Date().toISOString().split('T')[0];  


  entry = {
    date: '',
    day: '',
    time: '',
    name: '',
    task: '',
    status: '',
    priority: '',
    logHours: ''
  };
  
  names: string[] = []; 
  successMessageVisible = false;

  onDateChange(event: any) {
    const date = new Date(event.target.value);
    this.entry.day = date.toLocaleDateString('en-US', { weekday: 'long' });
    this.entry.date = date.toISOString().split('T')[0]; // Set in "yyyy-MM-dd" format
  }

  
  onTimeChange(event: any) {
    const timeValue = event.target.value; // Get the full time string
    const timeParts = timeValue.split(':'); // Split it into parts

    if (timeParts.length === 2) {
      const hours = timeParts[0].padStart(2, '0'); // Ensure hours are two digits
      const minutes = timeParts[1].padStart(2, '0'); // Ensure minutes are two digits
      this.entry.time = `${hours}:${minutes}`; // Store as HH:MM format
    } else {
      console.error('Invalid time format:', timeValue);
    }
  }

  

  onSubmit(form: NgForm) {
    try {
      // Converting date to epoch milliseconds
      const dateInput = new Date(this.entry.date);
      if (isNaN(dateInput.getTime())) {
        console.error('Invalid date input:', this.entry.date);
        return; // Prevent sending invalid date
      }
      const epochDate = dateInput.getTime();
  
      // Convert time to milliseconds
      const timeParts = this.entry.time.split(':');
      if (timeParts.length !== 2) {
        console.error('Invalid time format:', this.entry.time);
        return; // Prevent sending invalid time
      }
      const hours = parseInt(timeParts[0], 10);
      const minutes = parseInt(timeParts[1], 10);
      
      // Calculate time in milliseconds
      const timeInMillis = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
  
      // Prepare the payload
      const payload = {
        ...this.entry,
        date: epochDate, // Set epoch date
        time: timeInMillis, // Set time in milliseconds
      };
  
      console.log('Payload to be sent:', payload);
  
      this.taskService.createTask(payload).subscribe(
        response => {
          console.log('Task created successfully', response);
          this.successMessageVisible = true;
          form.reset();
          this.router.navigate(['/task-list']);

        },
        error => {
          console.error('Error creating task', error);
          if (error.error && error.error.message) {
            console.error('Error details:', error.error.message);
          } else {
            console.error('Error details:', error);
          }
        }
      );
    } catch (error: unknown) {
      if (error instanceof Error) {
        console.error('Error preparing date or time:', error.message);
      } else {
        console.error('Unexpected error occurred:', error);
      }
    }
  }
  
  
  getFormattedDate(): string {
    const date = this.entry.date ? new Date(this.entry.date) : new Date(); // Use current date if entry.date is not set
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
    const day = date.getDate().toString().padStart(2, '0');
    
    return `${year}-${month}-${day}`; // Correct format yyyy-MM-dd
  }
  
  
   // You might also need this for time formatting
   getFormattedTime(): string {
    const time = this.entry.time || '00:00:00'; // Default time if not set
    return time;
  }
  
  
  

 
  onNameChange(event: any): void {
    this.entry.name = event.target.value;
  }
}
