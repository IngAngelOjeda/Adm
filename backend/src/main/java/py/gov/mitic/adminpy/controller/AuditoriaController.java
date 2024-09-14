package py.gov.mitic.adminpy.controller;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.AuditoriaService;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
	
	private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority({'auditoria:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idAuditoria") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String nombreUsuario,
        @RequestParam(required = false) String metodo,
        @RequestParam(required = false) String modulo,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) String rangoFecha,
        @RequestParam(required = false) Long idOee,
        @RequestParam(required = false) Long idUsuario,
        @RequestParam(required = false) String descripcionOee,
        @RequestParam(required = false) String nombreTabla
    ) {
        return service.getAll(page, pageSize, sortField, sortAsc, id, nombreUsuario, metodo, modulo, accion, rangoFecha, idOee, idUsuario, descripcionOee, nombreTabla).build();
    }

}
