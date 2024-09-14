package py.gov.mitic.adminpy.model.dto;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class UsuarioDTO {

	private Long idUsuario;

	private String username;

	//@JsonIgnore
	private String password;
	
	private String password2;

	private String nombre;

	private String apellido;

	private OeeDTO oee;

	private String fechaExpiracion;

	private List<RolDTO> roles;
	
	private Boolean estado;
	
	private String cargo;
	
	private String direccion;
	
	private String telefono;
	
	private String correo;

	private String cedula;

	private String justificacionAlta;

}