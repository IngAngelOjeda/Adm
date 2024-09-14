package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.RangoIp;
import py.gov.mitic.adminpy.repository.OeeRepository;
import py.gov.mitic.adminpy.repository.RangoIpRepository;
import py.gov.mitic.adminpy.repository.projections.RangoIpReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;


import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;
import java.net.UnknownHostException;

@Service
public class RangoIpService extends GenericSpecification<RangoIp>{

    private Log logger = LogFactory.getLog(RangoIpService.class);

    private final RangoIpRepository rangoIpRepository;
    private final AuditoriaService auditoriaService;
    private final OeeRepository oeeRepository;
    private final ReportService reportService;
    private Map<String, Object> auditMap = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();

    public RangoIpService(RangoIpRepository rangoIpRepository, AuditoriaService auditoriaService, OeeRepository oeeRepository, ReportService reportService) {
        this.rangoIpRepository = rangoIpRepository;
        this.auditoriaService = auditoriaService;
        this.oeeRepository = oeeRepository;
        this.reportService = reportService;
    }

    @Transactional
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idRango, Long id, Boolean permiso, String rango, String descripcionOee, Boolean perteneceDmz, Boolean perteneceIpNavegacion, Boolean perteneceVpn) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

        Boolean showOption = Objects.nonNull(id) && id > 0 ? true:false;

        Specification<RangoIp> specification = (root, query, cb) -> {
            Join<RangoIp, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idRango"), ((Objects.nonNull(idRango) && idRango > 0) ? idRango.toString() : "")),
                    like(cb, root.get("rango"), (!Objects.isNull(rango) ? rango : "")),
                    like(cb, root.get("perteneceDmz"), (!Objects.isNull(perteneceDmz) ? perteneceDmz.toString() : "")),
                    like(cb, root.get("perteneceIpNavegacion"), (!Objects.isNull(perteneceIpNavegacion) ? perteneceIpNavegacion.toString() : "")),
                    like(cb, root.get("perteneceVpn"), (!Objects.isNull(perteneceVpn) ? perteneceVpn.toString() : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };

        Page<RangoIp> pageList = rangoIpRepository.findAll(specification, paging);

        TableDTO<RangoIp> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

//        auditMap = new HashMap<>();
//        auditMap.put("paging", paging.toString());
//        auditMap.put("page", (int) pageList.getNumber() +1);
//        auditMap.put("totalRecords", (int) pageList.getTotalElements());
//        while (auditMap.values().remove(null));
//        while (auditMap.values().remove(""));
//        params = new HashMap<>();
//        params.put("idRango", id);
//        params.put("rango", rango);
//        params.put("descripcionOee", descripcionOee);
//        while (params.values().remove(null));
//        while (params.values().remove(""));
//
//        try {
//            auditoriaService.auditar("LISTAR", "S/I",
//                    auditMap, null, "rangoip", "/getAll/{params}", "/rangoip", "rangoip/", null, null, null, params);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO create(RangoIp dto) {


        if(Objects.isNull(dto.getOee())) {
            logger.info("No se detecto ninguna oee asociada al usuario");
            return new ResponseDTO("La OEE asociada al usuario es requerida", HttpStatus.BAD_REQUEST);
        }

        if(Objects.isNull(dto.getRango())) {
            logger.info("El campo 'Rango' es requerido");
            return new ResponseDTO("El campo 'Rango' es requerido.", HttpStatus.BAD_REQUEST);
        }

        try {

            RangoIp rangoIp = new RangoIp();
            rangoIp.setRango(dto.getRango());
            Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
            rangoIp.setOee(oee);
            rangoIp.setEstado(true);
            rangoIp.setPerteneceDmz(dto.getPerteneceDmz());
            rangoIp.setPerteneceIpNavegacion(dto.getPerteneceIpNavegacion());
            rangoIp.setPerteneceVpn(dto.getPerteneceVpn());
            rangoIpRepository.save(rangoIp);

            //Auditoria
            auditoriaService.auditar("CREAR",
            Objects.nonNull(rangoIp) ? rangoIp.getIdRango().toString():null,
            Objects.nonNull(rangoIp) ? rangoIp:null,
            null, "rangoIp",
            "/create", "/create", "rangoIp/", null, null, null, null);

            return new ResponseDTO("Rango IP creado con éxito", HttpStatus.OK);

        } catch (Exception ex) {

            ex.printStackTrace();

        }
        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);

    }

    public ResponseDTO update(Long id, RangoIpDTO dto) {
        // Obtener el rangoIp antes de la actualización
        RangoIp rangoIpAntes = rangoIpRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rango IP no encontrado con id: " + id));

        if (Objects.isNull(dto.getRango())) {
            logger.info("El campo 'Rango' es requerido");
            return new ResponseDTO("El campo 'Rango' es requerido.", HttpStatus.BAD_REQUEST);
        }

        // Crear una nueva instancia para la auditoría con los datos actuales
        RangoIp rangoIpAntesParaAuditoria = new RangoIp();
        rangoIpAntesParaAuditoria.setIdRango(rangoIpAntes.getIdRango());
        rangoIpAntesParaAuditoria.setRango(rangoIpAntes.getRango());
        rangoIpAntesParaAuditoria.setPerteneceDmz(rangoIpAntes.getPerteneceDmz());
        Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
        rangoIpAntesParaAuditoria.setOee(oee);
        rangoIpAntesParaAuditoria.setPerteneceVpn(rangoIpAntes.getPerteneceVpn());
        rangoIpAntesParaAuditoria.setPerteneceIpNavegacion(rangoIpAntes.getPerteneceIpNavegacion());

        // Realizar la actualización
        rangoIpAntes.setRango(dto.getRango());
        rangoIpAntes.setPerteneceDmz(dto.getPerteneceDmz());
        rangoIpAntes.setPerteneceVpn(dto.getPerteneceVpn());
        rangoIpAntes.setPerteneceIpNavegacion(dto.getPerteneceIpNavegacion());

        // Guardar el rangoIp actualizado
        RangoIp rangoIpDespues = rangoIpRepository.save(rangoIpAntes);

        // Auditoría
        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(rangoIpAntes) ? rangoIpAntes.getIdRango().toString() : null,
                    rangoIpDespues,
                    rangoIpAntesParaAuditoria, "rangoIp",
                    "/rangoIp/{id}", "/update", "rangoIp/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return new ResponseDTO("Rango IP actualizado con éxito", HttpStatus.OK);
    }


    public ResponseDTO updateStatus(Long id) {
        // Obtener el rangoIp antes de la actualización
        RangoIp rangoIpAntes = rangoIpRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rango IP no encontrado con id: " + id));

        // Realizar una copia del objeto para tener el estado antes de la actualización
        RangoIp rangoIpAntesCopia = new RangoIp();
        rangoIpAntesCopia.setIdRango(rangoIpAntes.getIdRango());
        rangoIpAntesCopia.setRango(rangoIpAntes.getRango());
        rangoIpAntesCopia.setOee(rangoIpAntes.getOee());
        rangoIpAntesCopia.setEstado(rangoIpAntes.getEstado());
        rangoIpAntesCopia.setPerteneceVpn(rangoIpAntes.getPerteneceVpn());
        rangoIpAntesCopia.setPerteneceIpNavegacion(rangoIpAntes.getPerteneceIpNavegacion());
        rangoIpAntesCopia.setPerteneceDmz(rangoIpAntes.getPerteneceDmz());
        // Copiar otras propiedades según sea necesario

        // Actualizar el estado
        rangoIpAntes.setEstado(!rangoIpAntes.getEstado());

        // Guardar el rangoIp actualizado
        RangoIp rangoIpDespues = rangoIpRepository.save(rangoIpAntes);

        // Auditoría
        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(rangoIpAntes) ? rangoIpAntes.getIdRango().toString() : null,
                    rangoIpDespues,
                    rangoIpAntesCopia, "rangoIp",
                    "/rangoIp/{id}", "/updateStatus", "rangoIp/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return new ResponseDTO("Estado del Rango IP actualizado con éxito", HttpStatus.OK);
    }


    public ResponseDTO delete(Long id) {
        // Obtener el rangoIp antes de la eliminación
        RangoIp rangoIpAntes = rangoIpRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rango IP no existe"));

        try {
            // Auditoría antes de la eliminación
            auditoriaService.auditar("BORRAR",
                    Objects.nonNull(rangoIpAntes) ? rangoIpAntes.getIdRango().toString() : null,
                    null,
                    rangoIpAntes, "rangoIp",
                    "/rangoIp/{id}/delete", "/delete", "rangoIp/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Eliminar el rangoIp
        rangoIpRepository.delete(rangoIpAntes);

        return new ResponseDTO("Rango IP eliminado con éxito", HttpStatus.OK);
    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String rango, String descripcionOee, String perteneceDmz, String perteneceVpn) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Rangos IP");
        reportParameters.put("fecha", fechaFormateada);

        List<RangoIpReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = rangoIpRepository.findRangoIpIdWithoutPermission(rango, descripcionOee);
        } else {
            data = rangoIpRepository.findRangoIpIdWithPermission(rango, descripcionOee,id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }



}
