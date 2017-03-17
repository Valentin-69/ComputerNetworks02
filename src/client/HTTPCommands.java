package client;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public enum HTTPCommands {
	

	GET{

		@Override
		public boolean isCorrectType(String type) {
			return type.equalsIgnoreCase("GET");
		}

		@Override
		public void execute(Request request) throws IllegalArgumentException, IllegalStateException{
			Socket socket= getSocket(request);
			String host = prompt("Your host name: ");
			sentRequest(request, socket, host); // includes the fileWriter
			FileWriter fw = initiateFileWriter("out.html"); // initiate the fileWriter with given fileName
			BufferedReader br = initBuffReader(socket); // initiate the BufferedReader
			System.out.println("RESULT: "); // Format info
			System.out.println("");		    // Format info
			String t;
			writeOutput(fw, br); 	   // Writes the output of the GET command
			closeReaderWriter(fw, br); // Closes used writer and reader
			closeSocket(socket);

		}

		private void closeReaderWriter(FileWriter fw, BufferedReader br) {
			try {
				br.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void writeOutput(FileWriter fw, BufferedReader br) {
			String t;
			try {
				int i=0;
				while((t = br.readLine()) != null){
					System.out.println(t);
					if(i>6){
						fw.write(t+"\r\n");
					}
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private FileWriter initiateFileWriter(String fileName) {
			try {
				FileWriter result = new FileWriter(fileName);
				return result;
			} catch (IOException e1) {
				System.out.println("could not create the fileWriter");
				e1.printStackTrace();
				throw new IllegalStateException();
			}
		}

		private BufferedReader initBuffReader(Socket socket) throws IllegalArgumentException{
			try {			
				BufferedReader result = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				return result;
			} catch (IOException e) {
				System.out.println("could not get the inputStream of the socket");
				throw new IllegalArgumentException();
			}
		}

		private void sentRequest(Request request, Socket socket, String host) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			}
			pw.println("GET "+request.getURIFile()+ " HTTP/1.1");
			pw.println("Host: "+host);
			pw.println("");
			pw.flush();
		}
		
	},
	HEAD{

		@Override
		public boolean isCorrectType(String type) {
			return type.equalsIgnoreCase("HEAD");
		}

		@Override
		public void execute(Request request) {
			// TODO Auto-generated method stub
			
		}
		
	},
	PUT{

		@Override
		public boolean isCorrectType(String type) {
			return type.equalsIgnoreCase("PUT");
		}

		@Override
		public void execute(Request request) {
			// TODO Auto-generated method stub
			
		}
		
	},
	POST{

		@Override
		public boolean isCorrectType(String type) {
			return type.equalsIgnoreCase("POST");
		}

		@Override
		public void execute(Request request) {
			// TODO Auto-generated method stub
			
		}
		
	};
	public static Scanner scanner = new Scanner(System.in);

	public abstract boolean isCorrectType(String type);
	public abstract void execute(Request request) throws IllegalArgumentException, IllegalStateException;

	public static HTTPCommands getType(String type){
		for (HTTPCommands command : HTTPCommands.values()) {
			if(command.isCorrectType(type)){
				return command;
			}
		}
		return null;
	}
	
	public static String prompt(String message){
		System.out.print(message);
    	String result = scanner.next();
	    return result;
	}
	
	public static Socket getSocket(Request request){
		try {
			return new Socket(request.getURIHost(), request.getPort());
		} catch (UnknownHostException e) {
			System.out.println("The given uri isn't a valid host.");
			throw new IllegalArgumentException();
		} catch (IOException e) {
			System.out.println("Unable to connect to: "+request.getURIHost()+":"+request.getPort());
			throw new IllegalArgumentException();
		}
	}
	
	public static void closeSocket(Socket socket){
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("could not close the socket");
			throw new IllegalStateException();
		}
	}
}
