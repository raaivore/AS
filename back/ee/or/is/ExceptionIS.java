/*
 * 
 */
package ee.or.is;

import org.w3c.dom.Node;

// import java.lang.*;

/**
 * The Class ExceptionIS.
 * 
 * @author or in 08.07.2009 See on uus vigadest teavitamise klass, et k�rgemal
 *         tasemel saaks neid edasi t��delda Esialgse skeemi j�rgi on vea
 *         p�hjuseks mingi Exception ( v�i error) v�i kasutaja poolne kas
 *         lihtsalt teade v�i koos veakoodiga
 */
public class ExceptionIS extends Exception 
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The error code */
	int					   iErrorCode	   = 0;
	public int getErrorCode() {	return iErrorCode;}

	/** The error msg ( format) */
	String					sError		   = null;
	/** The error 1. parameter */
	String					sPar1			= null;
	/** The error 2. parameter */
	String					sPar2			= null;
	/** The error 3. parameter */
	String					sPar3			= null;

	/**
	 * Constructor for ExceptionIS.
	 * 
	 * @param msg
	 * 
	 */
	public ExceptionIS(String msg) {
		super( msg);
	}

	/**
	 * Constructor for ExceptionIS.
	 * 
	 * @param cause
	 *            - the cause
	 */
	public ExceptionIS( Throwable cause) {
		super( cause);
		setStackTrace( cause.getStackTrace());
	}

	/**
	 * Constructor for ExceptionIS.
	 * 
	 * @param msg
	 *            - the msg
	 * @param cause
	 *            - the cause
	 */
	public ExceptionIS(Throwable cause, String msg) {
		super( cause);
		sError = msg;
	}
	public ExceptionIS( String msg, Throwable cause) {
		super( cause);
		sError = msg;
	}

	/**
	 * Instantiates a new exception.
	 * 
	 * @param iErrorCode
	 *            the code
	 * @param msg
	 *            the msg
	 */
	public ExceptionIS(int iErrorCode, String msg) {
		super( msg);
		this.iErrorCode = iErrorCode;
	}

	/**
	 * Instantiates a new exception.
	 * 
	 * @param iErrorCode
	 *            the code
	 * @param msg
	 *            the msg
	 */
	public ExceptionIS(int iErrorCode, String sFormat, String sPar1) {
		super();
		init( null, iErrorCode, sFormat, sPar1, null, null);
	}

	public ExceptionIS(int iErrorCode, String sFormat, String sPar1, String sPar2) {
		super();
		init( null, iErrorCode, sFormat, sPar1, sPar2, null);
	}

	public ExceptionIS(int iErrorCode, String sFormat, String sPar1, String sPar2, String sPar3) {
		super();
		init( null, iErrorCode, sFormat, sPar1, sPar2, sPar3);
	}

	public ExceptionIS(Throwable aCause, String sFormat, String sPar1) {
		super();
		init( aCause, -1, sFormat, sPar1, null, null);
	}

	public ExceptionIS(Throwable aCause, String sFormat, String sPar1, String sPar2) {
		super();
		init( aCause, -1, sFormat, sPar1, sPar2, null);
	}

	public ExceptionIS(Throwable aCause, String sFormat, String sPar1, String sPar2, String sPar3) {
		super();
		init( aCause, -1, sFormat, sPar1, sPar2, sPar3);
	}

	private void init( Throwable aCause, int iErrorCode, String sFormat, String sPar1,
			String sPar2, String sPar3) {
		this.iErrorCode = iErrorCode;
		this.sError = sFormat;
		this.sPar1 = sPar1;
		this.sPar2 = sPar2;
		this.sPar3 = sPar3;
		String sMsg = GlobalData.getFormatedString( sFormat, sPar1, sPar2, sPar3);
		this.initCause( (aCause != null)? new Exception( sMsg, aCause) : new Exception( sMsg));
	}

	public DOMData getDOMDoc( Sight aSight) throws ExceptionIS {
		DOMData Doc = aSight.getTemplate();
		Node aRoot = Doc.getRootNode();
		if( iErrorCode != 0 )
			Doc.addChildNode( aRoot, "error_nr", iErrorCode);
		Doc.addChildNode( aRoot, "error", getMessage());
		if( sError != null )
			Doc.addChildNode( aRoot, "error_format", sError);
		if( sPar1 != null )
			Doc.addChildNode( aRoot, "error_par1", sPar1);
		if( sPar2 != null )
			Doc.addChildNode( aRoot, "error_par2", sPar2);
		if( sPar3 != null )
			Doc.addChildNode( aRoot, "error_par3", sPar3);
		if( getCause() != null )
			Doc.addChildNode( aRoot, "error_exception", getCause().getClass().getName());
		Doc.setFileXSL( aSight, "ErrorForm");
		return Doc;
	}
}
