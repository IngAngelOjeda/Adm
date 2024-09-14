import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { Requisito } from '../models/requisito.model';

@Injectable({
  providedIn: 'root'
})
export class RequisitoService {

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

    getRequisitos(): Observable<MessageResponse>  {
        this.loading.next(true);
        return this.http.get<MessageResponse>("api/requisito/list", { headers })
        .pipe(finalize(() => { this.loading.next(false); }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("obtenerRequisitos",null)));
    }

    getRequisitosbyId(id: number): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.get<MessageResponse>(`api/requisito/${id}/list`, { headers })
        .pipe(finalize(() => { this.loading.next(false); }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("obtenerRequisitosIdServicio", null)));
    }

}
