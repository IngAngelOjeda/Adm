import { Component, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, MenuItem, Message, MessageService, TreeNode } from 'primeng/api';
import { SelectItem } from '../../../shared/models/select.model';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { OrganigramaTicService } from '../services/organigrama-tic.service';
import { UsuarioService } from '../../../usuario/services/usuario.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { delay } from 'rxjs/operators';
import { EditorModule } from 'primeng/editor';
import { Table } from '@fullcalendar/daygrid';
import { OrganigramaTic } from '../models/organigrama-tic.model';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
// EXPORTAR A ARCHIVOS
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-organigrama-tic-list',
  templateUrl: './organigrama-tic-list.component.html',
  styleUrls: ['./organigrama-tic-list.component.scss']
})
export class OrganigramaTicListComponent implements OnInit {

  @ViewChild("table") table: Table;
  items: MenuItem[];

  public msgs: Message[] = [];
  public usuarios: SelectItem[];
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
  public organigrama: OrganigramaTic[];
  public entity: OrganigramaTic;
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
    private service: OrganigramaTicService,
    private permission: PermissionGuardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    private usuarioService: UsuarioService,

    private storageManager: StorageManagerService
  ) {
    this.breadcrumbService.setItems([
      { label: "Administración" },
      { label: "Organigramas TIC", routerLink: ["/oee/organigrama-tic"] },
    ]);

    this.searchFormGroup = this.formBuilder.group({
      idFuncionario: [],
      nombre: [],
      apellido: [],
      cedula: [],
      cargo: [],
      correoInstitucional: [],
      descripcionDependencia: [],
      descripcionOee: [],
    });

  }

  ngOnInit(): void {
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.loadUsuariosOee(this.id);
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

  formatearNumero(numero:number){
    return new Intl.NumberFormat("es-CL").format(numero);
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
          var array = response.data?.lista;
          /* array.forEach(e => {
            e.presupuestoTicAnual = this.formatearNumero(e.presupuestoTicAnual);
          }); */
            this.organigrama = response.data?.lista;
            this.totalRecords = this.checkPermission('dependencia:listar') ? response.data?.totalRecords : 0;
        }
      }, error => {
          this.loading = false;
          console.log(error);
      }
    );
  }

  updateStatus(row: OrganigramaTic) {
    this.confirmationService.confirm({
      message: `Está seguro que desea ${ row.estadoFuncionario ? 'inactivar' : 'habilitar' } este Funcionario?`,
      header: '¡Atención',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.updateStatus(row.idFuncionario).subscribe(response => {
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

  loadUsuariosOee(id:number) {
    this.usuarioService.getUsuariosOeeList(id).subscribe((response) => {
      if (response) {
        this.usuarios = response.data;
      }
    });
  }

  openNew() {
    this.showDialog = true;
    this.entity = null;
  }

  openEdit(row: OrganigramaTic) {
    this.showDialog = true;
    this.entity = {...row};
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
  }

  openOrganigrama(){
    this.showOrganigram = true;
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
                  this.exportCsv('Funcionarios_export_' + new Date().getTime() +'.csv', body);
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
    this.saveAsExcelFile(excelBuffer, "Funcionarios");
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
  FileSaver.saveAs(blob, "Funcionarios" + '_export_' + new Date().getTime() + EXTENSION);
}

getColumnsExportExcel(data) {
    let body = [];
    for (let row of data) {            
        body.push({
            'Nombre': row.nombre,
            'Apellido': row.apellido,
            'Dependencia': row.dependencia.descripcionDependencia,
            'Cédula': row.cedula,
            'Cargo': row.cargo,
            'Correo Institucional': row.correoInstitucional,
            'Estado': row.estadoFuncionario == true ? 'ACTIVO' : 'INACTIVO',
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

generarPDF() {
  const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
  existePermisoAdmin ? this.permiso = true : '';
  this.service.getInforme(this.searchFormGroup.value, "pdf", this.id, this.permiso).subscribe(
    (data: Blob) => {
      FileSaver.saveAs(data, 'Reporte Organigrama.pdf');
    },
    error => {
      console.error('Error al descargar el Reporte', error);
    }
  );
}

/* generarPDF() {
  this.service.getInforme("pdf", this.id).subscribe(
    (data: Blob) => {
      FileSaver.saveAs(data, 'Reporte Organigrama.pdf');
    },
    error => {
      console.error('Error al descargar el Reporte', error);
    }
  );
} */



}
