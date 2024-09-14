import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Dependencias } from '../models/dependencias.model';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { SelectItem } from 'src/app/shared/models/select.model';
import { DependenciasService } from '../services/dependencias.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-dependencias-form',
  templateUrl: './dependencias-form.component.html',
  styleUrls: ['./dependencias-form.component.scss']
})
export class DependenciasFormComponent implements OnInit {

  @Input() row: Dependencias;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public form: FormGroup;
  public submitted = false;
  public datePipe: DatePipe;

  dependencia_padre: SelectItem[];
  selectedDrop: SelectItem;
  yearStart: number = new Date().getFullYear() - 1;
  yearFinal: number = new Date().getFullYear() + 5;

  /* ID OEE */
  public id: any;
  public idOee: any;
  public oee: any;

  constructor(
    private service: DependenciasService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService,
    private dependenciasService: DependenciasService,

    private storageManager: StorageManagerService
  ) { }

  ngOnInit(): void {
    this.initComponent();
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.oee = this.storageManager.getCurrenSession().usuario.oee;
  }

  ngOnChanges(): void {
    this.initComponent();
    this.loadDependencias();
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  loadDependencias() {
    this.idOee = this.storageManager.getCurrenSession().usuario.oee.id;
    this.dependenciasService.getDependenciasPadre(this.idOee).subscribe((response) => {
        if(response) {
          let data = response.data;
          
          let comboDependenciaPadre = data.map(function(item) {
            return {
              "id": item.idDependencia,
              "nombre": item.descripcionDependencia
            };
          });

          this.dependencia_padre = comboDependenciaPadre;
        }
    });
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      idDependencia: [''],
      codigo: ['', Validators.required],
      descripcionDependencia: ['', Validators.required],
      idDependenciaPadre: [''],
    });

    this.formEdit();
  }

  formEdit() {
    const datepipe: DatePipe = new DatePipe('en-PY');
    if (this.row != null) {
      this.form.controls['idDependencia'].setValue(this.row?.idDependencia);
      this.form.controls['codigo'].setValue(this.row?.codigo);
      this.form.controls['descripcionDependencia'].setValue(this.row?.descripcionDependencia);
      if(this.row.dependenciaPadre){
        this.form.controls['idDependenciaPadre'].setValue({ id: this.row?.dependenciaPadre['idDependencia'], nombre: this.row?.dependenciaPadre['descripcionDependencia'] });
      }

    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const dependencias                      = new Dependencias();
    dependencias.idDependencia              = formValue.idDependencia;
    dependencias.codigo                     = formValue.codigo;
    dependencias.descripcionDependencia     = formValue.descripcionDependencia;
    dependencias.idDependenciaPadre         = formValue.idDependenciaPadre ? formValue.idDependenciaPadre['id'] : null;
    dependencias.oee                        = this.oee;

    const response = this.row == null
      ? this.service.create(dependencias)
      : this.service.update(dependencias.idDependencia, dependencias);

    response.subscribe(resp => {
      this.onResponse.emit(resp);
      if ([200, 201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

}
