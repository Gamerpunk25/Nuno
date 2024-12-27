package Version_Norman;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Aufruf: java UnoServer <port>");
			System.exit(0);
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Ungültige Portnummer");
			System.exit(0);
		}
		try {
//Mögliche Lösung: Zentraler Server, der HostClients annimmt und temporär ein Gastserver entsteht, der Gäste annimmt und solange aktiv bleibt, bis die Runde vorbei ist
			//ServerSocket server = new ServerSocket(port);
			ServerSocket server = new ServerSocket(port, 20);
			System.out.println("UnoServer auf " + port + " gestartet. Warte auf Verbindung mit Host");
			while(true) {
				Socket hostClient = server.accept();
				new Thread(new Gamehandler(hostClient)).start();
			}
//Host, der das Spiel einrichtet und startet, wird ermittelt			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
