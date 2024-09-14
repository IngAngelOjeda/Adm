import { Etiqueta } from "../../etiquetas/models/etiqueta.model"

export class ServicioEtiqueta{

    id_servicio_etiqueta : number;
    id_servicio : number;
    id_etiqueta : number;
    etiqueta: Etiqueta;

}

export class SelectItemEtiqueta{
    idEtiqueta : number;
    nombreEtiqueta: string;
}