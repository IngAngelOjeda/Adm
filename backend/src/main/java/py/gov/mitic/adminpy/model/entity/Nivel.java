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
public class Nivel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "NIVEL_IDNIVEL_GENERATOR", sequenceName = "NIVEL_ID_NIVEL_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NIVEL_IDNIVEL_GENERATOR")
    @Column(name = "id_nivel")
    private Long idNivel;

    @Column(name = "codigo_nivel")
    private Integer codigoNivel;

    @Column(name = "descripcion_nivel")
    private String descripcionNivel;

    @Column(name = "estado_nivel")
    private String estadoNivel;

    @Column(name = "url_nivel")
    private String urlNivel;

    // bi-directional many-to-one association to Entidad
    @OneToMany(mappedBy = "nivel")
    @JsonIgnore
    private List<Entidad> entidads;

    public Nivel() {    }

    public Entidad addEntidad(Entidad entidad) {
        getEntidads().add(entidad);
        entidad.setNivel(this);
        return entidad;
    }

    public Entidad removeEntidad(Entidad entidad) {
        getEntidads().remove(entidad);
        entidad.setNivel(null);
        return entidad;
    }


}
