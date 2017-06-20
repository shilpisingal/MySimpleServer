package com.simple.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

	public class SimpleServer {
		
	    // *********************************** Main Method ********************
	    public static void main(String args[]) {
	        new SimpleServer();
	    }
	    

	    // TCP Components
	    private ServerSocket serverSocket;
	    private int portNo=-1; 

	    // Main Constructor
	    public SimpleServer() 
	    {
	    	startServer();// start the server
	    }

	    // *********************************** Method to start the server ********************
	    public void startServer() {
	    	
	    	//Read the properties file to get the connection info to the server and other preferences 
	    	ReadProperties rp = new ReadProperties();
			rp.loadFile("config.properties");    
			portNo= Integer.valueOf(rp.getPropertyValue("portNum"));
	        
		    if(portNo<0 || portNo>65535)
		    {
		    	System.out.println("Please supply a valid port number");    
		    	return;
		    }
		    
		    //Create a single threaded executor 
		    ExecutorService executor = Executors.newSingleThreadExecutor(); 
		    
	        try {
	            serverSocket = new ServerSocket(portNo, 0, InetAddress.getLocalHost());
	            System.out.println(serverSocket);
                //System.out.println(serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
	            System.out.println(".........................................................................");	            
	            
	            //Execute each task in a sequential manner using the single thread 
	            //Note: WorkerThread controls the time dedicated to each client 
	            while (true) {
	            	Socket socket = serverSocket.accept();
	            	WorkerThread worker = new WorkerThread(socket,executor);
	            	worker.execute();                	     
	            }
	        } catch (IOException e) {
	            System.out.println("IO Exception:" + e);	          
	        } catch (NumberFormatException e) {
	            System.out.println("Number Format Exception:" + e);
	            System.exit(1);
	        }
	        finally{
	        	if(executor !=null)
	        		executor.shutdown();
	        }
	    }
	  
	}//end class SimpleServer 

