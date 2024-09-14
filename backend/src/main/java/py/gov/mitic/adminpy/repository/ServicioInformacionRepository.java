package py.gov.mitic.adminpy.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.entity.ServicioInformacion;

import java.util.List;

@Repository
public interface ServicioInformacionRepository extends PagingAndSortingRepository<ServicioInformacion, Long>, JpaSpecificationExecutor<ServicioInformacion>{

    Page<ServicioInformacion> findAll(Specification<ServicioInformacion> spec, Pageable pageable);



}
