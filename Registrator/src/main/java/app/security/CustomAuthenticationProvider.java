package app.security;

import static java.util.Collections.emptyList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import app.entities.Admin;
import app.entities.User;
import app.repository.AdminRepository;
import app.repository.UserRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private PasswordEncoder encoder;
	private UserRepository userRepo;
	private AdminRepository adminRepo; 

	@Autowired
	public CustomAuthenticationProvider(UserRepository userRepo, AdminRepository adminRepo) {
		super();
		this.userRepo = userRepo;
		this.adminRepo = adminRepo;
	}

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		String email = auth.getName();
		String password = auth.getCredentials().toString();

		User user = userRepo.findByEmail(email);
		
		Admin admin = adminRepo.findByUsername(email);

		if (user == null && admin == null) {
			throw new BadCredentialsException("Authentication failed");
		}
		
		// proveri sifru
		if (user != null && encoder.matches(password, user.getPassword())) {
			return new UsernamePasswordAuthenticationToken(email, password, emptyList());
		}
		
		if (admin != null && password.equals(admin.getPassword()) ) {
	     	return new UsernamePasswordAuthenticationToken(email, password, emptyList());
		}

		throw new BadCredentialsException("Authentication failed");
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}

	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}
}
