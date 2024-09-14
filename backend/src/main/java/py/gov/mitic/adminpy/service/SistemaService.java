package py.gov.mitic.adminpy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.repository.OeeRepository;
import py.gov.mitic.adminpy.repository.SistemaRepository;
import py.gov.mitic.adminpy.repository.UsuarioRepository;
import py.gov.mitic.adminpy.repository.projections.DatosOeeReporteDTO;
import py.gov.mitic.adminpy.repository.projections.SistemasOeeReporteDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;
import py.gov.mitic.adminpy.specification.SearchCriteria;
import py.gov.mitic.adminpy.specification.SearchOperator;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SistemaService extends GenericSpecification<Sistema> {

    private Log logger = LogFactory.getLog(SistemaService.class);

    private final SistemaRepository sistemaRepository;
    private final OeeRepository oeeRepository;

    private final UsuarioRepository usuarioRepository;

    private final AuditoriaService auditoriaService;

    private final ReportService reportService;

    private Map<String, Object> auditMap = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    public SistemaService(SistemaRepository sistemaRepository, OeeRepository oeeRepository , UsuarioRepository usuarioRepository, AuditoriaService auditoriaService, ReportService reportService) {
        this.sistemaRepository = sistemaRepository;
        this.oeeRepository = oeeRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.reportService = reportService;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idSistema, Long id, Boolean permiso, Long idInstitucion, String nombre, String objetoProposito, String areaResponsable, String descripcionOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(id) && id > 0 ? true:false;

        Specification<Sistema> specification = (root, query, cb) -> {
            Join<Sistema, Oee> oeeJoin = root.join("oee", JoinType.LEFT);

            Predicate[] predicates = {
                    equal(cb, root.get("idSistema"), ((Objects.nonNull(idSistema) && idSistema > 0) ? idSistema.toString() : "")),
                    like(cb, root.get("nombre"), (!Objects.isNull(nombre) ? nombre : "")),
                    like(cb, root.get("objetoProposito"), (!Objects.isNull(objetoProposito) ? objetoProposito : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };
        //logger.info("Specification: " + specification);
        Page<Sistema> pageList = sistemaRepository.findAll(specification, paging);

        TableDTO<Sistema> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO create(SistemaDTO dto){
        try {
            if(Objects.isNull(dto.getOee())) {
                logger.info("El campo 'OEE' es requerido");
                return new ResponseDTO("La OEE es un campo requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getNombre())) {
                logger.info("El campo 'Nombre del software' es requerido");
                return new ResponseDTO("El campo 'Nombre del software' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(Objects.isNull(dto.getObjetoProposito())) {
                logger.info("El campo 'Objetivo o propósito del Sistema' es requerido");
                return new ResponseDTO("El campo 'Objetivo o propósito del Sistema' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(Objects.isNull(dto.getAreaResponsable())) {
                logger.info("El campo 'Área técnica responsable' es requerido");
                return new ResponseDTO("El campo 'Área técnica responsable' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(Objects.isNull(dto.getTipoUso())) {
                logger.info("El campo 'Tipo de uso' es requerido");
                return new ResponseDTO("El campo 'Tipo de uso' es requerido.", HttpStatus.BAD_REQUEST);
            }
            Sistema sistema = new Sistema();
            sistema.setNombre(dto.getNombre());
            Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
            sistema.setOee(oee);
            sistema.setObjetoProposito(dto.getObjetoProposito());
            sistema.setAreaResponsable(dto.getAreaResponsable());
            sistema.setTipoUso(dto.getTipoUso());
            //TECNOLOGIA
            sistema.setTecnologiaLenguaje(dto.getTecnologiaLenguaje());
            sistema.setTecnologiaFramework(dto.getTecnologiaFramework());
            sistema.setAnhoCreacion(dto.getAnhoCreacion());
            sistema.setAnhoImplementacion(dto.getAnhoImplementacion());
            sistema.setDesarrolladorFabricante(dto.getDesarrolladorFabricante());
            sistema.setPoseeVigencia(dto.getPoseeVigencia());
            if (dto.getFechaVigencia() != null && !dto.getFechaVigencia().isEmpty()) {
                try {
                    sistema.setFechaVigencia(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaVigencia()));
                } catch (Exception e) {
                    System.out.println("La fecha no es válida");
                }
            }
            //FUENTES
            sistema.setPoseeCodigoFuente(dto.getPoseeCodigoFuente());
            sistema.setLinkCodigoFuente(dto.getLinkCodigoFuente());
            sistema.setLinkProduccion(dto.getLinkProduccion());
            sistema.setPoseeLicencia(dto.getPoseeLicencia());
            sistema.setTipoLicencia(dto.getTipoLicencia());
            //SOPORTE
            sistema.setPoseeContratoMantenimiento(dto.getPoseeContratoMantenimiento());
            sistema.setTipoSoporte(dto.getTipoSoporte());
            sistema.setDataCenterInfraestructura(dto.getDataCenterInfraestructura());
            sistema.setCostoDesarrollo(dto.getCostoDesarrollo());
            sistema.setCostoMantenimiento(dto.getCostoMantenimiento());
            sistema.setListaDesarrolladores(dto.getListaDesarrolladores());
            sistemaRepository.save(sistema);

            try {
                auditoriaService.auditar("CREAR",
                        Objects.nonNull(sistema) ? sistema.getIdSistema().toString() : null,
                        sistema,
                        Objects.nonNull(sistema) ? sistema : null,
                        "sistema",
                        "sistema/",
                        "/create",
                        "sistema/create",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                logger.error("Error al realizar la auditoría.", e);
            }


            return new ResponseDTO("Sistema actualizado con éxito", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseDTO update(Long id, SistemaDTO dto){

        try {
            Optional<Sistema> optionalSistema = sistemaRepository.findById(id);
            if(Objects.isNull(optionalSistema) || !optionalSistema.isPresent()) {
                logger.info("Sistema no existe");
                return new ResponseDTO("Sistema no existe", HttpStatus.BAD_REQUEST);
            }

            if(sistemaRepository.findExistByNombre(dto.getNombre(), id)) {
                logger.info("El nombre del sistema ya está siendo utilizado");
                return new ResponseDTO("El nombre del sistema ya está siendo utilizado.", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getOee())) {
                logger.info("El campo 'OEE' es requerido");
                return new ResponseDTO("La OEE es un campo requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getNombre())) {
                logger.info("El campo 'Nombre del software' es requerido");
                return new ResponseDTO("El campo 'Nombre del software' es requerido.", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getObjetoProposito())) {
                logger.info("El campo 'Objetivo o propósito del Sistema' es requerido");
                return new ResponseDTO("El campo 'Objetivo o propósito del Sistema' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(Objects.isNull(dto.getAreaResponsable())) {
                logger.info("El campo 'Área técnica responsable' es requerido");
                return new ResponseDTO("El campo 'Área técnica responsable' es requerido.", HttpStatus.BAD_REQUEST);
            }
            if(Objects.isNull(dto.getTipoUso())) {
                logger.info("El campo 'Tipo de uso' es requerido");
                return new ResponseDTO("El campo 'Tipo de uso' es requerido.", HttpStatus.BAD_REQUEST);
            }
            Sistema entity = sistemaRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Sistema no existe"));

            ObjectMapper objectMapper = new ObjectMapper();
            Sistema entityBefore = objectMapper.convertValue(entity, Sistema.class);

            entity.setNombre(dto.getNombre());
            Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
            entity.setOee(oee);
            entity.setObjetoProposito(dto.getObjetoProposito());
            entity.setAreaResponsable(dto.getAreaResponsable());
            entity.setTipoUso(dto.getTipoUso());
            //TECNOLOGIA
            entity.setTecnologiaLenguaje(dto.getTecnologiaLenguaje());
            entity.setTecnologiaFramework(dto.getTecnologiaFramework());
            entity.setAnhoCreacion(dto.getAnhoCreacion());
            entity.setAnhoImplementacion(dto.getAnhoImplementacion());
            entity.setDesarrolladorFabricante(dto.getDesarrolladorFabricante());
            entity.setPoseeVigencia(dto.getPoseeVigencia());
            if (dto.getFechaVigencia() != null && !dto.getFechaVigencia().isEmpty()) {
                try {
                    entity.setFechaVigencia(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaVigencia()));
                } catch (Exception e) {
                    System.out.println("La fecha no es válida");
                }
            }


            //FUENTES
            entity.setPoseeCodigoFuente(dto.getPoseeCodigoFuente());
            entity.setLinkCodigoFuente(dto.getLinkCodigoFuente());
            entity.setLinkProduccion(dto.getLinkProduccion());
            entity.setPoseeLicencia(dto.getPoseeLicencia());
            entity.setTipoLicencia(dto.getTipoLicencia());
            //SOPORTE
            entity.setPoseeContratoMantenimiento(dto.getPoseeContratoMantenimiento());
            entity.setTipoSoporte(dto.getTipoSoporte());
            entity.setDataCenterInfraestructura(dto.getDataCenterInfraestructura());
            entity.setCostoDesarrollo(dto.getCostoDesarrollo());
            entity.setCostoMantenimiento(dto.getCostoMantenimiento());
            entity.setListaDesarrolladores(dto.getListaDesarrolladores());

            try {
                auditoriaService.auditar("MODIFICAR",
                        Objects.nonNull(entity) ? entity.getIdSistema().toString() : null,
                        entity,
                        entityBefore,
                        "sistema",
                        "/sistema/{id}",
                        "/update",
                        "sistema/update",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            sistemaRepository.save(entity);
            return new ResponseDTO("Sistema actualizado con éxito", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }

    public ResponseDTO updateStatus(Long id) {
        Sistema entity = sistemaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sistema no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Sistema entityBefore = objectMapper.convertValue(entity, Sistema.class);

        String estado = entity.getEstado();

        if ("A".equals(estado)) {
            entity.setEstado("I");
        } else if ("I".equals(estado)) {
            entity.setEstado("A");
        }

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdSistema().toString() : null,
                    entity,
                    entityBefore,
                    "sistema",
                    "/sistema/{id}/update-status",
                    "/updateStatus",
                    "sistema/updateStatus",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        sistemaRepository.save(entity);

        return new ResponseDTO("Estado del Sistema actualizado con éxito", HttpStatus.OK);
    }

    public ResponseDTO delete(Long id) {
        Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sistema no existe"));

        try {
            // Auditoría antes de la eliminación
            auditoriaService.auditar("BORRAR",
                    Objects.nonNull(sistema) ? sistema.getIdSistema().toString() : null,
                    null,
                    sistema, "sistema",
                    "/sistema/{id}/delete", "/delete", "sistema/", null, null, null, null);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        sistemaRepository.delete(sistema);

        return new ResponseDTO("Sistema eliminado con éxito", HttpStatus.OK);
    }

    public byte[] generateReport(String format, Long id, String reportFileName, Boolean permiso, String nombreSistema, String objetivoSistema, String areaResponsable, String descripcionOee) {

        Date fechaActual = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        String fechaFormateada = dateFormat.format(fechaActual);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("title", "Reporte de Sistemas");
        reportParameters.put("fecha", fechaFormateada);

        List<SistemasOeeReporteDTO> data;

        if (Boolean.TRUE.equals(permiso)) {
            data = sistemaRepository.findSistemasIdWithoutPermission(nombreSistema, objetivoSistema, areaResponsable, descripcionOee);
        } else {
            data = sistemaRepository.findSistemasIdWithPermission(nombreSistema, objetivoSistema, areaResponsable, descripcionOee,id);
        }

        return reportService.generateReport(format, reportFileName, reportParameters, data);

    }
}
