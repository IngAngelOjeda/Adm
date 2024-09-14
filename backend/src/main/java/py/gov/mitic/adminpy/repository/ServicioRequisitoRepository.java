package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.adminpy.model.dto.EtiquetaDTO;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.model.entity.ServicioEtiqueta;
import py.gov.mitic.adminpy.model.entity.ServicioRequisito;
import py.gov.mitic.adminpy.repository.projections.EtiquetaServicioDTO;
import py.gov.mitic.adminpy.repository.projections.UsuarioPermisoDTO;

import java.util.List;
import java.util.Optional;

public interface ServicioRequisitoRepository extends PagingAndSortingRepository<ServicioRequisito, Long>, JpaSpecificationExecutor<ServicioRequisito> {

    Page<ServicioRequisito> findAll(Specification<ServicioRequisito> spec, Pageable pageable);


    @Query(nativeQuery = true, value = "SELECT * FROM servicio_requisito WHERE id_servicio = :idServicio")
    List<ServicioRequisito> findByServcioId(Long idServicio);

}