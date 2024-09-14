package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Dominio;
import py.gov.mitic.adminpy.model.entity.TipoDato;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDatoRepository extends  PagingAndSortingRepository<TipoDato, Long>, JpaSpecificationExecutor<TipoDato>{

    List<TipoDato> findByEstado(Boolean estado);

    Optional<TipoDato> findById(Long idTipoDato);
}
