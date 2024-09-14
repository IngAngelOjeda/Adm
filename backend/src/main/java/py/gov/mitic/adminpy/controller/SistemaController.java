package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import py.gov.mitic.adminpy.model.dto.UsuarioDTO;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.SistemaDTO;
import py.gov.mitic.adminpy.service.SistemaService;
import py.gov.mitic.adminpy.service.UsuarioService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/sistema")

public class SistemaController {

    @Autowired
    SistemaService sistemaService;

    @PreAuthorize("hasAuthority('sistemas:listar')")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idSistema") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idSistema,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) Long idInstitucion,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String objetoProposito,
            @RequestParam(required = false) String areaResponsable,
            @RequestParam(required = false) String descripcionOee

    ) {
        return sistemaService.getAll(page, pageSize, sortField, sortAsc, idSistema, id, permiso, idInstitucion, nombre, objetoProposito, areaResponsable, descripcionOee).build();
    }

    @PreAuthorize("hasAuthority('sistemas:crear')")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody SistemaDTO sistemaDTO) {
        return sistemaService.create(sistemaDTO).build();
    }

    @PreAuthorize("hasAuthority('sistemas:editar')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody SistemaDTO dto) {
        return sistemaService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'sistemas:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return sistemaService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'sistemas:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return sistemaService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOee") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String nombre,
                               @RequestParam String objetoProposito,
                               @RequestParam String areaResponsable,
                               @RequestParam String descripcionOee,
                               HttpServletResponse response) {

        byte[] reportBytes = sistemaService.generateReport(format, id, "rptSistemasListadoL", permiso, nombre, objetoProposito, areaResponsable,descripcionOee);

        if (reportBytes != null) {
            response.setContentType(Util.getContentType(format));
            response.setHeader("Content-Disposition", "attachment; filename=report." + format);
            response.setContentLength(reportBytes.length);

            try {
                response.getOutputStream().write(reportBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}