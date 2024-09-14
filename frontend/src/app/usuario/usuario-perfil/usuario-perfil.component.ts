import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MessageService } from 'primeng/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { Usuario } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';
import { StorageManagerService } from '../../shared/services/storage-manager.service';

@Component({
  selector: 'app-usuario-perfil',
  templateUrl: './usuario-perfil.component.html',
  styleUrls: ['./usuario-perfil.component.scss']
})
export class UsuarioPerfilComponent implements OnInit {

 
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  form: FormGroup;
  submitted = false;

  nombre: string;
  apellido: string;
  cargo: string;
  telefono: string;

  constructor(
    private formBuilder: FormBuilder,
    private service: UsuarioService,
    private storageManager: StorageManagerService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.initComponent();
  }

  ngOnChanges(): void {
    this.initComponent();
  }

  initComponent(): void {
    this.submitted = false;
    
    this.form = this.formBuilder.group({
      nombre: ['', [ Validators.required ] ],
      apellido: ['', [ Validators.required ] ],
      telefono: ['', [ Validators.required ] ],
      cargo: [''],
    });
    this.form.reset();

    this.nombre = this.storageManager.getCurrenSession().usuario.nombre;
    this.apellido = this.storageManager.getCurrenSession().usuario.apellido;
    this.cargo = this.storageManager.getCurrenSession().usuario.cargo;
    this.telefono = this.storageManager.getCurrenSession().usuario.telefono;

    this.formEdit();
  }

  formEdit() {
    // console.log(this.storageManager.getCurrenSession().usuario.telefono);
    if(this.nombre != null && this.apellido != null && this.telefono != null) {
        this.form.controls['nombre'].setValue(this.nombre);
        this.form.controls['apellido'].setValue(this.apellido);
        this.form.controls['cargo'].setValue(this.cargo);
        this.form.controls['telefono'].setValue(this.telefono);
    }
  }

  

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const usuario = new Usuario();
    usuario.nombre = formValue.nombre.toUpperCase();
    usuario.apellido = formValue.apellido.toUpperCase();
    usuario.telefono = formValue.telefono;
    usuario.cargo = formValue.cargo ? formValue.cargo.toUpperCase() : null;

    this.service.updatePerfil(usuario).subscribe(resp => {
        this.onResponse(resp);
        if ([200,201].indexOf(resp.code) !== -1) this.close();
    });
  }

  onResponse(res: MessageResponse) {
    if (res.code == 200) {
      this.messageService.add({severity: 'success', summary: 'Operación exitosa', detail: res.message, life: 3000});
    } else {
      if (res.code < 500) {
        this.messageService.add({severity: 'warn', summary: 'Atención', detail: res.message, life: 30000});
      } else {
        this.messageService.add({severity: 'error', summary: 'Atención', detail: res.message, life: 30000});
      }
    }
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

}
