package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.dto.RolDTO;

/**
 * The persistent class for the rol database table.
 */
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Rol implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ROL_ID_GENERATOR", sequenceName = "public.rol_rol_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROL_ID_GENERATOR")
	@Column(name = "id", unique = true, nullable = false)
	private Long idRol;

	@Column(name="activo", length = 100)
	private Boolean estado;

	@Column(length = 100)
	private String descripcion;

	private String nombre;

	// bi-directional many-to-many association to Permiso
	@ManyToMany(targetEntity = Permiso.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "rol_permiso", schema = "public",
			joinColumns = { @JoinColumn(name = "rol_id")},
			inverseJoinColumns = {@JoinColumn(name = "permiso_id")})
	private List<Permiso> permisos;

	@OneToMany(mappedBy = "roles", cascade = { CascadeType.ALL })
	@JsonIgnore
	private List<Usuario> usuarios = new ArrayList<>();

	public List<Permiso> getPermisos() {
		return permisos;
	}

	public void setPermisos(List<Permiso> permisos) {
		this.permisos = permisos;
	}

	public Rol() {
	}

	public Rol(Long idRol) {
		super();
		this.idRol = idRol;
	}

	public Rol(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Rol(Long idRol, String nombre) {
		super();
		this.idRol = idRol;
		this.nombre = nombre;
	}
	public Rol(Long idRol, String nombre, Boolean estado) {
		super();
		this.idRol = idRol;
		this.nombre = nombre;
		this.estado = estado;
	}

	public Rol(RolDTO rol) {
		super();
		this.idRol = rol.getId();
		this.nombre = rol.getNombre();
		this.descripcion = rol.getDescripcion();
	}

	public Rol(Long idRol, String descripcion, String nombre) {
		super();
		this.idRol = idRol;
		this.descripcion = descripcion;
		this.nombre = nombre;
	}

	public Rol(Long idRol, String descripcion, String nombre, Boolean estado) {
		super();
		this.idRol = idRol;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.estado = estado;
	}

	public Rol(Long idRol, String descripcion, String nombre, Boolean estado, List<Permiso> permisos) {
		super();
		this.idRol = idRol;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.estado = estado;
		this.permisos = permisos;
	}

	public Rol(Long idRol, Boolean estado, String descripcion) {
		super();
		this.idRol = idRol;
		this.descripcion = descripcion;
		this.estado = estado;
	}

	public void add(Permiso permiso) {
		if(permiso != null) {
			getPermisos().add(permiso);
			//permiso.add(this);
		}
	}

	public void removePermiso(Permiso permiso) {
		getPermisos().remove(permiso);
	}

	@Override
	public String toString() {
		return "Rol{" +
				"idRol=" + idRol +
				", estado=" + estado +
				", descripcion='" + descripcion + '\'' +
				", nombre='" + nombre + '\'' +
				'}';
	}
}