package py.gov.mitic.adminpy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.ClasificadorDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.ClasificadorService;
import py.gov.mitic.adminpy.util.Util;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/clasificador")
public class ClasificadorController {

    private final ClasificadorService clasificadorService;

    public ClasificadorController(ClasificadorService clasificadorService) {
        this.clasificadorService = clasificadorService;
    }

    @PreAuthorize("hasAuthority({'clasificador:obtenerPorParametros'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList() {

        return clasificadorService.getList().build();
    }

    @PreAuthorize("hasAuthority({'clasificador:obtenerPorParametros'})")
    @GetMapping(value = "/{id}/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id){
        return clasificadorService.getListbyId(id).build();
    }
}
