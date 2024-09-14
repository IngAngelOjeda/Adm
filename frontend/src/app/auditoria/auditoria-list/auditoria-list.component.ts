import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { FormBuilder, FormGroup } from '@angular/forms';
import { delay } from 'rxjs/operators';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';
import { Auditoria } from '../models/auditoria.models';
import { AuditoriaService } from '../services/auditoria.service';
import { InstitucionService } from '../../institucion/services/institucion.service';
import { UsuarioService } from '../../usuario/services/usuario.service';
import { SelectItem } from '../../shared/models/select.model';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-auditoria-list',
  templateUrl: './auditoria-list.component.html',
  styleUrls: ['./auditoria-list.component.scss']
})
export class AuditoriaListComponent implements OnInit {

	items: MenuItem[];
	public formFiltrar: FormGroup;
	public searchFormGroup: FormGroup;
    public pageSize: number = 10;
    public start: number = 0;
    public filter: string;
    public totalRecords: number = 0;
    public sortAsc: boolean = true;
    public sortField: string;

    public loading: boolean = true;
	public auditoria: Auditoria[];
	public instituciones: SelectItem[];
	public usuarios: SelectItem[];

	public showDialog: boolean;
	public entity: Auditoria;

	public list: any[];
  	public listAll: any[];

	constructor(
		private breadcrumbService: AppBreadcrumbService,
		private permission: PermissionGuardService,
		private formBuilder: FormBuilder,
		private institucionService: InstitucionService,
		private usuarioService: UsuarioService,
		private service: AuditoriaService,
	) { 
		this.breadcrumbService.setItems([
			{ label: "Administración" },
			{ label: "Auditoria", routerLink: ["/administracion/auditoria"] },
		]);

		this.searchFormGroup = this.formBuilder.group({
			idAuditoria: [],
			metodo: [],
			modulo: [],
			accion: [],
			nombreUsuario: [],
			nombreTabla: [],
			fechaHora: [],
			descripcionOee: [],
		});

		this.formFiltrar = this.formBuilder.group({
			rangoFecha: [''],
			idOee: [''],
			idUsuario: [''],
		});
		
	}

	ngOnInit(): void {
		this.loadInstituciones();
		this.loadUsuarios();
		this.service.connect().pipe(delay(0)).subscribe((l) => { this.loading = l; });
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
			//{separator: true},
			//{label: 'Setup', icon: 'pi pi-cog', routerLink: ['/#']}
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

	filterToolbar() {
		this.loadData();
		// this.formFiltrar.reset();
	}
  
	loadData() {
		this.loading = true;
		this.service.getAll(this.filter, this.pageSize, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.formFiltrar.value)
			.subscribe((response) => {
			this.loading = false;
			if(response) {
				this.auditoria = response.data?.lista;
				this.totalRecords = response.data?.totalRecords;
			}
		}, error => {
			this.loading = false;
			console.log(error);
		});
	}
	loadInstituciones() {
		this.institucionService.getInstituciones().subscribe((response) => {
			if(response) {
				this.instituciones = response.data;
			}
		});
	}
	loadUsuarios() {
		this.usuarioService.getUsuarios().subscribe((response) => {
			if(response) {
				this.usuarios = response.data;
			}
		});
	}
	openDetail (row: Auditoria) {
		// console.log(row);
		this.showDialog = true;
		this.entity = {...row};
	}

	changeDialogVisibility($event) {
		this.showDialog = $event;
	}

	limpiarform() {
		this.formFiltrar.reset();
	}

	exportData(band) {
		this.service.getAll(this.filter, -1, this.start, this.sortField, this.sortAsc, this.searchFormGroup.value, this.formFiltrar.value).subscribe((response) => {
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
				  this.exportCsv('Auditoria_export_' + new Date().getTime() + '.csv', body);
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
		this.saveAsExcelFile(excelBuffer, "Auditoria");
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
		FileSaver.saveAs(blob, "Auditoria" + '_export_' + new Date().getTime() + EXTENSION);
	  }
	
	  getColumnsExportExcel(data) {
		let body = [];
		for (let row of data) {
		  body.push({
			'#': row?.idAuditoria,
			'Fecha': row?.fechaHora,
			'Institucion': row?.institucion,
			'Nombre y Apellido': row?.usuario,
			'Usuario': row?.nombreUsuario,
			'Tabla': row?.nombreTabla,
			'Modulo': row?.modulo,
			'Método': row?.metodo,
			'Accion': row?.accion,
			'Valor': row?.valor,
		  });
		}
		// console.table(body);
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
