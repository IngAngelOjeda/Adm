import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { ServicioInformacion } from '../models/servicio-informacion.model';
import { Servicio } from '../models/servicio.model';
import { ServicioInformacionService } from '../services/servicio-informacion.service';
import { DatePipe } from '@angular/common';
import { SelectItem } from '../../tipo-dato/models/tipo-dato.model';

@Component({
  selector: 'app-servicios-informacion-form',
  templateUrl: './servicios-informacion-form.component.html',
  styleUrls: ['./servicios-informacion-form.component.scss']
})
export class ServiciosInformacionFormComponent implements OnInit {
  
  @Input() row: ServicioInformacion;
  @Input() servicio: Servicio;
  @Input() tipoDato: SelectItem[];
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  form: FormGroup;
  submitted = false;
  datePipe: DatePipe;

  constructor(
    private service: ServicioInformacionService,
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
      idServicioInformacion: [''],
      tipoDato: ['', [Validators.required]],
      descripcionServicioInformacion: ['', [ Validators.required ] ],
      checkCode: [false],
    });

    this.formEdit();
  }

  formEdit() {
    if(this.row != null) {
        this.form.controls['idServicioInformacion'].setValue(this.row?.idServicioInformacion);
        this.form.controls['tipoDato'].setValue({idTipoDato: this.row?.tipoDato?.idTipoDato, descripcionTipoDato: this.row?.tipoDato?.descripcionTipoDato });
        this.form.controls['descripcionServicioInformacion'].setValue(this.row?.descripcionServicioInformacion);
    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const servicioInformacion = new ServicioInformacion();
    servicioInformacion.servicio = this.servicio;
    servicioInformacion.tipoDato = formValue.tipoDato;
    servicioInformacion.descripcionServicioInformacion = formValue.checkCode
    ? this.stripHtml(formValue.descripcionServicioInformacion)
    : formValue.descripcionServicioInformacion;
    const response = this.row.idServicioInformacion == null
        ? this.service.create(servicioInformacion)
        : this.service.update(this.row.idServicioInformacion, servicioInformacion);

    response.subscribe(resp => {
        this.onResponse.emit(resp);
        if ([200,201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

  stripHtml(html: string): string {
    const doc = new DOMParser().parseFromString(html, 'text/html');
    return doc.body.textContent || '';
  }

}
