package py.gov.mitic.adminpy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
public class RequisitoDTO {

    private Long idRequisito;

    private String nombreRequisito;

//    private OeeDTO idOee;
//
//    private Date fechaCreacion;
//
//    private Boolean estado;
//
//    private Date fechaModificacion;
//
//    private TipoRequisitoDTO tipoRequisito;

}
