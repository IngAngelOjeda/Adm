package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "tipo_dato")
@Setter
@Getter
public class TipoDato implements Serializable{

    public static final Long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ID_TIPO_DATO_SEQ",sequenceName = "id_tipo_dato_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_TIPO_DATO_SEQ")
    @Column(name = "id_tipo_dato", unique = true, nullable = false)
    private Long idTipoDato;

    @JsonIgnore()
    @Column(name = "icono")
    private String icono;

    @Column(name = "descripcion_tipo_dato")
    private String descripcionTipoDato;

    @Column(name = "tipo")
    private String tipo;

    @JsonIgnore()
    @Column(name = "categoria")
    private String categoria;

    @JsonIgnore()
    @Column(name = "estado")
    private Boolean estado;

    @JsonIgnore()
    @Column(name = "orden")
    private Long orden;

    @JsonIgnore()
    @Column(name = "codigo_tipo_dato")
    private String codigoTipoDato;

    @JsonIgnore()
    @Column(name = "ayuda")
    private String ayuda;

    public TipoDato(){}


}
