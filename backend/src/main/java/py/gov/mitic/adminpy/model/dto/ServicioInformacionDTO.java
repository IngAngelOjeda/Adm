package py.gov.mitic.adminpy.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.entity.Servicio;
import py.gov.mitic.adminpy.model.entity.TipoDato;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
public class ServicioInformacionDTO {

    private Long idServicioInformacion;

    private Servicio servicio;

    private TipoDato tipoDato;

    private String descripcionServicioInformacion;

    private Date fechaHoraCreacion;

}
