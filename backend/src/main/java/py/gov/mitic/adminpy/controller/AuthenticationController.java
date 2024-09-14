package py.gov.mitic.adminpy.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import py.gov.mitic.adminpy.model.dto.*;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.model.request.TokenRefreshRequest;
import py.gov.mitic.adminpy.model.response.AuthenticationResponse;
import py.gov.mitic.adminpy.model.response.TokenRefreshResponse;
import py.gov.mitic.adminpy.repository.TokenRepository;
import py.gov.mitic.adminpy.security.TokenManager;
import py.gov.mitic.adminpy.exceptions.UnauthorizedException;
import py.gov.mitic.adminpy.model.entity.Usuario;
import py.gov.mitic.adminpy.service.UsuarioService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

	private Log logger = LogFactory.getLog(AuthenticationController.class);

	private final TokenManager tokenComponent;

	private final AuthenticationManager authManager;

	private final UsuarioService usuarioService;

	@Autowired(required=true)
	TokenRepository tokenRepository;

	public AuthenticationController(
		@Qualifier("tokenConfig") TokenManager tokenComponent,
		AuthenticationManager authManager,
		UsuarioService usuarioService
	) {
		this.tokenComponent = tokenComponent;
		this.authManager = authManager;
		this.usuarioService = usuarioService;
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authReq) throws UsernameNotFoundException, JsonProcessingException {
		try {
			/**
			 * validacion de credenciales de acceso
			 * */
			authManager.authenticate(new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword()));

			// Se valida y se inserta en auditoría
			usuarioService.loadUserByUsernameForAudit(authReq.getUsername());
			UserDetails userDetails = usuarioService.loadUserByUsername(authReq.getUsername());

			/**
			 * creacion de token de acceso y de actualizacion
			 * */
			String accessToken = tokenComponent.generateToken(userDetails);
			String refreshToken = tokenComponent.createRefreshToken(accessToken);

			/**
			 * consulta datos del usuario
			 * */
			Usuario usuario = usuarioService.getUserSession(userDetails.getUsername());


			tokenRepository.create(new UsuarioSession(usuario.getUsername(), accessToken));

			return ResponseEntity.ok(new AuthenticationResponse(
				accessToken, usuario, userDetails.getAuthorities(), refreshToken
			));

		} catch (BadCredentialsException e) {
//			e.printStackTrace();
			logger.info("Usuario y/o contraseña inválidos");
			throw new UnauthorizedException("Usuario y/o contraseña inválidos");
		}
	}

	@PostMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
		try {
			String requestAccessToken = tokenRefreshRequest.getAccessToken();
			String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

			final UsuarioSession sessionCache = this.tokenRepository.get(requestAccessToken);
			if(Objects.isNull(sessionCache)) {
				throw new UnauthorizedException("Token de actualización expirado");
			}

			/**
			 * verificamos que el token sea valido y que no haya expirado
			 * */
			String usernameFromToken = tokenComponent.getUsernameFromToken(requestRefreshToken);
			UserDetails userDetails = usuarioService.loadUserByUsername(usernameFromToken);

			/**
			 * accessToken: generamos un nuevo token de acceso
			 * refreshToken: generamos un refresh token
			 * */
			String accessToken = tokenComponent.generateToken(userDetails);
			String refreshToken = tokenComponent.createRefreshToken(requestRefreshToken);

			/**
			 * verificamos la session del usuario
			* */
			Usuario usuario = usuarioService.getUserSession(userDetails.getUsername());

			/**
			 * limpiamos la sesion actual
			 * */
			tokenRepository.delete(requestAccessToken);

			/**
			 * actualizamos la nueva session
			 * */
			tokenRepository.create(new UsuarioSession(usuario.getUsername(), accessToken));

			return ResponseEntity.ok(new TokenRefreshResponse(
				accessToken, refreshToken
			));
		} catch (BadCredentialsException e) {
			logger.info("Usuario y/o contraseña inválidos");
			throw new UnauthorizedException("Usuario y/o contraseña inválidos");
		}
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response, @RequestBody LogoutRequest req) {
		logger.info("tokenLogout: " + req.getToken());
		/*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}*/
		tokenRepository.delete(req.getToken());
	}

	@PostMapping(value = "/reset-pass", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<ResponseDTO> resetPass(@RequestBody AuthenticationRequest authDto) {
		//logger.info("username: " + authDto.getUsername());
		return usuarioService.resetPass(authDto).build();
	}

}