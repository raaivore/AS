/*
 * 
 */
package ee.or.is;

// TODO: Auto-generated Javadoc
// import java.lang.*;

/**
 * The Class MException.
 * 
 * @author toivo
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MException extends ExceptionIS {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The Msg. */
	StringBuffer Msg = null;
	/**
	 * Constructor for MException.
	public MException() {
		super();
	}
	 */

	/**
	 * Constructor for MException.
	 * 
	 * @param msg the msg
	 */
	public MException(String msg) {
		super( msg);
		Msg = new StringBuffer( msg);
Log.info( Msg.toString());
	}
	
	/**
	 * Constructor for MException.
	 * 
	 * @param cause the cause
	 * @param msg the msg
	 */
	public MException(String msg, Throwable cause) {
		super(msg, cause);
		Msg = new StringBuffer( msg);
		Log.info( toString());
		Log.info( cause);
	}
	/**
	 * Instantiates a new m exception.
	 * 
	 * @param code the code
	 * @param msg the msg
	 */
	public MException( String code, String msg) {
		super(msg);
		Msg = new StringBuffer( "");
		if( code != null ) Msg.append( code + " ");
		if( msg != null ) Msg.append( msg);
Log.info( Msg.toString());
//		status = new Status();
		// Lisame esimese teate teadete hoidlasse.
//		status.add( code, msg, null, null);
	}
	
	/**
	 * Instantiates a new m exception.
	 * 
	 * @param code the code
	 * @param msg the msg
	 * @param var1 the var1
	 * @param var2 the var2
	 */
	public MException( String code, String msg, String var1, String var2) {
		super(msg);
		Msg = new StringBuffer( "");
		if( code != null ) Msg.append( code + " ");
		if( msg != null ) Msg.append( msg + " ");
		if( var1 != null ) Msg.append( var1 + " ");
		if( var2 != null ) Msg.append( var2 + " ");
Log.info( Msg.toString());
//		status = new Status();
		// Lisame esimese teate teadete hoidlasse.
//		status.add( code, msg, var1, var2);
	}

	/**
	 * Constructor for MException.
	 * 
	 * @param cause the cause
	 */
	public MException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Instantiates a new m exception.
	 * 
	 * @param code the code
	 * @param msg the msg
	 * @param var1 the var1
	 * @param var2 the var2
	 * @param cause the cause
	 */
	public MException( String code, String msg, String var1, String var2, Exception cause) {
		super( msg, cause);
		Msg = new StringBuffer( "");
		if( code != null ) Msg.append( code + " ");
		if( msg != null ) Msg.append( msg + " ");
		if( var1 != null ) Msg.append( var1 + " ");
		if( var2 != null ) Msg.append( var2 + " ");
		if( cause != null ) Msg.append( cause.getMessage());
Log.info( Msg.toString());
//		status = new Status();
		// Lisame esimese teate teadete hoidlasse.
//		status.add( code, msg, var1, var2);
	}
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	public String toString()
	{
		return (Msg!=null)? Msg.toString(): super.toString();		
	}

}
