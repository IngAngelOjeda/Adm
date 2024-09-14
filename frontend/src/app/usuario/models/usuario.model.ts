import { Rol } from "../../rol/models/rol.model";
import { Oee } from '../../institucion/models/institucion.model';

export class Usuario {
    idUsuario: number;
    username: string;
    cedula: string;
    password: string;
    password2: string;
    nombre: string;
    apellido: string;
    fechaExpiracion: string;
    oee: Oee;
    permisos: string[];
    roles: Rol[];
    estado: boolean;
    cargo: string;
    direccion: string;
    telefono: string;
    correo: string;
    justificacionAlta: string;
}

