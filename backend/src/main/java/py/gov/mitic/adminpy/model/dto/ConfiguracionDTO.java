package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracionDTO {

    private Long id;

    private String estado;

    private String nombre;

    private String valor;
}
