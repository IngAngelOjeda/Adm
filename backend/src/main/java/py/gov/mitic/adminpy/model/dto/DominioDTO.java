package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;
import py.gov.mitic.adminpy.model.entity.SubDominio;

@Setter
@Getter
public class DominioDTO {

    private Long idDominio;

    private String dominio;

    private OeeDTO oee;

    private Boolean estado;

}
