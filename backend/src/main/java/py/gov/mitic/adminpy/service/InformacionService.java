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
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.TipoDato;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.DatosOeeReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import java.util.Objects;

@Service
public class InformacionService extends GenericSpecification<DatosOee>{

    private Log logger = LogFactory.getLog(InformacionService.class);
    private final InformacionRepository informacionRepository;
    private final OeeRepository oeeRepository;
    private final TipoDatoRepository tipoDatoRepository;
    private final ReportService reportService;
    private final AuditoriaService auditoriaService;
    private final DependenciaService dependenciaService;

    public InformacionService(InformacionRepository informacionRepository, OeeRepository oeeRepository, TipoDatoRepository tipoDatoRepository, ReportService reportService, AuditoriaService auditoriaService, DependenciaService dependenciaService) {
        this.informacionRepository = informacionRepository;
        this.oeeRepository = oeeRepository;
        this.tipoDatoRepository = tipoDatoRepository;
        this.reportService = reportService;
        this.auditoriaService = auditoriaService;
        this.dependenciaService = dependenciaService;
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

        Page<DatosOee> pageList = informacionRepository.findAll(specification, paging);

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

    @Transactional
    public ResponseDTO getAllOee(int page, int pageSize, String sortField, boolean sortAsc, Long id, Boolean permiso, String descripcionOee, String descripcionCorta, String urlOee, String descripcionOeeInformacion ) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0 && !permiso;

        Specification<Oee> specification = (root, query, cb) -> {

            Predicate[] predicates = {
                    equal(cb, root.get("id"), (showOption ? id.toString() : "")),
                    like(cb, root.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : "")),
                    like(cb, root.get("descripcionCorta"), (!Objects.isNull(descripcionCorta) ? descripcionCorta : "")),
                    like(cb, root.get("urlOee"), (!Objects.isNull(urlOee) ? urlOee : ""))
            };

            return query.where(cb.and(predicates)).distinct(true).getRestriction();
        };

        Page<Oee> pageList = oeeRepository.findAll(specification, paging);

        TableDTO<Oee> tableDTO = new TableDTO<>();
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

    @Transactional
    public ResponseDTO update(List<DatosOeeDTO> dtos) {
        try {
            for (DatosOeeDTO dto : dtos) {
                // Validar si el registro existe
                Long id = dto.getIdOeeInformacion();

                // Verificar si el DTO tiene un ID válido
                if (Objects.isNull(id) || id <= 0) {
                    logger.info("El ID de DatosOeeDTO no es válido");
                    return new ResponseDTO("El ID de DatosOeeDTO no es válido", HttpStatus.BAD_REQUEST);
                }

                DatosOee entity = informacionRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Registro no encontrado"));

                // Mapear los datos del DTO a la entidad
                mapDtoToEntity(dto, entity);

                // Guardar la entidad actualizada
                informacionRepository.save(entity);

                // Auditoria para cada registro
//                auditoriaService.auditar("MODIFICAR",
//                        Objects.nonNull(entity) ? entity.getIdOeeInformacion().toString() : null, entity,
//                        null, "datosOee", "/informacion/{id}", "/update", "informacion/", null, null, null, null);
            }

            return new ResponseDTO("Registros actualizados con éxito", HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.info(e.getMessage());
            return new ResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud", e);
            return new ResponseDTO("Error al procesar la solicitud", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String descripcionOeeInformacion, String descripcionOee, String descripcionTipoDato) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Informacion OEE");
        reportParameters.put("fecha", fechaFormateada);

        List<DatosOeeReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = informacionRepository.findDependenciaIdWithoutPermission(descripcionOeeInformacion, descripcionOee, descripcionTipoDato);
        } else {
            data = informacionRepository.findDependenciaIdWithPermission(descripcionOeeInformacion, descripcionOee, descripcionTipoDato, id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }

    private void mapDtoToEntity(DatosOeeDTO dto, DatosOee entity) {
        // Mapear los campos que pueden ser actualizados desde el DTO a la entidad
        entity.setDescripcionOeeInformacion(dto.getDescripcionOeeInformacion());
    }


}
