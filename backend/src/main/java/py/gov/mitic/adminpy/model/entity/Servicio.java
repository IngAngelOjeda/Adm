package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Servicio implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SERVICIO_ID_SERVICIO_SEQ", sequenceName = "servicio_id_servicio_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICIO_ID_SERVICIO_SEQ")
    @Column(name = "id_servicio", unique = true, nullable = false)
    private Long idServicio;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @JsonIgnore()
    @ManyToMany(targetEntity = Etiqueta.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "servicio_etiqueta",
            schema = "public",
            joinColumns = @JoinColumn(name = "id_servicio"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    public List<Etiqueta> etiqueta;

    @JsonIgnore()
    @ManyToMany(targetEntity = Requisito.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "servicio_requisito",
            schema = "public",
            joinColumns = @JoinColumn(name = "id_servicio"),
            inverseJoinColumns = @JoinColumn(name = "id_requisito")
    )
    public List<Requisito> requisito;

    @JsonIgnore()
    @ManyToMany(targetEntity = Clasificador.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "servicio_clasificador",
            schema = "public",
            joinColumns = @JoinColumn(name = "id_servicio"),
            inverseJoinColumns = @JoinColumn(name = "id_clasificador")
    )
    public List<Clasificador> clasificador;

    @JsonIgnore()
    @ManyToMany(targetEntity = ServicioInformacion.class, cascade =  { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY )
    @JoinTable(
            name = "servicio_informacion",
            schema = "public",
            joinColumns = @JoinColumn(name = "id_servicio"),
            inverseJoinColumns = @JoinColumn(name = "id_servicio_informacion")
    )
    public List<ServicioInformacion> servicioInformacion;

    @Column(name = "nombre_servicio")
    private String nombreServicio;

    @Column(name = "descripcion_servicio")
    private  String descripcionServicio;

    @Column(name = "estado_servicio")
    private String estadoServicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    @Column(name = "codigo_servicio")
    private Long codigoServicio;

    @Column(name = "destacado")
    private Boolean destacado;

    @Column(name = "utiliza_identidad_electronica")
    private Boolean utilizaIdentidadElectronica;

    @Column(name = "url_online")
    private String urlOnline;

    public Servicio(){

    }
}
