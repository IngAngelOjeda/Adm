package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.entity.Clasificador;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.Requisito;

import java.sql.Date;
import java.util.List;

@Setter
@Getter
public class ServicioDTO {

    private Long idServicio;

    private String nombreServicio;

    private String descripcionServicio;

    private Oee oee;

    private List<EtiquetaDTO> etiqueta;

    private List<ClasificadorDTO> clasificador;

    private List<RequisitoDTO> requisito;

    private String estadoServicio;

    private Date fechaCreacion;

    private Date fechaModificacion;

    private Long codigoServicio;

    private Boolean destacado;

//    private Boolean utilizaEntidadElectronica;

    private String urlOnline;

}
