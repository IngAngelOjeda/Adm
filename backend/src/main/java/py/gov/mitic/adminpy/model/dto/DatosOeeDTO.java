package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosOeeDTO {
//    private Long idDependencia;
//    private OeeDTO idOee;
//    private String sitioWeb;
//    private String correoElectronico;
//    private String telefono;
//    private String autoridad;
//    private String vision;
//    private String direccion;
//    private String descripcion;
//    private String ubicacion;
//    private String mision;
//    private String responsable;

    //    OEE INFORMACION
    private Long idOeeInformacion;

    private TipoDatoDto tipoDato;

    private OeeDTO oee;

    private String descripcionOeeInformacion;

    private String estadoOeeInformacion;
}
