import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { SelectItem, SelectItemP } from '../../../shared/models/select.model';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatePipe } from '@angular/common';
import { Sistemas } from '../models/sistemas.model';
import { SistemasService } from '../services/sistemas.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { InstitucionService } from '../../../institucion/services/institucion.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-sistemas-form',
  templateUrl: './sistemas-form.component.html',
  styleUrls: ['./sistemas-form.component.scss']
})
export class SistemasFormComponent implements OnInit, OnChanges {

  @Input() row: Sistemas;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public form: FormGroup;
  public submitted = false;
  public datePipe: DatePipe;
  public instituciones: SelectItem[];

  tipoUso: SelectItemP[];
  yearStart: number = new Date().getFullYear() - 1;
  yearFinal: number = new Date().getFullYear() + 5;

  public id: any;
  public oeeNombre: any;
  public Session: any;
  public permiso: boolean = false;
  // DEFINIR EL PERMISO QUE VERIFICARA PARA PODER MOSTRAR TODOS LOS DATOS O SOLO LOS DE LA OEE
  public PermisoAdmin: string = 'PermisoAdmin';
  public existePermisoAdmin;

  constructor(
    private service: SistemasService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService,
    private institucionService: InstitucionService,
    private storageManager: StorageManagerService,
  ) { }

  ngOnChanges(): void {
    this.initComponent();
  }

  ngOnInit(): void {
    this.initComponent();
    this.loadInstituciones();
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.oeeNombre = this.storageManager.getCurrenSession().usuario.oee.descripcionOee;
    this.Session = this.storageManager.getCurrenSession().permisos;
    this.tipoUso = [
      { label: 'Ciudadanía ', value: 'C' },
      { label: 'Funcionarios', value: 'F' },
      { label: 'Ciudadanía - Funcionarios', value: 'CF' },
      { label: 'Funcionarios TIC', value: 'FTIC' }
    ];

    /* VERIFICA LA EXISTENCIA DEL PERMISO FIJADO MAS ARRIBA EN PermisoAdmin */
    this.existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
    this.existePermisoAdmin ? this.permiso = true : false;
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  loadInstituciones() {
    this.institucionService.getInstituciones().subscribe((response) => {
        if(response) {
           this.instituciones = response.data;
        }
    });
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;
    let oeeValidators = this.existePermisoAdmin ? ['', [ Validators.required ]] : [''];
    /* console.log('validators')
    console.log(oeeValidators) */

    this.form = this.formBuilder.group({
      idSistema: [''],
      oee: oeeValidators,
      /* oee: ['', [ Validators.required ]], */
      nombre: ['', [Validators.required]],
      objetoProposito: ['', [Validators.required]],
      areaResponsable: ['', [Validators.required]],
      tipoUso: ['', [Validators.required]],
      //TECNOLOGIA
      tecnologiaLenguaje: [''],
      tecnologiaBd: [''],
      tecnologiaFramework: [''],
      anhoCreacion: [''],
      anhoImplementacion: [''],
      desarrolladorFabricante: [''],
      poseeVigencia: [false],
      fechaVigencia: [''],
      //FUENTE
      poseeCodigoFuente: [false],
      linkCodigoFuente: [''],
      linkProduccion: [''],
      poseeLicencia: [false],
      tipoLicencia: [''],
      //SOPORTE
      poseeContratoMantenimiento: [false],
      tipoSoporte: [''],
      dataCenterInfraestructura: [''],
      costoDesarrollo: [''],
      costoMantenimiento: [''],
      listaDesarrolladores: [''],

    });

    this.formEdit();
  }


  formEdit() {
    const datepipe: DatePipe = new DatePipe('en-PY');
    if (this.row != null) {
      this.form.controls['idSistema'].setValue(this.row?.idSistema);
      this.form.controls['oee'].setValue({ id: this.row?.oee.id, nombre: this.row?.oee.descripcionOee });
      this.form.controls['nombre'].setValue(this.row?.nombre);
      this.form.controls['objetoProposito'].setValue(this.row?.objetoProposito);
      this.form.controls['areaResponsable'].setValue(this.row?.areaResponsable);
      this.form.controls['tipoUso'].setValue(this.row?.tipoUso);
      //TECNOLOGIA
      this.form.controls['tecnologiaLenguaje'].setValue(this.row?.tecnologiaLenguaje);
      this.form.controls['tecnologiaBd'].setValue(this.row?.tecnologiaBd);
      this.form.controls['tecnologiaFramework'].setValue(this.row?.tecnologiaFramework);
      this.form.controls['anhoCreacion'].setValue(this.row?.anhoCreacion);
      this.form.controls['anhoImplementacion'].setValue(this.row?.anhoImplementacion);
      this.form.controls['desarrolladorFabricante'].setValue(this.row?.desarrolladorFabricante);
      this.form.controls['poseeVigencia'].setValue(this.row?.poseeVigencia);
      this.form.controls['fechaVigencia'].setValue(datepipe.transform(this.row?.fechaVigencia, 'dd-MM-YYYY'));
      //FUENTE
      this.form.controls['poseeCodigoFuente'].setValue(this.row?.poseeCodigoFuente);
      this.form.controls['linkCodigoFuente'].setValue(this.row?.linkCodigoFuente);
      this.form.controls['linkProduccion'].setValue(this.row?.linkProduccion);
      this.form.controls['poseeLicencia'].setValue(this.row?.poseeLicencia);
      this.form.controls['tipoLicencia'].setValue(this.row?.tipoLicencia);
      //SOPORTE
      this.form.controls['poseeContratoMantenimiento'].setValue(this.row?.poseeContratoMantenimiento);
      this.form.controls['tipoSoporte'].setValue(this.row?.tipoSoporte);
      this.form.controls['dataCenterInfraestructura'].setValue(this.row?.dataCenterInfraestructura);
      this.row?.costoDesarrollo && this.form.controls['costoDesarrollo'].setValue(this.separadorMiles(this.row.costoDesarrollo));
      this.row?.costoMantenimiento && this.form.controls['costoMantenimiento'].setValue(this.separadorMiles(this.row.costoMantenimiento));
      this.form.controls['listaDesarrolladores'].setValue(this.row?.listaDesarrolladores);

    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;
    let oee = this.existePermisoAdmin ? formValue.oee : {id: this.id, nombre: this.oeeNombre};
   /*  console.log(formValue.oee) */

    const sistemas = new Sistemas();
    sistemas.idSistema = formValue.idSistema;
    sistemas.oee = oee;
    /* sistemas.oee = formValue.oee; */
    sistemas.nombre = formValue.nombre;
    sistemas.objetoProposito = formValue.objetoProposito;
    sistemas.areaResponsable = formValue.areaResponsable;
    sistemas.tipoUso = formValue.tipoUso;
    //TECNOLOGIA
    sistemas.tecnologiaLenguaje = formValue.tecnologiaLenguaje;
    sistemas.tecnologiaBd = formValue.tecnologiaBd;
    sistemas.tecnologiaFramework = formValue.tecnologiaFramework;
    sistemas.anhoCreacion = formValue.anhoCreacion;
    sistemas.anhoImplementacion = formValue.anhoImplementacion;
    sistemas.desarrolladorFabricante = formValue.desarrolladorFabricante;
    sistemas.poseeVigencia = formValue.poseeVigencia;
    sistemas.fechaVigencia = formValue.fechaVigencia;
    //FUENTE
    sistemas.poseeCodigoFuente = formValue.poseeCodigoFuente;
    sistemas.linkCodigoFuente = formValue.linkCodigoFuente;
    sistemas.linkProduccion = formValue.linkProduccion;
    sistemas.poseeLicencia = formValue.poseeLicencia;
    sistemas.tipoLicencia = formValue.tipoLicencia;
    //SOPORTE
    sistemas.poseeContratoMantenimiento = formValue.poseeContratoMantenimiento;
    sistemas.tipoSoporte = formValue.tipoSoporte;
    sistemas.dataCenterInfraestructura = formValue.dataCenterInfraestructura;
    sistemas.costoDesarrollo = this.limpiarSeparadoresMiles(formValue.costoDesarrollo);
    sistemas.costoMantenimiento = this.limpiarSeparadoresMiles(formValue.costoMantenimiento);
    sistemas.listaDesarrolladores = formValue.listaDesarrolladores;
    // console.log(sistemas);

    const response = this.row == null
      ? this.service.create(sistemas)
      : this.service.update(sistemas.idSistema, sistemas);

    response.subscribe(resp => {
      this.onResponse.emit(resp);
      if ([200, 201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

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
