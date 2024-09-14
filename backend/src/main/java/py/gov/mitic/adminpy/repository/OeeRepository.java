package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Oee;

import java.util.List;
import java.util.Optional;

@Repository
public interface OeeRepository extends PagingAndSortingRepository<Oee, Long>, JpaSpecificationExecutor<Oee> {

    Page<Oee> findAll(Specification<Oee> spec, Pageable pageable);

    List<Oee> findByEstadoOee(String estado);

    Optional<Oee> findByDescripcionOeeIgnoreCase(String descripcion);

    Optional<Oee> findByDescripcionOeeIgnoreCaseAndIdNot(String descripcion, Long idOee);
}
