import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { Usuario } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';
import { DatePipe } from '@angular/common';
import { SelectItem } from '../../shared/models/select.model';

@Component({
  selector: 'app-usuario-form',
  templateUrl: './usuario-form.component.html',
  styleUrls: ['./usuario-form.component.scss']
})
export class UsuarioFormComponent implements OnInit, OnChanges {

  @Input() row: Usuario;
  @Input() roles: SelectItem[];
  @Input() instituciones: SelectItem[];
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  form: FormGroup;
  submitted = false;
  datePipe: DatePipe;
  readonly: boolean = false;
  yearStart: number = new Date().getFullYear() -1;
  yearFinal: number = new Date().getFullYear() +5;
  
  constructor(
    private service: UsuarioService,
    private formBuilder: FormBuilder
  ) { }

  ngOnChanges(): void {
    this.initComponent();
  }

  ngOnInit(): void {
    this.initComponent();
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      idUsuario: [''],
      username: ['', [Validators.required, Validators.pattern('[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}')]],
      cedula: ['', [ Validators.required ] ],
      nombre: ['', [ Validators.required ] ],
      apellido: ['', [ Validators.required ] ],
      fechaExpiracion: ['', [ Validators.required ] ],
      roles: ['', [ Validators.required ] ],
      oee: ['', [ Validators.required ]],
      cargo: [''],
      telefono: ['', [ Validators.required ]],
      // correo: ['', [ Validators.required ]]
      justificacionAlta: ['', [ Validators.required ]]
    });

    this.formEdit();
  }

  formEdit() {
    this.readonly = false;
    const datepipe: DatePipe = new DatePipe('en-PY');
    this.form.reset();
    if(this.row != null) {  
        this.readonly = true;      
        this.form.controls['idUsuario'].setValue(this.row?.idUsuario);
        this.form.controls['username'].setValue(this.row?.username);
        this.form.controls['cedula'].setValue(this.row?.cedula);
        this.form.controls['nombre'].setValue(this.row?.nombre);
        this.form.controls['apellido'].setValue(this.row?.apellido);
        //this.form.controls['fechaExpiracion'].setValue(formatDate(this.row.fechaExpiracion));
        this.form.controls['fechaExpiracion'].setValue(datepipe.transform(this.row.fechaExpiracion, 'dd-MM-YYYY'));
        this.form.controls['oee'].setValue({ id: this.row?.oee.id, nombre: this.row?.oee.descripcionOee });
        this.form.controls['roles'].setValue(this.getSelectRolData(this.row?.roles));
        this.form.controls['cargo'].setValue(this.row?.cargo);
        this.form.controls['telefono'].setValue(this.row?.telefono);
        // this.form.controls['correo'].setValue(this.row?.correo);
        this.form.controls['justificacionAlta'].setValue(this.row?.justificacionAlta);

    }
  }

 getSelectRolData(data): any[] {
    if(data == null || data.length <= 0) return [];
    let itemList = [];
    data.forEach((row: any) => { itemList = [...itemList, { id: row.idRol ?? row?.id, nombre: row.nombre }] });
    return itemList;
 }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const usuario = new Usuario();
    usuario.idUsuario = formValue.idUsuario;
    usuario.username = formValue.username;
    usuario.password = formValue.password;
    usuario.password2 = formValue.password2;
    usuario.nombre = formValue.nombre.toUpperCase();
    usuario.apellido = formValue.apellido.toUpperCase();
    usuario.fechaExpiracion = formValue.fechaExpiracion;
    usuario.roles = formValue.roles;
    usuario.oee = formValue.oee;
    usuario.cargo = formValue.cargo ? formValue.cargo.toUpperCase() : null;
    usuario.direccion = formValue.direccion;
    usuario.telefono = formValue.telefono;
    // usuario.correo = formValue.correo;
    usuario.cedula = formValue.cedula;
    usuario.justificacionAlta = formValue.justificacionAlta;

    const response = this.row == null
        ? this.service.create(usuario)
        : this.service.update(usuario.idUsuario, usuario);

    response.subscribe(resp => {
        this.onResponse.emit(resp);
        if ([200,201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

}
