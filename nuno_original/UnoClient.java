import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UnoClient {
	
//Optimierungsmöglichkeit: Server sendet eine Meldung, wenn ein Spieler Daten senden darf
//Ohne Erlaubnis erfolgt eine Meldung an den Spieler, dass andere gerade senden (Token-Ring Prinzip)
	
	private static class Input extends Thread {
		private BufferedReader in;
		private Socket socket;
//Token, der unbefugtes Senden kontrollieren soll
		protected boolean allowed = false;
		
		
		Input(Socket socket) throws IOException{
			this.socket = socket;
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		}
		
		public void run() {
			String message = "";
			try {
				while ((message = in.readLine()) != null) {
					switch(message) {
//Server sendet die Erlaubnis zum Senden von Befehlen
					case "+SEND":
						this.allowed = true;
						break;
					case "-SEND":
						this.allowed = false;
						break;
					default:
						if(message.length() != 0) {
							System.out.println(message);
						}
					}
				}
				System.exit(0);
//Beim Zurücksetzen des Servers kommt ein Connection Reset Fehler
			} catch (IOException e) {
				System.err.println("Verbindung zum Server wurde abgebrochen");
				System.exit(0);
			}
		}
	}
	
	private static class Output extends Thread{
		
		private Socket socket;
		private PrintWriter out;
		private Scanner s = new Scanner(System.in);
//Kenntnis über den Input nur notwendig, um Sendeerlaubnis einzuholen
		private Input in;
		
		Output(Socket socket, Input in) throws IOException{
			this.socket = socket;
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in = in;
		}
		
		public void run() {
			String message = "";
			while(!(message = s.nextLine()).equals("Quit")) {
				if(in.allowed) {
				out.println(message);
				} else {
				System.out.println("Senden der Nachricht abgebrochen. Keine Sendeerlaubnis");
				}
			}
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		
		if(args.length != 2) {
			System.err.println("Aufruf: java UnoClient <host> <port>");
			System.exit(0);
		}
		
		String host = args[0];
		int port = 0;
		
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Ungültige Portnummer");
			System.exit(0);
		}
		try {
			Socket clientSocket = new Socket(host, port);
			Input input = new Input(clientSocket);
			Output output = new Output(clientSocket, input);
			input.start();
			output.start();
		} catch (Exception e) {
			
		}
	}
}
