import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { StorageManagerService } from '../shared/services/storage-manager.service';
import { RecuperarClaveService } from './services/recuperar-clave.service';
import { Message, MessageService } from 'primeng/api';

@Component({
  selector: 'app-recuperar-clave',
  templateUrl: './recuperar-clave.component.html',
  styleUrls: ['./recuperar-clave.component.scss']
})
export class RecuperarClaveComponent implements OnInit {

  public recuperarClaveForm: FormGroup;

  public loading: boolean = false;

  constructor(
    private router: Router,
    private recuperarClaveService: RecuperarClaveService,
    private storageManager: StorageManagerService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.recuperarClaveForm = new FormGroup({
      username: new FormControl('', Validators.required),
    });
    if (this.storageManager.getCurrenSession() != null) {
      this.router.navigate(['/home']);
    }
  }

  recuperarClave() {
    this.loading = true;
    this.recuperarClaveService.doRecuperarClave(this.recuperarClaveForm.value).subscribe(
      response => {
        if (response && response.code == 200) {
          this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000 });
          this.loading = false;
          this.recuperarClaveForm.reset();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          this.messageService.add({ severity: 'error', summary: 'Atención!', detail: response.message, life: 3000 });
          this.loading = false;
        }
      });
  }


}
