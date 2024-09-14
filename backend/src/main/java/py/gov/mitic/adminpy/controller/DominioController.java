package py.gov.mitic.adminpy.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.DominioDTO;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.service.DominioService;
import py.gov.mitic.adminpy.util.Util;

@RestController
@RequestMapping("/dominio")
public class DominioController {

    @Autowired
    DominioService dominioService;

    @PreAuthorize("hasAuthority({'dominio:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idDominio") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idDominio,
        @RequestParam(required = true) Long id,
        @RequestParam(required = true) Boolean permiso,
        @RequestParam(required = false) String dominio,
        @RequestParam(required = false) String descripcionOee
    ) {
        return dominioService.getAll(page, pageSize, sortField, sortAsc, idDominio,id, permiso, dominio, descripcionOee).build();
    }

    @PreAuthorize("hasAuthority({'dominio:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody DominioDTO dto) {
        return dominioService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'dominio:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return dominioService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'dominio:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody DominioDTO dto) {
        return dominioService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'dominio:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return dominioService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOee") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String dominio,
                               @RequestParam String descripcionOee,
                               HttpServletResponse response) {

        byte[] reportBytes = dominioService.generateReport(format, id, "rptDominioListado", permiso, dominio,descripcionOee);

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
