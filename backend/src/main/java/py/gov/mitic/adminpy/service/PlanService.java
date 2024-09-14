package py.gov.mitic.adminpy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.repository.OeeRepository;
import py.gov.mitic.adminpy.repository.PlanRepository;
import py.gov.mitic.adminpy.repository.RangoIpRepository;
import py.gov.mitic.adminpy.repository.projections.PlanReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;
import py.gov.mitic.adminpy.specification.SearchCriteria;
import py.gov.mitic.adminpy.specification.SearchOperator;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.ws.rs.BadRequestException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class PlanService extends GenericSpecification<Plan> {

    private Log logger = LogFactory.getLog(PlanService.class);

    private final PlanRepository planRepository;
    private final AuditoriaService auditoriaService;

    private final ReportService reportService;

    public PlanService(PlanRepository planRepository, AuditoriaService auditoriaService, ReportService reportService) {
        this.planRepository = planRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;
    }

    DecimalFormat formatea = new DecimalFormat("###,###,###,###,###,###");

    @Transactional
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String anho, String fecha, String linkPlan, String cantidadFuncionariosTic, String cantidadFuncionariosAdmin, String presupuestoTicAnual, Boolean estado) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

//        Boolean showOption = Objects.nonNull(idInstitucion) && idInstitucion > 0 ? true:false;

        Specification<Plan> specification = (root, query, cb) -> {
//            Join<Sistema, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            return query.where(cb.and(
                            equal(cb, root.get("idPlan"), ((Objects.nonNull(id) && id > 0 ? true:false) ? id.toString() : "")),
                            like(cb, root.get("anho"), (!Objects.isNull(anho) ? anho : "")),
                            like(cb, root.get("fecha"), (!Objects.isNull(fecha) ? fecha : "")),
                            like(cb, root.get("linkPlan"), (!Objects.isNull(linkPlan) ? linkPlan : "")),
                            like(cb, root.get("cantidadFuncionariosTic"), (!Objects.isNull(cantidadFuncionariosTic) ? cantidadFuncionariosTic : "")),
                            like(cb, root.get("cantidadFuncionariosAdmin"), (!Objects.isNull(cantidadFuncionariosAdmin) ? cantidadFuncionariosAdmin : "")),
                            like(cb, root.get("presupuestoTicAnual"), (!Objects.isNull(presupuestoTicAnual) ? presupuestoTicAnual : ""))
//                            like(cb, root.get("presupuestoTicAnual"), (!Objects.isNull(presupuestoTicAnual) ? formatea.format(presupuestoTicAnual) : ""))
                            //like(cb, root.get("estado"), (!Objects.isNull(estado) ? true : false))
//                            like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : "")),
//                            equal(cb, oeeJoin.get("id"),  (showOption ? id.toString() : ""))
                    ))
                    .distinct(true)
                    .getRestriction();
        };
        //logger.info("Specification: " + specification);
        Page<Plan> pageList = planRepository.findAll(specification, paging);

        TableDTO<Plan> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

//    FUNCION PARA VALIDAR UNA URL
    boolean isValidURL (String url){
        UrlValidator urlValidator = new UrlValidator();
        boolean resultado;
        if (urlValidator.isValid(url)) {
            resultado = true;
        } else {
            resultado = false;
        }
        return resultado;
    }

//    FUNCION CREAR
    @Transactional
    public ResponseDTO save(PlanDTO dto) {

            if(dto.getAnho().isEmpty()) {
                logger.info("El campo 'Año' es requerido.");
               return new ResponseDTO("El campo 'Año' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getFecha().isEmpty()) {
                logger.info("El campo 'Fecha' es requerido.");
                return new ResponseDTO("El campo 'Fecha' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getCantidadFuncionariosTic().isEmpty()) {
                logger.info("El campo 'Cant. Func. TIC' es requerido.");
                return new ResponseDTO("El campo 'Cant. Func. TIC' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getCantidadFuncionariosAdmin().isEmpty()) {
                logger.info("El campo 'Cant. Func. Administración' es requerido.");
                return new ResponseDTO("El campo 'Cant. Func. Administración' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getPresupuestoTicAnual().isEmpty()) {
                logger.info("Presupuesto Anula TIC' es requerido.");
                return new ResponseDTO("El campo 'Presupuesto Anula TIC' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getAnho().length() != 4){
                logger.info("El campo 'Año' debe tener 4 digitos.");
                return new ResponseDTO("El campo 'Año' debe tener 4 digitos.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getLinkPlan().isEmpty()) {
                logger.info("El campo 'Link del Plan' es requerido.");
                return new ResponseDTO("El campo 'Link del Plan' es requerido.", HttpStatus.BAD_REQUEST);
            }

            if(!isValidURL(dto.getLinkPlan())) {
                logger.info("El Link del Plan no es válido.");
                return new ResponseDTO("El Link del Plan no es válido.", HttpStatus.BAD_REQUEST);
            }

        try {

            Plan plan = new Plan();
            plan.setAnho(dto.getAnho());
            //plan.setFecha(dto.getFecha());
            plan.setFecha(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFecha()));
            plan.setLinkPlan(dto.getLinkPlan());
            plan.setCantidadFuncionariosTic(dto.getCantidadFuncionariosTic());
            plan.setCantidadFuncionariosAdmin(dto.getCantidadFuncionariosAdmin());
            plan.setPresupuestoTicAnual(dto.getPresupuestoTicAnual());
            plan.setEstado(true);
            planRepository.save(plan);

            //Se obtiene el ultimo registro
            Pageable pageable = PageRequest.of(0, 1);
            List<Plan> topPlanList = planRepository.findTopByOrderByIdPlanDesc(pageable);
            Plan ultimoPlan = topPlanList.isEmpty() ? null : topPlanList.get(0);

            //Auditoria
            auditoriaService.auditar("CREAR",
            Objects.nonNull(plan) ? plan.getIdPlan().toString():null,
            Objects.nonNull(ultimoPlan) ? ultimoPlan:null,
            null, "plan",
            "/create", "/create", "plan/", null, null, null, null);

            return new ResponseDTO("Plan TIC creado con éxito", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseDTO("No se pudo procesar la operación "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    FUNCION EDITAR
    @Transactional
    public ResponseDTO update(Long id, PlanDTO dto) {

        try {
            if(dto.getAnho().isEmpty()) {
                logger.info("El campo 'Año' es requerido");
                return new ResponseDTO("El campo 'Año' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getFecha().isEmpty()) {
                logger.info("El campo 'Fecha' es requerido");
                return new ResponseDTO("El campo 'Fecha' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getCantidadFuncionariosTic().isEmpty()) {
                logger.info("El campo 'Cant. Func. TIC' es requerido");
                return new ResponseDTO("El campo 'Cant. Func. TIC' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getCantidadFuncionariosAdmin().isEmpty()) {
                logger.info("El campo 'Cant. Func. Administración' es requerido");
                return new ResponseDTO("El campo 'Cant. Func. Administración' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getPresupuestoTicAnual().isEmpty()) {
                logger.info("El campo 'Presupuesto Anula TIC' es requerido");
                return new ResponseDTO("El campo 'Presupuesto Anula TIC' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getAnho().length() != 4){
                logger.info("El campo 'Año' debe tener 4 digitos");
                return new ResponseDTO("El campo 'Año' debe tener 4 digitos.", HttpStatus.BAD_REQUEST);
            }
            if(dto.getLinkPlan().isEmpty()) {
                logger.info("El campo 'Link del Plan' es requerido");
                return new ResponseDTO("El campo 'Link del Plan' es requerido.", HttpStatus.BAD_REQUEST);
            }

            if(!isValidURL(dto.getLinkPlan())) {
                logger.info("El Link del Plan no es válido");
                return new ResponseDTO("El Link del Plan no es válido.", HttpStatus.BAD_REQUEST);
            }

            Plan entity = planRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Plan TIC no encontrado con id: " + id));

            ObjectMapper objectMapper = new ObjectMapper();
            Plan entityBefore = objectMapper.convertValue(entity, Plan.class);


            // Actualizar el plan
            entity.setAnho(dto.getAnho());

            // Utilizar SimpleDateFormat para analizar la fecha con el mismo formato que la fecha del nuevo plan
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date fechaNueva = dateFormat.parse(dto.getFecha());
                entity.setFecha(fechaNueva);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            entity.setLinkPlan(dto.getLinkPlan());
            entity.setCantidadFuncionariosTic(dto.getCantidadFuncionariosTic());
            entity.setCantidadFuncionariosAdmin(dto.getCantidadFuncionariosAdmin());
            entity.setPresupuestoTicAnual(dto.getPresupuestoTicAnual());

            try {
                auditoriaService.auditar("MODIFICAR",
                        Objects.nonNull(entity) ? entity.getIdPlan().toString() : null,
                        entity,
                        entityBefore,
                        "plan",
                        "/plan/{id}",
                        "/update",
                        "plan/",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            // Guardar el plan actualizado
            planRepository.save(entity);

            return new ResponseDTO("Plan actualizado con éxito", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseDTO("No se pudo procesar la operación " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseDTO updateStatus(Long id) {
//        planRepository.findById(id).map(plan -> {
//            plan.setEstado((plan.getEstado() ? false : true));
//            return planRepository.save(plan);
//        }).orElseThrow(() -> new BadRequestException("Dominio no existe"));
//        return new ResponseDTO("Estado del Dominio actualizado con éxito", HttpStatus.OK);

        Plan entity = planRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Plan no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Plan entityBefore = objectMapper.convertValue(entity, Plan.class);

        entity.setEstado((entity.getEstado() ? false : true));

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdPlan().toString() : null,
                    entity,
                    entityBefore,
                    "plan",
                    "/plan/{id}",
                    "/update",
                    "plan/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        planRepository.save(entity);

        return new ResponseDTO("Estado del Plan actualizado con éxito", HttpStatus.OK);

    }

    public ResponseDTO delete(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Plan TIC no existe"));

        try {
            // Auditoría antes de la eliminación
            auditoriaService.auditar("BORRAR",
                    Objects.nonNull(plan) ? plan.getIdPlan().toString() : null,
                    null,
                    plan, "plan",
                    "/plan/{id}/delete", "/delete", "plan/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        planRepository.delete(plan);
        return new ResponseDTO("Plan TIC eliminado con éxito", HttpStatus.OK);
    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String anho, String fecha, String linkPlan, String cantidadFuncionariosTic, String presupuestoTicAnual) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Planes TIC");
        reportParameters.put("fecha", fechaFormateada);

        List<PlanReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = planRepository.findPlanIdWithPermission(anho, linkPlan, cantidadFuncionariosTic, presupuestoTicAnual);
        } else {
            data = planRepository.findPlanIdWithoutPermission(anho, linkPlan, cantidadFuncionariosTic, presupuestoTicAnual);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }
}
