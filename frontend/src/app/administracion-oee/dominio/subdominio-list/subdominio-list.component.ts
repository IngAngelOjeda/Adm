import { Component, OnInit, Input, EventEmitter, Output, OnChanges, ViewChild } from '@angular/core';
import { Table } from 'primeng/table';
import { delay } from 'rxjs/operators';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SubdominioService } from '../services/subdominio.service';
import { PermissionGuardService } from '../../../shared/services/permission-guard.service';
import { Dominio } from '../models/dominio.model';
import { SubDominio } from '../models/subdominio.model';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-subdominio-list',
  templateUrl: './subdominio-list.component.html',
  styleUrls: ['./subdominio-list.component.scss']
})
export class SubdominioListComponent implements OnInit {

  @ViewChild("modalTable") modalTable: Table;
  items: MenuItem[];
  @Input() row: Dominio;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  public searchFormGroupSub: FormGroup;
  public filter: string;
  public pageSize: number = 5;
  public start: number = 0;
  public totalRecords: number = 0;
  public sortAsc: boolean = true;
  public sortField: string;
  public loading: boolean = true;
  public subdominios: SubDominio[];
  public entity: SubDominio;
  public showSubdominiosList: boolean;
  public showDialog: boolean;

  public list: any[];
  public listAll: any[];
  public id: any;
  public Session: any;
  public permiso: boolean = false;
  // DEFINIR EL PERMISO QUE VERIFICARA PARA PODER MOSTRAR TODOS LOS DATOS O SOLO LOS DE LA OEE
  public PermisoAdmin: string = 'PermisoAdmin';

  constructor(
    private service: SubdominioService,
    private permission: PermissionGuardService,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,

    private storageManager: StorageManagerService,
  ) {
    this.searchFormGroupSub = this.formBuilder.group({
      idSubDominio: [],
      subdominio: [],
    });
  }

  ngOnInit(): void {
    this.initComponent();
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
      //{separator: true},
      //{label: 'Setup', icon: 'pi pi-cog', routerLink: ['/#']}
    ];
  }

  ngOnDestroy(): void {
    this.service.disconnet();
  }

  ngOnChanges(): void {
    // console.log(this.row);

    this.initComponent();
  }

  openNew(row: SubDominio) {
    this.showDialog = true;
    this.entity = { ...row };
    this.entity.dominio = this.row;
  }
  openEdit(row: SubDominio) {
    this.showDialog = true;
    this.entity = { ...row };
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
  }

  initComponent(): void {
    if (this.row && this.row.idDominio) {
      this.loadData();
    }
  }

  loadSub($event: any) {
    this.filter = $event?.globalFilter ? $event.globalFilter : null;
    this.start = $event?.first;
    this.pageSize = $event?.rows ? $event.rows : null;
    this.sortField = $event?.sortField;
    this.sortAsc = $event?.sortOrder == 1 ? true : false;
    if (this.row && this.row.idDominio) {
      this.loadData();
    }
  }

  loadData() {
    this.loading = true;
    this.service.getAll(this.row.idDominio, this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroupSub.value).subscribe((response) => {
      this.loading = false;
      if (response) {
        this.subdominios = response.data?.lista;
        this.totalRecords = this.checkPermission('subdominio:listar') ? response.data?.totalRecords : 0;
      }
    }, error => {
      this.loading = false;
      console.log(error);
    });
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  close() {
    this.setVisible.emit(false);
  }

  updateStatus(row: SubDominio) {
    this.confirmationService.confirm({
      message: `Está seguro que desea ${row.estado ? 'inactivar' : 'habilitar'} <b>${row.subdominio}</b>?`,
      header: '¡Atención!',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.updateStatus(row.idSubDominio).subscribe(response => {
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

  delete(row: SubDominio) {
    this.confirmationService.confirm({
      message: `Está seguro que desea eliminar <b>${row.subdominio}</b>?`,
      header: '¡Atención!',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.delete(row.idSubDominio).subscribe(response => {
          if (response?.code == 200) {
            this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: response.message, life: 3000 });
            this.loadData();
          } else {
            this.messageService.add({ severity: 'error', summary: 'Atención!', detail: `Error al eliminar`, life: 3000 });
          }
        }, error => console.log(error));
      }
    });
  }

  onResponse(res: MessageResponse) {
    if (res.code == 200) {
      this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: res.message, life: 3000 });
      this.loadData();
    } else {
      if (res.code < 500) {
        this.messageService.add({ severity: 'warn', summary: 'Atención', detail: res.message, life: 3000 });
      } else {
        this.messageService.add({ severity: 'error', summary: 'Atención', detail: 'Error al procesar la operación', life: 3000 });
      }
    }
  }

  filterModal(event: Event) {
    event.preventDefault();
    const valueToFilter = this.searchFormGroupSub.value;
    this.modalTable.filter(valueToFilter, '', 'contains');
    this.loadData();
  }

  exportData(band) {
    this.service.getAll(this.row.idDominio,this.filter, -1, this.start, this.sortField, this.sortAsc, this.searchFormGroupSub.value).subscribe((response) => {
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
              this.exportCsv('Subdominios_export_' + new Date().getTime() + '.csv', body);
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
    this.saveAsExcelFile(excelBuffer, "Subdominios");
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
    FileSaver.saveAs(blob, "Subdominios" + '_export_' + new Date().getTime() + EXTENSION);
  }

  getColumnsExportExcel(data) {
    let body = [];
    for (let row of data) {
      let dominio = row?.dominio;
      let oee = dominio.oee;

      body.push({
        'Subdominio': row.subdominio,
        'Dominio': dominio.dominio,
        'Descripción OEE': oee.descripcionOee,
        'Estado': row?.estado != undefined ? 'ACTIVO' : 'INACTIVO'
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

  generarPDF() {
    const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
    existePermisoAdmin ? this.permiso = true : '';
    this.service.getInforme(this.searchFormGroupSub.value, "pdf", this.id, this.permiso).subscribe(
      (data: Blob) => {
        FileSaver.saveAs(data, 'Reporte Subdominios.pdf');
      },
      error => {
        console.error('Error al descargar el Reporte', error);
      }
    );
  }

}
