package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.criteria.*;
import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.DatosOeeDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.DatosOee;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.TipoDato;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.DatosOeeReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import java.util.Objects;

@Service
public class DatosOeeService extends GenericSpecification<DatosOee> {

    private Log logger = LogFactory.getLog(DatosOeeService.class);
    private final DatosOeeRepository datosOeeRepository;
    private final OeeRepository oeeRepository;
    private final TipoDatoRepository tipoDatoRepository;
    private final ReportService reportService;
    private final AuditoriaService auditoriaService;

    public DatosOeeService(DatosOeeRepository datosOeeRepository, OeeRepository oeeRepository, TipoDatoRepository tipoDatoRepository, ReportService reportService, AuditoriaService auditoriaService) {
        this.datosOeeRepository = datosOeeRepository;
        this.oeeRepository = oeeRepository;
        this.tipoDatoRepository = tipoDatoRepository;
        this.reportService = reportService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idOeeInformacion, Long idTipoDato, Long id, Boolean permiso,  String descripcionOee,String descripcionOeeInformacion, String estadoOeeInformacion , String descripcionTipoDato) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0 ? true:false;

        Specification<DatosOee> specification = (root, query, cb) -> {
            Join<DatosOee, TipoDato> tipoDatoJoin = root.join("tipoDato", JoinType.LEFT);
            Join<DatosOee, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idOeeInformacion"), ((Objects.nonNull(idOeeInformacion) && idOeeInformacion > 0) ? idOeeInformacion.toString() : "")),
                    like(cb, root.get("descripcionOeeInformacion"), (!Objects.isNull(descripcionOeeInformacion) ? descripcionOeeInformacion : "")),
                    like(cb, root.get("estadoOeeInformacion"), (!Objects.isNull(estadoOeeInformacion) ? estadoOeeInformacion : "")),
                    like(cb, tipoDatoJoin.get("descripcionTipoDato"), (!Objects.isNull(descripcionTipoDato) ? descripcionTipoDato : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();
        };

        Page<DatosOee> pageList = datosOeeRepository.findAll(specification, paging);

        TableDTO<DatosOee> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        //		auditMap = new HashMap<>();
//      auditMap.put("paging", paging.toString());
//		auditMap.put("page", (int) pageList.getNumber() +1);
//		auditMap.put("totalRecords", (int) pageList.getTotalElements());
//		while (auditMap.values().remove(null));
//		while (auditMap.values().remove(""));
//		params = new HashMap<>();
//		params.put("idOeeInformacion", idOeeInformacion);
//		params.put("descripcionOeeInformacion", descripcionOeeInformacion);
//		params.put("estadoOeeInformacion", estadoOeeInformacion);
//		params.put("descripcionTipoDato", descripcionTipoDato);
//		params.put("descripcionOee", descripcionOee);
//		while (params.values().remove(null));
//		while (params.values().remove(""));
//		try {
//			auditoriaService.auditar("LISTAR", "S/I",
//					auditMap, null, "datosOee", "/getAll/{params}", "/datosOee", "datosOee/", null, null, null, params);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO update(Long id, DatosOeeDTO dto) {

        if(Objects.isNull(dto.getDescripcionOeeInformacion())) {
            logger.info("La descripción es requerida.");
            return new ResponseDTO("La descripción es requerida.", HttpStatus.BAD_REQUEST);
        }
//        datosOeeRepository.findById(id).map(datosOee -> {
//            datosOee.setDescripcionOeeInformacion(dto.getDescripcionOeeInformacion());
//            return datosOeeRepository.save(datosOee);
//        }).orElseThrow(() -> new BadRequestException("Error al actualizar"));
//        return new ResponseDTO("Descripción actualizada con éxito", HttpStatus.OK);

        DatosOee entity = datosOeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Información del Oee no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        DatosOee entityBefore = objectMapper.convertValue(entity, DatosOee.class);

        entity.setDescripcionOeeInformacion(dto.getDescripcionOeeInformacion());

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdOeeInformacion().toString() : null,
                    entity,
                    entityBefore,
                    "oee_infromacion",
                    "/datosOee/{id}",
                    "/update",
                    "datosOee/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return new ResponseDTO("Descripción actualizada con éxito", HttpStatus.OK);

    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String descripcionOeeInformacion, String descripcionOee, String descripcionTipoDato) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Datos OEE");
        reportParameters.put("fecha", fechaFormateada);

        List<DatosOeeReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = datosOeeRepository.findDependenciaIdWithoutPermission(descripcionOeeInformacion, descripcionOee, descripcionTipoDato);
        } else {
            data = datosOeeRepository.findDependenciaIdWithPermission(descripcionOeeInformacion, descripcionOee, descripcionTipoDato, id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }

}
