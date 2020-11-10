/*
 * 
 */
package ee.or.is;

import java.io.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class Log.
 * 
 * @author or
 */
public class Log {
    /** The Servlet. */
    private static ISServlet Servlet = null;
    public static ISServlet getServlet(){	return Servlet;}
    public static ISServlet setServlet( ISServlet servlet){ 
    	ISServlet aOldServlet = Servlet;
    	if( aOldServlet == null || servlet.isMain() ) 
    		Servlet = servlet;
    	return Servlet;
    }
    public static boolean hasServlet(){	return Servlet != null;}
    
    public static void info( int iDebug, String sMsg){ 		info( iDebug, null, sMsg); }
    public static void info( int iDebug, String sUser, String sMsg){ if( isDebug( iDebug)) info( sUser, sMsg);}
    public static void log( int iDebug, String sMsg){ if( isDebug( iDebug)) log( sMsg);}
    public static void info( String sUser, String sMsg){ 	
    	log( (( sUser != null )? sUser + " : ": "") + sMsg);}
    public static void info( String sMsg){ 					log( ( Sight)null, sMsg);}
    public static void log_error( String sMsg){				error( null, sMsg, true); }
    public static void error( String sMsg){ 				error( null, sMsg, false);}
    public static void log( String sMsg){ 					log( ( Sight)null, sMsg);}

    public static void log_error( Throwable E, boolean bCur) {	error( null, E, bCur);}
    public static void log_error( Throwable E){ 			error( null, E, false);}
    public static void error( Throwable E, boolean bCur){ 	error( null, E, bCur);}
    public static void error( Throwable E){ 				error( null, E, true);}
    public static void log( Throwable E){ 					error( null, E, true);}
    public static void info( Throwable E){ 					error( null, E, false);}
    
    public static void error( Sight aSight, Throwable aE, boolean bCur) 
    {
    	error( Servlet, aSight, aE, bCur);
    }
    public static void error( HttpServlet Servlet, Sight aSight, Throwable aE, boolean bCur) 
    {
    	if( aSight != null && bCur ) aSight.log( aE);
    	else if( bCur ){
    	    Thread T = Thread.currentThread();
    	    if( T instanceof ThreadLog )(( ThreadLog)T).log( aE);
    	}
    	if( Servlet != null ){
    	    if( Servlet instanceof ISServlet ) (( ISServlet)Servlet).final_log( aE);
    	    else{
    	    	Servlet.log( aE.getLocalizedMessage());
    	    	Servlet.log( aE.getMessage());
                StackTraceElement[] Trace = aE.getStackTrace();
                if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
                	Servlet.log( Trace[ i].toString());
    	    }
    	}
    }
    public static void error( Sight aSight, String sMsg, boolean bCur) 
    {
    	if( aSight != null && bCur ) aSight.log( sMsg);
    	else if( bCur ){
    	    Thread T = Thread.currentThread();
    	    if( T instanceof ThreadLog )(( ThreadLog)T).log( sMsg);
    	}
    	if( Servlet != null ){
    	    if( Servlet instanceof ISServlet ) (( ISServlet)Servlet).final_log( sMsg);
    	    else Servlet.log( sMsg);
    	}
    }
    public static void error( String sMsg, boolean bCur) 
    {
    	error( null, sMsg, bCur);
    }
    public static boolean isDebug( int iDebug) 
    {
    	Thread T = Thread.currentThread();
    	if( T instanceof ThreadLog ) return (( ThreadLog)T).isDebug( iDebug);
    	return (Servlet != null)? Servlet.isDebug( iDebug) : true;
    }
    public static void log( Sight aSight, String sMsg) 
    {
    	if( aSight == null && Servlet instanceof ISServlet ){
    		Thread T = Thread.currentThread();
	    	aSight = (( ISServlet)Servlet).getSight( T);
    	}
    	final_log( aSight, sMsg);
    }
    public static void final_log( Sight aSight, String sMsg) 
    {
    	if( aSight != null ) aSight.log( sMsg);
    	else{
        	Thread T = Thread.currentThread();
        	if( T instanceof ThreadLog ) (( ThreadLog)T).log( sMsg);
        	else if( Servlet != null ){
        	    if( Servlet instanceof ISServlet ){
        	    	(( ISServlet)Servlet).final_log( sMsg);
        	    }else Servlet.log( sMsg);
        	}
    	}
    }
    public static void logFile( String sFileName, Throwable aE) 
    {
		try {
			_log( sFileName, aE);
		}catch( Throwable aE2) {
			Servlet.final_log( aE2);
		}
    }
    private static synchronized void _log( String sFileName, Throwable aE) throws Throwable
    {
        PrintStream aPW = null;
		try {
            aPW = getLogStream( sFileName);
            if( aPW != null){
    			if( aE instanceof ExceptionIS){
    				if( (( ExceptionIS)aE).sError != null ) _log( aPW, (( ExceptionIS)aE).sError);
    			}
                _log( aPW, aE.toString());
 //               _log( aPW, aE.getLocalizedMessage());
                _log( aPW, aE.getMessage());
                StackTraceElement [] Trace = aE.getStackTrace();
                if( Trace != null ){
                    int n = ( Trace.length > 6 )? 6: Trace.length;
                    if( Trace != null ) for( int i = 0; i < n; ++i) 
                        _log( aPW, Trace[ i].toString());
                }
            }
        } finally{
        	if( aPW != null) aPW.close();
        }
    }
	public static PrintStream getLogStream( String sFileName) throws Exception
    {
		File aFile = new File( sFileName);
		if( aFile.exists() ){
			if( aFile.length() >= 100000000L ) return null;
		}else{
			int i = sFileName.lastIndexOf( '/');
			File aCat = new File( sFileName.substring( 0, i));
			if( !aCat.exists() ) aCat.mkdir();
		}
		return new PrintStream( new FileOutputStream( aFile, true), true, "UTF-8");
    }    
	public static void logFile( String sFileName, String sMsg) 
	{
		try {
			_logFile( sFileName, sMsg);
		}catch( Throwable aE) {
			Servlet.final_log( aE);
		}
	}
	private static void _logFile( String sFileName, String sMsg) throws Throwable
    {
		PrintStream aPW = null;
		try {
			aPW = getLogStream( sFileName);
            if( aPW != null) _log( aPW, sMsg);
	    } finally{
	       	if( aPW != null) aPW.close();
	    }
    }
    public static void _log( PrintStream PW, String sMsg) throws Throwable 
    {
    	if( sMsg != null){
    	    int n = sMsg.length();
	    	int i = sMsg.indexOf( '\n');
	    	if( i < 0 ) i = n;
	    	if( i > 200 ) i = 200;
	    	PW.println( GlobalDate.getTime() + (( i>0)? " " + sMsg.substring( 0, i): ""));
	    	for( ; i < n; ){
	    		if( sMsg.charAt( i) == '\n') ++i;
	   	    	int e = sMsg.indexOf( '\n', i);
	   	    	if( e > 0 ){
	   	    		if( e - i > 200 ) e = i + 200;
    	    		PW.println( "       " + (( e>i)? sMsg.substring( i, e): ""));
    	    		i = e;
	   	    	}else{
    	   	    	for( ; i < n; i += 200){
    	   	    		if( i + 200 < n ) PW.println( "       " + sMsg.substring( i, i + 200));
    	   	    		else PW.println( "       " + sMsg.substring( i));
    	   	    	}
    	   	    	break;
	   	    	}
	    	}
    	}
    }
	public static void log( HttpServletRequest Request, String sMsg)
	{
		Sight aSight = null;
		try{
			aSight = ( Sight)Servlet.getSight( Request);
		}catch( Throwable aE ){
		}
		log( aSight, sMsg);
	}
}
