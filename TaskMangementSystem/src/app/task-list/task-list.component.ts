import { Component, OnInit } from '@angular/core';
import { Task } from '../Task';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})

export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  originalTasks: Task[] = []; 
  isEditingTask: boolean = false;
  editedTask: Task | null = null; 
  paginatedTasks: Task[] = []; 
  filteredTasks: any[] = [];

  currentPage: number = 0; 
  pageSize: number = 10; 
  totalTasks: number = 0;  

  noDataFound: boolean = false; 
  statusOptions = ['TODO', 'PENDING', 'ON_HOLD', 'DONE','IN_TEST','IN_PROGRESS']; 
  
  constructor(private taskService: TaskService) {}
  
  ngOnInit(): void {
    this.getTasks(); 
  }

  getTasks(): void {
    this.taskService.getAllTasks().subscribe(
      (tasks: Task[]) => {
        console.log('Fetched tasks:', tasks); 
        this.originalTasks = tasks; 
        this.totalTasks = tasks.length; // Set total tasks for pagination
        // this.tasks = tasks; 
        this.currentPage =0;
        // console.log('Total Tasks:', this.totalTasks); // Log total tasks

        this.loadTasks(); 



      },
      (error) => {
        console.error('Error fetching tasks:', error); 
      }
    );
  } 
  
  loadTasks(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    // console.log('Current Page:', this.currentPage, 'Page Size:', this.pageSize);
    // console.log('Start Index:', startIndex, 'End Index:', endIndex);
    // console.log('Tasks:', this.tasks);  // Check if tasks array has data
    // this.tasks = this.originalTasks.slice(startIndex, endIndex); // Paginate tasks
    // this.paginatedTasks = this.originalTasks.slice(startIndex, endIndex);
    this.paginatedTasks = this.tasks.length 
    ? this.tasks.slice(startIndex, endIndex) 
    : this.originalTasks.slice(startIndex, endIndex);

    console.log('Paginated Tasks:', this.paginatedTasks);
    // console.log('Current tasks:', this.tasks); // Debugging line to see current tasks

  }
  goToPage(page: number): void {
    this.currentPage = page-1; // Update current page (0-based index)
    this.loadTasks(); // Load tasks for the selected page\
    // this.currentPage = page; // Update the current page to the clicked page

  }

  getPageNumbers(): number[] {
    const totalPages = Math.ceil(this.totalTasks / this.pageSize); // Calculate total pages
    // console.log('Total Pages:', totalPages); // Debugging line
    // console.log('Total Tasks:', this.totalTasks); // Debugging line
    // console.log('Page Size:', this.pageSize); // Debugging line
    return Array.from({ length: totalPages }, (_, index) => index + 1); // Generate page numbers
  }  

  search(newTasks: Task[]): void {
    this.tasks = newTasks.length ? newTasks : this.originalTasks;
    this.totalTasks = this.tasks.length;  // Update total tasks count for pagination
    this.currentPage = 0;                 // Reset to the first page
    this.loadTasks();                     // Load tasks for the current page
  }
  
  startEditTask(task: Task): void {
    this.isEditingTask = true;
    this.editedTask = { ...task }; 
  }
  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'URGENT':
        return 'red'; 
      case 'HIGH':
        return 'orange';
      case 'NORMAL':
        return 'blue'; 
      case 'LOW':
        return 'green'; 
      default:
        return 'black'; 
    }
  }
  
  getStatusColor(status: string): string {
    switch (status) {
      case 'TODO':
        return 'orange';
      case 'PENDING':
        return 'purple';
      case 'ON_HOLD':
        return 'red';
      case 'DONE':
        return ' green';
      case 'IN_TEST':
        return 'gray';
      case 'IN_PROGRESS':
        return 'blue';
      default:
        return 'black';
    }
  }
  
  updateTask(): void {

    if (this.editedTask?.id) {
      const transformedTask = this.transformTaskForUpdate(this.editedTask);

      console.log('Updating task with payload:', transformedTask);

      this.taskService.updateTask(this.editedTask.id, transformedTask).subscribe(
        () => {
          this.isEditingTask = false;
          this.editedTask = null; 
          this.getTasks(); 

          alert('Task updated successfully');
        },
        (error: any) => {
          console.error('Error updating task:', error);
          alert('Error updating task: ' + (error.message || 'Unknown error'));
        }
      );
    }
  }

  private transformTaskForUpdate(task: Task): any {
    const epochDate = new Date(task.date).getTime();

    const [hours, minutes, seconds] = task.time.split(':').map(Number);
    const totalMilliseconds = (hours * 3600 + minutes * 60 + seconds) * 1000;

    return {
      ...task,
      date: epochDate, 
      time: totalMilliseconds, 
    };
  }
  
  toggleEditMode(isEditing:boolean) :void {
    this.isEditingTask = isEditing;
   }

   cancelEdit(): void {
    this.toggleEditMode(false);
    this.editedTask = null;
    this.isEditingTask = false
   }


 

  deleteTask(id: string): void {
    if (confirm("Are you sure you want to delete this task?")) {
      this.taskService.deleteTask(id).subscribe(
        (response) => {
          console.log(response);
          this.tasks = this.tasks.filter(task => task.id !== id);
        },
        (error) => {
          console.error('Error deleting task:', error);
        }
      );
    }
  }

  
formatTime(timeString: string): string {
  if (!timeString) {
      return ''; 
  }
  return timeString.trim(); 
}

// onFilteredTasks(filteredTasks: Task[]) {
//   this.paginatedTasks = filteredTasks; 
//   this.currentPage =1;
// } 
onFilteredTasks(filteredTasks: Task[]): void {
  this.tasks = filteredTasks.length ? filteredTasks : this.originalTasks;
  this.totalTasks = this.tasks.length;  // Update total tasks count
  this.noDataFound = this.tasks.length === 0;  // Check if there are no tasks after filtering

  this.currentPage = 0;                 // Reset to the first page
  this.loadTasks();                     // Load tasks for the current page
  // this.search(filteredTasks);
} 

sortOrder: { [key: string]: string } = {}; // Store the sort order for each field
sortField: string = '';  // Current field being sorted

toggleSort(field: string): void {
  // Toggle the sort order based on current state
  if (this.sortField === field) {
    this.sortOrder[field] = this.sortOrder[field] === 'asc' ? 'desc' : 'asc';
  } else {
    this.sortField = field;
    this.sortOrder[field] = 'asc'; // Default to ascending if it's a new field
  }

  this.sortTasks(field, this.sortOrder[field]);
}

sortTasks(field: string, order: string): void {
  console.log(`Sorting by ${field}, Order: ${order}`);

  this.taskService.sortTasks(field, order).subscribe(
    (sortedTasks) => {
      this.paginatedTasks = sortedTasks;
      console.log('Sorted Tasks:', sortedTasks);
    },
    (error) => {
      console.error(`Error sorting tasks by ${field}:`, error);
    }
  );
}

// Determine which icon to show for sorting
sortIcon(field: string): string {
  if (this.sortField === field) {
    return this.sortOrder[field] === 'asc' ? '&#x25B2;' : '&#x25BC;';
  } else {
    return '&#x25B2;'; // Default to up arrow
  }
}

SortIconClass(field:string): string {
  if(this.sortField === field) { 
    return this.sortOrder[field] === 'asc' ? 'icon-green' : 'icon-red';

  }
  return'icon-default';
}
}
