package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import py.gov.mitic.adminpy.model.dto.DominioDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.SubDominio;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.DominioReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

@Service
public class DominioService extends GenericSpecification<Dominio>{

    private Log logger = LogFactory.getLog(SistemaService.class);
    private final DominioRepository dominioRepository;
    private final OeeRepository oeeRepository;
    private final SubDominioRepository subdominioRepository;
    private final AuditoriaService auditoriaService;

    private final ReportService reportService;

    public DominioService(DominioRepository dominioRepository, OeeRepository oeeRepository, SubDominioRepository subdominioRepository, AuditoriaService auditoriaService, ReportService reportService) {
        this.dominioRepository = dominioRepository;
        this.oeeRepository = oeeRepository;
        this.subdominioRepository = subdominioRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idDominio, Long id, Boolean permiso, String dominio, String descripcionOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0;

        Specification<Dominio> specification = (root, query, cb) -> {
            Join<Dominio, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idDominio"), ((Objects.nonNull(idDominio) && idDominio > 0) ? idDominio.toString() : "")),
                    like(cb, root.get("dominio"), (!Objects.isNull(dominio) ? dominio : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };
        //logger.info("Specification: " + specification);
        Page<Dominio> pageList = dominioRepository.findAll(specification, paging);

        TableDTO<Dominio> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO create(DominioDTO dto) {

        if(Objects.isNull(dto.getOee())) {
            logger.info("No se detecto ninguna oee asociada al usuario");
            return new ResponseDTO("La OEE asociada al usuario es requerida", HttpStatus.BAD_REQUEST);
        }

        if(Objects.isNull(dto.getDominio())) {
            logger.info("El campo 'Dominio' es requerido");
            return new ResponseDTO("El campo 'Dominio' es requerido.", HttpStatus.BAD_REQUEST);
        }

        Dominio dominio = new Dominio();
        dominio.setDominio(dto.getDominio());
        Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
        dominio.setOee(oee);
        dominio.setEstado(true);
        dominioRepository.save(dominio);

        try {
            auditoriaService.auditar("CREAR",
                    Objects.nonNull(dominio) ? dominio.getIdDominio().toString() : null,
                    dominio,
                    Objects.nonNull(dominio) ? dominio : null,
                    "dominio",
                    "dominio/",
                    "/create",
                    "dominio/create",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        return new ResponseDTO("Dominio creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, DominioDTO dto) {
        if(Objects.isNull(dto.getDominio())) {
            logger.info("El campo 'Dominio' es requerido");
            return new ResponseDTO("El campo 'Dominio' es requerido.", HttpStatus.BAD_REQUEST);
        }

        Dominio entity = dominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Dominio no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Dominio entityBefore = objectMapper.convertValue(entity, Dominio.class);

        entity.setDominio(dto.getDominio());


        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdDominio().toString() : null,
                    entity,
                    entityBefore,
                    "dominio",
                    "/dominio/{id}",
                    "/update",
                    "dominio/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dominioRepository.save(entity);

        return new ResponseDTO("Dominio actualizado con éxito", HttpStatus.OK);
    }

    public ResponseDTO updateStatus(Long id) {

        Dominio entity = dominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Dominio no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Dominio entityBefore = objectMapper.convertValue(entity, Dominio.class);

        entity.setEstado((entity.getEstado() ? false : true));

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdDominio().toString() : null,
                    entity,
                    entityBefore,
                    "dominio",
                    "/dominio/{id}",
                    "/update",
                    "dominio/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dominioRepository.save(entity);

        return new ResponseDTO("Estado del Dominio actualizado con éxito", HttpStatus.OK);
    }

    public ResponseDTO delete(Long id) {
        Dominio dominio = dominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Dominio no existe"));

        try {
            // Auditoría antes de la eliminación
            auditoriaService.auditar("BORRAR",
                    Objects.nonNull(dominio) ? dominio.getIdDominio().toString() : null,
                    null,
                    dominio, "dominio",
                    "/dominio/{id}/delete", "/delete", "dominio/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        List<SubDominio> subdominios = subdominioRepository.findSubDominiosByDominioId(id);

        for (SubDominio subdominio : subdominios) {
            try {
                // Auditoría antes de la eliminación
                auditoriaService.auditar("BORRAR",
                        Objects.nonNull(subdominio) ? subdominio.getIdSubDominio().toString() : null,
                        null,
                        subdominio, "subdominio",
                        "/dominio/{id}/delete", "/delete", "dominio/", null, null, null, null);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            subdominioRepository.delete(subdominio);

        }

        dominioRepository.delete(dominio);

        return new ResponseDTO("Dominio y sus subdominios eliminados con éxito", HttpStatus.OK);
    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String dominio, String descripcionOee) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Dominios");
        reportParameters.put("fecha", fechaFormateada);

        List<DominioReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = dominioRepository.findDominiosIdWithoutPermission(dominio, descripcionOee);
        } else {
            data = dominioRepository.findDominiosIdWithPermission(dominio, descripcionOee,id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }
}
