package client;

/**
 * A class that functions as the main method to run the client.
 *  
 * @author Valentin Cleays & Bart Breuls
 */
 class ClientMain {
	
	 /**
	  * Creates an new request from the given arguments and tries to execute it.
	  * 
	  * @param args
	  * 		Contains the command, the URI and the port number.
	  */
	public static void main(String[] args) {
		Request request;
		try{
			request = new Request(args);
		}catch(IllegalArgumentException e){
			return;
		}
		try{
			request.execute();
		}catch(IllegalArgumentException | IllegalStateException e){
			return;
		}
	}

}
 