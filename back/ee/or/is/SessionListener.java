/*
 * 
 */
package ee.or.is;

import javax.servlet.http.*;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving session events.
 * The class that is interested in processing a session
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSessionListener<code> method. When
 * the session event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SessionEvent
 */
public class SessionListener implements HttpSessionListener{
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent event) {
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event)
	{
	    try {
	    	Log.info( "sessionDestroyed");
/*	    	HttpSession Session = event.getSession();
		    Sight MySight = ISServlet.getSightReq( Session);
			if( MySight != null ) MySight.close( Session); */
	    } catch( SecurityException e ) {}
	}
}
