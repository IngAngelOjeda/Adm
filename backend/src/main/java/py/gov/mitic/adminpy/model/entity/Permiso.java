package py.gov.mitic.adminpy.model.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the permiso database table.
 */
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Permiso implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "PERMISO_ID_GENERATOR", sequenceName = "public.permiso_permiso_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISO_ID_GENERATOR")
	@Column(name = "id", unique = true, nullable = false)
	private Long idPermiso;

	private String nombre;

	private String descripcion;

	public Permiso() {
	}

	public Permiso(Long idPermiso, String nombre) {
		super();
		this.idPermiso = idPermiso;
		this.nombre = nombre;
	}

	public Permiso(Long idPermiso, String nombre, String descripcion) {
		super();
		this.idPermiso = idPermiso;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("idPermiso", this.idPermiso);
		map.put("nombre", this.nombre);
		map.put("descripcion", this.descripcion);
		return map;
	}

	public Permiso(String nombre) {
		super();
		this.nombre = nombre;
	}


}