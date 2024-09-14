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
import py.gov.mitic.adminpy.model.dto.SubdominioDTO;
import py.gov.mitic.adminpy.service.SubdominioService;
import py.gov.mitic.adminpy.util.Util;

@RestController
@RequestMapping("/subdominio")
public class SubdominioController {

    @Autowired
    SubdominioService subdominioService;

    @PreAuthorize("hasAuthority({'subdominio:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idSubDominio") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idDominio,
            @RequestParam(required = false) Long idSubDominio,
            @RequestParam(required = false) String subdominio
    ) {
        return subdominioService.getAll(page, pageSize, sortField, sortAsc, idDominio, idSubDominio, subdominio).build();

    }

    @PreAuthorize("hasAuthority({'subdominio:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody SubdominioDTO dto) {
        return subdominioService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'subdominio:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return subdominioService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'subdominio:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody SubdominioDTO dto) {
        return subdominioService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'subdominio:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return subdominioService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOee") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String subdominio,
                               HttpServletResponse response) {

        byte[] reportBytes = subdominioService.generateReport(format, id, "rptSubdominioListado", permiso, subdominio);

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

