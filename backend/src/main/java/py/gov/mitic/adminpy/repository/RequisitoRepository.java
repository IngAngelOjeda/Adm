package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Requisito;
import py.gov.mitic.adminpy.repository.projections.EtiquetaServicioDTO;
import py.gov.mitic.adminpy.repository.projections.RequisitoServicioDTO;

import java.util.List;

@Repository
public interface RequisitoRepository extends PagingAndSortingRepository<Requisito, Long>, JpaSpecificationExecutor<Requisito>{

    List<Requisito> findByEstado(Boolean estado);


    @Query(value = "SELECT r.id_requisito, r.nombre_requisito FROM public.requisito r inner join public.servicio_requisito sr on sr.id_requisito = r.id_requisito WHERE sr.id_servicio=:id_servicio", nativeQuery = true)
    List<RequisitoServicioDTO> findByIdServicio(@Param("id_servicio") Long id_servicio);
}
