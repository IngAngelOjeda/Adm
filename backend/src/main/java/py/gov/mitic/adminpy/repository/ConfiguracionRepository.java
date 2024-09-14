package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Configuracion;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends PagingAndSortingRepository<Configuracion, Long>, JpaSpecificationExecutor<Configuracion> {

    Page<Configuracion> findAll(Specification<Configuracion> spec, Pageable pageable);

    Optional<Configuracion> findByNombreIgnoreCaseAndIdNot(String toUpperCase, Long id);
}
