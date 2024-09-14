import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { DatosOee } from '../models/datos.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { SelectItem } from 'src/app/shared/models/select.model';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatosService } from '../services/datos.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';

@Component({
  selector: 'app-datos-form',
  templateUrl: './datos-form.component.html',
  styleUrls: ['./datos-form.component.scss']
})
export class DatosFormComponent implements OnInit {

  @Input() row: DatosOee;
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

  constructor(
    private service: DatosService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService
  ) { }

  ngOnChanges(): void {
    this.initComponent();
  }

  ngOnInit(): void {
    this.initComponent();
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      // poseeCodigoFuente: [false],
      idOeeInformacion: [''],
      descripcionOeeInformacion: ['', Validators.required],
      /* descripcionOee: ['', Validators.required], */
      /* anho: ['', Validators.required],
      fecha: ['', Validators.required],
      linkPlan: ['', Validators.required],
      cantidadFuncionariosTic: ['', Validators.required],
      cantidadFuncionariosAdmin: ['', Validators.required],
      presupuestoTicAnual: ['', Validators.required], */

    });

    this.formEdit();
  }

  formEdit() {
    const datepipe: DatePipe = new DatePipe('en-PY');
    if (this.row != null) {
      let oee = this.row.oee;
      console.log(oee["descripcionOee"])
      this.form.controls['idOeeInformacion'].setValue(this.row?.idOeeInformacion);

      /* this.form.controls['descripcionOee'].setValue(this.row?.oee["descripcionOee"]); */

      this.form.controls['descripcionOeeInformacion'].setValue(this.row?.descripcionOeeInformacion);
      // this.form.controls['anho'].setValue(this.row?.anho);
      // this.form.controls['fecha'].setValue(datepipe.transform(this.row.fecha, 'dd-MM-YYYY'));
      // this.form.controls['linkPlan'].setValue(this.row?.linkPlan);
      // this.form.controls['cantidadFuncionariosTic'].setValue(this.row?.cantidadFuncionariosTic);
      // this.form.controls['cantidadFuncionariosAdmin'].setValue(this.row?.cantidadFuncionariosAdmin);
      // this.form.controls['presupuestoTicAnual'].setValue(this.separadorMiles(this.row?.presupuestoTicAnual));
    }
  }

  guardar(formValue) {

    /* console.log(formValue);
    debugger; */

    this.submitted = true;
    if (this.form.invalid) return;

    const datos_Oee                      = new DatosOee();
    datos_Oee.idOeeInformacion           = formValue.idOeeInformacion;
    datos_Oee.descripcionOeeInformacion  = formValue.descripcionOeeInformacion;

    /* datos_Oee.anho                       = formValue.anho;
    datos_Oee.fecha                      = formValue.fecha;
    datos_Oee.linkPlan                   = formValue.linkPlan;
    datos_Oee.cantidadFuncionariosTic    = formValue.cantidadFuncionariosTic;
    datos_Oee.cantidadFuncionariosAdmin  = formValue.cantidadFuncionariosAdmin;
    datos_Oee.presupuestoTicAnual        = this.limpiarSeparadoresMiles(formValue.presupuestoTicAnual); */

    const response = this.row == null
      ? this.service.create(datos_Oee)
      : this.service.update(datos_Oee.idOeeInformacion, datos_Oee);

    response.subscribe(resp => {
      this.onResponse.emit(resp);
      if ([200, 201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    /* this.form.reset(); */
    this.setVisible.emit(false);
  }







}
