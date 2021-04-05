package app.entities;

import java.time.LocalDate;



public class Karta {
	
	
	private long id;
	
	private long idKorisnika;
	private long idLeta;
	private LocalDate datumKupovine;
	private String stanje;
	
	public Karta() {
		
	}
	
	

	public Karta(long idKorisnika, long idLeta, LocalDate datumKupovine) {
		this.idKorisnika = idKorisnika;
		this.idLeta = idLeta;
		this.datumKupovine = datumKupovine;
		this.stanje = "active";
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getStanje() {
		return stanje;
	}
	public void setStanje(String stanje) {
		this.stanje = stanje;
	}

}
