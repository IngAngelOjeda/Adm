package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.service.RecuperarClaveService;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/recuperar_clave")
public class RecuperarClaveController {

    @Autowired
    RecuperarClaveService service;

    @GetMapping(value = "/{code}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getCodeByUser(@PathVariable String code) {
        return service.getCodeByUser(code).build();
    }

    @PutMapping(value = "/{code}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updatePassword(@PathVariable String code, @RequestBody AuthenticationRequest authDto) {
        return service.updatePassword(code, authDto).build();
    }
}
