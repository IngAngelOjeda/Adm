package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.DatosOee;
import py.gov.mitic.adminpy.repository.projections.DatosOeeReporteDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatosOeeRepository extends PagingAndSortingRepository<DatosOee, Long>, JpaSpecificationExecutor<DatosOee> {

    Page<DatosOee> findAll(Specification<DatosOee> spec, Pageable pageable);

    Optional<DatosOee> findById(Long id);

//    @Query("SELECT NEW py.gov.mitic.adminpy.model.dto.FuncionarioReporteDTO(" +
//            "f.cargo, f.nombre, f.apellido, d.descripcionDependencia, f.cedula) " +
//            "FROM Funcionario f " +
//            "LEFT JOIN f.dependencia d " +
//            "WHERE f.estadoFuncionario = true")
//    List<DatosOee> findByEstadoOeeInformacion(String estado);


    @Query(value = "SELECT oi.id_oee_informacion, oi.id_tipo_dato, oi.id_oee, " +
            "o.descripcion_oee, oi.descripcion_oee_informacion, " +
            "oi.estado_oee_informacion, td.descripcion_tipo_dato, td.estado " +
            "FROM public.oee_informacion oi " +
            "JOIN public.tipo_dato td ON td.id_tipo_dato = oi.id_tipo_dato " +
            "JOIN public.oee o ON o.id_oee = oi.id_oee " +
            "WHERE (:descripcionOeeInformacion IS NULL OR oi.descripcion_oee_informacion LIKE CONCAT('%', :descripcionOeeInformacion, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:descripcionTipoDato IS NULL OR td.descripcion_tipo_dato LIKE CONCAT('%', :descripcionTipoDato, '%')) " +
            "AND (:id IS NULL OR o.id_oee = :id) " , nativeQuery = true)
    List<DatosOeeReporteDTO> findDependenciaIdWithPermission(
            @Param("descripcionOeeInformacion") String descripcionOeeInformacion,
            @Param("descripcionOee") String descripcionOee,
            @Param("descripcionTipoDato") String descripcionTipoDato,
            @Param("id") Long id);

    @Query(value = "SELECT oi.id_oee_informacion, oi.id_tipo_dato, oi.id_oee, " +
            "o.descripcion_oee, oi.descripcion_oee_informacion, " +
            "oi.estado_oee_informacion, td.descripcion_tipo_dato, td.estado " +
            "FROM public.oee_informacion oi " +
            "JOIN public.tipo_dato td ON td.id_tipo_dato = oi.id_tipo_dato " +
            "JOIN public.oee o ON o.id_oee = oi.id_oee " +
            "WHERE (:descripcionOeeInformacion IS NULL OR oi.descripcion_oee_informacion LIKE CONCAT('%', :descripcionOeeInformacion, '%')) " +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:descripcionTipoDato IS NULL OR td.descripcion_tipo_dato LIKE CONCAT('%', :descripcionTipoDato, '%')) " , nativeQuery = true)
    List<DatosOeeReporteDTO> findDependenciaIdWithoutPermission(
            @Param("descripcionOeeInformacion") String descripcionOeeInformacion,
            @Param("descripcionOee") String descripcionOee,
            @Param("descripcionTipoDato") String descripcionTipoDato);





//    @Query(value =  "SELECT oi.id_oee_informacion, oi.id_tipo_dato, oi.id_oee, " +
//                    "o.descripcion_oee, oi.descripcion_oee_informacion, " +
//                    "oi.estado_oee_informacion, td.descripcion_tipo_dato, td.estado " +
//                    "FROM public.oee_informacion oi " +
//                    "join public.tipo_dato td on td.id_tipo_dato = oi.id_tipo_dato " +
//                    "join public.oee o on o.id_oee = oi.id_oee", nativeQuery = true)
//    List<DatosOeeReporteDTO> findByEstadoOeeInformacion(@Param("id") Long id);

}
