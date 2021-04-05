package app.forms;

import java.time.LocalDate;

public class KartaForm {
	
	private long idKorisnika;
	private long idLeta;
	private LocalDate datumKupovine;
	
	public KartaForm() {
		
	}

	public KartaForm(long idKorisnika, long idLeta, LocalDate datumKupovine) {
		super();
		this.idKorisnika = idKorisnika;
		this.idLeta = idLeta;
		this.datumKupovine = datumKupovine;
	}

	public long getIdKorisnika() {
		return idKorisnika;
	}

	public void setIdKorisnika(long idKorisnika) {
		this.idKorisnika = idKorisnika;
	}

	public long getIdLeta() {
		return idLeta;
	}

	public void setIdLeta(long idLeta) {
		this.idLeta = idLeta;
	}

	public LocalDate getDatumKupovine() {
		return datumKupovine;
	}

	public void setDatumKupovine(LocalDate datumKupovine) {
		this.datumKupovine = datumKupovine;
	}
	
	
	

}
