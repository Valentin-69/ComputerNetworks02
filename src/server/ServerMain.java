package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {

	protected static final String DEFAULT_FILE_PATH="/index.html";
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new SafetyGui();
		ServerSocket listeningSocket;
		try {
			listeningSocket = new ServerSocket(80);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		while(true){
			try {
				new Thread(new HandlingRunnable(listeningSocket.accept())).start();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
}
