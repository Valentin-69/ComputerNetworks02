package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerMain {

	protected static final String DEFAULT_FILE_PATH="/index.html";
	protected static final String CONTENT_TYPE = "Content-Type"; 
	protected static final String DEFAULT_POST_PATH="/defaultForm.txt";
	protected static final ArrayList<String> POST_FORMS = new ArrayList<>(Arrays.asList(DEFAULT_POST_PATH,"/customForm.txt")); 

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
