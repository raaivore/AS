/*
 * 
 */
package ee.or.is;
// import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * The Class TCPServer.
 */
public class TCPServer extends ThreadLogImpl  
{
   	
	   /** The host server. */
	   private ServerSocket hostServer = null;
   	
	   /** The i port. */
	   private int iPort = 0;
   	
	   /** The i time out. */
	   private int iTimeOut = 3600000; 
	
	/** The Sockets. */
	private ArrayList<TCPSSocket> Sockets = null;
	
	/** The b run. */
	private boolean bRun = true;
	
	/**
	 * Instantiates a new tCP server.
	 */
	public TCPServer( ISServlet aServlet)
	{
		super( aServlet);
		setDebug( aServlet.getDebug());
	}
	
	/**
	 * Instantiates a new tCP server.
	 * 
	 * @param iPort the i port
	 */
	public TCPServer(  ISServlet aServlet, int iPort)
	{
		super( aServlet);
		this.iPort = iPort;
		setName( "TCPServer" + iPort);
		setLogName( "TCPServer" + iPort);
	}
   	
	   /* (non-Javadoc)
	    * @see java.lang.Thread#run()
	    */
	public boolean permitInHost( String sInHost)
	{
		return true;
	}
	public void run( )
   	{
		long lLastTime = 0;
	  	while( bRun) {
			try {
				log( "TCPServer " + iPort + " start");
			    Sockets = new ArrayList<TCPSSocket>();
				hostServer = new ServerSocket();
				hostServer.setReuseAddress( true);
				hostServer.setSoTimeout( iTimeOut);
	   	        hostServer.bind( new InetSocketAddress( iPort), 50);
				hostServer.setReuseAddress( true);
			  	while( bRun) {
						try {
							Socket aSocket = hostServer.accept();
							String sInHost = aSocket.getRemoteSocketAddress().toString();
							if( !permitInHost( sInHost) ){
								log( "Inadmissible connection: " + sInHost);
								aSocket.close();
								continue;
							}
							boolean bRet = GlobalDate.isNewDay( lLastTime);
							if( bRet ){
								lLastTime = (new Date()).getTime();
								log( 50, "starts new day ");
								getServlet().createLogCatalog();
							}
							if( bRun ){
								log( "TCPServer accept socket");
								if( aSocket != null ){
									TCPSSocket TSocket = createTCPSSocket( this, aSocket);
									TSocket.start();
									Sockets.add( TSocket);
								}
//							    if( aSocket != null ) aSocket.close();
							}
						}catch( SocketTimeoutException E){
							log( 5, "Port " + iPort + ": " + E.getMessage());
							if( hostServer == null || hostServer.isClosed() ) break;
						}catch( Exception E){
							log( "Port " + iPort + ": " + E.getMessage());
							break;
						}
		  		}
			} catch( Exception e) {
				log( "Port " + iPort + ": " + e.getMessage());
			}
			try {
	   	    	if( hostServer != null ){
	   	    	    close();
					log( "Port " + iPort + " closed");
					// for( int iT = iTimeOut; bRun && --iT>=0; ) 
					if( bRun ) sleep( 60000);
	   	    	}
   	    	} catch (Exception e) {
   	    	    log( "Port " + iPort + ": " + e.getMessage());
   	   	    	hostServer = null;
   	   	    	Sockets = null;
   	    	}
	  	}
	  	log( "TCPSocket Server " + iPort + " exits" );  
	}
   	
	   /**
	    * Creates the tcps socket.
	    * 
	    * @param aServer the a server
	    * @param aSocket the a socket
	    * 
	    * @return the tCPS socket
	    */
	   public TCPSSocket createTCPSSocket( TCPServer aServer, Socket aSocket)
   	{
   		return new TCPSSocket( aServer, aSocket);
   	}
   	
	   /**
	    * Close.
	    */
	   public void close()
   	{
   		try {
   	   		if( Sockets != null ){
              	for ( int i = Sockets.size(); --i >= 0; ){
  	   				try {
 	   				    TCPSSocket Socket = ( TCPSSocket)Sockets.get( i);
                        if( Socket != null ){
                        // if( ISServlet.isDebug( 5 ) )
                           log( " exit Socket " + Socket.getName() );
                           Socket.exit();
//                               Socket.close();
                           	// S[ i] = null;
                       }
  	   				} catch ( Exception E) {
  	   				    log( "1Port " + iPort + ": " + E.getMessage());
  	   				}
   	   			}
   	   			Sockets = null;
   	   		}
//            sleep( 1000); // Wait for sockets close
   			if( hostServer != null ){
   			    hostServer.close();
   			    hostServer = null;
   			}
   		} catch( Exception e){
	        log( "0Port " + iPort + ": " + e.getMessage());
	    }
   	}
   	
	   /**
	    * Exit.
	    * 
	    * @param aSocket the a socket
	    */
	   public void exit( TCPSSocket aSocket)
   	{
   		if( Sockets != null ) Sockets.remove( aSocket);
   	}
   	
	   /**
	    * Exit.
	    * 
	    * @return true, if successful
	    */
	   public boolean exit() {
   	    log( " Car Socket Server " + iPort + " exit start");    	
		bRun = false;
		try {
            if( hostServer != null ) hostServer.close();
            interrupt();
        } catch (IOException E) {
        }
//		interrupt();
//		close();
   		try {
            //		interrupt();
//     if( Db != null ) Db.close();
//      Db = null;
            Thread.sleep( 1000);
        } catch (InterruptedException E1) {
        }
   		log( " Car Socket Server " + iPort + " exit ends");    	
   		return !isAlive();
   }
	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return iPort;
	}
}
