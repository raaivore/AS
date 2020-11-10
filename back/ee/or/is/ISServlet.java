/*
 * 
 */
package ee.or.is;

/**
 *  @author or 26.12.2004
 */
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
//import org.w3c.dom.Node;
import java.util.*;

import org.apache.catalina.Realm;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.realm.LockOutRealm;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.rowset.serial.SerialBlob;

//import org.apache.catalina.core.ApplicationContext;
//import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.coyote.ActionCode;
import org.imgscalr.Scalr;
import org.w3c.dom.Node;

import ee.or.db.*;

/**
 * The Class ISServlet.
 */
public class ISServlet extends HttpServlet
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The appconfig. */
	protected Config		  appconfig	= null;	
	public boolean isTest(){ return appconfig.isTest();}
	public boolean isDevelop(){ return appconfig.isDevelop();}
	public static String	 approot = null;// public seet�ttu, et mti saaks t��tada
	// private String basedir;
	// public String sURL = null;
	/** The s config. */
	String sConfig	= "config.xml";
	

	/** The l last time. */
	private long			  lLastTime		= 0;		   // last 00:00
	/** The l s time. */
	// private long lSTime = 0; // operation
	// start

	/** The i debug. */
	private int			   iDebug		   = 0;

	private boolean bMain = true;
	public boolean isMain(){ return bMain;}
	
	private ThreadLogTask aLogTask = null;
	public ThreadLogTask getThreadLog()
	{
		return aLogTask;
	}
	/**
	 * Gets the debug.
	 * 
	 * @return the debug
	 */
	public int getDebug() {
		return iDebug;
	}

	/**
	 * Sets the debug.
	 * 
	 * @param debug
	 *            the new debug
	 */
	public void setDebug( int debug) {
		iDebug = debug;
	}

	/**
	 * Checks if is debug.
	 * 
	 * @param iDebug
	 *            the i debug
	 * 
	 * @return true, if is debug
	 */
	public boolean isDebug( int iDebug) {
		return this.iDebug >= iDebug;
	}
	public double getInitParameterDouble( String sParamName) 
	{
		double d = 0;
		if( sParamName != null ){
			String s = getInitParameter( sParamName);
			if ( s != null){
				try {
					d = Double.parseDouble( s);
				} catch( NumberFormatException e) {
					log( "Vigane " + sParamName + "='" + s + "' , peab olema number");
				}
			}
		}
		return d;			
	}
	public int getInitParameterInt( String sParamName) 
	{
		int i = 0;
		if( sParamName != null ){
			String s = getInitParameter( sParamName);
			if ( s != null){
				try {
					i = Integer.parseInt( s);
				} catch( NumberFormatException e) {
					log( "Vigane " + sParamName + "='" + s + "' , peab olema number");
				}
			}
		}
		return i;			
	}
	public String getTempPath() 
	{
		return (appconfig != null)? appconfig.getTempPath(): getServletContext().getRealPath( "work");
	}
	public String getInitPath(  String sFileName) 
	{
		return (appconfig != null)? appconfig.getInitPath( sFileName): getServletContext().getRealPath( sFileName);
	}
	public String getTempPath( String sFileName)
	{
		return getTempPath() + "/" + sFileName;
	}

	/**
	 * Sets the config name.
	 * 
	 * @param sConfig
	 *            the new config name
	 */
	public void setConfigName( String sConfig) {
		this.sConfig = sConfig;
	}

	/**
	 * Gets the config name.
	 * 
	 * @return the config name
	 */
	public String getConfigName() {
		return sConfig;
	}

	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	public Config getConfig() {
		return appconfig;
	}
	public void setConfig( Config aNewConf) {
		appconfig = aNewConf;
	}

	/**
	 * Gets the real path.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the real path
	 */
	public static String getRealPath( String sFileName) {
		return approot + sFileName;
	}

	/**
	 * Gets the web path.
	 * 
	 * @return the web path
	 */
	public static String getWebPath() {
		return getRealPath( "WEB-INF/");
	}

	/**
	 * Gets the web path.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the web path
	 */
	public static String getWebPath( String sFileName) {
		return getRealPath( "WEB-INF/" + sFileName);
	}

	/**
	 * Gets the real path.
	 * 
	 * @return the real path
	 */
	public static String getRealPath() {
		return approot;
	}

	/**
	 * Gets the db desc.
	 * 
	 * @return the db desc
	 */
	public DbDescr getDbDesc() {
		return (appconfig == null)? null : appconfig.getDatabaseDescriptor();
	}

	/**
	 * Gets the db desc.
	 * 
	 * @param sName
	 *            the s name
	 * 
	 * @return the db desc
	 */
	public DbDescr getDbDesc( String sName) {
		return (appconfig == null)? null : appconfig.getDatabaseDescriptor( sName);
	}
	public int getDbsSize() { return (appconfig != null)? appconfig.getDbsSize(): 0;}
	/**
	 * Creates the config.
	 * 
	 * @param servconf
	 *            the servconf
	 */
	public void createConfig( ServletConfig servconf) throws ExceptionIS 
	{
		appconfig = new Config( this, servconf);
		// appconfig = new Config( getWebPath(), servconf, sConfig);
	}

	private String sXSLPath = null;
	public String getPathXSL(){	return sXSLPath;}

	public void setXSL( Sight aSight, DOMData aDoc, HttpServletRequest aRequest, String sFormat)
	{
		if( aDoc.getFileXSL() == null ){
			String sReq = getRequest( aRequest);
			aDoc.setFileXSL( aSight.getPathXSL(), (sFormat != null && !sFormat.equalsIgnoreCase( "html"))? sReq + sFormat.toUpperCase(): sReq);
			
		}
		if( (sFormat != null && !sFormat.equalsIgnoreCase( "html")) ) {
			if( hasParameter( aRequest, "FILE_NAME"))
				aDoc.setOutFileName( getParameterString( aRequest, "FILE_NAME"));
		}
	}
	private SessionManager aSessionManager = null;
	private ISServlet aMainServlet = null;
	public ISServlet getMainServlet(){ return aMainServlet;}
	public void resetServlet(){ Log.setServlet( aMainServlet);}
	public QService createQService(){ return new QService( this);}
	public static boolean isWindows(){
		String osname =	System.getProperty("os.name").toLowerCase();	// n�iteks "windows 98"
		return osname.indexOf( "windows") >= 0;
	}
	
	public void init( ServletConfig servconf) throws ServletException 
	{
		String sLogFile = servconf.getInitParameter( "log_file");
		if( sLogFile != null) {
			bMain = false;
			setDefaultLogFileName( sLogFile);
		}
//		if( !Log.hasServlet() )
		aMainServlet = Log.setServlet( this);
		// mitme servleti puhul on esimene peamine ehk n�iteks kogu anon��mne
		// logimine l�heb sinna
		aLogTask = new ThreadLogTask( this);
		super.init( servconf);
		approot = getServletContext().getRealPath( "/");
		if( approot != null ){
			int i = approot.length() - 1;
			if( approot.charAt( i) != '/' && approot.charAt( i) != '\\' )
				approot += '/';
		}else
			approot = "";
		
		try{
			final_log( "************** ISServlet init start ************************");
//			ServletContext aContext = servconf.getServletContext();
			String sConfigFile = servconf.getInitParameter( "config_file");
			if( sConfigFile != null) setConfigName( sConfigFile);
			createConfig( servconf);
			aLogTask.start();
			lLastTime = /* lSTime = */(new Date()).getTime();
			// iLastTime = ( int)( lSTime / (24 * 3600 * 1000));
//			String basedir = getWebPath(); // approot + "WEB-INF/";
//			final_log( "Config File: " + basedir + sConfig);
			if( appconfig != null ){
				appconfig.readConfig();
				sXSLPath = appconfig.getElemAttr( "xsl", "path");
				if( sXSLPath != null) log( " xsl path is " + sXSLPath);
				else{
					sXSLPath = servconf.getInitParameter( "xsl_path");
					if( sXSLPath != null) log( "xsl_path = " + sXSLPath);
				}
			}
		}catch( Throwable e ){
			try{
				super_log( "config: " + sConfig + " error");
				super_log( e);
			}catch( Throwable aE ){
				try{
					super_log( aE);
				}catch( Throwable aE1 ){
				}
			}
			throw new ServletException( "setup Config error", e);
		}
		loadSQLUpdates();
//		final_log( "Java Date Time is: " + GlobalDate.getCurrentDateTimeStringS());
//		Calendar DateS = GregorianCalendar.getInstance();
//		final_log( "TimeZone: " + DateS.getTimeZone().toString());
//		final_log( "Default locale: " + Locale.getDefault());
//		Locale.setDefault( new Locale( "et", "EE"));
//		final_log( "Default locale: " + Locale.getDefault());
		if( isMain() ){
			aSessionManager = ( SessionManager)servconf.getServletContext().getAttribute( "SessionManager");
			if( aSessionManager != null ) aSessionManager.init( this);
		}else
			aSessionManager = aMainServlet.aSessionManager;
//		createSights();
	    Enumeration<String> iter = servconf.getInitParameterNames();
		while (iter.hasMoreElements() ) {
			log( iter.nextElement());
		}
		
//		log( servconf.getServletContext().getInitParameterNames().toString());
		final_log( "************** ISServlet init end ************************");
	}
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		log( "************** ISServlet destroy start ************************");
		closeSights();
		appconfig.close();
		if( aLogTask != null ) aLogTask.exit();
		aLogTask = null;
		if( isMain() ) final_log( "Servlet destroy end!");
		super.destroy();
		log( "************** ISServlet destroy end ************************");
	}

	/**
	 * Gets the parameter string.
	 * 
	 * @param request
	 *            the request
	 * @param sName
	 *            the s name
	 * 
	 * @return the parameter string
	 */
	public static String getParameterString( HttpServletRequest aRequest, String sName) 
	{
		if( aRequest instanceof MultipartRequest ) return aRequest.getParameter( sName);
		Enumeration<?> aNames = aRequest.getParameterNames();
		while( aNames.hasMoreElements() ){
			Object aObject = aNames.nextElement();
			if( (( String)aObject).equalsIgnoreCase( sName) ) return aRequest.getParameter( (( String)aObject));
		} // 17.11.2018 tagastas t�hja asemel "null"  ainult see v�imaldab case sensitive
//		return ( aRequest != null && sName != null )? aRequest.getParameter( sName): null;
		return null;
	}
	public static String getParameterStringNull( HttpServletRequest aRequest, String sName) 
	{
		String s = getParameterString( aRequest, sName);
		return ( s == null || s.trim().length() == 0 )? null: s.trim();
	}
	public static boolean hasParameter( HttpServletRequest aRequest, String sName) 
	{
		if( aRequest == null ) return false;
		String s = getParameterString( aRequest,  sName);
		if( s != null ) return true; // && s.length() > 0;
		return aRequest.getParameterValues( sName) != null;
	}
	public static boolean hasParameterValue( HttpServletRequest aRequest, String sName) {
		if( aRequest == null ) return false;
		String s = getParameterString( aRequest,  sName);
		return s != null && s.length() > 0;
	}
	public static boolean getParameterBoolean( HttpServletRequest request, String sName) 
	{
		boolean bRet = false;
		String s = getParameterString( request, sName);
		if( s != null ){
			if( s.equals( "on") )
				bRet = true;
			else if( s.equals( "yes") )
				bRet = true;
			else if( s.equals( "true") )
				bRet = true;
			else
				bRet = Boolean.getBoolean( s);
		}
		return bRet;
	}
	public static int getParameterInt( HttpServletRequest request, String sName) 
	{
		int iRet = 0;
		try{
			String s = getParameterString( request, sName);
			if( s != null )
				iRet = ( int)Math.round( Double.parseDouble( s));
		}catch( Exception e ){
		}
		return iRet;
	}
	public static int[] getParameterInts( HttpServletRequest Request, String sName) 
	{
		int[] iA = null;
		String[] sA = Request.getParameterValues( sName);
		if( sA != null ){
			int n = sA.length;
			if( n > 0 ){
				iA = new int[n];
				try{
					for( int i = n; --i >= 0;)
						iA[i] = ( int)Math.round( Double.parseDouble( sA[i]));
					;
				}catch( Exception e ){
					iA = null;
				}
			}
		}
		return iA;
	}
	public static int getParameterHex( HttpServletRequest request, String sName) {
		int iRet = 0;
		try{
			String s = getParameterString( request, sName);
			if( s != null ) iRet = ( int)Math.round( Integer.parseInt( s, 16));
		}catch( Exception e ){
		}
		return iRet;
	}
	static public long getParameterLong( HttpServletRequest request, String sName) 
	{
		long lRet = 0;
		String s = null;
		try{
			s = getParameterString( request, sName);
			if( s != null ) lRet = Long.valueOf( s).longValue();
		}catch( Exception E ){
			Log.error( "long format Exception ( " + E.getMessage() + ") in: "
					+ s, true);
		}
		return lRet;
	}
	static public java.sql.Date getParameterDate( HttpServletRequest request, String sName) 
	{
		java.sql.Date DateIn = null;
		String s = null;
		try{
			s = getParameterString( request, sName);
			if( s != null )	DateIn = GlobalFunc.parseDate( s, true);
		}catch( Exception E ){
			Log.error( "date format Exception ( " + E.getMessage() + ") in: "
					+ s, true);
		}
		return DateIn;
	}
	static public java.sql.Date getParameterDate( HttpServletRequest request, String sName, boolean bStart) 
	{
		java.sql.Date DateIn = null;
		try{
			String s = getParameterString( request, sName);
			if( s != null ) DateIn = GlobalFunc.parseDate( s, bStart);
		}catch( Exception e ){
		}
		return DateIn;
	}
	static public double getParameterDouble( HttpServletRequest request, String sName) 
	{
		double dRet = 0;
		try{
			String s = getParameterString( request, sName);
			if( s != null )	dRet = Double.valueOf( s).doubleValue();
		}catch( Exception e ){
		}
		return dRet;
	}
	public String getRequest( HttpServletRequest aRequest) 
	{
		String sReq = null;
		try{
			sReq = getParameterString( aRequest, "REQUEST");
			if( sReq == null ) sReq = getParameterString( aRequest, "request");
		}catch( Throwable aE ){
			final_log( aE.getMessage());
		}
		return sReq;
	}
	public static int getReturn( HttpServletRequest aRequest)
	{
		if( hasParameter( aRequest, "RETURN"))
			return getParameterInt( aRequest, "RETURN");
		if( hasParameter( aRequest, "ID_RETURN"))
			return getParameterInt( aRequest, "ID_RETURN");
		return 0;
	}

	/**
	 * Checks if is new day.
	 * 
	 * @return true, if is new day
	 */
	public synchronized boolean isNewDay() {
		boolean bRet = GlobalDate.isNewDay( lLastTime);
		if( bRet ){
			lLastTime = (new Date()).getTime();
			Log.info( 50, "starts new day ");
		}
		return bRet;
	}

	/**
	 * Vacuum db.
	 */
	public void vacuumDb() {
		DbDescr DbDes = appconfig.getDatabaseDescriptor( "db");
		if( DbDes != null ){
			Database Db = null;
			Log.info( "Start vacuum");
			try{
				Db = new Database( DbDes);
				Db.exec( "vacuum");
			}catch( Exception E ){
				Log.info( E);
			}
			if( Db != null )
				Db.close();
			Log.info( "End vacuum");
		}
	}

	/**
	 * Reload.
	 * @throws Throwable 
	 */
	public void reload()
	{
		closeTimeoutSights();
		createLogCatalog();
//		vacuumDb();
		/*
		 * DbDescr DbDes = appconfig.getDatabaseDescriptor( "db"); if( DbDes !=
		 * null ){ Database Db = null; Log.info( "Start vacuum"); try { Db = new
		 * Database( DbDes); Db.exec( "vacuum"); } catch( Exception E) {
		 * Log.info( E); } if( Db != null ) Db.close(); Log.info( "End vacuum");
		 * }
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	// J�rgnev on vajalik siis kui miski jama on sessioonidega mitme servleti korral
//	public void saveSessionId(String sSessionId) {}
	public boolean login( HttpServletRequest request, HttpServletResponse response) throws IOException { return false;}
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		long lSTime = (new Date()).getTime(); // Paneme algusaja paika
		boolean bFirst = false;
		String sReq = null;
		String sRet = null;
		HttpSession aSession = null;
		Sight MySight = null;
		boolean bSessionContinue = false;
		try{
			aSession = request.getSession( false);
/*			if( aSession == null ){ siit ma hakkasin tegema juhust kui miski jama on sessioonidega
				String sSessionId = request.getRequestedSessionId();
				if( sSessionId != null ){
					if( isDebug( 95) ) final_log( "Session is null, but sessionId=" + sSessionId);
					saveSessionId( sSessionId);
				}
			} */
			if( !request.isRequestedSessionIdValid() ){
				sReq = getRequest( request);
				if( !((aSession == null || aSession.isNew()) && sReq != null && sReq.equalsIgnoreCase( "Main")) ){
					if( isDebug( 95) ) final_log( "Request isn't valid, REQUEST=" + getRequest( request));
					System.out.println( "Request isn't valid, REQUEST=" + getRequest( request));
					return;
				}
				else {
					if( isDebug( 95) ) final_log( "Request is valid, REQUEST=" + getRequest( request));
					System.out.println( "Request is valid, REQUEST=" + getRequest( request));
				}
					
			}
			if( aSession == null ){
				aSession = request.getSession();
				if( isDebug( 95) ) log( "NewSession " + getRequest( request) + ":" + request.getParameter( "Comment"));
			}
			if( request.getCharacterEncoding() == null ){
				try {
					request.setCharacterEncoding( DOMData.getDefaultEncoding());
				} catch (UnsupportedEncodingException e) {
					Log.error( e);
				}
			}
//			request.setCharacterEncoding("UTF-8"); 
			if( isNewDay() ) reload();
			sReq = getRequest( request);
			if( sReq == null ){
				final_log( "REQUEST is null?");
				return;
			}else if( login( request, response)) return;
				
			boolean bMapInput =  sReq.equalsIgnoreCase( "MapInput");
			MySight = getSight( request); // 08.05.2015 aSession);
			if( MySight == null ){
				if( bMapInput ) return;
				if( !isValidStart( sReq) ){
					if( !bMapInput ) final_log( "Starting REQUEST is " + sReq + " from SESSION " + aSession.getId());
					return;
				}
				MySight = createSight( request);
				if( MySight != null && ( bSessionContinue || MySight.isValidStart( request)) ){
					bFirst = true;
					aSessionManager.setSight( aSession, MySight);
					if( MySight.isDebug( 98) )
						MySight.log( "setSight for SessionID=" + aSession.getId() + " " +  MySight.getUserName() + " " + aSession.isNew());
					String sURL = request.getRequestURL().toString();
					MySight.setURL( sURL);
					if( MySight.isDebug( 95) ){
						MySight.log( " ");
						String sQuery = request.getQueryString();
						if( bSessionContinue ){
							MySight.log( "... Session continue " + sQuery);
						}else{
							MySight.log( "URL: " + sURL	+ ( ( sQuery != null)? "?" + sQuery: ""));
							String sUserAgent = request.getHeader( "User-Agent");
							MySight.log( "Brauser " + sUserAgent + " encoding="	+ request.getCharacterEncoding());
							MySight.log( "User IP: " + request.getRemoteAddr());
						}
					}
					if( sURL.substring( 0, 6).equalsIgnoreCase( "https:") )
						MySight.setHttps( true);
					MySight.createUserStat( request);
//				}else if( MySight != null && isValidCall( request) ){
//					setSight( request, MySight);
				}else if( isValidCall( request, response)){ // used in proxy
					log( "Call " + request.getRemoteUser() + "["
							+ request.getRemoteAddr() + "] request "
							+ request.getRequestURI() + "?"
							+ request.getQueryString());
					if( MySight != null ){
						MySight.close();
						aSessionManager.closeSight( aSession, false);
					}
					return;
				}else{
					if( MySight != null ){
						if( MySight.isDebug( 95) ) final_log( "User " + request.getRemoteUser() + "["
								+ request.getRemoteAddr() + "] request "
								+ request.getRequestURI() + "?"
								+ request.getQueryString() + " has no rights");
						MySight.close();
						aSessionManager.closeSight( aSession, false);
					}
					try{
						response.sendRedirect( request.getContextPath() + "/errorLogin.html");
						aSession.invalidate();
					}catch( Exception aE ){
					}
					return;
				}
//				bFirst = true;
			}else if( sReq.equalsIgnoreCase( "log") ){
				if( ISServlet.hasParameter( request, "Comment") ){
					MySight.log( request.getParameter( "Comment"));
				}
				return;
			}else if( !request.isRequestedSessionIdValid() ){
				final_log( "User " + request.getRemoteUser() + "["
						+ request.getRemoteAddr() + "] request "
						+ request.getRequestURI() + "?"
						+ request.getQueryString() + " session is not valid");
				return;
			}else{
				lSTime = (new Date()).getTime();
				MySight.requestUserStat( request);
			}
			MySight.setThread( Thread.currentThread());
			DOMData Doc = null;
			try{
				if( bSessionContinue ){
					Doc = MySight.getTemplate();
					Node Root = Doc.getRootNode();
					if( bMapInput ){
						Doc.addChildNode( Root, "Comment", " töö jätkamiseks vajutage F5");
						Doc.addChildNode( Root, "ShowMapRet", -99);
						Doc.addChildNode( Root, "MapOper", 0);
						Doc.setHTML( false);
					}else{
						Doc.setFileXSL( MySight, "SessionTimeout");
					}
				}else{
					if( doRequest( request, response) ){
						mylog( MySight, request, 10, "Response " + sReq + " time: " + GlobalFunc.getPeriodMS( lSTime));
						if( isOneRequest( request) ) startLogout( MySight, aSession);
						return;
					}
					if( sReq != null ){
						if( !bMapInput ){
							MySight.setLastTime( lSTime);
							int iRet = getReturn( request);
							if( MySight.isDebug( 95) ) MySight.log( "Request: " + sReq + " Return:" + iRet);
							exitLogout( MySight, sRet);
						}
					}
					MySight.setTask( request);
					if( bMapInput || bFirst || MySight.isSessionOK( request) ){
//						MySight.setServlet( this); see ei olnud hea m�te
						sReq = getRequest( request);
						Doc = MySight.doRequest( request, response);
						if( Doc != null && !bMapInput ) MySight.addError( Doc);
					}else 
						final_log( "Session isn't OK");
				}
			}catch( Throwable aE ){
				ExceptionIS aE1 = (aE instanceof ExceptionIS)? ( ExceptionIS)aE
						: new ExceptionIS( aE);
				MySight.log( aE1);
//				final_log( "User " + MySight.getUserName() + " got error!");
//				final_log( "Throwable:" + aE.getMessage());
				Doc = aE1.getDOMDoc( MySight);
			}
			if( MySight != null ) MySight.setThread( null);
			if( Doc != null ){
				if( ISServlet.hasParameter( request, "format") ){ // ette on antud tagastusformaat
					String sFormat = ISServlet.getParameterString( request,	"format");
					if( sFormat.equalsIgnoreCase( "xml")){
						if( MySight.hasUserRightsXML( request) ) Doc.write( response);
					}else{
						setXSL( MySight, Doc, request, sFormat);
						if( !Doc.writeXSL( response, MySight, sFormat) ){
							if( MySight.User != null && MySight.isUserAdmin()) Doc.write( response);
							if( MySight.isDebug( 98) ) Doc.writeFile( MySight.getLogFileName( sReq	+ ".xml"));
						}
					}
				}else if( Doc.isHTML() ){ 
					setXSL( MySight, Doc, request, null);
					if( !Doc.writeXSL( response, MySight, null) ){	
						if( MySight.User != null && MySight.isUserAdmin()) Doc.write( response);
						if( MySight.isDebug( 98) ) Doc.writeFile( MySight.getLogFileName( sReq	+ ".xml"));
					}
				}else{
					Doc.write( response);
					if( MySight.isDebug( 98) ){
						if( bMapInput ){
							int iOper = Doc.getIntValue( Doc.getRootNode(), "MapOper");
							if( iOper == 50 ) sReq += iOper;
						}
						Doc.writeFile( MySight.getLogFileName( sReq + ".xml"));
					}
				}
			}else if( !bMapInput ){
//				if( request == null ) return;
				String sFormHTML = MySight.doRequest( request);
				if( sFormHTML != null ){
					response.setContentType( "text/html");
					try{
						PrintWriter out = response.getWriter();
						out.println( sFormHTML);
					}catch( Exception E ){
//						if( MySight != null )
							MySight.log( E);
//						else
//							log( E);
					}
					if( MySight.isDebug( 99) && sReq != null )
						writeFile( getLogFileName( sReq + ".html"), sFormHTML);
				}
			}
		}catch( Throwable aE ){
			if( MySight != null ){
				MySight.log( aE);
				try{
					final_log( "User " + MySight.getUserName() + " got error!");
				}catch( Throwable aE2){
					try{
						super_log( aE2);
					}catch( Throwable aE1 ){
					}
					throw new ServletException( "final_log error", aE2);
				}
				if( sReq != null && (sRet == null || !sRet.equals( "9")) ){
					if( aE instanceof ExceptionIS ) try{
						DOMData Doc = (( ExceptionIS)aE).getDOMDoc( MySight);
						if( !Doc.writeXSL( response, MySight, null) )
							Doc.write( response);
						startLogout( MySight, aSession);
					}catch( Throwable e ){
					}
				}
			}
			try{
				final_log( aE);
			}catch( Throwable aE2){
				try{
					super_log( aE);
					super_log( aE2);
				}catch( Throwable aE1 ){
				}
				throw new ServletException( "final_log error", aE);
			}
		}
/*		if( sRet.equals( "9") ){
			mylog( MySight, request, 50, "Close Form " + sReq + " time: "
					+ GlobalFunc.getPeriod( lSTime));
		}else*/
		if( sReq != null && MySight != null && MySight.isDebug( 95) ) 
			mylog( MySight, request, 95, "Response " + sReq + " time: " + 
					GlobalFunc.getPeriodMS( lSTime));
	}
	public synchronized void exitLogout( Sight aSight, String sRet)
	{
		if( aSight.Logout != null && (sRet == null || !sRet.equals( "9")) ){
			aSight.Logout.exit();
			aSight.Logout = null;
			if( aSight.isDebug( 98)) aSight.log( "session Logout to null");
		}
	}
	public synchronized void startLogout( Sight aSight, HttpSession aSession)
	{
		aSight.Logout = new Logout( this, aSession);
		aSight.Logout.start();
	}
	public void mylog( Sight MySight, HttpServletRequest Request, int iDebug, String sMsg) {
		if( MySight != null ){
			if( MySight.isDebug( iDebug)) MySight.log( sMsg);
		}else if( isDebug( iDebug)) 
			log( (Request != null)? Request.getRemoteAddr() + ": " + sMsg : sMsg);
	}
	public void log( int iDebug, String sMsg){ if( isDebug( iDebug) ) log( sMsg);}
	public void log( String sMsg)
	{ 
   		Thread T = Thread.currentThread();
   		Sight aSight = getSight( T);
    	if( aSight != null ) aSight.log( sMsg);
    	else{
        	if( T instanceof ThreadLog ) (( ThreadLog)T).log( sMsg);
        	else final_log( sMsg);
    	}
	}
	public void flush_log( )
	{ 
		if( aLogTask != null ) aLogTask.flush();
	}
	public void log( Sight aSight, Throwable E){
		mylog( aSight, null, 0, E.getMessage());
		Log.error( E);
	}
	public void log( Throwable E){ Log.error( E);}
	public void log_error( String sMsg){ Log.log_error( sMsg);}

	/**
	 * Do request.
	 * 
	 * @param request
	 *            the request
	 * @param Response
	 *            the response
	 * 
	 * @return true, if successful
	 */
	public boolean doRequest( HttpServletRequest request, HttpServletResponse Response) throws ExceptionIS {
		return false;
	}

	/**
	 * Creates the sight.
	 * 
	 * @param request
	 *            the request
	 * 
	 * @return the sight
	 */
	public Sight createSight( HttpServletRequest request) {
		return new Sight( request, this, appconfig);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	private SerialBlob aBlob = null;
	public SerialBlob getBlob(){ return aBlob;}
	public void doPost( HttpServletRequest aRequest, HttpServletResponse rsp) throws ServletException{
/*		boolean bMultipart = ServletFileUpload.isMultipartContent( aRequest);
		if( bMultipart){
			try{
				aRequest = new MultipartRequest( aRequest, -1, getTempPath());		// no size limit
			}catch( MException aE ){
				log( aE);
				return;
			}
		}*/
		if( isDebug( 99))
			final_log( "POST Session Id=" + aRequest.getRequestedSessionId());
		try{
			boolean bMultipart = ServletFileUpload.isMultipartContent( aRequest);
			if( bMultipart){
				HttpSession aSession = aRequest.getSession( false);
				Sight aSight = getSight( aSession);
				if( aSight != null ) aSight.setDownload( true);
				MultipartRequest aNewRequest = new MultipartRequest( aRequest, -1, getTempPath());		
				if( aNewRequest != null ) aRequest = aNewRequest;
				if( aSight != null ) aSight.setDownload( false);
			}
		}catch( Throwable aE ){
			HttpSession aSession = aRequest.getSession( false);
			Sight aSight = getSight( aSession);
			if( aSight != null ){
				aSight.log( aE); 
				aSight.setError( aE.getMessage());
			}else log( aE);
		}
		doGet( aRequest, rsp);
	}

	/**
	 * Gets the attr.
	 * 
	 * @param Request
	 *            the request
	 * @param sName
	 *            the s name
	 * 
	 * @return the attr
	 */
	public static Object getAttr( HttpServletRequest Request, String sName) {
		HttpSession Session = Request.getSession();
		return Session.getAttribute( sName);
	}

	/**
	 * Sets the attr.
	 * 
	 * @param Request
	 *            the request
	 * @param sName
	 *            the s name
	 * @param Obj
	 *            the obj
	 */
	public static void setAttr( HttpServletRequest Request, String sName,
			Object Obj) {
		HttpSession Session = Request.getSession();
		Session.setAttribute( sName, Obj);
	}

	/**
	 * Write file.
	 * 
	 * @param sFilename
	 *            the s filename
	 * @param sSource
	 *            the s source
	 * 
	 * @return true, if successful
	 */
	public boolean writeFile( String sFilename, String sSource) {
		try{
			PrintWriter PW = new PrintWriter( new FileOutputStream( new File(
					sFilename)));
			PW.print( sSource);
			PW.close();
			return true;
		}catch( FileNotFoundException E ){
		}
		return false;
	}

	/**
	 * Gets the writer.
	 * 
	 * @param sFilename
	 *            the s filename
	 * 
	 * @return the writer
	 */
	public static PrintWriter getWriter( String sFilename) {
		try{
			return new PrintWriter( new FileOutputStream( new File( sFilename)));
		}catch( FileNotFoundException E ){
		}
		return null;
	}

	/**
	 * Gets the uRL attr.
	 * 
	 * @param sURL
	 *            the s url
	 * @param sAttr
	 *            the s attr
	 * 
	 * @return the uRL attr
	 */
	public static String getURLAttr( String sURL, String sAttr) {
		if( sURL != null & sAttr != null ){
			int i = sURL.indexOf( sAttr + "=");
			if( i < 0 )
				i = sURL.indexOf( sAttr.toUpperCase() + "=");
			if( i > 0
					&& (sURL.charAt( i - 1) == '&' || sURL.charAt( i - 1) == '?') ){
				i += sAttr.length() + 1;
				int l = sURL.indexOf( '&', i);
				return (l > 0)? sURL.substring( i, l) : sURL.substring( i);
			}
		}
		return null;
	}
	public void loadSQLUpdates( )
	{
	    String sFileName = getRealPath( "alterDB.sql");
		String sLine = null;
		BufferedReader In = null;
		Database Db = null;
		DbAccess DbIn = null;
		try {
		    In = new BufferedReader( new FileReader( new File( sFileName)));
			Db = new Database( appconfig.getDatabaseDescriptor( "db"));
			StringBuffer LineSQL = new StringBuffer();
		    while(( sLine = In.readLine())!= null) {
		        log( sLine);
		        if( sLine.length() > 3 && sLine.startsWith( "rem") ) continue;
		        int n = sLine.indexOf( ';');
		        if( n > 0 ){
		            LineSQL.append( sLine.substring( 0, n));
		            String sQuery = LineSQL.toString(); 
		            try {
		            	if( sQuery.indexOf( "update") >= 0 || sQuery.indexOf( "UPDATE") >= 0 ){
		            		Db.exec( sQuery);
		        		    Db.commit();
		            	}else if( sQuery.indexOf( "select") >= 0 || sQuery.indexOf( "SELECT") >= 0 ){
	            			if( DbIn == null ) DbIn =  new DbAccess( Db);
	            			if( DbIn.select( sQuery )) do{
	            				StringBuffer RS = new StringBuffer();
	            				int j = DbIn.getColumnCount();
	            				for( int i = 1; i <= j; ++i){
	            					RS.append( DbIn.getString( i));
	            					RS.append( " ");
	            				}
	            				Log.info( RS.toString());
	            			}while( DbIn.next());
		            	}else{
		            		Db.exec( sQuery);
		        		    Db.commit();
		            	}
                    } catch (MException E1) {
                    }
                    Log.info( " ");
                    LineSQL = new StringBuffer();
		        }else LineSQL.append( sLine);
			}
		} catch ( Exception E) {
		}
		try {
            if( DbIn != null ) DbIn.close();
            if( Db != null ) Db.close();
            if( In != null ) In.close();
        } catch ( Exception E) {
        }
	}
	/**
	 * Gets the log file name.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the log file name
	 */

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http
	 * .HttpSessionEvent)
	 */

	/**
	 * Gets the temp file.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the temp file
	 */
	public String getTempFile( String sFileName) {
		return appconfig.getTempPath() + sFileName;
	}

	/**
	 * Gets the last time.
	 * 
	 * @return the last time
	 */
	public long getLastTime() {
		return lLastTime;
	}

	/**
	 * Sets the last time.
	 * 
	 * @param lastTime
	 *            the new last time
	 */
	public void setLastTime( long lastTime) {
		lLastTime = lastTime;
	}

	/**
	 * Sets the last time.
	 */
	public void setLastTime() {
		lLastTime = (new Date()).getTime();
	}

	/**
	 * Gets the sight.
	 * 
	 * @param aThread
	 *            the a thread
	 * 
	 * @return the sight
	 */
	private String sLogCatName = null;	/** The s log cat name. */
	public void setLogCatName(){ sLogCatName = getTempPath();}
	public void setLogCatName( String sLogCatName){	
		this.sLogCatName = sLogCatName;
		sLogFileName = null;
	}
	/** The s default log file name. */
	private String sDefaultLogFileName = "Servlet";
	public String getLogName(){	return sDefaultLogFileName;}
	public void setDefaultLogFileName( String sDefaultLogFileName) {
		this.sDefaultLogFileName = sDefaultLogFileName;
		sLogFileName = null;
	}
	public String getLogCatName(){	return sLogCatName;}
	public void createLogCatalog()
	{
		String sNewLogCatName = getTempPath( "Log" + GlobalDate.getDate());
		if( sNewLogCatName != null ){
			try{
				File Cat = new File( sNewLogCatName);
				if( Cat.mkdirs() )
					log( "New log cat is " + sNewLogCatName);
				else
					log( " log cat is " + sNewLogCatName);
				flush_log();
				sLogCatName = sNewLogCatName;
				sLogFileName = null;
				getLogFileName();
			}catch( Exception aE ){
				log( aE);
			}
		}else 
			super_log( "error on log cat");
	}
	public final synchronized boolean createCatalog( String sFileName) {
		int i = sFileName.lastIndexOf( "/");
		if( i > 0 ){
			try{
				File aCat = new File( sFileName.substring( 0, i));
				return aCat.mkdirs();
			}catch( Exception aE ){
				Log.log_error( aE);
			}
		}
		return false;
	}
	public String getLogFileName( String sFileName) {
		return (sLogCatName != null)? sLogCatName + "/" + sFileName : null;
	}
	private String sLogFileName	= null;
	public String getLogFileName() 
	{
		if( sLogFileName == null && sLogCatName != null){
			String sLogFileName = getLogFileName( sDefaultLogFileName, -1);
			if( sLogFileName != null ){ 
				try{
					for( int i = 0;; ++i){
						File LogFile = new File( sLogFileName);
						if( !LogFile.exists() ){
							this.sLogFileName = sLogFileName;
								// aLogTask.setLogName( sDefaultLogFileName + (( i > 0 )? "_" + ( i-1): ""));
							break;
						}
						sLogFileName = getLogFileName( sDefaultLogFileName, i);
					}
				}catch( Exception E ){
					log( E);
					sLogFileName = null;
				}
			}
			if( sLogFileName != null ){
			}
		}
		return sLogFileName;
	}
	private String getLogFileName( String sLogFileName, int i) 
	{
		StringBuffer FileName = new StringBuffer( sLogFileName);
		if( i >= 0 ){
			FileName.append( "_");
			FileName.append( i);
		}
		FileName.append( ".txt");
		return getLogFileName( FileName.toString());
	}
/*	public void setLogFileName( String sLogFileName) {
		sDefaultLogFileName = sLogFileName; // Default
		this.sLogFileName = null;
	}*/
	/** The b log cycle. */
	boolean bLogCycle = false;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#log(java.lang.String)
	 */

	/**
	 * Creates the catalogs.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return true, if successful
	 */

	/**
	 * Final_log.
	 * 
	 * @param sMsg
	 *            the s msg
	 */
	public void super_log( String sMsg) //throws Throwable 
	{
		super.log( sMsg);		
	}
    public void super_log( Throwable aE) //throws Throwable
    {
        super_log( aE.getLocalizedMessage());
        super_log( aE.getMessage());
        StackTraceElement[] Trace = aE.getStackTrace();
        if( Trace != null ) for( int i = 0; i < ((Trace.length>10)? 10: Trace.length); ++i) 
            super_log( Trace[ i].toString());
        if( aE.getCause() != null ){
        	Trace = aE.getCause().getStackTrace();
            if( Trace != null ) for( int i = 0; i < ((Trace.length>10)? 10: Trace.length); ++i) 
                super_log( Trace[ i].toString());
        }
    }
	public boolean thread_log( String sMsg)
	{
    	Thread T = Thread.currentThread();
		if( T instanceof ThreadLog ) (( ThreadLog)T).log( sMsg);
		return final_log( sMsg);
	}
	public boolean final_log( String sMsg)
	{
		if( aLogTask != null ) aLogTask.log( sMsg);
		else try{
			bLogCycle = false;
			String sFileName = getLogFileName();
			if( sFileName != null ){
				PrintStream aPW = null;
				try{
					aPW = Log.getLogStream( sFileName);
					_final_log( aPW, sMsg);
				}catch( Throwable E ){
					if( createCatalog( sFileName) && bLogCycle)
						final_log( sMsg);
					else{
						try{
							super_log( E);
							return true;
						}catch( Throwable aE ){
						}
					}
				}finally{
					if( aPW != null ) aPW.close();
				}
				return true;
			}else{
				super_log( sMsg);
				return true;
			}
		}catch( Exception aE){
			try{
				super_log( aE);
				return true;
			}catch( Throwable aE1 ){
			}
		}
		return false;
	}
    public boolean final_log( String sFileName, String sMsg) 
    {
		try {
			_final_log( sFileName, sMsg);
			return true;
		}catch( Throwable aE2) {
			try{
				super_log( aE2);
				return true;
			}catch( Throwable aE ){
			}
		}
		return false;
    }
    private void _final_log( String sFileName, String sMsg) throws Throwable
    {
        PrintStream aPW = null;
		try {
            aPW = Log.getLogStream( sFileName);
            if( aPW != null){
                _final_log( aPW, sMsg);
            }
        } finally{
        	if( aPW != null) aPW.close();
        }
    }
	public boolean final_log( Throwable E){ 
		if( aLogTask != null ){
			aLogTask.log( E);
			return true;
		}else return final_log( getLogFileName(), E);
	}
    public boolean final_log( String sFileName, Throwable aE)
    {
		try {
			_final_log( sFileName, aE);
			return true;
		}catch( Throwable aE2) {
			try{
				super_log( aE2);
				return true;
			}catch( Throwable aE1 ){
			}
		}
		return false;
    }
    private void _final_log( String sFileName, Throwable aE) throws Throwable
    {
        PrintStream aPW = null;
		try {
            aPW = Log.getLogStream( sFileName);
            if( aPW != null){
                _final_log( aPW, aE.getLocalizedMessage());
                _final_log( aPW, aE.getMessage());
                StackTraceElement[] Trace = aE.getStackTrace();
                if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
                    _final_log( aPW, Trace[ i].toString());
                if( aE.getCause() != null ){
                	Trace = aE.getCause().getStackTrace();
                    if( Trace != null ) for( int i = 0; i < Trace.length; ++i) 
                        _final_log( aPW, Trace[ i].toString());
                }
            }
        } finally{
        	if( aPW != null) aPW.close();
        }
    }
    private void _final_log( PrintStream PW, String sMsg) throws Throwable 
    {
	    int n = sMsg.length();
	    if( n <= 200 )
	    	PW.println( GlobalDate.getTime() + " " + sMsg);
	    else{
	    	PW.println( GlobalDate.getTime() + " " + sMsg.substring( 0, 200));
	    	for( int i = 200; i < n; i += 200)
	    		if( i + 200 < n ) PW.println( "       " + sMsg.substring( i, i + 200));
	    		else PW.println( "       " + sMsg.substring( i));
	    }
    }
	/*
	 * public void doFilter(ServletRequest request, ServletResponse response,
	 * FilterChain next) throws IOException, ServletException { // Respect the
	 * client-specified character encoding // (see HTTP specification section
	 * 3.4.1) response.setContentType( "text/html; charset=iso-8859-15");
	 * response.setCharacterEncoding( "iso-8859-15"); next.doFilter( request,
	 * response); Log.info( response.getCharacterEncoding()); }
	 */
	/**
	 * Sets the default log file name.
	 * 
	 * @param defaultLogFileName
	 *            the new default log file name
	 */

	/**
	 * Checks if is valid call.
	 * 
	 * @param req
	 *            the req
	 * 
	 * @return true, if is valid call
	 */
	public boolean isValidCall( HttpServletRequest req, HttpServletResponse response) {
		return false;
	}
	public boolean isLocalhost( HttpServletRequest aRequest)
	{
		return ( aRequest != null)? aRequest.getRemoteAddr().equals( "127.0.0.1"): false;
	}
	public boolean isOneRequest( HttpServletRequest aRequest)
	{
		return false;
	}
	public void doTask() // called from LogTask
	{
//		if( ( aSessionManager != null) ) aSessionManager.closeTimeoutSights();
	}
/*	private int iSessionTimeout = 0;
	public void setSessionTimeout( int iSessionTimeout){ 
		if( iSessionTimeout > 0 ) this.iSessionTimeout = iSessionTimeout;}*/
	public Sight getSight( HttpServletRequest aRequest){ 
		return  aSessionManager.getSight( aRequest.getSession( false));
	}
	public Sight getSight( HttpSession aSession){ 
		return  aSessionManager.getSight( aSession);
	}
	public Sight getSight( Thread aThread){ 
		return  ( aSessionManager != null)? aSessionManager.getSight( aThread): null;
	}
	public int getSightsNr(){ 
		return  aSessionManager.getSightsNr();
	}
	public ArrayList<Sight> getSights(){ 
		return  aSessionManager.getSights();
	}
	public void closeSight( HttpSession aSession){ 
		if(  aSessionManager != null ) aSessionManager.closeSight( aSession);
	}
	public void closeTimeoutSights(){ 
		if(  aSessionManager != null ) aSessionManager.closeTimeoutSights();
	}
	public void closeSights(){ 
		if(  aSessionManager != null ) aSessionManager.closeSights();
	}
	public boolean isValidStart( String sRequest) {
		return sRequest != null && sRequest.equalsIgnoreCase( "Main");
	}
    public void downloadFile( HttpServletResponse aResponse, File aFile, String sMimeType)
    {
    	ServletOutputStream op = null;
		DataInputStream in = null;
		if( aFile.exists() )try{
			op = aResponse.getOutputStream();
			if( sMimeType == null ){
				int i = aFile.getName().lastIndexOf( '.');
				if( i > 0 ){
					String sType = aFile.getName().substring( ++i);
					if( sType.equalsIgnoreCase( "pdf") ) sMimeType = "application/pdf";
					else sMimeType = "application/octet-stream"; 
				}
			}
			aResponse.setContentType( sMimeType);
			
			long length = aFile.length();
			aResponse.setContentLengthLong( length);
			log( 98, aFile.getPath() + " download size " + length);
			aResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + aFile.getName() + "\"");
			aResponse.setHeader( "Pragma", "public");
			aResponse.setHeader( "Cache-control", "must-revalidate");
			aResponse.setDateHeader( "Expires", 0);
			
			int n;
			byte[] bbuf = new byte[ 1024];
			in = new DataInputStream( new FileInputStream( aFile));
			long iSumLength = 0;
			while ((in != null) && ((n = in.read(bbuf)) != -1))
			{
				op.write( bbuf, 0, n);
				iSumLength += n;
			}
			log( 95, "download end " + iSumLength);
		}catch( Exception aE ){
			log( aE);
		}finally{
			try{
				if( in != null ) in.close();
				if( op != null ){
					op.flush();
					op.close();
				}
			}catch( IOException aE ){
				log( aE);
			}
		}
    }
	public void doFilter( HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
	}    
	public boolean writePicture( BufferedImage aImage, int iNewWidth, HttpServletResponse aResponse) 
	{
		try{
			if( aImage != null ){
				int iWidth = aImage.getWidth();
				if( iWidth > iNewWidth && iNewWidth > 0 ){
					int iNewHeight = (int) (aImage.getHeight() * ( (double)iNewWidth) / iWidth);
					aImage = Scalr.resize( aImage, Scalr.Method.ULTRA_QUALITY, iNewWidth, iNewHeight);
				}
				writeImageToOutput( aResponse, aImage, "image/png"); //  (aImage.getType() == BufferedImage.TYPE_4BYTE_ABGR)? "image/png": "image/jpeg" );
				return true;
			}else{
			}
		}catch( Exception aE){
			log( aE);
		}finally{
		}
		return false;
	}
	public void writeImageToOutput( HttpServletResponse aResponse, BufferedImage Image, String sFormat)
	{
		ServletOutputStream sout = null;
		try{
//	        aResponse.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate"); // Set standard HTTP/1.1 no-cache headers.
//	        aResponse.addHeader( "Cache-Control", "post-check=0, pre-check=0");  // Set IE extended HTTP/1.1 no-cache headers 
//	        aResponse.setHeader( "Pragma", "no-cache"); 	// Set standard HTTP/1.0 no-cache header.
//	        aResponse.setDateHeader("Expires", 0); //prevents caching at the proxy server
   	 		aResponse.setContentType( sFormat);
			sout = aResponse.getOutputStream();
			boolean bRet = false;
	    	if( sFormat.equalsIgnoreCase( "image/png") ){
	    		bRet = ImageIO.write( Image, "png", sout);
	    	}else if( sFormat.equalsIgnoreCase( "image/gif") ){
	    		bRet = ImageIO.write( Image, "gif", sout);
   			}else if( sFormat.equalsIgnoreCase( "image/jpeg") ){
   				bRet = ImageIO.write( Image, "jpeg", sout);
			}else{
				bRet = ImageIO.write( Image, sFormat, sout);
			}
	    	if( bRet ) sout.flush();
		}catch( Exception aE){
			log( aE.getMessage());
		}finally{
  			try{
				sout.close();
			}catch( IOException aE ){
			}
	   		sout = null;
		}
	}
	public static void setSmartCardCert( HttpServletRequest aRequest) throws Exception
	{
		java.lang.System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true");
        Field f = aRequest.getClass().getDeclaredField( "request"); 
        f.setAccessible(true); 
        org.apache.catalina.connector.Request aRealRequest = ( org.apache.catalina.connector.Request)f.get( aRequest); 
 		aRealRequest.getCoyoteRequest().action( ActionCode.REQ_SSL_CERTIFICATE, null);		
	}
	public boolean isRealm( HttpServletRequest aRequest)
	{
		Realm aRealm = getRealm( aRequest.getServletContext());
		if( aRealm != null && aRealm instanceof LockOutRealm ) aRealm = null;
		return aRealm != null;
	}
	private Realm getRealm(ServletContext context) {

	    if (context instanceof ApplicationContextFacade) {
	        ApplicationContext applicationContext = getApplicationContext(
	                (ApplicationContextFacade)context
	        );

	        if (applicationContext != null) {

	            StandardContext standardContext = getStandardContext(
	                    applicationContext
	            );

	            if (standardContext != null) {

	                return standardContext.getRealm();
	            }
	        }
	    }

	    return null;
	}

	private ApplicationContext getApplicationContext(
	        ApplicationContextFacade facade) {

	    try {

	        Field context = ApplicationContextFacade.class.getDeclaredField(
	               "context"
	        );

	        if (context != null) {

	            context.setAccessible(true);
	            Object obj = context.get(facade);

	            if (obj instanceof ApplicationContext) {

	                return (ApplicationContext)obj;
	            }
	        }

	    } catch (Exception ex) {
	    }

	    return null;
	}

	private StandardContext getStandardContext(
	        ApplicationContext applicationContext) {

	    try {

	        Field context = ApplicationContext.class.getDeclaredField(
	                "context"
	        );

	        if (context != null) {

	            context.setAccessible(true);
	            Object obj = context.get(applicationContext);

	            if (obj instanceof StandardContext) {

	                return (StandardContext)obj;
	            }
	        }

	    } catch (Exception ex) {
	    }

	    return null;
	}}
