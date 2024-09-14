import { Clasificador } from "../../clasificador/models/clasificador.model";

export class ServicioClasificador{

    idServicioClasificador : number;
    idServicio : number;
    idClasificador : number;
    clasificador: Clasificador

}

export class SelectItemClasificador{
    idClasificador : number;
    nombreClasificador: string;
}