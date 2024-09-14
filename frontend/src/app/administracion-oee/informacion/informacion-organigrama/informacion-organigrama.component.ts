import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { InformacionService } from '../services/informacion.service';
/* import { DependenciasService } from '../services/dependencias.service'; */
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';
import { Informacion } from '../models/informacion.model';

@Component({
  selector: 'app-informacion-organigrama',
  templateUrl: './informacion-organigrama.component.html',
  styleUrls: ['./informacion-organigrama.component.scss']
})
export class InformacionOrganigramaComponent implements OnInit {

  @Input() rowOrganigrama: Informacion;
  @Input() visibleOrganigram: boolean;
  @Output() setVisibleO: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  selectedNodes!: TreeNode[];

  public id: any;
  public oee: any;
  public descripcionOee: any;
  data: TreeNode[] = [];

  constructor(
    private informacionService: InformacionService,
    private storageManager: StorageManagerService
  ) { }

  ngOnChanges(): void {
    this.initComponent();
  }

  ngOnInit(): void {
    this.oee = this.storageManager.getCurrenSession().usuario.oee;
    this.descripcionOee = this.storageManager.getCurrenSession().usuario.oee.descripcionOee;
    /* this.generateOrganigrama(); */
  }

  /* ngAfterViewInit(): void{
    console.log("holis show")
  } */

  initComponent(): void {
    /* console.log('se inicio el componente...')
    console.log(this.rowOrganigrama); */
    this.data = [];
    if (this.rowOrganigrama != null) {
      /* let idOrganigrama = this.rowOrganigrama.id; */
      this.generateOrganigrama(this.rowOrganigrama);
    }

  }

  // FUNCION QUE GENERA LA ESTRUCTURA DEL ORGANIGRAMA
  crearOrganigrama(data, idPadre = null, labelKey = "label", childrenKey = "children", expandedKey = "expanded") {
    const result = [];
    var finalResult = [];
    for (const item of data) {
      if (item.idDependenciaPadre === idPadre) {
        const nodo = {
          [labelKey]: item.descripcionDependencia,
          [expandedKey]: true
        };
        const hijos = this.crearOrganigrama(data, item.idDependencia, labelKey, childrenKey, expandedKey);
        if (hijos.length > 0) {
          nodo[childrenKey] = hijos;
        }
        result.push(nodo);
      }
    }
    return result;
  }

  generateOrganigrama(rowOrganigrama: any) {
    /* generateOrganigrama() { */
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.informacionService.getShowDependencia(rowOrganigrama.id).subscribe((response) => {
      /* this.informacionService.getShowDependencia(this.id).subscribe((response) => { */
      if (response.data) {
        /* if (response) { */
        let data = response.data;
        let organigramaEstructure = [
          {
            label: this.descripcionOee,
            expanded: true,
            children: this.crearOrganigrama(data)
          }
        ];
        this.data = organigramaEstructure;
      } else {
        let organigramaEstructure = [
          {
            label: "Sin datos para mostrar.",
            expanded: true,
          }
        ];
        this.data = organigramaEstructure;
      }
    });
  }

  close() {
    this.setVisibleO.emit(false);
    this.data = [];
  }

}
