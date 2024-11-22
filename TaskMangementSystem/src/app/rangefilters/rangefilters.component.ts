  import { HttpClient } from '@angular/common/http';
  import { Component, EventEmitter, Output } from '@angular/core';
  import { Task } from '../Task';
import { response } from 'express';
import { error } from 'console';

  @Component({
    selector: 'app-rangefilters',
    templateUrl: './rangefilters.component.html',
    styleUrl: './rangefilters.component.scss'
  })
  export class RangefiltersComponent {
    @Output() filteredTasks = new EventEmitter<Task[]>(); 
    maxDate: string = new Date().toISOString().split('T')[0];  
    @Output() filtersChanged = new EventEmitter<any>();


    
    selectedPriority: string = '';
    selectedStatus: string = '';
    startDate: string = '';
    endDate: string = '';
    errorMessage: string = ''; 

    selectedField:string ="name";
    searchQuery:string = '';
    searchResults: any[] =[];
    tasks :Task[] = [];

     // Options for ng-select
  priorityOptions = [
    { name: 'URGENT', value: 'URGENT' },
    { name: 'HIGH', value: 'HIGH' },
    { name: 'NORMAL', value: 'NORMAL' },
    { name: 'LOW', value: 'LOW' },
    ]; 

    statusOptions = [
      { name: 'TODO', value: 'TODO' },
      { name: 'PENDING', value: 'PENDING' },
      { name: 'ON HOLD', value: 'ON_HOLD' },
      { name: 'DONE', value: 'DONE' },
      { name: 'IN TEST', value: 'IN_TEST' },
      { name: 'IN PROGRESS', value: 'IN_PROGRESS' },
    ];



    constructor(private http: HttpClient) {}

    onStartDateChange(): void {
      // Clear end date if start date is changed
      this.endDate = "";
      this.applyFilters();
    }

    applyFilters() { 

      if (!this.selectedPriority && !this.selectedStatus  && !this.startDate &&  !this.endDate ) {
        this.errorMessage = '';
        return;
      }

      this.errorMessage ='';

      // console.log("Selected Filters:", {
      //   selectedPriority: this.selectedPriority,
      //   selectedStatus: this.selectedStatus,
      //   startDate: this.startDate,
      //   endDate: this.endDate,
      // }); 

      const filters = {
        priority:this.selectedPriority,
        status:this.selectedStatus,
        startDate:this.startDate,
        endDate:this.endDate,
        searchQuery:this.searchQuery,
      };
      this.filtersChanged.emit(filters);

      
      if (!this.startDate && this.endDate) {
        const startDateEpoch = this.convertToEpoch(this.startDate);
        const endDateEpoch = this.convertToEpoch(this.endDate);
        if(startDateEpoch > endDateEpoch) {
          this.errorMessage = 'start date cannot be greater than the end date';
          return;
        }
      }
      this.errorMessage = ''; // Reset error message //hold

      // Construct the query
      const filterQuery = this.constructFilterQuery();
      console.log("Filter Query:", JSON.stringify(this.constructFilterQuery()));

      // Make HTTP request to fetch filtered data
      this.http.post('optimizedes/_search', filterQuery).subscribe(
        (response: any) => {
          // console.log('Filtered Data:', response);
          const filteredData = response.hits.hits.map((hit: any) => ({
            ...hit._source,
            date: hit._source.date ? this.formatDate(hit._source.date) : '',
          time: hit._source.time ? this.formatTime(hit._source.time) : '',

          })); 

          if (filteredData.length ===0 ) {
            this.errorMessage = "No Data fonund on selceted field";
          }
          // console.log(" mapped filterd data: ",filteredData);
          this.filteredTasks.emit(filteredData);
        },
        (error) => {
          console.error('Error fetching filterted results:', error);
          this.errorMessage = 'Error fetching filtered results. Please try again.';
        }
        
      ); 

      // console.log("Final Filter Query: ", filterQuery); 
    }

    // tasks: any[] = []; // Replace `any[]` with the actual type of your tasks
    originalTasks: any[] = []; // Store the original data for filtering
    totalTasks: number = 0; // Total number of tasks
    noDataFound: boolean = false; // Indicate if no data was found
    currentPage: number = 0; // Current page in pagination

    loadTasks() {
      // Implement logic to load tasks, e.g., from a service or API
      console.log('Loading tasks...');
    }

    onFilteredTasks(filteredData: Task[]): void {
      console.log('Filtered tasks received:', filteredData);
      this.tasks = filteredData.length ? filteredData : this.originalTasks;
      this.totalTasks = this.tasks.length;
      this.noDataFound = this.tasks.length === 0;
      this.currentPage = 0;
      this.loadTasks();
    }
    selectField(field: string) {
      this.selectedField = field;
    }
    clearSearch(){
      this.searchQuery ='';
      this.searchResults =[];
      this.applyFilters(); // Reapply filters with cleared search

      this.filteredTasks.emit([]);
    } 

    search() {
      let endpoint = `http://localhost:8090/apis/autoSuggest/${this.selectedField}/${encodeURIComponent(this.searchQuery)}`;
      this.http.get<any[]>(endpoint).subscribe(
        (response) => {
          this.searchResults = response;
          this.searchResults = Array.from(new Set(this.searchResults.map(task => task.id)))
        .map(id => this.searchResults.find(task => task.id === id));
        },
        (error) => {
          console.error('Error fetching search results:', error);
        }
      );
    }
    

    // selectSuggestion(suggestion: any) {
    //   const endpoint = `http://localhost:8090/apis/search?query=${encodeURIComponent(suggestion)}`;
    //   console.log(`Fetching data for suggestion: ${suggestion}`);

    //   this.http.get<any[]>(endpoint).subscribe(
    //     (response) => {
    //       this.tasks = response;
    //       console.log('Received table data:', this.tasks);

    //       this.filteredTasks.emit(this.tasks);
    //       // this.resultsUpdated.emit(this.tasks); // Emit the tasks fetched

    //     },
    //     (error) => {
    //       console.error('Error fetching suggestion details:', error);
    //       this.tasks = [];
    //     }
    //   );
    //   this.searchQuery = suggestion;
    //   this.applyFilters();
    // }
 
    // selectSuggestion(suggestion: any) {
    //   this.searchQuery = suggestion; // Set the search query
    //   this.applyFilters(); // Reapply filters with the selected suggestion
    // } 

    selectSuggestion(suggestion: any) {
      // Set the search query to the selected suggestion
      this.searchQuery = suggestion;
    
      // Log the suggestion
      console.log(`Fetching data for suggestion: ${suggestion}`);
    
      // Reapply filters, including the new search query
      const filterQuery = this.constructFilterQuery();
    
      // Fetch filtered data from the backend
      this.http.post('optimizedes/_search', filterQuery).subscribe(
        (response: any) => {
          const filteredData = response.hits.hits.map((hit: any) => ({
            ...hit._source,
            date: hit._source.date ? this.formatDate(hit._source.date) : '',
            time: hit._source.time ? this.formatTime(hit._source.time) : '',
          }));
          console.log('Filtered tasks received:', filteredData);
    
          this.tasks = filteredData;
          this.filteredTasks.emit(this.tasks);
        },
        (error) => {
          console.error('Error fetching suggestion details:', error);
          this.tasks = [];
        }
      );
    }
    
    
    constructFilterQuery() {
      const query: any = {
        size: 500, 
        query: { bool: {must: [] } },
      };

      if (this.selectedPriority) {
        query.query.bool.must.push({
          match: { 'priority.keyword': this.selectedPriority }
        });
      }
      if (this.selectedStatus) {
        query.query.bool.must.push({
          match: { 'status.keyword': this.selectedStatus }
        });
      }
      if (this.startDate || this.endDate) {
        const dateRange: any = {};
        if(this.startDate) {
          dateRange.gte = this.convertToEpoch(this.startDate);
        }
        if (this.endDate) {
          dateRange.lte = this.convertToEpoch(this.endDate);
        }
        query.query.bool.must.push({
          range:  {date: dateRange},
        })
      }
      if (this.searchQuery) {
        query.query.bool.must.push({
          match: { [`${this.selectedField}.keyword`]: this.searchQuery },
        });
      }
    return query;
    }

    convertToEpoch(dateString: string): number {
      return new Date(dateString).getTime();
    }
    formatDate(epoch: number): string {
      const date = new Date(epoch);
      return date.toISOString().split('T')[0];
    }
    formatTime(epoch: number): string {
      const date = new Date(epoch);
      return date.toISOString().substr(11, 8);
    }
    convertTimeToMillis(timeString: string): number {
      const [hours, minutes] = timeString.split(':').map(Number);
      return (hours * 60 * 60 * 1000) + (minutes * 60 * 1000); 
    }
    onFilterChange(filteredTasks: Task[]): void {
      this.filteredTasks.emit(filteredTasks);
    } 
    


  }
