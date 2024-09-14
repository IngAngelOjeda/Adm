import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { HttpErrorHandler } from 'src/app/shared/handlers/http.error.handler';
import { headers } from 'src/app/shared/helpers/util';
import { MessageResponse } from 'src/app/shared/models/message-response.model';

@Injectable({
    providedIn: 'root'
})
export class AuditoriaService {

    private handler: HttpErrorHandler = new HttpErrorHandler();
    private loading = new BehaviorSubject<boolean>(false);

    constructor(private http: HttpClient) { }

    connect(): Observable<boolean> {
        return this.loading.asObservable();
    }

    disconnet(): void {
        this.loading.complete();
    }

    getAll(filter: string, pageSize: number, start: number, sortField: string, sortAsc: boolean, advancedFilter: any, advancedFilterT: any): Observable<MessageResponse> {
        this.loading.next(true);

        let params = new HttpParams();
        if (filter) params = params.set('filter', filter);
        if (sortField) params = params.set('sortField', sortField);

        params = params
            .set('page', `${Math.ceil(start / pageSize)}`)
            .set('pageSize', `${pageSize}`)
            .set('sortAsc', `${sortAsc}`);

        const applyAdvancedFilter = (filterObject: any) => {
            Object.keys(filterObject).map((key) => {
                const searchValue = filterObject[key] != null && filterObject[key] != 'null' ? filterObject[key] : '';
                params = params.set(key, searchValue);
            });
        };

        applyAdvancedFilter(advancedFilter);
        applyAdvancedFilter(advancedFilterT);

        return this.http.get<MessageResponse>("api/auditoria/", { params, headers })
            .pipe(finalize(() => { this.loading.next(false) }))
            .pipe(catchError(this.handler.handleError<MessageResponse>("auditoria:list")));
    }
}
