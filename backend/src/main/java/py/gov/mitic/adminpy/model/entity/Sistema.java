package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(schema = "public", name = "sistema")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class Sistema implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SISTEMA_ID_GENERATOR", sequenceName = "public.sistema_sistema_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SISTEMA_ID_GENERATOR")
    @Column(name = "id_sistema", unique = true, nullable = false)
    private Long idSistema;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tecnologia_lenguaje")
    private String tecnologiaLenguaje;

    @Column(name = "tecnologia_bd")
    private String tecnologiaBd;

    @Column(name = "link_produccion")
    private String linkProduccion;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @Column(name = "estado")
    private String estado;

    @Column(name = "anho_creacion")
    private Integer anhoCreacion;

    @Column(name = "objeto_proposito")
    private String objetoProposito;

    @Column(name = "link_codigo_fuente")
    private String linkCodigoFuente;

    @Column(name = "tecnologia_framework")
    private String tecnologiaFramework;

    @Column(name = "area_responsable")
    private String areaResponsable;

    @Column(name="posee_codigo_fuente", length = 100)
    private Boolean poseeCodigoFuente;

    @Column(name="posee_contrato_mantenimiento", length = 100)
    private Boolean poseeContratoMantenimiento;

    @Column(name="posee_licencia", length = 100)
    private Boolean poseeLicencia;

    @Column(name = "tipo_licencia")
    private String tipoLicencia;

    @Column(name = "tipo_uso")
    private String tipoUso;

    @Column(name = "anho_implementacion")
    private Integer anhoImplementacion;

    @Column(name="posee_vigencia", length = 100)
    private Boolean poseeVigencia;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name="fecha_vigencia")
    private Date fechaVigencia;

    @Column(name = "tipo_soporte")
    private String tipoSoporte;

    @Column(name = "datacenter_infraestructura")
    private String dataCenterInfraestructura;

    @Column(name = "costo_desarrollo")
    private Integer costoDesarrollo;

    @Column(name = "lista_desarrolladores")
    private String listaDesarrolladores;

    @Column(name = "costo_mantenimiento")
    private Integer costoMantenimiento;

    @Column(name = "costo_esfuerzo")
    private Integer costoEsfuerzo;

    @Column(name = "desarrollador_fabricante")
    private String desarrolladorFabricante;

    public Sistema() {
        this.estado = "A";
    }
}
