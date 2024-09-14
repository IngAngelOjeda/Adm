package py.gov.mitic.adminpy.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "servicio_etiqueta")
@Setter
@Getter
public class ServicioEtiqueta implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ID_SERVICIO_ETIQUETA_SEQ",sequenceName = "servicio_etiqueta_id_servicio_etiqueta_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SERVICIO_ETIQUETA_SEQ")
    @Column(name = "id_servicio_etiqueta", unique = true, nullable = false)
    private Long idServicioEtiqueta;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_etiqueta")
    private Etiqueta etiqueta;

}
