import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Dependencias } from '../models/dependencias.model';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { DependenciasService } from '../services/dependencias.service';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { Table } from '@fullcalendar/daygrid';
import { delay } from 'rxjs/operators';
// EXPORTAR A ARCHIVOS
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
/* EXPORTAR A PDF */

import { HttpClient, HttpParams } from '@angular/common/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-dependencias-list',
  templateUrl: './dependencias-list.component.html',
  styleUrls: ['./dependencias-list.component.scss']
})
export class DependenciasListComponent implements OnInit {

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

  ngOnInit(): void {
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
        },
        {
          label: 'Exportar PDF',
          icon: 'pi pi-file-pdf', 
          command: () => {
              this.exportData('pdf');
          }
        }
    ];
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

  /* formatearNumero(numero:number){
    return new Intl.NumberFormat("es-CL").format(numero);
  } */

  loadData() {
    this.loading = true;
    // VERIFICA LA EXISTENCIA DEL PERMISO PARA VER TODOS LOS DATOS
    const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
    existePermisoAdmin ? this.permiso = true : '';
    this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso)
      .subscribe((response) => {
        this.loading = false;
        if(response) {
          /* var array = response.data?.lista; */
          /* array.forEach(e => {
            e.presupuestoTicAnual = this.formatearNumero(e.presupuestoTicAnual);
          }); */
            this.dependencias = response.data?.lista;
            this.totalRecords = this.checkPermission('dependencia:listar') ? response.data?.totalRecords : 0;
        }
      }, error => {
          this.loading = false;
          console.log(error);
      }
    );
  }


  openNew() {
    this.showDialog = true;
    this.entity = null;
  }

  openEdit(row: Dependencias) {
    this.showDialog = true;
    this.entity = {...row};
  }
  
  openOrganigrama(){
    this.showOrganigram = true;
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
  }
  changeDialogVisibilityO($event) {
    this.showOrganigram = $event;
  }

  onResponse(res: MessageResponse) {
    if (res.code == 200) {
      this.messageService.add({severity: 'success', summary: 'Operación exitosa', detail: 'Operación exitosa', life: 3000});
      this.loadData();
    } else {
      if (res.code < 500) {
        this.messageService.add({severity: 'warn', summary: 'Atención', detail: res.message, life: 3000});
      } else {
        this.messageService.add({severity: 'error', summary: 'Atención', detail: 'Error al procesar la operación', life: 3000});
      }
    }
  }

  updateStatus(row: Dependencias) {
    this.confirmationService.confirm({
      message: `Está seguro que desea ${ row.estadoDependencia ? 'inactivar' : 'habilitar' } esta Dependencia?`,
      header: '¡Atención',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.updateStatus(row.idDependencia).subscribe(response => {
            if(response?.code == 200) {
                this.messageService.add({severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000});
                this.loadData();
            } else {
                this.messageService.add({severity: 'error', summary: 'Atención!', detail: `Error al cambiar el estado`, life: 3000});
            }
        }, error => console.log(error));
      }
    });
  }

  // FUNCION EXPORTAR EN FORMATOS

  exportData(band) {
    this.service.getAll(this.filter, -1, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso).subscribe((response) => {
        if(response) {
          this.listAll = response.data?.lista;
          switch ( band ) {
            case 'xls':
              this.exportExcel(this.listAll);
              break;
            case 'csv':
              let tablaTemp = this.listAll;
              this.list = this.listAll;
              let body = this.getColumnsExportExcel(this.listAll);
              
              setTimeout(() => {
                  this.exportCsv('Dependencias_export_' + new Date().getTime() +'.csv', body);
                  this.listAll = tablaTemp;
              }, 2000);
              break;
            case 'json':
              this.exportJson(this.listAll);
              break;
            case 'pdf':
              this.generarPDF();
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
    this.saveAsExcelFile(excelBuffer, "Dependencias");
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
  }else {
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
  var blob = new Blob([JSON.stringify(listar)], {type:"application/json;charset=utf-8"});
  FileSaver.saveAs(blob, "Dependencias" + '_export_' + new Date().getTime() + EXTENSION);
}

getColumnsExportExcel(data) {
    let body = [];
    for (let row of data) {            
        body.push({
            'codigo': row.codigo,
            'Dependencia': row.descripcionDependencia,
            'Dependencia Padre': row.dependenciaPadre ? row.dependenciaPadre['descripcionDependencia'] : '',
            'Descripcion OEE': row.oee.descripcionOee,
            'Estado': row.estadoDependencia == true ? 'ACTIVO' : 'INACTIVO',
        });
    }
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

// FUNCION DESCARGAR EN PDF

/* generarPDF() {
  this.service.getInforme("pdf", this.id).subscribe(
    (data: Blob) => {
      FileSaver.saveAs(data, 'Reporte Dependencias.pdf');
    },
    error => {
      console.error('Error al descargar el Reporte', error);
    }
  );
} */

generarPDF() {
  const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
  existePermisoAdmin ? this.permiso = true : '';
  this.service.getInforme(this.searchFormGroup.value, "pdf", this.id, this.permiso).subscribe(
    (data: Blob) => {
      FileSaver.saveAs(data, 'Reporte Dependencias.pdf');
    },
    error => {
      console.error('Error al descargar el Reporte', error);
    }
  );
}


}
