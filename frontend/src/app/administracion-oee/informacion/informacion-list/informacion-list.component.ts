import { Component, OnInit, ViewChild } from '@angular/core';
import { Informacion } from '../models/informacion.model';
import { Table } from '@fullcalendar/daygrid';
import { delay } from 'rxjs/operators';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { FormBuilder, FormGroup } from '@angular/forms';
import { InformacionService } from '../services/informacion.service';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { MessageResponse } from 'src/app/shared/models/message-response.model';

@Component({
  selector: 'app-informacion-list',
  templateUrl: './informacion-list.component.html',
  styleUrls: ['./informacion-list.component.scss']
})
export class InformacionListComponent implements OnInit {

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
  public showDependencia: boolean;
  public showOrganigram: boolean;
  public informacion: Informacion[];
  public entity: Informacion;
  public cols: 3;

  // EXPORTAR A ARCHIVOS
  public list: any[];
  public listAll: any[];
  public id: any;
  public idRol: any;
  public Session: any;
  public permiso: boolean = false;
  // DEFINIR EL PERMISO QUE VERIFICARA PARA PODER MOSTRAR TODOS LOS DATOS O SOLO LOS DE LA OEE
  public PermisoAdmin: string = 'PermisoAdmin';

  constructor(
    private breadcrumbService: AppBreadcrumbService,
    private service: InformacionService,
    private permission: PermissionGuardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    private storageManager: StorageManagerService
  ) { 
    this.breadcrumbService.setItems([
      { label: "Datos OEE" },
      { label: "Información", routerLink: ["/oee/informacion"] },
    ]);
    this.searchFormGroup = this.formBuilder.group({
      descripcionOee: [],
      /* idPlan: [],
      anho: [],
      fecha: [],
      linkPlan: [],
      cantidadFuncionariosTic: [],
      cantidadFuncionariosAdmin: [],
      presupuestoTicAnual: [], */
    });
  }

  ngOnInit(): void {
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
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

  formatearNumero(numero:number){
    return new Intl.NumberFormat("es-CL").format(numero);
  }

  loadData() {
    this.loading = true;
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
     // VERIFICA LA EXISTENCIA DEL PERMISO PARA VER TODOS LOS DATOS
     const existePermisoAdmin = this.Session.some(objeto => objeto.authority === this.PermisoAdmin);
     existePermisoAdmin ? this.permiso = true : '';

     console.log(this.id)
 
     /* this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value) */
     this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.id, this.permiso)
      .subscribe((response) => {
        this.loading = false;
        if(response) {
          var array = response.data?.lista;

          
          
          
          /* const oeeDescriptions = {};
          
          array.forEach(item => {
            const oeeId = item.oee.id;
            if (!oeeDescriptions[oeeId]) {
              oeeDescriptions[oeeId] = {
                descripcionOee: item.oee.descripcionOee,
                id: item.oee.id,
                descripcionOeeInformacion: ""
              };
            }
          });

          array.forEach(item => {
            const oeeId = item.oee.id;
            if (item.tipoDato.idTipoDato === 1) {
              oeeDescriptions[oeeId].descripcionOeeInformacion = item.descripcionOeeInformacion;
            }
          });

          const resultJSON = JSON.stringify(Object.values(oeeDescriptions), null, 2);

          console.log(resultJSON); */

          
          
          
          
          /* array.forEach(e => {
            e.presupuestoTicAnual = this.formatearNumero(e.presupuestoTicAnual);
          }); */
            this.informacion = response.data?.lista;
            this.totalRecords = this.checkPermission('planes:listar') ? response.data?.totalRecords : 0;
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

  openEdit(row: Informacion) {
    this.showDialog = true;
    this.entity = {...row};
  }

  openDependencia(rowDependencia: Informacion) {
    this.showDependencia = true;
    this.entity = {...rowDependencia};
  }

  openOrganigrama(rowOrganigrama: Informacion){
    this.showOrganigram = true;
    this.entity = {...rowOrganigrama};
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
  }

  changeDialogVisibilityDependencia($event) {
    this.showDependencia = $event;
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

  openLink(url: string) {
    // Abre una nueva pestaña con la URL proporcionada
    window.open(url, '_blank');
  }

}
