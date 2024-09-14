package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "recuperar_clave")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecuperarClave {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "sec_id_recuperar_clave", sequenceName = "sec_id_recuperar_clave", allocationSize = 1)
    @GeneratedValue(generator = "sec_id_recuperar_clave")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "codigo")
    private String codigo;

    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public RecuperarClave(Long id, String codigo, String username) {
        super();
        this.id = id;
        this.codigo = codigo;
        this.usuario = new Usuario(username);
    }
}
