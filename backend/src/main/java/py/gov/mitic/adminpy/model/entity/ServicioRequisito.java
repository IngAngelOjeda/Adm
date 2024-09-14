package py.gov.mitic.adminpy.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "servicio_requisito")
@Setter
@Getter
public class ServicioRequisito implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SERVICIO_REQUISITO_ID_GENERATOR", sequenceName = "servicio_requisito_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICIO_REQUISITO_ID_GENERATOR")
    @Column(name = "id_servicio_requisito", unique = true, nullable = false)
    private Long idServicioRequisito;

    @ManyToOne
    @JoinColumn(name = "id_requisito")
    private Requisito requisito;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

}

