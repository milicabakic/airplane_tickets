package app.entities;

import java.util.ArrayList;
import java.util.List;

public class User {

	private long id;

	private String ime;
	private String prezime;
	private String email;
	private String password;
	private String brPasosa;
	
	private List<KreditnaKartica> kartice;
	
	private String rank;
	private long brojMilja;
	

	public User() {

	}

	public User(String ime, String prezime, String email, String password, String brPasosa) {
		this.ime = ime;
		this.prezime = prezime;
		this.email = email;
		this.password = password;
		this.brPasosa = brPasosa;
		this.kartice = new ArrayList<KreditnaKartica>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBrPasosa() {
		return brPasosa;
	}
	
	public void setBrPasosa(String brPasosa) {
		this.brPasosa = brPasosa;
	}

	public List<KreditnaKartica> getKartice() {
		return kartice;
	}

	public void setKartice(List<KreditnaKartica> kartice) {
		this.kartice = kartice;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public long getBrojMilja() {
		return brojMilja;
	}

	public void setBrojMilja(long brojMilja) {
		this.brojMilja = brojMilja;
	}
	
	@Override
	public String toString() {
		return this.getIme() + " " + this.getPrezime();
	}
	
}
