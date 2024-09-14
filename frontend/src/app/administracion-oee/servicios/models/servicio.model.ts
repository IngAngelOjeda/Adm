import { Oee } from "../../../institucion/models/institucion.model";
import { Etiqueta } from "../../etiquetas/models/etiqueta.model"
import { Requisito } from "../../requisito/models/requisito.model"
import { Clasificador } from "../../clasificador/models/clasificador.model"

export class Servicio{
    idServicio : number;
    oee: Oee;
    etiqueta: Etiqueta[];
    requisito: Requisito[];
    clasificador: Clasificador[];
    nombreServicio: string;
    descripcionServicio: string;
    fechaCreacion: string;
    fechaModificacion: string;
    urlOnline: string;
    destacado: boolean;
    estadoServicio: string;
    
}
