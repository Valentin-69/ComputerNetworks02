package client;


public class ClientMain {
	
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
