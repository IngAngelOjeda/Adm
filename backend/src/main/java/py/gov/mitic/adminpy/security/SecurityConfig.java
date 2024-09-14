package py.gov.mitic.adminpy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import py.gov.mitic.adminpy.exceptions.JwtAuthenticationEntryPoint;

/**
 * EnableWebSecurity: seguridad web
 * EnableGlobalMethodSecurity: permisos para especificar la seguridad a nivel de metodos
 * */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	UserDetailsService userDetailService;

	@Autowired
    SecurityFilter securityFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean(name="authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable();

		/**
		 * configuracion de seguridad de rutas
		 * */
		httpSecurity
				.authorizeRequests()
				.antMatchers("/api/usuario/**").hasAuthority("usuarios:ver")
				.antMatchers("/api/oee/**").hasAuthority("oee:ver")
				.antMatchers("/api/rol/**").hasAuthority("roles:ver")
				.antMatchers("/api/permiso/**").hasAuthority("permisos:ver")
				.antMatchers("/api/sistema/**").hasAuthority("sistemas:ver")
				.antMatchers("/api/dominio/**").hasAuthority("dominio:ver")
				.antMatchers("/api/rangoip/**").hasAuthority("rangoip:ver")
				.antMatchers("/api/servicio/**").hasAuthority("servicioOee:ver")
                .antMatchers("/api/plan/**").hasAuthority("planes:ver")
				.anyRequest().authenticated().and();

		/**
		 * manejo de mensajes de error fuera del controlador de errores de spring rest
		 * */
		httpSecurity
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint).and();

		httpSecurity
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		/***
		 *  Add a filter to validate the tokens with every request
		 * */
		httpSecurity.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) {
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setPermissionEvaluator(new PermissionChecker());
		web.ignoring()
		.antMatchers(new String[]{
			"/auth/login",
			"/auth/refreshToken",
			"/auth/logout",
			"/auth/reset-pass",
			"/recuperar_clave/**"
		})
		.mvcMatchers(HttpMethod.OPTIONS, "/**")
		.mvcMatchers(
			"/swagger-ui.html/**",
			"/configuration/**",
			"/swagger-resources/**", 
			"/v2/api-docs",
			"/webjars/**"
		);
		web.expressionHandler(handler);
	}

}