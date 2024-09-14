package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.Dependencia;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.repository.DependenciaRepository;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;
import java.util.Objects;
import java.util.stream.Collectors;

import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.DependenciaReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

@Service
public class DependenciaService extends GenericSpecification<Dependencia>{

    private Log logger = LogFactory.getLog(DependenciaService.class);
    private final DependenciaRepository dependenciaRepository;
    private final OeeRepository oeeRepository;
    private final AuditoriaService auditoriaService;
    private final ReportService reportService;

    @Autowired
    public DependenciaService(DependenciaRepository dependenciaRepository, OeeRepository oeeRepository, AuditoriaService auditoriaService, ReportService reportService) {
        this.dependenciaRepository = dependenciaRepository;
        this.oeeRepository = oeeRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idDependencia, Long id, Boolean permiso, String codigo, String descripcionDependencia, String descripcionOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0;

        Specification<Dependencia> specification = (root, query, cb) -> {
            Join<Dependencia, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idDependencia"), ((Objects.nonNull(idDependencia) && idDependencia > 0) ? idDependencia.toString() : "")),
                    like(cb, root.get("descripcionDependencia"), (!Objects.isNull(descripcionDependencia) ? descripcionDependencia : "")),
                    like(cb, root.get("codigo"), (!Objects.isNull(codigo) ? codigo : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

    //		auditMap = new HashMap<>();
    //      auditMap.put("paging", paging.toString());
    //		auditMap.put("page", (int) pageList.getNumber() +1);
    //		auditMap.put("totalRecords", (int) pageList.getTotalElements());
    //		while (auditMap.values().remove(null));
    //		while (auditMap.values().remove(""));
    //		params = new HashMap<>();
    //		params.put("idDependencia", idDependencia);
    //		params.put("descripcionDependencia", descripcionDependencia);
    //		params.put("codigo", codigo);
    //		params.put("descripcionOee", descripcionOee);
    //		while (params.values().remove(null));
    //		while (params.values().remove(""));
    //		try {
    //			auditoriaService.auditar("LISTAR", "S/I",
    //					auditMap, null, "dependencia", "/getAll/{params}", "/dependencia", "dependencia/", null, null, null, params);
    //		} catch (UnknownHostException e) {
    //			e.printStackTrace();
    //		}

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };

        Page<Dependencia> pageList = dependenciaRepository.findAll(specification, paging);

        TableDTO<Dependencia> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDTO> getListByOee(Long idOee) {
        // Lógica para obtener la lista de dependencias filtradas por idOee y estado true
        List<Dependencia> dependencias = dependenciaRepository.findByOeeIdAndEstadoDependenciaIsTrue(idOee);

        // Construir la respuesta DTO
        ResponseDTO responseDTO = new ResponseDTO(dependencias, HttpStatus.OK);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDTO> getAllByOee(Long idOee) {
        List<DependenciaWithParentDTO> dependencias = dependenciaRepository.findDependenciasWithParentByOeeId(idOee);
        ResponseDTO responseDTO = new ResponseDTO(dependencias, HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO create(DependenciaDTO dto) {

        try {
            if(Objects.isNull(dto.getOee())) {
                logger.info("No se detecto ninguna oee asociada al usuario");
                return new ResponseDTO("La OEE asociada al usuario es requerida", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getDescripcionDependencia()) || dto.getDescripcionDependencia().isEmpty()) {
                logger.info("La descripcion es requerida");
                return new ResponseDTO("La descripcion es requerida", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getCodigo()) || dto.getCodigo().isEmpty()) {
                logger.info("El codigo es requerido");
                return new ResponseDTO("El codigo es requerido", HttpStatus.BAD_REQUEST);
            }

            Dependencia dependencia = new Dependencia();
            dependencia.setDescripcionDependencia(dto.getDescripcionDependencia());
            dependencia.setCodigo(dto.getCodigo());
            dependencia.setOrden(0L);
            Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
            dependencia.setOee(oee);
            dependencia.setEstadoDependencia(true);
            dependencia.setFechaCreacion(new Date());

            if (Objects.nonNull(dto.getIdDependenciaPadre())) {
                Dependencia dependenciaPadre = dependenciaRepository.findById(dto.getIdDependenciaPadre())
                        .orElseThrow(() -> new BadRequestException("DependenciaPadre no encontrada con id: " + dto.getIdDependenciaPadre()));

                dependencia.setDependenciaPadre(dependenciaPadre);
            }

            // Guardar la dependencia
            dependenciaRepository.save(dependencia);

            //Auditoria
            auditoriaService.auditar("CREAR",
            Objects.nonNull(dependencia) ? dependencia.getIdDependencia().toString():null,
            Objects.nonNull(dependencia) ? dependencia:null,
            null, "dependencia",
            "/create", "/create", "dependencia/",
            null, null, null, null);

        } catch (Exception ex) {

            logger.error("Error al procesar la solicitud", ex);
            return new ResponseDTO("Error al procesar la solicitud", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseDTO("Dependencia creada con éxito", HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO update(Long id, DependenciaDTO dto) {

        if (Objects.isNull(dto.getDescripcionDependencia()) || dto.getDescripcionDependencia().isEmpty()) {
            logger.info("El campo Descripción es requerido");
            return new ResponseDTO("El campo Descripción es requerido.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getCodigo()) || dto.getCodigo().isEmpty()) {
            logger.info("El campo Código es requerido");
            return new ResponseDTO("El campo Código es requerido.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.nonNull(dto.getIdDependenciaPadre()) && dto.getIdDependencia().equals(dto.getIdDependenciaPadre())) {
            logger.info("La dependencia no puede ser su propio padre");
            return new ResponseDTO("La dependencia no puede ser su propio padre", HttpStatus.BAD_REQUEST);
        }

        // Obtener la dependencia antes de la actualización
        Dependencia entity = dependenciaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Dependencia no encontrada con id: " + id));

        ObjectMapper objectMapper = new ObjectMapper();
        Dependencia entityBefore = objectMapper.convertValue(entity, Dependencia.class);

        // Realizar la actualización de la dependencia
        entity.setIdDependencia(id);
        entity.setDescripcionDependencia(dto.getDescripcionDependencia());
        entity.setCodigo(dto.getCodigo());
        entity.setOrden(0L);
        Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
        entity.setOee(oee);
        entity.setFechaCreacion(entity.getFechaCreacion());
        entity.setEstadoDependencia(entity.getEstadoDependencia());

        if (Objects.nonNull(dto.getIdDependenciaPadre())) {
            Dependencia dependenciaPadre = dependenciaRepository.findById(dto.getIdDependenciaPadre())
                    .orElseThrow(() -> new BadRequestException("Dependencia Padre no encontrada con id: " + dto.getIdDependenciaPadre()));

            entity.setDependenciaPadre(dependenciaPadre);
        } else if (Objects.isNull(dto.getIdDependenciaPadre())) {

            entity.setDependenciaPadre(null);

        } else {

            entity.setDependenciaPadre(entity.getDependenciaPadre());

        }

        try {

            //auditoría
            auditoriaService.auditar("MODIFICAR",
            Objects.nonNull(entity) ? entity.getIdDependencia().toString() : null,
            entity,
            Objects.nonNull(entity) ? entityBefore : null,
            "dependencia",
            "/dependencia/{id}",
            "/update",
            "dependencia/",
            null,
            null,
            null,
            null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dependenciaRepository.save(entity);

        return new ResponseDTO("Dependencia actualizada con éxito", HttpStatus.OK);

    }

    @Transactional
    public ResponseDTO updateStatus(Long id) {
//        Optional<Dependencia> dependenciaOptional = dependenciaRepository.findById(id);
//
//        if (dependenciaOptional.isPresent()) {
//            Dependencia dependencia = dependenciaOptional.get();
//            boolean newStatus = !dependencia.getEstadoDependencia();
//            updateStatusRecursive(dependencia, newStatus);
//
//            return new ResponseDTO("Estado de la Dependencia y sus hijos actualizado con éxito", HttpStatus.OK);
//        } else {
//            throw new BadRequestException("La Dependencia no existe");
//        }
        Dependencia entity = dependenciaRepository.findById(id).orElseThrow(() -> new BadRequestException("La Dependencia no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Dependencia entityBefore = objectMapper.convertValue(entity, Dependencia.class);

        boolean newStatus = !entity.getEstadoDependencia();

        updateStatusRecursive(entity, newStatus);

        entity.setEstadoDependencia(newStatus);

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdDependencia().toString() : null,
                    entity,
                    entityBefore,
                    "dependencia",
                    "/dependencia/{id}",
                    "/update",
                    "dependencia/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dependenciaRepository.save(entity);

        return new ResponseDTO("Estado de la Dependencia actualizada con éxito", HttpStatus.OK);

    }

    private void updateStatusRecursive(Dependencia dependencia, boolean newStatus) {
        dependencia.setEstadoDependencia(newStatus);
        dependenciaRepository.save(dependencia);

        List<Dependencia> dependenciasHijas = dependenciaRepository.findByDependenciaPadre_IdDependencia(dependencia.getIdDependencia());

        for (Dependencia dependenciaHija : dependenciasHijas) {
            updateStatusRecursive(dependenciaHija, newStatus);
        }
    }

    public byte[] generateReport(String format, Long idOee, String reportFileName, Boolean permiso, String codigo, String descripcionDependencia, String descripcionOee) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Dependencias");
        reportParameters.put("fecha", fechaFormateada);

        List<DependenciaReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = dependenciaRepository.findDependenciaOeeWithoutId(codigo, descripcionDependencia, descripcionOee);
        } else {
            data = dependenciaRepository.findDependenciaOeeWitId(codigo, descripcionDependencia, descripcionOee, idOee);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }

    public ResponseDTO getAllDependencia(int page, int pageSize, String sortField, boolean sortAsc, Long idDependencia, Long id, Boolean permiso, String codigo, String descripcionDependencia, String descripcionOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0;

        Specification<Dependencia> specification = (root, query, cb) -> {
            Join<Dependencia, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idDependencia"), ((Objects.nonNull(idDependencia) && idDependencia > 0) ? idDependencia.toString() : "")),
                    like(cb, root.get("descripcionDependencia"), (!Objects.isNull(descripcionDependencia) ? descripcionDependencia : "")),
                    like(cb, root.get("codigo"), (!Objects.isNull(codigo) ? codigo : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            predicates = Arrays.copyOf(predicates, predicates.length + 1);
            predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));


            //		auditMap = new HashMap<>();
            //      auditMap.put("paging", paging.toString());
            //		auditMap.put("page", (int) pageList.getNumber() +1);
            //		auditMap.put("totalRecords", (int) pageList.getTotalElements());
            //		while (auditMap.values().remove(null));
            //		while (auditMap.values().remove(""));
            //		params = new HashMap<>();
            //		params.put("idDependencia", idDependencia);
            //		params.put("descripcionDependencia", descripcionDependencia);
            //		params.put("codigo", codigo);
            //		params.put("descripcionOee", descripcionOee);
            //		while (params.values().remove(null));
            //		while (params.values().remove(""));
            //		try {
            //			auditoriaService.auditar("LISTAR", "S/I",
            //					auditMap, null, "dependencia", "/getAll/{params}", "/dependencia", "dependencia/", null, null, null, params);
            //		} catch (UnknownHostException e) {
            //			e.printStackTrace();
            //		}

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };

        Page<Dependencia> pageList = dependenciaRepository.findAll(specification, paging);

        TableDTO<Dependencia> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }
}
