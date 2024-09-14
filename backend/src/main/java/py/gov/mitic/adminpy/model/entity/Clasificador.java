package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "clasificador")
@Setter
@Getter
public class Clasificador implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ID_CLASIFICADOR_SEQ",sequenceName = "id_clasificador_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_CLASIFICADOR_SEQ")
    @Column(name = "id_clasificador", unique = true, nullable = false)
    private Long idClasificador;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_tipo_clasificador")
    private TipoClasificador tipoClasificador;

    @Column(name = "nombre_clasificador")
    private String nombreClasificador;

    @Column(name = "descripcion_clasificador")
    private String descripcionClasificador;

    @Column(name = "estado_clasificador")
    private String estadoClasificador;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

}
