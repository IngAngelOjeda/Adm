package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SistemaDTO {
    private Long idSistema;

    private String nombre;

    private String tecnologiaLenguaje;

    private String tecnologiaBd;

    private String linkProduccion;

    private OeeDTO oee;

    private String estado;

    private Integer anhoCreacion;

    private String objetoProposito;

    private String linkCodigoFuente;

    private String tecnologiaFramework;

    private String areaResponsable;

    private Boolean poseeCodigoFuente;

    private Boolean poseeContratoMantenimiento;

    private Boolean poseeLicencia;

    private String tipoLicencia;

    private String tipoUso;

    private Integer anhoImplementacion;

    private Boolean poseeVigencia;

    private String fechaVigencia;

    private String tipoSoporte;

    private String dataCenterInfraestructura;

    private Integer costoDesarrollo;

    private String listaDesarrolladores;

    private Integer costoMantenimiento;

    private Long costoEsfuerzo;

    private String desarrolladorFabricante;


}
