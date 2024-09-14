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
@Table(schema = "public", name = "rangoip")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class RangoIp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "RANGO_IP_ID_GENERATOR", sequenceName = "public.rango_ip_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RANGO_IP_ID_GENERATOR")
    @Column(name = "id_rango", unique = true, nullable = false)
    private Long idRango;

    @Column(name = "rango")
    private String rango;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "pertenece_dmz")
    private Boolean perteneceDmz;

    @Column(name = "pertenece_ipnavegacion")
    private Boolean perteneceIpNavegacion;

    @Column(name = "pertenece_vpn")
    private Boolean perteneceVpn;

    public RangoIp(){

    }
}
