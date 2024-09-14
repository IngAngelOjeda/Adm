package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.model.entity.Servicio;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends PagingAndSortingRepository<Servicio, Long>, JpaSpecificationExecutor<Servicio> {

    Page<Servicio> findAll(Specification<Servicio> spec, Pageable pageable);

    Optional<Servicio> findById(Long id);

    @Query("SELECT s FROM Servicio s WHERE s.oee.id = :IdOee")
    List<Servicio> findByOeeId(@Param("IdOee") Long IdOee);

}
