package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.Permiso;
import py.gov.mitic.adminpy.model.entity.Rol;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.RolDTO;
import py.gov.mitic.adminpy.model.dto.RolPermisoDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.repository.DominioRepository;
import py.gov.mitic.adminpy.repository.RolRepository;
import py.gov.mitic.adminpy.specification.GenericSpecification;
import py.gov.mitic.adminpy.specification.SearchCriteria;
import py.gov.mitic.adminpy.specification.SearchOperator;

@Service
public class RolService extends GenericSpecification<Rol> {

    private Log logger = LogFactory.getLog(RolService.class);
    private final RolRepository rolRepository;
    private final AuditoriaService auditoriaService;

    public RolService(RolRepository rolRepository,AuditoriaService auditoriaService){
        this.rolRepository = rolRepository;
        this.auditoriaService = auditoriaService;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String nombre, String descripcion) {
        Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

        List<SearchCriteria> filters = new ArrayList<>();
        filters.add(new SearchCriteria("idRol", SearchOperator.EQUALS, (!Objects.isNull(id) ? id.toString() : "")));
        filters.add(new SearchCriteria("nombre", SearchOperator.LIKE, nombre));
        filters.add(new SearchCriteria("descripcion", SearchOperator.LIKE, descripcion));

        Page<Rol> pageList = rolRepository.findAll(getSpecifications(filters, true), paging);
        TableDTO<Rol> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());

        //		auditMap = new HashMap<>();
        //      auditMap.put("paging", paging.toString());
        //		auditMap.put("page", (int) pageList.getNumber() +1);
        //		auditMap.put("totalRecords", (int) pageList.getTotalElements());
        //		while (auditMap.values().remove(null));
        //		while (auditMap.values().remove(""));
        //		params = new HashMap<>();
        //		params.put("idRol", idRol);
        //		params.put("nombre", nombre);
        //		params.put("descripcion", descripcion);
        //		while (params.values().remove(null));
        //		while (params.values().remove(""));
        //		try {
        //			auditoriaService.auditar("LISTAR", "S/I",
        //					auditMap, null, "rol", "/getAll/{params}", "/rol", "rol/", null, null, null, params);
        //		} catch (UnknownHostException e) {
        //			e.printStackTrace();
        //		}

        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO getRoles() {
        List<Rol> roles = rolRepository.findAllRole();
        return new ResponseDTO(roles, HttpStatus.OK);
    }

    public ResponseDTO save(RolDTO dto) {

        Optional<Rol> optRole = rolRepository.findByNombreIgnoreCase(dto.getNombre().toUpperCase());
        if(optRole.isPresent()) {
            return new ResponseDTO("Ya se encuentra registrado el rol: " + dto.getNombre(), HttpStatus.BAD_REQUEST);
        }
        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        rol.setEstado(true);
        rolRepository.save(rol);

        try {
            auditoriaService.auditar("CREAR",
                    Objects.nonNull(rol) ? rol.getIdRol().toString() : null,
                    rol,
                    Objects.nonNull(rol) ? rol : null,
                    "rol",
                    "rol/",
                    "/save",
                    "rol/save",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            logger.error("Error al realizar la auditoría.", e);
        }

        return new ResponseDTO("Rol creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, RolDTO dto) {

        Optional<Rol> optRole = rolRepository.findByNombreIgnoreCaseAndIdRolNot(dto.getNombre().toUpperCase(), dto.getIdRol());
        if(optRole.isPresent()) {
            return new ResponseDTO("Ya se encuentra registrado el rol: " + dto.getNombre(), HttpStatus.BAD_REQUEST);
        }

        Rol entity = rolRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rol no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Rol entityBefore = objectMapper.convertValue(entity, Rol.class);

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdRol().toString() : null,
                    entity,
                    entityBefore,
                    "rol",
                    "/rol/{id}",
                    "/update",
                    "rol/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        rolRepository.save(entity);

        return new ResponseDTO("Rol actualizado con éxito", HttpStatus.OK);

    //        rolRepository.findById(id).map(rol -> {
//            rol.setNombre(dto.getNombre());
//            rol.setDescripcion(dto.getDescripcion());
//            return rolRepository.save(rol);
//        }).orElseThrow(() -> new BadRequestException("Rol no existe"));
//        return new ResponseDTO("Rol actualizado con éxito", HttpStatus.OK);

    }

    @Transactional
    public ResponseDTO asociarPermisos(RolPermisoDTO dto) {
        try {
            Optional<Rol> rolOp = rolRepository.findById(dto.getRol().getIdRol());
            if (!rolOp.isPresent()) {
                return new ResponseDTO("Rol no existe", HttpStatus.BAD_REQUEST);
            }

            Rol rol = rolOp.get();

            // Guardar el estado actual de los permisos antes de la modificación
//            List<Permiso> permisosAntes = new ArrayList<>(rol.getPermisos());

            List<Map<String, Object>> permisosAntes = rol.getPermisos().stream()
                    .map(Permiso::toMap)
                    .collect(Collectors.toList());

            // Actualizar permisos según la solicitud
            List<Permiso> nuevosPermisos = dto.getPermisos().stream()
                    .map(p -> new Permiso(p.getIdPermiso(), p.getNombre(), p.getDescripcion()))
                    .collect(Collectors.toList());

            //auditoria permisos anteriores
            List<Permiso> permisosAntesAuditoria = rol.getPermisos().stream()
                    .map(p -> new Permiso( p.getNombre()))
                    .collect(Collectors.toList());

            rol.setPermisos(nuevosPermisos);
            rolRepository.save(rol);


            //auditoria nuevos permisos
            List<Permiso> nuevosPermisosAuditoria = dto.getPermisos().stream()
                    .map(p -> new Permiso( p.getNombre()))
                    .collect(Collectors.toList());

            try {
                // Auditoría después de la modificación
                auditoriaService.auditar("MODIFICAR",
                        Objects.nonNull(rol) ? rol.getIdRol().toString() : null,
                        nuevosPermisosAuditoria,
                        permisosAntesAuditoria,
                        "rol",
                        "/asociar-permisos",
                        "/asociarPermisos",
                        "rol/asociarPermisos",
                        null,
                        null,
                        null,
                        null);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return new ResponseDTO("Error en la auditoría", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseDTO("Permisos actualizados con éxito", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }


    public ResponseDTO updateStatus(Long id) {
//        rolRepository.findById(id).map(rol -> {
//            rol.setEstado((rol.getEstado() ? false : true));
//            return rolRepository.save(rol);
//        }).orElseThrow(() -> new BadRequestException("Rol no existe"));
//        return new ResponseDTO("Estado del Rol actualizado con éxito", HttpStatus.OK);

        Rol entity = rolRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rol no existe"));

        ObjectMapper objectMapper = new ObjectMapper();
        Rol entityBefore = objectMapper.convertValue(entity, Rol.class);

        entity.setEstado((entity.getEstado() ? false : true));

        try {
            auditoriaService.auditar("MODIFICAR",
                    Objects.nonNull(entity) ? entity.getIdRol().toString() : null,
                    entity,
                    entityBefore,
                    "rol",
                    "/rol/{id}",
                    "/update",
                    "rol/update",
                    null,
                    null,
                    null,
                    null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        rolRepository.save(entity);

        return new ResponseDTO("Estado del Rol actualizado con éxito", HttpStatus.OK);

    }

}
