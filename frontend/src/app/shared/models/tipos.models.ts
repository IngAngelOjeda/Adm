export class Tipos{
    id: number;
    nombre: string;
    estado?: boolean;
}

export interface Departamento {
    id:         number;
    nombre:     string;
    ciudades?:  Ciudades[];
}

export interface Ciudades {
    id:         number;
    idDepartamento: number;
    nombre:     string;
    barrios:    Barrios[];
}
export interface Barrios {
    id:         number;
    idCiudad:   number;
    nombre:     string;
}