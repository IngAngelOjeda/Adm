import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { DatePipe } from '@angular/common';
import { Oee } from '../models/institucion.model';
import { InstitucionService } from '../services/institucion.service';
import { PermissionGuardService } from 'src/app/shared/services/permission-guard.service';

@Component({
  selector: 'app-institucion-form',
  templateUrl: './institucion-form.component.html',
  styleUrls: ['./institucion-form.component.scss']
})
export class InstitucionFormComponent implements OnInit, OnChanges {

    @Input() row: Oee;
    @Input() visible: boolean;
    @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
    @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

    public form: FormGroup;
    public submitted = false;
    public datePipe: DatePipe;

    constructor(
        private service: InstitucionService,
        private formBuilder: FormBuilder,
        private permission: PermissionGuardService
    ) { }

    ngOnChanges(): void {
        this.initComponent();
    }

    ngOnInit(): void {
        this.initComponent();
    }

    checkPermission(nombre: string): boolean {
        return this.permission.hasPermission(nombre);
    }

    initComponent(): void {
        this.datePipe = new DatePipe('es-PY');
        this.submitted = false;

        this.form = this.formBuilder.group({
            id: [''],
            nombre: ['', [ Validators.required ] ],
            abreviatura: ['', [ Validators.required ] ]
            
        });
        
        this.formEdit();
    }
    

    formEdit() {        
        if(this.row != null) {
            this.form.controls['id'].setValue(this.row?.id);
            this.form.controls['nombre'].setValue(this.row?.descripcionOee);
            this.form.controls['abreviatura'].setValue(this.row?.descripcionCorta);
        }
    }

    guardar(formValue) {
      this.submitted = true;
      if (this.form.invalid) return;

      const institucion = new Oee();
      institucion.id = formValue.id;
      institucion.descripcionOee = formValue.nombre;
      institucion.descripcionCorta = formValue.abreviatura;
      const response = this.row == null
          ? this.service.create(institucion)
          : this.service.update(institucion.id, institucion);

      response.subscribe(resp => {
          this.onResponse.emit(resp);
          if ([200,201].indexOf(resp.code) !== -1) this.close();
      });
    }

    close() {
      this.form.reset();
      this.setVisible.emit(false);
    }

  }
