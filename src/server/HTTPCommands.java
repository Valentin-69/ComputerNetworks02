package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

public enum HTTPCommands {
	

	GET{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("GET");
		}

		@Override
		protected void respond(Socket socket, ParsedRequest request){
			String file = request.getFile();
			if(file==null || file.isEmpty() || (file.length()==1 && file.substring(0, 1).equals("/"))){
				file=ServerMain.DEFAULT_FILE_PATH;
			}
			System.out.println("file: "+file);
			
			BufferedOutputStream socketStream = getBufferedOutputStreamFromSocket(socket);
			if(socketStream==null){
				System.out.println("could not create stream to socket");
				return;
			}
			
			BufferedInputStream fileStream = getBufferedInputStreamFromFileName(file);
			if(fileStream==null){
				respondWhenFileFails(socketStream,file);
				return;
			}
			

			
			writeOKHeaderToStream(socketStream,false);
			
			try {
				while(fileStream.available()!=0){
					socketStream.write(fileStream.read());
				}
				socketStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void respondWhenFileFails(BufferedOutputStream stream,String file){
			writeFileNotFoundHeaderToStream(stream,true);
			System.out.println("could not get file: \""+file+"\"");
		}

	},
	HEAD{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("HEAD");
		}

		@Override
		protected void respond(Socket socket, ParsedRequest request) {
			String file = request.getFile();
			if(file==null || file.isEmpty() || (file.length()==1 && file.substring(0, 1).equals("/"))){
				file=ServerMain.DEFAULT_FILE_PATH;
			}
			
			BufferedOutputStream socketStream = getBufferedOutputStreamFromSocket(socket);
			if(socketStream==null){
				System.out.println("could not create stream to socket");
				return;
			}
			
			BufferedInputStream fileStream = getBufferedInputStreamFromFileName(file);
			if(fileStream==null){
				respondWhenFileFails(socketStream,file);
				System.out.println("404");
				return;
			}

			writeOKHeaderToStream(socketStream,true);
		}
		
		private void respondWhenFileFails(BufferedOutputStream stream,String file){
			writeFileNotFoundHeaderToStream(stream,true);
			System.out.println("could not get file: \""+file+"\"");
		}
		
	},
	PUT{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("PUT");
		}

		@Override
		protected void respond(Socket socket, ParsedRequest request) {
			String file = request.getFile();
			if(file==null || file.isEmpty() || (file.length()==1 && file.substring(0, 1).equals("/"))){
				file=ServerMain.DEFAULT_FILE_PATH;
			}
			BufferedOutputStream socketStream = getBufferedOutputStreamFromSocket(socket);
			if(socketStream==null){
				System.out.println("could not create stream to socket");
				return;
			}

			respondForNormalPut(socketStream, file, request);

		}
		

		
	},
	POST{

		@Override
		protected boolean isCorrectType(String type) {
			return type.equals("POST");
		}

		@Override
		protected void respond(Socket socket, ParsedRequest request) {
			String file = request.getFile();
			if(file==null || file.isEmpty() || (file.length()==1 && file.substring(0, 1).equals("/"))){
				file=ServerMain.DEFAULT_FILE_PATH;
			}
			BufferedOutputStream socketStream = getBufferedOutputStreamFromSocket(socket);
			if(socketStream==null){
				System.out.println("could not create stream to socket");
				return;
			}

			if(request.getHeaderMap().containsKey(ServerMain.CONTENT_TYPE)&&
					request.getHeaderMap().get(ServerMain.CONTENT_TYPE).contains("urlencoded")){
				respondForContentPost(socketStream,file,request);
			}else{
				respondForNormalPut(socketStream,file,request);
			}
			
		}

		private void respondForContentPost(BufferedOutputStream socketStream, String file, ParsedRequest request){
			if(!ServerMain.POST_FORMS.contains(file)){
				throw new IllegalArgumentException();
			}
			
		}
		
	};
	protected static Scanner scanner = new Scanner(System.in);

	protected abstract boolean isCorrectType(String type);

	protected abstract void respond(Socket socket, ParsedRequest request);

	protected static HTTPCommands getType(String type) throws IllegalArgumentException{
		for (HTTPCommands command : HTTPCommands.values()) {
			if(command.isCorrectType(type)){
				return command;
			}
		}
		throw new IllegalArgumentException();
	}

	private static void respondForNormalPut(BufferedOutputStream socketStream,String file, ParsedRequest request){
		if(file.equals(ServerMain.DEFAULT_FILE_PATH) || ServerMain.POST_FORMS.contains(file)){
			writeServerErrorHeaderToStream(socketStream,true);
			return;
		}
		
		if(writeNewServerFile(file,request.getBody())){
			writeOKHeaderToStream(socketStream, true);
		}else{
			writeServerErrorHeaderToStream(socketStream,true);
		};
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
	
	protected static String getHeaderEnd() {
		return "Date: "+getServerTime()+"\r\n\r\n";
	}
	
	protected static void writeOKHeaderToStream(BufferedOutputStream stream, boolean end){
		String okHead = "HTTP/1.1 200 OK \r\n"+getHeaderEnd();
		writeStringToStream(okHead, stream,end);

	}
	
	protected static void writeFileNotFoundHeaderToStream(BufferedOutputStream stream, boolean end){
		String fileNotFound = "HTTP/1.1 404 Not Found \r\n";
		writeStringToStream(fileNotFound, stream,end);
	}
	
	
	protected static void writeBadRequestHeaderToStream(BufferedOutputStream stream, boolean end){
		String badRequest = "HTTP/1.1 400 Bad Request\r\n"+getHeaderEnd();
		writeStringToStream(badRequest, stream,end);
	}
	
	protected static void writeServerErrorHeaderToStream(BufferedOutputStream stream, boolean end){
		String serverError = "HTTP/1.1 500 Server Error\r\n"+getHeaderEnd();
		writeStringToStream(serverError, stream,end);
	}
	
	protected static void writeNotModifiedHeaderToStream(BufferedOutputStream stream, boolean end){
		String notModified = "HTTP/1.1 304 Not Modified\r\n"+getHeaderEnd();
		writeStringToStream(notModified, stream,end);
	}
		
	protected static void writeStringToStream(String data, BufferedOutputStream stream, boolean end){
		for (int i=0;i<data.length();i++) {
			try {
				stream.write((int) data.charAt(i));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(end){
			try {
				stream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static String getServerTime(){
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(
	        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dateFormat.format(calendar.getTime());
	}
	
	protected static boolean writeNewServerFile(String file, String body) {
		BufferedOutputStream stream = getStreamToServerFile(file);
		if(stream==null){
			return false;
		}
		writeStringToStream(body, stream, true);
		try {
			stream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		
	}
	
	protected static boolean updateServerPostFile(String file, HashMap<String, String> attributeToValueMap){
		if(!ServerMain.POST_FORMS.contains(file)){
			throw new IllegalArgumentException();
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader("serverFiles"+file));
			BufferedWriter writer = new BufferedWriter(new FileWriter("serverFiles"+file));
			String line;
			while(!(line = reader.readLine()).isEmpty()){
				if(line.contains(":") && attributeToValueMap.containsKey(line.substring(0, line.indexOf(":")))){
					writer.write(line.substring(0, line.indexOf(":"))+": "+attributeToValueMap.get(line.substring(0, line.indexOf(":"))));
					attributeToValueMap.remove(line.substring(0, line.indexOf(":")));
				}else{
					writer.write(line);
					writer.write("\r\n");
				}
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected static BufferedOutputStream getStreamToServerFile(String file){
		try {
			return new BufferedOutputStream(new FileOutputStream(new File("serverFiles"+file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
