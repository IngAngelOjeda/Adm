package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "requisito")
@Getter
@Setter
public class Requisito implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "REQUISITO_ID_SEQ",sequenceName = "requisito_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUISITO_ID_SEQ")
    @Column(name = "id_requisito", unique = true, nullable = false)
    private Long idRequisito;

    @Column(name = "nombre_requisito")
    private String nombreRequisito;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "tipo")
    private String tipo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    @OneToOne
    @JoinColumn(name = "id_tipo_requisito")
    private TipoRequisito tipoRequisito;

    public Requisito(){}

    public Requisito(Long idRequisito, String nombreRequisito, Boolean estado) {
        super();
        this.idRequisito = idRequisito;
        this.nombreRequisito = nombreRequisito;
        this.estado = estado;
    }
}
