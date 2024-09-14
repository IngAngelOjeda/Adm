package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Funcionario;
import py.gov.mitic.adminpy.model.entity.Dependencia;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.FuncionarioReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

@Service
public class FuncionarioService extends GenericSpecification<Funcionario>{

    private Log logger = LogFactory.getLog(FuncionarioService.class);
    private Map<String, Object> auditMap = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();
    @Value("${spring.application.url}") String BASE_URL;
    private final FuncionarioRepository funcionarioRepository;
    private final DependenciaRepository dependenciaRepository;
    private final OeeRepository oeeRepository;
    private final AuditoriaService auditoriaService;
    private final ReportService reportService;



    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository, DependenciaRepository dependenciaRepository, OeeRepository oeeRepository, AuditoriaService auditoriaService, ReportService reportService ) {
        this.funcionarioRepository = funcionarioRepository;
        this.dependenciaRepository = dependenciaRepository;
        this.oeeRepository = oeeRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;

    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idFuncionario, Long id, Boolean permiso, String nombre, String apellido, String correoInstitucional, String cedula, String descripcionDependencia, String cargo, String descripcionOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0 ? true:false;

        Specification<Funcionario> specification = (root, query, cb) -> {
            Join<Funcionario, Dependencia> dependenciaJoin = root.join("dependencia", JoinType.LEFT);
            Join<Dependencia, Oee> oeeJoin = dependenciaJoin.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idFuncionario"), ((Objects.nonNull(idFuncionario) && idFuncionario > 0) ? idFuncionario.toString() : "")),
                    like(cb, root.get("nombre"), (!Objects.isNull(nombre) ? nombre : "")),
                    like(cb, root.get("apellido"), (!Objects.isNull(apellido) ? apellido : "")),
                    like(cb, root.get("correoInstitucional"), (!Objects.isNull(correoInstitucional) ? correoInstitucional : "")),
                    like(cb, root.get("cedula"), (!Objects.isNull(cedula) ? cedula : "")),
                    like(cb, root.get("cargo"), (!Objects.isNull(cargo) ? cargo : "")),
                    like(cb, dependenciaJoin.get("descripcionDependencia"), (!Objects.isNull(descripcionDependencia) ? descripcionDependencia : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };

        Page<Funcionario> pageList = funcionarioRepository.findAll(specification, paging);

        TableDTO<Funcionario> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

//		auditMap = new HashMap<>();
//      auditMap.put("paging", paging.toString());
//		auditMap.put("page", (int) pageList.getNumber() +1);
//		auditMap.put("totalRecords", (int) pageList.getTotalElements());
//		while (auditMap.values().remove(null));
//		while (auditMap.values().remove(""));
//		params = new HashMap<>();
//		params.put("idFuncionario", idFuncionario);
//		params.put("nombre", nombre);
//		params.put("apellido", apellido);
//		params.put("correoInstitucional", correoInstitucional);
//		params.put("cedula", cedula);
//		params.put("cargo", cargo);
//		params.put("descripcionDependencia", descripcionDependencia);
//		params.put("descripcionOee", descripcionOee);
//		while (params.values().remove(null));
//		while (params.values().remove(""));
//		try {
//			auditoriaService.auditar("LISTAR", "S/I",
//					auditMap, null, "organigrama", "/getAll/{params}", "/organigrama", "organigrama/", null, null, null, params);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO create(FuncionarioDTO dto) {
        try {
            // Validar DTO
            if (Objects.isNull(dto.getDependencia()) || Objects.isNull(dto.getDependencia().getIdDependencia())) {
                logger.info("No se detectó ninguna dependencia asociada al funcionario");
                return new ResponseDTO("No se detectó ninguna dependencia asociada al funcionario", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(dto.getNombre()) || dto.getNombre().isEmpty()) {
                logger.info("El nombre es requerido");
                return new ResponseDTO("El nombre es requerido", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(dto.getCargo()) || dto.getCargo().isEmpty()) {
                logger.info("El cargo es requerido");
                return new ResponseDTO("El cargo es requerido", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(dto.getCedula()) || dto.getCedula().isEmpty()) {
                logger.info("La cédula es requerida");
                return new ResponseDTO("La cédula es requerida", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(dto.getCorreoInstitucional()) || dto.getCorreoInstitucional().isEmpty()) {
                logger.info("El correo es requerido");
                return new ResponseDTO("El correo es requerido", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(dto.getApellido()) || dto.getApellido().isEmpty()) {
                logger.info("El apellido es requerido");
                return new ResponseDTO("El apellido es requerido", HttpStatus.BAD_REQUEST);
            }

            // Crear funcionario
            Funcionario funcionario = new Funcionario();
            funcionario.setNombre(dto.getNombre());
            Dependencia dependencia = dependenciaRepository.findById(dto.getDependencia().getIdDependencia())
                    .orElseThrow(() -> new NoSuchElementException("Dependencia no encontrada con id: " + dto.getDependencia().getIdDependencia()));
            funcionario.setDependencia(dependencia);
            funcionario.setEstadoFuncionario(true);
            funcionario.setFechaCreacion(new Date());
            funcionario.setApellido(dto.getApellido());
            funcionario.setCargo(dto.getCargo());
            funcionario.setCedula(dto.getCedula());
            funcionario.setCorreoInstitucional(dto.getCorreoInstitucional());

            funcionarioRepository.save(funcionario);

            //Auditoria
            auditoriaService.auditar("CREAR",
            Objects.nonNull(funcionario) ? funcionario.getIdFuncionario().toString():null,
            Objects.nonNull(dto) ? dto:null,
            null, "funcionario",
            "/create", "/create",
            "funcionario/create", null,
            null, null, null);

            return new ResponseDTO("Funcionario creado con éxito", HttpStatus.OK);
        } catch (BadRequestException | NoSuchElementException e) {
            logger.info(e.getMessage());
            return new ResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud", e);
            return new ResponseDTO("Error al procesar la solicitud", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseDTO update(Long id, FuncionarioDTO dto) {

        // Validar DTO
        if (Objects.isNull(dto.getNombre()) || dto.getNombre().isEmpty()) {
            logger.info("El nombre es requerido");
            return new ResponseDTO("El nombre es requerido", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getApellido()) || dto.getApellido().isEmpty()) {
            logger.info("El apellido es requerido");
            return new ResponseDTO("El apellido es requerido", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getCargo()) || dto.getCargo().isEmpty()) {
            logger.info("El cargo es requerido");
            return new ResponseDTO("El cargo es requerido", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getCorreoInstitucional()) || dto.getCorreoInstitucional().isEmpty()) {
            logger.info("El correo es requerido");
            return new ResponseDTO("El correo es requerido", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getCedula()) || dto.getCedula().isEmpty()) {
            logger.info("La cédula es requerida");
            return new ResponseDTO("La cédula es requerida", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getDependencia()) || Objects.isNull(dto.getDependencia().getIdDependencia())) {
            logger.info("La dependencia es requerida");
            throw new BadRequestException("La dependencia es requerida para el funcionario");
        }

        // Actualizar funcionario
        Funcionario entity = funcionarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Funcionario no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Funcionario entityBefore = objectMapper.convertValue(entity, Funcionario.class);

        entity.setNombre(dto.getNombre());

        Dependencia dependencia = dependenciaRepository.findById(dto.getDependencia().getIdDependencia())
                .orElseThrow(() -> new BadRequestException("Dependencia no encontrada con id: " + dto.getDependencia().getIdDependencia()));
        entity.setDependencia(dependencia);

        entity.setApellido(dto.getApellido());
        entity.setCargo(dto.getCargo());
        entity.setCedula(dto.getCedula());
        entity.setCorreoInstitucional(dto.getCorreoInstitucional());
        entity.setEstadoFuncionario(entity.getEstadoFuncionario());

        funcionarioRepository.save(entity);

            //Auditoria
        try {
            auditoriaService.auditar("MODIFICAR",
            Objects.nonNull(entity) ? entity.getIdFuncionario().toString():null,entity,
            Objects.nonNull(entityBefore) ? entityBefore:null, "funcionario",
            "/funcionario/{id}", "/update", "funcionario/", null, null, null, null);

        } catch (UnknownHostException e) { e.printStackTrace();}

        return new ResponseDTO("Funcionario actualizado con éxito", HttpStatus.OK);

    }

    @Transactional
    public ResponseDTO updateStatus(Long id) {

        Funcionario entity = funcionarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Funcionario no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Funcionario entityBefore = objectMapper.convertValue(entity, Funcionario.class);

        entity.setEstadoFuncionario((entity.getEstadoFuncionario() ? false : true));

        //Auditoria
        try {
            auditoriaService.auditar("MODIFICAR",
            Objects.nonNull(entity) ? entity.getIdFuncionario().toString() : null,
            entity, entityBefore, "funcionario",
            "/updateStatus", "/editar",
            "funcionario/", null, null, null, null);
        } catch (UnknownHostException e) {e.printStackTrace();}

        funcionarioRepository.save(entity);

        return new ResponseDTO("Estado del Funcionario actualizado con éxito", HttpStatus.OK);

    }

    public ResponseEntity<ResponseDTO> getAllByOeeFuncionario(Long idOee) {
        List<DependenciaFuncionarioWithParentDTO> funcionarios = funcionarioRepository.findDependenciasFuncionariosWithParentByOeeId(idOee);
        ResponseDTO responseDTO = new ResponseDTO(funcionarios, HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

//    public ResponseEntity<ResponseDTO> getAllByOee(Long idOee) {
//        List<DependenciaWithParentDTO> dependencias = dependenciaRepository.findDependenciasWithParentByOeeId(idOee);
//        ResponseDTO responseDTO = new ResponseDTO(dependencias, HttpStatus.OK);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }

    public byte[] generateReport(String format, Long id, String reportFileName, Long page, Long pageSize, String sortField, Boolean sortAsc, Boolean permiso, String apellido, String cargo, String cedula, String correoInstitucional, String nombre, String descripcionDependencia, String descripcionOee) {
        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Funcionario");
        reportParameters.put("fecha", fechaFormateada);

        List<FuncionarioReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = funcionarioRepository.findFuncionarioOeeIdWithoutPermission(apellido, cargo, cedula, correoInstitucional, nombre, descripcionDependencia, descripcionOee);
        } else {
            data = funcionarioRepository.findFuncionarioOeeIdWithPermission(apellido, cargo, cedula, correoInstitucional, nombre, descripcionDependencia, descripcionOee, id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);
    }


}
