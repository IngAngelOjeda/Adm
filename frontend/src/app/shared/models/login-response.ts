import { Usuario } from "src/app/usuario/models/usuario.model";
import { Permiso } from "../../permiso/models/permiso.model";

export class LoginResponse {
    accessToken: string | undefined;
    refreshToken: string | undefined;
	usuario: Usuario | undefined;
	permisos: Permiso[] | undefined;
    message: string;
    code: number;
}
