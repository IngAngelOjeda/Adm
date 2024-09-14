package py.gov.mitic.adminpy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class EtiquetaDTO {

    private Long idEtiqueta;

    private String nombreEtiqueta;

//    private String estadoEtiqueta;
//
//    private Date fechaCreacion;
//
//    private Date fechaMoficacion;
}
