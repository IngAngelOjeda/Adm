package py.gov.mitic.adminpy.repository.projections;

import java.util.List;
/**
 * Proyección DTO basada en interfaz
 * Enfocado a las consultas personalizadas
 **/
public interface UsuarioListDTO {

    Long getIdUsuario();
    
    String getUsername();
    
    String getNombre();
    
    String getApellido();

    List<Roles> getRoles();

    interface Roles {
        String getNombre();
    }

}
