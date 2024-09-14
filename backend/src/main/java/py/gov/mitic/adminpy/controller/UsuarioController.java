package py.gov.mitic.adminpy.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.UsuarioDTO;
import py.gov.mitic.adminpy.service.UsuarioService;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private Log logger = LogFactory.getLog(UsuarioService.class);
    @Autowired
    UsuarioService usuarioService;

    @PreAuthorize("hasAuthority('usuarios:listar')")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idUsuario") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idUsuario,
        @RequestParam(required = false) Long idInstitucion,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String fechaExpiracion,
        @RequestParam(required = false) String institucion,
        @RequestParam(required = false) String roles
    ) {
        return usuarioService.getAll(page, pageSize, sortField, sortAsc, idUsuario, idInstitucion, nombre, apellido, 
        		username, fechaExpiracion, institucion, roles).build();
    }

    @PreAuthorize("hasAuthority({'usuarios:listar'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList() {
        return usuarioService.getList().build();
    }

    @PreAuthorize("hasAuthority('usuarios:crear')")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.create(usuarioDTO).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editar')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return  usuarioService.update(id, dto).build();
    }

//    @PreAuthorize("hasAuthority('usuarios:cambiarClave')")
    @PutMapping(value = "/update-password", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updatePassword(@RequestBody AuthenticationRequest authDto) {
    	return usuarioService.updatePassword(authDto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editarEstado')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return usuarioService.updateStatus(id).build();
    }
    
    @PreAuthorize("hasAuthority('usuarios:editar')")
    @PutMapping(value = "/{id}/update-status",
    			consumes = MediaType.APPLICATION_JSON, 
    			produces = MediaType.APPLICATION_JSON
    			)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioService.updateUserStatus(id, dto).build();
    }
    
    @PreAuthorize("hasAuthority({'usuarios:listar'})")
    @GetMapping(value = "/{idInstitucion}/total", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getTotalByInstitucion(@PathVariable Long idInstitucion, @RequestParam(required = false) Boolean estado) {
    	return usuarioService.getTotal(idInstitucion, estado).build();
    }

    @PostMapping(value = "/reset-pass", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> resetPass(@RequestBody AuthenticationRequest authDto) {
        return usuarioService.resetPass(authDto).build();
    }

//    @PreAuthorize("hasAuthority({'usuarios:editarPerfil'})")
    @PutMapping(value = "/update-profile", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateProfile(@RequestBody UsuarioDTO dto) {
        return usuarioService.updateProfile(dto).build();
    }

//    @PreAuthorize("hasAuthority({'usuarios:usuariosListarOee'})")
    @GetMapping(value = "/list/oee/{idInstitucion}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getUsuariosOeeList(@PathVariable Long idInstitucion) {
        return usuarioService.getUsuariosOeeList(idInstitucion).build();
    }

    @GetMapping(value = "/data/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getUsuarioOee(@PathVariable Long id) {
        return usuarioService.getUsuarioOee(id).build();
    }

}
