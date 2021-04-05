package app.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Avion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String naziv;
	private int kapacitetPutnika;
	
	public Avion() {

	}

	public Avion(String naziv, int kapacitetPutnika) {
		this.naziv = naziv;
		this.kapacitetPutnika = kapacitetPutnika;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getKapacitetPutnika() {
		return kapacitetPutnika;
	}

	public void setKapacitetPutnika(int kapacitetPutnika) {
		this.kapacitetPutnika = kapacitetPutnika;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.getNaziv() + ", kapacitet: " + this.getKapacitetPutnika();
	}
	
}
