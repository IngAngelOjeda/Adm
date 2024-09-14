import { Requisito } from "../../requisito/models/requisito.model"

export class ServicioRequisito{

    idServicioRequisito : number;
    idServicio : number;
    idRequisito : number;
    requisito: Requisito;

}

export class SelectItemRequisito{

    idRequisito : number;
    nombreRequisito: string;

}