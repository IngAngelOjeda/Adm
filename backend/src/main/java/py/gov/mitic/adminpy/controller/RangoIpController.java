package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.DominioDTO;
import py.gov.mitic.adminpy.model.dto.RangoIpDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.SubdominioDTO;
import py.gov.mitic.adminpy.model.entity.RangoIp;
import py.gov.mitic.adminpy.service.RangoIpService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("rangoip")
public class RangoIpController {

    @Autowired
    RangoIpService rangoIpService;

    @PreAuthorize("hasAuthority({'rangoip:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idRango") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idRango,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String rango,
            @RequestParam(required = false) String descripcionOee,
            @RequestParam(required = false) Boolean perteneceDmz,
            @RequestParam(required = false) Boolean perteneceIpNavegacion,
            @RequestParam(required = false) Boolean perteneceVpn
    ) {
        return rangoIpService.getAll(page, pageSize, sortField, sortAsc, idRango, id, permiso, rango, descripcionOee, perteneceDmz, perteneceIpNavegacion, perteneceVpn).build();

    }

    @PreAuthorize("hasAuthority({'rangoip:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody RangoIp dto) {
        return rangoIpService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'rangoip:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody RangoIpDTO dto) {
        return rangoIpService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'rangoip:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return rangoIpService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'rangoip:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return rangoIpService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOee") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String rango,
                               @RequestParam String descripcionOee,
                               @RequestParam String perteneceDmz,
                               @RequestParam String perteneceVpn,
                               HttpServletResponse response) {

        byte[] reportBytes = rangoIpService.generateReport(format, id, "rptRangoListado", permiso, rango, descripcionOee, perteneceDmz, perteneceVpn);

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
