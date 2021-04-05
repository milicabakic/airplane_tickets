package app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import app.entities.Karta;
import app.repository.KartaRepository;


@Component
public class Consumer {
	
	@Autowired
	KartaRepository kartaRepo;
	
	@JmsListener(destination = "ticket.queue")
	public void consume(String id) {
		
		long idLeta = Long.parseLong(id);
		
		for (Karta karta : kartaRepo.findByIdLeta(idLeta)) {
			karta.setStanje("canceled");
			kartaRepo.saveAndFlush(karta);
		}
		//System.out.println("Sve karte za idLeta: " + idLeta + " su otkazane!");
		
	}

}
