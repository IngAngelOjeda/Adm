import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../shared/models/message-response.model';
import { headers } from '../../shared/helpers/util';
import { Oee } from '../models/institucion.model';

@Injectable({
  providedIn: 'root'
})
export class InstitucionService {

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

    getAll(filter: string, pageSize: number, start: number, sortField: string, sortAsc: boolean, advancedFilter: any): Observable<MessageResponse> {
      this.loading.next(true);

      let params = new HttpParams();
      if (filter) params = params.set('filter', filter);
      if (sortField) params = params.set('sortField', sortField);

      params = params
          .set('page', `${ ( pageSize > 0 ? (Math.ceil(start / pageSize)) : 0 ) }`)
          .set('pageSize', `${pageSize}`)
          .set('sortAsc', `${sortAsc}`);

      Object.keys(advancedFilter).map((key) => {
          const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
          params = params.set(key, searchValue);
      });

      return this.http.get<MessageResponse>("api/oee/", { params, headers })
        .pipe(finalize(() => {this.loading.next(false) }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("oee:list")));
    }

    getInstituciones(): Observable<MessageResponse>  {
        this.loading.next(true);
        return this.http.get<MessageResponse>("api/oee/list", { headers })
        .pipe(finalize(() => { this.loading.next(false); }))
        .pipe(catchError(this.handler.handleError<MessageResponse>("obtenerInstituciones",null)));
    }

    getInstitucionesByServicioUsuario(): Observable<MessageResponse>  {
      this.loading.next(true);
      return this.http.get<MessageResponse>("api/oee/listByServicioUsuario", { headers })
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("listByServicioUsuario",null)));
  }
    /**
     * @param tipo	{ 0 = oee; 1=institucion_dependiente}
     */
    create(data: Oee): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.post<MessageResponse>(`api/oee/`, data, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('oee:create')));
    }

    update(id: number, data: Oee): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.put<MessageResponse>(`api/oee/${id}`, data, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('oee:update')));
    }

    delete(id: number): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.delete<MessageResponse>(`api/oee/${id}`, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('oee:delete')));
    }

    hide(id: number, data: Oee): Observable<MessageResponse> {
      this.loading.next(true);
      return this.http.put<MessageResponse>(`api/oee/hide/${id}`, data, { headers })
        .pipe(finalize(() => { this.loading.next(false) }))
        .pipe(catchError(this.handler.handlePostError<MessageResponse>('oee:hide')));
    }

    getInforme(advancedFilter: any, formato: string, id: number): Observable<Blob> {
      this.loading.next(true);
      let params = new HttpParams();
      params = params
          .set('id', `${id}`)
      Object.keys(advancedFilter).map((key) => {
          const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
          params = params.set(key, searchValue);
      });
      return this.http.get(`api/oee/downloadReport/${formato}`, {responseType: 'blob', params, headers})
      /* return this.http.get(`api/oee/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers}) */
    }

}
