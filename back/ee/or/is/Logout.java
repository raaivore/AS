/*
 * 
 */
package ee.or.is;

import java.io.Serializable;

import javax.servlet.http.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Logout.
 */
public class Logout extends Thread implements Serializable
{
	private static final long serialVersionUID = 1L;
	private boolean bLogout = true;
	private ISServlet aServlet = null;	
	private HttpSession  aSession = null;
	private int iTime = 60000; //60s
	public void setTime( int iTime){ this.iTime = iTime * 1000;}
	
	private boolean bExit = false;
	
	public Logout( ISServlet aServlet, HttpSession aSession) {
		super();
		this.aServlet = aServlet;
		this.aSession = aSession;
		this.setName( "Logout " + aSession.getId());
		if( aServlet.isDebug( 95) )aServlet.final_log( "Logout " + aSession.getId());
    }
	public synchronized void cancelLogout()
	{
		bLogout = false;
	}
    public void run()
    {
        try {
        	for(;;){
        		Thread.sleep( iTime);
        		if( bLogout ){
        			break;
        		}else
        			bLogout = true;
        	}
        }catch ( InterruptedException e){
        }
		if( !bExit ) try {
	    	if( aServlet.isDebug( 95) ) aServlet.final_log( "sessionLogout started");
		    aServlet.closeSight( aSession);
		} catch ( Exception e) {
		}
    }
    public void exit() 
    {
    	bExit = bLogout = true;
    }
    public synchronized void done()
    {
    	bLogout = true;
    	interrupt();
    }
}
