/*
 * 
 */
package ee.or.is;


import javax.servlet.http.*;

import org.w3c.dom.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;

import ee.or.db.*;

//import Acme.JPM.Encoders.*;
// import ee.or.gis.GISSight;

/**
 * The Class Sight.
 * 
 * @author administrator
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class Sight implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Db des. */
	private DbDescr			DbDes			= null; // = new DbDescr();
	public DbDescr getDatabaseDesc(){	return DbDes;}
	/** The Db. */
	public Database		   Db = null;
	public void closeDatabase()
	{
		if( Db != null ) Db.close();
		Db = null;
	}
	/** The Doc. */
//	private DOMData			Doc			  = null;
	// boolean bShow = false;
	/** The s error. */
	private String			sError		   = null; // veateate
	// v�ljastamiseks on
	// vaja m�lemat

	/** The i error nr. */
	private int			   iErrorNr		 = 0;	// veateate number

	/** The s comment. */
	private String			sComment		 = null; // ajutiselt v�lja kuni
	// t�hendus t�psustub

	/** The s session id. */
	private String	sSessionID	   = null;
	public String getSessionID(){	return sSessionID;}
	public void setSessionID( String sSessionID){ this.sSessionID = sSessionID;}
	
	/** The User. */
	public User			   User			 = null;

	/** The b first. */
	private boolean				   bFirst		   = true;
	public void setFirst( boolean bFirst){ this.bFirst = bFirst;}
	
	/** The s title. */
	String					sTitle		   = null;
	// public boolean bSVG = true;
	/** The Servlet. */
	private ISServlet Servlet;
	public ISServlet getServlet(){	return Servlet;}
	public void setServlet( ISServlet servlet){	Servlet = servlet;}

	/** The Logout. */
	public Logout			 Logout		   = null;

	/** The s url. */
	private String			sURL			 = null;

	/** The Cur data object. */
	public DataObject		 CurDataObject	= null;
	public DataObject getCurDataObject(){ return CurDataObject;}
	public void setCurDataObject( DataObject CurDataObject){ this.CurDataObject = CurDataObject;}

	/** The l last time. */
	private long			  lLastTime		= 0;

	/** The a thread. */
	private Thread			aThread		  = null;

	/** The i debug. */
	private int			   iDebug		   = 0;

	/** The a user stat. */
	private UserStat		  aUserStat		= null;

	/** The b https. */
	private boolean		   bHttps		   = false;

	/** The i browser. */
	private int			   iBrowser		  = -1;			 // = unknown;
	 	// 0 = IE 7, 8, ...
		// 3 = IE(6.0); 1
	    // 1 = Other 
	public boolean isIE() {	return iBrowser == 0 || iBrowser == 3;}
	public boolean isIE6(){	return iBrowser == 3;}
	private int iVersion = 0;
	public int getVersion(){ return iVersion;}

	/*	public boolean isMozilla() {
		return iBrowser == 1;
	}*/
	private String sPhone = null;
	public boolean isPhoneOS(){	return sPhone != null;}
	public String getPhone(){ return sPhone;}
	public void setPhone( String sPhone){ this.sPhone = sPhone;}
	
	private String sLang = null;
	public String getLang() {	return (sLang==null)? "est": sLang;}
	public void setLang( String sLang){ this.sLang = sLang;}
	public void setLang(  HttpServletRequest aRequest){ 
		if( ISServlet.hasParameter( aRequest, "lang") )
			setLang( ISServlet.getParameterString( aRequest, "lang"));
	}

	private ThreadLogTask aLogTask = null;
	public ThreadLogTask getLogTask(){ return aLogTask;}
	public void exitLogTask(){ if( aLogTask != null ) aLogTask.exit();}

	private String sLogFileName = "User";
	public String getLogFileName(){  return sLogFileName;}
	public void setLogFileName( String sLogFileName){ this.sLogFileName = sLogFileName;}

	private int iScreenWidth = 0;
	private int iScreenHeight = 0;

	/**
	 * Instantiates a new sight.
	 */
	public Sight() {
	}

	/**
	 * Instantiates a new sight.
	 * 
	 * @param Request
	 *            the request
	 * @param Servlet
	 *            the servlet
	 * @param appconfig
	 *            the appconfig
	 */
	public Sight( HttpServletRequest Request, ISServlet Servlet, Config appconfig) // throws
	// MException
	{
		this.Servlet = Servlet;
	}
	public void init() // see t��tab �ksnes peaaknal ( sightG)
	{ 
		if( !isDebug( 98) ){
			aLogTask = new ThreadLogTask( Servlet, getLogFileName());
			aLogTask.start();
		}
		try{
			if( Db != null ){
				Db.close();
				Db = null;
			}
			DbDes = getConfig().getDatabaseDescriptor( "db");
			if( DbDes != null ){
				Db = new Database( DbDes, null); //this);
			}
		}catch( MException e ){
		}
	}

	/**
	 * Sets the my sight.
	 * 
	 * @param MySight
	 *            the my sight
	 * @param request
	 *            the request
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public void setMySight( Sight MySight, HttpServletRequest request) throws MException
	// String sSessionID, String sUser)
	{
		MySight.Servlet = Servlet;
		if( MySight.User != null ){
			MySight.User.setUserIP( request);
			if( MySight.isDebug( 98) ){
			    if( MySight.createLogCat() ){
			    	MySight.log( 99, "Created log cat is " + MySight.getLogCatName());
			    }else
					MySight.log( "User log cat is " + MySight.getLogCatName());
			}
			MySight.log( 95, MySight.User.getUserIP() + " login in role " + MySight.User.getRoleName());
		}
		// MySight.setDebug( iDebug);
		try{
			if( MySight.Db != null ){
				MySight.Db.close();
				MySight.Db = null;
			}
			MySight.getDatabase();
/*			if( DbDes != null ){
				MySight.Db = new Database( DbDes, MySight);
			}*/
		}catch( Exception E ){
			MySight.Db = null;
		}
//		MySight.bUserStat = bUserStat;
		if( request != null){
			String sUserAgent = request.getHeader( "User-Agent");
			if( sUserAgent != null ){
				sUserAgent = sUserAgent.toLowerCase();
				if( sUserAgent.indexOf( "msie 6") >= 0 ){ // IE 6
					MySight.iBrowser = 3;
					MySight.iVersion = 6;
				}else if( sUserAgent.indexOf( "msie") >= 0 ){ // IE 7,8,...
					MySight.iBrowser = 0;
					int i = sUserAgent.indexOf( "msie");
					MySight.iVersion = GlobalData.getInt( sUserAgent.substring( i+5));
				}else if( sUserAgent.indexOf( "opera") >= 0 ){
					MySight.iBrowser = 2;
				}else if( sUserAgent.indexOf( "chrome") >= 0 ){
					MySight.iBrowser = 4;
				}else if( sUserAgent.indexOf( "firefox") >= 0 ){
					MySight.iBrowser = 1;
				}else if( sUserAgent.indexOf( "safari") >= 0 ){
					int i = sUserAgent.indexOf( "safari");
					MySight.iBrowser = 5;
					MySight.iVersion = GlobalData.getInt( sUserAgent.substring( i+7));
				}else if( sUserAgent.indexOf( "rv:") >= 0 ){ // IE 11,...
					MySight.iBrowser = 0;
					int i = sUserAgent.indexOf( "rv:");
					MySight.iVersion = GlobalData.getInt( sUserAgent.substring( i+3));
				}else
					MySight.iBrowser = -1;
				if( sUserAgent.indexOf( "iphone") >= 0 ) MySight.sPhone = "iPhone";
				else if( sUserAgent.indexOf( "ipod") >= 0 ) MySight.sPhone = "iPod";
				else if( sUserAgent.indexOf( "ipad") >= 0 ) MySight.sPhone = "iPad";
				else if( sUserAgent.indexOf( "android") >= 0 ) MySight.sPhone = "Android";
			}else{
				Enumeration<String> E = request.getHeaderNames();
				if( E != null )
					for( ; E.hasMoreElements();){
						String sName = ( String)E.nextElement();
						MySight.log( "Request Header Name " + sName);
					}
				else
					MySight.log( "Request has no Headers!?");
			}
		}
	}

	/**
	 * Gets the user log name.
	 * 
	 * @return the user log name
	 */
	public String getUserLogName() {
		return (User != null)? "User_" + getUserName() : getLogFileName();
	}

	/**
	 * Close.
	 */
	public void close() 
	{
		if( Logout != null ){
			Logout.exit();
			Logout = null;
		}
//		Doc = null;
		if( aUserStat != null ){
			aUserStat.save();
			aUserStat = null;
		}
		if( Db != null ){
			Db.close();
			Db = null;
		}
		if( isDebug( 98) ) log( "Sight closed");
		if( aLogTask != null ){
			aLogTask.exit();
			aLogTask = null;
		}
		sSessionID = null;
	}

	/**
	 * Close.
	 * 
	 * @param Session
	 *            the session
	 */
/*	public final void close( HttpSession Session) 
	{
//		close();
		Servlet.closeSight( Session);
		if( User != null )
			User.logout( this);
		User = null;
	}*/

	/**
	 * Sets the task.
	 * 
	 * @param Request
	 *            the new task
	 */
	public void setTask( HttpServletRequest Request) {
	}

	/**
	 * Call.
	 * 
	 * @param Session
	 *            the session
	 */
	public void call( HttpSession Session) {
	}

	/**
	 * Sets the title.
	 * 
	 * @param sTitle
	 *            the new title
	 */
	public void setTitle( String sTitle) {
		this.sTitle = sTitle;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return sTitle;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return sComment;
	}

	/**
	 * Sets the comment.
	 */
	public void setComment() {
		sComment = null;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param sComment
	 *            the new comment
	 */
	public void setComment( String sComment) {
		this.sComment = sComment;
	}

	/**
	 * Checks if is comment.
	 * 
	 * @param sComment
	 *            the s comment
	 * 
	 * @return true, if is comment
	 */
	public boolean isComment( String sComment) {
		return this.sComment != null && sComment != null
				&& this.sComment.equalsIgnoreCase( sComment) || this.sComment == null
				&& sComment == null;
	}

	/**
	 * Checks if is error.
	 * 
	 * @return true, if is error
	 */
	public boolean isError() {
		return sError != null || iErrorNr > 0;
	}

	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public String getError() {
		return sError;
	}

	/**
	 * Sets the error.
	 * 
	 * @param sError
	 *            the new error
	 */
	public void setError( String sError) {
		this.sError = sError;
	}
	public void setError( int iErrorNr, String sError) {
		this.iErrorNr = iErrorNr;
		this.sError = sError;
		log( sError);
	}
	public void clearError()
	{
		iErrorNr = 0;
		sError = null;
	}
	/**
	 * Adds the error.
	 * 
	 * @param Doc
	 *            the doc
	 */
	public void addError( DOMData Doc) 
	{
		if( sError != null ){
			Node Root = Doc.getRootNode();
			int i = sError.indexOf( '\n');
			if( i < 0 ) Doc.addChildNode( Root, "error", sError);
			else{
				Doc.addChildNode( Root, "error", sError.substring( 0, i));
				while ( i >= 0 ){
					int j = sError.indexOf( '\n', ++i);
					if( j > 0 ) Doc.addChildNode( Root, "error", sError.substring( i, j));
					else{
						Doc.addChildNode( Root, "error", sError.substring( i));
						break;
					}
					i = j;
				}
			}
			sError = null;
		}
		if( iErrorNr > 0 ){
			Node Root = Doc.getRootNode();
			Doc.addChildNode( Root, "error_nr", iErrorNr);
			iErrorNr = 0;
		}
	}

	/**
	 * Sets the error.
	 * 
	 * @param E
	 *            the new error
	 */
	public void setError( Exception aE) {
		if( aE == null )
			sError = null;
		else{
			String sMsg = aE.getMessage();
			int i = sMsg.indexOf( "ERROR:");
			if( i>=0 ) sMsg = sMsg.substring( i+6);
			sError = sMsg;
/*
			String sMsg = E.toString();
			Throwable C = E.getCause();
			String sC = (C != null)? C.getMessage() : "";
			setError( sMsg + sC); */
		}
	}

	/**
	 * Sets the error nr.
	 * 
	 * @param iErr
	 *            the new error nr
	 */
	public void setErrorNr( int iErr) {
		this.iErrorNr = iErr;
	}

	/**
	 * Adds the error nr.
	 * 
	 * @param Doc
	 *            the doc
	 */
/*	public void addErrorNr( DOMData Doc) {
		if( iErrorNr > 0 ){
			Node Root = Doc.getRootNode();
			Doc.addChildNode( Root, "error_nr", iErrorNr);
			iErrorNr = 0;
		}
	}*/

	/**
	 * Gets the error nr.
	 * 
	 * @return the error nr
	 */
	public int getErrorNr() {
		return iErrorNr;
	}

	/**
	 * Adds the url user part.
	 * 
	 * @param sURL
	 *            the s url
	 * 
	 * @return the string
	 */
	public String addURLUserPart( String sURL) {
		return sURL;
	}

	/**
	 * Error form xml.
	 * 
	 * @param Request
	 *            the request
	 * 
	 * @return the dOM data
	 */
	public DOMData ErrorFormXML( HttpServletRequest Request) throws ExceptionIS {
		DOMData Doc = getTemplate();
		Node Root = Doc.getRootNode();
		if( isError() ){
			Doc.addChildNode( Root, "error", getError());
			setError( ( String)null);
			Doc.addChildNode( Root, "error1", getComment());
			setComment();
			if( iErrorNr > 0 ) Doc.addChildNode( Root, "error_nr", iErrorNr);
		}
		iErrorNr = 0;
		return Doc;
	}

	/**
	 * Do input request.
	 * 
	 * @param iOper
	 *            the i oper
	 * @param request
	 *            the request
	 * 
	 * @return the int
	 * @throws ExceptionIS 
	 */
	public int doInputRequest( int iOper, HttpServletRequest request) throws ExceptionIS {
		return -1;
	}

	/**
	 * Do request.
	 * 
	 * @param Request
	 *            the request
	 * 
	 * @return the string
	 */
	public String doRequest( HttpServletRequest Request) {
		return null;
	}
	public QService createQService()
	{
		return new QService( this);
	}
	/**
	 * Do request.
	 * 
	 * @param Request the request
	 * @param Response the response
	 * 
	 * @return the dOM data
	 */
	public DOMData doRequest( HttpServletRequest Request, HttpServletResponse Response)
			throws ExceptionIS {
		DOMData Doc = null;
		String sReq = Request.getParameter( "REQUEST");
		if( sReq == null ){
			log( Request.getQueryString());
			clearAll();
			Doc = MainFormXML( Request, true);
			if( Doc != null )
				Doc.setFileXSL( this, "Main");
		}else if( sReq.equalsIgnoreCase( "Main") ){
			clearAll();
			Doc = MainFormXML( Request, true);
		}else if( sReq.equalsIgnoreCase( "MainMenu") ){
			Doc = MainMenuFormXML();
		}else if( sReq.compareToIgnoreCase( "ErrorForm") == 0 ){
			int iRet = ISServlet.getParameterInt( Request, "ID_RETURN");
			if( iRet != 9 )
				Doc = ErrorFormXML( Request);
		}else if( sReq.equalsIgnoreCase( "LogForm") ){
			if( isUserAdmin() )
				Doc = getLogFormXML( Request);
			else {
				Doc = getTemplate();
				setError( "Lubatud üksnes adminile");				
			}
		}else if( sReq.equalsIgnoreCase( "QueryForm") ){
			QService aQService = createQService();
			aQService.init( Request);
			aQService.setResponse( Response);
			Doc = aQService.doQuery( Request, false);
		}else if( sReq.equalsIgnoreCase( "DataTableForm") ){
			QService aQService = createQService();
			aQService.init( Request);
			Doc = aQService.doDataTable( Request);
		}else if( sReq.equalsIgnoreCase( "SearchForm") ){
			QService aQService = createQService();
			aQService.init( Request);
			aQService.setSearch( true);
			Doc = aQService.doSearch( Request);
		}else if( sReq.equalsIgnoreCase( "setPicture") ){
			QService aQService = createQService();
			aQService.init( Request);
			Doc = aQService.doQuery( Request, true);
		}else if( sReq.equalsIgnoreCase( "insertData") ){
			QService aQService = createQService();
			aQService.init( Request);
			if( aQService.insert( Request) ){
				Doc = aQService.doQuery( Request, false);				
			}else{
				Doc = aQService.getEmpty( Request);
			}
		}else if( sReq.equalsIgnoreCase( "updateData") ){
			QService aQService = createQService();
			aQService.init( Request);
			if( aQService.update( Request, true) ){
				Doc = aQService.doQuery( Request, false);				
			}else{
				Doc = aQService.getEmpty( Request);
			}
		}else if( sReq.equalsIgnoreCase( "getEmpty") ){
			QService aQService = createQService();
			aQService.init( Request);
			Doc = aQService.doQuery( Request, 2); // teeb rohkem
//			Doc = aQService.getEmpty( Request);
		}else if( sReq.equalsIgnoreCase( "getPicture") ){
			QService aQService = createQService();
			aQService.init( Request);
			if( !aQService.getPicture( Request, Response)){
//				Doc = getTemplate();
			}
/*			byte[] aPic = aQService.getPicture( Request);
			if( aPic != null ){
				ByteArrayInputStream aPics = new ByteArrayInputStream( aPic);
				try{
					BufferedImage aImage = ImageIO.read( aPics);
					if( aImage != null ){
						writeImageToOutput( Response, aImage, "image/gif");
					}
				}catch( IOException aE ){
					
				}
			}*/
		}else if( sReq.equalsIgnoreCase( "Log") ){
			if( ISServlet.hasParameter( Request, "Comment") ){
				log( Request.getParameter( "Comment"));
			}
			Doc = getTemplate();
			Doc.setHTML( false);
			return Doc;
		}else{
			Doc = getTemplate();
		}
		return Doc;
	}

	/**
	 * Form xml.
	 * 
	 * @param Request
	 *            the request
	 * @param Objects
	 *            the objects
	 * @param sClassName
	 *            the s class name
	 * @param bEdit
	 *            the b edit
	 * 
	 * @return the dOM data
	 */
	public DOMData FormXML( HttpServletRequest Request, OptionsList Objects, String sClassName,
			boolean bEdit) throws ExceptionIS {
		DataObject OldCurDataObject = CurDataObject;
		int iRet = 0;
		if( ISServlet.hasParameter( Request, "RETURN"))
			iRet = ISServlet.getParameterInt( Request, "RETURN");
		else
			iRet = ISServlet.getParameterInt( Request, "ID_RETURN");
		if( iRet == -1 ){
			int iNewID = 0;
			if( ISServlet.hasParameter( Request, "id"))
				iNewID = ISServlet.getParameterInt( Request, "id");
			else
				iNewID = ISServlet.getParameterInt( Request, "ID");
			if( Objects != null ){
				CurDataObject = ( DataObject)Objects.getObjectByValue( iNewID);
				if( CurDataObject == null ){
					CurDataObject = Objects.getDataObject( CurDataObject, sClassName, bEdit);
				}else if( bEdit )
					CurDataObject = ( DataObject)CurDataObject.clone(); // see
				// on
				// uus
				// or
				// 07.11.2007
			}
		}else if( Objects != null ){
			CurDataObject = Objects.getDataObject( CurDataObject, sClassName, bEdit);
			if( iRet >= 0 && CurDataObject != null && CurDataObject.getID() > 0
					&& !CurDataObject.mustSave( iRet) )
				CurDataObject = ( DataObject)Objects.getObjectByValue( CurDataObject.getValue());
		}else if( CurDataObject == null || !CurDataObject.isDataObject( sClassName) ){
			try{
				CurDataObject = ( DataObject) (Class.forName( sClassName).newInstance());
			}catch( Exception E ){
				Log.error( E, true);
				CurDataObject = null;
			}
		}
		if( CurDataObject == null ){
			if( OldCurDataObject != null )
				OldCurDataObject.exitForm();
			return getTemplate();
		}
		if( bEdit )
			bEdit = isUserAdmin( CurDataObject);
		if( CurDataObject.isNewForm() ){
			boolean bSave = iRet == 1;
			iRet = CurDataObject.ctrlFormXML( Request, this, bEdit);
			if( CurDataObject == null )
				return getTemplate();
			if( bSave && iRet != 0 && CurDataObject.getID() > 0 )
				CurDataObject = ( DataObject)CurDataObject.clone();
			return CurDataObject.getFormXML( Request, this, bEdit, iRet);
		}
		if( OldCurDataObject != null && !OldCurDataObject.equals( CurDataObject) )
			OldCurDataObject.exitForm();
		return CurDataObject.FormXML( Request, this, bEdit);
	}

	/**
	 * Checks for class method.
	 * 
	 * @param sClassName
	 *            the s class name
	 * @param sMethodName
	 *            the s method name
	 * 
	 * @return true, if successful
	 */
	public boolean hasClassMethod( String sClassName, String sMethodName) {
		try{
			Class.forName( sClassName).getDeclaredMethod( sMethodName, ( Class[])null);
		}catch( Exception E ){
			Log.error( E, true);
		}
		return false;
	}

	/**
	 * Checks for user rights.
	 * 
	 * @param C
	 *            the c
	 * @param iRet
	 *            the i ret
	 * 
	 * @return true, if successful
	 */
	@SuppressWarnings("rawtypes")
	public boolean hasUserRights( Class C, int iRet) {
		return true;
	}
	public boolean hasUserRights( String sTableName, int iRet) {
		return hasUserRights( sTableName, iRet, true);
	}
	public boolean hasUserRightsTo( String sTableName, int iRet) {
		return hasUserRights( sTableName, iRet, false);
	}
	public boolean hasUserRights( String sTableName, int iRet, boolean bOnlyTest) {
		return true;
	}

	/**
	 * Gets the temp path.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the temp path
	 */
	public String getTempPath( String sFileName) {
		return Servlet.getTempPath( sFileName);
	}

	/**
	 * Gets the real path.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the real path
	 */
	public String getRealPath( String sFileName) {
		return ISServlet.getRealPath( sFileName);
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return User;
	}

	/**
	 * Gets the user name.
	 * 
	 * @return the user name
	 */
	public String getUserName() {
		return (User == null)? null : User.getName();
	}
	public int getUserID() {
		return (User == null)? 0 : User.getID();
	}
	public int getUserRights() {
		return (User == null)? 0: User.getRights();
	}
	/**
	 * Gets the user name ip.
	 * 
	 * @return the user name ip
	 */
	public String getUserNameIP() {
		return (User == null)? null : User.getNameIP();
	}
	private ThreadLog aLog = null;
	public ThreadLog getLog(){ return aLog;}
	public void setLog( ThreadLog aLog){ this.aLog = aLog;}
	public void log( String sMsg) 
	{
		if( aLog != null ){
			aLog.log( sMsg);
			return;
		}
		String sUser = getUserName();
		if( sUser == null ) sUser = "?";

		ThreadLogTask aLogTask = getLogTask();
		if( aLogTask != null ){
			aLogTask.log( sUser + " : " + sMsg);
		}else if( User != null && User.getLogName() != null ){
			if( isDebug( 98)) Log.logFile( getLogFileName( "User.txt"), sMsg);
			else{
				Log.logFile( getLogFileName( "User.txt"), sUser + " : " + sMsg);
			}
		}else Log.final_log( null, sMsg);
	}
	public void log( int iDebug, String sMsg)
	{
		if( isDebug( iDebug) ) log( sMsg);
	}
	public void log( int iDebug, Object aObject)
	{
		if( isDebug( iDebug) )
			if( aObject.getClass().isArray() )
				log( iDebug, ( Object[])aObject);
			else if(  aObject instanceof ArrayList<?>)
				log( iDebug, ( ArrayList<?>)aObject);
			else log( aObject.toString());
	}
	public void log( int iDebug, ArrayList<?> aObjects)
	{
		if( isDebug( iDebug) ){
			for( int i = aObjects.size(); --i >= 0;){
				Object aObject = aObjects.get( i);
				log( aObject.toString());
			}
		}
	}
	public void log( int iDebug, Object[] aObjects)
	{
		if( isDebug( iDebug) ){
			for( int i = aObjects.length; --i >= 0;){
				Object aObject = aObjects[ i];
				log( aObject.toString());
			}
		}
	}
	public void log( Throwable aE) 
	{
		if( aLog != null ){
			aLog.log( aE);
			return;
		}
		String sUser = getUserName();
		if( sUser == null ) sUser = "?";
		
		ThreadLogTask aLogTask = getLogTask();
		if( aLogTask != null ){
			aLogTask.log( sUser);
			aLogTask.log( aE);
		}else{
			String sLogName = getLogFileName( "User.txt");
			Log.logFile( sLogName, aE);
		}
        getServlet().final_log( "Throwable: " + aE.getMessage() + " user: " + sUser);
	}

	/**
	 * Gets the template.
	 * 
	 * @return the template
	 */
	public DOMData getTemplate() throws ExceptionIS 
	{
		DOMData aDoc = new DOMData( ISServlet.getRealPath( "WEB-INF/xsl/Temp.xml"));
		addTemplate( aDoc);
		return aDoc;
	}
	public void addTemplate( DOMData aDoc)
	{
		Node aRoot = aDoc.getRootNode();
		aDoc.setChildNodeValue( aRoot, "ie", isIE());
		if( isIE() ) aDoc.setChildNodeValue( aRoot, "browser", "ie");
		else if( iBrowser == 1 ) aDoc.setChildNodeValue( aRoot, "browser", "firefox");
		else if( iBrowser == 2 ) aDoc.setChildNodeValue( aRoot, "browser", "opera");
		else if( iBrowser == 4 ) aDoc.setChildNodeValue( aRoot, "browser", "chrome");
		else if( iBrowser == 5 ) aDoc.setChildNodeValue( aRoot, "browser", "safari");
		aDoc.setChildNodeValue( aRoot, "version", getVersion());
		aDoc.setChildNodeValue( aRoot, "lang", getLang());
		aDoc.setChildNodeValue( aRoot, "rights", getUserRights());
		if( isPhoneOS() ) aDoc.setChildNodeValue( aRoot, "phone", sPhone);
		aDoc.addChildNode( aRoot, "width", iScreenWidth);
		aDoc.addChildNode( aRoot, "height", iScreenHeight);
	}
	public String getTempPath(){ return Servlet.getTempPath();}

	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	public Config getConfig() {
		return Servlet.getConfig();
	}
	public void setConfig( Config aConf) {
		Servlet.setConfig( aConf);
		init();
	}

	/**
	 * Gets the uRL.
	 * 
	 * @return the uRL
	 */
	public String getURL() {
		return sURL;
	}
	public String getPortApplication() {
		
		int i = sURL.indexOf(  ':');
		i = sURL.indexOf(  ':', ++i);
		int j = sURL.indexOf(  '/', i);
		j = sURL.indexOf(  '/', ++j);
		return sURL.substring( i, j);
	}

	/**
	 * Sets the uRL.
	 * 
	 * @param sURL
	 *            the new uRL
	 */
	public void setURL( String sURL) {
		int iS = sURL.lastIndexOf( '?');
		if( iS > 0 )
			this.sURL = sURL.substring( 0, iS);
		else
			this.sURL = sURL;
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
	 * Checks if is valid start.
	 * 
	 * @param Request
	 *            the request
	 * 
	 * @return true, if is valid start
	 */
	public boolean isValidStart( HttpServletRequest Request) {
		return User != null;
	}

	/**
	 * Clear all.
	 */
	public void clearAll() 
	{
		if( Db != null ) Db.close();
		getDatabase();
		CurDataObject = null;
		aLists = null;
		bFirst = true;
	}

	/**
	 * Gets the thread.
	 * 
	 * @return the thread
	 */
	public Thread getThread() {
		return aThread;
	}

	/**
	 * Sets the thread.
	 * 
	 * @param thread
	 *            the new thread
	 */
	public void setThread( Thread thread) {
		aThread = thread;
	}

	/**
	 * Gets the cur data object.
	 * 
	 * @param sClassName
	 *            the s class name
	 * 
	 * @return the cur data object
	 */
	public DataObject getCurDataObject( String sClassName) {
		return (CurDataObject != null && CurDataObject.isDataObject( sClassName))? CurDataObject
				: null;
	}

	/**
	 * Close form.
	 */
	public void closeForm() {
		CurDataObject = null;
	}

	/**
	 * Gets the debug.
	 * 
	 * @return the debug
	 */
	private int getDebug() {
		int iUserDebug = (User != null)? User.getDebug() : 0;
		int iServletDebug = Servlet.getDebug();
		if( iServletDebug > iUserDebug )
			iUserDebug = iServletDebug;
		return (iDebug > iUserDebug)? iDebug : iUserDebug;
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
		return getDebug() >= iDebug;
	}

	/**
	 * Creates the log cat.
	 * 
	 * @return true, if successful
	 */
	public boolean createLogCat() {
		if( isDebug( 98) ){
			File aCat = new File( getLogCatName());
			return aCat.mkdir();
		}
		return true;
	}

	/**
	 * Gets the log cat name.
	 * 
	 * @return the log cat name
	 */
	public String getLogCatName() {
		return isDebug( 98)? Servlet.getLogCatName() + "/" + ( (User != null && User.getLogName() != null )? User.getLogName() : "users")
				: Servlet.getLogCatName();
	}

	/**
	 * Gets the log file name.
	 * 
	 * @param sFileName
	 *            the s file name
	 * 
	 * @return the log file name
	 */
	public String getLogFileName( String sFileName) {
		return getLogCatName() + "/" + sFileName;
	}

	/**
	 * Gets the database.
	 * 
	 * @return the database
	 */
	public Database getDatabase() {
		if( Db == null ){
			try{
				if( DbDes != null )
					Db = new Database( DbDes, this);
			}catch( MException e ){
			}
		}
		return Db;
	}

	/**
	 * Checks if is user stat.
	 * 
	 * @return true, if is user stat
	 */
	public boolean isUserStat() {
		return aUserStat != null;
	}
	/**
	 * Creates the user stat.
	 * 
	 * @param aRequest
	 *            the a request
	 */
	public void createUserStat( HttpServletRequest aRequest) {
		if( Db != null && Db.hasTable( "stat_user") ){
			aUserStat = new UserStat( this, aRequest);
		}
	}

	/**
	 * Request user stat.
	 * 
	 * @param aRequest
	 *            the a request
	 */
	public void requestUserStat( HttpServletRequest aRequest) {
		if( aUserStat != null )
			aUserStat.request( aRequest);
	}

	/**
	 * Gets the user stat.
	 * 
	 * @return the user stat
	 */
	public UserStat getUserStat() {
		return aUserStat;
	}

	/**
	 * Sets the user stat.
	 * 
	 * @param userStat
	 *            the new user stat
	 */
	public void setUserStat( UserStat userStat) {
		aUserStat = userStat;
	}

	/**
	 * Checks if is user admin.
	 * 
	 * @param aObj
	 *            the a obj
	 * 
	 * @return true, if is user admin
	 */
	public boolean isUserAdmin( DataObject aObj) {
		return true;
	}

	/**
	 * Checks if is user admin.
	 * 
	 * @return true, if is user admin
	 */
	public boolean isUserAdmin() {
		return User != null && User.isAdmin();
	}

	/**
	 * Checks if is https.
	 * 
	 * @return true, if is https
	 */
	public boolean isHttps() {
		return bHttps;
	}

	/**
	 * Sets the https.
	 * 
	 * @param https
	 *            the new https
	 */
	public void setHttps( boolean https) {
		bHttps = https;
	}

	/**
	 * Gets the servlet.
	 * 
	 * @return the servlet
	 */

	/**
	 * Sets the debug.
	 * 
	 * @param debug
	 *            the new debug
	 */
	public void setDebug( int debug) 
	{
		iDebug = debug;
		createLogCat();
	}
	/**
	 * Checks if is iE.
	 * 
	 * @return true, if is iE
	 */
	private File aCat = null;
	public DOMData getLogFormXML( HttpServletRequest Request) throws ExceptionIS
	{
		DOMData Doc = getTemplate();
		if( aCat == null || !ISServlet.hasParameter( Request, "ID_RETURN") ){
			aCat = new File( getLogCatName());
			if( isDebug( 98) &&  aCat.getParentFile() != null ){
				aCat = aCat.getParentFile();
			}
		}
		if( aCat != null ){
			int iID = 0;
			String [] List = aCat.list();
//			File aFile = null;
			Node Root = Doc.getRootNode();

			int iRet = ISServlet.getParameterInt( Request, "ID_RETURN");
	 		if( iRet == -1 ){
	 			iID = ISServlet.getParameterInt( Request, "ID_LOG");
	 			if( iID > 0 ){
//		 			String sFileName = getLogFileName( List[ iID-1]);
		 			try {
		 				String sLine = null;
		 				File aFile = new File( aCat, List[ iID-1]); 
		 				if( aFile.isDirectory() ){
		 					aCat = aFile;
		 					List = aFile.list();
		 					Doc.addChildNode( Root, "sub_cat", true); 
		 				}else{
			 				StringBuffer Text = new StringBuffer();
							BufferedReader BR = new BufferedReader( new FileReader( aFile));
			 		        while ((sLine = BR.readLine()) != null){
			 		        	for( int iLine = 0, lLine = sLine.length(); iLine < lLine; iLine += 125){
			 		        		if( iLine > 0 ) Text.append("    ");
			 		        		if( iLine+125 < lLine) Text.append( sLine.substring( iLine, iLine+125));
			 		        		else Text.append( sLine.substring( iLine));
				 		        	Text.append("\r");
			 		        	}
			 		        }
			 		        BR.close();
			 				Doc.addChildNode( Root, "log", Text); 
		 				}
					} catch ( Exception E) {
						log( E);
					}
	 			}else{
	 				aCat = aCat.getParentFile();
	 				List = aCat.list();
	 			}
	 		}
			Doc.addChildNode( Root, "logs", List, iID); 			
		}
		return Doc;
	}
	public String getRequest( HttpServletRequest Request) 
	{
		return Servlet.getRequest( Request);
	}
	private OptionsList aLists = null;
	public synchronized QService getList( String sDataName)
	{
		QService aList = ( aLists != null)? ( QService)aLists.getObjectByValue( sDataName): null;
		if( aList == null ){
			
			aList = createQService();
			int i = sDataName.indexOf( '?');
			if( i> 0 ){
				aList.setTableName( sDataName.substring( 0, i));
				int j = sDataName.indexOf( '?', ++i);
				if( j > 0){
					aList.setListWhere( sDataName.substring( i, j));
					aList.setListOrder( sDataName.substring( ++j));
				}else
					aList.setListWhere( sDataName.substring( i));
			}else{
				aList.setTableName( sDataName);
			}
			if( aList.getDataName() != null ){
				aList.init();
				aList.createList();
				if( aLists == null ) aLists = new OptionsList();
				aLists.add( aList);
			}else aList = null; // tegin n��d! nii peaks tegema, kuid m�ni teine kord
		}
		return aList;
	}
	public QService  getListIfExsist( String sDataName)
	{
		 return ( aLists != null)? ( QService)aLists.getObjectByValue( sDataName): null;
	}
	public synchronized void removeList( String sDataName)
	{
		if( aLists != null ) aLists.removeObjectByValue( sDataName);
	}
	public boolean hasReturn( HttpServletRequest aRequest)
	{
		return ISServlet.hasParameter( aRequest, "RETURN") ||
			ISServlet.hasParameter( aRequest, "ID_RETURN");
	}
	public static int getReturn( HttpServletRequest aRequest)
	{
		return ISServlet.getReturn( aRequest);
/*		if( ISServlet.hasParameter( aRequest, "RETURN"))
			return ISServlet.getParameterInt( aRequest, "RETURN");
		if( ISServlet.hasParameter( aRequest, "ID_RETURN"))
			return ISServlet.getParameterInt( aRequest, "ID_RETURN");
		return 0; */
	}
	public String getPathXSL()
	{
		return getServlet().getPathXSL();
	}
	public DOMData MainFormXML( HttpServletRequest Request, boolean bEdit) throws ExceptionIS 
	{
		if( ISServlet.hasParameterValue( Request, "WIDTH"))
			iScreenWidth = ISServlet.getParameterInt( Request, "WIDTH");
		if( ISServlet.hasParameterValue( Request, "HEIGHT"))
			iScreenHeight = ISServlet.getParameterInt( Request, "HEIGHT");
		if( ISServlet.hasParameterValue( Request, "debug"))
			if( getUser().isAdmin() ) getUser().setDebug( ISServlet.getParameterInt( Request, "debug"));
		DOMData aDoc = getTemplate();
		Node aRoot = aDoc.getRootNode();
		if( aDoc.findChildNode( aRoot, "user") == null )
			User.setXML( aDoc, aDoc.addChildNode( aRoot, "user"));
		if( bFirst ){
			bFirst = false;
			aDoc.addChildNode( aRoot, "start");
		}
		return aDoc;
	}
	public DOMData MainMenuFormXML() throws ExceptionIS 
	{
		DOMData aDoc = getTemplate();
		Node aRoot = aDoc.getRootNode();
   		User.setXML( aDoc, aDoc.addChildNode( aRoot, "user"));
		return aDoc;
	}
	public void addProgress( String sLine) {
	}
	public boolean hasUserRightsXML( HttpServletRequest Request)
	{
		return User != null && isUserAdmin();
	}
	public boolean isSessionOK(   HttpServletRequest aRequest) {
		return true;
	}
	private boolean bDownload = false;
	public boolean isDownload(){ return bDownload;}
	public void setDownload( boolean bDownload){ this.bDownload = bDownload;}
}