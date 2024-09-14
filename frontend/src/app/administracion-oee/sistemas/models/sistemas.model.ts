import { Oee } from "../../../institucion/models/institucion.model";

export class Sistemas {
    id_sistema: number;
    idSistema: number;
    nombre: string;
    tecnologiaLenguaje: string;
    tecnologiaBd: string;
    oee: Oee;
    estado: string;
    anhoCreacion: number;
    objetoProposito: string;
    linkCodigoFuente: string;
    tecnologiaFramework: string;
    areaResponsable: string;
    poseeCodigoFuente: boolean;
    poseeContratoMantenimiento: boolean;
    tipoLicencia: string;
    tipoUso: string;
    anhoImplementacion: string;
    poseeVigencia: boolean;
    tipoSoporte: string;
    dataCenterInfraestructura: string;
    costoDesarrollo: string;
    costoMantenimiento: string;
    costoEsfuerzo: number;
    fechaVigencia: string;
    desarrolladorFabricante: string;
    linkProduccion: string;
    poseeLicencia: boolean;
    listaDesarrolladores: string;
}