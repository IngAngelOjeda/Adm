import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { SelectItem } from '../../../shared/models/select.model';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatePipe } from '@angular/common';
import { Servicio } from '../models/servicio.model';
import { ServicioService } from '../services/servicios.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { EtiquetaService } from '../../../administracion-oee/etiquetas/services/etiqueta.service';
import { ClasificadorService } from '../../../administracion-oee/clasificador/services/clasificador.service';
import { RequisitoService } from '../../../administracion-oee/requisito/services/requisito.service';
import { ServicioInformacionService } from '../../../administracion-oee/servicios/services/servicio-informacion.service';


@Component({
  selector: 'app-servicio-form',
  templateUrl: './servicios-form.component.html',
  styleUrls: ['./servicios-form.component.scss']
})


export class ServicioFormComponent implements OnInit, OnChanges {

  @Input() row: Servicio;
  @Input() instituciones: SelectItem[];
  @Input() requisitos: SelectItem[];
  @Input() clasificadores: SelectItem[];
  @Input() etiquetas: SelectItem[];
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public form: FormGroup;
  public submitted = false;
  public loading: boolean = true;
  public datePipe: DatePipe;
  public etiquetasServicio: SelectItem[];
  public requisitosServicio: SelectItem[];
  public clasificadoresServicio: SelectItem[];
  


  constructor(
    private service: ServicioService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService,
    private EtiquetaService: EtiquetaService,
    private ClasificadorService: ClasificadorService,
    private RequisitoService: RequisitoService,
    private ServicioInformacionService: ServicioInformacionService,

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
      //informacion basica
      idServicio: [''],
      nombreServicio: ['', [Validators.required]],
      descripcionServicio: ['', [Validators.required]],
      destacado: [false],
      checkOnline: [false],
      oee: ['', [Validators.required]],
      urlOnline: [''],
      //etiquetas
      etiquetas: [[], [Validators.required]],
      //categorias
      clasificadores: [[], [Validators.required]],
      //requisitos
      requisitos: [[], [Validators.required]]

    });

    this.formEdit();

  }

  formEdit() {
    if (this.row != null) {

      //informacion basica
      this.form.controls['idServicio'].setValue(this.row?.idServicio);
      this.form.controls['nombreServicio'].setValue(this.row?.nombreServicio);
      this.form.controls['descripcionServicio'].setValue(this.row?.descripcionServicio);
      this.form.controls['oee'].setValue({ id: this.row?.oee.id, nombre: this.row?.oee.descripcionOee });

      this.loadDataById(this.row?.idServicio);

      this.form.controls['destacado'].setValue(this.row?.destacado);
      if (this.row?.urlOnline) {
        this.form.controls['checkOnline'].setValue(true);
      } else {
        this.form.controls['checkOnline'].setValue(false);
      }
      this.form.controls['urlOnline'].setValue(this.row?.urlOnline);

    }
  }

  loadDataById(idServicio: number): void {
    this.loadDataService(idServicio, this.EtiquetaService, 'etiquetas', 'getEtiquetasbyId', data => this.getSelectData(data, 'idEtiqueta', 'nombreEtiqueta'));
    this.loadDataService(idServicio, this.RequisitoService, 'requisitos', 'getRequisitosbyId', data => this.getSelectData(data, 'idRequisito', 'nombreRequisito'));
    this.loadDataService(idServicio, this.ClasificadorService, 'clasificadores', 'getClasificadoresbyId', data => this.getSelectData(data, 'idClasificador', 'nombreClasificador'));
  }
  loadDataService(idServicio: number, service: any, controlName: string, functionName: string, transformFunction: Function): void {
      service[functionName](idServicio).subscribe((response) => {
        if (response && response.data) {
          const transformedData = transformFunction(response.data);
          this.form.controls[controlName].setValue(transformedData);
        } else {
          this.form.controls[controlName].reset();
        }
      });
  }
  getSelectData(data, idKey: string, nameKey: string): any[] {
    if (!data || data.length === 0) return [];
    return data.map(row => ({ [idKey]: row[idKey] ?? row?.[idKey], [nameKey]: row?.[nameKey] }));
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;
    const servicio = new Servicio();

    //informacion basica
    servicio.idServicio = formValue.idServicio;
    servicio.nombreServicio = formValue.nombreServicio;
    servicio.descripcionServicio = formValue.descripcionServicio;
    servicio.destacado = formValue.destacado;
    servicio.oee = formValue.oee;
    servicio.urlOnline = formValue.urlOnline;
    //etiquetas
    servicio.etiqueta = formValue.etiquetas;
    //categorias
    servicio.clasificador = formValue.clasificadores;
    //requisitos
    servicio.requisito = formValue.requisitos;
    console.log(servicio);
    const response = this.row == null
      ? this.service.create(servicio)
      : this.service.update(servicio.idServicio, servicio);

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
