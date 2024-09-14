import { Component, OnInit } from '@angular/core';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';

@Component({
  selector: 'app-datos-oee',
  templateUrl: './datos-oee.component.html',
  styleUrls: ['./datos-oee.component.scss']
})
export class DatosOeeComponent implements OnInit {

  constructor(
    private breadcrumbService: AppBreadcrumbService,
    private permission: PermissionGuardService,
  ) { 
    this.breadcrumbService.setItems([
      { label: "Administración" },
      { label: "Datos de OEE", routerLink: ["/oee/datos-oee"] },
    ]);
  }

  ngOnInit(): void {
  }

  checkPermission(nombre: string): boolean {
    return this.permission.hasPermission(nombre);
  }

}
