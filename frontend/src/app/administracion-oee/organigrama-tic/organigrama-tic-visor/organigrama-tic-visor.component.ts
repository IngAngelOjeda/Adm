import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ElementRef } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { OrganigramaTicService } from '../services/organigrama-tic.service';
import { StorageManagerService } from 'src/app/shared/services/storage-manager.service';

@Component({
  selector: 'app-organigrama-tic-visor',
  templateUrl: './organigrama-tic-visor.component.html',
  styleUrls: ['./organigrama-tic-visor.component.scss']
})
export class OrganigramaTicVisorComponent implements OnInit {

  @Input() visibleOrganigram: boolean;
  @Output() setVisibleO: EventEmitter<boolean> = new EventEmitter<boolean>(true);


  isClosed = true;

  selectedNodes!: TreeNode[];

  public id: any;
  public oee: any;
  public descripcionOee: any;
  data: TreeNode[] = [];

  constructor(
    private service: OrganigramaTicService,
    private storageManager: StorageManagerService
  ) { }

  ngOnInit(): void {
    this.oee = this.storageManager.getCurrenSession().usuario.oee;
    this.descripcionOee = this.storageManager.getCurrenSession().usuario.oee.descripcionOee;
    this.generateOrganigrama();
  }

  ngOnChanges(): void {
    this.generateOrganigrama();
  }

/* NUEVA FUNCION */

construirEstructura1(jsonOriginal, jsonFuncionarios, idDependenciaPadre = null) {
  const result = [];

  jsonOriginal.forEach(item => {
    if (item.idDependenciaPadre === idDependenciaPadre) {
      const funcionarios = jsonFuncionarios.filter(funcionario => funcionario.idDependencia === item.idDependencia);

      
      const nodo = {
        expanded: true,
        type: 'person',
        data: {
          name: item.descripcionDependencia,
          id: item.idDependencia,
          funcionarios: funcionarios 
        },
        children: this.construirEstructura1(jsonOriginal, jsonFuncionarios, item.idDependencia)
      };

      result.push(nodo);
    }
  });

  return result;
}

  generateOrganigrama() {
    this.id = this.storageManager.getCurrenSession().usuario.oee.id;
    this.service.ShowDependencia(this.id).subscribe((response) => {
      if (response) {

        let dependencias = response.data;
        this.service.ShowOrganigramaDependencia(this.id).subscribe((response) => {

          if(response){
            
            let funcionarios = response.data;
            


            let organigramaEstructure = [
              {
                  label: this.descripcionOee,
                  expanded: true,
                  children: this.construirEstructura1(dependencias, funcionarios)
              }
            ];
    
            this.data = organigramaEstructure;
          }
        });
      }
    });
  }

toggleFuncionarios() {
  this.isClosed = !this.isClosed;
}


close() {
  this.setVisibleO.emit(false);
  this.data = [];
}

}
