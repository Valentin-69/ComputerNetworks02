package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

class HandlingRunnable implements Runnable{

	private final Socket socket;
	
	public HandlingRunnable(Socket socket) {
		this.socket =socket;
	}
	
	@Override
	public void run() {
		BufferedReader reader = HTTPCommands.getReaderFromSocket(socket);
		if(reader==null){
			System.out.println("closing thread, cause: failed BufferedReader");
			return;
		}
		
		
		ParsedRequest request;
		try{
			request = new ParsedRequest(reader,socket);
		}catch(IllegalStateException e){
			System.out.println("closing thread, cause: failed request");
			return;
		}
		
		request.respond(socket);
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("closing thread, cause: failed socket closing");
			return;
		}
		System.out.println("closing thread, cause: done");
	}

}
