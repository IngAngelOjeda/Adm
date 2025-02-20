import { Component, ElementRef, ViewChild } from '@angular/core';
import { trigger, style, transition, animate, AnimationEvent } from '@angular/animations';
import { MegaMenuItem } from 'primeng/api';
import { AppComponent } from './app.component';
import { AppMainComponent } from './app.main.component';
import { LoginService } from './login/services/login.service';
import { Router } from '@angular/router';
import { StorageManagerService } from './shared/services/storage-manager.service';
import { UserSession } from './shared/models/usersession.model';

@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html',
    animations: [
        trigger('topbarActionPanelAnimation', [
            transition(':enter', [
                style({opacity: 0, transform: 'scaleY(0.8)'}),
                animate('.12s cubic-bezier(0, 0, 0.2, 1)', style({ opacity: 1, transform: '*' })),
              ]),
              transition(':leave', [
                animate('.1s linear', style({ opacity: 0 }))
              ])
        ])
    ]
})
export class AppTopBarComponent {

    currentSession: UserSession;
    activeItem: number;

    constructor(
        public appMain: AppMainComponent,
        public app: AppComponent,
        private loginService: LoginService,
        private router: Router,
        private storageManager: StorageManagerService,
    ) {
        this.currentSession = this.storageManager.getCurrenSession();
    }


    model: MegaMenuItem[] = [
        {
            label: 'UI KIT',
            items: [
                [
                    {
                        label: 'UI KIT 1',
                        items: [
                            { label: 'Form Layout', icon: 'pi pi-fw pi-id-card', routerLink: ['/uikit/formlayout'] },
                            { label: 'Input', icon: 'pi pi-fw pi-check-square', routerLink: ['/uikit/input'] },
                            { label: 'Float Label', icon: 'pi pi-fw pi-bookmark', routerLink: ['/uikit/floatlabel'] },
                            { label: 'Button', icon: 'pi pi-fw pi-mobile', routerLink: ['/uikit/button'] },
                            { label: 'File', icon: 'pi pi-fw pi-file', routerLink: ['/uikit/file'] }
                        ]
                    }
                ],
                [
                    {
                        label: 'UI KIT 2',
                        items: [
                            { label: 'Table', icon: 'pi pi-fw pi-table', routerLink: ['/uikit/table'] },
                            { label: 'List', icon: 'pi pi-fw pi-list', routerLink: ['/uikit/list'] },
                            { label: 'Tree', icon: 'pi pi-fw pi-share-alt', routerLink: ['/uikit/tree'] },
                            { label: 'Panel', icon: 'pi pi-fw pi-tablet', routerLink: ['/uikit/panel'] },
                            { label: 'Chart', icon: 'pi pi-fw pi-chart-bar', routerLink: ['/uikit/charts'] }
                        ]
                    }
                ],
                [
                    {
                        label: 'UI KIT 3',
                        items: [
                            { label: 'Overlay', icon: 'pi pi-fw pi-clone', routerLink: ['/uikit/overlay'] },
                            { label: 'Media', icon: 'pi pi-fw pi-image', routerLink: ['/uikit/media'] },
                            { label: 'Menu', icon: 'pi pi-fw pi-bars', routerLink: ['/uikit/menu'] },
                            { label: 'Message', icon: 'pi pi-fw pi-comment', routerLink: ['/uikit/message'] },
                            { label: 'Misc', icon: 'pi pi-fw pi-circle-off', routerLink: ['/uikit/misc'] }
                        ]
                    }
                ]
            ]
        },
        {
            label: 'UTILITIES',
            items: [
                [
                    {
                        label: 'UTILITIES 1',
                        items: [
                            { label: 'Display', icon: 'pi pi-fw pi-desktop', routerLink: ['utilities/display'] },
                            { label: 'Elevation', icon: 'pi pi-fw pi-external-link', routerLink: ['utilities/elevation'] }
                        ]
                    },
                    {
                        label: 'UTILITIES 2',
                        items: [
                            { label: 'FlexBox', icon: 'pi pi-fw pi-directions', routerLink: ['utilities/flexbox'] }
                        ]
                    }
                ],
                [
                    {
                        label: 'UTILITIES 3',
                        items: [
                            { label: 'Icons', icon: 'pi pi-fw pi-search', routerLink: ['utilities/icons'] }
                        ]
                    },
                    {
                        label: 'UTILITIES 4',
                        items: [
                            { label: 'Text', icon: 'pi pi-fw pi-pencil', routerLink: ['utilities/text'] },
                            { label: 'Widgets', icon: 'pi pi-fw pi-star-o', routerLink: ['utilities/widgets'] }
                        ]
                    }
                ],
                [
                    {
                        label: 'UTILITIES 5',
                        items: [
                            { label: 'Grid System', icon: 'pi pi-fw pi-th-large', routerLink: ['utilities/grid'] },
                            { label: 'Spacing', icon: 'pi pi-fw pi-arrow-right', routerLink: ['utilities/spacing'] },
                            { label: 'Typography', icon: 'pi pi-fw pi-align-center', routerLink: ['utilities/typography'] }
                        ]
                    }
                ],
            ]
        }
    ];

    @ViewChild('searchInput') searchInputViewChild: ElementRef;

    onSearchAnimationEnd(event: AnimationEvent) {
        switch(event.toState) {
            case 'visible':
                this.searchInputViewChild.nativeElement.focus();
            break;
        }
    }

    logout(event: any) {
        console.log("logout");
        event.preventDefault();
        this.loginService.doLogout();
        this.router.navigate(['/login']);
    }
}
