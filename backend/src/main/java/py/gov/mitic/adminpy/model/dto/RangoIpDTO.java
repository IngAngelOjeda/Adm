package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RangoIpDTO {

    Long idRango;

    String rango;

    private OeeDTO oee;

    Boolean estado;

    Boolean perteneceDmz;

    Boolean perteneceIpNavegacion;

    Boolean perteneceVpn;

}
