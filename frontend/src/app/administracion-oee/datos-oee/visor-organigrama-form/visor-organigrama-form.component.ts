import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { DependenciasService } from '../services/dependencias.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-visor-organigrama-form',
  templateUrl: './visor-organigrama-form.component.html',
  styleUrls: ['./visor-organigrama-form.component.scss']
})
export class VisorOrganigramaFormComponent implements OnInit {

  /* visible = true; */

  @Input() visibleOrganigram: boolean;
  @Output() setVisibleO: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  selectedNodes!: TreeNode[];

  /* data: TreeNode[] = [
    {
      "label": "MITIC",
      "expanded": true,
      "children": [
        
        {
          "label": "Dependencia 1",
          "expanded": true,
          "children": [
              {
                  "label": "Dependencia 3",
                  "expanded": true,
                  "children": [
                      {
                          "label": "Dependencia 4",
                          "expanded": true,
                          "children": null
                      }
                  ]
              },
              {
                  "label": "Dependencia 2",
                  "expanded": true,
                  "children": null
              }
          ]
      }
      ]
  }
] */

  public id: any;
  public oee: any;
  public descripcionOee: any;
  data: TreeNode[] = [];

  constructor(
    private dependenciasService: DependenciasService,
    private storageManager: StorageManagerService
  ) { }

  ngOnInit(): void {
    /* this.id = this.storageManager.getCurrenSession().usuario.oee.id; */
    this.oee = this.storageManager.getCurrenSession().usuario.oee;
    this.descripcionOee = this.storageManager.getCurrenSession().usuario.oee.descripcionOee;
    this.generateOrganigrama();
  }

  ngOnChanges(): void {
    this.generateOrganigrama();
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

  generateOrganigrama() {
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.dependenciasService.getShowDependencia(this.id).subscribe((response) => {
      if (response) {
        let data = response.data;
        let organigramaEstructure = [
          {
            label: this.descripcionOee,
            expanded: true,
            children: this.crearOrganigrama(data)
          }
        ];
        /* let organigramaEstructure = this.crearOrganigrama(data); */
        this.data = organigramaEstructure;
      }
    });
  }

  close() {
    this.setVisibleO.emit(false);
    this.data = [];
  }

}
