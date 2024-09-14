package py.gov.mitic.adminpy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.adminpy.model.entity.RecuperarClave;

@Repository
public interface RecuperarClaveRepository  extends JpaRepository<RecuperarClave, Long> {
    RecuperarClave findByUsuario_idUsuario(Long idUsuario);

    @Query("SELECT new RecuperarClave(t.id, t.codigo, u.username) FROM RecuperarClave t INNER JOIN t.usuario u WHERE t.codigo = ?1")
    RecuperarClave getCodeByUser(String code);

    @Query("SELECT new RecuperarClave(t.id, t.codigo, u.username) FROM RecuperarClave t INNER JOIN t.usuario u WHERE t.codigo = ?1")
    RecuperarClave getUserByCode(String code);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM recuperar_clave WHERE codigo =:code")
    void deleteByCode(String code);
}
