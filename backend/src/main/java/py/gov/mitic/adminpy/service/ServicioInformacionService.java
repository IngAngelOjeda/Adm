package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.repository.ServicioInformacionRepository;
import py.gov.mitic.adminpy.repository.ServicioRepository;
import py.gov.mitic.adminpy.repository.TipoDatoRepository;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioInformacionService extends GenericSpecification<ServicioInformacion>{

    private Log logger = LogFactory.getLog(RequisitoService.class);
    private final ServicioInformacionRepository servicioInformacionRepository;
    private final TipoDatoRepository tipoDatoRepository;
    private final ServicioRepository servicioRepository;
    private final AuditoriaService auditoriaService;


    public ServicioInformacionService(ServicioInformacionRepository servicioInformacionRepository, ServicioRepository servicioRepository, TipoDatoRepository tipoDatoRepository, AuditoriaService auditoriaService){

        this.servicioInformacionRepository = servicioInformacionRepository;
        this.servicioRepository = servicioRepository;
        this.tipoDatoRepository = tipoDatoRepository;
        this.auditoriaService = auditoriaService;

    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long idServicio, Long idServicioInformacion, String descripcionServicioInformacion, String descripcionTipoDato) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
        Boolean showOption = Objects.nonNull(idServicio) && idServicio > 0 ? true:false;

        Specification<ServicioInformacion> specification = (root, query, cb) -> {
            Join<Servicio, ServicioInformacion> ServicioJoin = root.join("servicio", JoinType.LEFT);
            Join<TipoDato, ServicioInformacion> TipoDatoJoin = root.join("tipoDato", JoinType.LEFT);

            return query.where(cb.and(
                            equal(cb, root.get("idServicioInformacion"), ((Objects.nonNull(idServicioInformacion) && idServicioInformacion > 0 ? true:false) ? idServicioInformacion.toString() : "")),
                            like(cb, root.get("descripcionServicioInformacion"), (!Objects.isNull(descripcionServicioInformacion) ? descripcionServicioInformacion : "")),
                            like(cb, TipoDatoJoin.get("descripcionTipoDato"), (!Objects.isNull(descripcionTipoDato) ? descripcionTipoDato : "")),
                            equal(cb, ServicioJoin.get("idServicio"),  (showOption ? idServicio.toString() : ""))
                    ))
                    .distinct(true)
                    .getRestriction();
        };
        Page<ServicioInformacion> pageList = servicioInformacionRepository.findAll(specification, paging);

        TableDTO<ServicioInformacion> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        //		auditMap = new HashMap<>();
//      auditMap.put("paging", paging.toString());
//		auditMap.put("page", (int) pageList.getNumber() +1);
//		auditMap.put("totalRecords", (int) pageList.getTotalElements());
//		while (auditMap.values().remove(null));
//		while (auditMap.values().remove(""));
//		params = new HashMap<>();
//		params.put("idServicioInformacion", idServicioInformacion);
//		params.put("descripcionServicioInformacion", descripcionServicioInformacion);
//		params.put("descripcionTipoDato", descripcionTipoDato);
//		params.put("idServicio", idServicio);
//		while (params.values().remove(null));
//		while (params.values().remove(""));
//      verificar los parametros que se le envia
//		try {
//			auditoriaService.auditar("LISTAR", "S/I",
//					auditMap, null, "servicio_informacion", "/getAll/{params}", "/servicioInformacion", "servicioInformacion/", null, null, null, params);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO create(ServicioInformacionDTO dto) {


        if(Objects.isNull(dto.getDescripcionServicioInformacion())) {
            logger.info("El campo 'Información del tipo de dato' es requerido");
            return new ResponseDTO("El campo 'Información del tipo de dato' es requerido.", HttpStatus.BAD_REQUEST);
        }

        if(Objects.isNull(dto.getTipoDato())) {
            logger.info("El campo 'Tipo de dato' es requerido");
            return new ResponseDTO("El campo 'Tipo de dato' es requerido.", HttpStatus.BAD_REQUEST);
        }

        if(Objects.isNull(dto.getServicio())) {
            logger.info("La información no está asociada a ningún servicio.");
            return new ResponseDTO("La información no está asociada a ningún servicio.", HttpStatus.BAD_REQUEST);
        }

        ServicioInformacion servicioInformacion = new ServicioInformacion();
        servicioInformacion.setDescripcionServicioInformacion(dto.getDescripcionServicioInformacion());
        TipoDato tipodato = tipoDatoRepository.findById(dto.getTipoDato().getIdTipoDato()).get();
        servicioInformacion.setTipoDato(tipodato);
        Servicio servicio = servicioRepository.findById(dto.getServicio().getIdServicio()).get();
        servicioInformacion.setServicio(servicio);
        servicioInformacion.setEstadoServicioInformacion("A");
        servicioInformacion.setFechaHoraCreacion(new Date());
        servicioInformacionRepository.save(servicioInformacion);
        return new ResponseDTO("Información del servicio creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, ServicioInformacionDTO dto) {

        if(Objects.isNull(dto.getDescripcionServicioInformacion())) {
            logger.info("El campo 'Información del tipo de dato' es requerido");
            return new ResponseDTO("El campo 'Información del tipo de dato' es requerido.", HttpStatus.BAD_REQUEST);
        }

        if(Objects.isNull(dto.getTipoDato())) {
            logger.info("El campo 'Tipo de dato' es requerido");
            return new ResponseDTO("El campo 'Tipo de dato' es requerido.", HttpStatus.BAD_REQUEST);
        }

        servicioInformacionRepository.findById(id).map(servicioInformacion -> {

            servicioInformacion.setDescripcionServicioInformacion(dto.getDescripcionServicioInformacion());
            TipoDato tipodato = tipoDatoRepository.findById(dto.getTipoDato().getIdTipoDato()).get();
            servicioInformacion.setTipoDato(tipodato);
            return servicioInformacionRepository.save(servicioInformacion);
        }).orElseThrow(() -> new BadRequestException("Error al actualizar"));
        return new ResponseDTO("Información del servicio actualizado con éxito", HttpStatus.OK);
    }

    public ResponseDTO updateStatus(Long id) {
        servicioInformacionRepository.findById(id).map(servicioInformacion -> {

            String estado = servicioInformacion.getEstadoServicioInformacion();
            if(estado.equals("A")){
                servicioInformacion.setEstadoServicioInformacion("I");
            }else if (estado.equals("I")) {
                servicioInformacion.setEstadoServicioInformacion("A");
            }

            return servicioInformacionRepository.save(servicioInformacion);
        }).orElseThrow(() -> new BadRequestException("Información del servicio no existe"));
        return new ResponseDTO("Estado del la Información actualizada con éxito", HttpStatus.OK);
    }

    public ResponseDTO delete(Long id) {
        ServicioInformacion servicioInformacion = servicioInformacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Información del servicio no existe"));

        servicioInformacionRepository.delete(servicioInformacion);

        return new ResponseDTO("Información del servicio eliminado con éxito", HttpStatus.OK);
    }


}
