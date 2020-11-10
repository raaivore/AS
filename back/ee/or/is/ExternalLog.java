package ee.or.is;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.w3c.dom.Node;

public class ExternalLog extends ThreadLogImpl 
{
	private Sight aSight = null;
	private InputStream aIn = null;
	private ThreadLog aThread = null;
//	private StringBuffer aProgress = null;
	
 	public ExternalLog( Sight aSight, String sLogName, InputStream aIn)
	{
 		super( aSight.getServlet());
		this.aSight = aSight;
		setLogName( sLogName);
 		this.aIn = aIn;
// 		aProgress = new StringBuffer();
 		start();
	}
 	public ExternalLog( ISServlet aServlet, String sLogName, InputStream aIn)
	{
 		super( aServlet);
 		setLogName( sLogName);
 		this.aIn = aIn;
// 		aProgress = new StringBuffer();
 		start();
	}
 	public ExternalLog( ISServlet aServlet, ThreadLog aThread, InputStream aIn)
	{
 		super( aServlet);
 		this.aThread = aThread;
 		this.aIn = aIn;
 		start();
	}
	public void run() 
	{
		log( "Start logging " + getLogName());
		try {
			BufferedReader aBR = new BufferedReader( new InputStreamReader( aIn));
			String sLine = null;
			while( (sLine = aBR.readLine()) != null) {
				if( aSight != null ) aSight.addProgress( sLine);
				log( sLine);
			}
		} catch( Exception aE) {
			if( aE.getMessage().indexOf( "closed") < 0 )
				log( aE);
		} 
		log( "End logging "  + getLogName());
	}
    public void log( Throwable aE)
	{
    	if( aThread != null ) aThread.log( aE);
    	else if( aSight != null ) aSight.log( aE); 
    	else super.log(  aE);
	}
	public void log( String sMsg)
	{
		if( aThread != null ) aThread.log( sMsg);
		else if( aSight != null ) aSight.log( sMsg); 
		else super.log(  sMsg);
	}
	public void setXML( DOMData aDoc, Node aRoot)
    {
//		aDoc.addChildNode( aRoot, getLogName(), aProgress.toString());
    }
}
