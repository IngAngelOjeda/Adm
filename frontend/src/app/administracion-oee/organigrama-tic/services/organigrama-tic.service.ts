import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { headers } from '../../../shared/helpers/util';
import { OrganigramaTic } from '../models/organigrama-tic.model';

@Injectable({
  providedIn: 'root'
})
export class OrganigramaTicService {

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

    return this.http.get<MessageResponse>("api/organigrama/", { params, headers })
      .pipe(finalize(() => {this.loading.next(false) }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("organigrama:listar")));
  }

  getPermisos(): Observable<MessageResponse>  {
    this.loading.next(true);
    return this.http.get<MessageResponse>("api/organigrama/list", { headers })
    .pipe(finalize(() => { this.loading.next(false); }))
    .pipe(catchError(this.handler.handleError<MessageResponse>("permiso:get",null)));
  }

  create(data: OrganigramaTic): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.post<MessageResponse>('api/organigrama/create', data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('organigrama:create')));
  }

  update(id: number, data: OrganigramaTic): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/organigrama/${id}`, data, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('organigrama:update'))); 
  }

  updateStatus(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.put<MessageResponse>(`api/organigrama/${id}/update-status`, { headers })
      .pipe(finalize(() => { this.loading.next(false) }))
      .pipe(catchError(this.handler.handlePostError<MessageResponse>('organigrama:updateStatus')));
  } 

  getDependencias(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>(`api/organigrama/list?idOee=${id}`, { headers })
    /* return this.http.get<MessageResponse>(`api/dependencia/list?idOee=104`, { headers }) */
    /* return this.http.get<MessageResponse>(`api/dependencia/list/${id}`, { headers }) */
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("organigrama:getDependenciasPadre", null)));
  }

  /* CONSULTA SOLO PARA LAS DEPENDENCIAS */
  ShowDependencia(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>(`api/dependencia/showDependencia?idOee=${id}`, { headers })
    /* return this.http.get<MessageResponse>(`api/dependencia/list/${id}`, { headers }) */
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependencia:getShowDependencia", null)));
  }

  // SOLICITUD PARA DATOS DEL ORGANIGRAMA
  ShowOrganigramaDependencia(id: number): Observable<MessageResponse> {
    this.loading.next(true);
    return this.http.get<MessageResponse>(`api/organigrama/showOrganigramaFuncionario?idOee=${id}`, { headers })
    /* return this.http.get<MessageResponse>(`api/dependencia/list/${id}`, { headers }) */
      .pipe(finalize(() => { this.loading.next(false); }))
      .pipe(catchError(this.handler.handleError<MessageResponse>("dependencia:getShowDependencia", null)));
  }

  /* INFORME SIN PARAMETRADO */
  /* getInforme(formato: string, id: number): Observable<Blob> {
    this.loading.next(true);
    return this.http.get(`api/organigrama/downloadReport/${formato}/${id}`, { 
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
    return this.http.get(`api/organigrama/downloadReport/${formato}/${id}`, {responseType: 'blob', params, headers})
  }
}
