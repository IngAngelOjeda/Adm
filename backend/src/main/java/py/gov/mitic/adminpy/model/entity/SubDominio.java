package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(schema = "public", name = "subdominio")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class SubDominio implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SUBDOMINIO_ID_GENERATOR", sequenceName = "public.subdominio_id_subdominio_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUBDOMINIO_ID_GENERATOR")
    @Column(name = "id_subdominio", unique = true, nullable = false)
    private Long idSubDominio;

    @Column(name = "subdominio")
    private String subdominio;

    @ManyToOne
    @JoinColumn(name = "id_dominio")
    private Dominio dominio;

    @Column(name = "estado")
    private Boolean estado;

    public SubDominio() {

    }
}

