import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { Sistemas } from '../models/sistemas.model';
import { headers } from '../../../shared/helpers/util';

@Injectable({
  providedIn: 'root'
})
export class SistemasService {

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

  getAll(filter: string, pageSize: number, start: number, sortField: string, sortAsc: boolean, advancedFilter: any, id: number, permiso: boolean): Observable<MessageResponse> {
    this.loading.next(true);

    let params = new HttpParams();
    if (filter) params = params.set('filter', filter);
    if (sortField) params = params.set('sortField', sortField);

    params = params
        .set('page', `${Math.ceil(start / pageSize)}`)
        .set('pageSize', `${pageSize}`)
        .set('sortAsc', `${sortAsc}`)
        .set('id', `${id}`)
        .set('permiso', `${permiso}`);

    Object.keys(advancedFilter).map((key) => {
        const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
        params = params.set(key, searchValue);
    });

    return this.http.get<MessageResponse>("api/sistema/", { params, headers })
      .pipe(finalize(() => {this.loading.next(false) }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("sistemas:listar")));
  }

  create(data: Sistemas): Observable<MessageResponse> {
    this.loading.next(true); 
    return this.http.post<MessageResponse>('api/sistema/', data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('sistemas:crear')));
  }

  update(id: number, data: Sistemas): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/sistema/${id}`, data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('sistemas:editar')));
  }

  delete(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.delete<MessageResponse>(`api/sistema/${id}/delete`, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('sistemas:borrar')));
  }

  updateStatus(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/sistema/${id}/update-status`, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('sistema:updateStatus')));
  }

  /* INFORME PARAMETRADO */
  getInforme(advancedFilter: any, formato: string, id: number, permiso: boolean): Observable<Blob> {
    this.loading.next(true);
    let params = new HttpParams();
    params = params
        .set('permiso', `${permiso}`)
    Object.keys(advancedFilter).map((key) => {
        const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
        params = params.set(key, searchValue);
    });
    return this.http.get(`api/sistema/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers})
  }

}
