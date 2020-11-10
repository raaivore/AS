package ee.or.is;

import java.io.File;
import java.io.PrintStream;

/**
 *  @author or 16.12.2009
 */

//import java.io.PrintStream;

public class ThreadLogImpl extends Thread implements ThreadLog  
{
 	private int iDebug = -1;
	public boolean isDebug( int iDebug){ return this.iDebug >= iDebug;}
	public int getDebug(){ return iDebug;}
	public void setDebug(int debug){ iDebug = debug;}
	
	private ISServlet aServlet = null;
	public ISServlet getServlet(){	return aServlet;}
	public void setServlet(ISServlet servlet){ aServlet = servlet;}

	private boolean bNewLog = true;
	public boolean isNewLog(){ return bNewLog;}
	public void setNewLog( boolean bNewLog){ this.bNewLog = bNewLog;}

	private String sLogName = null;
	public boolean hasLogName(){ return sLogName != null;}
	public String getLogName(){	return ( sLogName != null)? sLogName: getName();}
	public void setLogName( String sLogName){	if( sLogName != null ) setLogFileName( sLogName);}
	private String sLogCatName = null;
	public String getLogCatName(){	return ( sLogCatName != null)? sLogCatName: getName();}
	public void setLogCatName( String sLogCatName){	this.sLogCatName = sLogCatName;}
	public void setLogCatName( File aCat){	this.sLogCatName = aCat.getPath();}
	public boolean hasLogCatName(){ return sLogCatName != null;}
	public String getLogFileName()
	{
		return getLogFileName( sLogName);
	}
	public String getLogFileName( String sLogName)
	{	
		return ( sLogCatName != null)? ( sLogCatName + "/" + sLogName):
				(( sLogName == null)? aServlet.getLogFileName(): aServlet.getLogFileName( sLogName));
//		if( sLogName != null ) aServlet.setDefaultLogFileName( sLogName);
//		return aServlet.getLogFileName();
	}
	public PrintStream getLogStream() throws Exception
    {
		return Log.getLogStream( getLogFileName());
    }
	public PrintStream getLogStream( String sFileName) throws Exception
    {
		return Log.getLogStream( getLogFileName( sFileName));
    }
	public void clearLogName()
	{
		sLogCatName = null;
		sLogName = null;		
	}
	public void setLogFileName( String sLogFileName) 
	{
		sLogName = getLogFileName( sLogFileName, -1);
		if( bNewLog && sLogName != null ){ 
			try{
				for( int i = 0;; ++i){
					File LogFile = new File( getLogFileName());
					if( !LogFile.exists() )	break;
					sLogName = getLogFileName( sLogFileName, i);
				}
			}catch( Exception E ){
				log( E);
				sLogName = null;
			}
		}
	}
	public String getLogFileName( String sLogFileName, int i) 
	{
		StringBuffer FileName = new StringBuffer( sLogFileName);
		if( i >= 0 ){
			FileName.append( "_");
			FileName.append( i);
		}
		FileName.append( ".txt");
		return FileName.toString();
	}
	public ThreadLogImpl( ISServlet aServlet)
	{
 		this.aServlet = aServlet;
 		iDebug = aServlet.getDebug();
	}
 	public void log( String sMsg)
    {
        String sFileName = getLogFileName(); //aServlet.getLogFileName( getLogName() + ".txt");
		Log.logFile( sFileName, sMsg);
    }
	public void log( int iDebug, String sMsg)
    {
		if( isDebug( iDebug) ) log( sMsg);
    }
    public void log( Throwable E)
	{
        String sFileName = getLogFileName();
        Log.logFile( sFileName, E);
        aServlet.final_log( "Exception: " + E.getMessage() + " in " + getLogName());
	}
}
