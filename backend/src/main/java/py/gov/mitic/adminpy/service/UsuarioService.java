package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.gov.mitic.adminpy.exceptions.UnauthorizedException;
import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.entity.*;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.repository.projections.UsuarioPermisoDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

@Service
@AllArgsConstructor
public class UsuarioService extends GenericSpecification<Usuario> implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	private final RolRepository rolRepository;

	private final OeeRepository oeeRepository;

	private final AuditoriaRepository auditoriaRepo;

	private final AuditoriaService auditoriaService;

	private final RecuperarClaveRepository resetPassRepo;

	private final EmailService email;
	
	private static final String SEPARATOR = ",";
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private Log logger = LogFactory.getLog(UsuarioService.class);
	
	private Map<String, Object> auditMap = new HashMap<>();
	private Map<String, Object> auditMapOld = new HashMap<>();
	private Map<String, Object> params = new HashMap<>();

	@Value("${spring.application.url}") String BASE_URL;

	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, OeeRepository oeeRepository, AuditoriaRepository auditoriaRepo, AuditoriaService auditoriaService, RecuperarClaveRepository resetPassRepo, EmailService email) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.oeeRepository = oeeRepository;
		this.auditoriaRepo = auditoriaRepo;
		this.auditoriaService = auditoriaService;
		this.resetPassRepo = resetPassRepo;
		this.email = email;
	}

	public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, Long idInstitucion, String nombre, String apellido, String username, String fechaExpiracion, String institucion, String roles) {
		Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));
		
		Boolean showOption = Objects.nonNull(idInstitucion) && idInstitucion > 0 ? true:false;
		
		Specification<Usuario> specification = (root, query, cb) -> {
			Join<Usuario, Oee> institucionJoin = root.join("oee", JoinType.LEFT);
			Join<Usuario, Rol> rolJoin = root.join("roles", JoinType.LEFT);
			return query
					.where(cb.and(
							equal(cb, root.get("idUsuario"), (Objects.nonNull(id) ? id.toString() : "")),
							like(cb, root.get("username"), username),
							like(cb, root.get("nombre"), nombre),
							like(cb, root.get("apellido"), apellido),
							//like(cb, institucionJoin.get("nombre"), institucion),
							like(cb, rolJoin.get("nombre"), (Objects.nonNull(roles) ? roles : "")),
							equal(cb, institucionJoin.get("id"),  (showOption ? idInstitucion.toString() : ""))
							)
						)
					.distinct(true)
					.getRestriction();
		};

		Page<Usuario> pageList = usuarioRepository.findAll(specification, paging);
		TableDTO<Usuario> tableDTO = new TableDTO<>();
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
//		params.put("username", username);
//		params.put("nombre", nombre);
//		params.put("apellido", apellido);
//		params.put("institucion", institucion);
//		params.put("rol", roles);
//		params.put("idInstitucion", idInstitucion);
//		while (params.values().remove(null));
//		while (params.values().remove(""));
//		try {
//			auditoriaService.auditar("LISTAR", "S/I",
//					auditMap, null, "usuario, roles, institucion", "/getAll/{params}", "/usuario", "usuario/listar/datos", null, null, null, params);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

		return new ResponseDTO(tableDTO, HttpStatus.OK);
	}

	public Usuario getUserSession(String username) {
		return usuarioRepository.findByUserSession(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsername(username);

			if (Objects.isNull(usuario)) {
				logger.info("El usuario ".concat(username).concat(" no existe."));
				throw new UsernameNotFoundException("El usuario ".concat(username).concat(" no existe."));
			}

			Date date = new Date();

//			if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
//				updateStatus(usuario.getIdUsuario());
//				logger.info("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
//				throw new UnauthorizedException("Usuario inactivo por fecha de expiración.");
//			}

			if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
				logger.info("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
				throw new UnauthorizedException("Usuario inactivo por fecha de expiración.");
			}

			if (!usuario.getEstado()) {
				logger.info("El usuario ".concat(username).concat(" está inactivo."));
				throw new UnauthorizedException("El usuario ".concat(username).concat(" está inactivo."));
			}

			if (usuario.getOee() == null) {
				logger.info("El usuario ".concat(username).concat(" no posee una institución."));
				throw new UnauthorizedException("El usuario ".concat(username).concat(" no posee una institución."));
			}

			List<UsuarioPermisoDTO> usuarioPermisos = usuarioRepository.findRolesPorUsuario(usuario.getIdUsuario());
			if (Objects.nonNull(usuarioPermisos)) {
				List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

				if (usuarioPermisos.isEmpty()) {
					throw new BadRequestException("El usuario no cuenta con permiso suficiente para acceder al sistema");
				}

				usuarioPermisos.forEach(authority -> {
					if (authority.getPermiso() != null && !authority.getPermiso().equals("")) {
						grantedAuthorities.add(new SimpleGrantedAuthority(authority.getPermiso()));
					}
				});

				return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), grantedAuthorities);
			}

			throw new BadRequestException("El usuario no cuenta con permiso suficiente para acceder al sistema");
	}


	/**
	 * Recupera e inserta en Auditoría datos de inicio de sesión
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 * @throws JsonProcessingException
	 */
	public UserDetails loadUserByUsernameForAudit(String username) throws UsernameNotFoundException, JsonProcessingException {

		Usuario usuario = usuarioRepository.findByUsername(username);

		AuthenticationRequest credentials = new AuthenticationRequest();
		credentials.setUsername(username);

		Auditoria auditoria = new Auditoria();
		auditoria.setAccion("LOGIN");
		auditoria.setFechaHora(new Date());
		auditoria.setIpUsuario(getClientIpAddressIfServletRequestExist());
		auditoria.setNombreUsuario(username);
		auditoria.setRoles("");
		auditoria.setNombreTabla("usuario");
		auditoria.setMetodo("/doLogin");
		auditoria.setModulo("/login");
		auditoria.setTipoEvento("usuario/auth/login");

		if (Objects.isNull(usuario)) {

			logger.info("El usuario ".concat(username).concat(" no existe."));
			credentials.setUsername("El usuario ".concat(username).concat(" no existe."));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UsernameNotFoundException("El usuario ".concat(username).concat(" no existe."));
		}

		Date date = new Date();

		if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
			updateStatus(usuario.getIdUsuario());

			logger.info("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
			auditoria.setRoles("");
			credentials.setPassword("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UnauthorizedException("Usuario inactivo por fecha de expiración.");
		}

		if (!usuario.getEstado()) {
			logger.info("El usuario ".concat(username).concat(" está inactivo."));
			credentials.setPassword("El usuario ".concat(username).concat(" está inactivo."));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UnauthorizedException("El usuario ".concat(username).concat(" está inactivo."));
		}

		if (usuario.getOee() == null) {
			logger.info("El usuario ".concat(username).concat(" no posee una institución."));
			credentials.setPassword("El usuario ".concat(username).concat(" no posee una institución."));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			throw new UnauthorizedException("El usuario ".concat(username).concat(" no posee una institución."));
		}

		List<UsuarioPermisoDTO> usuarioPermisos = usuarioRepository.findRolesPorUsuario(usuario.getIdUsuario());
		if(Objects.nonNull(usuarioPermisos)) {

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			usuarioPermisos.stream().forEach(authority -> {
				if(authority.getPermiso() != null && !authority.getPermiso().equals("")) {
					grantedAuthorities.add(new SimpleGrantedAuthority(authority.getPermiso()));
				}
			});

			credentials.setPassword("Inicio de sesión con éxito");
			credentials.setPassword2("x");
			auditoria.setRoles(this.rolesString(usuario.getRoles()));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoria.setIdUsuario(usuario.getIdUsuario());
			auditoria.setIdOee(usuario.getOee().getId());
			auditoria.setIdRegistro(usuario.getIdUsuario().toString());
			auditoriaRepo.save(auditoria);

			return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), grantedAuthorities);
		}

		throw new BadRequestException("El usuario no cuenta con permiso suficiente para acceder al sistema");
	}

	@Transactional
	public ResponseDTO create(UsuarioDTO dto) {

		try {
			if(dto.getUsername() == null) {
				logger.info("El email del usuario es un campo requerido");
				return new ResponseDTO("El email del usuario es un campo requerido", HttpStatus.BAD_REQUEST);
			}
			if(dto.getCedula() == null) {
				logger.info("La cédula es un campo requerido");
				return new ResponseDTO("La cédula es un campo requerido", HttpStatus.BAD_REQUEST);
			}
			Usuario existUser = usuarioRepository.findByUsername(dto.getUsername());
			if(Objects.nonNull(existUser)) {
				logger.info("El Email del usuario ya existe: "+dto.getUsername());
				return new ResponseDTO("El Email del usuario ya existe.", HttpStatus.BAD_REQUEST);
			}

			Usuario existUsernameCedula = usuarioRepository.findExistByUsernameCedula(dto.getUsername(), dto.getCedula());

			if (Objects.nonNull(existUsernameCedula)){
				logger.info("Email y Cedula en uso: "+dto.getUsername());
				return new ResponseDTO("El Email ya está asociado a la cédula.", HttpStatus.BAD_REQUEST);
			}
			if(Objects.isNull(dto.getRoles()) || dto.getRoles().size() <= 0) {
				logger.info("El usuario no tiene asociado ningún rol");
				return new ResponseDTO("El usuario no tiene asociado ningún rol. ¡Favor verificar!", HttpStatus.BAD_REQUEST);
			}
			if(Objects.isNull(dto.getOee())) {
				logger.info("La institución es un campo requerido");
				return new ResponseDTO("La institución es un campo requerido", HttpStatus.BAD_REQUEST);
			}

			if(dto.getFechaExpiracion() == null) {
				logger.info("La fecha de expiración es requerido");

				return new ResponseDTO("La fecha de expiración es requerido", HttpStatus.BAD_REQUEST);
			}
			if(dto.getJustificacionAlta() == null) {
				logger.info("La justificación del alta es requerida");
				return new ResponseDTO("La justificación del alta es requerida", HttpStatus.BAD_REQUEST);
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date fechaExpiracion = dateFormat.parse(dto.getFechaExpiracion());

			Date fechaActual = new Date();
			if (fechaExpiracion.before(fechaActual)) {
				logger.info("La fecha de expiración no puede ser menor a la fecha del sistema");
				return new ResponseDTO("La fecha de expiración no puede ser menor a la fecha del sistema", HttpStatus.BAD_REQUEST);
			}

			Usuario usuario = new Usuario();
			usuario.setUsername(dto.getUsername());
			// Password generate
			String randomPass = (RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 13) + 8)).substring(0, 12);
			usuario.setPassword(new BCryptPasswordEncoder().encode(randomPass));

			usuario.setNombre(dto.getNombre().toUpperCase());
			usuario.setApellido(dto.getApellido().toUpperCase());
			usuario.setEstado(true);
			usuario.setFechaExpiracion(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaExpiracion()));
			usuario.setOee(oeeRepository.findById(dto.getOee().getId()).get());
			usuario.setCargo(dto.getCargo());
			usuario.setTelefono(dto.getTelefono());
			usuario.setCorreo(dto.getUsername().toLowerCase());
			usuario.setCedula(dto.getCedula());
			usuario.setJustificacionAlta(dto.getJustificacionAlta());

			List<Rol> roles = new ArrayList<>();
			dto.getRoles().stream().map(rol -> rolRepository.findById(rol.getId()).get()).forEach(rol -> { if(rol != null) roles.add(rol); });
			usuario.setRoles(roles);

			Usuario userSaved = usuarioRepository.save(usuario);
			// send mail
			if (Objects.nonNull(userSaved)) {
				// agregar auditoría
				auditoriaService.auditar("CREAR",
						Objects.nonNull(usuario) ? usuario.getIdUsuario().toString():null,
						Objects.nonNull(usuario) ? usuario:null,
						null, "usuario",
						"usuario/", "/create", "usuario/", null, null, null, null);

				sendMailToUser(usuario, randomPass);
			}

			return new ResponseDTO("Usuario creado con éxito", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	@Transactional
	public ResponseDTO update(Long id, UsuarioDTO dto) {
		try {
			if(dto.getCedula() == null) {
				return new ResponseDTO("La cédula es un campo requerido", HttpStatus.BAD_REQUEST);
			}
			Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
			if(Objects.isNull(usuarioOptional) || !usuarioOptional.isPresent()) {
				logger.info("Usuario no existe");
				return new ResponseDTO("Usuario no existe", HttpStatus.BAD_REQUEST);
			}

			Usuario existUsernameCedula = usuarioRepository.findExistByUsernameCedula(dto.getUsername(), dto.getCedula());

			if (Objects.nonNull(existUsernameCedula)){
				logger.info("Email y Cedula en uso: "+dto.getUsername());
				return new ResponseDTO("El Email ya está asociado a la cédula.", HttpStatus.BAD_REQUEST);
			}

			if(usuarioRepository.findExistByUsername(dto.getUsername(), id)) {
				logger.info("El Email del usuario ya está siendo utilizado");
				return new ResponseDTO("El Email del usuario ya está siendo utilizado.", HttpStatus.BAD_REQUEST);
			}

			if(Objects.isNull(dto.getRoles()) || dto.getRoles().size() <= 0) {
				logger.info("El usuario no tiene asociado ningún rol");
				return new ResponseDTO("El usuario no tiene asociado ningún rol. ¡Favor verificar!", HttpStatus.BAD_REQUEST);
			}
			if(Objects.isNull(dto.getOee())) {
				logger.info("La institución es un campo requerido");
				return new ResponseDTO("La institución es un campo requerido", HttpStatus.BAD_REQUEST);
			}
			if(dto.getFechaExpiracion() == null) {
				logger.info("La fecha de expiración es requerido");
				return new ResponseDTO("La fecha de expiración es requerido", HttpStatus.BAD_REQUEST);
			}

			Usuario entity = usuarioRepository.findById(id)
					.orElseThrow(() -> new BadRequestException("Usuario no existe"));

			ObjectMapper objectMapper = new ObjectMapper();
			Usuario entityBefore = objectMapper.convertValue(entity, Usuario.class);
			
//			Usuario usuario = usuarioOptional.get();
			entity.setUsername(dto.getUsername());
			entity.setNombre(dto.getNombre().toUpperCase());
			entity.setApellido(dto.getApellido().toUpperCase());
			entity.setFechaExpiracion(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaExpiracion()));
			Oee oee = oeeRepository.findById(dto.getOee().getId()).get();
			entity.setOee(oee);

			/**
			 * actualiza usuario rol
			 **/
			List<Rol> roles = new ArrayList<>();
			dto.getRoles().stream().map(rol -> rolRepository.findById(rol.getId()).get()).forEach(rol -> {
				entity.removeRol(rol);
				if(rol != null) roles.add(rol);
			});
			entity.setRoles(roles);

			entity.setCargo(dto.getCargo());
			entity.setTelefono(dto.getTelefono());
			entity.setCorreo(dto.getUsername());
			entity.setCedula(dto.getCedula());
			entity.setJustificacionAlta(dto.getJustificacionAlta());

			try {
				auditoriaService.auditar("MODIFICAR",
						Objects.nonNull(entity) ? entity.getIdUsuario().toString() : null,
						entity,
						entityBefore,
						"usuario",
						"/usuario/{id}",
						"/update",
						"usuario/update",
						null,
						null,
						null,
						null);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			usuarioRepository.save(entity);
			return new ResponseDTO("Usuario actualizado con éxito", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	public ResponseDTO updatePassword(AuthenticationRequest auth) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());
		//Usuario usuario = usuarioRepository.findByUsername(auth.getUsername());
	
		if(usuario == null) {
			logger.info("Usuario no existe");
			throw new BadRequestException("Usuario no existe.");
		}
		
		if (!usuario.getEstado()) {
			logger.info("Usuario no se encuentra activo");
			throw new BadRequestException("Usuario no se encuentra activo.");
		}

		if (auth.getPassword() == null || auth.getPassword().isEmpty()) {
			logger.info("Contraseña vacía, no se cambiará la contraseña");
			throw new BadRequestException("Campo contraseña no puede estar vacía.");
		}

		if (!auth.getPassword().equals(auth.getPassword2())) {
			logger.info("Las contraseñas no coinciden.");
			throw new BadRequestException("Las contraseñas no coinciden.");
		}

		try {
			validatePassword(auth.getPassword());
		} catch (UsuarioService.PasswordValidationException e) {
			logger.info("Error de validación de contraseña: " + e.getMessage());
			throw new BadRequestException(e.getMessage());
		}


		usuario.setPassword(new BCryptPasswordEncoder().encode(auth.getPassword()));

		auditMap = new HashMap<>();
		auditMap.put("password", new BCryptPasswordEncoder().encode(auth.getPassword()));
		auditMap.put("idUsuario", usuario.getIdUsuario());
		while (auditMap.values().remove(null));
		while (auditMap.values().remove(""));

		auditMapOld = new HashMap<>();
		auditMapOld.put("password", usuario.getPassword());
		auditMapOld.put("idUsuario", usuario.getIdUsuario());
		while (auditMapOld.values().remove(null));
		while (auditMapOld.values().remove(""));



		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(usuario) ? usuario.getIdUsuario().toString() : null,
					auditMap,
					auditMapOld,
					"usuario",
					"/usuario/update-password",
					"/updatePassword",
					"usuario/updatePassword",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		usuarioRepository.save(usuario);

		return new ResponseDTO("Clave actualizada con éxito para el usuario ".concat(usuario.getUsername()), HttpStatus.OK);
	}

	public ResponseDTO updateProfile(UsuarioDTO dto ) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		if(usuario == null) {
			logger.info("Usuario no existe");
			throw new BadRequestException("Usuario no existe.");
		}

		if (!usuario.getEstado()) {
			logger.info("Usuario no se encuentra activo");
			throw new BadRequestException("Usuario no se encuentra activo.");
		}

		if(dto.getNombre() == null) {
			logger.info("Usuario no se encuentra activo");
			return new ResponseDTO("El campo Nombre es requerido", HttpStatus.BAD_REQUEST);
		}
		if(dto.getApellido() == null) {
			logger.info("El campo Apellido es requerido");
			return new ResponseDTO("El campo Apellido es requerido", HttpStatus.BAD_REQUEST);
		}
		if(dto.getTelefono() == null) {
			logger.info("El campo Teléfono es requerido");
			return new ResponseDTO("El campo Teléfono es requerido", HttpStatus.BAD_REQUEST);
		}

		Usuario entity = usuarioRepository.findById(usuario.getIdUsuario())
				.orElseThrow(() -> new BadRequestException("Usuario no existe"));

		ObjectMapper objectMapper = new ObjectMapper();
		Usuario entityBefore = objectMapper.convertValue(entity, Usuario.class);

		entity.setNombre(dto.getNombre());
		entity.setApellido(dto.getApellido());
		entity.setTelefono(dto.getTelefono());
		if(dto.getCargo() != null) {
			entity.setCargo(dto.getCargo());
		}

		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(entity) ? entity.getIdUsuario().toString() : null,
					entity,
					entityBefore,
					"usuario",
					"/usuario/update-profile",
					"/update-profile",
					"usuario/update-profile",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		usuarioRepository.save(entity);

		return new ResponseDTO("¡Perfil actualizado con éxito! En la próxima sesión podrás visualizar los cambios.", HttpStatus.OK);
    }

	public ResponseDTO updateStatus(Long id) {

		Usuario entity = usuarioRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Usuario no existe"));

		ObjectMapper objectMapper = new ObjectMapper();
		Usuario entityBefore = objectMapper.convertValue(entity, Usuario.class);

		entity.setEstado(false);

		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(entity) ? entity.getIdUsuario().toString() : null,
					entity,
					entityBefore,
					"usuario",
					"/usuario/{id}/update-status",
					"/updateUserStatus",
					"usuario/update-status",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		usuarioRepository.save(entity);

		return new ResponseDTO("El usuario fue dado de baja con éxito", HttpStatus.OK);
	}
	
	public ResponseDTO updateUserStatus(Long id, UsuarioDTO dto) {
		String status = dto.getEstado() ? "activado":"inactivado";

		Usuario entity = usuarioRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Usuario no existe"));

		ObjectMapper objectMapper = new ObjectMapper();
		Usuario entityBefore = objectMapper.convertValue(entity, Usuario.class);

		entity.setEstado(dto.getEstado());

		try {
			auditoriaService.auditar("MODIFICAR",
					Objects.nonNull(entity) ? entity.getIdUsuario().toString() : null,
					entity,
					entityBefore,
					"usuario",
					"/usuario/{id}/update-status",
					"/updateUserStatus",
					"usuario/update-status",
					null,
					null,
					null,
					null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		usuarioRepository.save(entity);

		return new ResponseDTO("Se ha " + status + " al usuario con éxito.", HttpStatus.OK);
	}
		
	public ResponseDTO getTotal(Long idInstitucion, Boolean estado) {
		return new ResponseDTO(usuarioRepository.countByUserStatus(idInstitucion, estado), HttpStatus.OK);
    }

	public ResponseDTO getList() {
		List<CommonDTO> list = usuarioRepository.findAllUsuarios()
				.stream()
				.map(o -> new CommonDTO(o.getIdUsuario(),  o.getCedula()+" | "+o.getNombre()+" "+ o.getApellido()))
				.collect(Collectors.toList());
		return new ResponseDTO(list, HttpStatus.OK);
	}

	public ResponseDTO getUsuariosOeeList(Long id) {
		List<CommonDTO> list = usuarioRepository.findAllUsuariosOee(id)
				.stream()
				.map(o -> new CommonDTO(o.getIdUsuario(),  o.getCedula()+" | "+o.getNombre()+" "+ o.getApellido()))
				.collect(Collectors.toList());
		return new ResponseDTO(list, HttpStatus.OK);
	}

	public ResponseDTO getUsuarioOee(Long id) {
		List<UsuarioDatoBasicoDTO> list = usuarioRepository.findById(id)
				.stream()
				.map(o -> new UsuarioDatoBasicoDTO(o.getIdUsuario(),o.getNombre(),o.getApellido(),o.getCedula(),o.getCargo(),o.getCorreo()))
				.collect(Collectors.toList());
		return new ResponseDTO(list, HttpStatus.OK);
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
	
	public String rolesString(List<Rol> roles) {
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

	public ResponseDTO resetPass(AuthenticationRequest auth) {
		String username = auth.getUsername();
		Usuario usuario = usuarioRepository.findByUsername(username);
		if (Objects.isNull(usuario)) {
			logger.info("El usuario ".concat(username).concat(" no existe."));
			throw new UsernameNotFoundException("El usuario ".concat(username).concat(" no existe."));
		}
		//logger.info("ROL: " + usuario.getRoles());
		if (usuario.getRoles().isEmpty()) { // Si no cuenta con roles
			logger.info("El usuario ".concat(username).concat(" no existe."));
			throw new UsernameNotFoundException("Usuario no autorizado para el reseteo de clave.");
		}
		if (!usuario.getEstado()) { // Si estado = inactivo
			logger.info("El usuario ".concat(username).concat(" no está activo."));
			throw new UsernameNotFoundException("Usuario no se encuentra activo.");
		}

		String codigo = "";
		// Si aún no solicitó recuperación, se crea nuevo código
		// caso contrario se reutiliza el existente
		if (Objects.isNull(usuario.getTokenReset())){
			usuario.setTokenReset(UUID.randomUUID().toString());
			codigo = usuario.getTokenReset();
			usuarioRepository.save(usuario);
		} else codigo = usuario.getTokenReset();

		if (Objects.nonNull(codigo)) {

			String url = BASE_URL + "#/modificar-clave/" + codigo;
			EmailContent ec = new EmailContent();
			ec.setUrlSistema(BASE_URL);
			ec.setTitulo("Recuperación de clave de acceso");
			ec.setNombre(usuario.getNombre());
			ec.setApellido(usuario.getApellido());
			ec.setLinkAction1(url);

			email.send(
					usuario.getUsername(), // to
					"[AdminPY] Restablecer clave de acceso", // Subject
					email.replaceTemplate("recuperar_clave_template.html", ec) // template [data_to_replace_in_template]
			);
		}

		return new ResponseDTO("Solicitud de restablecimiento de clave enviado con éxito", HttpStatus.OK);
	}

	private void sendMailToUser(Usuario u, String clave) throws Exception {
		EmailContent ec = new EmailContent();
		String url = BASE_URL + "/login";
		ec.setTitulo("Usuario creado");
		ec.setUrlSistema(BASE_URL);
		ec.setLinkAction1(url);
		ec.setNombre(u.getNombre());
		ec.setApellido(u.getApellido());
		ec.setNombreUsuario(u.getUsername());
		ec.setClaveGenerada(clave);

		email.send(
				u.getUsername(), // to
				"[AdminPY] Usuario registrado", // Subject
				email.replaceTemplate("generacion_clave_template.html", ec) // template [data_to_replace_in_template]
		);
	}

	private void validatePassword(String password) throws UsuarioService.PasswordValidationException {

		if (password.length() < 6) {
			throw new UsuarioService.PasswordValidationException("La contraseña debe tener al menos 6 caracteres.");
		}

		boolean contieneMayuscula = false;
		boolean contieneCaracterEspecial = false;


		String caracteresEspeciales = "!@#$%^&*/.()-_+=<>?";

		for (char c : password.toCharArray()) {

			if (Character.isUpperCase(c)) {
				contieneMayuscula = true;
			}
			if (caracteresEspeciales.contains(String.valueOf(c))) {
				contieneCaracterEspecial = true;
			}
			if (contieneMayuscula && contieneCaracterEspecial) {
				break;
			}
		}

		if (!contieneMayuscula) {
			throw new UsuarioService.PasswordValidationException("La contraseña debe contener al menos una letra mayúscula.");
		}
		if (!contieneCaracterEspecial) {
			throw new UsuarioService.PasswordValidationException("La contraseña debe contener al menos un carácter especial.");
		}
	}
	public class PasswordValidationException extends Exception {
		public PasswordValidationException(String message) {
			super(message);
		}
	}
}