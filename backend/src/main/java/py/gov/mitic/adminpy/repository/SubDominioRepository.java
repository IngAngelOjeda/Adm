package py.gov.mitic.adminpy.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import py.gov.mitic.adminpy.model.entity.Oee;
import py.gov.mitic.adminpy.model.entity.SubDominio;
import py.gov.mitic.adminpy.repository.projections.SubdominioReporteDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubDominioRepository extends PagingAndSortingRepository<SubDominio, Long>, JpaSpecificationExecutor<SubDominio>{

    Page<SubDominio> findAll(Specification<SubDominio> spec, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM subdominio WHERE id_dominio = :idDominio")
    List<SubDominio> findSubDominiosByDominioId(Long idDominio);

    @Query(value =  "SELECT d.dominio, s.subdominio, o.descripcion_oee " +
            "FROM public.subdominio s " +
            "JOIN public.dominio d on s.id_dominio = d.id_dominio " +
            "JOIN public.oee o on d.id_oee = o.id_oee " +
            "AND (:subdominio IS NULL OR s.subdominio LIKE CONCAT('%', :subdominio, '%')) "+
            "AND (:id IS NULL OR o.id_dominio = :id) ", nativeQuery = true)
    List<SubdominioReporteDTO> findSubDominioIdWithPermission(
            @Param("subdomino") String subdominio,
            @Param("id") Long id);

    @Query(value = "SELECT d.dominio, s.subdominio, o.descripcion_oee " +
            "FROM public.subdominio s " +
            "JOIN public.dominio d on s.id_dominio = d.id_dominio " +
            "JOIN public.oee o on d.id_oee = o.id_oee " +
            "AND (:subdominio IS NULL OR s.subdominio LIKE CONCAT('%', :subdominio, '%')) " , nativeQuery = true)
    List<SubdominioReporteDTO> findSubDominioIdWithoutPermission(
            @Param("subdominio") String subdominio);

}
