import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TaskStateServiceService {
  private isEditingTaskSubject = new BehaviorSubject<boolean>(false);
  isEditingTask$ = this.isEditingTaskSubject.asObservable();

  setEditingTask(isEditing: boolean) {
    console.log('setting isEditing in service:', isEditing);
    this.isEditingTaskSubject.next(isEditing);
  }

}
