package py.gov.mitic.adminpy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import py.gov.mitic.adminpy.model.entity.Auditoria;

@Repository
public interface AuditoriaRepository extends PagingAndSortingRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
	
	Page<Auditoria> findAll(Specification<Auditoria> spec, Pageable pageable);

}
