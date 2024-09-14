import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { StorageManagerService } from '../shared/services/storage-manager.service';
import { LoginService } from './services/login.service';
import { Message, MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public signinForm: FormGroup;

  public loading:boolean = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private storageManager: StorageManagerService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.signinForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });
    if(this.storageManager.getCurrenSession() != null) {
      this.router.navigate(['/home']);
    }
  }

  signin() {
    this.loading = true;
    this.loginService.doLogin(this.signinForm.value).subscribe(response => {
      if(response && response.accessToken){
        this.storageManager.saveAccessToken(response.accessToken);
        this.storageManager.saveRefreshToken(response.refreshToken);
        this.storageManager.saveSession({ usuario: response.usuario, permisos: response.permisos });
        this.router.navigate(['/home']);
      } else {
        this.messageService.add({severity: 'error', summary: 'Atención!', detail: response.message, life: 3000});
        this.loading = false;
      }
    });
  }

}
