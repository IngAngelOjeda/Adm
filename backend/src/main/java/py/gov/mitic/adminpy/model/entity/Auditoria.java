package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.dto.OeeDTO;
import py.gov.mitic.adminpy.model.dto.UsuarioDTO;

@Entity
@Setter
@Getter
@AllArgsConstructor
@Table(schema = "public", name = "auditoria")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Auditoria implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "AUDITORIA_ID_GENERATOR", sequenceName = "auditoria_id_auditoria_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDITORIA_ID_GENERATOR")
    @Column(name = "id_auditoria", unique = true, nullable = false)
	private Long idAuditoria;

	@Column(name = "id_registro")
	private String idRegistro;

	@Column(name = "id_oee")
	private Long idOee;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "id_oee", updatable = false, insertable = false)
	private Oee oee;

	@Column(name = "id_usuario")
	private Long idUsuario;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "id_usuario", updatable = false, insertable = false)
	private Usuario usuario;

	@Column(name = "detalle")
	private String valor;

	private String accion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_hora")
	private Date fechaHora;

	@Column(name = "nombre_tabla")
	private String nombreTabla;

	@Column(name = "cliente_nro_ip ")
	private String ipUsuario;

	private String metodo;

	private String modulo;

	@Column(name = "nombre_usuario")
	private String nombreUsuario;

	private String roles;

	@Column(name = "tipo_evento")
	private String tipoEvento;

	private String parametros;

	// Se utilizará para palabra clave a buscar. Ej.: RUC, RUA, CI
	@Column(name = "parametro_clave")
	private String valorClave;
	
	private String motivo;

	@Column(name = "id_servicio")
	private Long idServicio;

	public Auditoria() {
	}

	public Auditoria(Long idAuditoria, Date fechaHora, String nombreUsuario, String metodo, String modulo, String tipoEvento,
			String accion, String nombreTabla, String idRegistro, Long idInstitucion, String nombreInstitucion, String abreviatura, Long idUsuario, String nombre, String apellido, String cedula) {
		super();
		this.idAuditoria = idAuditoria;
		this.idRegistro = idRegistro;
		this.oee = new Oee(idInstitucion, nombreInstitucion, abreviatura);
		this.usuario = new Usuario(idUsuario, nombre, apellido,cedula);
		this.accion = accion;
		this.fechaHora = fechaHora;
		this.nombreTabla = nombreTabla;
		this.metodo = metodo;
		this.modulo = modulo;
		this.nombreUsuario = nombreUsuario;
		this.tipoEvento = tipoEvento;
	}


	public Auditoria(Long idAuditoria, String valor) {
		super();
		this.idAuditoria = idAuditoria;
		this.valor = valor;
	}
	
	public Auditoria(String valor) {
		super();
		this.valor = valor;
	}
	
	public Auditoria(String valor, String accion) {
		super();
		this.valor = valor;
		this.accion = accion;
	}

	@JsonIgnore
	public String getFechaHoraFormateada() {
		return this.getFechaHora() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.getFechaHora())
				: "";
	}

//	@JsonProperty("username")
//	public String getUsername() {
//		return this.usuario != null ? this.usuario.getUsername() : null;
//	}

	@JsonProperty("usuario")
	public String getNombre() {
		return this.usuario!=null?this.usuario.getNombre()+" "+this.usuario.getApellido():null;
	}

	@JsonProperty("cedula")
	public String getCedula() {
		return this.usuario!=null?this.usuario.getCedula():null;
	}

	@JsonProperty("institucion")
	public String getInstitucion() {
		return this.oee != null ? this.oee.getDescripcionOee() : null;
	}


}
