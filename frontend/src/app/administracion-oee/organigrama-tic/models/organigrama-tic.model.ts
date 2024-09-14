import { Oee } from "src/app/institucion/models/institucion.model";
import { Dependencias, selectItem } from "../../datos-oee/models/dependencias.model";

export class OrganigramaTic {
    idFuncionario: number;
    idDependencia: number;
    nombre: string;
    apellido: string;
    cedula: string;
    cargo: string;
    correoInstitucional: string;
    estadoFuncionario: boolean;
    oee: Oee[];
    dependencia: Dependencias[];
    /* dependencia1: selectItem[]; */

    /* idOrganigrama: number; */
    /* anho: string;
    fecha: string;
    linkPlan: string;
    cantidadFuncionariosTic: string;
    cantidadFuncionariosAdmin: string;
    presupuestoTicAnual: string;
    estado: boolean; */
    /* oee: Oee[];
    estado: string;
    anhoCreacion: number;
    poseeCodigoFuente: boolean; */
}