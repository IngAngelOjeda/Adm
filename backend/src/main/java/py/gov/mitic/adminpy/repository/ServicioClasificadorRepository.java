package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import py.gov.mitic.adminpy.model.entity.ServicioClasificador;

import java.util.List;

public interface ServicioClasificadorRepository extends PagingAndSortingRepository<ServicioClasificador, Long>, JpaSpecificationExecutor<ServicioClasificador> {

    @Query(nativeQuery = true, value = "SELECT * FROM servicio_clasificador WHERE id_servicio = :idServicio")
    List<ServicioClasificador> findByServcioId(Long idServicio);
}
