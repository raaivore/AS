/*
 * 
 */
package ee.or.is;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.*;

// import org.apache.catalina.session.StandardSession;
import org.w3c.dom.Element;

public class SessionManager implements HttpSessionListener, ServletContextListener
{
	private ISServlet aServlet = null;
//	private ThreadLogTask aLogTask = null;

	private int iSessionTimeout = 15;
	private int iSessionMaxNr = 50;

	private Hashtable < String, Sight> aSights = null; 
	private void createSights(){ createSights( iSessionMaxNr);}
	private void createSights( int nSights){ aSights = new Hashtable< String, Sight>( nSights);}
	public int getSightsNr( ){	return aSights.size();}

    public void contextInitialized( ServletContextEvent event) 
    {
        event.getServletContext().setAttribute( "SessionManager", this);
    }
    public void init( ISServlet aServlet)
    {
    	if( this.aServlet != null ) return;
    	this.aServlet = aServlet;
//		aLogTask = new ThreadLogTask( aServlet, "SessionManager");
//		aLogTask.start();
		Config aConfig = aServlet.getConfig();
		Element aRoot = ( Element)aConfig.getRootNode();
		if( aRoot.hasAttribute(  "session_timeout") ){
		    iSessionTimeout = aConfig.getElemAttrInt( aRoot, "session_timeout");
		    log( " session_timeout = " + iSessionTimeout);
		}
		if( aRoot.hasAttribute(  "session_max") ){
		    iSessionMaxNr = aConfig.getElemAttrInt( aRoot, "session_max");
		    log( " session max nr = " + iSessionMaxNr);
		}
		createSights();
		log( "SessionManager started");
    }
	public void sessionCreated( HttpSessionEvent event) 
	{
		if( isDebug( 98) ){
			try{
				HttpSession Session = event.getSession();
				log( "The Session Listener got start! " + Session.getId());
				log( "Login: " + event.toString());
			}catch( Throwable e ){
				log( e);
			}
		}
	}
	public void setSight( HttpSession aSession, Sight aSight)
	{
		String sSessionID = aSession.getId();
		if( sSessionID == null || sSessionID.length() == 0) 
			log( "setSight no: session ID is empty");
		else if( aSight == null ) log( "setSight no: Sight is null");
		else{
			aSight.setSessionID( sSessionID);
			aSights.put( sSessionID, aSight);
			aSession.setAttribute( "or", sSessionID);
			if( isDebug( 98) ) log( "setSight done for SessionID=" + sSessionID + " " + 
				aSight.getUserName());
		}
	}
	private Sight getSight( String sSessionID)
	{
		if( aSights == null ) return null;
		Sight aSight = ( Sight)aSights.get( sSessionID); 
		if( isDebug( 99) )
			if( aSight != null ) aSight.log( "get Sight by ID" + sSessionID);
			else log( "no Sight by ID" + sSessionID);
		return aSight;
	}
	public synchronized  Sight getSight( HttpSession aSession)
	{ 
		String sSessionID = aSession.getId(); 
		Sight aSight = getSight( sSessionID);
		if( aSight == null ){
			String sOldSessionID = null;
			try {
				sOldSessionID = ( String)aSession.getAttribute( "or");
			} catch (Exception e) {
//				log( e); ei saa muidu kontrollida, et kas session is inavalid !?
			}
			if( sOldSessionID != null ){
				if( sSessionID.equalsIgnoreCase( sOldSessionID)){ // Session closed
					if( isDebug( 95) ) log( "session is already closed: " + sSessionID);
					aSession.removeAttribute( "or");
					aSight = null;
				}else{
					aSight = getSight( sOldSessionID);
					if( aSight != null ){
						aSession.setAttribute( "or", sSessionID);
						aSights.remove( sOldSessionID);
						aSights.put( sSessionID, aSight);
						if( isDebug( 98) ) 
							log( "session ID is changed " + sSessionID + " != " + sOldSessionID);
					}else{ // this is impossible
						log( "session is empty : " + sSessionID);
						aSession.removeAttribute( "or");
						aSight = null;
					}
				}
			}
		}
		return aSight; 
	}
	public Sight getSight( Thread aThread) {
		if( aSights != null ){
			Object[] aAllSights = ( aSights.entrySet().toArray());
			if( aAllSights != null )
				for( int i = aAllSights.length; --i >= 0;){
					@SuppressWarnings("rawtypes")
					Sight aSight = ( Sight) (( Map.Entry)aAllSights[i]).getValue();
					if( aSight.getThread() == aThread )	return aSight;
				}
		}
		return null;
	}
	private void closeSight( String sSessionID, boolean bClose) 
	{
		if( sSessionID == null || sSessionID.length() == 0) 
			log( "closeSight no: sessionID is empty");
		else{
			Sight aSight = ( Sight)aSights.remove( sSessionID);
			if( bClose )
			if( aSight != null){
				try{
					aSight.close();
				}catch( Exception aE ){
					log( aE);
				}
	        	if( isDebug( 98) ) log( "closeSight done for SessionID=" + sSessionID);
			}else if( isDebug( 95) ) log( "closeSight: no Sight for SessionID=" + sSessionID);
		}
	}
	public void closeSight( HttpSession aSession, boolean bClose)
	{
		if( aSession == null ) log( "closeSight no: Session is null");
		closeSight( aSession.getId(), bClose);
		aSession.removeAttribute( "or");
		aSession.invalidate();
	}
	public void closeSight( HttpSession aSession)
	{
		closeSight( aSession, true);
	}
	public void sessionDestroyed( HttpSessionEvent event) 
	{
		try{
			HttpSession aSession = event.getSession();
			Sight aSight = getSight( aSession);
			if( aSight != null ){
				aSight.close();
				closeSight( aSession, false);
				log( 95, "The Session Listener destroy starts for " + aSight.getUserName());
			}else{
				log( 95, "The Session Listener destroy starts! No Sight?");
				log( 95, "    ID=" + aSession.getId());
			}
		}catch( Throwable e ){
			log( e);
		}
	}
	private synchronized void closeTimeoutSights( long lSTime) 
	{
		if( aSights == null ) return;
		Object[] Sessions = aSights.keySet().toArray();
		if( Sessions != null ) for( int i = Sessions.length; --i >= 0;){
			String sSessionID = ( String)Sessions[i];
			Sight aSight = ( Sight)aSights.get( sSessionID);
			aSight.createLogCat();
			if( lSTime == 0 ||
				iSessionTimeout > 0 &&
				(lSTime - aSight.getLastTime()) >= iSessionTimeout * 60 * 1000 ) 
					closeSight( sSessionID, true);
		}
	}
	public void closeTimeoutSights() 
	{
		closeTimeoutSights( (new Date()).getTime());
	}
	public synchronized void closeSights() 
	{
		closeTimeoutSights( 0);
	}
	public void contextDestroyed( ServletContextEvent event) 
    {
    	closeSights();
//		if( aLogTask != null ) aLogTask.exit();
//		aLogTask = null;
//    	System.out.println( " is Destroyed");
    	log( "Session Manager destroyed");
    }

	private void log( String sMsg)
	{
		if( aServlet != null ) aServlet.final_log( sMsg);
	}
	private void log( int iDebug, String sMsg)
	{
		if( aServlet != null && aServlet.isDebug( iDebug)) aServlet.final_log( sMsg);
	}
	private boolean isDebug( int iDebug)
	{
		return ( aServlet != null )? aServlet.isDebug( iDebug): true;
	}
	private void log( Throwable E){ 
		if( aServlet != null ) aServlet.final_log( E);
	}
	public ArrayList<Sight> getSights()
	{
		if( aSights != null ){
			ArrayList<Sight> aSightsList = new ArrayList<Sight>();
			@SuppressWarnings("unchecked")
			Map.Entry<String,Sight> [] aAllSights = ( Map.Entry<String,Sight> [])( aSights.entrySet().toArray());
			if( aAllSights != null ){
				for( int i = aAllSights.length; --i >= 0;){
					Sight aSight = ( Sight) (( Map.Entry<String,Sight>)aAllSights[i]).getValue();
					if( aSight != null ) aSightsList.add( aSight);
				}
				return aSightsList;
			}
		}
		return null;
	}
}
