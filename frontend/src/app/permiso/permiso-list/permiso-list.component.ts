import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { delay } from 'rxjs/operators';
import { AppBreadcrumbService } from '../../app.breadcrumb.service';
import { MessageResponse } from '../../shared/models/message-response.model';
import { PermissionGuardService } from '../../shared/services/permission-guard.service';
import { Permiso } from './../models/permiso.model';
import { PermisoService } from './../services/permiso.service';
// EXPORTAR A ARCHIVOS
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-permiso-list',
  templateUrl: './permiso-list.component.html',
  styleUrls: ['./permiso-list.component.scss']
})
export class PermisoListComponent implements OnInit {

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
  public permisos: Permiso[];
  public entity: Permiso;

  // EXPORTAR A ARCHIVOS
  public list: any[];
  public listAll: any[];
  public id: any;


  constructor(
    private breadcrumbService: AppBreadcrumbService,
    private service: PermisoService,
    private permission: PermissionGuardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,

    private storageManager: StorageManagerService
  ) {

    this.breadcrumbService.setItems([
      { label: "Administración" },
      { label: "Permisos", routerLink: ["/administracion/permiso"] },
    ]);

    this.searchFormGroup = this.formBuilder.group({
      idPermiso: [],
      nombre: [],
      descripcion: [],
    });

  }

  ngOnInit(): void {
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
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
    this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value)
      .subscribe((response) => {
        this.loading = false;
        if(response) {
            this.permisos = response.data?.lista;
            this.totalRecords = this.checkPermission('permisos:listar') ? response.data?.totalRecords : 0;
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

  openEdit(row: Permiso) {
    this.showDialog = true;
    this.entity = {...row};
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
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

  delete(row: Permiso) {
    this.confirmationService.confirm({
      message: `Está seguro que desea dar de baja el permiso <b>${row.nombre}</b>?`,
      header: 'Eliminar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.service.delete(row.idPermiso).subscribe(response => {
          console.log('status',response);
          if(response?.status == 200) {
            this.messageService.add({severity: 'success', summary: 'Operación exitosa', detail: `El permiso ${row.nombre} se dió de baja con éxito`, life: 3000});
            this.loadData();
          } else {
            this.messageService.add({severity: 'error', summary: 'Atención!', detail: `No se pudo dar de baja`, life: 3000});
          }
        }, error => console.log(error));
      }
    });
  }

  // FUNCION EXPORTAR EN FORMATOS

  exportData(band) {
    this.service.getAll(this.filter, 1000, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value).subscribe((response) => {
    /* this.service.getAll(this.filter, -1, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id).subscribe((response) => { */
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
                  this.exportCsv('Permisos_export_' + new Date().getTime() +'.csv', body);
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
    this.saveAsExcelFile(excelBuffer, "Permisos");
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
  FileSaver.saveAs(blob, "Permisos" + '_export_' + new Date().getTime() + EXTENSION);
}

getColumnsExportExcel(data) {
    let body = [];
    for (let row of data) {            
        body.push({
            'Nombre': row.nombre,
            'Descripción': row.descripcion,
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

}
