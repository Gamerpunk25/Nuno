package Version_Norman;

public class Karte {

	private String wert;
	private String farbe;
	
	public Karte(String wert, String farbe) {
		this.wert = wert;
		this.farbe = farbe;
	}
	
	public String getWert() {
		return this.wert;
	}
	
	public void setWert(String wert) {
		this.wert = wert;
	}
	
	public String getFarbe() {
		return this.farbe;
	}
	
	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}
}
