import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Auditoria } from '../models/auditoria.models';

@Component({
	selector: 'app-auditoria-details',
	templateUrl: './auditoria-details.component.html',
	styleUrls: ['./auditoria-details.component.scss']
})
export class AuditoriaDetailsComponent implements OnInit {

	formattedJsonNew: string;
	formattedJsonOld: string;

	@Input() row: Auditoria;
	@Input() visible: boolean;

	@Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);

	constructor() { }

	ngOnChanges(): void {
		this.initComponent();
	}

	ngOnInit(): void {
		this.initComponent();
	}

	initComponent(): void {
		if (this.row != null) {
			if (this.row?.accion == "MODIFICAR") {
				try {
					const parsedRow = JSON.parse(this.row?.valor);
					console.log(parsedRow?.nuevoValor);
					this.formattedJsonNew = this.formatJson(parsedRow?.nuevoValor);
					this.formattedJsonOld = this.formatJson(parsedRow?.valorAnterior);
				} catch (error) {
					console.error('Error al analizar JSON extractValues:', error);
					// Manejar el error según tus necesidades
				}
			} else if( this.row?.accion == "BORRAR"){
				try {
					const parsedRow = JSON.parse(this.row?.valor);
					this.formattedJsonNew = "";
					this.formattedJsonOld = this.formatJson(parsedRow?.valorAnterior);
				} catch (error) {
					console.error('Error al analizar JSON extractValues:', error);
				}
			} else {
				this.formattedJsonNew = this.formatJson(this.row?.valor);
				this.formattedJsonOld = "";
			}
		}
	}

	close() { this.setVisible.emit(false); }

	formatJson(jsonString: string | object): string {
		try {
			const jsonObj = typeof jsonString === 'string' ? JSON.parse(jsonString) : jsonString;
			return JSON.stringify(jsonObj, null, 2);
		} catch (error) {
			console.error('Error al analizar JSON formatJson:', error);
			return '';
		}
	}

	selectedLineChange($event: any) {
		const selectedLineNumber = $event.lineNumber;
		const selectedLineContent = $event.lineContent;
	  
		console.log(`Línea ${selectedLineNumber} seleccionada: ${selectedLineContent}`);
	  }
}