package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TipoRequisitoDTO {

    private Long idTipoRequisito;

    private String nombreTipoRequisito;

    private Boolean estado;

    private Date fechaCreacion;

    private Date fechaMoficacion;
}
