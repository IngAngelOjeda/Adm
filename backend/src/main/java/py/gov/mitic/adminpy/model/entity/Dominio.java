package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.dto.DominioDTO;


@Entity
@Table(schema = "public", name = "dominio")
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Dominio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DOMINIO_ID_GENERATOR", sequenceName = "public.dominio_id_dominio_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOMINIO_ID_GENERATOR")
    @Column(name = "id_dominio", unique = true, nullable = false)
    private Long idDominio;

    @Column(name="dominio")
    private String dominio;

    @Column(name="estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    public Dominio() {}



}
