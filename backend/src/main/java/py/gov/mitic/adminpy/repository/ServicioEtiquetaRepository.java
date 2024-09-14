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
import py.gov.mitic.adminpy.repository.projections.EtiquetaServicioDTO;
import py.gov.mitic.adminpy.repository.projections.UsuarioPermisoDTO;

import java.util.List;
import java.util.Optional;

public interface ServicioEtiquetaRepository extends PagingAndSortingRepository<ServicioEtiqueta, Long>, JpaSpecificationExecutor<ServicioEtiqueta> {
    Page<ServicioEtiqueta> findAll(Specification<ServicioEtiqueta> spec, Pageable pageable);

    @Query(value = "SELECT e.id_etiqueta, e.nombre_etiqueta FROM public.etiqueta e inner join public.servicio_etiqueta se on se.id_etiqueta = e.id_etiqueta WHERE se.id_servicio=:id_servicio", nativeQuery = true)
    List<EtiquetaServicioDTO> findByIdServicio(@Param("id_servicio") Long id_servicio);

    @Query(nativeQuery = true, value = "SELECT * FROM servicio_etiqueta WHERE id_servicio = :idServicio")
    List<ServicioEtiqueta> findByServcioId(Long idServicio);

}
