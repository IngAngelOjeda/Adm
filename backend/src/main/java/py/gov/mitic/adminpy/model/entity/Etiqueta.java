package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "etiqueta")
@Setter
@Getter
public class Etiqueta implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ID_ETIQUETA_SEQ",sequenceName = "id_etiqueta_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_ETIQUETA_SEQ")
    @Column(name = "id_etiqueta", unique = true, nullable = false)
    private Long idEtiqueta;

    @Column(name = "nombre_etiqueta")
    private String nombreEtiqueta;

    @Column(name = "descripcion_etiqueta")
    private String descripcionEtiqueta;

    @Column(name = "estado_etiqueta")
    private String estadoEtiqueta;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    public Etiqueta(){}

    public Etiqueta(Long idEtiqueta, String nombreEtiqueta, String descripcionEtiqueta) {
        super();
        this.idEtiqueta = idEtiqueta;
        this.nombreEtiqueta = nombreEtiqueta;
        this.descripcionEtiqueta = descripcionEtiqueta;
    }
}
