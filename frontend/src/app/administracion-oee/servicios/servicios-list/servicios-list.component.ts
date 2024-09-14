import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { Component, OnInit, ViewChild } from '@angular/core';
import { SelectItem } from '../../../shared/models/select.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { delay } from 'rxjs/operators';
import { AppBreadcrumbService } from '../../../app.breadcrumb.service';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { PermissionGuardService } from '../../../shared/services/permission-guard.service';
import { Servicio } from '../models/servicio.model';
import { SelectItemClasificador } from '../models/servicio-clasificador.model';
import { SelectItemEtiqueta } from '../models/servicio-etiqueta.model';
import { SelectItemRequisito } from '../models/servicio-requisito.model';
import { ServicioService } from '../services/servicios.service';
import { InstitucionService } from '../../../institucion/services/institucion.service';
import { EtiquetaService } from '../../../administracion-oee/etiquetas/services/etiqueta.service';
import { RequisitoService } from '../../../administracion-oee/requisito/services/requisito.service';
import { ClasificadorService } from '../../../administracion-oee/clasificador/services/clasificador.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-servicio-list',
  templateUrl: './servicios-list.component.html',
  styleUrls: ['./servicios-list.component.scss']
})
export class ServiciosListComponent implements OnInit {

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
  public showDialogInformacionList: boolean;
  public servicios: Servicio[];
  public instituciones: SelectItem[];
  public etiquetas: SelectItemEtiqueta[];
  public requisitos: SelectItemRequisito[];
  public clasificadores: SelectItemClasificador[];
  public entity: Servicio;

  public list: any[];
  public listAll: any[];
  public idOee: number;
  public id: any;
  public Session: any;
  public permiso: boolean = false;
  // DEFINIR EL PERMISO QUE VERIFICARA PARA PODER MOSTRAR TODOS LOS DATOS O SOLO LOS DE LA OEE
  public PermisoAdmin: string = 'PermisoAdmin';

  constructor(
    private breadcrumbService: AppBreadcrumbService,
    private service: ServicioService,
    private permission: PermissionGuardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    private institucionService: InstitucionService,
    private EtiquetaService: EtiquetaService,
    private RequisitoService: RequisitoService,
    private ClasificadorService: ClasificadorService,
    private storageManager: StorageManagerService,

  ) {

    this.breadcrumbService.setItems([
      { label: "Datos Servicios" },
      { label: "Servicios", routerLink: ["/administracion/usuario"] },
    ]);

    this.searchFormGroup = this.formBuilder.group({
      idServicio: [],
      nombreServicio: [],
      descripcionServicio: [],
      descripcionOee: [],
    });

  }

  ngOnInit(): void {
    this.loadInstituciones();
    this.loadEtiquetas();
    this.loadRequisitos();
    this.loadClasificadores();
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.Session = this.storageManager.getCurrenSession().permisos;
    this.items = [
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
      }
    ];
  }

  ngOnDestroy(): void {
    this.service.disconnet();
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

    this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso).subscribe((response) => {
      this.loading = false;
      if (response) {
        this.servicios = response.data?.lista;
        let datos = response.data?.lista;
        datos.forEach(e => {
            let urlPortal = "https://www.paraguay.gov.py/oee/"+e.oee.urlOee+"/"+e.idServicio;
            e.urlPortal = urlPortal;
        });
        this.totalRecords = this.checkPermission('servicioOee:listar') ? response.data?.totalRecords : 0;
      }
    }, error => {
      this.loading = false;
      console.log(error);
    });
  }

  loadInstituciones() {
    this.institucionService.getInstituciones().subscribe((response) => {
      if (response) {
        this.instituciones = response.data;
      }
    });
  }

  loadEtiquetas() {
    this.EtiquetaService.getEtiquetas().subscribe((response) => {
      if (response) {
        this.etiquetas = response.data;
      }
    });
  }

  loadRequisitos() {
    this.RequisitoService.getRequisitos().subscribe((response) => {
      if (response) {
        this.requisitos = response.data;
      }
    });
  }

  loadClasificadores() {
    this.ClasificadorService.getClasificadores().subscribe((response) => {
      if (response) {
        this.clasificadores = response.data;
      }
    });
  }


  openLink(url: string) {
    // Abre una nueva pestaña con la URL proporcionada
    window.open(url, '_blank');
  }

  openNew() {
    this.showDialog = true;
    this.entity = null;
  }

  openInformacion(row: Servicio) {
    this.showDialogInformacionList = true;
    this.entity = { ...row };
  }
  changeDialogVisibilityInformacion($event) {
    this.showDialogInformacionList = $event;
  }

  openEdit(row: Servicio) {
    this.showDialog = true;
    this.entity = { ...row };
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
  }

  onResponse(res: MessageResponse) {
    if (res.code == 200) {
      this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: 'Operación exitosa', life: 3000 });
      this.loadData();
    } else {
      if (res.code < 500) {
        this.messageService.add({ severity: 'warn', summary: 'Atención', detail: res.message, life: 3000 });
      } else {
        this.messageService.add({ severity: 'error', summary: 'Atención', detail: 'Error al procesar la operación', life: 3000 });
      }
    }
  }

  updateStatus(row: Servicio) {
    this.confirmationService.confirm({
      message: `Está seguro que desea ${row.estadoServicio ? 'inactivar' : 'habilitar'} el Servicio <b>${row.nombreServicio}</b>?`,
      header: '¡Atención',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.updateStatus(row.idServicio).subscribe(response => {
          if (response?.code == 200) {
            this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000 });
            this.loadData();
          } else {
            this.messageService.add({ severity: 'error', summary: 'Atención!', detail: `Error al cambiar el estado`, life: 3000 });
          }
        }, error => console.log(error));
      }
    });
  }

  exportData(band) {
    this.service.getAll(this.filter, -1, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso).subscribe((response) => {
      if (response) {
        this.listAll = response.data?.lista;
        switch (band) {
          case 'xls':
            this.exportExcel(this.listAll);
            break;
          case 'csv':
            let tablaTemp = this.listAll;
            this.list = this.listAll;
            let body = this.getColumnsExportExcel(this.listAll);

            setTimeout(() => {
              this.exportCsv('Servicios_export_' + new Date().getTime() + '.csv', body);
              this.listAll = tablaTemp;
            }, 2000);
            break;
          case 'json':
            this.exportJson(this.listAll);
            break;
          default:
            break;
        }
      }
    }, error => {
      console.log(error);
    });
  }

  exportExcel(dataExport) {
    const worksheet = XLSX.utils.json_to_sheet(this.getColumnsExportExcel(dataExport));
    const workbook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, "Servicio");
  }

  exportCsv(filename: string, rows: object[]) {
    if (!rows || !rows.length) {
      return;
    }
    const separator = ',';
    const keys = Object.keys(rows[0]);
    const csvContent =
      keys.join(separator) +
      '\n' +
      rows.map(row => {
        return keys.map(k => {
          let cell = row[k] === null || row[k] === undefined ? '' : row[k];
          cell = cell instanceof Date
            ? cell.toLocaleString()
            : cell.toString().replace(/"/g, '""');
          if (cell.search(/("|,|\n)/g) >= 0) {
            cell = `"${cell}"`;
          }
          return cell;
        }).join(separator);
      }).join('\n');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const nav = (window.navigator as any);
    if (nav.msSaveOrOpenBlob) {
      // IE 10+
      nav.msSaveOrOpenBlob(blob, filename);
    } else {
      const link = document.createElement('a');
      if (link.download !== undefined) {
        // Browsers that support HTML5 download attribute
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  }

  exportJson(listar) {
    let EXTENSION = '.json';
    var blob = new Blob([JSON.stringify(listar)], { type: "application/json;charset=utf-8" });
    FileSaver.saveAs(blob, "Servicio" + '_export_' + new Date().getTime() + EXTENSION);
  }

  getColumnsExportExcel(data) {
    let body = [];
    for (let row of data) {
      body.push({
        'Nombre Serv.': row.nombreServicio,
        'Desc. Serv.': row.descripcionServicio,
        'Institución': row.oee.descripcionOee,
        'URL. Serv.': row.urlOnline,
        /* 'Destacado': row.destacado, */
        'Destacado': row?.destacado && row?.destacado != undefined ? 'SI' : 'NO',
        'Fecha Creación': row.fechaCreacion,
        'Fecha Modificacion': row.fechaModificacion,
        'Estado Serv.': row?.estadoServicio == "A" ? 'ACTIVO' : 'INACTIVO'
        /* 'Estado Serv.': row?.estadoServicio != undefined ? row?.estadoServicio : '' */
      });
    }
    //console.table(body);
    return body;
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE
    });
    FileSaver.saveAs(data, fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION);
  }

  confirmAllServices() {
    this.idOee = this.storageManager.getCurrenSession().usuario.oee.id;
    this.confirmationService.confirm({
      message: `Está seguro que desea confirmar todos los servicios ?`,
      header: '¡Atención!',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.confirmAllServices(this.idOee).subscribe(response => {
          if (response?.code == 200) {
            this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000 });
            this.loadData();
          } else {
            this.messageService.add({ severity: 'error', summary: '¡Atención!', detail: `No se pudo modificar`, life: 3000 });
          }
        }, error => console.log(error));
      }
    });
  }
}
