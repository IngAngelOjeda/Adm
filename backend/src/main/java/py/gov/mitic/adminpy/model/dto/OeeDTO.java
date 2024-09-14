package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OeeDTO {

    private Long id;

    private String descripcionCorta;

    private String descripcionOee;

    private String estadoOee;

    private String fechaCreacion;

    private String fechaModificacion;

    private String urlOee;

    private Long idEntidad;

    private EntidadDTO entidad;


}
