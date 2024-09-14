package py.gov.mitic.adminpy.model.response;

import org.springframework.security.core.GrantedAuthority;
import py.gov.mitic.adminpy.model.entity.Usuario;

import java.io.Serializable;
import java.util.Collection;

public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;

	private String accessToken;
	
	private Usuario usuario;

	private Collection<? extends GrantedAuthority> permisos;

	private String refreshToken;

	public AuthenticationResponse(String token) {
		this.accessToken = token;
	}

	public AuthenticationResponse(String accessToken, Usuario usuario, Collection<? extends GrantedAuthority> collection, String refreshToken) {
		this.accessToken = accessToken;
		this.usuario = usuario;
		this.permisos = collection;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Collection<? extends GrantedAuthority> getPermisos() {
		return permisos;
	}

	public void setPermisos(Collection<? extends GrantedAuthority> permisos) {
		this.permisos = permisos;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}