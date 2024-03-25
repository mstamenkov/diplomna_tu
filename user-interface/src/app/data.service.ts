import { HttpClient, HttpHeaders } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class DataService {

  constructor(private http : HttpClient, public snackBar:MatSnackBar) { }
  url : string = 'http://localhost:8080';
  @Output() event_callback: EventEmitter<any> = new EventEmitter();

  getCommands(){
    return this.http.get(this.url+'/commands').pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }
  
  getCommandDetails(id:number){
    return this.http.get(this.url+'/command/'+id).pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }

  postRequest(request:any){
    return this.http.post<any>(this.url + '/execution/execute', request).pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }

  deleteRequest(id:number){
    return this.http.delete<any>(this.url + '/command/' +id).pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }

  getExecutions(){
    return this.http.get(this.url + '/executions').pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }

  getExecutionById(id:string){
    return this.http.get(this.url + '/execution/' + id).pipe(catchError((error) => {
      this.sendErrorMessage(error);
      return throwError(() => new Error(error.message));
    }));
  }

  sendErrorMessage(error: any){
    console.log(error);
    this.event_callback.emit(error);
  }
}
