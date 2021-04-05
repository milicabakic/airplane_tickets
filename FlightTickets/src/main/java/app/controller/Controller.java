package app.controller;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.entities.Karta;
import app.forms.KartaForm;
import app.repository.KartaRepository;
import app.utils.UtilsMethods;

@RestController
@RequestMapping("")
public class Controller {

	private KartaRepository kartaRepo;

	@Autowired
	public Controller(KartaRepository kartaRepo) {
		this.kartaRepo = kartaRepo;
	}

	@PostMapping("/buyTicket")
	public ResponseEntity<String> buyTicket(@RequestBody KartaForm kartaForm) {

		DecimalFormat format = new DecimalFormat("0.00");

		try {
			int kapacitetAviona = UtilsMethods
					.sendGetResInteger("http://localhost:8082/findKapacitet/" + kartaForm.getIdLeta()).getBody();
			int prodateKarte = kartaRepo.countTicketsByIdLeta(kartaForm.getIdLeta());

			if (prodateKarte < kapacitetAviona) {

				String response;
				String rank = UtilsMethods
						.sendGetResString("http://localhost:8081/whatRankAmI/" + kartaForm.getIdKorisnika()).getBody();

				int cenaLeta = UtilsMethods.sendGetResInteger("http://localhost:8082/findCena/" + kartaForm.getIdLeta())
						.getBody();

				if (rank.equals("ZLATNI"))
					response = "Uspešna kupovina! Ostvarili ste popust od 20%, nova cena: "
							+ format.format(cenaLeta * 0.8) + " €.";
				else if (rank.equals("SREBRNI"))
					response = "Uspešna kupovina! Ostvarili ste popust od 10%, nova cena: "
							+ format.format(cenaLeta * 0.9) + " €.";
				else
					response = "Uspešna kupovina! Cena karte je: " + cenaLeta + " €.";

				Karta karta = new Karta(kartaForm.getIdKorisnika(), kartaForm.getIdLeta(),
						kartaForm.getDatumKupovine());
				kartaRepo.saveAndFlush(karta);

				long milje = UtilsMethods.sendGetResLong("http://localhost:8082/findMiles/" + kartaForm.getIdLeta())
						.getBody();

				UtilsMethods.sendGetResString(
						"http://localhost:8081/updateMiles/" + kartaForm.getIdKorisnika() + "/" + milje);

				return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
			} else
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/ticketsByIdLeta/{idLeta}")
	public ResponseEntity<List<Long>> getTicketsByIdLeta(@PathVariable long idLeta) {

		try {

			return new ResponseEntity<>(kartaRepo.findUsersByIdLeta(idLeta), HttpStatus.ACCEPTED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/ticketsByIdKorisnika/{idKorisnika}")
	public ResponseEntity<List<Karta>> getTicketsByIdKorisnika(@PathVariable long idKorisnika) {

		try {

			return new ResponseEntity<>(kartaRepo.findByIdKorisnikaSorted(idKorisnika), HttpStatus.ACCEPTED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/countTicketsByIdLeta/{idLeta}")
	public ResponseEntity<Integer> countTicketsByIdLeta(@PathVariable long idLeta) {

		try {

			return new ResponseEntity<>(kartaRepo.countTicketsByIdLeta(idLeta), HttpStatus.ACCEPTED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

}
