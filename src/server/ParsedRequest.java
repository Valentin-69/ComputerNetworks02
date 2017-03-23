package server;

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
	
	
	protected ParsedRequest(BufferedReader reader, Socket socket) throws IllegalStateException{
		System.out.println("getting request: ");
		try {
			String line = reader.readLine();
			System.out.println(line);
			if(line==null){
				badRequest(socket);
				return;
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
			while(reader.ready()){
				System.out.print(Character.toString((char) reader.read()));
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(this);

	}
	
	private void badRequest(Socket socket) throws IllegalStateException{
		System.out.println("bad request");
	}
	
	protected void respond(Socket socket){
		command.respond(socket,file);
	}
	
	@Override
	public String toString() {
		return "PR:["+command+";"+file+";"+hostName+"]";
	}
}
