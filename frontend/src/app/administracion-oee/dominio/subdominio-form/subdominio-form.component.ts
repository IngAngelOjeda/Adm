import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { SubDominio } from '../models/subdominio.model';
import { Dominio } from '../models/dominio.model';
import { SubdominioService } from '../services/subdominio.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-subdominio-form',
  templateUrl: './subdominio-form.component.html',
  styleUrls: ['./subdominio-form.component.scss']
})
export class SubdominioFormComponent implements OnInit {

  @Input() row: SubDominio;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  form: FormGroup;
  submitted = false;
  datePipe: DatePipe;

  constructor(
    private service: SubdominioService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.initComponent();
  }

  ngOnChanges(): void {
    this.initComponent();
  }


  initComponent(): void {
    this.datePipe = new DatePipe('es-PY');
    this.submitted = false;

    this.form = this.formBuilder.group({
      idSubDominio: [''],
      subdominio: ['', [ Validators.required ] ],
    });

    this.formEdit();
  }

  formEdit() {
    if(this.row != null) {
        this.form.controls['idSubDominio'].setValue(this.row?.idSubDominio);
        this.form.controls['subdominio'].setValue(this.row?.subdominio);
    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const subdominio = new SubDominio();
    subdominio.dominio = this.row?.dominio;
    subdominio.idSubDominio = formValue.idSubDominio;
    subdominio.subdominio   = formValue.subdominio;
    const response = this.row.idSubDominio == null
        ? this.service.create(subdominio)
        : this.service.update(subdominio.idSubDominio, subdominio);

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
