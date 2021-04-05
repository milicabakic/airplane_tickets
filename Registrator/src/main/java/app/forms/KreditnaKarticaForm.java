package app.forms;

public class KreditnaKarticaForm {


	private String brojKartice;
	private String pin;
	
	public KreditnaKarticaForm() {
		
	}

	public KreditnaKarticaForm(String brojKartice, String pin) {
		super();
		this.brojKartice = brojKartice;
		this.pin = pin;
	}


	public String getBrojKartice() {
		return brojKartice;
	}
	
	public void setBrojKartice(String brojKartice) {
		this.brojKartice = brojKartice;
	}
	
	public String getPin() {
		return pin;
	}
	
	public void setPin(String pin) {
		this.pin = pin;
	}
	
}
