package py.gov.mitic.adminpy.model.dto;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.entity.Dependencia;

@Setter
@Getter

public class FuncionarioDTO {

    private Long idFuncionario;

    private Dependencia dependencia;

    private String nombre;

    private String apellido;

    private String correoInstitucional;

    private String cedula;

    private String cargo;

    private Boolean estadoFuncionario;

}
