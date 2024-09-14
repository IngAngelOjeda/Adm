import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { Login } from '../models/login.model';
import { LoginResponse } from '../../shared/models/login-response';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private handler: HttpErrorHandler = new HttpErrorHandler();

  constructor(
    private http: HttpClient,
    private storageManager: StorageManagerService
  ) { }

  doLogin(data: Login): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('api/auth/login',data).pipe(
      catchError(this.handler.handlePostError<LoginResponse>('doLogin'))
    );
  }

  refreshToken(accessToken: string, refreshToken: string) {
    const httpOptions = { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) };
    return this.http.post('api/auth/refreshToken', { accessToken, refreshToken }, httpOptions);
  }

  doLogout() {
    this.storageManager.deleteStorage();
  }

}
