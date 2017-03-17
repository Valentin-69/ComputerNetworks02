package client;

public class Request {
	
	private final HTTPCommands command;
	private final String uriHost;
	private final String uriFile;
	private final int port;
	
	public Request(String[] args) throws IllegalArgumentException{
		if(args.length<2){
			giveIllegalArgument("Not enough arguments");
			throw new IllegalArgumentException();
		}
		
		command = HTTPCommands.getType(args[0]);
		if(command==null){
			giveIllegalArgument("Wrong command");
			throw new IllegalArgumentException();
		}
		
		int index = args[1].indexOf("/",args[1].indexOf("."));
		uriHost = args[1].substring(0, index);
		uriFile = args[1].substring(index, args[1].length());
		
		try{
			if(args.length==2){
				port=80;
			}else{
				port = Integer.parseInt(args[2]);
			}
		}catch(NumberFormatException e){
			giveIllegalArgument("port has to be a number");
			throw new IllegalArgumentException();
		}
	}
	
	public HTTPCommands getCommand(){
		return this.command;
	}
	
	public String getURIHost(){
		return this.uriHost;
	}
	
	public String getURIFile(){
		return this.uriFile;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void execute(){
		command.execute(this);
	}
	
	public static void giveIllegalArgument(){
		System.out.println("Invalid input, the input has to follow the syntax found on this page: ");
		System.out.println("https://p.cygnus.cc.kuleuven.be/bbcswebdav/pid-19522495-dt-content-rid-95790460_2/courses/B-KUL-G0Q43a-1617/2017-Assignment2-Java.pdf");
	}
	
	public static void giveIllegalArgument(String extraInfo){
		giveIllegalArgument();
		System.out.println("Input error: "+extraInfo);
	}
}
