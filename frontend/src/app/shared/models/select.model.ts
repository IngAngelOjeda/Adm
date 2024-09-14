export class SelectItem {
    id: number;
    nombre: string;
}


export class SelectItemP<T = any> {
    label?: string;
    value: T;
    styleClass?: string;
    icon?: string;
    title?: string;
    disabled?: boolean;
}
