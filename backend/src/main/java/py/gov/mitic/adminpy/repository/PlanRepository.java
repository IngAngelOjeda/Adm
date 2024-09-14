package py.gov.mitic.adminpy.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Plan;
import py.gov.mitic.adminpy.model.entity.RangoIp;
import py.gov.mitic.adminpy.repository.projections.PlanReporteDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends PagingAndSortingRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    Page<Plan> findAll(Specification<Plan> spec, Pageable pageable);

    Optional<Plan> findById(Long id);

    @Query("SELECT p FROM Plan p ORDER BY p.idPlan DESC")
    List<Plan> findTopByOrderByIdPlanDesc(Pageable pageable);

    @Query(value =  "SELECT p.anho, p.fecha, p.link_plan, p.cantidad_funcionarios_tic, p.cantidad_funcionarios_admin, p.presupuesto_tic_anual " +
            "FROM public.plan p " +
            "WHERE (:anho IS NULL OR p.anho LIKE CONCAT('%', :anho, '%')) " +
            "AND (:linkPlan IS NULL OR p.link_plan LIKE CONCAT('%', :linkPlan, '%')) " +
            "AND (:cantidadFuncionariosTic IS NULL OR p.cantidad_funcionarios_tic LIKE CONCAT('%', :cantidadFuncionariosTic, '%')) " +
            "AND (:presupuestoTicAnual IS NULL OR p.presupuesto_tic_anual LIKE CONCAT('%', :presupuestoTicAnual, '%')) ", nativeQuery = true)
    List<PlanReporteDTO> findPlanIdWithPermission(
            @Param("anho") String anho,
            @Param("linkPlan") String linkPlan,
            @Param("cantidadFuncionariosTic") String cantidadFuncionariosTic,
            @Param("presupuestoTicAnual") String presupuestoTicAnual);

    @Query(value = "SELECT p.anho, p.fecha, p.link_plan, p.cantidad_funcionarios_tic, p.cantidad_funcionarios_admin, p.presupuesto_tic_anual  " +
            "FROM public.plan p " +
            "WHERE (:anho IS NULL OR p.anho LIKE CONCAT('%', :anho, '%')) " +
            "AND (:linkPlan IS NULL OR p.link_plan LIKE CONCAT('%', :linkPlan, '%')) " +
            "AND (:cantidadFuncionariosTic IS NULL OR p.cantidad_funcionarios_tic LIKE CONCAT('%', :cantidadFuncionariosTic, '%')) " +
            "AND (:presupuestoTicAnual IS NULL OR p.presupuesto_tic_anual LIKE CONCAT('%', :presupuestoTicAnual, '%')) " , nativeQuery = true)
    List<PlanReporteDTO> findPlanIdWithoutPermission(
            @Param("anho") String anho,
            @Param("linkPlan") String linkPlan,
            @Param("cantidadFuncionariosTic") String cantidadFuncionariosTic,
            @Param("presupuestoTicAnual") String presupuestoTicAnual);

}
