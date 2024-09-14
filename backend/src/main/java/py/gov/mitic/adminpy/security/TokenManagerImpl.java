package py.gov.mitic.adminpy.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.impl.DefaultClock;
import py.gov.mitic.adminpy.model.dto.UsuarioSession;

/**
 * Gestión de token de authorización
 * */
@Component("tokenConfig")
public class TokenManagerImpl implements TokenManager {

    private Log logger = LogFactory.getLog(TokenManagerImpl.class);
    private Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    String secret;

    @Value("${jwt.expiration}")
    Long expiration;

    @Value("${jwt.refreshExpiration}")
    Long refreshExpiration;

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 1. Se define: claims of the token, like Issuer, Expiration, Subject, and the ID
     * 2. Firma jwt utilizando el algoritmo HS512 y la clave secreta.
     * 3. According to JWS Compact
     * */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate, (expiration * 1000));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public String createRefreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate, (refreshExpiration * 1000));
        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    @Override
    public UsuarioSession validateUserToken(String reqHeader) {
        try {
            String token = reqHeader.substring(7);
            String username = getUsernameFromToken(token);
            return new UsuarioSession(username, token);
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado");
        } catch (IllegalArgumentException | UnsupportedJwtException | MalformedJwtException e) {
            logger.error("No se pudo obtener el token");
        } catch (Exception e) {
            logger.error("Permiso denegado");
        }
        return null;
    }

    private Date calculateExpirationDate(Date currentDate, Long expirationToken) {
        return new Date(currentDate.getTime() + expirationToken);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

}
