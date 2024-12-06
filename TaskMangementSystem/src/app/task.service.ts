import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, Subject } from 'rxjs';
import { Task } from './Task';
import { AuthService } from './auth.service';
import { error } from 'console';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  
  private apiUrl = 'http://localhost:8090/apis';
  private elasticsearchUrl = '/optimizedes/_search';


  constructor(private http: HttpClient, private authService: AuthService) { }

  public getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('jwtToken');
    return {
      Authorization: `Bearer ${token}`,
    };
  }
  
  
  getAllTasks(): Observable<Task[]> {
    const token = localStorage.getItem('jwtToken');
    const userRole = localStorage.getItem('userRole');
  
    if (!token || !userRole) {
      return of([]); // Handle case where token or role is missing
    }
  
    let endpoint = '';
    if (userRole === 'USER') {
      endpoint = '/user-tasks'; // Endpoint for regular users
    } else if (userRole === 'ADMIN') {
      endpoint = '/findAll'; // Endpoint for admin users
    } else {
      return of([]); // Handle unrecognized roles
    }
  
    return this.http
      .get<Task[]>(`${this.apiUrl}${endpoint}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          console.error('Error fetching tasks:', error);
          return of([]); // Return empty array in case of error
        })
      );
  }
  
  
  
  // createTask(task: any): Observable<string> {
  //   const token = this.authService.getToken();
  //   const headers = {
  //     Authorization: `Bearere ${token}`
  //   };
  //   return this.http.post<string>(`${this.apiUrl}/insert`, task, {headers});
  // } 

  // createTask(task: any): Observable<string> {
  //   const token = localStorage.getItem('jwtToken'); // Assuming the token is stored in localStorage
  //   const headers = {
  //     Authorization: `Bearer ${token}`,
  //   };
  
  //   return this.http.post<string>(`${this.apiUrl}/insert`, task, { headers });
  // } 

  createTask(task: Task): Observable<string> {
    return this.http
      .post<string>(`${this.apiUrl}/insert`, task, { headers: this.getAuthHeaders() })
      .pipe(catchError((error) => of(error.message || 'Task creation failed')));
  }

  
  
  
  updateTask(id: string, task: Task): Observable<Task> {
    return this.http.
    put<Task>(`${this.apiUrl}/${id}`, task, {headers: this.getAuthHeaders() })
    .pipe(catchError((error) => of(error.message || 'Task update failed')));
  }
  

  deleteTask(id: string): Observable<string> {
    return this.http
    .delete<string>(`${this.apiUrl}/${id}`,{ headers: this.getAuthHeaders() })
    .pipe(catchError((error)=> of(error.message || 'Task deletion failed')));
  }

  

  // getTasks(page: number, size: number): Observable<Task[]> {
  //   const params = new HttpParams()
  //     .set('page', page)
  //     .set('size', size);
  
  //   return this.http.get<Task[]>(`${this.apiUrl}/findAll`, { params }).pipe(
  //     map(response => {
  //       console.log('Fetched tasks:', response);
  //       return response;
  //     }),
  //     catchError((error: any) => {
  //       console.error('Error fetching tasks:', error);
  //       return of([]); 
  //     })
  //   );
  // } 

  
  
  
   // Fetch paginated tasks
  //  getPaginatedTasks(page: number, size: number): Observable<{ tasks: Task[], total: number }> {
  //   return this.http.get<{ tasks: Task[], total: number }>(`${this.apiUrl}?page=${page}&size=${size}`);
  // }

  
  
  
  fuzzySearch(approximateTaskName: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/fuzzySearch/${approximateTaskName}`);
  }

  // autoSuggest(field: string, searchTerm: string): Observable<string[]> {
  //   return this.http.get<string[]>(`${this.apiUrl}/autoSuggest/${field}/${searchTerm}`);
  // }
  sortTasks(field: string, order: string): Observable<any[]> {
    
      const fieldName= field == 'date' || field == 'time' ? field: `${field}.keyword`;
    

    const body = {
      query: { match_all: {} },
      sort: [{ [fieldName]: { order: order } }]
    };

    return this.http.post<any>(this.elasticsearchUrl, body).pipe(
      map(response => {
        return response.hits.hits.map((hit: any) => {
          const task = hit._source;
          // task.date = new Date(task.date).toLocaleDateString();
          // task.time = new Date(task.time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
          task.date = this.convertEpochToDate(task.date);
          task.time = this.convertEpochToTime(task.time);
          return task;
        });
      })
    );
  }
  convertEpochToDate(epoch: number):string {
    const date = new Date(epoch);
    return date.toISOString().split('T')[0];
  }
  convertEpochToTime(epoch: number):string {
    const date = new Date(epoch);
    return date.toTimeString().split(' ')[0];
  } 


   
  
   



}
