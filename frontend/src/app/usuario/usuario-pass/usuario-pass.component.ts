import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageResponse } from 'src/app/shared/models/message-response.model';
import { MessageService } from 'primeng/api';
import { Usuario } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-usuario-pass',
  templateUrl: './usuario-pass.component.html',
  styleUrls: ['./usuario-pass.component.scss']
})
export class UsuarioPassComponent implements OnInit {
  
  @Input() visible: boolean;
  @Output() setVisible: EventEmitter<boolean> = new EventEmitter<boolean>(true);

  form: FormGroup;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private service: UsuarioService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {

    this.submitted = false;

    this.form = this.formBuilder.group({
      password: ['', [ Validators.required ] ],
      password2: ['', [ Validators.required ] ],
    });
    this.form.reset();
    
  }

  guardar(formValue) {
    this.submitted = true;
    if (this.form.invalid) return;

    const usuario = new Usuario();
    usuario.password = formValue.password;
    usuario.password2 = formValue.password2;

    this.service.updatePassword(usuario).subscribe(resp => {
      // console.log(resp);
        this.onResponse(resp);
        if ([200,201].indexOf(resp.code) !== -1) this.close();
    });
  }

  onResponse(res: MessageResponse) {
    if (res.code == 200) {
      this.messageService.add({severity: 'success', summary: 'Operación exitosa', detail: res.message, life: 3000});
    } else {
      if (res.code < 500) {
        this.messageService.add({severity: 'warn', summary: 'Atención', detail: res.message, life: 30000});
      } else {
        this.messageService.add({severity: 'error', summary: 'Atención', detail: res.message, life: 30000});
      }
    }
  }

  close() {
    this.form.reset();
    this.setVisible.emit(false);
  }

}
