package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Clasificador;
import py.gov.mitic.adminpy.repository.projections.ClasificadorServicioDTO;
import py.gov.mitic.adminpy.repository.projections.RequisitoServicioDTO;

import java.util.List;

@Repository
public interface ClasificadorRepository extends PagingAndSortingRepository<Clasificador, Long>, JpaSpecificationExecutor<Clasificador>{

    List<Clasificador> findByEstadoClasificador(String estado);

    @Query(value = "SELECT c.id_clasificador, c.nombre_clasificador  FROM public.clasificador c inner join public.servicio_clasificador sc on sc.id_clasificador = c.id_clasificador WHERE sc.id_servicio=:id_servicio", nativeQuery = true)
    List<ClasificadorServicioDTO> findByIdServicio(@Param("id_servicio") Long id_servicio);

}
