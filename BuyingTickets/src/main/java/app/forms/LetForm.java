package app.forms;

import app.entities.Avion;

public class LetForm {
	
	private Avion avion;
	private String pocetnaDestinacija;
	private String krajnjaDestinacija;
	private long duzinaLeta;
	private int cena;
	
	public LetForm() {
		
	}

	public LetForm(Avion avion, String pocetnaDestinacija, String krajnjaDestinacija, long duzinaLeta, int cena) {
		this.avion = avion;
		this.pocetnaDestinacija = pocetnaDestinacija;
		this.krajnjaDestinacija = krajnjaDestinacija;
		this.duzinaLeta = duzinaLeta;
		this.cena = cena;
	}

	public Avion getAvion() {
		return avion;
	}

	public void setAvion(Avion avion) {
		this.avion = avion;
	}

	public String getPocetnaDestinacija() {
		return pocetnaDestinacija;
	}

	public void setPocetnaDestinacija(String pocetnaDestinacija) {
		this.pocetnaDestinacija = pocetnaDestinacija;
	}

	public String getKrajnjaDestinacija() {
		return krajnjaDestinacija;
	}

	public void setKrajnjaDestinacija(String krajnjaDestinacija) {
		this.krajnjaDestinacija = krajnjaDestinacija;
	}

	public long getDuzinaLeta() {
		return duzinaLeta;
	}

	public void setDuzinaLeta(long duzinaLeta) {
		this.duzinaLeta = duzinaLeta;
	}

	public int getCena() {
		return cena;
	}

	public void setCena(int cena) {
		this.cena = cena;
	}
	
	
	

}
