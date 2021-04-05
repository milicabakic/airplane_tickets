package app.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import app.entities.User;
import app.repository.UserRepository;

public class UtilsThreadSafe {

	//synchronized metoda nije kreacija novog threada
	//vec kontrolisani pristup metodi iz vise threadova
	
	public static synchronized void userChangde(UserRepository repo, BCryptPasswordEncoder encoder, long id, 
			String ime, String prezime, String email, String password, String brojPasosa) {
		
		User user = repo.findById(id);
		
		user.setIme(ime);
		user.setPrezime(prezime);
		user.setEmail(email);
		user.setPassword(encoder.encode(password));
		user.setBrPasosa(brojPasosa);

		repo.saveAndFlush(user);
	}
	
	public static synchronized void updateMilesAdd(UserRepository repo, long id, long miles) {
		User user = repo.findById(id);
		
		long currMiles = user.getBrojMilja();		
	    user.setBrojMilja(currMiles + miles);				
		
		updateRankForUser(user);
		repo.saveAndFlush(user);
	}
	
	private static synchronized void updateRankForUser(User u) {
		long milje = u.getBrojMilja();
		if(milje > 10000) 
			u.setRank("ZLATNI");
		else if(milje > 1000) 
			u.setRank("SREBRNI");
		else 
			u.setRank("BRONZANI");	
	}
}
