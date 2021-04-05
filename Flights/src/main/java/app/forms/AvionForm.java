package app.forms;

public class AvionForm {
	
	private String naziv;
	private int kapacitetPutnika;
	
	public AvionForm() {
		
	}

	public AvionForm(String naziv, int kapacitetPutnika) {
		super();
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

}
