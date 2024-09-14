package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import py.gov.mitic.adminpy.model.entity.Dependencia;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.adminpy.model.dto.DependenciaWithParentDTO;
import py.gov.mitic.adminpy.repository.projections.DependenciaReporteDTO;


@Repository
public interface DependenciaRepository extends  PagingAndSortingRepository<Dependencia, Long>, JpaSpecificationExecutor<Dependencia>{

    Page<Dependencia> findAll(Specification<Dependencia> spec, Pageable pageable);

    List<Dependencia> findByEstadoDependencia(Boolean estadoDependencia);

    boolean existsByOrden(Long orden);

    Optional<Dependencia> findById(Long idDependencia);

    // #### TreeNode
    //List<Dependencia> findByOeeId(@Param("id") Long id);

    @Query("SELECT NEW py.gov.mitic.adminpy.model.dto.DependenciaWithParentDTO(" +
            "d.idDependencia, d.descripcionDependencia, d.orden, " +
            "COALESCE(dp.idDependencia, NULL), " +
            "COALESCE(dp.descripcionDependencia, NULL), " +
            "d.oee.id, d.oee.descripcionOee, d.estadoDependencia, d.fechaCreacion) " +
            "FROM Dependencia d " +
            "LEFT JOIN d.dependenciaPadre dp " +
            "WHERE d.oee.id = :idOee AND d.estadoDependencia = true")
    List<DependenciaWithParentDTO> findDependenciasWithParentByOeeId(@Param("idOee") Long idOee);

    List<Dependencia> findByOeeIdAndEstadoDependenciaIsTrue(Long idOee);

    List<Dependencia> findByDependenciaPadre_IdDependencia(Long idDependencia);


    @Query(value = "SELECT d.id_dependencia, d.id_dependencia_padre, d.codigo, " +
            "d.descripcion_dependencia, COALESCE(dd.descripcion_dependencia ,'') as descripcion_dependencia_padre, o.descripcion_oee " +
            "FROM public.dependencia d " +
            "JOIN public.oee o ON o.id_oee = d.id_oee " +
            "LEFT JOIN public.dependencia dd ON d.id_dependencia_padre = dd.id_dependencia " +
            "WHERE (:codigo IS NULL OR d.codigo LIKE CONCAT('%', :codigo, '%')) " +
            "AND (:descripcionDependencia IS NULL OR d.descripcion_dependencia LIKE CONCAT('%', :descripcionDependencia, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%'))" +
            "AND (:idOee IS NULL OR o.id_oee = :idOee)", nativeQuery = true)
    List<DependenciaReporteDTO> findDependenciaOeeWitId(
            @Param("codigo") String codigo,
            @Param("descripcionDependencia") String descripcionDependencia,
            @Param("descripcionOee") String descripcionOee,
            @Param("idOee") Long idOee);


    @Query(value = "SELECT d.id_dependencia, d.id_dependencia_padre, d.codigo, " +
            "d.descripcion_dependencia, COALESCE(dd.descripcion_dependencia ,'') as descripcion_dependencia_padre, o.descripcion_oee " +
            "FROM public.dependencia d " +
            "JOIN public.oee o ON o.id_oee = d.id_oee " +
            "LEFT JOIN public.dependencia dd ON d.id_dependencia_padre = dd.id_dependencia " +
            "WHERE (:codigo IS NULL OR d.codigo LIKE CONCAT('%', :codigo, '%')) " +
            "AND (:descripcionDependencia IS NULL OR d.descripcion_dependencia LIKE CONCAT('%', :descripcionDependencia, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%'))", nativeQuery = true)
    List<DependenciaReporteDTO> findDependenciaOeeWithoutId(
            @Param("codigo") String codigo,
            @Param("descripcionDependencia") String descripcionDependencia,
            @Param("descripcionOee") String descripcionOee);




//    @Query(value =  "SELECT d.id_dependencia, d.id_dependencia_padre, d.codigo , " +
//                    "d.descripcion_dependencia, COALESCE(dd.descripcion_dependencia ,'') as descripcion_dependencia_padre, o.descripcion_oee " +
//                    "FROM public.dependencia d " +
//                    "join public.oee o on o.id_oee = d.id_oee " +
//                    "left join public.dependencia dd on d.id_dependencia_padre = dd.id_dependencia", nativeQuery = true)
//    List<DependenciaReporteDTO> findDependenciaOee(Long idOee);

}
