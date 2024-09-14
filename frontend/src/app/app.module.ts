import {NgModule, LOCALE_ID} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import {BrowserModule} from '@angular/platform-browser';
import {HashLocationStrategy, LocationStrategy, registerLocaleData} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import localePy from '@angular/common/locales/es-PY';

import {AccordionModule} from 'primeng/accordion';
import {EditorModule} from 'primeng/editor';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {AvatarModule} from 'primeng/avatar';
import {AvatarGroupModule} from 'primeng/avatargroup';
import {BadgeModule} from 'primeng/badge';
import {BreadcrumbModule} from 'primeng/breadcrumb';
import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {CarouselModule} from 'primeng/carousel';
import {CascadeSelectModule} from 'primeng/cascadeselect';
import {ChartModule} from 'primeng/chart';
import {CheckboxModule} from 'primeng/checkbox';
import {ChipModule} from 'primeng/chip';
import {ChipsModule} from 'primeng/chips';
import {CodeHighlighterModule} from 'primeng/codehighlighter';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ConfirmPopupModule} from 'primeng/confirmpopup';
import {ColorPickerModule} from 'primeng/colorpicker';
import {ContextMenuModule} from 'primeng/contextmenu';
import {DataViewModule} from 'primeng/dataview';
import {DialogModule} from 'primeng/dialog';
import {DividerModule} from 'primeng/divider';
import {DropdownModule} from 'primeng/dropdown';
import {FieldsetModule} from 'primeng/fieldset';
import {FileUploadModule} from 'primeng/fileupload';
import {FullCalendarModule} from '@fullcalendar/angular';
import {GalleriaModule} from 'primeng/galleria';
import {ImageModule} from 'primeng/image';
import {InplaceModule} from 'primeng/inplace';
import {InputNumberModule} from 'primeng/inputnumber';
import {InputMaskModule} from 'primeng/inputmask';
import {InputSwitchModule} from 'primeng/inputswitch';
import {InputTextModule} from 'primeng/inputtext';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {KnobModule} from 'primeng/knob';
import {LightboxModule} from 'primeng/lightbox';
import {ListboxModule} from 'primeng/listbox';
import {MegaMenuModule} from 'primeng/megamenu';
import {MenuModule} from 'primeng/menu';
import {MenubarModule} from 'primeng/menubar';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {MultiSelectModule} from 'primeng/multiselect';
import {OrderListModule} from 'primeng/orderlist';
import {OrganizationChartModule} from 'primeng/organizationchart';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {PaginatorModule} from 'primeng/paginator';
import {PanelModule} from 'primeng/panel';
import {PanelMenuModule} from 'primeng/panelmenu';
import {PasswordModule} from 'primeng/password';
import {PickListModule} from 'primeng/picklist';
import {ProgressBarModule} from 'primeng/progressbar';
import {RadioButtonModule} from 'primeng/radiobutton';
import {RatingModule} from 'primeng/rating';
import {RippleModule} from 'primeng/ripple';
import {ScrollPanelModule} from 'primeng/scrollpanel';
import {ScrollTopModule} from 'primeng/scrolltop';
import {SelectButtonModule} from 'primeng/selectbutton';
import {SidebarModule} from 'primeng/sidebar';
import {SkeletonModule} from 'primeng/skeleton';
import {SlideMenuModule} from 'primeng/slidemenu';
import {SliderModule} from 'primeng/slider';
import {SplitButtonModule} from 'primeng/splitbutton';
import {SplitterModule} from 'primeng/splitter';
import {StepsModule} from 'primeng/steps';
import {TabMenuModule} from 'primeng/tabmenu';
import {TableModule} from 'primeng/table';
import {TabViewModule} from 'primeng/tabview';
import {TagModule} from 'primeng/tag';
import {TerminalModule} from 'primeng/terminal';
import {TieredMenuModule} from 'primeng/tieredmenu';
import {TimelineModule} from 'primeng/timeline';
import {ToastModule} from 'primeng/toast';
import {ToggleButtonModule} from 'primeng/togglebutton';
import {ToolbarModule} from 'primeng/toolbar';
import {TooltipModule} from 'primeng/tooltip';
import {TreeModule} from 'primeng/tree';
import {TreeTableModule} from 'primeng/treetable';
import {VirtualScrollerModule} from 'primeng/virtualscroller';

import {AppCodeModule} from './app.code.component';
import {AppComponent} from './app.component';
import {AppMainComponent} from './app.main.component';
import {AppConfigComponent} from './app.config.component';
import {AppMenuComponent} from './app.menu.component';
import {AppMenuitemComponent} from './app.menuitem.component';
import {AppInlineMenuComponent} from './app.inlinemenu.component';
import {AppRightMenuComponent} from './app.rightmenu.component';
import {AppBreadcrumbComponent} from './app.breadcrumb.component';
import {AppTopBarComponent} from './app.topbar.component';
import {AppFooterComponent} from './app.footer.component';
import {DashboardComponent} from './demo/view/dashboard.component';
import {DashboardAnalyticsComponent} from './demo/view/dashboardanalytics.component';
import {FormLayoutDemoComponent} from './demo/view/formlayoutdemo.component';
import {FloatLabelDemoComponent} from './demo/view/floatlabeldemo.component';
import {InvalidStateDemoComponent} from './demo/view/invalidstatedemo.component';
import {InputDemoComponent} from './demo/view/inputdemo.component';
import {ButtonDemoComponent} from './demo/view/buttondemo.component';
import {TableDemoComponent} from './demo/view/tabledemo.component';
import {ListDemoComponent} from './demo/view/listdemo.component';
import {TreeDemoComponent} from './demo/view/treedemo.component';
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
import {AppNotfoundComponent} from './pages/app.notfound.component';
import {AppErrorComponent} from './pages/app.error.component';
import {AppAccessdeniedComponent} from './pages/app.accessdenied.component';
import {AppLoginComponent} from './pages/app.login.component';
import {AppTimelineDemoComponent} from './pages/app.timelinedemo.component';
import {AppWizardComponent} from './pages/app.wizard.component';
import {AppLandingComponent} from './pages/app.landing.component';

import {CountryService} from './demo/service/countryservice';
import {CustomerService} from './demo/service/customerservice';
import {EventService} from './demo/service/eventservice';
import {IconService} from './demo/service/iconservice';
import {NodeService} from './demo/service/nodeservice';
import {PhotoService} from './demo/service/photoservice';
import {ProductService} from './demo/service/productservice';

import {MenuService} from './app.menu.service';
import {AppBreadcrumbService} from './app.breadcrumb.service';
import {AppContactusComponent} from './pages/app.contactus.component';

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

import { NgxDiffModule } from 'ngx-diff';

import { AuthorizationHeaderInterceptorService } from './shared/services/authorization-header-interceptor.service';
import { ResponseInterceptorService } from './shared/services/response-interceptor.service';
import { CookiesStorageService, SessionStorageService } from 'ngx-store';
import { PermissionGuardService } from './shared/services/permission-guard.service';
import { LoginComponent } from './login/login.component';
import { LoginService } from './login/services/login.service';
import { RecuperarClaveComponent } from './recuperar-clave/recuperar-clave.component';
import { RecuperarClaveService } from './recuperar-clave/services/recuperar-clave.service';
import { ModificarClaveService } from './modificar-clave/services/modificar-clave.service';
import { HomeComponent } from './home/home.component';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UsuarioFormComponent } from './usuario/usuario-form/usuario-form.component';
import { CalendarModule } from 'primeng/calendar';
import { RolListComponent } from './rol/rol-list/rol-list.component';
import { PermisoFormComponent } from './permiso/permiso-form/permiso-form.component';
import { RolFormComponent } from './rol/rol-form/rol-form.component';
import { DominioFormComponent } from './administracion-oee/dominio/dominio-form/dominio-form.component';
import { DominioListComponent } from './administracion-oee/dominio/dominio-list/dominio-list.component';
import { InstitucionListComponent } from './institucion/institucion-list/institucion-list.component';
import { InstitucionFormComponent } from './institucion/institucion-form/institucion-form.component';
import { RolPermisoComponent } from './rol/rol-permiso/rol-permiso.component';
import { UsuarioPassComponent } from './usuario/usuario-pass/usuario-pass.component';
import { PermisoListComponent } from './permiso/permiso-list/permiso-list.component';
import { UsuarioListComponent } from './usuario/usuario-list/usuario-list.component';
import { AuditoriaListComponent } from './auditoria/auditoria-list/auditoria-list.component';
import { AuditoriaDetailsComponent } from './auditoria/auditoria-details/auditoria-details.component';
import { OrganigramaTicListComponent } from './administracion-oee/organigrama-tic/organigrama-tic-list/organigrama-tic-list.component';
import { PlanesTicListComponent } from './administracion-oee/planes-tic/planes-tic-list/planes-tic-list.component';
import { RangoIpListComponent } from './administracion-oee/rango-ip/rango-ip-list/rango-ip-list.component';
import { ServiciosListComponent } from './administracion-oee/servicios/servicios-list/servicios-list.component';
import { SistemasListComponent } from './administracion-oee/sistemas/sistemas-list/sistemas-list.component';
import { SistemasFormComponent } from './administracion-oee/sistemas/sistemas-form/sistemas-form.component';
import { OeeListComponent } from './administracion-oee/oee/oee-list/oee-list.component';
import { ModificarClaveComponent } from './modificar-clave/modificar-clave.component';
import { ServicioFormComponent } from './administracion-oee/servicios/servicio-form/servicios-form.component';
import { SubdominioListComponent } from './administracion-oee/dominio/subdominio-list/subdominio-list.component';
import { SubdominioFormComponent } from './administracion-oee/dominio/subdominio-form/subdominio-form.component';
import { PlanesTicFormComponent } from './administracion-oee/planes-tic/planes-tic-form/planes-tic-form.component';
import { RangoIpFormComponent } from './administracion-oee/rango-ip/rango-ip-form/rango-ip-form.component';
import { UsuarioPerfilComponent } from './usuario/usuario-perfil/usuario-perfil.component';
import { OrganigramaTicFormComponent } from './administracion-oee/organigrama-tic/organigrama-tic-form/organigrama-tic-form.component';
import { DatosOeeComponent } from './administracion-oee/datos-oee/datos-oee.component';
import { DatosListComponent } from './administracion-oee/datos-oee/datos-list/datos-list.component';
import { DependenciasListComponent } from './administracion-oee/datos-oee/dependencias-list/dependencias-list.component';
import { DependenciasFormComponent } from './administracion-oee/datos-oee/dependencias-form/dependencias-form.component';
import { DatosFormComponent } from './administracion-oee/datos-oee/datos-form/datos-form.component';
import { VisorOrganigramaFormComponent } from './administracion-oee/datos-oee/visor-organigrama-form/visor-organigrama-form.component';
import { ServiciosInformacionListComponent } from './administracion-oee/servicios/servicios-informacion-list/servicios-informacion-list.component';
import { ServiciosInformacionFormComponent } from './administracion-oee/servicios/servicios-informacion-form/servicios-informacion-form.component';
import { OrganigramaTicVisorComponent } from './administracion-oee/organigrama-tic/organigrama-tic-visor/organigrama-tic-visor.component';
import { InformacionListComponent } from './administracion-oee/informacion/informacion-list/informacion-list.component';
import { InformacionFormComponent } from './administracion-oee/informacion/informacion-form/informacion-form.component';
import { InformacionOrganigramaComponent } from './administracion-oee/informacion/informacion-organigrama/informacion-organigrama.component';
import { InformacionDependenciasFormComponent } from './administracion-oee/informacion/informacion-dependencias-form/informacion-dependencias-form.component';
import { InformacionDependenciasListComponent } from './administracion-oee/informacion/informacion-dependencias-list/informacion-dependencias-list.component';

FullCalendarModule.registerPlugins([
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin
]);
registerLocaleData(localePy, 'es');

@NgModule({
    imports: [
        BrowserModule,
        EditorModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        HttpClientModule,
        BrowserAnimationsModule,
        AccordionModule,
        AutoCompleteModule,
        AvatarModule,
        AvatarGroupModule,
        BadgeModule,
        BreadcrumbModule,
        ButtonModule,
        CalendarModule,
        CardModule,
        CarouselModule,
        CascadeSelectModule,
        ChartModule,
        CheckboxModule,
        ChipModule,
        ChipsModule,
        CodeHighlighterModule,
        ConfirmDialogModule,
        ConfirmPopupModule,
        ColorPickerModule,
        ContextMenuModule,
        DataViewModule,
        DialogModule,
        DividerModule,
        DropdownModule,
        FieldsetModule,
        FileUploadModule,
        FullCalendarModule,
        GalleriaModule,
        ImageModule,
        InplaceModule,
        InputNumberModule,
        InputMaskModule,
        InputSwitchModule,
        InputTextModule,
        InputTextareaModule,
        KnobModule,
        LightboxModule,
        ListboxModule,
        MegaMenuModule,
        MenuModule,
        MenubarModule,
        MessageModule,
        MessagesModule,
        MultiSelectModule,
        OrderListModule,
        OrganizationChartModule,
        OverlayPanelModule,
        PaginatorModule,
        PanelModule,
        PanelMenuModule,
        PasswordModule,
        PickListModule,
        ProgressBarModule,
        RadioButtonModule,
        RatingModule,
        RippleModule,
        ScrollPanelModule,
        ScrollTopModule,
        SelectButtonModule,
        SidebarModule,
        SkeletonModule,
        SlideMenuModule,
        SliderModule,
        SplitButtonModule,
        SplitterModule,
        StepsModule,
        TableModule,
        TabMenuModule,
        TabViewModule,
        TagModule,
        TerminalModule,
        TimelineModule,
        TieredMenuModule,
        ToastModule,
        ToggleButtonModule,
        ToolbarModule,
        TooltipModule,
        TreeModule,
        TreeTableModule,
        VirtualScrollerModule,
        AppCodeModule,
        NgxDiffModule
    ],
    declarations: [
        AppComponent,
        AppMainComponent,
        AppConfigComponent,
        AppMenuComponent,
        AppMenuitemComponent,
        AppInlineMenuComponent,
        AppRightMenuComponent,
        AppBreadcrumbComponent,
        AppTopBarComponent,
        AppFooterComponent,
        DashboardComponent,
        DashboardAnalyticsComponent,
        FormLayoutDemoComponent,
        FloatLabelDemoComponent,
        InvalidStateDemoComponent,
        InputDemoComponent,
        ButtonDemoComponent,
        TableDemoComponent,
        ListDemoComponent,
        TreeDemoComponent,
        PanelsDemoComponent,
        OverlaysDemoComponent,
        MediaDemoComponent,
        MenusDemoComponent,
        MessagesDemoComponent,
        MessagesDemoComponent,
        MiscDemoComponent,
        ChartsDemoComponent,
        EmptyDemoComponent,
        FileDemoComponent,
        DocumentationComponent,
        DisplayComponent,
        ElevationComponent,
        FlexboxComponent,
        GridComponent,
        IconsComponent,
        WidgetsComponent,
        SpacingComponent,
        TypographyComponent,
        TextComponent,
        AppCrudComponent,
        AppCalendarComponent,
        AppLoginComponent,
        AppLandingComponent,
        AppInvoiceComponent,
        AppHelpComponent,
        AppNotfoundComponent,
        AppErrorComponent,
        AppAccessdeniedComponent,
        AppTimelineDemoComponent,
        AppWizardComponent,
        AppContactusComponent,
        LoginComponent,
        RecuperarClaveComponent,
        HomeComponent,
        UsuarioListComponent,
        UsuarioFormComponent,
        UsuarioPassComponent,
        PermisoListComponent,
        PermisoFormComponent,
        RolFormComponent,
        DominioFormComponent,
        ServicioFormComponent,
        RolListComponent,
        DominioListComponent,
        RolPermisoComponent,
        InstitucionListComponent,
        InstitucionFormComponent,
        AuditoriaListComponent,
        AuditoriaDetailsComponent,
        OrganigramaTicListComponent,
        PlanesTicListComponent,
        RangoIpListComponent,
        ServiciosListComponent,
        SistemasListComponent,
        SistemasFormComponent,
        OeeListComponent,
        ModificarClaveComponent,
        SubdominioListComponent,
        SubdominioFormComponent,
        PlanesTicFormComponent,
        RangoIpFormComponent,
        UsuarioPerfilComponent,
        OrganigramaTicFormComponent,
        DependenciasListComponent,
        DatosOeeComponent,
        DatosListComponent,
        DependenciasFormComponent,
        DatosFormComponent,
        VisorOrganigramaFormComponent,
        ServiciosInformacionListComponent,
        ServiciosInformacionFormComponent,
        OrganigramaTicVisorComponent,
        InformacionListComponent,
        InformacionFormComponent,
        InformacionOrganigramaComponent,
        InformacionDependenciasFormComponent,
        InformacionDependenciasListComponent,
    ],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: AuthorizationHeaderInterceptorService, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: ResponseInterceptorService, multi: true },
        { provide: LocationStrategy, useClass: HashLocationStrategy },
        { provide: LOCALE_ID, useValue: 'es-PY' },
        CountryService,
        CustomerService,
        EventService,
        IconService,
        NodeService,
        PhotoService,
        ProductService,
        MenuService,
        AppBreadcrumbService,
        CookiesStorageService,
        SessionStorageService,
        PermissionGuardService,
        LoginService,
        RecuperarClaveService,
        ModificarClaveService,
        ConfirmationService,
        MessageService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
