import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatePipe } from '@angular/common';
import { PlanesTic } from '../models/planes-tic.model';
import { PlanesTicService } from '../services/planes-tic.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-planes-tic-form',
  templateUrl: './planes-tic-form.component.html',
  styleUrls: ['./planes-tic-form.component.scss']
})
export class PlanesTicFormComponent implements OnInit, OnChanges {

  @Input() row: PlanesTic;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public form: FormGroup;
  public submitted = false;
  public datePipe: DatePipe;

  tipoUso: SelectItem[];
  selectedDrop: SelectItem;
  yearStart: number = new Date().getFullYear() - 1;
  yearFinal: number = new Date().getFullYear() + 5;
  
  public id: any;
  public oee: any;
  /* public dependencia: any; */

  constructor(
    private service: PlanesTicService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService,

    private storageManager: StorageManagerService
  ) { }

  ngOnChanges(): void {
    this.initComponent();
    this.id           = this.storageManager.getCurrenSession().usuario.oee.id;
    this.oee          = this.storageManager.getCurrenSession().usuario.oee;
    /* this.dependencia  = this.storageManager.getCurrenSession().usuario.oee.id; */
  }

  ngOnInit(): void {
    this.initComponent();
    this.tipoUso = [
      { label: 'Ciudadanía ', value: { tipo: 'C' } },
      { label: 'Funcionarios', value: { tipo: 'F' } },
      { label: 'Ciudadanía - Funcionarios', value: { tipo: 'CF' } },
      { label: 'Funcionarios TIC', value: { tipo: 'FTIC' } }
    ];
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      // poseeCodigoFuente: [false],
      idPlan: [''],
      anho: ['', Validators.required],
      fecha: ['', Validators.required],
      linkPlan: ['', Validators.required],
      cantidadFuncionariosTic: ['', Validators.required],
      cantidadFuncionariosAdmin: ['', Validators.required],
      presupuestoTicAnual: ['', Validators.required],

    });

    this.formEdit();
  }


  formEdit() {
    const datepipe: DatePipe = new DatePipe('en-PY');
    if (this.row != null) {
      this.form.controls['idPlan'].setValue(this.row?.idPlan);
      this.form.controls['anho'].setValue(this.row?.anho);
      this.form.controls['fecha'].setValue(datepipe.transform(this.row.fecha, 'dd-MM-YYYY'));
      this.form.controls['linkPlan'].setValue(this.row?.linkPlan);
      this.form.controls['cantidadFuncionariosTic'].setValue(this.row?.cantidadFuncionariosTic);
      this.form.controls['cantidadFuncionariosAdmin'].setValue(this.row?.cantidadFuncionariosAdmin);
      this.form.controls['presupuestoTicAnual'].setValue(this.separadorMiles(this.row?.presupuestoTicAnual));
    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const planes                      = new PlanesTic();
    planes.idPlan                     = formValue.idPlan;
    planes.anho                       = formValue.anho;
    planes.fecha                      = formValue.fecha;
    planes.linkPlan                   = formValue.linkPlan;
    planes.cantidadFuncionariosTic    = formValue.cantidadFuncionariosTic;
    planes.cantidadFuncionariosAdmin  = formValue.cantidadFuncionariosAdmin;
    planes.presupuestoTicAnual        = this.limpiarSeparadoresMiles(formValue.presupuestoTicAnual);

    const response = this.row == null
      ? this.service.create(planes)
      : this.service.update(planes.idPlan, planes);

    response.subscribe(resp => {
      this.onResponse.emit(resp);
      if ([200, 201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

  onlyNumericInput(input: HTMLInputElement): void {
    const numericValue = input.value.replace(/[^\d]/g, '');
    input.value = numericValue;
  }

  // yearInput(input: HTMLInputElement) {
  //   if (input.value.length <= 4) {
  //     return false;
  //     /* const yearValue = input.value.replace(/[^\d]/g, '');
  //     input.value = yearValue; */
  //   }
  // }

  separadorMilesOnInput(input: HTMLInputElement): void {
    const numericValue = input.value.replace(/[^\d]/g, '');
    const formattedValue = this.separadorMiles(numericValue);
    input.value = formattedValue;
  }

  separadorMiles(value: any): string {
    const parts = value.toString().split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    return parts.join('.');
  }

  limpiarSeparadoresMiles(value: string | number): string {
    if (typeof value === 'number') {
      return value.toString().replace(/\./g, '');
    } else {
      return value.replace(/\./g, '');
    }
  }

}
