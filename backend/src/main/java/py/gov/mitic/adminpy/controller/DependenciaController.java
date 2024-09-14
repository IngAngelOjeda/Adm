package py.gov.mitic.adminpy.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.DependenciaDTO;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.service.DependenciaService;
import py.gov.mitic.adminpy.model.entity.Dependencia;
import py.gov.mitic.adminpy.repository.DependenciaRepository;
import py.gov.mitic.adminpy.util.Util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/dependencia")
public class DependenciaController {

    private final DependenciaService dependenciaService;
    private final DependenciaRepository dependenciaRepository;

    @Autowired
    public DependenciaController(DependenciaService dependenciaService, DependenciaRepository dependenciaRepository) {
        this.dependenciaService = dependenciaService;
        this.dependenciaRepository = dependenciaRepository;
    }

    @PreAuthorize("hasAuthority({'dependencia:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idDependencia") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idDependencia,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String descripcionDependencia,
            @RequestParam(required = false) String descripcionOee
    ) {
        return dependenciaService.getAll(page, pageSize, sortField, sortAsc, idDependencia, id, permiso, codigo, descripcionDependencia, descripcionOee).build();
    }

    @PreAuthorize("hasAuthority({'dependencia:obtenerPorParametros'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList(@RequestParam(required = true) Long idOee) {
        return dependenciaService.getListByOee(idOee);
    }

    @PreAuthorize("hasAuthority({'dependencia:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody DependenciaDTO dto) {
        return dependenciaService.create(dto).build();
    }

    @PreAuthorize("hasAuthority({'dependencia:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody DependenciaDTO dto) {
        return dependenciaService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'dependencia:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return dependenciaService.updateStatus(id).build();
    }

//    @PreAuthorize("hasAuthority({'dependencia:mostrarOrganigrama'})")
    @GetMapping(value = "/showDependencia", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(@RequestParam(required = true) Long idOee) {
        return dependenciaService.getAllByOee(idOee);
    }

    @GetMapping("/downloadReport/{format}/{idOee}")
//    @PreAuthorize("hasAuthority('depencia:downloadReport')")
    public void downloadReport(@PathVariable String format,
                               @PathVariable Long idOee,
                               @RequestParam(defaultValue = "0") Long page,
                               @RequestParam(defaultValue = "10") Long pageSize,
                               @RequestParam(defaultValue = "idDependencia") String sortField,
                               @RequestParam(defaultValue = "true") boolean sortAsc,
                               @RequestParam(required = true) Boolean permiso,
                               @RequestParam String codigo,
                               @RequestParam String descripcionDependencia,
                               @RequestParam String descripcionOee,
                               HttpServletResponse response) {

        byte[] reportBytes = dependenciaService.generateReport(format, idOee,"rptDependenciasListado", permiso, codigo, descripcionDependencia, descripcionOee);

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

    @PreAuthorize("hasAuthority({'dependencia:listar'})")
    @GetMapping(value = "/getAllDependencia", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAllDependencia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idDependencia") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idDependencia,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String descripcionDependencia,
            @RequestParam(required = false) String descripcionOee
    ) {
        return dependenciaService.getAllDependencia(page, pageSize, sortField, sortAsc, idDependencia, id, permiso, codigo, descripcionDependencia, descripcionOee).build();
    }


    // #### TreeNode
    //@PreAuthorize("hasAuthority({'dependencia:obtenerPorParametros'})")
//    @GetMapping(value = "/showDependencia", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
//    public ResponseEntity<List<Dependencia.TreeNode>> getAllDependencias(@RequestParam(required = true) Long idOee) {
//
//        if (idOee == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        Iterable<Dependencia> dependenciasIterable = dependenciaRepository.findByOeeId(idOee);
//        List<Dependencia> dependencias = StreamSupport
//                .stream(dependenciasIterable.spliterator(), false)
//                .collect(Collectors.toList());
//
//        List<Dependencia.TreeNode> treeNodes = dependencias.stream()
//                .map(Dependencia.TreeNode::fromDependencia)
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(treeNodes, HttpStatus.OK);
//    }

}
