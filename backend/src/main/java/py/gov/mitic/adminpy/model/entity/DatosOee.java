package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "oee_informacion")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DatosOee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "OEE_INFORMACION_ID_GENERATOR", sequenceName = "public.oee_informacion_id_oee_informacion_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "id_oee_informacion", unique = true, nullable = false)
    private Long idOeeInformacion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_dato")
    private TipoDato tipoDato;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @Column(name = "descripcion_oee_informacion")
    private String descripcionOeeInformacion;

    @Column(name = "estado_oee_informacion")
    private String estadoOeeInformacion;

    public DatosOee(){}
}
