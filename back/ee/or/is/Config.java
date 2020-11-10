/*
 * 
 */
package ee.or.is;

// import java.util.Enumeration;

// import java.io.Serializable;

import java.io.File;
import javax.servlet.*;

import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
//import org.apache.catalina.core.StandardContext;

import java.lang.reflect.Field;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import ee.or.db.DbDescr;

/**
 * The Class Config.
 * 
 * @author TA
 * 
 * Config kindlustab kogu rakendusele konfiguratsiooni info.
 * Rakenduse seisukohalt polegi oluline kust see tuleb
 * - millegi saamiseks p��rdutakse Config poole.
 * Praktiliselt loeb Config vajaliku info 'config.xml' failist.
 */
public class Config extends DOMData //implements Serializable
{

	/** The dbdescr. */
	private DbDescr[] 	dbdescr;		// Andmebaasi poole pöördumiseks vajalik info.
	public int getDbsSize() { return (dbdescr != null)? dbdescr.length: 0;}
										
	/** The s temp path. */
	private String		sTempPath;		// siia tulevad ajutised v�ljundid
	
	/** The Config. */
	private ServletConfig aServletConfig;
	
	/** The Servlet. */
	private ISServlet	Servlet = null;
	private boolean bFull = true;
	private boolean bTest = false;
	private boolean bLive = false;
	public boolean isTest() {	return bTest;}
	public boolean isLive() {	return bLive;}
	public boolean isDevelop() {	return !bLive && !bTest;}
// saaks teha veel preLive-i Live = true && Test = true, kuid seadistus? näiteks test on, kuid ei ole väärtustatud

	public Config( ISServlet Servlet, ServletConfig servconf) throws ExceptionIS
	{
	    super();
	    this.Servlet = Servlet;
	    aServletConfig = servconf;
	    String sFileName = getInitPath( Servlet.getConfigName());
/*	    
	    final ApplicationContext aContext = (ApplicationContext) get(ApplicationContextFacade.class, servconf.getServletContext());
//	       final StandardContext aStContext = (StandardContext) get(ApplicationContext.class, aContext);
//	    ServletContext aContext = servconf.getServletContext();
	    String sPath = aContext.getInitParameter( "path");
//	    String sPath1 = aContext.getContextPath();
//	    String sPath2 = aStContext.getDocBase();
//	    log( sPath);
//	    log( sPath1);
//	    log( sPath2);
//	    String sPath = servconf.getInitParameter( "path");
	    String sFileName = ( sPath != null )? sPath + "/" + Servlet.getConfigName(): ISServlet.getRealPath( Servlet.getConfigName()); */
	    if( loadFromFile( sFileName, "UTF-8", true) )	
	    	Servlet.log( "Config File: " + sFileName);
	    else												
	    	Servlet.log( "Error reading Config File: " + sFileName );
	} 
    public Config( Config aOldConfig) throws ExceptionIS
	{
	    super();
	    this.Servlet = aOldConfig.Servlet;
	    aServletConfig = aOldConfig.getServletConfig();
	    loadFromFile( ISServlet.getWebPath( Servlet.getConfigName()));
	} 
    private static Object get(final Class<?> clazz, final Object facade){
        Field field = null;
        boolean acc = false;
        try {
			field = clazz.getDeclaredField("context");
	        field.setAccessible(true);
			return field.get(facade);
		} catch ( Exception e) {
			e.printStackTrace();
        } finally {
            field.setAccessible( acc);
        }
        return null;
    }
	/**
	 * Gets the servlet config.
	 * 
	 * @return the servlet config
	 */
	public ServletConfig getServletConfig()
	{
		return aServletConfig;
	}
	/**
	 * Gets the servlet context.
	 * 
	 * @return the servlet context
	 */
	public ServletContext getServletContext()
	{
		return aServletConfig.getServletContext();
	}
	/**
	 * Gets the temp path.
	 * 
	 * @return the temp path
	 */
	public String getTempPath()
	{
	    if( sTempPath != null && sTempPath.charAt( 0) != '/' && sTempPath.charAt( 1) != ':' )
	        return getInitPath( sTempPath);
	    return sTempPath;
	}
	public String getInitPath( String sFileName) 
	{
		ApplicationContext aContext = (ApplicationContext) get( ApplicationContextFacade.class, aServletConfig.getServletContext());
		String sPath = aContext.getInitParameter( "path");
		return ( sPath != null )? sPath + "/" + sFileName: ISServlet.getRealPath( sFileName); 
	}
	/**
	 * Gets the first database descriptor 
	 * 
	 * @param sClassName the s class name
	 * 
	 * @return the database descriptor
	 */
	public DbDescr getDatabaseDescriptor() {
		return ( dbdescr!=null && dbdescr.length > 0)? dbdescr[0]: null;
	}
	/**
	 * Gets the database descriptor by name
	 * 
	 * @param sClassName the s class name
	 * 
	 * @return the database descriptor
	 */
	public DbDescr getDatabaseDescriptor( String sClassName) 
	{
		if( dbdescr != null ) for( int i = dbdescr.length; --i>=0;)
			if( dbdescr[ i].getDbclass().equalsIgnoreCase( sClassName) )
				return dbdescr[ i];
		return null;
	}
	/**
	 * Method readConfig.
	 * 
	 * @throws MException the m exception
	 */
	public void readConfig() throws ExceptionIS 
	{
		Element configroot = Doc.getDocumentElement();
		if( configroot.hasAttribute(  "debug") ){
		    int iDebug = getElemAttrInt( configroot, "debug");
		    Servlet.setDebug( iDebug);
		    log( " debug = " + iDebug);
		}
		if( configroot.hasAttribute(  "test") ) {
			bTest = getElemAttrBoolean( configroot, "test");
			bLive = false;
		}else {
			bTest = false;
			bLive = true;
		}
		Element Temp = getElement( configroot, "temp");
		if( Temp != null ){
			sTempPath = Temp.getAttribute( "path");
//			log( "Temp path: " + sTempPath);
		}else sTempPath = "work";
		String sError = null;
		String sWorkCatName = getTempPath();
		File aWorkCat = new File( sWorkCatName);
		if( aWorkCat != null ){
			if( !aWorkCat.exists() ){ 
				if( !aWorkCat.mkdir() ){ sError = "can't create work directory: " + sWorkCatName;}
			}else if( !aWorkCat.isDirectory()){ sError = "this isn't work directury: " + sWorkCatName;}
/*			if( !aWorkCat.canWrite() ){
				if( !aWorkCat.setWritable( true, false))
					sError = "there are no write rights in work directory: " + sWorkCatName + " and cat't  set it";
			}*/
		}
		if( sError != null ){
			Servlet.super_log( sError);
			sTempPath = null;
			throw new ExceptionIS( sError);
		}
		Servlet.createLogCatalog();
        if( bFull){
    		NodeList list 	= configroot.getElementsByTagName("database");
    		/*		if( list == null || list.getLength() <= 0 ){
    					String msg = "Viga konfiguratsiooni failis '" + sFilename + "' - puudub <database ...";
    					log( msg);
    					throw new MException( msg);
    				}
    				else*/
			try{
				getDatabaseConf( list, sFilename);
				CallAddedConfig( configroot, sFilename);
			}catch( MException aE ){
				throw new ExceptionIS( aE);
			}	
        }
	}
	
	/**
	 * Call added config.
	 * 
	 * @param configroot the configroot
	 * @param filename the filename
	 * 
	 * @throws MException the m exception
	 */
	public void CallAddedConfig( Element configroot, String filename) throws MException
	{
	} 
	/**
	 * Method getDatabaseConf.
	 * 
	 * @param elem the elem
	 * @param stderrmsg the stderrmsg
	 * 
	 * @return the database conf
	 * 
	 * @throws MException the m exception
	 */
	private DbDescr getDatabaseConf( Element elem, String stderrmsg) throws MException 
	{
		DbDescr aDbDescr = null;
		if( elem != null ){
			aDbDescr = new DbDescr();
			aDbDescr.setDbclass( getElemAttr( elem, "class",	stderrmsg + "class=''"));
			aDbDescr.setDbtypestr( getElemAttr( elem, "typestr",	stderrmsg + "typestr=''"));
			aDbDescr.setDbname( getElemAttr( elem, "dbname",	stderrmsg + "dbname=''"));
			aDbDescr.setDrivername( getElemAttr( elem, "drivername", stderrmsg + "drivername=''"));
			aDbDescr.setDbuser( getElemAttr( elem, "user", stderrmsg + "user=''"));
			aDbDescr.setDbpasswd( getElemAttr( elem, "passwd", stderrmsg + "passwd=''"));
			String sAttr = elem.getAttribute( "host");
			if( sAttr != null && sAttr.length() > 0 ){
				aDbDescr.setDbhost( getElemAttr( elem, "host", stderrmsg + "host=''"));
			}else 
				aDbDescr.setDbhost( ISServlet.getWebPath());
			sAttr = elem.getAttribute( "srid");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setDbGeometry( sAttr);
			sAttr = elem.getAttribute( "codebase");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setCodebase( sAttr);
			sAttr = elem.getAttribute( "encoding");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setEncoding( sAttr);
			sAttr = elem.getAttribute( "date_format");
			if( sAttr != null && sAttr.length() > 0 ){
				aDbDescr.setDateFormat( sAttr);
			    log( "date_format='" + sAttr + "'");
			}
			sAttr = elem.getAttribute( "schema");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setSchemaName( sAttr);

			sAttr = elem.getAttribute( "time_format");
			if( sAttr != null && sAttr.length() > 0 ){
				aDbDescr.setDateTimeFormat( sAttr);
			    log( "time_format='" + sAttr + "'");
			}
			sAttr = elem.getAttribute( "timeout");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setTimeout( sAttr);
			Node aNode = findChildNode( elem, "database");
			aDbDescr.setSubDbDescr( getDatabaseConf( ( Element)aNode, stderrmsg));
			sAttr = elem.getAttribute( "name");
			if( sAttr != null && sAttr.length() > 0 ) aDbDescr.setDbclass( sAttr);
		}
		return aDbDescr;
	}
	
	/**
	 * Gets the database conf.
	 * 
	 * @param list the list
	 * @param filename the filename
	 * 
	 * @return the database conf
	 * 
	 * @throws MException the m exception
	 */
	private void getDatabaseConf( NodeList list, String filename) throws MException {
		String stderrmsg = "Viga konfig. failis '" + filename + "' puudub ";

		if( list != null ){
			int n = list.getLength();
			if( n > 0 ){
				dbdescr = new DbDescr[ n];		
				for( int i = n; --i >= 0;){
					dbdescr[ i] = getDatabaseConf( (Element) list.item( i), stderrmsg); 
						
					log( " config, db: driver='" + dbdescr[ i].getDatabaseUrl());
		}	}	}
	}
    public void log( String sMsg)
    {
    	Servlet.log( sMsg);
    }
    public void log( Throwable aE)
    {
    	Servlet.log( aE);
    }
    /**
     * Gets the servlet.
     * 
     * @return the servlet
     */
    public ISServlet getServlet()
    {
        return Servlet;
    }
	
	/**
	 * Gets the element attr.
	 * 
	 * @param sElement the s element
	 * @param sAttr the s attr
	 * 
	 * @return the element attr
	 * 
	 * @throws MException the m exception
	 */
	public String getElementAttr( String sElement, String sAttr) throws MException
	{
		NodeList list = Doc.getDocumentElement().getElementsByTagName( sElement);
		if( list != null ){
			if( list.getLength() > 0 ){
				Element E = (Element) list.item( 0);
				return E.getAttribute( sAttr);
			}
		}
		return null;
	}
	public void setMinRead()
	{
		bFull = false;
	}
	public void reloadDatabaseConf()
	{
		Element configroot = Doc.getDocumentElement();
		NodeList list = configroot.getElementsByTagName("database");
		try{
			getDatabaseConf( list, sFilename);
			CallAddedConfig( configroot, sFilename);
		}catch( Exception aE ){
			log( aE);
		}	
	}
}
