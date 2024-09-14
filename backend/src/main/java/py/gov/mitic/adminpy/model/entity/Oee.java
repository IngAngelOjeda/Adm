package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the institucion database table.
 */
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Oee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "OEE_IDOEE_GENERATOR", sequenceName = "OEE_ID_OEE_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OEE_IDOEE_GENERATOR")
    @Column(name = "id_oee", unique = true, nullable = false)
    private Long id;

    @Column(name="codigo_oee")
    private Integer codigoOee;

    @Column(name="descripcion_oee")
    private String descripcionOee;

    @Column(name="descripcion_corta")
    private String descripcionCorta;

    @Column(name="url_oee")
    private String urlOee;

    @Column(name="estado_oee")
    private String estadoOee;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name="fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name="fecha_modificacion")
    private Date fechaModificacion;

    @OneToMany()
    @JoinColumn(name = "id_oee")
    @JsonIgnore
    private List<Usuario> usuarios;

    @Column(name = "id_entidad", updatable = false, insertable = false)
    private Long idEntidad;

    @ManyToOne(fetch=javax.persistence.FetchType.LAZY)
    @JoinColumn(name = "id_entidad")
    private Entidad entidad;

    public Oee() {
    }

    public Oee(Long id, String descripcionOee, String abreviatura) {
        super();
        this.id = id;
        this.descripcionOee = descripcionOee;
        this.descripcionCorta = abreviatura;
    }

}