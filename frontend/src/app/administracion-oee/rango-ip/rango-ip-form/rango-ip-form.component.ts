import { Component, OnInit, Input, EventEmitter, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { RangoIp } from '../models/rango-ip.model';
import { RangoIpService } from '../services/rango-ip.service';
import { DatePipe } from '@angular/common';
import { StorageManagerService } from '../../../shared/services/storage-manager.service';

@Component({
  selector: 'app-rango-ip-form',
  templateUrl: './rango-ip-form.component.html',
  styleUrls: ['./rango-ip-form.component.scss']
})
export class RangoIpFormComponent implements OnInit {

  @Input() row: RangoIp;
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);
  @Output() onResponse: EventEmitter<MessageResponse> = new EventEmitter<MessageResponse>(true);

  public oee: any;

  form: FormGroup;
  submitted = false;
  datePipe: DatePipe;


  constructor(
    private service: RangoIpService,
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
      idRango: [''],
      optionIp: ['optionIp1'],
      rango: ['', [Validators.required, this.ipPublicaValidator()]],
      perteneceDmz: [false],
      perteneceIpNavegacion: [false],
      perteneceVpn: [false],
    });

    this.form.get('optionIp').valueChanges.subscribe((optionIp) => {
      const ipAddressControl = this.form.get('rango');
      if (optionIp === 'optionIp1') {
        ipAddressControl.setValidators([Validators.required, this.ipPublicaValidator()]);
      } else {
        ipAddressControl.setValidators([Validators.required, this.cidrValidator()]);
      }
      ipAddressControl.setValue(null);
      ipAddressControl.updateValueAndValidity();
    });

    this.formEdit();
  }


  formEdit() {
    if (this.row != null) {
      if (this.row?.rango && this.row?.rango.includes('/')) {
        this.form.controls['optionIp'].setValue('optionIp2'); 
      } else {
        this.form.controls['optionIp'].setValue('optionIp1'); 
      }
      this.form.controls['idRango'].setValue(this.row?.idRango);
      this.form.controls['rango'].setValue(this.row?.rango);
      this.form.controls['perteneceDmz'].setValue(this.row?.perteneceDmz);
      this.form.controls['perteneceIpNavegacion'].setValue(this.row?.perteneceIpNavegacion);
      this.form.controls['perteneceVpn'].setValue(this.row?.perteneceVpn);
      
    }
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;
    const rangoip = new RangoIp();
    rangoip.idRango = formValue.idRango;
    rangoip.rango = formValue.rango;
    rangoip.perteneceDmz = formValue.perteneceDmz;
    rangoip.perteneceIpNavegacion = formValue.perteneceIpNavegacion;
    rangoip.perteneceVpn = formValue.perteneceVpn;
    rangoip.oee = this.oee;
    const response = this.row == null
      ? this.service.create(rangoip)
      : this.service.update(rangoip.idRango, rangoip);

    response.subscribe(resp => {
      this.onResponse.emit(resp);
      if ([200, 201].indexOf(resp.code) !== -1) this.close();
    });
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

  formatoIpOnInput(input: HTMLInputElement): void {
    const ipValue = input.value;
    const formattedIp = this.formatoIP(ipValue);
    input.value = formattedIp;
  }

  formatoIP(ip: string): string {
    const numericValues = ip.replace(/[^\d./]/g, '');
    const parts = numericValues.split('.');
    if (numericValues.includes('/')) {
      return ip;
    }
    for (let i = 0; i < parts.length; i++) {
      parts[i] = parts[i].replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }
    return parts.join('.');
  }

  ipPublicaValidator(): any {
    return Validators.pattern(
      /^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$/
    );
  }
  cidrValidator(): any {
    return Validators.pattern(
      /^(\d{1,3}\.){3}\d{1,3}\/\d{1,2}$/
    );
  }


}
