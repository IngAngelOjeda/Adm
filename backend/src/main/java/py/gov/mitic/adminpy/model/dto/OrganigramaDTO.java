package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganigramaDTO {
    private Long idOrganigrama;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String cedula;
    private OeeDTO oee;
//    VER SI ESTO ES DE TIPO DTOCARGO
    private String cargo;
}
