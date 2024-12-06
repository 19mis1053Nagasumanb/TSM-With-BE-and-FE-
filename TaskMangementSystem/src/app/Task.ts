export interface Task {

  id?: string;
  username: string;
  task: string;
  day: string;

  priority: string;
  logHours: string;
  status: string;
  date: string | number;
  time: string | number;
}
