package py.gov.mitic.adminpy.controller;

import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.adminpy.model.dto.PermisoDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.PermisoService;

@RestController
@RequestMapping("/permiso")
public class PermisoController {

    @Autowired
    PermisoService permisoService;

    @PreAuthorize("hasAuthority({'permisos:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idPermiso") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idPermiso,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String descripcion
    ) {
        return permisoService.getAll(page, pageSize, sortField, sortAsc, idPermiso, nombre, descripcion).build();
    }

    @PreAuthorize("hasAuthority({'permisos:listar'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getListPermisos() {
        return permisoService.getPermisos().build();
    }

    @PreAuthorize("hasAuthority({'permisos:crear'})")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody PermisoDTO dto) {
        return permisoService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'permisos:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody PermisoDTO dto) {
        return permisoService.update(id, dto).build();
    }

}
