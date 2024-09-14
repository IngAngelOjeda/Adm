package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RolDTO {

    private Long id;

    private Long idRol;

    private String nombre;

    private String descripcion;

    private PermisoDTO permisos;
}
