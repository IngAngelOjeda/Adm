import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { Dominio } from '../models/dominio.model';
import { DominioService } from '../services/dominio.service';
import { DatePipe } from '@angular/common';
import { StorageManagerService } from '../../../shared/services/storage-manager.service';

@Component({
  selector: 'app-dominio-form',
  templateUrl: './dominio-form.component.html',
  styleUrls: ['./dominio-form.component.scss']
})
export class DominioFormComponent implements OnInit, OnChanges {

    @Input() row: Dominio;
    @Input() visible: boolean;
    @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
    @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

    public oee: any;

    form: FormGroup;
    submitted = false;
    datePipe: DatePipe;
    

    constructor(
      private service: DominioService,
      private formBuilder: FormBuilder,
      private storageManager: StorageManagerService
    ) { }

    ngOnChanges(): void {
      this.initComponent();
    }

    ngOnInit(): void {
      this.initComponent();
      this.oee = this.storageManager.getCurrenSession().usuario.oee;
    }

    initComponent(): void {
      this.datePipe = new DatePipe('es-PY');
      this.submitted = false;

      this.form = this.formBuilder.group({
        idDominio: [''],
        dominio: ['', [ Validators.required ] ],
      });

      this.formEdit();
    }

    formEdit() {
      if(this.row != null) {
          this.form.controls['idDominio'].setValue(this.row?.idDominio);
          this.form.controls['dominio'].setValue(this.row?.dominio);
      }
    }

    guardar(formValue) {
      this.submitted = true;
      if (this.form.invalid) return;

      const dominio = new Dominio();
      dominio.idDominio = formValue.idDominio;
      dominio.dominio = formValue.dominio;
      dominio.oee = this.oee; 
      const response = this.row == null
          ? this.service.create(dominio)
          : this.service.update(dominio.idDominio, dominio);

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
