import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Gamehandler implements Runnable {
//Hier werden die GastServerPorts gespeichert, die gerade in Benutzung sind. 
//Ein GastServerport wird gelöscht, sobald ein Spiel abgeschlossen ist
	private static ArrayList<String> usedServerPorts = new ArrayList<String>();
	private Spieler host;
	private BufferedReader inHost;
	private PrintWriter outHost;
//Hier wird die Eingabe durch den Host gespeichert
	private String input;
	private int zahl;
//Dient zur Feststellung, ob ein Menü beendet werden soll.
	private boolean quit = false;
//Dient dem Speichern von Argumenten bei Befehlen, die diese Information benötigen	
	private String[] argumente;
	private Spielfeld spielfeld;
	
//Fremder Quellcodeteil zum extrahieren der Spielanleitung aus dem Package.
//Dient dem Suchen des Pfades der Spielanleitung vor der Ausgabe
	private URL Spielanleitung = getClass().getResource("UNO_Spielanleitung.txt");
	
//Savegame: Hand der Mitspieler mit deren Nickname als Unterscheidungskriterium
//Abgleich der verwendeten Nicknames bevor ein Spiel gestartet wird
	
//Bestenliste: Nicknames werden gespeichert und angegeben, wie oft dieser
//den 1., 2. und 3. Platz erhalten hat. Geringste Anzahl an Runden wird gezählt
//Datenbank muss dafür erstellt werden
	public Gamehandler(Socket socket) throws IOException {
		this.host = new Spieler(socket);
		this.inHost = this.host.getInput();
		this.outHost = this.host.getOutput();
//Spielfeld mit den Defaulteinstellungen wird erstellt
		this.spielfeld = new Spielfeld(this.host);
//Host bekommt für die Einstellung des Spiels Senderechte
//+SEND bewirkt, dass dem Output-Thread des Clients klar gemacht wird, dass dieser Senden darf.
//Alleine die Nutzung des UnoClient wird empfohlen, da nur dieser die Sendeberechtigung berücksichtigt.
//Verbindungen über telnet lassen unbefugte Eingaben zu, welche zu unbeabsichtigten Zügen während des Spiels führen kann
		this.outHost.println("+SEND");
	}
	
	private void printMenu() {
		outHost.println("Spiel starten: /STARTGAME\n"
				+ "Einstellungen vornehmen: /CONFIGS\n"
				+ "Spielanleitung zeigen: /SHOWMAN\n"
				+ "Spiel beenden: /QUIT\n"
				+ "Nickname ändern: /CHANGENAME <nickname>\n"
				+ "Menü-Befehle zeigen: /SHOWCOMMAND");
	}
	
	private void printConfigs() {
		outHost.println("Anzahl Spieler: /PLNUMBR <anzahl>\n"
				+ "Anzahl Spielkarten: /CDNUMBR <anzahl>\n"
				+ "Einstellungen verlassen: /EXITCON\n"
				+ "Einstellung-Befehle zeigen: /COMMAND\n"
				+ "Aktuelle Einstellungen zeigen: /SHOWCON");
	}
//Zeigt die Regeln des Spiels an. Text-Datei wird automatisch gefunden durch die Initailisierung von URL Spielanleitung
	private synchronized void printManual() {
		Scanner scan = null; 
		try {
			scan = new Scanner(new File(this.Spielanleitung.getPath()));
			while(scan.hasNextLine()) {
				this.outHost.println(scan.nextLine());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.outHost.println("Datei kann nicht geladen werden");
		}
	}
//Startet die Einstellungen. Anzahl Spielkarten zu Beginn und Anzahl Spieler anpassbar
	
	private void configGamesetting() {
		this.printConfigs();
		try {
			while((this.input = inHost.readLine()) != null) {
			System.out.println(this.inHost.skip(1000));
//Eingabe des Host wird durch Leerzeichen getrennt. Funktionstüchtig
//Mögliche Anpassung: Prüfen, ob Anzahl Argumente passt vor dem Prüfen des Befehls
				argumente = input.split(" ",0);
				switch(argumente[0]) {
				case "/CDNUMBR":
					try {
						zahl = Integer.parseInt(argumente[1]);
					} catch (NumberFormatException e) {
						this.outHost.println("Parameter nach Befehl muss eine Zahl sein");
					}
					if(zahl > 4 && zahl < 11) {
						this.spielfeld.setAnzahlKarten(zahl);
						this.outHost.println("Bestätigt. Karten zu Beginn beträgt nun: "+this.spielfeld.getAnzahlKarten());
					} else {
						this.outHost.println("Jeder Spieler muss zu Beginn 5 Karten aber darf maximal 10 Karten erhalten");
					}
					break;
				case "/PLNUMBR":
					try {
						zahl = Integer.parseInt(argumente[1]);
					} catch (NumberFormatException e) {
						this.outHost.println("Parameter nach Befehl muss eine Zahl sein");
					}
					if(zahl > 1 && zahl < 7) {
						this.spielfeld.setAnzahlSpieler(zahl);
						this.outHost.println("Bestätigt. Spieleranzahl beträgt nun: "+this.spielfeld.getAnzahlSpieler());
					} else {
						this.outHost.println("Es müssen 2 Spieler und es dürfen maximal 6 Spieler teilnehmen");
					}
					break;
				case "/EXITCON":
					this.outHost.println("Einstellungen werden beendet. Lade Menü");
					this.quit = true;
					break;
				case "/COMMAND":
					this.printConfigs();
					break;
				case "/SHOWCON":
					this.outHost.println("Spieleranzahl: "+this.spielfeld.getAnzahlSpieler()+"\n"
							+ "Spielkarten zu Beginn: "+this.spielfeld.getAnzahlKarten());
					break;
				default:
					this.outHost.println("-ERR: Unbekannter Befehl "+input);
				}
				if(this.quit == true) {
					break;
				}
			}
//Dient zum Schutz, dass das Menü unbeasichtigt beendet wird.
			this.quit = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//Methode: Ermitteln des Ports für den Gastserver durch Zufallszahl.
//Ermittlung von Zufallszahlen, bis ein freier Port gefunden wurde
//Funktionstüchtig
	private int getPortNumber() {
		int random;
		do {
			random = (int) (Math.random() * (65535 - 49152) + 49152);
		} while (Gamehandler.usedServerPorts.contains(Integer.toString(random)));
		Gamehandler.usedServerPorts.add(Integer.toString(random));
		return random;
	}
	
//Möglichkeit: Auslagern in Spieler
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Angabe der verbundenen Hosts
		System.out.println("Neuer Host verbunden von " + this.host.getHandle());
		outHost.println("Willkommen auf dem UNOserver. Bitte legen Sie einen Nickname mit /CHANGENAME <nickname> an");
		this.printMenu();
//Hauptmenü
		try {
			while((this.input = inHost.readLine()) != null) {
//Sofern der Host noch keinen Nickname besitzt
				try {
				if(this.host.getNickname() == "" && !this.input.startsWith("/CHANGENAME")) {
					this.outHost.println("Bitte erst den Befehl /CHANGENAME verwenden");
					continue;
				}
				this.argumente = this.input.split(" ",0);
				switch (argumente[0]) {
				case "/STARTGAME":
//Eine Lobby für Gäste des Hosts wird eingerichtet
					outHost.println("-SEND");
					outHost.println("Spiel wird gestartet");
					int gastport = this.getPortNumber();
//Suche findet solange statt, bis die Lobby voll ist und das Spiel automatisch startet
					this.spielfeld.sucheSpieler(gastport);
					System.out.println("Lobby voll. Spiel startet nun.");
					this.spielfeld.gameLoop();
					this.spielfeld.clearLobby();

//Nach Beendigung des Spiels wird der benutze Port wieder freigegeben, solange keine weitere Partie stattfindet
					Gamehandler.usedServerPorts.remove(Integer.toString(gastport));
					outHost.println("+SEND");
					outHost.println("Menü wird geladen");
					this.printMenu();
					break;
				case "/CONFIG":
					outHost.println("Einstellungen werden geladen");
					this.configGamesetting();
					this.printMenu();
					break;
				case "/SHOWMAN":
					outHost.println("Spielanleitung wird geladen");
					this.printManual();
					break;
				case "/QUIT":
					outHost.println("Verbindung zum Server wird getrennt. Auf Wiedersehen");
					quit = true;
					break;
//NICK-Befehl wurde mit Hilfe des NICK-Befehls aus dem Chat-Server Kleinlösung entwickelt
				case "/CHANGENAME":
					this.host.changeNick(argumente[1]);
					break;
				case "/SHOWCOMMAND":
					this.printMenu();
					break;
				default:
					outHost.println("-ERR: Unbekannter Befehl: "+this.input);
				}
				if(quit == true) {
					break;
				}
				} catch (StringIndexOutOfBoundsException e) {
					this.outHost.println("-ERR Ungültige Eingabe: Leerzeichen nicht erlaubt");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket "+this.host.getHandle()+" wird fehlerhaft geschlossen");
		}
		System.out.println("Host verbunden von "+ this.host.getHandle() +" bekannt unter "+ this.host.getNickname()+" hat sich abgemeldet");
		try {
			this.inHost.close();
			this.outHost.close();
			this.host.closeSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket "+this.host.getHandle()+" wird fehlerhaft geschlossen");
		}
	}
}
