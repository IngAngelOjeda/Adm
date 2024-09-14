package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "plan")
@Setter
@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "PLAN_ID_GENERATOR", sequenceName = "public.plan_id_plan_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_ID_GENERATOR")
    //@Column(name = "id_plan", unique = true, nullable = false)
    @Column(name = "id_plan", unique = true, nullable = false)
    private Long idPlan;

    @Column(name = "anho", nullable = false)
    private String anho;

    @Column(name = "fecha", nullable = false)
    private Date fecha;
    //private String fecha;

    @Column(name = "link_plan", nullable = false)
    private String linkPlan;

    @Column(name = "cantidad_funcionarios_tic", nullable = false)
    private String cantidadFuncionariosTic;

    @Column(name = "cantidad_funcionarios_admin", nullable = false)
    private String cantidadFuncionariosAdmin;

    @Column(name = "presupuesto_tic_anual", nullable = false)
    private String presupuestoTicAnual;

    @Column(name = "estado")
    private Boolean estado;

    public Plan(){}

}
