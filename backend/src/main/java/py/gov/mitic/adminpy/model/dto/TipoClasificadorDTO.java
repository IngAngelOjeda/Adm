package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TipoClasificadorDTO {

    private Long idTipoClasificador;

    private String nombreTipoClasificador;

    private Date fechaCreacion;

    private Date fechaModificacion;

}
