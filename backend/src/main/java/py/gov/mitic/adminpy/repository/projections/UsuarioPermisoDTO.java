package py.gov.mitic.adminpy.repository.projections;

/**
 * Proyección DTO basada en interfaz
 * Enfocado a las consultas personalizadas
 **/
public interface UsuarioPermisoDTO {
	
    Long getIdUsuario();
    
    Long getIdRol();
    
    Long getIdPermiso();
    
    String getPermiso();
    
}
