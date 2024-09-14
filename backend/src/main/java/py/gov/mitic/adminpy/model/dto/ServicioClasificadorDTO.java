package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServicioClasificadorDTO {

    private Long idServicioClasificador;

    private ClasificadorDTO clasificador;

    private ServicioDTO servicio;

}
