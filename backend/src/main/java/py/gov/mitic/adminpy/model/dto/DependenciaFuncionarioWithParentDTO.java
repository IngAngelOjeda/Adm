package py.gov.mitic.adminpy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DependenciaFuncionarioWithParentDTO {

    private Long idDependencia;
    private String descripcionDependencia;
    private Long orden;
    private Long idDependenciaPadre;
    private String descripcionDependenciaPadre;
    private Long idOee;
    private String descripcionOee;
    private Boolean estadoDependencia;
    private Date fechaCreacion;
    private Long idFuncionario;
    private String nombre;
    private String apellido;
    private String cargo;

    // Constructor adicional para crear instancias a partir de un arreglo de objetos
    public DependenciaFuncionarioWithParentDTO(Object[] result) {
        this.idDependencia = (Long) result[0];
        this.descripcionDependencia = (String) result[1];
        this.orden = (Long) result[2];
        this.idDependenciaPadre = (Long) result[3];
        this.descripcionDependenciaPadre = (String) result[4];
        this.idOee = (Long) result[5];
        this.descripcionOee = (String) result[6];
        this.estadoDependencia = (Boolean) result[7];
        this.fechaCreacion = (Date) result[8];
        this.idFuncionario = (Long) result[9];
        this.nombre = (String) result[10];
        this.apellido = (String) result[11];
        this.cargo = (String) result[12];
    }

}
