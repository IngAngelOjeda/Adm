import { TipoDato } from "../../tipo-dato/models/tipo-dato.model"
import { Servicio } from "../models/servicio.model"

export class ServicioInformacion{

    idServicioInformacion : number;
    tipoDato : TipoDato;
    servicio : Servicio;
    descripcionServicioInformacion : string;
    estadoServicioInformacion : string;
    fechaHoraCreacion : string;
}