package py.gov.mitic.adminpy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import py.gov.mitic.adminpy.model.dto.CommonDTO;
import py.gov.mitic.adminpy.model.dto.OeeDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.RangoIp;
import py.gov.mitic.adminpy.repository.OeeRepository;
import py.gov.mitic.adminpy.repository.UsuarioRepository;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import javax.ws.rs.BadRequestException;

import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OeeService extends GenericSpecification<Oee> {

	private Log logger = LogFactory.getLog(OeeService.class);

    private final OeeRepository oeeRepository;

	private final UsuarioRepository usuarioRepository;

	private final AuditoriaService auditoriaService;

	private final ReportService reportService;
    
    private Map<String, Object> auditMap = new HashMap<>();

	private Map<String, Object> params = new HashMap<>();

	public OeeService(OeeRepository oeeRepository, UsuarioRepository usuarioRepository, AuditoriaService auditoriaService, ReportService reportService) {
		this.oeeRepository = oeeRepository;
		this.usuarioRepository = usuarioRepository;
		this.auditoriaService = auditoriaService;
		this.reportService = reportService;
	}

	public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String descripcionOee, String descripcionCorta, String urlOee) {
        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

		Specification<Oee> specification = (root, query, cb) -> {
        	
        	return query.where(cb.and(
					equal(cb, root.get("id"), ((Objects.nonNull(id) && id > 0 ? true:false) ? id.toString() : "")),
							like(cb, root.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : "")),
							like(cb, root.get("descripcionCorta"), (!Objects.isNull(descripcionCorta) ? descripcionCorta : "")),
							like(cb, root.get("urlOee"), (!Objects.isNull(urlOee) ? urlOee : ""))
			))
        	.distinct(true)
        	.getRestriction();
		};
		
		Page<Oee> pageList = oeeRepository.findAll(specification, paging);
        
		TableDTO<Oee> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
		
//		auditMap = new HashMap<>();
//        auditMap.put("paging", paging.toString());
//		auditMap.put("page", (int) pageList.getNumber() +1);
//		auditMap.put("totalRecords", (int) pageList.getTotalElements());
//		while (auditMap.values().remove(null));
//		while (auditMap.values().remove(""));
//		params = new HashMap<>();
//		params.put("id", id);
//		params.put("descripcionOee", descripcionOee);
//		params.put("descripcionCorta", descripcionCorta);
//		while (params.values().remove(null));
//		while (params.values().remove(""));
//		try {
//			auditoriaService.auditar("LISTAR", "S/I",
//					auditMap, null, "oee", "/getAll/{params}", "/oee", "oee/", null, null, null, params);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		
		
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO getList() {
        List<CommonDTO> list = oeeRepository.findByEstadoOee("A")
				.stream()
				.map(o -> new CommonDTO(o.getId(), o.getDescripcionOee()))
				.collect(Collectors.toList());
        return new ResponseDTO(list, HttpStatus.OK);
    }
	@Transactional
    public ResponseDTO create(OeeDTO dto) {

		if(Objects.isNull(dto.getDescripcionCorta())) {
			logger.info("El campo 'Abreviatura' es requerido");
			return new ResponseDTO("El campo 'Abreviatura' es requerido.", HttpStatus.BAD_REQUEST);
		}

		if(Objects.isNull(dto.getDescripcionOee())) {
			logger.info("El campo 'Nombre' es requerido");
			return new ResponseDTO("El campo 'Nombre' es requerido.", HttpStatus.BAD_REQUEST);
		}

    	Oee oee = new Oee();
        oee.setDescripcionOee(dto.getDescripcionOee());
		oee.setDescripcionCorta(dto.getDescripcionCorta());
        oee.setEstadoOee("A");
		oee.setFechaCreacion(new Date());
		oeeRepository.save(oee);

		try {
			auditoriaService.auditar("CREAR",
					Objects.nonNull(oee) ? oee.getId().toString() : null,
					oee,
					Objects.nonNull(oee) ? oee : null,
					"oee",
					"oee/",
					"/save",
					"instituciones/save",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			logger.error("Error al realizar la auditoría.", e);
		}

        return new ResponseDTO("Institución creada con éxito", HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO update(Long id, OeeDTO dto){

		Optional<Oee> optionalOee = oeeRepository.findByDescripcionOeeIgnoreCaseAndIdNot(dto.getDescripcionOee().toUpperCase(), id);
		if(optionalOee.isPresent()) {
			return new ResponseDTO("Ya se encuentra registrado el OEE: " + dto.getDescripcionOee(), HttpStatus.BAD_REQUEST);
		}

    	if(Objects.isNull(dto.getDescripcionCorta())) {
			logger.info("El campo 'Abreviatura' es requerido");
			return new ResponseDTO("El campo 'Abreviatura' es requerido.", HttpStatus.BAD_REQUEST);
		}

		if(Objects.isNull(dto.getDescripcionOee())) {
			logger.info("El campo 'Nombre' es requerido");
			return new ResponseDTO("El campo 'Nombre' es requerido.", HttpStatus.BAD_REQUEST);
		}

		Oee entity = oeeRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Institución no existe"));

		ObjectMapper objectMapper = new ObjectMapper();
		Oee entityBefore = objectMapper.convertValue(entity, Oee.class);

		entity.setDescripcionOee(dto.getDescripcionOee().toUpperCase());
		entity.setDescripcionCorta(dto.getDescripcionCorta().toUpperCase());
		entity.setFechaModificacion(new Date());

		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(entity) ? entity.getId().toString() : null,
					entity,
					entityBefore,
					"oee",
					"/oee/{id}",
					"/update",
					"oee/update",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		oeeRepository.save(entity);

        return new ResponseDTO("OEE actualizada con éxito", HttpStatus.OK);
    }



	public ResponseDTO delete(Long id) {
		Oee entity = oeeRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Institución no existe"));

		ObjectMapper objectMapper = new ObjectMapper();
		Oee entityBefore = objectMapper.convertValue(entity, Oee.class);

		String estado = entity.getEstadoOee();

		if ("A".equals(estado)) {
			entity.setEstadoOee("I");
		} else if ("I".equals(estado)) {
			entity.setEstadoOee("A");
		}

		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(entity) ? entity.getId().toString() : null,
					entity,
					entityBefore,
					"institucion",
					"/instituciones/delete/{id}",
					"/delete",
					"instituciones/delete",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		oeeRepository.save(entity);

		return new ResponseDTO("Registro eliminado con éxito", HttpStatus.OK);
	}





	public byte[] generateReport(String format, String reportFileName) {

		Map<String, Object> reportParameters = new HashMap<>();
		reportParameters.put("title", "Reporte de OEE");

		List<Oee> data = oeeRepository.findByEstadoOee("A");

		return reportService.generateReport(format, reportFileName, reportParameters, data);

	}
}
