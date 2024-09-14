package py.gov.mitic.adminpy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioDatoBasicoDTO {
	
	private Long id;

    private String nombre;

    private String apellido;

    private String cedula;

    private String cargo;

    private String correo;

}
