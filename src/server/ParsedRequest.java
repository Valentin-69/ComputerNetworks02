package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ParsedRequest {
	
	private HTTPCommands command;
	private String file;
	private String hostName;
	private String body="";
	
	
	protected ParsedRequest(BufferedReader reader, Socket socket) throws IllegalStateException{
		System.out.println("getting request: ");
		try {
			String line = reader.readLine();
			System.out.println(line);
			if(line==null){
				badRequest(socket);
				throw new IllegalStateException();
			}
			try{
				int indexToFirstSpace =line.indexOf(" ");
				command=HTTPCommands.getType(line.substring(0, indexToFirstSpace));
				int indexToSecondSpace = line.indexOf(" ",indexToFirstSpace+1);
				file=line.substring(indexToFirstSpace+1,indexToSecondSpace);
				if(file.isEmpty() ||!file.substring(0, 1).equals("/")){
					file = "/"+file;
				}

				line=reader.readLine();
				System.out.println(line);
				hostName = line.substring(line.indexOf(":")+1,line.length());

			}catch(StringIndexOutOfBoundsException | IllegalArgumentException e){
				badRequest(socket);
				e.printStackTrace();
				return;
			}
			
			System.out.println("");
			System.out.println("NON OBLIGATORY HEAD");
			System.out.println("-------------------");
			while(!(line = reader.readLine()).isEmpty()){
				System.out.println(line);
			}
			System.out.println("");
			System.out.println("BODY");
			System.out.println("-------------------");

			while(reader.ready()){
				char charToAdd = (char) reader.read();
				body+=Character.toString(charToAdd);
				System.out.print(Character.toString(charToAdd));
			}
			
		} catch (IOException e) {
			serverError(socket);
			e.printStackTrace();
		}
		System.out.println(this);

	}
	
	protected String getFile(){
		return file;
	}
	
	protected String getBody(){
		return body;
	}
	
	private void badRequest(Socket socket){
		System.out.println("bad request");
		try {
			HTTPCommands.writeBadRequestHeaderToStream(new BufferedOutputStream(socket.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void serverError(Socket socket){
		System.out.println("server error");
		try{
			HTTPCommands.writeServerErrorHeaderToStream(new BufferedOutputStream(socket.getOutputStream()), true);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	protected void respond(Socket socket){
		command.respond(socket,this);
	}
	
	@Override
	public String toString() {
		return "PR:["+command+";"+file+";"+hostName+"]";
	}
}
