package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "servicio_clasificador")
@Setter
@Getter
public class ServicioClasificador implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SERVICIO_CLASIFICADOR_ID_GENERATOR", sequenceName = "servicio_clasificador_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICIO_CLASIFICADOR_ID_GENERATOR")
    @Column(name = "id_servicio_clasificador", unique = true, nullable = false)
    private Long idServicioClasificador;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_clasificador")
    private Clasificador clasificador;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

}
