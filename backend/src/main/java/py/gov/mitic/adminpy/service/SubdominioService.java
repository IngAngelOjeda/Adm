package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
import py.gov.mitic.adminpy.model.dto.SubdominioDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.SubDominio;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.SubdominioReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

@Service
public class SubdominioService extends GenericSpecification<Dominio>{

    private Log logger = LogFactory.getLog(SistemaService.class);
    private final DominioRepository dominioRepository;
    private final SubDominioRepository subdominioRepository;
    private final AuditoriaService auditoriaService;
    private final ReportService reportService;

    public SubdominioService(DominioRepository dominioRepository, SubDominioRepository subdominioRepository, AuditoriaService auditoriaService, ReportService reportService) {
        this.dominioRepository = dominioRepository;
        this.subdominioRepository = subdominioRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;
    }
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idDominio, Long idSubdominio, String subdominio) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(idDominio) && idDominio > 0 ? true:false;

        Specification<SubDominio> specification = (root, query, cb) -> {
            Join<SubDominio, Dominio> DominioJoin = root.join("dominio", JoinType.LEFT);

            return query.where(cb.and(
                            equal(cb, root.get("idSubDominio"), ((Objects.nonNull(idSubdominio) && idSubdominio > 0 ? true:false) ? idSubdominio.toString() : "")),
                            like(cb, root.get("subdominio"), (!Objects.isNull(subdominio) ? subdominio : "")),
                            equal(cb, DominioJoin.get("idDominio"),  (showOption ? idDominio.toString() : ""))
                    ))
                    .distinct(true)
                    .getRestriction();
        };
        Page<SubDominio> pageList = subdominioRepository.findAll(specification, paging);

        TableDTO<SubDominio> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO create(SubdominioDTO dto) {


        if(Objects.isNull(dto.getSubdominio())) {
            logger.info("El campo 'Subdominio' es requerido");
            return new ResponseDTO("El campo 'Subdominio' es requerido.", HttpStatus.BAD_REQUEST);
        }

        SubDominio subDominio = new SubDominio();
        subDominio.setSubdominio(dto.getSubdominio());
        Dominio dominio = dominioRepository.findById(dto.getDominio().getIdDominio()).get();
        subDominio.setDominio(dominio);
        subDominio.setEstado(true);
        subdominioRepository.save(subDominio);

        try {
            auditoriaService.auditar("CREAR",
                    Objects.nonNull(subDominio) ? subDominio.getIdSubDominio().toString() : null,
                    subDominio,
                    Objects.nonNull(subDominio) ? subDominio : null,
                    "subdominio",
                    "subdominio/",
                    "/create",
                    "subdominio/create",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        return new ResponseDTO("Subdominio creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, SubdominioDTO dto) {

        if(Objects.isNull(dto.getSubdominio())) {
            logger.info("El campo 'Subdominio' es requerido");
            return new ResponseDTO("El campo 'Subdominio' es requerido.", HttpStatus.BAD_REQUEST);
        }

//        subdominioRepository.findById(id).map(subDominio -> {
//            subDominio.setSubdominio(dto.getSubdominio());
//            return subdominioRepository.save(subDominio);
//        }).orElseThrow(() -> new BadRequestException("Error al actualizar"));
//        return new ResponseDTO("Subdominio actualizado con éxito", HttpStatus.OK);
        SubDominio entity = subdominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Subdominio no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        SubDominio entityBefore = objectMapper.convertValue(entity, SubDominio.class);

        entity.setSubdominio(dto.getSubdominio());

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdSubDominio().toString() : null,
                    entity,
                    entityBefore,
                    "subdominio",
                    "/subdominio/{id}",
                    "/update",
                    "subdominio/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        subdominioRepository.save(entity);

        return new ResponseDTO("Subdominio actualizado con éxito", HttpStatus.OK);

    }

    public ResponseDTO updateStatus(Long id) {
//        subdominioRepository.findById(id).map(subDominio -> {
//            subDominio.setEstado((subDominio.getEstado() ? false : true));
//            return subdominioRepository.save(subDominio);
//        }).orElseThrow(() -> new BadRequestException("Subdominio no existe"));
//        return new ResponseDTO("Estado del Subdominio actualizado con éxito", HttpStatus.OK);

        SubDominio entity = subdominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Subdominio no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        SubDominio entityBefore = objectMapper.convertValue(entity, SubDominio.class);

        entity.setEstado((entity.getEstado() ? false : true));

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdSubDominio().toString() : null,
                    entity,
                    entityBefore,
                    "subdominio",
                    "/subdominio/{id}",
                    "/update",
                    "subdominio/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        subdominioRepository.save(entity);

        return new ResponseDTO("Estado del Subdominio actualizado con éxito", HttpStatus.OK);
    }

    public ResponseDTO delete(Long id) {
        SubDominio subdominio = subdominioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Subdominio no existe"));

        try {
            // Auditoría antes de la eliminación
            auditoriaService.auditar("BORRAR",
                    Objects.nonNull(subdominio) ? subdominio.getIdSubDominio().toString() : null,
                    null,
                    subdominio, "subdominio",
                    "/subdominio/{id}/delete",
                    "/delete",
                    "subdominio/",
                    null,
                    null,
                    null,
                    null);

        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        subdominioRepository.delete(subdominio);

        return new ResponseDTO("Subdominio eliminado con éxito", HttpStatus.OK);

    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String subdominio) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Subdominios");
        reportParameters.put("fecha", fechaFormateada);

        List<SubdominioReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = subdominioRepository.findSubDominioIdWithoutPermission( subdominio);
        } else {
            data = subdominioRepository.findSubDominioIdWithPermission( subdominio,id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }
}

