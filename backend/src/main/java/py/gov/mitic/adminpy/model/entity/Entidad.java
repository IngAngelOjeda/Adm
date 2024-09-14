package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Entidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ENTIDAD_IDENTIDAD_GENERATOR", sequenceName = "ENTIDAD_ID_ENTIDAD_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENTIDAD_IDENTIDAD_GENERATOR")
    @Column(name = "id_entidad")
    private Long idEntidad;

    @Column(name = "codigo_entidad")
    private Integer codigoEntidad;

    @Column(name = "descripcion_entidad")
    private String descripcionEntidad;

    @Column(name = "estado_entidad")
    private String estadoEntidad;

    @Column(name = "url_entidad")
    private String urlEntidad;

    // bi-directional many-to-one association to Nivel
    @ManyToOne
    @JoinColumn(name = "id_nivel")
    private Nivel nivel;

    // bi-directional many-to-one association to Oee
    @OneToMany(mappedBy = "entidad")
    @JsonIgnore
    private List<Oee> oees;

    public Entidad() {
    }

    public Entidad(Long idEntidad, String descripcionEntidad) {
        this.idEntidad = idEntidad;
        this.descripcionEntidad = descripcionEntidad;
    }

    public Oee addOee(Oee oee) {
        getOees().add(oee);
        oee.setEntidad(this);
        return oee;
    }

    public Oee removeOee(Oee oee) {
        getOees().remove(oee);
        oee.setEntidad(null);
        return oee;
    }

}
