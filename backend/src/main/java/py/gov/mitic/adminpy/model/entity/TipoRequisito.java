package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "tipo_requisito")
@Getter
@Setter
public class TipoRequisito implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "TIPO_REQUISITO_ID_SEQ",sequenceName = "tipo_requisito_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIPO_REQUISITO_ID_SEQ")
    @Column(name = "id_tipo_requisito", unique = true, nullable = false)
    private Long idTipoRequisito;

    @Column(name = "nombre_tipo_requisito")
    private String nombreTipoRequisito;

    @Column(name = "estado")
    private Boolean estado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    public TipoRequisito(){}

    public TipoRequisito(Long idTipoRequisito, String nombreTipoRequisito){
        super();
        this.idTipoRequisito = idTipoRequisito;
        this.nombreTipoRequisito = nombreTipoRequisito;
    }
}
