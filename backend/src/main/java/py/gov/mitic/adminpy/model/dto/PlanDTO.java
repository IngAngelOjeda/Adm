package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlanDTO {
    private Long idPlan;
    private String anho;
    private String fecha;
    //private Date fecha;
    private String linkPlan;
    private String cantidadFuncionariosTic;
    private String cantidadFuncionariosAdmin;
    private String presupuestoTicAnual;
    private Boolean estado;
    //private PermisoDTO permisos;
}
