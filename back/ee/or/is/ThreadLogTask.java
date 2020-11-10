package ee.or.is;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ThreadLogTask extends ThreadLogImpl 
{
	private List<String> aLogs = null;
	private PrintStream aPW = null;
	private boolean bRun = true;
	private boolean bWorking = true;		// kui puhver saab liiga t�is, siis l�heb false
	private boolean bMain = true;			// servlet-i enda logi
	
 	public ThreadLogTask( ISServlet aServlet)
	{
 		super( aServlet);
 		init( aServlet.getLogName());
 	}
 	public ThreadLogTask( ISServlet aServlet, String sLogName)
	{
 		super( aServlet);
 		bMain = false;
 		init( sLogName);
	}
	public void setLogName( String sLogName)
	{	
		super.setLogName( sLogName);
		close();
	}
 	public void init( String sLogName)
 	{
 		if( sLogName != null){
 //			if( !bMain ) 
 			setName( "Log_" + sLogName);
 			if( bMain ) ;
 			else setLogName( sLogName);
 		}
 		aLogs = new ArrayList<String>( 100);
// 		aLogs = Collections.synchronizedList( aLogsArray);
 	}
 	public synchronized void log( String sMsg)
    {
 		try{
			if( aLogs.size() <= 1000 ){
				aLogs.add( GlobalDate.getTimeMS() + " " + sMsg);
			}else if( bWorking ){
				getServlet().super_log( getName() + " is full!");
				bWorking = false;
			}
			if( aLogs.size() > 100 ) interrupt();
		}catch( Exception aE ){
			getServlet().super_log( aE);		
		}
    }
    public synchronized void log( Throwable aE)
	{
		if( aLogs.size() <= 500 ){
			if( aE instanceof ExceptionIS){
				if( (( ExceptionIS)aE).sError != null ) aLogs.add( (( ExceptionIS)aE).sError);
			}
			aLogs.add( aE.getLocalizedMessage());
			aLogs.add( aE.getMessage());
			StackTraceElement[] Trace = aE.getStackTrace();
            if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
              	aLogs.add( Trace[ i].toString());
			if( aE.getCause() != null ){
				aLogs.add( "Cause ...");
				aE = aE.getCause();
				aLogs.add( aE.getLocalizedMessage());
				aLogs.add( aE.getMessage());
				Trace = aE.getStackTrace();
	            if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
	              	aLogs.add( Trace[ i].toString());
			}
 		}else if( bWorking ){
 			getServlet().super_log( getName() + " is full!");
 			bWorking = false;
		}
		interrupt();
	}
    private synchronized String get()
    {
    	return ( String)aLogs.remove( 0);
    }
	public void run( )
   	{
		setPriority( MIN_PRIORITY);
		error_log( " Log Task " + getLogName() + " started"); 
		long lLastTime = 0;
		try{
			while( bRun) {
	            try{
	            	if(  GlobalDate.isNewDay( lLastTime)){ // 31.01.2012 see oli v�lja kommenteeritud!(?)
	            		close();
	            		if( bMain ) getServlet().reload(); //.createLogCatalog();
	            	}
	            	lLastTime = GlobalDate.getCurrentDate().getTime();
					if( aPW == null && !aLogs.isEmpty() ){
						String sLogFileName = getLogFileName(); 
						if(  sLogFileName != null )	aPW = Log.getLogStream( sLogFileName);
					}
					if( aPW != null){
						int iCount = 0;
						while( !aLogs.isEmpty()){
							String sMsg = get();
							if( sMsg != null){
								Log._log( aPW, sMsg);
								++iCount;
							}
						}
						if( iCount > 0 ){
							aPW.flush();
							if( aLogs.isEmpty() && !bWorking) bWorking = true;
						}
/*						synchronized( aLogs){
							aLogs.trimToSize();
						}*/
					}
//	            	getServlet().doTask();
					doTask(); // sellele on üles ehitatud DynKeeper mäluvabastus
					if( bRun ){
						Thread.sleep( 60000);
		            	if( bMain ) getServlet().doTask();
					}
					if( aLogs.isEmpty() ){
						close();
					}
	            } catch (InterruptedException aE) {
	            } catch( Throwable aE ){
	            	if( bWorking ){
	            		error_log( aE);
	            		bWorking = false;
	            		close();
	            	}
            		try{
						Thread.sleep( 60000);
					}catch( InterruptedException aE1 ){
					}
				}
			}
		}catch( Throwable aE ){
			getServlet().super_log( aE);
		} finally{
			close();
		}
//        error_log( " Log Task " + getLogName() + " ended"); 
	}
	public boolean exit() 
	{
		log( " Log Task " + getLogName() + " exit start");
		flush();
		bRun = false;
   		try {
            interrupt();
            Thread.sleep( 100);
        } catch( InterruptedException aE) {
        } catch( Throwable aE) {
			getServlet().super_log( aE);
        }
   		getServlet().super_log( " Log Task " + getLogName() + " exit ends " + !isAlive());    	
   		return !isAlive();
	}
	public void flush()
	{
		int i = 10;
		while( aPW != null && !aLogs.isEmpty()){
			try {
				interrupt();
				Thread.sleep( 1000);
				getServlet().super_log( "Wait");
			} catch( Throwable aE) {
				getServlet().super_log( aE);
			}
			if( --i < 0 ) break;
		}
	}
	public void close()
	{
		if( aPW != null){
			aPW.flush();
			aPW.close();
			aPW = null;
		}
	}
	public void error_log( String sMsg){
 //       if( bMain ) getServlet().final_log( sMsg);
 //       else 
        	getServlet().final_log( sMsg);
	}
	public void error_log( Throwable aE){
//        if( bMain ) getServlet().final_log( aE);
//        else 
        	getServlet().final_log( aE);
	}
	public void setExit(){ bRun = false;}
	public boolean isRunning(){ return bRun;}
	public void doTask() 
	{
	}
}
