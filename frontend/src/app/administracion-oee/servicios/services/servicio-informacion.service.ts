import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { ServicioInformacion } from '../models/servicio-informacion.model';

@Injectable({
  providedIn: 'root'
})
export class ServicioInformacionService {

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

    getAll(filter: string, pageSize: number, start: number, sortField: string, sortAsc: boolean, advancedFilter: any, idServicio: number): Observable<MessageResponse> {
      this.loading.next(true);
  
      let params = new HttpParams();
      if (filter) params = params.set('filter', filter);
      if (sortField) params = params.set('sortField', sortField);
  
      params = params
          .set('page', `${Math.ceil(start / pageSize)}`)
          .set('pageSize', `${pageSize}`)
          .set('idServicio', `${idServicio}`)
          .set('sortAsc', `${sortAsc}`);
  
      Object.keys(advancedFilter).map((key) => {
          const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
          params = params.set(key, searchValue);
      });
  
      return this.http.get<MessageResponse>("api/servicioInformacion/", { params, headers })
        .pipe(finalize(() => {this.loading.next(false) }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("servicioOee:servicioInformacion:obtener")));
    }

    create(data: ServicioInformacion): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.post<MessageResponse>('api/servicioInformacion/create', data, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('servicioOee:servicioInformacion:crear')));
    }
  
    update(id: number, data: ServicioInformacion): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.put<MessageResponse>(`api/servicioInformacion/${id}`, data, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('servicioOee:servicioInformacion:borrar')));
    }
  
    updateStatus(id: number): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.put<MessageResponse>(`api/servicioInformacion/${id}/update-status`, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:updateStatus')));
    }
  
    delete(id: number): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.delete<MessageResponse>(`api/servicioInformacion/${id}/delete`, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:borrar')));
    }
}
