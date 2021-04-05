package app.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import app.email.EmailType;
import app.email.SendEmail;
import app.entities.User;
import app.repository.UserRepository;

@Component
public class Consumer {
	
	@Autowired
	UserRepository userRepo;
	
	@JmsListener(destination = "user.queue")
	public void consume(String idUsersAndMiles) {
		String s[] = idUsersAndMiles.split(",");
		long idUsera = Long.parseLong(s[0]);
		long miles = Long.parseLong(s[1]);
		
		User user = userRepo.findById(idUsera);
		long currMiles = user.getBrojMilja();
		if(currMiles - miles > 0) 
			user.setBrojMilja(currMiles - miles);				
		else 
			user.setBrojMilja(0);
		
		updateRankForUser(user);
		userRepo.saveAndFlush(user);
		
		//System.out.println("Korisniku " + user.getIme() + " " + user.getPrezime() + " su azurirane milje na: " + (currMiles - miles));
		
		SendEmail.sendEmail(EmailType.OTKAZIVANJE_LETA);
		
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
