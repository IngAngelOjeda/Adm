package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class DependenciaDTO {

    private Long idDependencia;

    private String descripcionDependencia;

    private String codigo;

    private Long orden;

    private DependenciaDTO dependenciaDTO;

    private OeeDTO oee;

    private Boolean estadoDependencia;

    private Date fechaCreacion;

    private Long idDependenciaPadre;


}
