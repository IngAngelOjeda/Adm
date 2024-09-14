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
import py.gov.mitic.adminpy.model.dto.FuncionarioDTO;
import py.gov.mitic.adminpy.model.entity.Funcionario;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.repository.FuncionarioRepository;
import py.gov.mitic.adminpy.service.DependenciaService;
import py.gov.mitic.adminpy.service.FuncionarioService;
import py.gov.mitic.adminpy.service.OeeService;
import py.gov.mitic.adminpy.util.Util;

import java.util.List;

@RestController
@RequestMapping("/organigrama")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final OeeService oeeService;
    private final DependenciaService dependenciaService;
    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioService funcionarioService, OeeService oeeService, FuncionarioRepository funcionarioRepository, DependenciaService dependenciaService) {
        this.funcionarioService = funcionarioService;
        this.oeeService = oeeService;
        this.funcionarioRepository = funcionarioRepository;
        this.dependenciaService = dependenciaService;
    }

    @PreAuthorize("hasAuthority({'organigrama:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idFuncionario") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAsc,
            @RequestParam(required = false) Long idFuncionario,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) Boolean permiso,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String correoInstitucional,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String descripcionDependencia,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String descripcionOee

    ) {
        return funcionarioService.getAll(
                                            page,
                                            pageSize,
                                            sortField,
                                            sortAsc,
                                            idFuncionario,
                                            id,
                                            permiso,
                                            nombre,
                                            apellido,
                                            correoInstitucional,
                                            cedula,
                                            descripcionDependencia,
                                            cargo,
                                            descripcionOee).build();
    }

    @PreAuthorize("hasAuthority({'dependencia:obtenerPorParametros'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getList(@RequestParam(required = true) Long idOee) {
        return dependenciaService.getListByOee(idOee);
    }

        @PreAuthorize("hasAuthority({'organigrama:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> create(@RequestBody FuncionarioDTO dto) {
        return funcionarioService.create(dto).build();
    }

        @PreAuthorize("hasAuthority({'organigrama:editar'})")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody FuncionarioDTO dto) {
        return funcionarioService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'organigrama:editar'})")
    @PutMapping(value = "/{id}/update-status", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return funcionarioService.updateStatus(id).build();
    }

//    @PreAuthorize("hasAuthority({'organigrama:mostrarOrganigramaFuncionario'})")
    @GetMapping(value = "/showOrganigramaFuncionario", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<ResponseDTO> getAll(@RequestParam(required = true) Long idOee) {
        return funcionarioService.getAllByOeeFuncionario(idOee);
    }

//    @PreAuthorize("hasAuthority('organigrama:downloadReport')")
    @GetMapping("/downloadReport/{format}/{id}")
    public void downloadReport(
                                @PathVariable String format,
                                @PathVariable Long id,
                                @RequestParam(defaultValue = "0") Long page,
                                @RequestParam(defaultValue = "10") Long pageSize,
                                @RequestParam(defaultValue = "idFuncionario") String sortField,
                                @RequestParam(defaultValue = "true") boolean sortAsc,
                                @RequestParam(required = true) Boolean permiso,
                                @RequestParam(required = false) String apellido,
                                @RequestParam(required = false) String cargo,
                                @RequestParam(required = false) String cedula,
                                @RequestParam(required = false) String correoInstitucional,
                                @RequestParam(required = false) String nombre,
                                @RequestParam(required = false) String descripcionDependencia,
                                @RequestParam(required = false) String descripcionOee,
                                HttpServletResponse response) {

        byte[] reportBytes = funcionarioService.generateReport(format, id, "rptFuncionarioListadoH", page, pageSize, sortField, sortAsc, permiso, apellido, cargo, cedula, correoInstitucional, nombre, descripcionDependencia, descripcionOee);

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

//    @PreAuthorize("hasAuthority('organigrama:downloadReport')")
//    @GetMapping(value = "/downloadReport", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
//    public ResponseEntity<List<Funcionario>> buscarFuncionarios(
//            @RequestParam(required = false) String nombre,
//            @RequestParam(required = false) Long id,
//            @RequestParam(required = false) String apellido,
//            @RequestParam(required = false) String correoInstitucional,
//            @RequestParam(required = false) String cedula,
//            @RequestParam(required = false) String descripcionDependencia,
//            @RequestParam(required = false) String cargo,
//            @RequestParam(required = false) String descripcionOee){
//
//        List<Funcionario> funcionarios = funcionarioService.findFuncionarios(nombre, id, apellido, correoInstitucional, cedula, descripcionDependencia, cargo, descripcionOee);
//        return ResponseEntity.ok(funcionarios);
//    }

}
