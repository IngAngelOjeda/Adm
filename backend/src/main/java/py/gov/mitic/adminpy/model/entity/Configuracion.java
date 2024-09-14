package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(schema = "public", name = "configuracion")
public class Configuracion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CONFIGURACION_ID_GENERATOR", sequenceName = "configuracion_id_configuracion_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIGURACION_ID_GENERATOR")
    @Column(name = "id_configuracion", unique = true, nullable = false)
    private Long id;

    private String estado;

    @Column(name = "nombre_variable")
    private String nombre;

    @Column(name="valor_variable", columnDefinition = "text")
    private String valor;

}
