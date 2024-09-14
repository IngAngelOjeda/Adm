package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.RangoIp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.adminpy.repository.projections.RangoIpReporteDTO;
import py.gov.mitic.adminpy.repository.projections.SistemasOeeReporteDTO;

@Repository
public interface RangoIpRepository extends PagingAndSortingRepository<RangoIp, Long>, JpaSpecificationExecutor<RangoIp> {

    Page<RangoIp> findAll(Specification<RangoIp> spec, Pageable pageable);

    @Query("SELECT r FROM RangoIp r ORDER BY r.idRango DESC")
    List<RangoIp> findTopByOrderByIdRangoDesc(Pageable pageable);

    @Query(value =  "SELECT r.rango, o.descripcion_oee, " +
            "CASE WHEN r.pertenece_dmz THEN 'Si' ELSE 'No' END AS pertenece_dmz, " +
            "CASE WHEN r.pertenece_ipnavegacion THEN 'Si' ELSE 'No' END AS pertenece_ipnavegacion, " +
            "CASE WHEN r.pertenece_vpn THEN 'Si' ELSE 'No' END AS pertenece_vpn " +
            "FROM public.rangoip r " +
            "JOIN public.oee o ON r.id_oee = o.id_oee " +
            "WHERE (:rango IS NULL OR r.rango LIKE CONCAT('%', :rango, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:id IS NULL OR o.id_oee = :id) ", nativeQuery = true)
    List<RangoIpReporteDTO> findRangoIpIdWithPermission(
            @Param("rango") String rango,
            @Param("descripcionOee") String descripcionOee,
            @Param("id") Long id);

    @Query(value = "SELECT r.rango, o.descripcion_oee, " +
            "CASE WHEN r.pertenece_dmz THEN 'Si' ELSE 'No' END AS pertenece_dmz, " +
            "CASE WHEN r.pertenece_ipnavegacion THEN 'Si' ELSE 'No' END AS pertenece_ipnavegacion, " +
            "CASE WHEN r.pertenece_vpn THEN 'Si' ELSE 'No' END AS pertenece_vpn " +
            "FROM public.rangoip r " +
            "JOIN public.oee o ON r.id_oee = o.id_oee " +
            "WHERE (:rango IS NULL OR r.rango LIKE CONCAT('%', :rango, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " , nativeQuery = true)
    List<RangoIpReporteDTO> findRangoIpIdWithoutPermission(
            @Param("rango") String rango,
            @Param("descripcionOee") String descripcionOee );

}
