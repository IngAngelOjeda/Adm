package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Rol;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends PagingAndSortingRepository<Rol, Long>, JpaSpecificationExecutor<Rol> {

    Page<Rol> findAll(Specification<Rol> spec, Pageable pageable);

    Optional<Rol> findById(Long idRol);

    @Query("SELECT new Rol(r.idRol, r.nombre) FROM Rol r WHERE r.estado is true")
    List<Rol> findAllRole();

    Optional<Rol> findByNombreIgnoreCase(String name);

    Optional<Rol> findByNombreIgnoreCaseAndIdRolNot(String name, Long idRol);

}
