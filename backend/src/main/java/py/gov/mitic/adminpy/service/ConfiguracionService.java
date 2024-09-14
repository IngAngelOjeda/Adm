package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.ConfiguracionDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.dto.TableDTO;
import py.gov.mitic.adminpy.model.entity.Configuracion;
import py.gov.mitic.adminpy.repository.ConfiguracionRepository;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ConfiguracionService extends GenericSpecification<Configuracion> {

    private Log logger = LogFactory.getLog(ConfiguracionService.class);

    private final ConfiguracionRepository repository;

    public ConfiguracionService(ConfiguracionRepository repository) {
        this.repository = repository;
    }

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String nombre, String valor) {

        Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE, getSortField(sortAsc, sortField));

        Specification<Configuracion> specification = (root, query, cb) -> {

            return query.where(cb.and(
                            equal(cb, root.get("id"), ((Objects.nonNull(id) && id > 0 ? true:false) ? id.toString() : "")),
                            like(cb, root.get("nombre"), (!Objects.isNull(nombre) ? nombre : "")),
                            like(cb, root.get("valor"), (!Objects.isNull(valor) ? valor : ""))
                    ))
                    .distinct(true)
                    .getRestriction();
        };

        Page<Configuracion> pageList = repository.findAll(specification, paging);

        TableDTO<Configuracion> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);

    }

    public ResponseDTO save(ConfiguracionDTO dto) {
        if (Objects.isNull(dto.getNombre())) {
            return new ResponseDTO("El campo 'Nombre' es requerido.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(dto.getValor())) {
            return new ResponseDTO("El campo 'Valor' es requerido.", HttpStatus.BAD_REQUEST);
        }

        List<Configuracion> configuraciones = StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (configuraciones.stream().anyMatch(c -> c.getNombre().equals(dto.getNombre()))) {
            return new ResponseDTO("El campo 'nombre' ya existe.", HttpStatus.BAD_REQUEST);
        }

        if (configuraciones.stream().anyMatch(c -> c.getValor().equals(dto.getValor()))) {
            return new ResponseDTO("El campo 'valor' ya existe.", HttpStatus.BAD_REQUEST);
        }

        Configuracion entity = new Configuracion();
        entity.setNombre(dto.getNombre());
        entity.setValor(dto.getValor());
        entity.setEstado("ACTIVO");
        repository.save(entity);
        return new ResponseDTO("Configuración creada con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, ConfiguracionDTO dto) {

        if (Objects.isNull(dto.getNombre())) {
            return new ResponseDTO("El campo 'Nombre' es requerido.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(dto.getValor())) {
            return new ResponseDTO("El campo 'Valor' es requerido.", HttpStatus.BAD_REQUEST);
        }

        Optional<Configuracion> optConfig = repository.findByNombreIgnoreCaseAndIdNot(dto.getNombre().toUpperCase(), id);
        if(optConfig.isPresent()) {
            return new ResponseDTO("Ya se encuentra registrado la configuración: " + dto.getNombre(), HttpStatus.BAD_REQUEST);
        }

        repository.findById(id).map(entity -> {
            entity.setNombre(dto.getNombre());
            entity.setValor(dto.getValor());

            return repository.save(entity);

        }).orElseThrow(() -> new BadRequestException("Configuración no existe"));

        return new ResponseDTO("Configuración actualizada con éxito", HttpStatus.OK);
    }

}
