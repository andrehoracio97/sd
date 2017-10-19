import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class establishes a TCP connection to a specified server, and loops
 * sending/receiving strings to/from the server.
 * <p>
 * The main() method receives two arguments specifying the server address and
 * the listening port.
 * <p>
 * The usage is similar to the 'telnet <address> <port>' command found in most
 * operating systems, to the 'netcat <host> <port>' command found in Linux,
 * and to the 'nc <hostname> <port>' found in macOS.
 *
 * @author Raul Barbosa
 * @author Alcides Fonseca
 * @version 1.1
 */
//----------------------------------------------------
class Mensagens{
	String mensagem;
	long time=0;
	public Mensagens() {
	}
	String coloca(String nova) {
		if(mensagem.split(";")[0].equals("Type|request")) {
			mensagem="Type|login;"+mensagem.split(";")[1]+";password|"+nova;
		}
		else if(mensagem.split(";")[0].equals("Type|listas")) {
			mensagem="Type|voto;lista|"+nova;
		}	
		return mensagem;
	}
	void clear() {
		mensagem=null;
	}
	void set(String s) {
		mensagem=s;
	}
	String ver() {
		return mensagem;
	}
	void set_time(long t) {
		time=t;
	}
	boolean timeout(long x) {
		if(x>=time)
			return false;
		else
			return true;
	}
	
}
//-------------------------------------------------------


class tcp_client {
  public static void main(String[] args) {
    Socket socket;
    PrintWriter outToServer;
    BufferedReader inFromServer = null;
    Mensagens msg = new Mensagens();
    
    
    
    
    try {
      // connect to the specified address:port (default is localhost:12345)
      if(args.length == 2)
        socket = new Socket(args[0], Integer.parseInt(args[1]));
      else
        socket = new Socket("localhost", 6000);
        
      // create streams for writing to and reading from the socket
      inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outToServer = new PrintWriter(socket.getOutputStream(), true);
      

      
      // create a thread for reading from the keyboard and writing to the server
      new Thread() {
        public void run() {
          Scanner keyboardScanner = new Scanner(System.in);
         
          while(!socket.isClosed()) {
 
        	  //apresentar listas
        	  String readKeyboard = keyboardScanner.nextLine();
        	  synchronized(msg) {
        		  try {
        			  if(!msg.timeout(System.currentTimeMillis())) {
        				  msg.clear();
        			  }
					  outToServer.println(msg.coloca(readKeyboard));
					  msg.clear();
        		  }
        		  catch(NullPointerException e) {
        			  System.out.println("block");
        		
        		  }
        	  }

          }
        }
      }.start();

      // the main thread loops reading from the server and writing to System.out
      
      String messageFromServer;
      while((messageFromServer = inFromServer.readLine()) != null) {
    	  System.out.println(messageFromServer);
    	  if(messageFromServer.equals("Type|empty;")) {
    		  synchronized(msg) {
        		 if(msg.ver()==null) {
        			 outToServer.println(messageFromServer+"disp|1;");
        			 System.out.println("deu");
        		 }
        		 else {
        			 outToServer.println(messageFromServer+"disp|0;");
        		 }
        	  }
    	  }
    	  else {
    		  synchronized(msg) {
        		  msg.set(messageFromServer);
        		  msg.set_time(System.currentTimeMillis()+120000);
        	  }
    	  }
    	  
      }
      
    } catch (IOException e) {
      if(inFromServer == null)
        System.out.println("\nUsage: java TCPClient <host> <port>\n");
      System.out.println(e.getMessage());
    } finally {
      try { inFromServer.close(); } catch (Exception e) {}
    }
  }
}