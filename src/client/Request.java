package client;

class Request {
	
	private final HTTPCommands command;
	private final String uriHost;
	private final String uriFile;
	private final int port;
	
	protected Request(String[] args) throws IllegalArgumentException{
		if(args.length<2){
			giveIllegalArgument("Not enough arguments");
			throw new IllegalArgumentException();
		}
		command = extractCommand(args);		                        // Set the command
		String[] uriHostAndFile = extractUriHostAndFile(args); 		// setting the host and file
		uriHost = uriHostAndFile[0];
		uriFile = uriHostAndFile[1];
		port = extractPort(args);		                            // setting the port
	}
	
	protected HTTPCommands getCommand(){
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
	

	/**
	 * Extracts the portnumber from the given args which consist of 2 or 3 elements.
	 * @param args
	 * 			[command, uri, port]  port is optional.
	 */
	private int extractPort(String[] args) {
		int result;
		try{
			if(args.length==2){
				result = 80;
			}else{
				result = Integer.parseInt(args[2]);
			}
		}catch(NumberFormatException e){
			giveIllegalArgument("port has to be a number");
			throw new IllegalArgumentException();
		}
		return result;
	}

	/**
	 * Extracts the uriHost and uriFile from the given args which consist of 2 or 3 elements.
	 * @param args
	 * 			[command, uri, port]  port is optional.
	 */
	private String[] extractUriHostAndFile(String[] args) {
		if(args[1].contains("://")){
			args[1]=args[1].substring(args[1].indexOf("://")+3, args[1].length()); // for dealing with the http:// of the URI.
		}
		String[] result = new String[2];
		int index= args[1].indexOf("/",args[1].indexOf("."));
		String host, file;
		if(index!=-1){ //there is a / after a point in the string
			host = args[1].substring(0, index);
			file = args[1].substring(index, args[1].length());
		}else{
			host = args[1];
			file = "/";
		}
		result[0] = host;
		result[1] = file;
		return result;
	}

	/**
	 * Extracts the command from the given args which consist of 2 or 3 elements.
	 * @param args
	 * 			[command, uri, port]  port is optional.
	 */
	private HTTPCommands extractCommand(String[] args) {
		HTTPCommands result = HTTPCommands.getType(args[0]);
		if(result==null){
			giveIllegalArgument("Wrong command");
			throw new IllegalArgumentException();
		}
		return result;
	}
	
	public void execute(){
		command.executeRequest(this);
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
