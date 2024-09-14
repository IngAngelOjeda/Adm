package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.adminpy.model.dto.DependenciaFuncionarioWithParentDTO;
import py.gov.mitic.adminpy.model.dto.DependenciaWithParentDTO;
import py.gov.mitic.adminpy.model.dto.FuncionarioDTO;
import py.gov.mitic.adminpy.model.entity.Funcionario;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.repository.projections.FuncionarioReporteDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends PagingAndSortingRepository<Funcionario, Long>, JpaSpecificationExecutor<Funcionario>{

    Page<Funcionario> findAll(Specification<Funcionario> spec, Pageable pageable);

    @Query("SELECT NEW py.gov.mitic.adminpy.model.dto.DependenciaFuncionarioWithParentDTO(" +
            "d.idDependencia, d.descripcionDependencia, d.orden, " +
            "COALESCE(dp.idDependencia, NULL), " +
            "COALESCE(dp.descripcionDependencia, NULL), " +
            "d.oee.id, d.oee.descripcionOee, d.estadoDependencia, d.fechaCreacion, " +
            "f.idFuncionario, f.nombre, f.apellido, f.cargo) " +
            "FROM Dependencia d " +
            "LEFT JOIN d.dependenciaPadre dp " +
            "RIGHT JOIN Funcionario f ON d.idDependencia = f.dependencia.idDependencia " +
            "WHERE d.oee.id = :idOee AND d.estadoDependencia = true AND (f IS NULL OR f.estadoFuncionario = true)")
    List<DependenciaFuncionarioWithParentDTO> findDependenciasFuncionariosWithParentByOeeId(@Param("idOee") Long idOee);

    Optional<Funcionario> findById(Long id);

    @Query(value = "SELECT f.id_funcionario, f.apellido, f.cargo, " +
            "f.cedula, f.correo_institucional, f.estado_funcionario, " +
            "f.nombre, d.descripcion_dependencia, o.descripcion_oee  " +
            "FROM public.funcionario f " +
            "JOIN public.dependencia d ON d.id_dependencia = f.id_dependencia " +
            "JOIN public.oee o ON o.id_oee = d.id_oee " +
            "WHERE (:apellido IS NULL OR f.apellido LIKE CONCAT('%', :apellido, '%'))" +
            "AND (:cargo IS NULL OR f.cargo LIKE CONCAT('%', :cargo, '%'))" +
            "AND (:cedula IS NULL OR f.cedula LIKE CONCAT('%', :cedula, '%'))" +
            "AND (:correoInstitucional IS NULL OR f.correo_institucional LIKE CONCAT('%', :correoInstitucional, '%'))" +
            "AND (:nombre IS NULL OR f.nombre LIKE CONCAT('%', :nombre, '%'))" +
            "AND (:descripcionDependencia IS NULL OR d.descripcion_dependencia LIKE CONCAT('%', :descripcionDependencia, '%'))" +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%')) " +
            "AND (:id IS NULL OR o.id_oee = :id)", nativeQuery = true)
    List<FuncionarioReporteDTO> findFuncionarioOeeIdWithPermission(
            @Param("apellido") String apellido,
            @Param("cargo") String cargo,
            @Param("cedula") String cedula,
            @Param("correoInstitucional") String correoInstitucional,
            @Param("nombre") String nombre,
            @Param("descripcionDependencia") String descripcionDependencia,
            @Param("descripcionOee") String descripcionOee,
            @Param("id") Long id
    );


    @Query(value = "SELECT f.id_funcionario, f.apellido, f.cargo, " +
            "f.cedula, f.correo_institucional, f.estado_funcionario, " +
            "f.nombre, d.descripcion_dependencia, o.descripcion_oee  " +
            "FROM public.funcionario f " +
            "JOIN public.dependencia d ON d.id_dependencia = f.id_dependencia " +
            "JOIN public.oee o ON o.id_oee = d.id_oee " +
            "WHERE (:apellido IS NULL OR f.apellido LIKE CONCAT('%', :apellido, '%'))" +
            "AND (:cargo IS NULL OR f.cargo LIKE CONCAT('%', :cargo, '%'))" +
            "AND (:cedula IS NULL OR f.cedula LIKE CONCAT('%', :cedula, '%'))" +
            "AND (:correoInstitucional IS NULL OR f.correo_institucional LIKE CONCAT('%', :correoInstitucional, '%'))" +
            "AND (:nombre IS NULL OR f.nombre LIKE CONCAT('%', :nombre, '%'))" +
            "AND (:descripcionDependencia IS NULL OR d.descripcion_dependencia LIKE CONCAT('%', :descripcionDependencia, '%'))" +
            "AND (:descripcionOee IS NULL OR o.descripcion_oee LIKE CONCAT('%', :descripcionOee, '%'))", nativeQuery = true)
    List<FuncionarioReporteDTO> findFuncionarioOeeIdWithoutPermission(
            @Param("apellido") String apellido,
            @Param("cargo") String cargo,
            @Param("cedula") String cedula,
            @Param("correoInstitucional") String correoInstitucional,
            @Param("nombre") String nombre,
            @Param("descripcionDependencia") String descripcionDependencia,
            @Param("descripcionOee") String descripcionOee
    );



}
