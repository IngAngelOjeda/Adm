package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PermisoDTO {

    private Long idPermiso;

    private String nombre;

    private String descripcion;

    public Long getIdPermiso() {
        return idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
