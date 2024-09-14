import { Injectable, OnInit, OnDestroy } from '@angular/core';
import { CookiesStorageService, CookieStorage, SessionStorageService } from 'ngx-store';
import { UserSession } from '../models/usersession.model';

@Injectable({
  providedIn: 'root'
})
export class StorageManagerService implements OnInit, OnDestroy {

  constructor(
    private sessionStorageService: SessionStorageService,
    private cookieService: CookiesStorageService
  ) { }

  ngOnInit() { }

  ngOnDestroy() { }

  saveAccessToken(accessToken: string) {
    this.cookieService.set('access_token', accessToken);
  }

  saveRefreshToken(refreshToken: string) {
    this.cookieService.set('refresh_token', refreshToken);
  }

  public getAccessToken(): string {
    return this.cookieService.get('access_token');
  }

  public getRefreshToken(): string {
    return this.cookieService.get('refresh_token');
  }

  saveSession(data: UserSession) {
    this.sessionStorageService.set('current_user', data);
  }

  public getCurrenSession(): UserSession {
    return this.sessionStorageService.get('current_user');
  }

  deleteStorage() {
    this.sessionStorageService.clear();
    this.cookieService.clear();
  }
}
