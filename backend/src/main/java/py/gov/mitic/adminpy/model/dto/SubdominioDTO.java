package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubdominioDTO {

    private Long idSubdominio;

    private String subdominio;

    private DominioDTO dominio;

    private Boolean estado;

}
