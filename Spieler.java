package Version_Norman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Spieler {
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private String nickname = "";
	private String handle;
	private ArrayList<Karte> hand;
	
	public Spieler(Socket psocket) throws IOException {
		this.socket = psocket;
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
		this.handle = this.socket.getInetAddress().getHostAddress() +":"+ this.socket.getPort();
		this.hand = new ArrayList<Karte>();
	}
	
	public void changeNick(String newnickname) {
		if(newnickname.length() <2) {
			this.output.println("Dieser Nickname ist zu kurz!");
			return;
		}
		if(newnickname.length() >21) {
			this.output.println("Dieser Nickname ist zu lang!");
			return;
		}
		this.nickname = newnickname;
		
		//Server gibt die Adresse und den Port des an den Client gebundenen Socket wieder sowie dessen Nickname
		this.output.println("Nickname "+this.nickname+" wurde bestätigt");
		System.out.println("Spieler verbunden von "+this.handle+" ist bekannt unter "+this.nickname);
	}
	
//Methode, um Karten legen zu können. Sucht nach der gewünschten Karte anhand der Konsoleneingabe.
//Anfrage wird wiederholt, wenn der 
	public void karteLegen() {
		
	}
//Methode, um die Hand zu zeigen. Funktioniert
//Erweiterung: Karten graphisch darstellen. Noch zu erledigen
	protected void kartenAnzeigen() {
		for(int j = 0; j < 6; j++) {
			switch(j) {
			case 0:
			for(int i = 0; i < this.hand.size(); i++) {
				this.output.print("_______ ");
			}
			this.output.println();
			break;
			case 1:
				for(int i = 0; i < this.hand.size(); i++) {
					this.output.print("| "+this.hand.get(i).getFarbe()+"|");
				}
				this.output.println();
				break;
			case 2:
				for(int i = 0; i < this.hand.size(); i++) {
					this.output.print("|     | ");
				}
				this.output.println();
				break;
			case 3:
				for(int i = 0; i < this.hand.size(); i++) {
					this.output.print("|  "+this.hand.get(i).getWert()+"  | ");
				}
				this.output.println();
				break;
			case 4:
				for(int i = 0; i < this.hand.size(); i++) {
					this.output.print("\u00AF\u00AF\u00AF\u00AF\u00AF\u00AF\u00AF ");
				}
				this.output.println();
				break;
			case 5:
				for(int i = 0; i < this.hand.size(); i++) {
					this.output.print("   "+(i+1)+"    ");
				}
				this.output.println();
				break;
			}
		}
	}
	
//Mehtode, um die Größe der Hand zu zeigen
	public int anzahlKartenHand() {
		return this.hand.size();
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public void closeSocket() throws IOException {
		this.socket.close();
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String name) {
		this.nickname = name;
	}
	
	public BufferedReader getInput() {
		return this.input;
	}
	
	public PrintWriter getOutput() {
		return this.output;
	}
	
	public ArrayList<Karte> getHand() {
		return this.hand;
	}
	
	public Karte getKarte(int index) {
		return this.hand.get(index);
	}
	
	public void setHand(List<Karte> karten){
		this.hand.addAll(karten);
	}
	
	public void removeKarte(int index) {
		this.hand.remove(index);
	}
	public void drawKarte(Karte karte) {
		this.hand.add(karte);
	}
	
	public String getHandle() {
		return this.handle;
	}
}
