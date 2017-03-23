package server;

import java.io.BufferedReader;
import java.io.IOException;

class ParsedRequest {
	
	private HTTPCommands command;
	private String file;
	
	
	public ParsedRequest(BufferedReader reader) {
		try {
			String line = reader.readLine();
			if(line==null){
				return;
			}
			try{
				int indexToFirstSpace =line.indexOf(" ");
				command=HTTPCommands.getType(line.substring(0, indexToFirstSpace));
				int indexToSecondSpace = line.indexOf(" ",indexToFirstSpace);
				file=line.substring(indexToFirstSpace+1,indexToSecondSpace);
				
				
			}catch(StringIndexOutOfBoundsException | IllegalArgumentException e){
				e.printStackTrace();
				return;
			}
			
			while((line = reader.readLine()) != null){
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
