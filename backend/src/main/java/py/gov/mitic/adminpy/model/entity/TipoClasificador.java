package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(schema = "public", name = "tipo_clasificador")
@Setter
@Getter
public class TipoClasificador implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ID_TIPO_CLASIFICADOR_SEQ", sequenceName = "id_tipo_clasificador_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_TIPO_CLASIFICADOR_SEQ")
    @Column(name = "id_tipo_clasificador", unique = true, nullable = false)
    private Long idTipoClasificador;

    @Column(name = "nombre_tipo_clasificador")
    private String nombreTipoClasificador;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    @OneToMany(mappedBy = "tipoClasificador")
    @JsonManagedReference
    private List<Clasificador> clasificadores;

    public TipoClasificador(){}

}
