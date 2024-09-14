package py.gov.mitic.adminpy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.RequisitoDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.ServicioInformacionDTO;
import py.gov.mitic.adminpy.model.dto.SubdominioDTO;
import py.gov.mitic.adminpy.service.ServicioInformacionService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/servicioInformacion")
public class ServicioInformacionController {

    private final ServicioInformacionService servicioInformacionService;

    public ServicioInformacionController(ServicioInformacionService servicioInformacionService) {
        this.servicioInformacionService = servicioInformacionService;
    }

    @PreAuthorize("hasAuthority({'servicioOee:servicioInformacion:obtener'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idServicioInformacion") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idServicio,
            @RequestParam(required = false) Long idServicioInformacion,
            @RequestParam(required = false) String descripcionServicioInformacion,
            @RequestParam(required = false) String descripcionTipoDato
    ) {
        return servicioInformacionService.getAll(page, pageSize, sortField, sortAsc, idServicio, idServicioInformacion, descripcionServicioInformacion, descripcionTipoDato).build();

    }
    @PreAuthorize("hasAuthority({'servicioOee:servicioInformacion:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody ServicioInformacionDTO dto) {
        return servicioInformacionService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:servicioInformacion:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return servicioInformacionService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:servicioInformacion:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody ServicioInformacionDTO dto) {
        return servicioInformacionService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:servicioInformacion:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return servicioInformacionService.delete(id).build();
    }

}
