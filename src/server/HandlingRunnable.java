package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class HandlingRunnable implements Runnable{

	private final Socket socket;
	
	public HandlingRunnable(Socket socket) {
		this.socket =socket;
	}
	
	@Override
	public void run() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}		
		
		ParsedRequest request = new ParsedRequest(reader);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}	
		
		
	}

}
