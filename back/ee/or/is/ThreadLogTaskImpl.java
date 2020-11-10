package ee.or.is;

import java.io.File;

public class ThreadLogTaskImpl extends Thread implements ThreadLog
{
	private ISServlet aServlet = null;
	public ISServlet getServlet(){	return aServlet;}
	private ThreadLogTask aLogTask = null;
	public void setLogName( String sLogName){	
		aLogTask.setLogName( sLogName);
		aLogTask.setName( "Log_" + sLogName);
	}
	public void setLogCatName( String sLogCatName){	aLogTask.setLogCatName( sLogCatName);}
	public void setLogCatName( File aCat){	aLogTask.setLogCatName( aCat);}
 	public ThreadLogTaskImpl( ISServlet aServlet)
	{
 		this.aServlet = aServlet;
		aLogTask = new ThreadLogTask( aServlet);
		aLogTask.start();
	}
	public void log( String sMsg){ aLogTask.log( sMsg);}
    public void log( Throwable aE)
	{
        aLogTask.log( aE);
        aServlet.final_log( "Exception: " + aE.getMessage() + " in " + getLogName());
	}
	public void log( int iDebug, String sMsg){ aLogTask.log( iDebug, sMsg); }
	public boolean isDebug( int iDebug){	return aServlet.isDebug(iDebug);}
	public String getLogName() {	return aLogTask.getLogName();}
	public String getLogFileName()
	{	
		return aLogTask.getLogFileName();
	}
	public boolean exit() 
	{
		return aLogTask.exit();
	}
}
