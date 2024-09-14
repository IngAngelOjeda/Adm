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
@Table(schema = "public", name = "usuario")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "USUARIO_ID_GENERATOR", sequenceName = "public.usuario_usuario_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_ID_GENERATOR")
	@Column(name = "id", unique = true, nullable = false)
	private Long idUsuario;

	private String nombre;

	private String apellido;

	private String username;

	@Column(name = "cedula", nullable = false)
	private String cedula;

	@Column(name = "url_justificacion_alta")
	private String justificacionAlta;

	@JsonIgnore()
	private String password;

	@JsonIgnore()
	@Column(name = "email", nullable = false)
	private String correo;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Asuncion")
	@Column(name = "fecha_expiracion", nullable = false)
	private Date fechaExpiracion;

	@ManyToMany(targetEntity = Rol.class, cascade =  { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY )
	@JoinTable(name = "rol_usuario", schema = "public",
			joinColumns = { @JoinColumn(name = "usuario_id")},
			inverseJoinColumns = { @JoinColumn(name = "rol_id") })
	public List<Rol> roles;

	@ManyToOne(fetch=javax.persistence.FetchType.LAZY)
	@JoinColumn(name = "id_oee")
	private Oee oee;

	@Column(name = "id_oee", updatable = false, insertable = false)
	private Long idOee;

	@Column(name = "activo", nullable = false)
	private Boolean estado;

	private String cargo;

	private String telefono;

	@Column(name = "token_reset")
	private String tokenReset;

	public Usuario() {
	}

	public Usuario(Long idUsuario) {

		this.idUsuario = idUsuario;
	}

	public Usuario(String username) {

		this.username = username;
	}

	public Usuario(String username, String nombre, String apellido) {
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public Usuario(Long idUsuario, String nombre, String apellido, String cedula) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.cedula = cedula;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Date fechaExpiracion, Boolean estado) {
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaExpiracion = fechaExpiracion;
		this.estado = estado;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Boolean estado) {
		super();
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.estado = estado;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Boolean estado, String telefono, String cargo, Long idInstitucion, String nombreInstitucion, String abreviatura) {
		this.idUsuario = idUsuario;
		this.apellido = apellido;
		this.nombre = nombre;
		this.username = username;
		this.estado = estado;
		this.telefono = telefono;
		this.cargo = cargo;
		this.oee = new Oee(idInstitucion, nombreInstitucion, abreviatura);
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Boolean estado, List<Rol> roles) {
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.estado = estado;
		this.roles = roles;
	}

	public Usuario(Long idUsuario, Boolean estado, String nombre, String username, List<Rol> roles) {
		this.idUsuario = idUsuario;
		this.estado = estado;
		this.nombre = nombre;
		this.username = username;
		this.roles = roles;
	}

	public Usuario(Long idUsuario, String nombre, String apellido, String username, String correo, Boolean estado,
				   Date fechaExpiracion, List<Rol> roles, Long idInstitucion, String nombreInstitucion, String abreviatura) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.username = username;
		this.correo = correo;
		this.estado = estado;
		this.fechaExpiracion = fechaExpiracion;
		this.roles = roles;
		this.oee = new Oee(idInstitucion, nombreInstitucion, abreviatura);
	}

	public Usuario(Long idUsuario, String nombre, String apellido, String username, String correo, Boolean estado,
				   Date fechaExpiracion, List<Rol> roles, Oee oee, String cargo,
				   String telefono,String cedula,String justificacionAlta,Long idOee) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.username = username;
		this.correo = correo;
		this.estado = estado;
		this.fechaExpiracion = fechaExpiracion;
		this.roles = roles;
		this.oee = oee;
		this.cargo = cargo;
		this.telefono = telefono;
		this.cedula = cedula;
		this.justificacionAlta = justificacionAlta;
		this.idOee = idOee;
	}

	public void addRol(Rol rol) {
		if(rol != null) {
			getRoles().add(rol);
			rol.getUsuarios().add(this);
		}
	}

	public void removeRol(Rol rol) {
		getRoles().remove(rol);
	}

}