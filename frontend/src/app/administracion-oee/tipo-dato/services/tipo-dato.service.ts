import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { TipoDato } from '../models/tipo-dato.model';

@Injectable({
  providedIn: 'root'
})
export class TipoDatoService {

    private handler: HttpErrorHandler = new HttpErrorHandler();
    private loading = new BehaviorSubject<boolean>(false);

    constructor(
      private http: HttpClient
    ) { }

    connect(): Observable<boolean> {
      return this.loading.asObservable();
    }

    disconnet(): void {
      this.loading.complete();
    }

    getTipoDato(): Observable<MessageResponse>  {
        this.loading.next(true);
        return this.http.get<MessageResponse>("api/tipoDato/list", { headers })
        .pipe(finalize(() => { this.loading.next(false); }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("obtenerTipoDato",null)));
    }

}
