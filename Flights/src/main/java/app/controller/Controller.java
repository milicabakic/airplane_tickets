package app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipios.springsearch.anotation.SearchSpec;

import app.entities.Avion;
import app.entities.Let;
import app.forms.AvionForm;
import app.forms.LetForm;
import app.repository.AvionRepository;
import app.repository.LetRepository;
import app.utils.UtilsMethods;

@RestController
@RequestMapping("")
public class Controller {

	private AvionRepository avionRepo;
	private LetRepository letRepo;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	Queue userQueue;

	@Autowired
	Queue ticketQueue;

	@Autowired
	public Controller(AvionRepository avionRepo, LetRepository letRepo) {
		this.avionRepo = avionRepo;
		this.letRepo = letRepo;
	}

	@GetMapping("/flights")
	public ResponseEntity<List<Let>> searchForFlights(@SearchSpec Specification<Let> specs) {
		try {
			List<Let> flights = letRepo.findAll(Specification.where(specs));
			List<Let> toReturn = new ArrayList<Let>();
			for (Let let : flights) {
				int kapacitetAviona = let.getAvion().getKapacitetPutnika();
				int brojLjudi = UtilsMethods
						.sendGetResInteger("http://localhost:8083/countTicketsByIdLeta/" + let.getId()).getBody();
				if (kapacitetAviona > brojLjudi && let.getStanje().equals("active"))
					toReturn.add(let);
			}
			return new ResponseEntity<>(toReturn, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/allflights")
	public ResponseEntity<List<Let>> searchForAllFlights(@SearchSpec Specification<Let> specs) {
		try {
			List<Let> flights = letRepo.findAll(Specification.where(specs));
			return new ResponseEntity<>(flights, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/planes")
	public ResponseEntity<List<Avion>> searchForPlanes() {
		try {
			List<Avion> planes = avionRepo.findAll();
			return new ResponseEntity<>(planes, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}

	@PostMapping("/addPlane")
	public ResponseEntity<List<Avion>> addPlane(@RequestBody AvionForm avionForm) {

		try {

			Avion avion = new Avion(avionForm.getNaziv(), avionForm.getKapacitetPutnika());
			avionRepo.saveAndFlush(avion);
			
			List<Avion> avioni = avionRepo.findAll();

			return new ResponseEntity<>(avioni, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping("/deletePlane/{id}")
	public ResponseEntity<List<Avion>> deletePlane(@PathVariable long id) {

		try {
			Avion avion = avionRepo.findById(id);
			
			if (avion != null && letRepo.findByAvion(avion).size() == 0) {
				avionRepo.delete(avion);
				List<Avion> planes = avionRepo.findAll();
				return new ResponseEntity<>(planes, HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/addFlight")
	public ResponseEntity<List<Let>> addFlight(@RequestBody LetForm letForm) {

		try {
			Avion avion = avionRepo.findById(letForm.getAvion().getId());
			if (avion == null) {
				avion = avionRepo.saveAndFlush(letForm.getAvion());
			}
			Let let = new Let(avion, letForm.getPocetnaDestinacija(), letForm.getKrajnjaDestinacija(),
					letForm.getDuzinaLeta(), letForm.getCena());

			letRepo.saveAndFlush(let);
			
			List<Let> letovi = letRepo.findAll();

			return new ResponseEntity<>(letovi, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping("/deleteFlight/{id}")
	public ResponseEntity<List<Let>> deleteFlight(@PathVariable long id) {

		try {
			ResponseEntity<List<Long>> response = UtilsMethods.sendGet("http://localhost:8083/ticketsByIdLeta/" + id);

			Let let = letRepo.findById(id);
			long milje = let.getDuzinaLeta();

			if (response.getBody().size() > 0) {

				for (long idUsera : response.getBody())
					jmsTemplate.convertAndSend(userQueue, idUsera + "," + milje);

			}
			// otkazivanje karata za prosledjeni id leta
			jmsTemplate.convertAndSend(ticketQueue, id + "");

			let.setStanje("canceled");
			letRepo.saveAndFlush(let);
			
			List<Let> letovi = letRepo.findAll();

			return new ResponseEntity<>(letovi, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/findCena/{id}")
	public ResponseEntity<Integer> searchForFlights(@PathVariable long id) {

		try {
			Let let = letRepo.findById(id);

			return new ResponseEntity<>(let.getCena(), HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/findKapacitet/{id}")
	public ResponseEntity<Integer> searchForKapacitet(@PathVariable long id) {

		try {
			Let let = letRepo.findById(id);

			return new ResponseEntity<>(let.getAvion().getKapacitetPutnika(), HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/findMiles/{id}")
	public ResponseEntity<Long> searchForMiles(@PathVariable long id) {

		try {
			Let let = letRepo.findById(id);

			return new ResponseEntity<>(let.getDuzinaLeta(), HttpStatus.ACCEPTED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
