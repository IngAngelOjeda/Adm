import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { headers } from '../../shared/helpers/util';
import { ModificarClave } from '../models/modificar-clave.model';
import { MessageResponse } from '../../shared/models/message-response.model';

@Injectable({
  providedIn: 'root'
})
export class ModificarClaveService {

  private handler: HttpErrorHandler = new HttpErrorHandler();

  constructor(
    private http: HttpClient
  ) { }

  consultarToken(token: string): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`api/recuperar_clave/${token}`, { headers }).pipe(
      catchError(this.handler.handlePostError<MessageResponse>('consultarToken'))
    );
  }

  doModificarClave(token: string, data: ModificarClave): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`api/recuperar_clave/${token}`,data).pipe(
      catchError(this.handler.handlePostError<MessageResponse>('doModificarClave'))
    );
  }


}