/*
 * 
 */
package ee.or.is;

import java.io.*;
import java.net.*;

// TODO: Auto-generated Javadoc
/**
 * The Class TCPSSocket.
 */
public class TCPSSocket extends Thread implements ThreadLog // implements Runnable 
{
	
	/** The a s server. */
	private TCPServer aSServer = null;
   	
	   /** The a socket. */
	private Socket aSocket = null;
	public Socket getSocket(){	return aSocket;}

	/** The in. */
	   private InputStream in = null;
	   public InputStream getInput(){  return in;}
	
	/** The in buf. */
	   private BufferedReader inBuf = null;
   	
	   /** The out. */
	   private OutputStream out = null;
	   public OutputStream getOutput(){  return out;}
	   
	   /** The out buf. */
	   private PrintWriter outBuf = null;
	
	/** The b run. */
	private boolean bRun = true;
   	public void endRun(){ bRun = false;}
	   /** The CA r_ timeout. */
	   private int CAR_TIMEOUT = 3600000;
   	
	   /** The i debug. */
   private int iDebug = 99;
   public boolean isDebug( int iDebug){	return iDebug <= this.iDebug;}

   /////////////////////////////////////////////////////////////////
	/**
    * Instantiates a new tCPS socket.
    * 
    * @param aServer the a server
    * @param aSocket the a socket
    */
   	public TCPSSocket( TCPServer aServer)
	{
		this.aSServer = aServer;
       	log( 95, "New Socket 2 " + aServer.getPort());
	}
   	public TCPSSocket( TCPServer aServer, Socket aSocket)
	{
		this.aSServer = aServer;
		this.aSocket = aSocket;
       	log( 95, "New Socket " + aServer.getPort());
       	try {
    		aSocket.setSoTimeout( CAR_TIMEOUT);
    		
    		in = aSocket.getInputStream();
   			inBuf = new BufferedReader( new InputStreamReader( in));
    		out = aSocket.getOutputStream();
   			outBuf = new PrintWriter( out);
 		} catch (IOException E) {
		    log( "New Socket Error:", E);
        	close();
        }
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
      	log( 95, "New TCPSSocket run " + aSServer.getPort());
        while( bRun ){
      	    if( in == null ) break;
         	try { // Poll every ~1 s
/*         	    try {
					int iChar = in.read();
				} catch (IOException e) {
					e.printStackTrace();
				} */
         	  	sleep( 1000);
         	}
         	catch (InterruptedException e) {}
            try{
                while( bRun && inBuf.ready() /* && in.available() > 0 */ ){
                    String sLine = inBuf.readLine();
   			      	if( sLine != null &&  sLine.length() > 0 ){
   					    getLine( sLine);
   					}else{
   					    log( "Empty Message?");
   					}
                } 
       		}catch( SocketTimeoutException E) {
       		    log( "Socket Timeout:", E);
           	}catch( Exception E) {
           	    log( "Socket error:" + E);
           	}
      	}
       	close();
	}
	
	/**
	 * Put line.
	 * 
	 * @param sLine the s line
	 */
	public void putLine( String sLine)
	{
		outBuf.println( sLine);
		outBuf.flush();
	}
	
	/**
	 * Gets the line.
	 * 
	 * @param sLine the s line
	 * 
	 * @return the line
	 */
	public void getLine( String sLine)
	{
	    log( "get: " + sLine);
	}
   	
	   /**
	    * Close.
	    */
	   public void close() 
   	{
      	try {
         	if( in != null ) in.close();
        }catch( Exception e ){}
		in = null;
      	try {
         	if( out != null ) out.close();
        }catch( Exception e ){}
		out = null;
       	try {
         	if( aSocket != null ) aSocket.close();
      	} catch( Exception e ){}
		aSocket = null;
		log( "Socket close: " +  + aSServer.getPort());
   	}
   	
	   /**
	    * Exit.
	    */
	public void exit()
   	{
   		bRun = false;
		interrupt();
   	}
   	
	   /**
	    * End.
	    */
	   public void end()
   	{
   		aSServer.exit( this);
   		exit();
   	}
 	
	 /* (non-Javadoc)
	  * @see or.is.ThreadLog#getLogName()
	  */
	 public String getLogName()
	{
 		return "TCPSSocket" + ((aSServer!=null)? Integer.toString( aSServer.getPort()): "");
	}
	 public void setLogName( String sLogName){}
	
	/* (non-Javadoc)
	 * @see or.is.ThreadLog#log(java.lang.String)
	 */
	public synchronized void log( String sMsg)
    {
        String sFileName = aSServer.getServlet().getLogFileName( getLogName() + ".txt");
		try {
            PrintWriter PW = new PrintWriter( new FileOutputStream( new File( sFileName), true));
            PW.println( GlobalDate.getTime() + " " + sMsg);
            PW.close();
		}catch( Exception E) {
            Log.log( E);
        }
    }
	
	/**
	 * Log.
	 * 
	 * @param sMsg the s msg
	 * @param E the e
	 */
	public void log( String sMsg, Exception E)
	{
	    log( sMsg + " " + E.getMessage());
	}
    
    /* (non-Javadoc)
     * @see or.is.ThreadLog#log(int, java.lang.String)
     */
	public void log( int iDebug, String sMsg)
    {
    	if( iDebug <= this.iDebug ) log( sMsg);
    }
    
    /**
     * Log.
     * 
     * @param Trace the trace
     */
    public void log( StackTraceElement[] Trace)
    {
        if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
            log( Trace[ i].toString());
    }
    
    /* (non-Javadoc)
     * @see or.is.ThreadLog#log(java.lang.Exception)
     */
    public void log( Throwable E)
	{
	    log( E.getLocalizedMessage());
	    log( E.getMessage()); 
		log( E.getStackTrace()); 
	}
	
	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public TCPServer getServer() {
		return aSServer;
	}
	public boolean isRun()
	{
		return bRun;
	}
}
