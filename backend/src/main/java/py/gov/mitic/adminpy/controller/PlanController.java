package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.PlanDTO;
import py.gov.mitic.adminpy.service.PlanService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    PlanService planService;

    @PreAuthorize("hasAuthority({'planes:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idPlan") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idPlan,
        @RequestParam(required = false) String anho,
        @RequestParam(required = false) String fecha,
        @RequestParam(required = false) String linkPlan,
        @RequestParam(required = false) String cantidadFuncionariosTic,
        @RequestParam(required = false) String cantidadFuncionariosAdmin,
        @RequestParam(required = false) String presupuestoTicAnual,
        @RequestParam(required = false) Boolean estado

    ) {
        return planService.getAll(page, pageSize, sortField, sortAsc, idPlan, anho, fecha, linkPlan, cantidadFuncionariosTic, cantidadFuncionariosAdmin, presupuestoTicAnual, estado).build();
    }

    @PreAuthorize("hasAuthority({'planes:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody PlanDTO dto) {
        return planService.save(dto).build();
    }

    @PreAuthorize("hasAuthority({'planes:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody PlanDTO dto) {
        return planService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'planes:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return planService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'planes:borrar'})")
    @DeleteMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return planService.delete(id).build();
    }

    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long id,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idOee") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String anho,
                               @RequestParam String fecha,
                               @RequestParam String linkPlan,
                               @RequestParam String cantidadFuncionariosTic,
                               @RequestParam String presupuestoTicAnual,
                               HttpServletResponse response) {

        byte[] reportBytes = planService.generateReport(format, id, "rptPlanListado", permiso, anho, fecha, linkPlan, cantidadFuncionariosTic, presupuestoTicAnual);

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