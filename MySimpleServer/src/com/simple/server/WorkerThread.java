package com.simple.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WorkerThread implements Runnable {

	// TCP Components
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    private String clientName;
    private int counter; 
    private ExecutorService executor; 
   
    // Main Constructor
    public WorkerThread(Socket socket, ExecutorService executor) {
    	try {
            this.client = socket;
            this.executor = executor;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }//end constructor 
    
    
    // *********************************** Business processing logic ********************
    public void run() {
        try {
        	
        	int count=0; 
        	
        	//Now start reading input from client
        	//Dot indicates end of data 
        	//Client sends 2 things - clientName and the counter 
            while((clientName = in.readLine()) != null && !clientName.equals(".")) 
            {
            	counter=Integer.parseInt(in.readLine());
            	System.out.println("Client:"+clientName + ",Counter value:"+counter);   
            	
            	//Send response to the client 
                out.println("Received record:"+counter);
                
                //Note:My client is sending records with 1 second delay. Hence to do the fair scheduling, I used 
                //a counter to read the #of records processed. 
                //Alternate way to do this is to measure the elapsed time something like this.
                //long startTime = System.nanoTime();    
                //long estimatedTime = System.nanoTime() - startTime;
                            
                count++;                
                if(count%4==0)
                {//After processing 4 records, put the task at the end of queue 
                	executor.execute(this);	    
                    break;
                }
            }                            	
          
        } catch (IOException e) {
            System.out.println(e);
            closeConnection();
        } catch (Exception e){
        	closeConnection();
        }
        finally { 
            try { 
            	if(clientName.equals("."))    //For the last record, client sends dot followed by clientName 
            	{
            		clientName=in.readLine();  //get the real client name 
            		out.println("Server received last record from " + clientName);
            		closeConnection();
            		System.out.println("All Tasks completed for client "+clientName);
            	}
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
         } 
    }
    
    private void closeConnection(){
    	try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		out.close(); 
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 	
    }

    // *********************************** Execute the task using executor service ********************
	public void execute() {
		executor.execute(this);
	}  
   
}