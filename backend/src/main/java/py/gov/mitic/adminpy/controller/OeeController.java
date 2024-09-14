package py.gov.mitic.adminpy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.OeeDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.OeeService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/oee")
public class OeeController {

    private final OeeService oeeService;

    public OeeController(OeeService oeeService) {
        this.oeeService = oeeService;
    }

    @PreAuthorize("hasAuthority({'oee:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String descripcionOee,
        @RequestParam(required = false) String descripcionCorta,
        @RequestParam(required = false) String urlOee
    ) {
        return oeeService.getAll(page, pageSize, sortField, sortAsc, id, descripcionOee, descripcionCorta, urlOee).build();
    }

    @PreAuthorize("hasAuthority({'oee:listar'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList() {

        return oeeService.getList().build();
    }

    /**
     *
     * @param dto	
     * @return
     */
//    @PreAuthorize("hasAuthority({'oee:crear'})")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody OeeDTO dto) {
        return oeeService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'oee:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody OeeDTO dto) {
        return oeeService.update(id, dto).build();
    }
    
    @PreAuthorize("hasAuthority({'oee:borrar'})")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return oeeService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}")
    @PreAuthorize("hasAuthority('oee:downloadReport')")
    public void downloadReport(@PathVariable String format, HttpServletResponse response) {

        byte[] reportBytes = oeeService.generateReport(format, "rpt-oee-listado");

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
