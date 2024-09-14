import { Component, OnInit, Input, EventEmitter, Output, OnChanges, ViewChild } from '@angular/core';
import { Table } from 'primeng/table';
import { delay } from 'rxjs/operators';
import { PermissionGuardService } from '../../../shared/services/permission-guard.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ServicioInformacionService } from '../services/servicio-informacion.service';
import { TipoDatoService } from '../../tipo-dato/services/tipo-dato.service';
import { Servicio } from '../models/servicio.model';
import { ServicioInformacion } from '../models/servicio-informacion.model';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { MessageResponse } from '../../../shared/models/message-response.model';
import { SelectItem } from '../../tipo-dato/models/tipo-dato.model';

@Component({
  selector: 'app-servicios-informacion-list',
  templateUrl: './servicios-informacion-list.component.html',
  styleUrls: ['./servicios-informacion-list.component.scss']
})
export class ServiciosInformacionListComponent implements OnInit {

  @ViewChild("modalTable") modalTable: Table;
  @Input() row: Servicio;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  public searchFormGroupInformacion: FormGroup;
  public filter: string;
  public pageSize: number = 7;
  public start: number = 0;
  public totalRecords: number = 0;
  public sortAsc: boolean = true;
  public sortField: string;
  public loading: boolean = true;
  public servicioInformacion: ServicioInformacion[];
  public entity: ServicioInformacion;
  public servicio: Servicio;
  public showSubdominiosList: boolean;
  public showDialog: boolean;
  public tipoDato: SelectItem[];

  constructor(
    private service: ServicioInformacionService,
    private tipoDatoService: TipoDatoService,
    private permission: PermissionGuardService,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
    this.searchFormGroupInformacion = this.formBuilder.group({
      idServicioInformacion: [],
      descripcionServicioInformacion: [],
      descripcionTipoDato: [],
    });
   }

  ngOnInit(): void {
    this.loadTipoDato()
    this.initComponent();
    this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
  }

  ngOnDestroy(): void {
    this.service.disconnet();
  }

  ngOnChanges(): void {
    // console.log(this.row);
    this.initComponent();
  }

  initComponent(): void {
    this.servicio = this.row;
    if (this.row && this.row.idServicio) {
      this.loadData();
    }
  }

  loadInformacion($event: any) {
    this.filter = $event?.globalFilter ? $event.globalFilter : null;
    this.start = $event?.first;
    this.pageSize = $event?.rows ? $event.rows : null;
    this.sortField = $event?.sortField;
    this.sortAsc = $event?.sortOrder == 1 ? true : false;
    if (this.row && this.row.idServicio) {
      this.loadData();
    }
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

  loadData() {
    this.loading = true;
    this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroupInformacion.value,this.row.idServicio).subscribe((response) => {
      this.loading = false;
      if (response) {
        this.servicioInformacion = response.data?.lista;
        this.totalRecords = this.checkPermission('servicioOee:servicioInformacion:obtener') ? response.data?.totalRecords : 0;
      }
    }, error => {
      this.loading = false;
      console.log(error);
    });
  }

  filterModal(event: Event) {
    event.preventDefault();
    const valueToFilter = this.searchFormGroupInformacion.value;
    this.modalTable.filter(valueToFilter, '', 'contains');
    this.loadData();
  }

  loadTipoDato() {
    this.tipoDatoService.getTipoDato().subscribe((response) => {
      if (response) {
        this.tipoDato = response.data;
      }
    });
  }

  close() {
    this.searchFormGroupInformacion.reset();
    this.setVisible.emit(false);
  }

  openNew(row: ServicioInformacion) {
    this.showDialog = true;
    this.entity = { ...row };
  }
  openEdit(row: ServicioInformacion) {
    this.showDialog = true;
    this.entity = { ...row };
  }

  changeDialogVisibility($event) {
    this.showDialog = $event;
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

  updateStatus(row: ServicioInformacion) {
    this.confirmationService.confirm({
      message: `Está seguro que desea ${row.estadoServicioInformacion === 'I' ? 'habilitar' : 'inactivar'} <b>${row.descripcionServicioInformacion}</b>?`,
      header: '¡Atención!',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.updateStatus(row.idServicioInformacion).subscribe(response => {
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

  delete(row: ServicioInformacion) {
    this.confirmationService.confirm({
      message: `Está seguro que desea eliminar <b>${row.descripcionServicioInformacion}</b>?`,
      header: '¡Atención!',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.service.delete(row.idServicioInformacion).subscribe(response => {
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

}
