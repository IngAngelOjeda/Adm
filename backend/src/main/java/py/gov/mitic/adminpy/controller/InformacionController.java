package py.gov.mitic.adminpy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.DatosOeeDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.DatosOeeService;
import py.gov.mitic.adminpy.service.InformacionService;
import py.gov.mitic.adminpy.service.ServicioService;
import py.gov.mitic.adminpy.util.Util;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("/informacion")
public class InformacionController {

    private final InformacionService informacionService;
    private final ServicioService servicioService;

    public InformacionController(InformacionService informacionService, ServicioService servicioService){
        this.informacionService = informacionService;
        this.servicioService = servicioService;
    }

//    @PreAuthorize("hasAuthority({'informacion:listar'})")
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
        return informacionService.getAll(page, pageSize, sortField, sortAsc, idOeeInformacion, idTipoDato, id, permiso, descripcionOee, descripcionOeeInformacion, estadoOeeInformacion, descripcionTipoDato ).build();
    }

    //    @PreAuthorize("hasAuthority({'informacion:listar'})")
    @GetMapping(value = "/getAllOee", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAllOee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String descripcionOee,
            @RequestParam(required = false) String descripcionCorta,
            @RequestParam(required = false) String urlOee,
            @RequestParam(required = false) String descripcionOeeInformacion


    ) {
        return informacionService.getAllOee(page, pageSize, sortField, sortAsc, id, permiso, descripcionOee, descripcionCorta, urlOee, descripcionOeeInformacion ).build();
    }

//    @PreAuthorize("hasAuthority({'informacion:editar'})")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@RequestBody List<DatosOeeDTO> dtos) {
        return informacionService.update(dtos).build();
    }

//    @PreAuthorize("hasAuthority({'informacion:editar'})")
    @PutMapping(value = "/{id}/update-all-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return servicioService.updateStatusAllService(id).build();
    }

//    @PreAuthorize("hasAuthority('oee:downloadReport')")
//    @GetMapping("/downloadReport/{format}/{id}")
//    public void downloadReport(@PathVariable String format,
//                               @PathVariable Long id,
//                               @RequestParam(defaultValue = "0") Long page,
//                               @RequestParam(defaultValue = "10") Long pageSize,
//                               @RequestParam(defaultValue = "idOeeInformacion") String sortField,
//                               @RequestParam(defaultValue = "true") boolean sortAsc,
//                               @RequestParam(required = true) Boolean permiso,
//                               @RequestParam String descripcionOeeInformacion,
//                               @RequestParam String descripcionOee,
//                               @RequestParam String descripcionTipoDato,
//                               HttpServletResponse response) {
//
//        byte[] reportBytes = datosOeeService.generateReport(format, id, "rptDatosOeeListado", permiso, descripcionOeeInformacion, descripcionOee, descripcionTipoDato);
//
//        if (reportBytes != null) {
//            response.setContentType(Util.getContentType(format));
//            response.setHeader("Content-Disposition", "attachment; filename=report." + format);
//            response.setContentLength(reportBytes.length);
//
//            try {
//                response.getOutputStream().write(reportBytes);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
