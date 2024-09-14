package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "funcionario")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class Funcionario implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "FUNCIONARIO_ID_GENERATOR", sequenceName = "public.funcionario_id_funcionario_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FUNCIONARIO_ID_GENERATOR")
    @Column(name = "id_funcionario", unique = true, nullable = false)
    private Long idFuncionario;

    @ManyToOne
    @JoinColumn(name = "id_dependencia")
    private Dependencia dependencia;

    @Column(name = "id_dependencia", updatable = false, insertable = false)
    private Long idDependencia;

    @Column(name="nombre")
    private String nombre;

    @Column(name="apellido")
    private String apellido;

    @Column(name="correo_institucional")
    private String correoInstitucional;

    @Column(name="cedula")
    private String cedula;

    @Column(name="cargo")
    private String cargo;

    @Column(name = "estado_funcionario")
    private Boolean estadoFuncionario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    public Funcionario(){}

}
