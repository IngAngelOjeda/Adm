package py.gov.mitic.adminpy.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import py.gov.mitic.adminpy.model.dto.DominioDTO;
import py.gov.mitic.adminpy.model.entity.Dominio;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.repository.projections.DominioReporteDTO;

import java.util.List;
import java.util.Optional;


@Repository
public interface DominioRepository extends PagingAndSortingRepository<Dominio, Long>, JpaSpecificationExecutor<Dominio> {
    Page<Dominio> findAll(Specification<Dominio> spec, Pageable pageable);

    Optional<Dominio> findById(Long id);
//    @Query("SELECT new Dominio(d.id, d.dominio, d.estado) FROM Dominio d")
//    List<Dominio> findAllDominio();

    @Query("SELECT DISTINCT d FROM Dominio d LEFT JOIN FETCH d.oee")
    List<Dominio> findAllDominio();

    @Query(value =  "SELECT d.dominio, o.descripcion_oee, estado " +
            "FROM public.dominio d " +
            "JOIN public.oee o ON d.id_oee = o.id_oee " +
            "WHERE (:dominio IS NULL OR d.dominio LIKE CONCAT('%', :dominio, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:id IS NULL OR o.id_oee = :id) ", nativeQuery = true)
    List<DominioReporteDTO> findDominiosIdWithPermission(
            @Param("dominio") String dominio,
            @Param("descripcionOee") String descripcionOee,
            @Param("id") Long id);

    @Query(value = "SELECT d.dominio, o.descripcion_oee, estado " +
            "FROM public.dominio d " +
            "JOIN public.oee o ON d.id_oee = o.id_oee " +
            "WHERE (:dominio IS NULL OR d.dominio LIKE CONCAT('%', :dominio, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " , nativeQuery = true)
    List<DominioReporteDTO> findDominiosIdWithoutPermission(
            @Param("dominio") String dominio,
            @Param("descripcionOee") String descripcionOee );


}
