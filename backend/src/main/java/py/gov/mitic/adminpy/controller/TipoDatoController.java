package py.gov.mitic.adminpy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.TipoDatoService;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/tipoDato")
public class TipoDatoController {

    private final TipoDatoService tipoDatoService;

    public TipoDatoController(TipoDatoService tipoDatoService) {
        this.tipoDatoService = tipoDatoService;
    }

    @PreAuthorize("hasAuthority({'tipoDato:obtener'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList() {

        return tipoDatoService.getList().build();
    }

}
