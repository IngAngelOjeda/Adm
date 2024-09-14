export class Dependencias{
    id: number;
    idDependencia: number;
    codigo: string;
    descripcionDependencia: string;
    idDependenciaPadre: number;
    estadoDependencia: boolean;
    oee :[];
    dependenciaPadre: [];
    /* idOeeInformacion: number;
    descripcionOeeInformacion: string;
    oee: [];
    tipoDato: []; */
}

export class selectItem{
    idDependencia: number;
    descripcionDependencia: string;
}