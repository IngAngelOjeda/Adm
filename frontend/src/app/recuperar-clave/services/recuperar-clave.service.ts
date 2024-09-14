import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { RecuperarClave } from '../models/recuperar-clave.model';
import { MessageResponse } from '../../shared/models/message-response.model';

@Injectable({
  providedIn: 'root'
})
export class RecuperarClaveService {

  private handler: HttpErrorHandler = new HttpErrorHandler();

  constructor(
    private http: HttpClient
  ) { }

  doRecuperarClave(data: RecuperarClave): Observable<MessageResponse> {
    return this.http.post<MessageResponse>('api/auth/reset-pass',data).pipe(
      catchError(this.handler.handlePostError<MessageResponse>('doRecuperarClave'))
    );
  }


}