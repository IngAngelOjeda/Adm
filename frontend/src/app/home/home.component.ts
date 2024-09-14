import { Component, OnInit } from '@angular/core';
import { Oee } from '../institucion/models/institucion.model';
import { PermissionGuardService } from '../shared/services/permission-guard.service';
import { StorageManagerService } from '../shared/services/storage-manager.service';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
    // Variables user
    public name: string;
    public username: string;
    public institucion: Oee;
    public estado: boolean = false;

    constructor(
        private permission: PermissionGuardService,
        private storageManager: StorageManagerService
    ) { }

    ngOnInit(): void {
        this.name = this.storageManager.getCurrenSession().usuario.nombre.concat(' ',this.storageManager.getCurrenSession().usuario.apellido);
        this.username = this.storageManager.getCurrenSession().usuario.username;
        this.institucion = this.storageManager.getCurrenSession().usuario.oee;
        this.estado = this.storageManager.getCurrenSession().usuario.estado;
    }

    checkPermission(nombre: string): boolean {
        return this.permission.hasPermission(nombre);
    }

}
