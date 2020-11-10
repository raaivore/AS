/*
 * Created on 21.10.2006 by OR
 *
 */
package ee.or.is;

// TODO: Auto-generated Javadoc
/**
 * The Interface ThreadLog.
 * @author or
 * 
 */
public interface ThreadLog 
{
    
    /**
     * Log.
     * 
     * @param sMsg the s msg
     */
    public void log( String sMsg);
    
    /**
     * Log.
     * 
     * @param iDebug the i debug
     * @param sMsg the s msg
     */
    public void log( int iDebug, String sMsg);
    public boolean isDebug( int iDebug);
//    public void log( StackTraceElement[] Trace);
    /**
     * Log.
     * 
     * @param E the e
     */
    public void log( Throwable E);
    /**
     * Gets the log name.
     * 
     * @return the log name
     */
    public String getLogName();
	public void setLogName( String sLogName);
}
