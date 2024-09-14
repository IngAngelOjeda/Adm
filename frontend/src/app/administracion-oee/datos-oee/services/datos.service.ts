import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { DatosOee } from '../models/datos.model';

@Injectable({
  providedIn: 'root'
})
export class DatosService {

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

    return this.http.get<MessageResponse>("api/datosOee/", { params, headers })
      .pipe(finalize(() => {this.loading.next(false) }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependecia:listar")));
  }

  getPermisos(): Observable<MessageResponse>  {
    this.loading.next(true);
    return this.http.get<MessageResponse>("api/datosOee/list", { headers })
    .pipe(finalize(() => { this.loading.next(false); }))
    .pipe(catchError(this.handler.handleError<MessageResponse>("permiso:get",null)));
  }

  create(data: DatosOee): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.post<MessageResponse>('api/datosOee/create', data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('dependecia:create')));
  }

  update(id: number, data: DatosOee): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/datosOee/${id}`, data, { headers })
    /* return this.http.put<MessageResponse>(`api/datosOee/3460`, data, { headers }) */
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('dependecia:update'))); 
  }

  // CONSULTA PDF
  /* getInforme(formato: string, id: number): Observable<Blob> {
    return this.http.get(`api/datosOee/downloadReport/${formato}/${id}`, {  
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
    return this.http.get(`api/datosOee/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers})
  }

}
