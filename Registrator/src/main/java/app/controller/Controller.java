package app.controller;

import static app.security.SecurityConstants.HEADER_STRING;
import static app.security.SecurityConstants.SECRET;
import static app.security.SecurityConstants.TOKEN_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import app.email.EmailType;
import app.email.SendEmail;
import app.entities.KreditnaKartica;
import app.entities.User;
import app.forms.KreditnaKarticaForm;
import app.forms.RegistrationForm;
import app.repository.KreditnaKarticaRepository;
import app.repository.UserRepository;
import app.utils.UtilsThreadSafe;

@RestController
@RequestMapping("")
public class Controller {

	private BCryptPasswordEncoder encoder;
	private UserRepository userRepo;
	private KreditnaKarticaRepository karticaRepo;

	@Autowired
	public Controller(BCryptPasswordEncoder encoder, UserRepository userRepo, KreditnaKarticaRepository cardRepo) {
		this.encoder = encoder;
		this.userRepo = userRepo;
		this.karticaRepo = cardRepo;
	}

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody RegistrationForm registrationForm) {

		try {

			User user = new User(registrationForm.getIme(), registrationForm.getPrezime(), registrationForm.getEmail(),
					encoder.encode(registrationForm.getPassword()), registrationForm.getBrPasosa());

			user.setBrojMilja(0);
			user.setRank("BRONZANI");
			
			userRepo.saveAndFlush(user);

			SendEmail.sendEmail(EmailType.REGISTRACIJA);
			
			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PutMapping("/updateUser/{id}")
	public ResponseEntity<User> updateUser(@RequestBody RegistrationForm registrationForm, @PathVariable long id) {

		try {

			User user = userRepo.findById(id);
			
			String email = user.getEmail();
			
			UtilsThreadSafe.userChangde(userRepo, encoder, id, registrationForm.getIme(), registrationForm.getPrezime(), 
					registrationForm.getEmail(), registrationForm.getPassword(), registrationForm.getBrPasosa());
	/*		
			user.setIme(registrationForm.getIme());
			user.setPrezime(registrationForm.getPrezime());
			user.setEmail(registrationForm.getEmail());
			user.setPassword(encoder.encode(registrationForm.getPassword()));
			user.setBrPasosa(registrationForm.getBrPasosa());

			userRepo.saveAndFlush(user);
*/
			if(!(registrationForm.getEmail().equals(email))) {
				SendEmail.sendEmail(EmailType.PROMENA_MEJLA);
				System.out.println("Potvrda nove e-mail adrese.");
			}
			
			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@GetMapping("/updateMiles/{id}/{miles}")
	public ResponseEntity<String> updateMilesPlus(@PathVariable long id, @PathVariable long miles) {
		try {

			UtilsThreadSafe.updateMilesAdd(userRepo, id, miles);
/*			User user = userRepo.findById(id);
			long currMiles = user.getBrojMilja();
			
		    user.setBrojMilja(currMiles + miles);				
			
			updateRankForUser(user);
			userRepo.saveAndFlush(user);
*/			
			return new ResponseEntity<>("success", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/whatRankAmI/{id}")
	public ResponseEntity<String> getRankForUser(@PathVariable long id) {
		try {

			User user = userRepo.findById(id);

			return new ResponseEntity<>(user.getRank(), HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<User> getUser(@PathVariable long id) {
		try {
			User user = userRepo.findById(id);
			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/creditCard/{id}")
	public ResponseEntity<User> addCreditCard(@RequestBody KreditnaKarticaForm karticaForm, @PathVariable long id) {

		try {

			User user = userRepo.findById(id);

			KreditnaKartica kartica = new KreditnaKartica(user.getIme(), user.getPrezime(),
					karticaForm.getBrojKartice(), karticaForm.getPin());

			karticaRepo.saveAndFlush(kartica);

			user.getKartice().add(kartica);
			userRepo.saveAndFlush(user);

			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/whoAmI")
	public ResponseEntity<User> whoAmI(@RequestHeader(value = HEADER_STRING) String token) {
		try {

			// izvlacimo iz tokena subject koji je postavljen da bude email
			String email = JWT.require(Algorithm.HMAC512(SECRET.getBytes())).build()
					.verify(token.replace(TOKEN_PREFIX, "")).getSubject();

			User user = userRepo.findByEmail(email);

			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	private void updateRankForUser(User u) {
		long milje = u.getBrojMilja();
		if(milje > 10000) 
			u.setRank("ZLATNI");
		else if(milje > 1000) 
			u.setRank("SREBRNI");
		else 
			u.setRank("BRONZANI");
				
	}

}
