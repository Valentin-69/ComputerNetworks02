package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public enum HTTPCommands {
	

	GET{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("GET");
		}

		@Override
		protected void respond(Socket socket, String file){
			if(file==null || file.isEmpty() || (file.length()==1 && file.substring(0, 1).equals("/"))){
				file=ServerMain.DEFAULT_FILE_PATH;
			}
			System.out.println("file: "+file);
			BufferedInputStream fileStream = getBufferedInputStreamFromFileName(file);
			if(fileStream==null){
				respondWhenFileFails(socket, file);
				return;
			}
			
			BufferedOutputStream socketStream = getBufferedOutputStreamFromSocket(socket);
			if(socketStream==null){
				System.out.println("could not create stream to socket");
				return;
			}
			writeOKHeaderToStream(socketStream);
			try {
				while(fileStream.available()!=0){
					socketStream.write(fileStream.read());
				}
				socketStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void respondWhenFileFails(Socket socket, String file){
			System.out.println("could not get file: \""+file+"\"");
		}

	},
	HEAD{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("HEAD");
		}

		@Override
		protected void respond(Socket socket, String file) {
			// TODO Auto-generated method stub
			
		}
		
		
	},
	PUT{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("PUT");
		}

		@Override
		protected void respond(Socket socket, String file) {
			// TODO Auto-generated method stub
			
		}

		
	},
	POST{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("POST");
		}

		@Override
		protected void respond(Socket socket, String file) {
			// TODO Auto-generated method stub
			
		}

	};
	protected static Scanner scanner = new Scanner(System.in);

	protected abstract boolean isCorrectType(String type);

	protected abstract void respond(Socket socket, String file);

	protected static HTTPCommands getType(String type) throws IllegalArgumentException{
		for (HTTPCommands command : HTTPCommands.values()) {
			if(command.isCorrectType(type)){
				return command;
			}
		}
		throw new IllegalArgumentException();
	}

	protected static BufferedWriter GetWriterToSocket(Socket socket){
		try {
			return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	protected static BufferedReader getReaderFromSocket(Socket socket){
		try {
			return  new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	protected static BufferedInputStream getBufferedInputStreamFromFileName(String file){
		try {
			return new BufferedInputStream(new FileInputStream(new File("serverFiles"+file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static BufferedOutputStream getBufferedOutputStreamFromSocket(Socket socket){
		try {
			return new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static void writeOKHeaderToStream(BufferedOutputStream socketStream){
		String okHead = "HTTP/1.1 200 OK \r\n\r\n";
		for (int i=0;i<okHead.length();i++) {
			try {
				socketStream.write((int) okHead.charAt(i));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
}
