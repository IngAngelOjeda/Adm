

import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
// EXPORTAR A ARCHIVOS
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
import { Table } from '@fullcalendar/daygrid';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Dependencias } from '../../datos-oee/models/dependencias.model';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { DependenciasService } from '../../datos-oee/services/dependencias.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { HttpClient } from '@angular/common/http';
import { delay } from 'rxjs/operators';

@Component({
  selector: 'app-informacion-dependencias-list',
  templateUrl: './informacion-dependencias-list.component.html',
  styleUrls: ['./informacion-dependencias-list.component.scss']
})
export class InformacionDependenciasListComponent implements OnInit {

  @Input() rowDependencia: Dependencias;
  @Input() visibleDependencia: boolean;
  @Output() setVisibleDependencia: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  @ViewChild("table") table: Table;
  
  items: MenuItem[];

  public msgs: Message[] = [];
  public searchFormGroup: FormGroup;
  public pageSize: number = 10;
  public start: number = 0;
  public filter: string;
  public totalRecords: number = 0;
  public sortAsc: boolean = true;
  public sortField: string;

  public loading: boolean = true;
  public showDialog: boolean;
  public showOrganigram: boolean;
  public dependencias: Dependencias[];
  public entity: Dependencias;
  public cols: 3;

  // EXPORTAR A ARCHIVOS
  public list: any[];
  public listAll: any[];
  public id: any;
  public Session: any;
  public permiso: boolean = false;
  // DEFINIR EL PERMISO QUE VERIFICARA PARA PODER MOSTRAR TODOS LOS DATOS O SOLO LOS DE LA OEE
  public PermisoAdmin: string = 'PermisoAdmin';

  constructor(
    private breadcrumbService: AppBreadcrumbService,
    private service: DependenciasService,
    private permission: PermissionGuardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,

    private storageManager: StorageManagerService,

    private http: HttpClient
  ) { 
    this.searchFormGroup = this.formBuilder.group({
      codigo: [],
      descripcionDependencia: [],
      descripcionOee: [],
    });
  }

  ngOnChanges(): void {
    this.initComponent();
    // this.id           = this.storageManager.getCurrenSession().usuario.oee.id;
    // this.oee          = this.storageManager.getCurrenSession().usuario.oee;
  }

  ngOnInit(): void {
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
    // this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.Session = this.storageManager.getCurrenSession().permisos;
    /* this.items = [
      {
            label: 'Exportar Excel',
            icon: 'pi pi-file-excel', 
            command: () => {
                this.exportData('xls');
            }
        },
        {
            label: 'Exportar CSV',
            icon: 'pi pi-file-o', 
            command: () => {
                this.exportData('csv');
            }
        },
        {
            label: 'Exportar JSON',
            icon: 'pi pi-file', 
            command: () => {
                this.exportData('json');
            }
        },
        {
          label: 'Exportar PDF',
          icon: 'pi pi-file-pdf', 
          command: () => {
              this.exportData('pdf');
          }
      }
    ]; */
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  load($event: any) {
    this.filter = $event?.globalFilter ? $event.globalFilter : null;
    this.start = $event?.first;
    this.pageSize = $event?.rows ? $event.rows : null;
    this.sortField = $event?.sortField;
    this.sortAsc = $event?.sortOrder == 1 ? true : false;
    this.loadData();
  }

  loadData() {
    this.loading = true;
    // VERIFICA LA EXISTENCIA DEL PERMISO PARA VER TODOS LOS DATOS
    const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
    existePermisoAdmin ? this.permiso = true : '';
    this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso)
      .subscribe((response) => {
        this.loading = false;
        if(response) {
            this.dependencias = response.data?.lista;
            this.totalRecords = this.checkPermission('dependencia:listar') ? response.data?.totalRecords : 0;
        }
      }, error => {
          this.loading = false;
          console.log(error);
      }
    );
  }

  initComponent(): void {
    // this.datePipe = new DatePipe('es-PY');
    // this.submitted = false;

    // this.form = this.formBuilder.group({
    //   idPlan: [''],
    //   anho: ['', Validators.required],
    //   fecha: ['', Validators.required],
    //   linkPlan: ['', Validators.required],
    //   cantidadFuncionariosTic: ['', Validators.required],
    //   cantidadFuncionariosAdmin: ['', Validators.required],
    //   presupuestoTicAnual: ['', Validators.required],

    // });

    // this.formEdit();
    // console.log("Dependencia es")
    // console.log(this.rowDependencia.id)
    if(this.rowDependencia){
      this.id = this.rowDependencia.id;
      this.loadData();
    }
  }

  close() {
    this.setVisibleDependencia.emit(false);
  }

}
