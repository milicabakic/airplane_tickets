package app.entities;



public class Let {
	
	
	private long id;
	
	private Avion avion;
	private String pocetnaDestinacija;
	private String krajnjaDestinacija;
	private long duzinaLeta; //u miljama
	private int cena;
	private String stanje;
	
	public Let() {
		
	}

	public Let(Avion avion, String pocetnaDestinacija, String krajnjaDestinacija, long duzinaLeta, int cena) {
		this.avion = avion;
		this.pocetnaDestinacija = pocetnaDestinacija;
		this.krajnjaDestinacija = krajnjaDestinacija;
		this.duzinaLeta = duzinaLeta;
		this.cena = cena;
		this.stanje = "active";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	
	public String getStanje() {
		return stanje;
	}
	
	public void setStanje(String stanje) {
		this.stanje = stanje;
	}

}
