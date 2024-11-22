import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, Subject } from 'rxjs';
import { Task } from './Task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  
  private apiUrl = 'http://localhost:8090/apis';
  private elasticsearchUrl = '/optimizedes/_search';


  constructor(private http: HttpClient) { }

  
  getAllTasks(): Observable<Task[]> {
    console.log("Hitting getAll");
    
    return this.http.get<Task[]>(`${this.apiUrl}/findAll`).pipe(
      map(response => {
        return response; 
      }),
      catchError((error: any) => {
        console.error('Error fetching tasks:', error);
        return of([]); 
      })
    );
  }
  createTask(task: any): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/insert`, task);
  }
  
  
  updateTask(id: string, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }
  

  deleteTask(id: string): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
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
