package py.gov.mitic.adminpy.service;

import java.net.UnknownHostException;
import java.util.*;
import javax.ws.rs.BadRequestException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.dto.PermisoDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.Permiso;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.specification.GenericSpecification;
import py.gov.mitic.adminpy.specification.SearchCriteria;
import py.gov.mitic.adminpy.specification.SearchOperator;

@Service
public class PermisoService extends GenericSpecification<Permiso> {

    private Log logger = LogFactory.getLog(UsuarioService.class);
    private final AuditoriaService auditoriaService;
    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository, AuditoriaService auditoriaService) {
        this.permisoRepository = permisoRepository;
        this.auditoriaService = auditoriaService;
    }



    @Transactional(readOnly = true)
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String nombre, String descripcion) {
        Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

        List<SearchCriteria> filters = new ArrayList<>();
        filters.add(new SearchCriteria("idPermiso", SearchOperator.EQUALS, (!Objects.isNull(id) ? id.toString() : "")));
        filters.add(new SearchCriteria("nombre", SearchOperator.LIKE, nombre));
        filters.add(new SearchCriteria("descripcion", SearchOperator.LIKE, descripcion));

        Page<Permiso> pageList = permisoRepository.findAll(getSpecifications(filters, true), paging);

        TableDTO<Permiso> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO getPermisos() {
        List<Permiso> permisos = permisoRepository.findAllPermiso();
        return new ResponseDTO(permisos, HttpStatus.OK);
    }

    public ResponseDTO create(PermisoDTO dto) {

        try {
        Optional<Permiso> optPermiso = permisoRepository.findByNombreIgnoreCase(dto.getNombre().toUpperCase());
        if(optPermiso.isPresent()) {
            return new ResponseDTO("Ya se encuentra registrado el permiso: " + dto.getNombre(), HttpStatus.BAD_REQUEST);
        }

        Permiso permiso = new Permiso();
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        permisoRepository.save(permiso);

        //auditoría
        auditoriaService.auditar("CREAR",
                Objects.nonNull(permiso) ? permiso.getIdPermiso().toString():null,
                Objects.nonNull(permiso) ? permiso:null,
                null, "permiso",
                "/create", "/create", "permiso/", null, null, null, null);

            return new ResponseDTO("Permiso creado con éxito", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }

    public ResponseDTO update(Long id, PermisoDTO dto) {

        Optional<Permiso> optPermiso = permisoRepository.findByNombreIgnoreCaseAndIdPermisoNot(dto.getNombre().toUpperCase(), id);
        if(optPermiso.isPresent()) {
            return new ResponseDTO("Ya se encuentra registrado el permiso: " + dto.getNombre(), HttpStatus.BAD_REQUEST);
        }

        permisoRepository.findById(id).map(permiso -> {
            
            //auditoría
            try {
                auditoriaService.auditar("MODIFICAR",
                        Objects.nonNull(permiso) ? permiso.getIdPermiso().toString():null,
                        dto,
                        Objects.nonNull(permiso) ? permiso:null, "permiso",
                        "/permiso/{id}", "/update", "permiso/", null, null, null, null);

            } catch (UnknownHostException e) { e.printStackTrace();}

            permiso.setNombre(dto.getNombre());
            permiso.setDescripcion(dto.getDescripcion());

            return permisoRepository.save(permiso);
        }).orElseThrow(() -> new BadRequestException("Permiso no existe"));

        return new ResponseDTO("Permiso actualizado con éxito", HttpStatus.OK);
    }

}
