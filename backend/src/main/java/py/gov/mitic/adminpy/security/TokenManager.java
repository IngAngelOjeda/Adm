package py.gov.mitic.adminpy.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import py.gov.mitic.adminpy.model.dto.UsuarioSession;
import java.util.Date;
import java.util.function.Function;

public interface TokenManager {

    String getUsernameFromToken(String token);

    Date getExpirationDateFromToken(String token);

    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    Boolean validateToken(String token, String username);

    UsuarioSession validateUserToken(String reqHeader);

    String createRefreshToken(String token);

}
