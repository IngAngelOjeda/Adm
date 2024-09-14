import { Usuario } from "src/app/usuario/models/usuario.model";
import { Permiso } from "../../permiso/models/permiso.model";

export class RecuperarClaveResponse {
    accessToken: string;
    refreshToken: string;
	usuario: Usuario;
	permisos: Permiso[];
}
