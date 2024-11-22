import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Task } from '../Task';
import { TaskStateServiceService } from '../task-state-service.service';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent implements OnInit {
  @Output() resultsUpdated = new EventEmitter<any[]>(); 

  @Output() searchChanged = new EventEmitter<string>();


  isEditingTask:boolean = true;
  
  ngOnInit(): void {
    this.taskStateServiceService.isEditingTask$.subscribe(isEditing => {
      console.log('searchComponent isEditingTask:', isEditing);
      this.isEditingTask = isEditing;
      this.cd.detectChanges();
    })
  }

  selectedField: string = 'name'; 
  searchQuery: string = '';
  searchResults: any[] = [];
  tasks: any[] = []; 
  paginatedTasks : Task[] =[]
  constructor(private http: HttpClient , private taskStateServiceService: TaskStateServiceService , private cd: ChangeDetectorRef) {}

 

  selectField(field: string) {
    this.selectedField = field;
  }
  clearSearch() {
    this.searchQuery = '';
    this.searchResults = []; 
    this.resultsUpdated.emit([]); 

  }
  search() {
    let endpoint = 'http://localhost:8090/apis/autoSuggest';
    if (this.selectedField) {
      endpoint += `/${this.selectedField}`;
    }
    if (this.searchQuery) {
      endpoint += `/${encodeURIComponent(this.searchQuery)}`;
    }
    console.log(`Making request to: ${endpoint}`);
    this.http.get<any[]>(endpoint).subscribe(
      (response) => {
        this.searchResults = response;
        this.searchResults = Array.from(new Set(this.searchResults.map(task => task.id)))
        .map(id => this.searchResults.find(task => task.id === id));
        this.resultsUpdated.emit(response); 
                console.log(response);


      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );  

    this.searchChanged.emit(this.searchQuery);


  }
  selectSuggestion(suggestion: string) {
    const endpoint = `http://localhost:8090/apis/search?query=${encodeURIComponent(suggestion)}`;
    console.log(`Fetching data for suggestion: ${suggestion}`);
    // Call the API to get detailed information for the selected suggestion
    this.http.get<any[]>(endpoint).subscribe(
      (response) => {
        console.log("data",response);
     
        this.tasks = response;
        console.log('Received table data:', this.tasks);
        this.resultsUpdated.emit(this.tasks); // Emit the tasks fetched

      },
      (error) => {
        console.error('Error fetching table data:', error);
        this.tasks = [];
      }
    );
  } 
}

