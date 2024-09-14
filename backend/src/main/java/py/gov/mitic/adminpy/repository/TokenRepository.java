package py.gov.mitic.adminpy.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import py.gov.mitic.adminpy.model.dto.UsuarioSession;

@Repository
public class TokenRepository {

    private static String KEY = "backendadminpy";
    private RedisTemplate<String, UsuarioSession> redisTemplate;
    private HashOperations<String, String, UsuarioSession> hashOperations;
    
    private Log logger = LogFactory.getLog(TokenRepository.class);

    public TokenRepository(RedisTemplate<String, UsuarioSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    public UsuarioSession get(String token) {
        return (UsuarioSession) hashOperations.get(KEY, token);
    }

    public void create(UsuarioSession item) {
        hashOperations.put(KEY, item.getToken(), item);
    }

    public void update(UsuarioSession item) {
        create(item);
    }

    public void delete(String token) {
        logger.info("token antes de eliminar: "+get(token));
        hashOperations.delete(KEY, token);
        if (get(token) == null) {
        	logger.info("Token eliminado.");
        }
    }

}
