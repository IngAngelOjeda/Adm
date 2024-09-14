package py.gov.mitic.adminpy.security;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import py.gov.mitic.adminpy.model.dto.UsuarioSession;
import py.gov.mitic.adminpy.repository.TokenRepository;
import py.gov.mitic.adminpy.service.UsuarioService;

/**
 * Clase base de filtro que tiene como objetivo garantizar una ejecución única por envío de solicitud,
 * en cualquier contenedor de servlets.
 * */
@Component
public class SecurityFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	private final TokenManager tokenComponent;

	private final TokenRepository tokenRepository;

	private final UsuarioService usuarioService;

	@Autowired
	public SecurityFilter(@NotNull TokenManager tokenComponent, @NotNull TokenRepository tokenRepository, @NotNull UsuarioService usuarioService) {
		this.tokenComponent = tokenComponent;
		this.tokenRepository = tokenRepository;
		this.usuarioService = usuarioService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {

			final String reqHeader = request.getHeader(AUTHORIZATION_HEADER);
			if (Objects.isNull(reqHeader) || !reqHeader.startsWith(TOKEN_PREFIX)) {
				chain.doFilter(request, response);
				return;
			}

			final UsuarioSession currentUser = tokenComponent.validateUserToken(reqHeader);

			if (Objects.nonNull(currentUser) && Objects.nonNull(currentUser.getUsername()) && !currentUser.getUsername().isEmpty()) {

				final UsuarioSession sessionCache = this.tokenRepository.get(reqHeader.split("\\s")[1]);

				logger.info(sessionCache.getUsername()+" - "+sessionCache.getToken());

				if(Objects.nonNull(sessionCache)) {

					UserDetails userDetails = this.usuarioService.loadUserByUsername(currentUser.getUsername());

					if (tokenComponent.validateToken(currentUser.getToken(), userDetails.getUsername())) {

						UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(
								userDetails,null, userDetails.getAuthorities()
						);

						userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
					}

				}

			}

		} catch (Exception e) {
			logger.error("Cannot set user authentication: "+ e.getMessage());
		}

		chain.doFilter(request, response);
	}


}