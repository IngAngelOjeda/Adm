package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Permiso;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends PagingAndSortingRepository<Permiso, Long>, JpaSpecificationExecutor<Permiso> {

    Page<Permiso> findAll(Specification<Permiso> spec, Pageable pageable);

    @Query("SELECT new Permiso(p.idPermiso, p.nombre, p.descripcion) FROM Permiso p")
    List<Permiso> findAllPermiso();

    Optional<Permiso> findByNombreIgnoreCase(String name);

    Optional<Permiso> findByNombreIgnoreCaseAndIdPermisoNot(String name, Long idPermiso);
}
