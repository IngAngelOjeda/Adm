import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { StorageManagerService } from '../shared/services/storage-manager.service';
import { ModificarClaveService } from './services/modificar-clave.service';
import { Message, MessageService } from 'primeng/api';


@Component({
  selector: 'app-modificar-clave',
  templateUrl: './modificar-clave.component.html',
  styleUrls: ['./modificar-clave.component.scss']
})
export class ModificarClaveComponent implements OnInit {

  public modificarClaveForm: FormGroup;

  public loading: boolean = false;

  token: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private modificarClaveService: ModificarClaveService,
    private storageManager: StorageManagerService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.modificarClaveForm = new FormGroup({
      password: new FormControl('', Validators.required),
      password2: new FormControl('', Validators.required)
    });
    if (this.storageManager.getCurrenSession() != null) {
      this.router.navigate(['/home']);
    }
    this.route.paramMap.subscribe(params => {
      this.token = params.get('token');
    });
    this.consultarToken();
  }

  consultarToken() {
    this.modificarClaveService.consultarToken(this.token).subscribe((response) => {
        // console.log(response);
        if (response && response.code != 200) {
          this.messageService.add({ severity: 'error', summary: 'Atención!', detail: response.message, life: 3000 });
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        }
    });
  }

  modificarClave() {
    this.loading = true;
    this.modificarClaveService.doModificarClave(this.token,this.modificarClaveForm.value).subscribe(
      response => {
        if (response && response.code == 200) {
          this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000 });
          this.loading = false;
          this.modificarClaveForm.reset();
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
