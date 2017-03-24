package client;

/**
 * A class which contains information about the command that has to be
 * executed and information about the server to which the client must connect.
 * @author Valentin Cleays & Bart Breuls
 */
class Request {
	
	private final HTTPCommands command;
	private final String uriHost;
	private final String uriFile;
	private final int port;
	
	/**
	 * Create a new request with the given argument. The given argument should look like this:
	 * [command, uri, port]. The port is optional, the other two are not. From the Uri it
	 * extracts the uri host and the uri file. If there is no port given in the args, it is set
	 * to the default number 80.
	 * 
	 * @param args
	 * 			[Command, uri, port] with port optional.
	 * @throws IllegalArgumentException
	 * 			When the given args contains less then 2 elements or more then 3.
	 */
	protected Request(String[] args) throws IllegalArgumentException{
		if(args.length<2){
			giveIllegalArgument("Not enough arguments");
			throw new IllegalArgumentException();
		}
		if(args.length > 3){
			giveIllegalArgument("To many arguments");
			throw new IllegalArgumentException();
		}
		command = extractCommand(args);		                        // Set the command
		String[] uriHostAndFile = extractUriHostAndFile(args); 		// setting the host and file
		uriHost = uriHostAndFile[0];
		uriFile = uriHostAndFile[1];
		port = extractPort(args);		                            // setting the port
	}
	
	/**
	 * Gets the command of this request.
	 * @return the command of this request.
	 */
	protected HTTPCommands getCommand(){
		return this.command;
	}
	
	/**
	 * Gets the Uri Host of this request.
	 * @return the uriHost of this request.
	 */
	public String getURIHost(){
		return this.uriHost;
	}
	
	/**
	 * Gets the Uri file of this request.
	 * @return the uriFile of this request.
	 */
	public String getURIFile(){
		return this.uriFile;
	}
	
	/**
	 * Gets the port of this request.
	 * @return the port of this request.
	 */
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
		int index= args[1].indexOf("/");
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
	
	/**
	 * Executes the command of this request.
	 */
	public void execute(){
		command.executeRequest(this);
	}
	
	/**
	 * Displays a predefined message to the user in case of an illegal argument exception.
	 */
	public static void giveIllegalArgument(){
		System.out.println("Invalid input, the input has to follow the syntax found on this page: ");
		System.out.println("https://p.cygnus.cc.kuleuven.be/bbcswebdav/pid-19522495-dt-content-rid-95790460_2/courses/B-KUL-G0Q43a-1617/2017-Assignment2-Java.pdf");
	}
	
	/**
	 * Displays a predefined message to the user and some extra info about the cause of the error.
	 * @param extraInfo
	 * 			Extra information about what caused the error.
	 */
	public static void giveIllegalArgument(String extraInfo){
		giveIllegalArgument();
		System.out.println("Input error: "+extraInfo);
	}
}
