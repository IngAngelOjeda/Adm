package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.model.entity.SubDominio;

import java.util.List;

@Repository
public interface EtiquetaRepository extends PagingAndSortingRepository<Etiqueta, Long>, JpaSpecificationExecutor<Etiqueta>{

    List<Etiqueta> findByEstadoEtiqueta(String estado);


}
