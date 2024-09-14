package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.Sistema;
import py.gov.mitic.adminpy.repository.projections.DatosOeeReporteDTO;
import py.gov.mitic.adminpy.repository.projections.SistemasOeeReporteDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SistemaRepository extends PagingAndSortingRepository<Sistema, Long>, JpaSpecificationExecutor<Sistema>{

    Page<Sistema> findAll(Specification<Sistema> spec, Pageable pageable);

    @Query("SELECT count(s) = 1 FROM Sistema s WHERE nombre = ?1 and idSistema != ?2 ")
    boolean findExistByNombre(String nombre, Long id);

    @Query(value =  "SELECT s.nombre, s.objeto_proposito, s.area_responsable, o.descripcion_oee " +
            "FROM public.sistema s " +
            "JOIN public.oee o ON s.id_oee = o.id_oee " +
            "WHERE (:nombreSistema IS NULL OR s.nombre LIKE CONCAT('%', :nombreSistema, '%')) " +
            "AND (:objetivoSistema IS NULL OR s.objeto_proposito LIKE CONCAT('%', :objetivoSistema, '%')) " +
            "AND (:areaResponsable IS NULL OR s.area_responsable LIKE CONCAT('%', :areaResponsable, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:id IS NULL OR o.id_oee = :id) ", nativeQuery = true)
    List<SistemasOeeReporteDTO> findSistemasIdWithPermission(
            @Param("nombreSistema") String nombreSistema,
            @Param("objetivoSistema") String objetivoSistema,
            @Param("areaResponsable") String areaResponsable,
            @Param("descripcionOee") String descripcionOee,
            @Param("id") Long id);

    @Query(value = "SELECT s.nombre, s.objeto_proposito, s.area_responsable, o.descripcion_oee " +
            "FROM public.sistema s " +
            "JOIN public.oee o ON s.id_oee = o.id_oee " +
            "WHERE (:nombreSistema IS NULL OR s.nombre LIKE CONCAT('%', :nombreSistema, '%')) " +
            "AND (:objetivoSistema IS NULL OR s.objeto_proposito LIKE CONCAT('%', :objetivoSistema, '%')) " +
            "AND (:areaResponsable IS NULL OR s.area_responsable LIKE CONCAT('%', :areaResponsable, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " , nativeQuery = true)
    List<SistemasOeeReporteDTO> findSistemasIdWithoutPermission(
            @Param("nombreSistema") String nombreSistema,
            @Param("objetivoSistema") String objetivoSistema,
            @Param("areaResponsable") String areaResponsable,
            @Param("descripcionOee") String descripcionOee );

}
