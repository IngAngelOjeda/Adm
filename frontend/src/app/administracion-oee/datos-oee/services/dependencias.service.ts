import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { Dependencias } from '../models/dependencias.model';

@Injectable({
  providedIn: 'root'
})
export class DependenciasService {

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

    return this.http.get<MessageResponse>("api/dependencia/getAllDependencia/", { params, headers })
    /* return this.http.get<MessageResponse>("api/dependencia/", { params, headers }) */
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependencia:listar")));
  }

  getPermisos(): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>("api/permiso/list", { headers })
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("permiso:get", null)));
  }

  create(data: Dependencias): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.post<MessageResponse>('api/dependencia/create', data, { headers })
    /* return this.http.post<MessageResponse>('api/dependencia/create', data, { headers }) */
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('dependencia:create')));
  }

  update(id: number, data: Dependencias): Observable<MessageResponse> { 
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/dependencia/${id}`, data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('dependencia:update')));
  }
  
  updateStatus(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/dependencia/${id}/update-status`, { headers })
    .pipe(finalize(() => { this.loading.next(false) }))
    .pipe(catchError(this.handler.handlePostError<MessageResponse>('dependencia:updateStatus')));
  }
  
  getDependenciasPadre(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>(`api/dependencia/list?idOee=${id}`, { headers })
    /* return this.http.get<MessageResponse>(`api/dependencia/list/${id}`, { headers }) */
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependencia:getDependenciasPadre", null)));
  }

  // SOLICITUD PARA DATOS DEL ORGANIGRAMA
  getShowDependencia(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>(`api/dependencia/showDependencia?idOee=${id}`, { headers })
    /* return this.http.get<MessageResponse>(`api/dependencia/list/${id}`, { headers }) */
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependencia:getShowDependencia", null)));
  }
 
  // CONSULTA PDF
  /* getInforme(formato: string, id: number): Observable<Blob> {
    this.loading.next(true);
    return this.http.get(`api/dependencia/downloadReport/${formato}/${id}`, {  
      responseType: 'blob',
    });
  } */

  /* INFORME PARAMETRADO */
  getInforme(advancedFilter: any, formato: string, id: number, permiso: boolean): Observable<Blob> {
    this.loading.next(true);
    let params = new HttpParams();
    params = params
        /* .set('page', `${Math.ceil(start / pageSize)}`)
        .set('pageSize', `${pageSize}`)
        .set('sortAsc', `${sortAsc}`) */
        .set('permiso', `${permiso}`)
    Object.keys(advancedFilter).map((key) => {
        const searchValue = advancedFilter[key] != null && advancedFilter[key] != 'null' ? advancedFilter[key] : '';
        params = params.set(key, searchValue);
    });
    return this.http.get(`api/dependencia/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers})
  }

}
