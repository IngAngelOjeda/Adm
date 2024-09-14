package py.gov.mitic.adminpy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.ServicioDTO;
import py.gov.mitic.adminpy.service.ServicioService;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/servicio")
public class ServicioController {

    @Autowired
    ServicioService servicioService;

    @PreAuthorize("hasAuthority({'servicioOee:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces =  MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue =  "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idServicio") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) Long idServicio,
            @RequestParam(required = false) String nombreServicio,
            @RequestParam(required = false) String descripcionServicio,
            @RequestParam(required = false) String descripcionOee

    ){
        return servicioService.getAll(page, pageSize,sortField, sortAsc, id, permiso, idServicio, nombreServicio, descripcionServicio, descripcionOee).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:crear'})")
    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody ServicioDTO dto){
        return  servicioService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody ServicioDTO dto){
        return servicioService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'servicioOee:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id){
        return  servicioService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'oee:servicio:confirmarServicios'})")
    @PutMapping(value = "/{id}/confirm-all-services", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> confirmServices(@PathVariable Long id){
        return  servicioService.confirmAllServices(id).build();
    }

}
