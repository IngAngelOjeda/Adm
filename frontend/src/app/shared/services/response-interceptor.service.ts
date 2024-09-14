import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpRequest, HttpEvent, HttpHandler, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { StorageManagerService } from './storage-manager.service';
import { LoginService } from '../../login/services/login.service';

const TOKEN_HEADER_KEY = 'Authorization';
const UNAUTHORIZED = 401;

@Injectable()
export class ResponseInterceptorService {

  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private storageManager: StorageManagerService, private authService: LoginService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
        catchError(error => {
            const refreshToken = this.storageManager.getRefreshToken();
            const accessToken = this.storageManager.getAccessToken();
            console.log(error);
            console.log(req);
            if (error instanceof HttpErrorResponse && !req.url.includes('api/auth/login') && !req.url.includes('api/auth/refreshToken') && error.status === UNAUTHORIZED) {
                return this.handleUnauthorizedError(req, next, accessToken, refreshToken);
            }
            return throwError(error);
        })
    );
  }

  private handleUnauthorizedError(request: HttpRequest<any>, next: HttpHandler, accessToken: string, refreshToken: string) {
    this.refreshTokenSubject.next(null);
    if(refreshToken) {
      return this.authService.refreshToken(accessToken, refreshToken).pipe(
        switchMap((response: any) => {
          this.storageManager.saveAccessToken(response.accessToken);
          this.storageManager.saveRefreshToken(response.refreshToken);
          this.refreshTokenSubject.next(response.accessToken);
          return next.handle(this.addTokenHeader(request, response.accessToken));
        }), catchError((err) => {
          this.storageManager.deleteStorage();
          this.router.navigate(['/']);
          return throwError(err);
        })
      );
    }
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({ headers: request.headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token) });
  }

}
