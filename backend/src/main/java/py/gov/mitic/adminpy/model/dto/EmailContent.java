package py.gov.mitic.adminpy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmailContent {

    public String titulo;
    public String institucion;
    public String nombreUsuario;
    public String linkAction1;
    public String linkAction2;
    public String linkAction3;
    public String claveGenerada;
    public String host;
    public String nombre;
    public String apellido;
    public String email;
    public String numeroDocumento;
    public String urlSistema;

}
