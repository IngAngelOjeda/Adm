package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DependenciaWithParentDTO {

    private Long idDependencia;
    private String descripcionDependencia;
    private Long orden;
    private Long idDependenciaPadre;
    private String descripcionDependenciaPadre;
    private Long idOee;
    private String descripcionOee;
    private Boolean estadoDependencia;
    private Date fechaCreacion;

    public DependenciaWithParentDTO(Long idDependencia, String descripcionDependencia, Long orden, Long idDependenciaPadre, String descripcionDependenciaPadre, Long idOee, String descripcionOee, Boolean estadoDependencia, Date fechaCreacion) {
        this.idDependencia = idDependencia;
        this.descripcionDependencia = descripcionDependencia;
        this.orden = orden;
        this.idDependenciaPadre = idDependenciaPadre;
        this.descripcionDependenciaPadre = descripcionDependenciaPadre;
        this.idOee = idOee;
        this.descripcionOee = descripcionOee;
        this.estadoDependencia = estadoDependencia;
        this.fechaCreacion = fechaCreacion;
    }
}
