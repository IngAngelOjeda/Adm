import { Oee } from "../../../institucion/models/institucion.model";

export class RangoIp {
    idRango: number;
    oee: Oee;
    rango: string;
    estado: boolean;
    perteneceDmz: boolean;
    perteneceIpNavegacion: boolean;
    perteneceVpn: boolean;
}