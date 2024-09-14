package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.DatosOeeDTO;
import py.gov.mitic.adminpy.model.dto.DominioDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.DatosOeeService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/datosOee")
public class DatosOeeController {

    private final DatosOeeService datosOeeService;

    public DatosOeeController(DatosOeeService datosOeeService){
        this.datosOeeService = datosOeeService;
    }

    @PreAuthorize("hasAuthority({'datosOee:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idOeeInformacion") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idOeeInformacion,
            @RequestParam(required = false) Long idTipoDato,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String descripcionOee,
            @RequestParam(required = false) String descripcionOeeInformacion,
            @RequestParam(required = false) String estadoOeeInformacion,
            @RequestParam(required = false) String descripcionTipoDato

    ) {
        return datosOeeService.getAll(page, pageSize, sortField, sortAsc, idOeeInformacion, idTipoDato, id, permiso, descripcionOee, descripcionOeeInformacion, estadoOeeInformacion, descripcionTipoDato ).build();
    }

    @PreAuthorize("hasAuthority({'datosOee:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody DatosOeeDTO dto) {
        return datosOeeService.update(id, dto).build();
    }

//    @PreAuthorize("hasAuthority('oee:downloadReport')")
    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOeeInformacion") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String descripcionOeeInformacion,
                               @RequestParam String descripcionOee,
                               @RequestParam String descripcionTipoDato,
                               HttpServletResponse response) {

        byte[] reportBytes = datosOeeService.generateReport(format, id, "rptDatosOeeListado", permiso, descripcionOeeInformacion, descripcionOee, descripcionTipoDato);

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
