package py.gov.mitic.adminpy.model.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import py.gov.mitic.adminpy.model.dto.TipoDatoDto;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.repository.ServicioInformacionRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "servicio_informacion")
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServicioInformacion implements Serializable{

    @Id
    @SequenceGenerator(name = "SERVICIO_INFORMACION_ID_SERVICIO_INFORMACION_SEQ", sequenceName = "servicio_informacion_id_servicio_informacion_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICIO_INFORMACION_ID_SERVICIO_INFORMACION_SEQ")
    @Column(name = "id_servicio_informacion", unique = true, nullable = false)
    private Long idServicioInformacion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_dato")
    private TipoDato tipoDato;

    @JsonIgnore()
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @Column(name = "descripcion_servicio_informacion")
    private  String descripcionServicioInformacion;

    @Column(name = "estado_servicio_informacion")
    private  String estadoServicioInformacion;

    @JsonIgnore()
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_hora_creacion")
    private Date fechaHoraCreacion;

    public ServicioInformacion(){

    }

}


