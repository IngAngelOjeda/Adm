package py.gov.mitic.adminpy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.ServicioDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.repository.*;
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
public class ServicioService extends GenericSpecification<Servicio> {

    private Log logger = LogFactory.getLog((ServicioService.class));

    private final ServicioRepository serviceRepository;

    private final OeeRepository oeeRepository;

    private final EtiquetaRepository etiquetaRepository;

    private final RequisitoRepository requisitoRepository;

    private final ClasificadorRepository clasificadorRepository;

    private final ServicioEtiquetaRepository servicioEtiquetaRepository;

    private final ServicioRequisitoRepository servicioRequisitoRepository;

    private final ServicioClasificadorRepository servicioClasificadorRepository;

    private final AuditoriaService auditoriaService;

    private final ReportService reportService;

    private Map<String, Object> auditMap = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    public ServicioService(
            ServicioRepository servicioRepository,
            AuditoriaService auditoriaService,
            ReportService reportService,
            OeeRepository oeeRepository,
            EtiquetaRepository etiquetaRepository,
            RequisitoRepository requisitoRepository,
            ClasificadorRepository clasificadorRepository,
            ServicioEtiquetaRepository servicioEtiquetaRepository,
            ServicioRequisitoRepository servicioRequisitoRepository,
            ServicioClasificadorRepository servicioClasificadorRepository
    ){
            this.serviceRepository = servicioRepository;
            this.auditoriaService = auditoriaService;
            this.reportService = reportService;
            this.oeeRepository = oeeRepository;
            this.etiquetaRepository = etiquetaRepository;
            this.requisitoRepository = requisitoRepository;
            this.clasificadorRepository = clasificadorRepository;
            this.servicioEtiquetaRepository = servicioEtiquetaRepository;
            this.servicioRequisitoRepository = servicioRequisitoRepository;
            this.servicioClasificadorRepository = servicioClasificadorRepository;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, Boolean permiso, Long idServicio, String nombreServicio, String descripcionServicio, String descripcionOee){
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

        Boolean showOption = Objects.nonNull(id) && id > 0 ? true:false;

        Specification<Servicio> specification = (root, query, cb) -> {
            Join<Servicio, Oee> oeeJoin = root.join( "oee", JoinType.LEFT);

            Predicate[] predicates = {

                    equal(cb, root.get("idServicio"), ((Objects.nonNull(idServicio) && idServicio > 0 ? true : false) ? idServicio.toString() : "")),
                    like(cb, root.get("nombreServicio"), (!Objects.isNull(nombreServicio) ? nombreServicio : "")),
                    like(cb, root.get("descripcionServicio"), (!Objects.isNull(nombreServicio) ? descripcionServicio : "")),
                    like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : ""))
            };

            if (permiso != true) {
                predicates = Arrays.copyOf(predicates, predicates.length + 1);
                predicates[predicates.length - 1] = equal(cb, oeeJoin.get("id"), (showOption ? id.toString() : ""));
            }

            return query.where(cb.and(predicates)).distinct(true).getRestriction();

        };

        Page<Servicio> pageList = serviceRepository.findAll(specification, paging);
        TableDTO<Servicio> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        //		auditMap = new HashMap<>();
        //      auditMap.put("paging", paging.toString());
        //		auditMap.put("page", (int) pageList.getNumber() +1);
        //		auditMap.put("totalRecords", (int) pageList.getTotalElements());
        //		while (auditMap.values().remove(null));
        //		while (auditMap.values().remove(""));
        //		params = new HashMap<>();
        //		params.put("idServicio", idServicio);
        //		params.put("nombreServicio", nombreServicio);
        //		params.put("descripcionServicio", descripcionServicio);
        //		params.put("descripcionOee", descripcionOee);
        //		params.put("id", id);
        //		while (params.values().remove(null));
        //		while (params.values().remove(""));
        //		try {
        //			auditoriaService.auditar("LISTAR", "S/I",
        //					auditMap, null, "servicio", "/getAll/{params}", "/servicio", "servicio/", null, null, null, params);
        //		} catch (UnknownHostException e) {
        //			e.printStackTrace();
        //		}

        return new ResponseDTO(tableDTO, HttpStatus.OK);

    }

    public ResponseDTO create(ServicioDTO dto){

        try {

            if(Objects.isNull(dto.getNombreServicio())) {
                logger.info("El campo 'Nombre del servicio' es requerido");
                return new ResponseDTO("El Nombre del servicio es un campo requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getDescripcionServicio())) {
                logger.info("El campo 'De qué se trata el servicio' es requerido");
                return new ResponseDTO("El campo 'De qué se trata el servicio' es requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getOee())) {
                logger.info("El campo 'OEE' es requerido");
                return new ResponseDTO("La OEE es un campo requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getEtiqueta())) {
                logger.info("El campo 'Etiqueta' es requerido");
                return new ResponseDTO("La Etiqueta es un campo requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getRequisito())) {
                logger.info("El campo 'Requisito' es requerido");
                return new ResponseDTO("El campo 'Requisito' es requerido", HttpStatus.BAD_REQUEST);
            }

            if(Objects.isNull(dto.getClasificador())) {
                logger.info("El campo 'Categoria' es requerido");
                return new ResponseDTO("El campo 'Categoria' es requerido", HttpStatus.BAD_REQUEST);
            }

            if(!isValidURL(dto.getUrlOnline())) {
                logger.info("La Url o enlace al servicio no es válido.");
                return new ResponseDTO("La Url o enlace al servicio no es válido.", HttpStatus.BAD_REQUEST);
            }

            Servicio servicio = new Servicio();
            servicio.setNombreServicio(dto.getNombreServicio());
            servicio.setDescripcionServicio(dto.getDescripcionServicio());

            Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
            servicio.setOee(oee);

            // Etiquetas
            List<Etiqueta> etiquetas = dto.getEtiqueta().stream()
                    .map(etiquetaDTO -> etiquetaRepository.findById(etiquetaDTO.getIdEtiqueta()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            servicio.setEtiqueta(etiquetas);

            //Requisitos
            List<Requisito> requisitos = dto.getRequisito().stream()
                    .map(requisitoDTO -> requisitoRepository.findById(requisitoDTO.getIdRequisito()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            servicio.setRequisito(requisitos);

            //Clasificador
            List<Clasificador> clasificador = dto.getClasificador().stream()
                    .map(clasificadorDTO -> clasificadorRepository.findById(clasificadorDTO.getIdClasificador()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            servicio.setClasificador(clasificador);

            servicio.setDestacado(dto.getDestacado());
            servicio.setUrlOnline(dto.getUrlOnline());
            servicio.setEstadoServicio("A");
            servicio.setFechaCreacion(new Date());
            serviceRepository.save(servicio);

            try {
                auditoriaService.auditar("CREAR",
                        Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                        servicio,
                        null,
                        "servicio",
                        "servicio/",
                        "/create",
                        "servicio/create",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                logger.error("Error al realizar la auditoría.", e);
            }

            //Etiquetas
            try {
                auditoriaService.auditar("CREAR",
                        Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                        etiquetas,
                        null,
                        "servicio_etiqueta",
                        "servicio/",
                        "/create",
                        "servicio/create",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                logger.error("Error al realizar la auditoría.", e);
            }

            //Requisitos

            try {
                auditoriaService.auditar("CREAR",
                        Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                        requisitos,
                        null,
                        "servicio_requisito",
                        "servicio/",
                        "/create",
                        "servicio/create",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                logger.error("Error al realizar la auditoría.", e);
            }

            //Clasificador

            try {
                auditoriaService.auditar("CREAR",
                        Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                        clasificador,
                        null,
                        "servicio_clasificador",
                        "servicio/",
                        "/create",
                        "servicio/create",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                logger.error("Error al realizar la auditoría.", e);
            }

            return new ResponseDTO("Servicio guardado con exito", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return  new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }

    public ResponseDTO update(Long id, ServicioDTO dto){
        try {
            return  serviceRepository.findById(id).map(servicio -> {
                if(Objects.isNull(dto.getNombreServicio())) {
                    logger.info("El campo 'Nombre del servicio' es requerido");
                    return new ResponseDTO("El Nombre del servicio es un campo requerido", HttpStatus.BAD_REQUEST);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                Servicio entityBefore = objectMapper.convertValue(servicio, Servicio.class);

                if(Objects.isNull(dto.getDescripcionServicio())) {
                    logger.info("El campo 'De qué se trata el servicio' es requerido");
                    return new ResponseDTO("El campo 'De qué se trata el servicio' es requerido", HttpStatus.BAD_REQUEST);
                }

                if(Objects.isNull(dto.getOee())) {
                    logger.info("El campo 'OEE' es requerido");
                    return new ResponseDTO("La OEE es un campo requerido", HttpStatus.BAD_REQUEST);
                }

                if(Objects.isNull(dto.getEtiqueta())) {
                    logger.info("El campo 'Etiqueta' es requerido");
                    return new ResponseDTO("La Etiqueta es un campo requerido", HttpStatus.BAD_REQUEST);
                }

                if(Objects.isNull(dto.getRequisito())) {
                    logger.info("El campo 'Requisito' es requerido");
                    return new ResponseDTO("El campo 'Requisito' es requerido", HttpStatus.BAD_REQUEST);
                }

                if(Objects.isNull(dto.getClasificador())) {
                    logger.info("El campo 'Categoria' es requerido");
                    return new ResponseDTO("El campo 'Categoria' es requerido", HttpStatus.BAD_REQUEST);
                }

                if(!isValidURL(dto.getUrlOnline())) {
                    logger.info("La Url o enlace al servicio no es válido.");
                    return new ResponseDTO("La Url o enlace al servicio no es válido.", HttpStatus.BAD_REQUEST);
                }


                servicio.setNombreServicio(dto.getNombreServicio());
                servicio.setDescripcionServicio(dto.getDescripcionServicio());

                Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
                servicio.setOee(oee);

                ///////////////////////////////ETIQUETAS///////////////////////////////////
                List<ServicioEtiqueta> servicioEtiquetas = servicioEtiquetaRepository.findByServcioId(id);

                List<Etiqueta> etiquetaRequisitosAut = servicioEtiquetaRepository.findByServcioId(id).stream()
                        .map(ServicioEtiqueta::getEtiqueta) //
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                for (ServicioEtiqueta servicioEtiqueta : servicioEtiquetas) {
                    servicioEtiquetaRepository.delete(servicioEtiqueta);
                }
                List<Etiqueta> etiquetas = dto.getEtiqueta().stream()
                        .map(etiquetaDTO -> etiquetaRepository.findById(etiquetaDTO.getIdEtiqueta()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                servicio.setEtiqueta(etiquetas);

                ///////////////////////////////REQUISITOS///////////////////////////////////
                List<ServicioRequisito> servicioRequisitos = servicioRequisitoRepository.findByServcioId(id);

                List<Requisito> servicioRequisitosAut = servicioRequisitoRepository.findByServcioId(id).stream()
                        .map(ServicioRequisito::getRequisito) //
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                for (ServicioRequisito servicioRequisito : servicioRequisitos) {
                    servicioRequisitoRepository.delete(servicioRequisito);
                }

                List<Requisito> requisitos = dto.getRequisito().stream()
                        .map(requisitoDTO -> requisitoRepository.findById(requisitoDTO.getIdRequisito()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                servicio.setRequisito(requisitos);

                ///////////////////////////////CLASIFICADOR///////////////////////////////////
                List<ServicioClasificador> servicioClasificadors = servicioClasificadorRepository.findByServcioId(id);

                List<Clasificador> servicioClasificadorAut = servicioClasificadorRepository.findByServcioId(id).stream()
                        .map(ServicioClasificador::getClasificador) //
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                for (ServicioClasificador servicioClasificador : servicioClasificadors) {
                    servicioClasificadorRepository.delete(servicioClasificador);
                }

                List<Clasificador> clasificador = dto.getClasificador().stream()
                        .map(clasificadorDTO -> clasificadorRepository.findById(clasificadorDTO.getIdClasificador()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                servicio.setClasificador(clasificador);

                servicio.setDestacado(dto.getDestacado());
                servicio.setUrlOnline(dto.getUrlOnline());
                servicio.setFechaModificacion(new Date());

                servicio = serviceRepository.save(servicio);

                try {
                    auditoriaService.auditar("MODIFICAR",
                            Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                            servicio,
                            entityBefore,
                            "servicio",
                            "/servicio/{id}",
                            "/update",
                            "servicio/update",
                            null,
                            null,
                            null,
                            null);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return new ResponseDTO("Error en la auditoría", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                //Etiquetas
                try {
                    auditoriaService.auditar("MODIFICAR",
                            Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                            etiquetas,
                            etiquetaRequisitosAut,
                            "servicio_etiqueta",
                            "/servicio/{id}",
                            "/update",
                            "servicio/update",
                            null,
                            null,
                            null,
                            null);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return new ResponseDTO("Error en la auditoría", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                //Requisitos

                try {
                    auditoriaService.auditar("MODIFICAR",
                            Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                            requisitos,
                            servicioRequisitosAut,
                            "servicio_requisito",
                            "/servicio/{id}",
                            "/update",
                            "servicio/update",
                            null,
                            null,
                            null,
                            null);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return new ResponseDTO("Error en la auditoría", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                //Clasificador

                try {
                    auditoriaService.auditar("MODIFICAR",
                            Objects.nonNull(servicio) ? servicio.getIdServicio().toString() : null,
                            clasificador,
                            servicioClasificadorAut,
                            "servicio_clasificador",
                            "/servicio/{id}",
                            "/update",
                            "servicio/update",
                            null,
                            null,
                            null,
                            null);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return new ResponseDTO("Error en la auditoría", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseDTO("Servicio actualizado con éxito", HttpStatus.OK);

            }).orElseThrow(() -> new BadRequestException("El servicio no existe"));

        } catch (BadRequestException e) {
            logger.info(e.getMessage());
            return new ResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud", e);
            return new ResponseDTO("Error al procesar la solicitud", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseDTO updateStatus(Long id){
//        serviceRepository.findById(id).map(servicio -> {
//
//            String estado = servicio.getEstadoServicio();
//
//            if(estado.equals("A")){
//                servicio.setEstadoServicio("I");
//            }else if (estado.equals("I")) {
//                servicio.setEstadoServicio("A");
//            }
//
//            return  serviceRepository.save(servicio);
//        }).orElseThrow(() -> new BadRequestException("Servicio no existe"));
//
//        return  new ResponseDTO("Estado del Servicio actualizado con exito", HttpStatus.OK);

        Servicio entity = serviceRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Servicio no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Servicio entityBefore = objectMapper.convertValue(entity, Servicio.class);

        String estado = entity.getEstadoServicio();

        if ("A".equals(estado)) {
            entity.setEstadoServicio("I");
        } else if ("I".equals(estado)) {
            entity.setEstadoServicio("A");
        }

        entity.setFechaModificacion(new Date());

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdServicio().toString() : null,
                    entity,
                    entityBefore,
                    "servicio",
                    "/servicio/update/{id}",
                    "/update",
                    "servicio/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        serviceRepository.save(entity);

        return  new ResponseDTO("Estado del Servicio actualizado con exito", HttpStatus.OK);

    }

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

    public ResponseDTO updateStatusAllService(Long idOee){

        try {
            // Obtener todos los servicios con el idOee dado
            List<Servicio> servicios = serviceRepository.findByOeeId(idOee);

            // Actualizar estadoServicio y fechaModificacion para cada servicio
            for (Servicio servicio : servicios) {
                servicio.setEstadoServicio("A");
                servicio.setFechaModificacion(new Date());
            }

            // Guardar los cambios en la base de datos
            serviceRepository.saveAll(servicios);

            return new ResponseDTO("Todos los Estados de los Servicios se actualizaron con éxito", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("Error al actualizar el estado de los servicios", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseDTO confirmAllServices(Long idOee ){
        try {
            // Obtener todos los servicios con el idOee dado
            List<Servicio> servicios = serviceRepository.findByOeeId(idOee);

            // Actualizar fechaModificacion para cada servicio
            for (Servicio servicio : servicios) {
                servicio.setFechaModificacion(new Date());
            }

            // Guardar los cambios en la base de datos
            serviceRepository.saveAll(servicios);

            return new ResponseDTO("Se confirmaron los servicios con éxito", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("Error al confirmar los servicios", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
