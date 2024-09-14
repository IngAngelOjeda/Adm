import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {DashboardAnalyticsComponent} from './demo/view/dashboardanalytics.component';
import {FormLayoutDemoComponent} from './demo/view/formlayoutdemo.component';
import {InvalidStateDemoComponent} from './demo/view/invalidstatedemo.component';
import {PanelsDemoComponent} from './demo/view/panelsdemo.component';
import {OverlaysDemoComponent} from './demo/view/overlaysdemo.component';
import {MediaDemoComponent} from './demo/view/mediademo.component';
import {MenusDemoComponent} from './demo/view/menusdemo.component';
import {MessagesDemoComponent} from './demo/view/messagesdemo.component';
import {MiscDemoComponent} from './demo/view/miscdemo.component';
import {EmptyDemoComponent} from './demo/view/emptydemo.component';
import {ChartsDemoComponent} from './demo/view/chartsdemo.component';
import {FileDemoComponent} from './demo/view/filedemo.component';
import {DocumentationComponent} from './demo/view/documentation.component';
import {AppMainComponent} from './app.main.component';
import {AppNotfoundComponent} from './pages/app.notfound.component';
import {AppContactusComponent} from './pages/app.contactus.component';
import {AppErrorComponent} from './pages/app.error.component';
import {AppAccessdeniedComponent} from './pages/app.accessdenied.component';
import {AppLandingComponent} from './pages/app.landing.component';
import {InputDemoComponent} from './demo/view/inputdemo.component';
import {FloatLabelDemoComponent} from './demo/view/floatlabeldemo.component';
import {ButtonDemoComponent} from './demo/view/buttondemo.component';
import {TableDemoComponent} from './demo/view/tabledemo.component';
import {ListDemoComponent} from './demo/view/listdemo.component';
import {AppTimelineDemoComponent} from './pages/app.timelinedemo.component';
import {TreeDemoComponent} from './demo/view/treedemo.component';
import {DisplayComponent} from './utilities/display.component';
import {ElevationComponent} from './utilities/elevation.component';
import {FlexboxComponent} from './utilities/flexbox.component';
import {GridComponent} from './utilities/grid.component';
import {IconsComponent} from './utilities/icons.component';
import {WidgetsComponent} from './utilities/widgets.component';
import {SpacingComponent} from './utilities/spacing.component';
import {TypographyComponent} from './utilities/typography.component';
import {TextComponent} from './utilities/text.component';
import {AppCrudComponent} from './pages/app.crud.component';
import {AppCalendarComponent} from './pages/app.calendar.component';
import {AppInvoiceComponent} from './pages/app.invoice.component';
import {AppHelpComponent} from './pages/app.help.component';
import {AppWizardComponent} from './pages/app.wizard.component';

import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RecuperarClaveComponent } from './recuperar-clave/recuperar-clave.component';
import { ModificarClaveComponent } from './modificar-clave/modificar-clave.component';
import { UsuarioListComponent } from './usuario/usuario-list/usuario-list.component';
import { RolListComponent } from './rol/rol-list/rol-list.component';
import { PermisoListComponent } from './permiso/permiso-list/permiso-list.component';
import { InstitucionListComponent } from './institucion/institucion-list/institucion-list.component';
import { AuditoriaListComponent } from './auditoria/auditoria-list/auditoria-list.component';
// import { DominiosListComponent } from './administracion-oee/dominios/dominios-list/dominios-list.component';
import { DominioListComponent } from './administracion-oee/dominio/dominio-list/dominio-list.component';
import { SistemasListComponent } from './administracion-oee/sistemas/sistemas-list/sistemas-list.component';
import { ServiciosListComponent } from './administracion-oee/servicios/servicios-list/servicios-list.component';
import { RangoIpListComponent } from './administracion-oee/rango-ip/rango-ip-list/rango-ip-list.component';
import { OrganigramaTicListComponent } from './administracion-oee/organigrama-tic/organigrama-tic-list/organigrama-tic-list.component';
import { PlanesTicListComponent } from './administracion-oee/planes-tic/planes-tic-list/planes-tic-list.component';
import { OeeListComponent } from './administracion-oee/oee/oee-list/oee-list.component';
//import { DependenciasListComponent } from './administracion-oee/dependencias/dependencias-list/dependencias-list.component';
import { DatosOeeComponent } from './administracion-oee/datos-oee/datos-oee.component';
import { InformacionListComponent } from './administracion-oee/informacion/informacion-list/informacion-list.component';

@NgModule({
    imports: [
    RouterModule.forRoot([
            {
                path: '', component: AppMainComponent,
                children: [
                    {path: '', redirectTo: '/login', pathMatch: 'full'},
                    // menú: Administración
                    {path: 'home', component: HomeComponent},
                    {path: 'administracion/auditoria', component: AuditoriaListComponent},
                    {path: 'administracion/usuario', component: UsuarioListComponent},
                    {path: 'administracion/rol', component: RolListComponent},
                    {path: 'administracion/permiso', component: PermisoListComponent},
                    {path: 'administracion/institucion', component: InstitucionListComponent},
                    // menú: OEE
                    {path: 'oee/list', component: OeeListComponent},
                    {path: 'oee/servicios', component: ServiciosListComponent},
                    {path: 'oee/sistemas', component: SistemasListComponent},
                    {path: 'oee/dominio', component: DominioListComponent},
                    // {path: 'administracion/dominio', component: DominioListComponent},
                    {path: 'oee/rango-ip', component: RangoIpListComponent},
                    {path: 'oee/organigrama-tic', component: OrganigramaTicListComponent},
                    {path: 'oee/planes-tic', component: PlanesTicListComponent},
                    //{path: 'oee/dependencia', component: DependenciasListComponent},
                    {path: 'oee/datos-oee', component: DatosOeeComponent},
                    {path: 'oee/informacion', component: InformacionListComponent},

                    {path: 'favorites/dashboardanalytics', component: DashboardAnalyticsComponent},
                    {path: 'uikit/formlayout', component: FormLayoutDemoComponent},
                    {path: 'uikit/floatlabel', component: FloatLabelDemoComponent},
                    {path: 'uikit/invalidstate', component: InvalidStateDemoComponent},
                    {path: 'uikit/input', component: InputDemoComponent},
                    {path: 'uikit/button', component: ButtonDemoComponent},
                    {path: 'uikit/table', component: TableDemoComponent},
                    {path: 'uikit/list', component: ListDemoComponent},
                    {path: 'uikit/tree', component: TreeDemoComponent},
                    {path: 'uikit/panel', component: PanelsDemoComponent},
                    {path: 'uikit/overlay', component: OverlaysDemoComponent},
                    {path: 'uikit/menu', component: MenusDemoComponent},
                    {path: 'uikit/media', component: MediaDemoComponent},
                    {path: 'uikit/message', component: MessagesDemoComponent},
                    {path: 'uikit/misc', component: MiscDemoComponent},
                    {path: 'uikit/charts', component: ChartsDemoComponent},
                    {path: 'uikit/file', component: FileDemoComponent},
                    {path: 'utilities/display', component: DisplayComponent},
                    {path: 'utilities/elevation', component: ElevationComponent},
                    {path: 'utilities/flexbox', component: FlexboxComponent},
                    {path: 'utilities/grid', component: GridComponent},
                    {path: 'utilities/icons', component: IconsComponent},
                    {path: 'utilities/widgets', component: WidgetsComponent},
                    {path: 'utilities/spacing', component: SpacingComponent},
                    {path: 'utilities/typography', component: TypographyComponent},
                    {path: 'utilities/text', component: TextComponent},
                    {path: 'pages/crud', component: AppCrudComponent},
                    {path: 'pages/calendar', component: AppCalendarComponent},
                    {path: 'pages/timeline', component: AppTimelineDemoComponent},
                    {path: 'pages/invoice', component: AppInvoiceComponent},
                    {path: 'pages/help', component: AppHelpComponent},
                    {path: 'pages/empty', component: EmptyDemoComponent},
                    {path: 'documentation', component: DocumentationComponent}
                ]
            },
            {path: 'login', component: LoginComponent},
            {path: 'recuperar-clave', component: RecuperarClaveComponent},
            {path: 'modificar-clave/:token', component: ModificarClaveComponent},

            {path: 'error', component: AppErrorComponent},
            {path: 'access', component: AppAccessdeniedComponent},
            {path: 'notfound', component: AppNotfoundComponent},
            {path: 'contactus', component: AppContactusComponent},
            {path: 'landing', component: AppLandingComponent},
            {path: 'pages/wizard', component: AppWizardComponent},
            {path: '**', redirectTo: '/notfound'},
        ], { useHash: false, scrollPositionRestoration: 'enabled'})
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
