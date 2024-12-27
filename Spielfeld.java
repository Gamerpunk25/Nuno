package Version_Norman;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Spielfeld {
	
	private ArrayList<Karte> ablage; //Ablagestapel für Karten
	private ArrayList<Karte> deck; //Hier werden Karten gezogen
	private ArrayList<Spieler> runde; //Spieler mit Karten werden gespeichert
	private ArrayList<Spieler> sieger; //Spieler, die ihre Karten abgelegt haben, werden hier gespeichert
//Zähler, der den zugberechtigten Spieler markiert
	private int zug = 0;
//Gibt die Richtung der Runde an. Wird geändert, wenn ein Spieler eine reverse-Karte spielt. Funktion nur ab 3 Spielern wirksam
	private boolean aufsteigend = true;
	private int anzahlSpieler = 2;
	private int cardNumber = 5;
	private String eingabe;
//Hier wird die ausgewählte Karte des Spielers gespeichert
	private Karte auswahl;
//Hier wird die Anzahl an Karten gespeichert, die der nächste Spieler ziehen muss. Funktion, dass +2 Karten gestapelt werden können, wird hierdurch möglich.
	private int ziehen = 0;
	
	
	public Spielfeld(Spieler host) {
		this.runde = new ArrayList<Spieler>();
		this.sieger = new ArrayList<Spieler>();
		this.ablage = new ArrayList<Karte>();
		this.deck = new ArrayList<Karte>();
		this.runde.add(host);
	}
	
	private void verteileKarten() {
		//Sublist versuchen zum Karten austeilen
//Schleife läuft solange, wie es Spieler am Spielfeld gibt
		for(int i = 0; i < this.runde.size(); i++) {
//Die ersten cardNumber Karten werden pro Durchlauf an den Spieler ausgeteilt
			this.runde.get(i).setHand(this.deck.subList(0, (this.cardNumber)));
//Entfernt die ersten cardNumber Karten aus dem Deck, die nun dem Spieler gehören
			this.deck.subList(0, (cardNumber)).clear();
		}
//Ablage bekommt die oberste Karte des Decks nach dem die Spieler ihre Karten erhalten haben
		this.ablage.add(this.deck.get(0));
		this.deck.remove(0);
	}
	
	private void mischen() {
		
	}
	
//Methode: neues Deck für UNO erstellen
	private void createDeck() {
		for (int i = 0; i < 5; i++) {
			switch(i) {
			case 0:
				for(int j = 0; j < 13; j++) {
					switch(j) {
					case 10:
//Karten-Objekt an einer zufälligen Stelle im Deck erstellt
						this.deck.add((int) (Math.random() * this.deck.size()), new Karte("reverse","Rot"));
						break;
					case 11:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("draw2","Rot"));
						break;
					case 12:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("block","Rot"));
						break;
					default:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte((j)+"","Rot"));
					}
				}
				break;
			case 1:
				for(int j = 0; j < 13; j++) {
					switch(j) {
					case 10:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("reverse","Gelb"));
						break;
					case 11:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("draw2","Gelb"));
						break;
					case 12:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("block","Gelb"));
						break;
					default:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte((j)+"","Gelb"));
					}
				}
				break;
			case 2:
				for(int j = 0; j < 13; j++) {
					switch(j) {
					case 10:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("reverse","Blau"));
						break;
					case 11:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("draw2","Blau"));
						break;
					case 12:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("block","Blau"));
						break;
					default:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte((j)+"","Blau"));
					}
				}
				break;
			case 3:
				for(int j = 0; j < 13; j++) {
					switch(j) {
					case 10:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("reverse","Grün"));
						break;
					case 11:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("draw2","Grün"));
						break;
					case 12:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte("block","Grün"));
						break;
					default:
						this.deck.add((int) (Math.random() * this.deck.size()),new Karte((j)+"","Grün"));
					}
				}
				break;
			case 4:
				for(int j = 0; j < 4; j++) {
					this.deck.add((int) (Math.random() * this.deck.size()),new Karte("wild",""));
				}
				break;
			}
		}
	}
	
	public void gameLoop() {
//Spielablau: Deck wird erstellt und Karten werden auf Spieler und Ablagestapel aufgeteilt
		this.createDeck();
		this.verteileKarten();
//Notwendig, um die Eingabe des Spielers String umzuwandeln, damit mit der int-Variante auf Kartenobjekte zugegriffen werden kann
		int index;
//Hier wird das Spielerobjekt, dass aktuell an der Reihe ist, gespeichert für Zugriffe.
		Spieler spieler;
		System.out.println("Karten im Deck");
		for(int i = 0; i < this.deck.size(); i++) {
			System.out.println(this.deck.get(i).getFarbe()+":"+this.deck.get(i).getWert());			
		}
		System.out.println("Karte im Ablagestapel");
		System.out.println(this.ablage.get(0).getFarbe()+":"+this.ablage.get(0).getWert());
		System.out.println("Karten der Spieler");
		for(int j = 0; j < this.runde.size(); j++) {
			this.runde.get(j).kartenAnzeigen();
		}
//Solange zumindest 2 Spieler am Tisch sind, geht der normale Spielverlauf weiter.
//Das Spiel ist beendet, wenn nur noch ein Spieler Karten auf der Hand hat.
		while(this.runde.size() > 1) {
			spieler = this.runde.get(this.zug);
			if(this.ziehen != 0) {
				for(;this.ziehen > 0; this.ziehen--) {
					spieler.drawKarte(this.deck.get(0));
					this.deck.remove(0);
				}
			}
			spieler.getOutput().println("+SEND");
			spieler.getOutput().println("Bitte wählen Sie nun eine Karte aus");
			try {
//Der Spieler am Zug muss solange eine Eingabe tätigen, bis eine Eingabe als gültig angesehen wird
//Dabei muss die Karte entweder auf die Ablage gehen dürfen oder der Spieler passt und zieht eine Karte
				while((this.eingabe = spieler.getInput().readLine()) != null) {
//Die Eingabe muss aus Ziffern bestehen, die mindestens 1-2 auftreten dürfen. Buchstaben und Sonderzeichen sind nicht erwünscht. Minuszahlen nicht möglich
					try {
						index = Integer.parseInt(eingabe);
//Eingabe muss eine positive Zahl sein und darf die Größe der Hand +1 nicht überschreiten.
//Die Zahl Größe der Hand +1 soll die Passenfunktion auslösen.
						if(index > 0) {
							if(index < (spieler.getHand().size()+1)) {
								if(index == spieler.getHand().size()+1) {
									spieler.getOutput().println("Passen bestätigt. Karte wird gezogen");
//Nur wenn die Eingabe im Wertebereich ist und nicht gepasst werden soll, kann eine Karte ausgewählt werden für die Prüfung
								} else {
									this.auswahl = spieler.getKarte(index);
//Karte wird gespielt, wenn die Farbe übereinstimmt
									if (this.auswahl.getWert().equals("wild")) {
										spieler.removeKarte(index);
										this.ablage.add(0, auswahl);
//Wildkarten haben nach dem Farbwunsch eine Farbe zugewiesen bekommen. Damit ein Farbwunsch möglich ist, muss die Farbe zurückgesetzt werden beim spielen
										this.ablage.get(0).setFarbe("");
										break;
//Karte wird gespielt, wenn der Wert übereinstimmt
									} else if (this.auswahl.getWert().equals(this.ablage.get(0).getFarbe())) {
										spieler.removeKarte(index);
										this.ablage.add(0, auswahl);
										break;
//Farbwunsch kann jederzeit gespielt werden
									} else if (this.auswahl.getFarbe().equals(this.ablage.get(0).getFarbe())) {
										spieler.removeKarte(index);
//Gespielte Karte wird oben auf die Ablage gelegt
										this.ablage.add(0, auswahl);
										break;
									} else {
										spieler.getOutput().println("Karte kann nicht gespielt werden. Wert und/oder Farbe müssen übereinstimmen oder Karte muss Farbwunsch sein");
									}
								}
							} else {
								spieler.getOutput().println("Eingegebene Zahl zu groß");
							}
						} else {
							spieler.getOutput().println("Eingegebene Zahl zu klein");
						}
					} catch (NumberFormatException e) {
						this.runde.get(this.zug).getOutput().println("Eingabe ungültig. Eingabe muss eine Zahl mit 1-2 Ziffern sein.");
					}
				} //Ende der Eingabemöglichkeit für den Spieler.
			} catch (IOException e) {
//Wenn der Spieler nicht mehr erreichbar sein sollte, weil sein Output-Stream geschlossen wurde, wird der Spieler aus der Runde entfernt
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(spieler.getNickname() +" hat die Partie verlassen");
				this.runde.remove(this.zug);
			}
			spieler.getOutput().println("-SEND");
//Falls eine Sonderkarte gespielt wurde, wird hier der Effekt der Sonderkarte anhand ihrer Art ausgelöst
			switch (this.ablage.get(0).getWert()) {
			case "wild":
//Spieler bekommt 4 Farben gestellt, von denen dieser eine auswählen darf. Diese wird der Wildkarte angehaftet.
//Sobald der Wildkarte eine Farbe angehaftet wurde, wird die Farbauswahl beendet.
				spieler.getOutput().println("+SEND");
				try {
					while((this.eingabe = spieler.getInput().readLine()) != null) {
						spieler.getOutput().println("Geben Sie nun ihre Wunschfarbe ein!");
//Fragen, ob Switch case oder Regex besser ist

						switch(this.eingabe) {
						case "Rot":
							this.ablage.get(0).setFarbe(this.eingabe);
						break;
						case "Grün":
							this.ablage.get(0).setFarbe(this.eingabe);
						break;
						case "Blau":
							this.ablage.get(0).setFarbe(this.eingabe);
						break;
						case "Gelb":
							this.ablage.get(0).setFarbe(this.eingabe);
						break;
						default:
							spieler.getOutput().println("Eingabe ungültig. Eingabe muss eine der vier erlaubten Farben sein");
						}
//Wenn die Eingabe gültig ist, nimmt die gespielte Wild-Karte die gewünschte Farbe an und beendet den Effekt. Nächste Runde beginnt
						if(!this.ablage.get(0).getFarbe().equals("")) {
							break;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				spieler.getOutput().println("-SEND");
				break;
			case "draw2":
				this.ziehen = this.ziehen + 2;
				break;
			case "reverse":
				if(this.aufsteigend) {
					this.aufsteigend = false;
				} else {
					this.aufsteigend = true;
				}
				break;
			case "block":
				if(this.aufsteigend) {
					this.zug++;
				} else {
					this.zug--;
				}
				break;
			}

//Zug wird geändert, anhand der Richtung
			if(this.aufsteigend) {
				this.zug++;
			} else {
				this.zug--;
			}
			if(this.zug > this.runde.size()) {
//Wenn Spieler auf index 3 bei einer Lobbygröße von 4 weitergibt, muss der nächste spieler auf index 0 sein. zug beträgt hier 4
//Wenn Spieler auf index 3 bei einer Lobbygröße von 4 block verwendet. darf nur der spieler auf index 1 spielen. Zug beträgt hier 5. Somit 4-5
				this.zug = this.runde.size() - this.zug;
			}
			if(this.zug < 0) {
//Wenn Spieler auf index 0 bei einer Lobbygröße von 4 weitergibt, muss der nächste index 3 sein. zug beträgt hier -1, also 4 -1
//Wenn Spieler auf index 0 block verwendet bei einer Lobbygröße von 4, darf nur spieler auf index 2 spielen. zug beträgt -2, also 4 -2 
				this.zug = this.runde.size() + this.zug;
			}
			//Wenn der Spieler seine letzte Karte abgelegt hat, wird der Spieler in die Siegerliste aufgenommen und aus der Runde entfernt
			if(spieler.getHand().size() == 0) {
				this.sieger.add(spieler);
				this.runde.remove(spieler);
			}
		}

//Vom Host beginnt spielt jeder Spieler eine Karte. Passen funktioniert nur, wenn keine Karte möglich ist
//Nur wenn Karten durch Passen oder draw2 gezogen wurden, wird geprüft, ob das Deck leer ist
//Wenn Deck leer ist, werden alle Karten bis auf die Oberste des Droppiles in das Deck gemischt.
//Bei jedem Zug wird geprüft, ob ein Spieler seine Karten los ist. Dabei wird der Spieler in die Winnerliste aufgenommen
	}
	
/*  
  Dient dem Zeigen der Karten im Deck
  
	
 */
	
	public void sucheSpieler(int portnummer) {
		try {
//Gastserver auf dem übergebenen Port wird gestartet
//Baut Verbindungen solange auf, bis die eingestellte Lobby voll ist. Danach wird der Gastserver geschlossen
			ServerSocket gastserver = new ServerSocket(portnummer);
			System.out.println("Ein Gastserver auf dem Port "+portnummer+" wurde geöffnet für Gäste von "+this.runde.get(0).getNickname());
			while(this.runde.size() != this.anzahlSpieler) {
				System.out.println("Suche nach Spielern läuft");
				Spieler gast = new Spieler(gastserver.accept());
				System.out.println("Spieler verbunden");
				gast.getOutput().println("+SEND");
				gast.getOutput().println("Bitte geben Sie nun ihren Nickname für diese Partie ein");
//Falls der Spieler nicht mehr erreichbar sein sollte, soll die Suche nach Spielern weitergehen
				while ((eingabe = gast.getInput().readLine()) != null) {
					gast.changeNick(eingabe);
					if(!gast.getNickname().equals("")) {
						break;
					}
				}
//Schutzmaßnnahme, falls die Verbindung zu dem Mitspieler abbrechen sollte
				if(gast.getNickname().equals("")) {
					continue;
				}
//Senderechte für den neuen Mitspieler wird entzogen
				gast.getOutput().println("-SEND");
			this.runde.add(gast);
			System.out.println(gast.getSocket().getInetAddress().getHostAddress() + ":"+ gast.getSocket().getPort()+ " mit dem Nickname "+gast.getNickname()+" ist der Runde von "+ this.runde.get(0).getNickname() + " beigetreten");
			}
			gastserver.close();
			
//Verbindungsaufruf wurde abgeschlossen. Spiel startet nun.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(portnummer+" ist bereits vergeben");
		}
	}
	
	public void setAnzahlSpieler(int anzahl) {
		this.anzahlSpieler = anzahl;
	}
	
	public int getAnzahlSpieler() {
		return this.anzahlSpieler;
	}
	
	public void setAnzahlKarten(int anzahl) {
		this.cardNumber = anzahl;
	}
	
	public int getAnzahlKarten() {
		return this.cardNumber;
	}
	
	public void clearLobby() {
		for(int i = 1; i < this.runde.size(); i++) {
			try {
				this.runde.get(i).closeSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.runde.clear();
	}
}
