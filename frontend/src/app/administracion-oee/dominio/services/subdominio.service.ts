import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorHandler } from '../../../shared/handlers/http.error.handler';
import { finalize, catchError } from 'rxjs/operators';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { SubDominio } from '../models/subdominio.model';
import { headers } from '../../../shared/helpers/util';

@Injectable({
  providedIn: 'root'
})
export class SubdominioService {

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

  getAll(idDominio: number, filter: string, pageSize: number, start: number, sortField: string, sortAsc: boolean, advancedFilter: any): Observable<MessageResponse> {
    this.loading.next(true);

    let params = new HttpParams();
    if (filter) params = params.set('filter', filter);
    if (sortField) params = params.set('sortField', sortField);

    params = params
        .set('idDominio', `${idDominio}`)
        .set('page', `${Math.ceil(start / pageSize)}`)
        .set('pageSize', `${pageSize}`)
        .set('sortAsc', `${sortAsc}`);

    Object.keys(advancedFilter).map((key) => {
        const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
        params = params.set(key, searchValue);
    });

    return this.http.get<MessageResponse>(`api/subdominio/`, { params, headers })
      .pipe(finalize(() => {this.loading.next(false) }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("subdominio:listar")));
  }

  create(data: SubDominio): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.post<MessageResponse>('api/subdominio/create', data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:crear')));
  }

  update(id: number, data: SubDominio): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/subdominio/${id}`, data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:editar')));
  }

  updateStatus(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/subdominio/${id}/update-status`, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:updateStatus')));
  }

  delete(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.delete<MessageResponse>(`api/subdominio/${id}/delete`, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('subdominio:borrar')));
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
    return this.http.get(`api/subdominio/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers})
  }
  
}
