package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.repository.AuditoriaRepository;
import py.gov.mitic.adminpy.repository.OeeRepository;
import py.gov.mitic.adminpy.repository.UsuarioRepository;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;
import py.gov.mitic.adminpy.specification.SearchCriteria;
import py.gov.mitic.adminpy.specification.SearchOperator;

@Service
public class AuditoriaService extends GenericSpecification<Auditoria> {

	private final AuditoriaRepository auditoriaRepo;

	private final UsuarioRepository usuarioRepository;
	private final OeeRepository oeeRepository;

	private static final String SEPARATOR = ",";

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public AuditoriaService(AuditoriaRepository auditoriaRepo, UsuarioRepository usuarioRepository, OeeRepository oeeRepository) {
		this.auditoriaRepo = auditoriaRepo;
		this.usuarioRepository = usuarioRepository;
		this.oeeRepository = oeeRepository;
	}

	public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id,
							  String nombreUsuario, String metodo, String modulo, String accion, String rangoFecha, Long idOee, Long idUsuario, String descripcionOee, String nombreTabla) {

		Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

		Boolean showOption = Objects.nonNull(idOee) && idOee > 0 ? true : false;
		Boolean showOption2 = Objects.nonNull(idUsuario) && idUsuario > 0 ? true : false;

		Specification<Auditoria> specification = (root, query, cb) -> {
			Join<Auditoria, Oee> oeeJoin = root.join("oee", JoinType.LEFT);
			Predicate predicate = cb.conjunction();

			predicate = cb.and(
					equal(cb, root.get("idAuditoria"), (id != null ? id.toString() : "")),
					like(cb, root.get("nombreUsuario"), (!Objects.isNull(nombreUsuario) ? nombreUsuario : "")),
					like(cb, root.get("metodo"), (!Objects.isNull(metodo) ? metodo : "")),
					like(cb, root.get("modulo"), (!Objects.isNull(modulo) ? modulo : "")),
					like(cb, root.get("accion"), (!Objects.isNull(accion) ? accion : "")),
					like(cb, root.get("nombreTabla"), (!Objects.isNull(nombreTabla) ? nombreTabla : "")),
					like(cb, oeeJoin.get("descripcionOee"), (!Objects.isNull(descripcionOee) ? descripcionOee : "")),
					equal(cb, root.get("idOee"), (showOption ? idOee.toString() : "")),
					equal(cb, root.get("idUsuario"), (showOption2 ? idUsuario.toString() : ""))
			);

			if (rangoFecha != null && !rangoFecha.isEmpty()) {
				String[] fechas = rangoFecha.split(",");
				if (fechas.length == 2) {
					try {
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						Date fechaInicio = dateFormat.parse(fechas[0].trim());
						Date fechaFin = dateFormat.parse(fechas[1].trim());

						predicate = cb.and(predicate, cb.between(root.get("fechaHora"), fechaInicio, fechaFin));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			return query.where(predicate).distinct(true).getRestriction();
		};

		Page<Auditoria> pageList = auditoriaRepo.findAll(specification, paging);

		TableDTO<Auditoria> tableDTO = new TableDTO<>();
		tableDTO.setLista(pageList.getContent());
		tableDTO.setTotalRecords((int) pageList.getTotalElements());
		return new ResponseDTO(tableDTO, HttpStatus.OK);
	}

	public void auditar(String accion, String idRegistro, Object nuevoValor, Object valorAnterior, String nombreTabla,
						String metodo, String modulo, String tipoEvento, String valorClave, String motivo, Long idServicio,
						Object parametros) throws UnknownHostException {

		Auditoria auditoria = new Auditoria();
		auditoria.setAccion(accion);
		auditoria.setFechaHora(new Date());
		auditoria.setIdRegistro(idRegistro);

		try {
			if (accion != null && !accion.isEmpty()) {
				if (accion.equalsIgnoreCase("MODIFICAR")) {
					auditoria.setValor("{\"nuevoValor\":" + objectMapper.writeValueAsString(nuevoValor)
							+ ",\"valorAnterior\":" + objectMapper.writeValueAsString(valorAnterior) + "}");
				} else if (accion.equalsIgnoreCase("BORRAR")) {
					auditoria.setValor("{\"nuevoValor\":\"Sin Datos\", \"valorAnterior\":" +
							(valorAnterior != null ? objectMapper.writeValueAsString(valorAnterior) : "Sin Datos") + "}");
				} else {
					auditoria.setValor(objectMapper.writeValueAsString(nuevoValor));
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		if (usuario != null) {
			auditoria.setIdUsuario(usuario.getIdUsuario());
			auditoria.setNombreUsuario(usuario.getUsername());
			auditoria.setRoles(this.rolesString(usuario.getIdUsuario()));
			auditoria.setIdOee(usuario.getOee().getId());
		} else {
			auditoria.setIdUsuario(Long.parseLong(idRegistro));
			auditoria.setNombreUsuario("S/I");
			auditoria.setRoles("S/I");
		}

		auditoria.setNombreTabla(nombreTabla);
		auditoria.setIpUsuario(getClientIpAddressIfServletRequestExist());
		auditoria.setMetodo(metodo);
		auditoria.setModulo(modulo);
		auditoria.setTipoEvento(tipoEvento);
		auditoria.setValorClave(valorClave);
		auditoria.setMotivo(motivo);

		if (Objects.nonNull(idServicio)) auditoria.setIdServicio(idServicio);
		try {
			auditoria.setParametros(objectMapper.writeValueAsString(parametros));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		auditoriaRepo.save(auditoria);
	}


	public String rolesString(Long idUsuario) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		List<Rol> roles = usuario.getRoles();
		StringBuilder builder = new StringBuilder();
		String rolesAudit;

		if (roles != null && !roles.isEmpty()) {
			for (Rol r : roles) {
				builder.append(r.getNombre());
				builder.append(SEPARATOR);
			}
			rolesAudit = builder.toString().substring(0, builder.toString().length() - SEPARATOR.length());
		} else {
			rolesAudit = "";
		}
		return rolesAudit;
	}

	private static final String[] IP_HEADER_CANDIDATES = {
	        "X-Forwarded-For",
	        "Proxy-Client-IP",
	        "WL-Proxy-Client-IP",
	        "HTTP_X_FORWARDED_FOR",
	        "HTTP_X_FORWARDED",
	        "HTTP_X_CLUSTER_CLIENT_IP",
	        "HTTP_CLIENT_IP",
	        "HTTP_FORWARDED_FOR",
	        "HTTP_FORWARDED",
	        "HTTP_VIA",
	        "REMOTE_ADDR"
	};

	public static String getClientIpAddressIfServletRequestExist() {

		if (RequestContextHolder.getRequestAttributes() == null) {
			return "0.0.0.0";
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		for (String header : IP_HEADER_CANDIDATES) {
			String ipList = request.getHeader(header);
			if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
				String ip = ipList.split(",")[0];
				return ip;
			}
		}

		return request.getRemoteAddr();
	}	

}
