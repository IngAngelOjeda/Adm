package py.gov.mitic.adminpy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.entity.TipoClasificador;

@Setter
@Getter
@AllArgsConstructor
public class ClasificadorDTO {

    private Long idClasificador;

    private String nombreClasificador;

//    private String descripcionClasificador;
//
//    private String estadoClasificador;
//
//    private TipoClasificador idTipoClasificador;

}
