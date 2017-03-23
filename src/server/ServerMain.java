package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
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
