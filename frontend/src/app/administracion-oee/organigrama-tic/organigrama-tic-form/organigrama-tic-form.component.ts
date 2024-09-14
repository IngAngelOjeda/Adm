import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { SelectItem } from '../../../shared/models/select.model';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatePipe } from '@angular/common';
import { OrganigramaTic } from '../models/organigrama-tic.model';
import { OrganigramaTicService } from '../services/organigrama-tic.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { selectItem } from '../../datos-oee/models/dependencias.model';
import { UsuarioService } from '../../../usuario/services/usuario.service';

@Component({
  selector: 'app-organigrama-tic-form',
  templateUrl: './organigrama-tic-form.component.html',
  styleUrls: ['./organigrama-tic-form.component.scss']
})
export class OrganigramaTicFormComponent implements OnInit, OnChanges {

  @Input() row: OrganigramaTic;
  @Input() usuarios: SelectItem[];
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public form: FormGroup;
  public submitted = false;
  public datePipe: DatePipe;

  dependencia: selectItem[];
  /* dependencia: SelectItem[]; */
  /* selectedDrop: SelectItem; */
  yearStart: number = new Date().getFullYear() - 1;
  yearFinal: number = new Date().getFullYear() + 5;
  
  public id: any;
  public idOee: any;

  constructor(
    private service: OrganigramaTicService,
    private formBuilder: FormBuilder,
    private permission: PermissionGuardService,
    private OrganigramaService: OrganigramaTicService,

    private storageManager: StorageManagerService,
    private usuarioService: UsuarioService
  ) { }

  ngOnChanges(): void {
    this.initComponent();
    this.loadDependencias();
  }

  ngOnInit(): void {
    this.initComponent();
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  onUsuarioSeleccionado(event: any) {
    const usuarioSeleccionado = event.value;
  
    if (usuarioSeleccionado && usuarioSeleccionado.id) {
      // Si usuarioSeleccionado.id existe, realiza la llamada al servicio
      this.usuarioService.getUsuarioOee(usuarioSeleccionado.id).subscribe((response) => {
        if (response && response.data && response.data.length > 0) {
          const usuarioData = response.data[0];
          this.form.controls['nombre'].setValue(usuarioData?.nombre);
          this.form.controls['apellido'].setValue(usuarioData?.apellido);
          this.form.controls['cedula'].setValue(usuarioData?.cedula);
          this.form.controls['cargo'].setValue(usuarioData?.cargo);
          this.form.controls['correoInstitucional'].setValue(usuarioData?.correo);
        } else {
          // Limpiar los campos si no se encuentra ningún usuario con ese ID
          this.limpiarCampos();
        }
      });
    } else {
      // Limpiar los campos si no hay usuario seleccionado o no tiene ID
      this.limpiarCampos();
    }
  }
  
  limpiarCampos() {
    // Puedes implementar la lógica para limpiar los campos según tus necesidades
    this.form.reset(); // Esto restablecerá todos los campos del formulario a sus valores iniciales
  }
  

  loadDependencias() {
    this.idOee = this.storageManager.getCurrenSession().usuario.oee.id;
    /* console.log(this.idOee)
    debugger; */
    this.OrganigramaService.getDependencias(this.idOee).subscribe((response) => {
        if(response) {
          let data = response.data;
          
          let comboDependenciaPadre = data.map(function(item) {
            return {
              "idDependencia": item.idDependencia,
              "descripcionDependencia": item.descripcionDependencia
            };
          });

          this.dependencia = comboDependenciaPadre;
        }
    });
  }

  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      // poseeCodigoFuente: [false],
      idFuncionario: [''],
      idUsuario: [''],
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      cedula: ['', Validators.required],
      cargo: ['', Validators.required],
      correoInstitucional: ['', Validators.required],
      idDependencia: ['', Validators.required],
      /* presupuestoTicAnual: ['', Validators.required], */
    });

    this.formEdit();
  }


  formEdit() {
    const datepipe: DatePipe = new DatePipe('en-PY');
    if (this.row != null) {
      /* console.log(this.row)
      debugger */
      this.form.controls['idFuncionario'].setValue(this.row?.idFuncionario);
      this.form.controls['nombre'].setValue(this.row?.nombre);
      this.form.controls['apellido'].setValue(this.row?.apellido);
      this.form.controls['cedula'].setValue(this.row?.cedula);
      this.form.controls['cargo'].setValue(this.row?.cargo);
      this.form.controls['correoInstitucional'].setValue(this.row?.correoInstitucional);
      this.form.controls['idDependencia'].setValue({ idDependencia: this.row?.dependencia['idDependencia'], descripcionDependencia: this.row?.dependencia['descripcionDependencia'] });
      /* this.form.controls['presupuestoTicAnual'].setValue(this.separadorMiles(this.row?.presupuestoTicAnual)); */
    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const organigrama               = new OrganigramaTic();
    organigrama.idFuncionario       = formValue.idFuncionario;
    organigrama.nombre              = formValue.nombre;
    organigrama.apellido            = formValue.apellido;
    organigrama.cedula              = formValue.cedula;
    organigrama.cargo               = formValue.cargo;
    organigrama.correoInstitucional = formValue.correoInstitucional;
    organigrama.dependencia         = formValue.idDependencia;

    /* console.log(organigrama);
    debugger; */

    const response = this.row == null
      ? this.service.create(organigrama)
      : this.service.update(organigrama.idFuncionario, organigrama);

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

