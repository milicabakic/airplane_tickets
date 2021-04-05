package app.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.forms.Login_Form;

import static app.security.SecurityConstants.*;

/**
 * Sluzi da da JSON Web Token user-u koji pokusava da pristupi (user salje
 * username i password).
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {

			Login_Form user = new ObjectMapper().readValue(req.getInputStream(), Login_Form.class);

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
					user.getPassword(), Collections.emptyList());

			return authenticationManager.authenticate(token);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) {

		String email = auth.getName();

		String token = JWT.create().withSubject(email)
				.withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
				.sign(HMAC512(SECRET.getBytes()));

		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}
}