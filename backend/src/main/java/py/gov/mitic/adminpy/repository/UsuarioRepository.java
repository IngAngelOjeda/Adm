package py.gov.mitic.adminpy.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.model.entity.Usuario;
import py.gov.mitic.adminpy.repository.projections.UsuarioPermisoDTO;

@Repository
public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

	Page<Usuario> findAll(Specification<Usuario> spec, Pageable pageable);

	@Query("SELECT u FROM Usuario u")
	List<Usuario> findAllUsuarios();

	@Query("SELECT u FROM Usuario u WHERE u.idOee=?1")
	List<Usuario> findAllUsuariosOee(Long idOee);

	@Query("SELECT u FROM Usuario u WHERE u.username=?1")
	Usuario findByUsername(String username);

	@Query("SELECT new Usuario(u.idUsuario, u.username, u.nombre, u.apellido, u.estado, u.telefono, u.cargo, o.id, o.descripcionOee, o.descripcionCorta) "
			+ " FROM Usuario u INNER JOIN u.oee o WHERE u.username=?1")
	Usuario findByUserSession(String username);

	@Query(value = "SELECT u.id as idUsuario, ur.rol_id as idRol, p.id as idPermiso, p.nombre as permiso FROM public.usuario u inner join public.rol_usuario ur on ur.usuario_id = u.id inner join public.rol_permiso rp on rp.rol_id = ur.rol_id inner join public.permiso p on rp.permiso_id = p.id WHERE u.id=:idUsuario", nativeQuery = true)
	List<UsuarioPermisoDTO> findRolesPorUsuario(@Param("idUsuario") Long idUsuario);

	@Query("SELECT count(1) FROM Usuario WHERE (estado = :estado OR null = :estado) and (oee.id = :idInstitucion OR -1 = :idInstitucion)")
    Long countByUserStatus(@Param("idInstitucion") Long idInstitucion, @Param("estado") Boolean estado);

	Usuario findByCorreo(String correo);

	@Query("SELECT count(u) = 1 FROM Usuario u WHERE username = ?1 and idUsuario != ?2 ")
	boolean findExistByUsername(String username, Long id);

	@Query("SELECT u FROM Usuario u WHERE username = ?1 and cedula != ?2 ")
	Usuario findExistByUsernameCedula(String username, String cedula);

	@Query("SELECT count(u) = 1 FROM Usuario u WHERE correo = ?1 and idUsuario != ?2 ")
	boolean findExistByCorreo(String correo, Long id);

	@Query("SELECT u FROM Usuario u WHERE u.tokenReset=?1")
	Usuario getCodeByUser(String code);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = "UPDATE usuario SET token_reset=NULL WHERE token_reset =:code")
	void deleteByCode(String code);

}