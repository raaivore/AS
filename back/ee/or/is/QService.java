package ee.or.is;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
// import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.Date;
import java.util.Iterator;

import org.imgscalr.*;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ee.or.db.*;
import ee.or.geom.Geom;
import ee.or.geom.GeomArea;
import ee.or.geom.GeomPoint;
import ee.or.geom.GeomPointLL;
import ee.or.gis.*;
// import oracle.jdbc.OracleResultSet;

public class QService implements OptionElement
{
	private Sight aSight = null;
	public Sight getSight() {	return aSight;}
	private ISServlet aServlet = null;
	public ISServlet getServlet() {	return aSight.getServlet();}
	
	private String sTableName = null;
	public String getTableName(){  	return sTableName;}
    public void setTableName( String sTableName){ if( sTableName != null) this.sTableName = sTableName.trim();}
    public String getTagName(){  	return sTableName;}
    public String getTableNameSql(){ return sTableName.equalsIgnoreCase( "user")? "\"" + sTableName + "\"": sTableName;}

	private String sKeyName = "id";
	public String getPKeyName(){  	return sKeyName;}
	public void setPKeyName( String sPKeyName){ if( sPKeyName != null) this.sKeyName = sPKeyName.trim();}
	public boolean isPKeyChar()
	{
		return false;
	}
	private boolean bEdit = true; 
	public boolean isEdit() { return bEdit;}
	   
	private String sDataName = "name";
	public String getDataName(){ return sDataName;}
	public void setDataName( String sDataName){ this.sDataName = sDataName;}

	private int iID = 0;
    public int getID(){	return iID;}
    public void setID( int iID) { this.iID = iID;}
    
	private String sWhere = null;
	public String getWhere(){ return sWhere;}
	public void setWhere( String sWhere){ this.sWhere = sWhere;}
	public void addWhere( String sWhere){ 
		if( this.sWhere == null ) this.sWhere = sWhere;
		else this.sWhere += " and " + sWhere;
	}

	private String sOrder = null;
	public String getOrder(){ return sOrder;}
	public void setOrder( String sOrder){ this.sOrder = sOrder;}
	
	private String sFieldName = null;
	
//	private boolean bEdit = false;
	private boolean bAddList = true;
	public boolean isAddList(){	return bAddList;}
	public void setAddList( boolean bAddList){ this.bAddList = bAddList;}

	private String sListWhere = null;
	public String getListWhere(){	return sListWhere;}
	public void setListWhere(String sListWhere){ this.sListWhere = sListWhere;}

	private String sListOrder = null;
	public String getListOrder(){	return sListOrder;}
	public void setListOrder(String sListOrder){ this.sListOrder = sListOrder;}
	
	private String sListField = null;
	public String getListField(){  	return sListField;}
	public boolean bQuery = false;
	
	private OptionsList aDatas = null;
	public OptionsList getDatas(){	return aDatas;}
	public void setDatas(OptionsList aDatas){ this.aDatas = aDatas;}

	private Marray aRefs = null;
	public Marray getRefs(){	return aRefs;}

//	ResultSet aMetaData = null;
	
	boolean bSearch = false; 
	public boolean isSearch(){ return bSearch;}
	public void setSearch( boolean bSearch){ this.bSearch = bSearch;}

	private Geom aGeom = null;
	public Geom getGeom(){	return aGeom;}
	
	private QServiceDO aDO = null;
	public QServiceDO getCurrentDO(){	return aDO;}
	public void setCurrentDO( QServiceDO aDO){ this.aDO = aDO;}
	
	private String sFormat = null;
	public void setFormat(String sFormat){	this.sFormat = sFormat;}
	public boolean outputKML(){ return sFormat != null && sFormat.equalsIgnoreCase( "kml");}
	public boolean outputSHP(){ return sFormat != null && sFormat.equalsIgnoreCase( "shp");}
	public boolean outputGML(){ return sFormat != null && sFormat.equalsIgnoreCase( "gml");}
	public boolean isFormat(){ return sFormat != null && !sFormat.equalsIgnoreCase( "xml");}
	public boolean isCsv(){ return sFormat != null && sFormat.equalsIgnoreCase( "csv");}

	private String sNameSpace = null;
	public String getNameSpace(){	return sNameSpace;}
	public void setNameSpace( String sNameSpace){ this.sNameSpace = sNameSpace;}
	private String getName( String sName){ return (sNameSpace != null)? (sNameSpace + ":" + sName): sName;}
	
	private int iRecCount = 0;
	public int getRecCount(){ return iRecCount;}

	private HttpServletResponse aResponse = null;
	public HttpServletResponse getResponse() {		return aResponse;}
	public void setResponse( HttpServletResponse aResponse) {	this.aResponse = aResponse;}
	
	private boolean bOrderXY = true;
	public boolean isOrderXY(){		return bOrderXY;}
	public void setOrderXY( boolean bOrderXY){	this.bOrderXY = bOrderXY;}
	
	public QService( Sight aSight)
	{
		this.aSight = aSight;
		aServlet = aSight.getServlet();
	}
	public QService( ISServlet aServlet)
	{
		this.aServlet = aServlet;
	}
	public void init( HttpServletRequest Request)
	{
		if( ISServlet.hasParameter( Request, "TABLE")){
			sTableName = Request.getParameter( "TABLE");
/*			int i = sTableName.indexOf( '.');
			if( i > 0){
				String sSchemaName = sTableName.substring( 0, i);
				sTableName = sTableName.substring( ++i);
				aSight.getDatabase().setScema( sSchemaName);
			}*/
			bEdit = aSight.hasUserRights( sTableName, 0); // uus 19.12.2018 ja ka j�rgmine
		}
		if( bEdit && ISServlet.hasParameter( Request, "EDIT") )
			bEdit = ISServlet.getParameterBoolean( Request, "EDIT");
		if( ISServlet.hasParameter( Request, "FIELD")) sFieldName = Request.getParameter( "FIELD");
		if( ISServlet.hasParameter( Request, getPKeyName()) ){
//			sWhere = null;
			sWhere = ( ISServlet.hasParameter( Request, "WHERE"))? Request.getParameter( "WHERE"): null;
			iID = ISServlet.getParameterInt( Request, getPKeyName());
			if( ISServlet.hasParameter( Request, "ORDER")) sOrder = Request.getParameter( "ORDER");
		}else if( ISServlet.hasParameter( Request, "WHERE")){
			sWhere = Request.getParameter( "WHERE");
			if( sWhere.length() == 0 ){ // WHERE-is �ksi on v�he - peab olema ikka tingimus
				sWhere = null;
				iID = -1;
				
			}else{
				iID = getKeyFromWhere( sWhere);
				bAddList = !( iID > 0);			
				
	/*			int i = sWhere.indexOf( "=" + getPKeyName() + "=");
				if( i>= 0 ){
					try{
						iID = Integer.parseInt( sWhere.substring( i+3));
//						bAddList = false; see kirjutatakse �le
					}catch( NumberFormatException aE ){
						iID = 0;
					}
				}else iID = 0; */
			}
			if( ISServlet.hasParameter( Request, "ORDER")) sOrder = Request.getParameter( "ORDER");
		}else{
			sWhere = null;
			iID = 0;
		}
		if( ISServlet.hasParameter( Request, "LIST_WHERE")){
			sListWhere = Request.getParameter( "LIST_WHERE");
			bAddList = true;
		}
		if( ISServlet.hasParameter( Request, "LIST_FIELD")) sListField = Request.getParameter( "LIST_FIELD");
		if( ISServlet.hasParameter( Request, "format")){
			sFormat = ISServlet.getParameterString( Request, "format");
			
			if( sFormat.equals( "html") ) sFormat = null; // his päring muidu ei tööta
		}
	}
	public void init()
	{
	}
	public DOMData doQuery( HttpServletRequest aRequest, boolean bInserPicture) throws ExceptionIS  
	{ // see on vanade s�steemidega liidestamiseks
		return doQuery( aRequest, bInserPicture? 1: 0);
	}
	public DOMData doQuery( HttpServletRequest Request, int iOper)	throws ExceptionIS 
	{
		int iRet = 0, iOutRet = 0;
		boolean bRet = ISServlet.hasParameter( Request, "RETURN");
		DOMData aDoc = null;
		
		if( iOper == 1 ){ // insert picture
			bRet = true;
			bEdit = aSight.hasUserRights( sTableName, 0);
			if( bEdit && ISServlet.hasParameter( Request, "EDIT"))
				bEdit = ISServlet.getParameterBoolean( Request, "EDIT");
			if( bEdit && setPicture( Request) ){
				if( iID == 0 ) iRet = 1;
			}else{
				bAddList = false;
				aDoc = getEmpty( Request);
//				aSight.addError( aDoc);
			}
		}else if( iOper == 2 ){ // get Empty
			bRet = true;
			bEdit = true;
			aDoc = getEmpty( Request);
		}else if( sWhere != null || iID < 0 ){
//		}else if( sWhere != null && iID <= 0 && !isPKeyChar()){
			/*			if( ISServlet.hasParameter( Request, "LIST_WHERE")){
			sListWhere = Request.getParameter( "LIST_WHERE");
			bAddList = !( sListWhere == null || sListWhere.length() == 0 );
			// kui LIST_WHERE on k�ll parameeter, kuid t�hi, siis listi ei taheta
		}else */
			// bEdit = false; // panin tagasi 08.11.2018 - enne oli selle l�pus. Seda asja peab veel m�tlema
			bAddList = false;
			iRet = ISServlet.getParameterInt( Request, "RETURN");
			if( iRet != 0 ) {
		    	if( iRet == 1){
	    			if( !insert( Request) ){
	    			}
	    		}else
	    			iOutRet = doUserQuery( Request, iRet);			
			}
			int iLimit = getLimit( Request); 
			String sOffset = null;
			if( ISServlet.hasParameter( Request, "OFFSET")){
				int iOffset = ISServlet.getParameterInt( Request, "OFFSET");
				if( iOffset < 0 ){
					iOffset = getTableRecCount() - iLimit;
					sOffset = Integer.toString( iOffset);
				}else  sOffset = Request.getParameter( "OFFSET");
			}
//			if( ISServlet.hasParameter( Request, "LIMIT")) sLimit = Request.getParameter( "LIMIT");
			
			String sSQL = getQuery( sTableName, sWhere, sOrder, sOffset, iLimit, false);

			aDoc = getXML( aSight.getDatabase(), getTagName(), sSQL);
			if( iLimit > 0 ) aDoc.addChildNode( aDoc.getRootNode(), "limit", iLimit);
			if( sOffset != null ) aDoc.addChildNode( aDoc.getRootNode(), "offset", sOffset);
			bEdit = aSight.hasUserRights( sTableName, 1, true);
		}else{
//	    	bEdit = aSight.isUserAdmin(); // aSight.isUserAdmin( aObj)
//			bAddList = sWhere == null; ka where korral tuleks luua list
		    if( bRet ){
		    	iRet = ISServlet.getParameterInt( Request, "RETURN");
		    	bEdit = aSight.hasUserRights( sTableName, iRet);
		    	if( bEdit ){
/*			    	if( iRet == 0 ){
						if( !update( Request, false) ) aDoc = getEmpty( Request);
			    	}else */
			    	if( iRet == 1){
			    		if( iID > 0 || isPKeyChar() && sWhere != null ){ 
			    			if( !update( Request, false) ){
			    				aDoc = getEmpty( Request);
			    			}
			    		}else if( !insert( Request) ){
			    			aDoc = getEmpty( Request);
			    		}
			    	}else if( iRet == 2){ 
				    	if( iID > 0){
				    		boolean bOldAddList = bAddList; 
				    		if( sWhere == null ){
				    			bAddList = false;
				    			String sSQL = getQuery( sTableName, null, null, null, 0, false);
				    			aDoc = getXML( aSight.getDatabase(), sTableName, sSQL);
				    		}
							if( delete( Request)){
								iID = 0;
								if( getWhere() != null ) aDoc = null; // 20.03.2019 tabelisse uute kirjete lisamine ja eemaldamine
								else if( aDoc != null ) aDoc.addChildNode( aDoc.getRootNode(), "ok", iRet);
							}else{
							}
							bAddList = bOldAddList;
							if( bAddList && aDoc != null ) addList( aDoc, aDoc.getRootNode());
				    	}
			    	}else if( iRet == 3){ // koordinaatide muutus
						aDoc = getEmpty( Request);
			    	}else if( iRet == 4){ // osaline muutus
						if( !update( Request, false) ){
//							aDoc = getEmpty( Request);
// siin on probleem, sest pole �ige ei t�hja v�ljastamine ega ka praegune ehk sisestuse eiramine,
// kuid baasi v��rtuste n�itamine on siiski parem.
// Sama probleem on ka p�ris update-iga, kuid seal v�ib eeldada, et k�ik olulised v�ljad on uuendatavad
// Muidugi j��b ikkagi probleemiks, et mis teha muudetud v�ljaga
						}
			    	}else if( iRet == 11){ // save only Picture
			    		setPicture( Request, false);
			    		iOutRet = 1;
			    	}else if( iRet == 12){ // goto
			    		iOutRet = gotoObject( Request);
			    	}else if( iRet == 13){ // empty
			    		aDoc = clear( Request);
			    	}else if( iRet != -1 ){
			    		iOutRet = doUserQuery( Request, iRet);
			    		if( iOutRet < 0 ){
			    			aDoc = getEmpty( Request);
			    			aDoc.addChildNode( aDoc.getRootNode(), "ok", iRet);
			    		}
//			    		if( iOutRet > 0 ){
//			    			aDoc.addChildNode( aDoc.getRootNode(), "ok", iRet);
//			    		}
			    	}
		    	}
		    }else if( sTableName != null)
		    	bEdit = aSight.hasUserRights( sTableName, 0);
		}
	    if( aDoc == null ){
	    	if( sTableName == null ) aDoc = aSight.getTemplate();
	    	else if( iID > 0){
				String sSQL = getQuery( sTableName, null, null, null, 0, false);
				aDoc = getXML( aSight.getDatabase(), sTableName, sSQL);
/*				if( aSight instanceof GISSight ) 
				if( (( GISSight)aSight).hasMarkers() ){
					(( GISSight)aSight).clearMarkers(); Miks? 16.03.2019
					iOutRet = 4;
				} */
				bEdit = aSight.hasUserRights( sTableName, 1, true);
	    	}else if( sWhere != null ){ // see on uus ja siia tuleb n�iteks p�rast kasutaja t��tlust
	    		bAddList = false;
				String sSQL = getQuery( sTableName, sWhere, sOrder, null, getLimit( Request), false);
				aDoc = getXML( aSight.getDatabase(), sTableName, sSQL);
				aDoc.addChildNode( aDoc.getRootNode(), "limit", getLimit( Request));
				bEdit = aSight.hasUserRights( sTableName, 1, true);
	    	}else{
	    		bEdit = aSight.hasUserRights( sTableName, 1, true);
//	    		aDoc = getEmpty( null); // Request); -1ja iID=0 n�uavad nii
	    		aDoc = getEmpty( (iRet==-1)? null : Request); // see on eelmisele lisatud, sest v�ib-olla on miskit jua ette antud
	    		// 01.12.2013 siin oli bRet, kuid siis ei saa teha kaheastmelisi valikuid
	    		// ikkagi ei saa kliendi poolt midagi ette anda, sest -1 nullib k�ik
	    	}
	    	if( bRet /* && bEdit */ && iRet >= 0 ){
	    		// 11.12.2018 ei saa aru, et mika siin on 2 kordne kontroll. Panin selle iRet > 20 juurde
	    		if( iRet == 0 || iRet == 1 || iRet == 2 && iID == 0 || iRet > 20 && iOutRet > 0)
	    			aDoc.addChildNode( aDoc.getRootNode(), "ok", iRet);
	    	}
	    }
		if( bEdit && ISServlet.hasParameter( Request, "EDIT"))
			bEdit = ISServlet.getParameterBoolean( Request, "EDIT");
		if( aDoc != null ){
			Node aRoot = aDoc.getRootNode();
			aDoc.addChildNode( aRoot, "edit", bEdit);
			if( sWhere != null ){
				aDoc.addChildNode( aRoot, "where", sWhere);
				if( sOrder != null ) aDoc.addChildNode( aRoot, "order", sOrder);
			}
			if( sListWhere != null ) aDoc.addChildNode( aRoot, "list_where", sListWhere);
			if( addRef( bEdit, iRet) ){
				for( int i = aRefs.size(); --i >= 0;){
       				QService aFService = ( QService)aRefs.getValue( i);
       				if( addList( aFService) ){
       					aDoc.addChildNode( aRoot, aFService.getTableName(), aFService.getList(), 0, false, false); 
       				}
				}
			}
/*			if( getSight().isError() ) { // aDoc.addChildNode( aDoc.getRootNode(), "ok", -iRet);
				aDoc.addChildNode( aRoot, "error", aSight.getError());
				aSight.clearError();
			} or 23.04.1019 see on Servlet-is ka olemas */
			if( iOutRet != 0 ) aDoc.addChildNode( aRoot, "return", iOutRet);
			setFileXSL( aDoc, Request);
			if( aSight.getComment() != null ){
				aDoc.addChildNode( aRoot, "msg", aSight.getComment());
				aSight.setComment();
			}
		}
		return aDoc;
	}
	public DOMData clear( HttpServletRequest aRequest) throws ExceptionIS
	{
		iID = 0;
		return getEmpty( null);
	}
	
	public boolean addRef( boolean bEdit, int iRet){ return ( bEdit || bSearch /* && sWhere == null*/ || iRet == -2) && aRefs != null ;}
	public void setFileXSL( DOMData aDoc, String sXSL)
	{
		aDoc.setFileXSL( aSight, sXSL);
	}
	public void setFileXSL( DOMData aDoc, HttpServletRequest aRequest)
	{
		String sXsl = ISServlet.getParameterStringNull( aRequest, "XSL");
		if( sXsl == null ){
			String sRequest = getSight().getRequest( aRequest);
			if( sRequest.equalsIgnoreCase( "QueryForm") ) sXsl = ( iID > 0)? "Form": "List";
			else sXsl = sRequest;
		}
		if( ISServlet.hasParameter( aRequest, "format") ){ // ette on antud tagastusformaat
			String sFormat = ISServlet.getParameterString( aRequest, "format");
			if( !(sFormat.equalsIgnoreCase( "xml") || sFormat.equalsIgnoreCase( "html")) ) sXsl += sFormat.toUpperCase();
		}
		aDoc.setFileXSL( aSight, sXsl);
	}
	public boolean addList( QService aList)
	{
		return true;
	}
	public int doUserQuery( HttpServletRequest aRequest)	throws ExceptionIS 
	{
		int iRet = getReturn( aRequest);
		if( iRet == 1 ) return update( aRequest, false)? 0: 0;
		return doUserQuery( aRequest, iRet);
	}
	public int doUserQuery( HttpServletRequest Request, int iRet)	throws ExceptionIS 
	{
		return 0;
	}
	public DOMData doSearch( HttpServletRequest Request)	throws ExceptionIS 
	{
		DOMData aDoc = getEmpty( Request);
		if( aDoc != null ){
			Node aRoot = aDoc.getRootNode();
			if( aRefs != null ){
				for( int i = aRefs.size(); --i >= 0;){
	   				QService aFService = ( QService)aRefs.getValue( i);
	   				if( addList( aFService) ){
	   					aDoc.addChildNode( aRoot, aFService.getTableName(), aFService.getList(), 0, false, false); 
	   				}
				}
			}
			if( sWhere != null ) aDoc.addChildNode( aRoot, "where", sWhere);
			aDoc.addChildNode( aRoot, "limit", getLimit( Request));
//			setFileXSL( aDoc, Request);
		}
		return aDoc;
	}
	public DOMData doDataTable( HttpServletRequest aRequest)	throws ExceptionIS 
	{
		DOMData aDoc = aSight.getTemplate();
		Node aRoot = aDoc.getRootNode();
		StringBuffer aURL = new StringBuffer( "?");
		aURL.append( "REQUEST=QueryForm&TABLE="); 
		aURL.append( getTableName()); 
		aURL.append( "&WHERE="); 
		aURL.append( getWhere());
		if( ISServlet.hasParameter( aRequest, "XSL") ){
			aURL.append( "&XSL="); 
			aURL.append( aRequest.getParameter( "XSL")); 
		}
		aDoc.addChildNode( aRoot, "URL", aURL.toString());
		aDoc.setFileXSL( aSight, "DataTableForm");
		aDoc.addChildNode( aRoot, "Images", getTableName().equalsIgnoreCase( "vrakk"));
		return aDoc;
	}
	public void setRefs()
	{
		if( aRefs == null && sTableName != null ){
			ResultSet rs = null;
			try {
				rs = aSight.getDatabase().getTableKeys( isOracle()? sTableName.toUpperCase(): sTableName); 
				if( rs != null ){
					while( rs.next()){
						String sPTableName = rs.getString( "PKTABLE_NAME");
						if( isRef( sPTableName) ){
							String sFColName = rs.getString( "FKCOLUMN_NAME");
//							String sPColName = rs.getString( "PKCOLUMN_NAME");
//							String sFTableName = rs.getString( "FKTABLE_NAME");
							QService aFService = getList( sPTableName);
							addRef( sFColName, aFService);
						}
					}
					rs.close();
				}
			
			} catch( Exception e) {
				aSight.log( e);
			}finally{
				try{
					if( rs != null ) rs.close();
				}catch( SQLException aE ){
				}
			}
		}
	}
	public QService getList(String sTableName){
		return aSight.getList( sTableName);
	}
	public boolean isRef( String sTableName)
	{
		return true;
	}
	public void addRef( String sColName, QService aFService)
	{
		if( aRefs == null ) aRefs = new Marray();
		aRefs.put( sColName, aFService);
	}
	public boolean addQuery( String sColName, String sTypeName)
	{
		if( sTypeName.equalsIgnoreCase( "bytea") )
			if( outputSHP() || outputKML() || outputGML() ) return false;
		if( sColName.indexOf( "?") >= 0 )  return false; // kui view l�heb sassi
		return true;
	}
	public String getQuery()
	{
		return getQuery( aSight.getDatabase(), sTableName, null, sOrder, null, 0, false);
	}
	public String getQuery( String sWhere)
	{
		return getQuery( aSight.getDatabase(), sTableName, sWhere, sOrder, null, 0, false);
	}
	public String getQuery( String sTableName, String sWhere, String sOrder, String sOffset, int iLimit, 
		boolean bGeometryOnly)
	{
		return getQuery( aSight.getDatabase(), sTableName, sWhere, sOrder, sOffset, iLimit, bGeometryOnly);
	}
	public String getQuery( String sTableName, String sWhere, String sOrder, String sOffset, int iLimit, 
		boolean bGeometryOnly, boolean bUpdate)
	{
		return getQuery( aSight.getDatabase(), sTableName, sWhere, sOrder, sOffset, iLimit, bGeometryOnly, bUpdate);
	}
	public String getQuery( Database aDb, String sTableName, String sWhere, String sOrder, String sOffset, int iLimit, 
		boolean bGeometryOnly)
	{
		return getQuery( aDb, sTableName, sWhere, sOrder, sOffset, iLimit, bGeometryOnly, false);
	}
	public String getQuery( Database aDb, String sTableName, String sWhere, String sOrder, String sOffset, int iLimit, 
			boolean bGeometryOnly, boolean bUpdate)
	{
		StringBuffer aCall = new StringBuffer( "select ");
		int i = 0;
		try {
			ResultSet aMetaData = null;
			String sSchemaName = null;
			int n = sTableName.indexOf( '.');
			if( n > 0){
				sSchemaName = sTableName.substring( 0, n);
				sTableName = sTableName.substring( ++n);
				aMetaData = aDb.getMetaData( sSchemaName, isOracle()? sTableName.toUpperCase(): sTableName);
			}else
				aMetaData = aDb.getMetaData( isOracle()? sTableName.toUpperCase(): sTableName);
			if( aMetaData == null ){ // baasis puudub vastav tabel, siis otsime p�ringut config failist
				Config aConfig = getServlet().getConfig();
				Element aRoot = ( Element)aConfig.getRootNode();
				String sSQL = aConfig.getValue( aRoot, sTableName);
				if( sSQL != null ){
					return sSQL.replaceAll( "%1", sWhere);
				}
			}
			if( aMetaData != null ){
				while ( aMetaData.next() ) {
					if( aMetaData.getString( "TABLE_NAME").equalsIgnoreCase( sTableName) ){
//						String sTableName2 = aMetaData.getString( "TABLE_NAME");
						String sColName = aMetaData.getString( "COLUMN_NAME");
						String sTypeName = aMetaData.getString( "TYPE_NAME");
//						String sShemaName = aMetaData.getString( "TABLE_SCHEM");
//						String sCatName = aMetaData.getString( "TABLE_CAT");
						if( !addQuery( sColName, sTypeName) );
						else if( sTypeName.equalsIgnoreCase( "geometry") ){
							if( i++ > 0 ) aCall.append( ", ");
							if( outputSHP() ){
								aCall.append( sColName);
							}else if( outputGML() ){
								aCall.append( "st_asgml( 2, ST_Force2d( ");
								if( (( ee.or.wfs.QService)this).getSrid() != null ){
									aCall.append( " transform( ");
									aCall.append( sColName);
									aCall.append( ", 4326)");
								}else
									aCall.append( sColName);
								aCall.append( "), 4, "); // oli 10, enne 0 enne 7
								aCall.append( 0); // oli bOrderXY? 4: 20 oli 5 oli 0 enne 7
								aCall.append( ") as "); // oli 0 enne 7
								aCall.append( sColName); // "gml_" + 
								if( bGeometryOnly ) break;
							}else if( outputKML() ){
								aCall.append( "st_askml( ");
								aCall.append( sColName);
								aCall.append( ") as geom_"); // as coord");
								aCall.append( sColName);
								if( bGeometryOnly ) break;
							}else{
								aCall.append( "st_asewkt( ");
								aCall.append( sColName);
								aCall.append( ") as geom_"); // as coord");
								aCall.append( sColName);
								if( bGeometryOnly ) break;
							}
//							iLimit = 0;
						}else if( bGeometryOnly){
						}else if( sTypeName.equalsIgnoreCase( "bytea") ){
/*							if( outputSHP() || outputKML() || outputGML() ){
								
							}else{ */
								if( i++ > 0 ) aCall.append( ", ");
								aCall.append( sColName);
								if( bUpdate ){
								}else{
									// näitab kas binaarne asi on olemas v�i mitte
									aCall.append( " is not null as ");
									aCall.append( sColName);
									aCall.append( "_exists");
									aCall.append( ", ");
									aCall.append( sColName);
								}
//							}
						}else{
							if( i++ > 0 ) aCall.append( ", ");
							aCall.append( sColName);
						}
//						int type = aMetaData.getInt("DATA_TYPE");
					}
				}
				if( i == 0 ) return null;
			}else aCall.append( "*");
			aCall.append( " from ");
			if( sSchemaName != null ){
				aCall.append( sSchemaName);
				aCall.append( '.');
			}
			aCall.append( getTableNameSql());
			addQuery( aMetaData, aCall);
			if( getID() > 0 ){
				aCall.append( " where ");
				aCall.append( getPKeyName());
				aCall.append( "=");
				aCall.append( getID());
				iLimit = 0;
			}else{
				String sNewQuery = getQueryWhere( sWhere);
				if( sNewQuery != null ){
					aCall.append( " where ");
					aCall.append( sNewQuery);
					if( this.sWhere != null ){
						this.sWhere = sNewQuery;
					}
				}
			}
			if( sOrder != null){
				aCall.append( " order by ");
				aCall.append( sOrder);
			}
			if( sOffset != null){
				aCall.append( " offset ");
				aCall.append( sOffset);
			}
			if( !isOracle() && iLimit > 0 ){
				aCall.append( " limit ");
				aCall.append( iLimit);
			}
		} catch( Exception e) {
			aSight.log( e);
			aCall.append( "*");
		}finally{
/*			try{
				if( aMetaData != null ){
					aMetaData.close();
					aMetaData = null;
				}
			}catch( SQLException aE ){
			}*/
		}
		return aCall.toString();
	}
	public void addQuery( ResultSet aMetaData, StringBuffer aCall) {
	}
	public String getListQuery()
	{
		return getListQuery( sListWhere);
	}
	public String getListQuery( String sWhere)
	{
		StringBuffer aCall = new StringBuffer( "select ");
		String sListField = getListField();
		if( sListField != null ) aCall.append( sListField);
		else{
			aCall.append( getPKeyName());
			aCall.append( ",");
			aCall.append( getDataName());
		}
		aCall.append( " from ");
		aCall.append( getTableNameSql());
		if( sWhere != null){
			aCall.append( " where ");
			aCall.append(  sWhere);
		}
		if( sListOrder != null){
			aCall.append( " order by ");
			aCall.append(  sListOrder);
		}
		return aCall.toString();
	}
	public String getListQuery( String sWhere, String sOrder)
	{
		StringBuffer aCall = new StringBuffer( "select ");
		aCall.append( getPKeyName());
		aCall.append( ",");
		aCall.append( getDataName());
		aCall.append( " from ");
		aCall.append( getTableName());
		if( sWhere != null){
			aCall.append( " where ");
			aCall.append( sWhere);
		}
		aCall.append( " order by ");
		if( sOrder == null ){
			sOrder = getDataName();
	    	int i = sOrder.indexOf( " as ");
	    	if( i > 0 ) sOrder = sOrder.substring( 0, i);
		}
		aCall.append( sOrder);
		return aCall.toString();
	}
	public void addList( DOMData aDoc, Node aRoot)
	{
		if( bAddList && sTableName != null && aDoc != null ){
			QService aFService = aSight.getList( getListName());
			
/*			QService aFService = ( getListWhere() == null)? aSight.getList( sTableName):
				aSight.getList( sTableName + "?" + getListWhere() +
				(( getListOrder() != null)? "?" + getListOrder(): ""));*/
			//	createList();
			if( aFService != null )
				aDoc.addChildNode( aRoot, "DataObjects", aFService.getDatas(), iID, false, false); 
		}
	}
	public void addList( DOMData aDoc, String sListName)
	{
		if( sListName != null && aDoc != null ){
			 Node aRoot = aDoc.getRootNode();
			QService aFService = aSight.getList( sListName);
			if( aFService != null )
				aDoc.addChildNode( aRoot, sListName, aFService.getDatas(), 0, false, false); 
		}
	}
	public String getListName()
	{
		return ( getListWhere() == null)? sTableName:
			(sTableName + "?" + getListWhere() +
			(( getListOrder() != null)? "?" + getListOrder(): ""));
		
	}
	public OptionsList getList()
	{
		createList();
		return aDatas;
	}
	public int getListSize()
	{
		return ( aDatas != null)? aDatas.size(): 0;
	}
	public void createList()
	{
		if( aDatas == null )
			if( getListQuery() != null ){
				QServiceDO aData = ( QServiceDO)createDataObject();
			// Siit l�heb asi jamaks, sest see alus Qservice j��b siia
				aData.loadList( aSight, getListQuery());
				aDatas = aData.getDataObjects();
				aDatas.trimToSize();
/*			}else {
				QServiceDO aData = ( QServiceDO)createDataObject();
				aData.loadList( aSight, ( sListWhere != null)? sListWhere: sWhere, ( sListOrder != null)? sListOrder: null);
				aDatas = aData.getDataObjects();
				aDatas.trimToSize(); */
			}
	}
	public QServiceDO getFromList( int iID)
	{
		return ( aDatas != null)? ( QServiceDO)aDatas.getObjectByValue( iID): null;
	}
	public QServiceDO getFromList( String sValue)
	{
		return ( aDatas != null)? ( QServiceDO)aDatas.getObjectByValue( sValue): null;
	}
	public QServiceDO getFromListByName( String sName)
	{
		return ( aDatas != null)? ( QServiceDO)aDatas.getObjectByName( sName): null;
	}
	public DOMData getXML( Database Db, String sTableName, String sQuery)
	{
		setRefs();
		DOMData aDoc = null;
		try{
			aDoc = aSight.getTemplate();
			Node aRoot = aDoc.getRootNode();
//			aDoc.addChildNode( aRoot, "edit", aSight.hasUserRights( sTableName, 0));
    		if( sTableName != null && !isFormat() ){
    			aDoc.addChildNode( aRoot, "table", sTableName);
    			//if( bAddList ) kuidagi see peab nii olema, kuid?
    			addList( aDoc, aRoot);
    		}
			addXML( aDoc, aRoot, Db, sTableName, sQuery);
			aDoc.addChildNode( aDoc.getRootNode(), "rec_count", iRecCount);
		}catch( ExceptionIS aE ){
		}
		return aDoc;
	}
	public boolean mustAddCoord()
	{
		return getID() > 0;
	}
	public String getValue( String sName, Object aValue)
	{
		return ( aValue != null )? aValue.toString(): "";
	}
	public void addXML( DOMData aDoc, Node aRoot, Database Db, String sNodeName, String sQuery)
	{
		iRecCount = 0;
		if( sQuery == null ) return ;
 //   	Statement aSt = null;
    	ResultSet aRS = null;
    	try
    	{
 			aRS = Db.execSelect( sQuery);
//    	    aSt = Db.getStatement( true);
//   	    aSt.execute( sQuery);
// 	       	aRS = aSt.getResultSet();
			if( aRS != null ){
	 	       	ResultSetMetaData rsmd = aRS.getMetaData();
	 	       	int colCount           = rsmd.getColumnCount();
	 	       	while( aRS.next())
	 	       	{
	 	       		if( !isLimit( ++iRecCount) ) break;
	 	       		Node aRow;
	 	       		if( outputGML() ){
	 	       			aRow = aDoc.addChildNode( aRoot, "gml:featureMember");
	 	       			aRow = aDoc.addChildNode( aRow, getName( sNodeName));
	 	       			String sId = aRS.getString( getPKeyName());
	 	       			aDoc.setElemAttr( aRow, "gml:id", sNodeName + "_" + sId);
	 	       		}else
	 	       			aRow = ( sNodeName != null)? aDoc.addChildNode( aRoot, getName( sNodeName)): aRoot;

	 	       		for (int i = 1; i <= colCount; i++)
	 	       		{
	 	       			String columnName = rsmd.getColumnName(i).toLowerCase();
	 	       			String columnType = rsmd.getColumnTypeName( i);
	 	       			int iType = rsmd.getColumnType( i);
	 	       			if( columnType.equalsIgnoreCase( "bytea" )){
	 	       				Node aNode = aDoc.addChildNode( aRow, getName( columnName));
	 	       				aDoc.setElemAttr( aNode, "type", "bytea");
	 	       				aDoc.addChildNode( aDoc.getRootNode(), "picture_field", getName( columnName));
	 	       				
	 	       			}else{
		 	       			Object aValue = aRS.getObject(i);
		 	       			{
			 	       			String sValue = getValue( columnName, aValue); 
			 	       			if( outputGML() ){
			 	       				int s = sValue.indexOf( "<gml:");
			 	       				if( s >= 0 ){
			 	       					Node aNode = aDoc.addChildNode( aRow, getName( columnName));
			 	       					aDoc.addXML( aNode, sValue);
			 	       				}else
			 	       					aDoc.addChildNode( aRow, getName( columnName), sValue);
//			 	       				/* Node aNode = */ aDoc.addChildNode( aRow, getName( columnName), sValue);
			 	       			}else if( columnType.equalsIgnoreCase( "text")){
			 	       				int s = columnName.indexOf( "geom_");
			 	       				if( s == 0){
			 	       					if( mustAddCoord() ){ // see peaks minema keerulisemaks
//			 	       						aGeom = getCoord( aRequest, columnName, sValue);
			 	 	 	       				aGeom = Geom.getGeom( sValue);
			 	 	 	       				if( aGeom == null ) aGeom = new GeomPoint();
			 	 	 	       				if( aGeom != null ){
			 	 	 	       					aGeom.setSRID( getSight().getDatabase().getProjection());
			 	 	 	       					if( aGeom.isPoint() ){
			 	 	 	       						String sColName = columnName.substring( s+5);
			 	 	 	       						Node aNode = aDoc.addChildNode( aRow, sColName, getCoordXY( aGeom.getCenter()));
			 	 	 	       						aDoc.setElemAttr( aNode, "type", "geometry");
			 	 	 	       						aDoc.setElemAttr( aNode, "size", 24);
			 	 	 	       						aDoc.setElemAttr( aNode, "type_id", iType);
			 	 	 	       						aGeom = new GeomPointLL( aGeom);
			 	 	 	       						Point2D aCoordLL = (( GeomPointLL)aGeom).getCoordLL();
//			 	 	 		 	       			Point2D aCoordLL = Projection.getCoord( aCoord[ 0], Projection.CS_L_EST, Projection.CS_LL);
			 	 	 	       						aNode = aDoc.addChildNode( aRow, sColName + "_ll", GlobalGIS.getAsLL_M( aCoordLL));
			 	 	 	       						aDoc.setElemAttr( aNode, "type", "geometry_ll");
			 	 	 	       						aDoc.setElemAttr( aNode, "size", 25);
			 	 	 	       						aDoc.setElemAttr( aNode, "type_id", iType);	
			 	 	 	       					}else{ // j�rgnev oli v�ljas, Kuid HIS probleem vajab seda ???
//			 	 	 	       						Node aNode = 
			 	 	 	       						aDoc.addChildNode( aRow, "coords", aGeom.toString());
			 	 	 	       					}	
			 	 	 	       				}
			 	       					}else if( outputKML() ){
	 	 	 	       						String sColName = columnName.substring( s+5);
		 	 	 	       					aDoc.addChildNode( aRow, sColName + "_kml", sValue);
			 	       					}else{
			 	       						Geom aGeom = Geom.getGeom( sValue);
					 	       				if( aGeom != null && aGeom.isPoint() ) {
					 	       					Node aNode = aDoc.addChildNode( aRow, columnName, true);
					 	       					aDoc.setElemAttr( aNode, "type", "bool");
					 	       				}
			 	       					}
			 	       				}else{
			 	       					sValue = sValue.replaceAll( "\r\n",  "\r");
			 	       					
				 	 	       			Node aNode = aDoc.addChildNode( aRow, columnName, (iType == 12)? sValue.trim(): sValue);
				 	 	       			if( aRefs != null && sValue != null ){
				 	 	       				QService aFService = ( QService)aRefs.get( columnName);
				 	 	       				if( aFService != null ){
				 	 	       					try{
				 	 	       						QServiceDO aDO = aFService.getFromList( sValue);
				 	 	       						if( aDO != null ){
				 	 	       							aDO.setXML( aDoc, aDoc.addChildNode( aNode, aFService.getTableName()), false);
				 	 	       						}
				 	 	       					}catch( Exception aE ){
				 	 	       						getSight().log( aE);
				 	 	       					}
				 	 	       				}
				 	 	       			}
					 	       			aDoc.setElemAttr( aNode, "type", columnType);
				 	       				int iSize = rsmd.getColumnDisplaySize( i);
				 	 	       			aDoc.setElemAttr( aNode, "size", iSize);
				 	 	       			aDoc.setElemAttr( aNode, "type_id", iType);
			 	       				}
			 	       			}else if( columnType.equalsIgnoreCase( "geometry")){
			 	       				Geom aGeom = getGeom( aRS, columnName,  Integer.parseInt( Db.getDbGeometry()));
			 	       				if( aGeom != null && aGeom.isPoint() ) {
			 	       					Node aNode = aDoc.addChildNode( aRow, columnName, true);
			 	       					aDoc.setElemAttr( aNode, "type", "bool");
			 	       				}
			 	       			}else if( columnType.equalsIgnoreCase( "date") ){
			 	       				sValue = getDate( aRS, columnName);
			 	 	       			Node aNode = aDoc.addChildNode( aRow, columnName, sValue);
			 	 	       			// GlobalDate.getDateString( sValue));
				 	       			aDoc.setElemAttr( aNode, "type", columnType);
				 	       			int iSize = 10;
			 	 	       			aDoc.setElemAttr( aNode, "size", iSize);
			 	 	       			aDoc.setElemAttr( aNode, "type_id", iType);
			 	       			}else if( columnType.indexOf( "timestamp") >= 0){
			 	       				sValue = getDateTime( aRS, columnName);
			 	 	       			Node aNode = aDoc.addChildNode( aRow, columnName, sValue);
			 	 	       			// GlobalDate.getDateString( sValue));
				 	       			aDoc.setElemAttr( aNode, "type", columnType);
				 	       			int iSize = 10;
			 	 	       			aDoc.setElemAttr( aNode, "size", iSize);
			 	 	       			aDoc.setElemAttr( aNode, "type_id", iType);
			 	       			}else{
			 	 	       			Node aNode = aDoc.addChildNode( aRow, columnName, sValue);
			 	 	       			if( /* columnType.equalsIgnoreCase( "int4") && */ aRefs != null  ){
			 	 	       				QService aFService = ( QService)aRefs.get( columnName);
			 	 	       				if( aFService != null ){
			 	 	       					aDoc.setElemAttr( aNode, "values", aFService.getTableName());
			 	 	       					if( aValue != null ) try{
//												int iValue = Integer.parseInt(  sValue);
												QServiceDO aDO = aFService.getFromList( sValue);
												if( aDO != null ){
													aDO.setXML( aDoc, aDoc.addChildNode( aNode, aFService.getTableName()), false);
												}
											}catch( Exception aE ){
											}
			 	 	       				}
			 	 	       			}
				 	       			aDoc.setElemAttr( aNode, "type", columnType);
			 	       				int iSize = rsmd.getColumnDisplaySize( i);
			 	 	       			aDoc.setElemAttr( aNode, "size", iSize);
			 	 	       			aDoc.setElemAttr( aNode, "type_id", iType);
			 	       			}
			 	       			
		 	       			}
	 	       			}
	 	       		}
	 	       		addXML( aDoc, aRow, aRS);
	    	    }
			}
 	    }catch (Exception aE){
    		log( aE);
    	}finally{
			Db.close( aRS);
    	}
//		return aDoc;
	}
	public boolean isLimit( int iRecCount) {
		return true;
	}
	public Node addChildNode( DOMData aDoc, Node aRow, String columnName, String sValue)
	{
 		Node aNode = aDoc.setChildNodeValue( aRow, columnName, sValue);
 		if( aRefs != null && sValue != null ){
 			try{
 				QService aFService = ( QService)aRefs.get( columnName);
 				if( aFService != null ){
// 					int iValue = Integer.parseInt(  sValue);
 					QServiceDO aDO = aFService.getFromList( sValue);
 					if( aDO != null ){
 						aDO.setXML( aDoc, aDoc.addChildNode( aNode, aFService.getTableName()), false);
 					}
 				}
 			}catch( Exception aE ){
 			}
		}
		return aNode;
	}
	public void addXML( DOMData aDoc, Node aRow, ResultSet aRS)  throws SQLException {
	}
	public DOMData getEmpty( HttpServletRequest Request) throws ExceptionIS  
	{
		DOMData aDoc = aSight.getTemplate();
		if( sTableName != null ){
			Node aRoot = aDoc.getRootNode();
			aDoc.addChildNode( aRoot, "table", sTableName);
			setRefs();
			addList( aDoc, aRoot);
//			aDoc.addChildNode( aRoot, "edit", aSight.hasUserRights( sTableName, 0));
			addEmptyXML( aDoc, aRoot, sTableName, Request);
		}
		setFileXSL( aDoc, Request);
		return aDoc;
	}
	public void addEmptyXML( DOMData aDoc, Node aRoot, String sNodeName, HttpServletRequest Request) 
	{
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
		try {
			Node aRow = aDoc.addChildNode( aRoot, sNodeName);
			ResultSet aMetaData = aSight.getDatabase().getMetaData( sTableName);
	       	while( aMetaData.next())
	       	if( aMetaData.getString( "TABLE_NAME").equalsIgnoreCase( sTableName) ){
	       		String sValue = null;
       			String columnName = aMetaData.getString( "COLUMN_NAME").toLowerCase();
       			String columnType = aMetaData.getString( "TYPE_NAME");
       			String columnDef = aMetaData.getString( "COLUMN_DEF");
       			int iType = aMetaData.getShort("DATA_TYPE");

       			String sSize = aMetaData.getString( "COLUMN_SIZE");
       			if( aSight.isDebug( 98)) aSight.log( columnName + " " + columnType + " " + columnDef);
       			if( !addQuery( columnName, columnType) );
       			else if( columnType.equalsIgnoreCase( "geometry" ) ){
//       				if( ISServlet.getParameterInt( Request, "RETURN") == 3 ){
// N�iteks ka Uploadfile tuleb siit l�bi
       					Geom aCoord = getCoord( Request, columnName, null);
       					if( aCoord != null && aCoord.isPoint() ){
       						sValue = GlobalGIS.toString( aCoord.getCenter()); 
	 		 	       		Point2D aCoordLL = Projection.getCoord( aCoord.getCenter(), Projection.CS_L_EST, Projection.CS_LL);
	 	 	 	 	       	Node aNode = aDoc.addChildNode( aRow, columnName + "_ll", getCoordLL( aCoordLL));
 		 	       			aDoc.setElemAttr( aNode, "type", "geometry_ll");
       					}else if( aCoord != null ){
	       					aDoc.addChildNode( aRow, "coords", aCoord.toString());
       					}else{
       						sValue = "";
       						sSize = "24";
       					}
/*       				}else/* if( mustAddCoord() ) /{
       					sValue = "";
          				sSize = "24";
          			}*/
       			}else if( columnType.equalsIgnoreCase( "bytea" )){
       			}else if( ISServlet.hasParameter( Request, columnName)){
       				sValue = Request.getParameter( columnName);	
 	       		}else if( columnName.equalsIgnoreCase( getPKeyName()) && ! isPKeyChar()){
 	       			sValue = "0";
 	       		}else{
 	       			sValue = columnDef; // DEFAULT
 	       			if( sValue == null || sValue.equalsIgnoreCase( "('now'::text)::date")) sValue = "";
 	       		}
       			if( sValue != null){
 	       			Node aNode = aDoc.addChildNode( aRow, columnName, sValue);
 	       			aDoc.setElemAttr( aNode, "type", columnType);
 	       			if( !columnType.equalsIgnoreCase( "text")){
 	 	       			aDoc.setElemAttr( aNode, "size", sSize);
 	       			}
 	       			if( columnType.equalsIgnoreCase( "int4") && aRefs != null ){
  	       				QService aFService = ( QService)aRefs.get( columnName);
  	       				if( aFService != null ){
  	       					aDoc.setElemAttr( aNode, "values", aFService.getTableName());
  	       					if( sValue.length() > 0  ){
  	 	       					QServiceDO aDO = aFService.getFromList( sValue);
  	 	       					if( aDO != null ){
  	 	       						aDO.setXML( aDoc, aDoc.addChildNode( aNode, aFService.getTableName()), false);
  	 	 	       				}
  	       					}
 	 	       			}
 	       			}
 	       			aDoc.setElemAttr( aNode, "type_id", iType);
       			}
    	    }
	       	addEmptyXML( aDoc, aRow);
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
//			aDB.close();
		}
    	if( aSight.isDebug( 5)) aSight.log( "DB empty time=" + (( new Date()).getTime() - lSTime) + "ms");
	}
	public void addEmptyXML( DOMData aDoc, Node aRow)  throws SQLException 
	{
	}
	public int getKeyFromWhere( String sWhere)
	{
		if( sWhere != null ){
			int i = sWhere.indexOf( "=");
			if( i > 0 && sWhere.substring( 0, i).trim().equalsIgnoreCase( getPKeyName())){
				try{
					iID = Integer.parseInt( sWhere.substring( ++i));
					return iID;
//					bAddList = false; see kirjutatakse �le
				}catch( NumberFormatException aE ){
				}
			}
		}
		return 0;
	}
	public boolean isDateTime( int iType)
	{
		return  iType == Types.DATE || iType == Types.TIMESTAMP || iType == Types.TIME;
	}
	public boolean isTime( int iType)
	{
		return  iType == Types.TIME;
	}
	public boolean isString( String sType)
	{
		return  sType != null && (sType.equalsIgnoreCase( "text") || sType.equalsIgnoreCase( "varchar"));
	}
	public boolean isInt( String sType)
	{
		return  sType != null && (sType.equalsIgnoreCase( "integer") || sType.equalsIgnoreCase( "long"));
	}
	public boolean isInt( int iType)
	{
		return  iType == Types.BIGINT || iType == Types.INTEGER || iType == Types.SMALLINT; //  || iType == Types.NUMERIC;
	}
	public boolean isBoolean( int iType)
	{
		return  iType == Types.BOOLEAN || iType == Types.BIT;
	}
	public boolean isBinary( int iType)
	{
		return  iType == Types.BINARY || iType == Types.LONGVARBINARY || iType == Types.VARBINARY  
			|| iType == Types.CLOB || iType == Types.BLOB;
	}
	public boolean isString( int iType)
	{
		return  iType == Types.CHAR || iType == Types.LONGNVARCHAR || iType == Types.VARCHAR;
	}
	public boolean isGeom( String columnName, String columnType)
	{
		return columnType != null && columnType.equalsIgnoreCase( "geometry") ||
			columnName != null && columnName.indexOf( "geom_") == 0;
	}
	public String getGeomName( String columnName)
	{
		return columnName.substring( 5);
	}
	public boolean setPicture( HttpServletRequest aRequest) throws ExceptionIS 
	{ // see l�ks �le �ldisesse update-i ja insert-i
		return setPicture( aRequest, true);
	}
	public boolean setPicture( HttpServletRequest Request, boolean bWithData) throws ExceptionIS 
	{ // see l�ks �le �ldisesse update-i ja insert-i
		if( !aSight.hasUserRights( sTableName, 0) ){
			aSight.setError( 6010, "User has no rights to update table " + sTableName);
			return false;
		}
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
//		byte [] aPic = getPicture( Request);
		DbAccess aDB = null;
//		PreparedStatement ps = null;
		try {
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, getPKeyName());
			aDB.setAutoCommit( false);
			iID = getID();
			if( iID > 0 ){
				aDB.edit( iID);
			}else if( sWhere != null ){
				iID = getKeyFromWhere( sWhere);
				if( iID > 0 ) aDB.edit( iID);
				else{
					aDB.addNew();
//					aSight.setError( 6001, "Wrong key value for update!");
//					return false;
				}
			}else aDB.addNew();
			{
	 	       	ResultSet rsmd = aDB.getMetaData( sTableName);
		       	while( rsmd.next())
	 	       	{
	       			String columnName = rsmd.getString( "COLUMN_NAME");
	       			String columnType = rsmd.getString( "TYPE_NAME");
	       			if( columnName.equalsIgnoreCase( "coord") ){
	       			}else if( columnType.equalsIgnoreCase( "bytea" )){
	       				if( sFieldName == null ) sFieldName = columnName;
	       			}else if( bWithData && ISServlet.hasParameter( Request, columnName)){
	       				String sValue = Request.getParameter( columnName);	       				
	       				aDB.setValue( columnName, sValue, isString( columnType));
	       				if( columnName.equals( getPKeyName())) iID = Integer.parseInt( sValue);
	 	       		}else if( sWhere != null ){ // selle peaks kandma setUpdate-i
	 	       			int i = sWhere.indexOf( columnName + "=");
	 	       			if( i == 0 ){
	 	       				String sValue = sWhere.substring( columnName.length() + 1);
	 	       				aDB.setValue( columnName, sValue, isString( columnType));
	 	       			}
	 	       		}
	    	    }
			}
	       	if( iID == 0){
				if( aSight.getDatabase().supportsPreKey() ){
					iID = ( int)aDB.getPreKey( sTableName);
					if( iID > 0 ) aDB.setValue( getPKeyName(), iID);
					else{
						iID = 0;
						aSight.setError( 6008, "Wrong key value got from database!");						
					}
				} 
	       	}
       		if( sFieldName != null){
       			return updatePicture(  Request, aDB, lSTime);
/*       			if( aPic == null ) aPic = (( MultipartRequest)Request).getBinaryData();
       			if( aPic != null  ){
           			aDB.setValue( sFieldName, "?", false);
           			String sQuery = aDB.getQuery();
           			if( aSight.isDebug( 2)) aSight.log( sQuery);
           			ps = aDB.getPreStatement( sQuery);
           			ps.setBytes( 1, aPic);
           			ps.executeUpdate();
     	       		if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
           			return true;
       			}else{
           			aSight.setError( 6003, "There is picture!");
           			return false;
       			} */
       		}else{
       			aSight.setError( 6002, "There is no picture field in this table " + sTableName);
       			return false;
       		}
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
/*			try {
				if( ps != null ) ps.close();
			} catch( Exception aE) {
			}*/
			try {
				aDB.setAutoCommit( true);
				aDB.close();
			} catch( Exception aE) {
			}
		}
		return false;
	}
	private boolean updatePicture(  HttpServletRequest aRequest, DbAccess aDB, long lSTime)
	{
		PreparedStatement ps = null;
		try {
			byte [] aPic = (( MultipartRequest)aRequest).getBinaryData( sFieldName);
// download picture only bu name is new 16.03-2017
//			byte [] aPic = getPicture( aRequest);
/*			if( aPic == null ){
				if( aRequest instanceof MultipartRequest )
					aPic = (( MultipartRequest)aRequest).getBinaryData();
				else 
					return false;
			}*/
			if( aPic != null  ){
				aDB.setValue( sFieldName, "?", false);
				String sQuery = aDB.getQuery();
				if( aSight.isDebug( 2)) aSight.log( sQuery);
				ps = aDB.getPreStatement( sQuery);
				ps.setBytes( 1, aPic);
				int iRet = ps.executeUpdate();
				if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
				return iRet == 1;
			}else{
//				aSight.setError( 6003, "There is picture!");
				return aDB.update();
			}
		} catch (Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
			try {
				if( ps != null ) ps.close();
			} catch( Exception aE) {
			}
		}
		return false;
	}
	public boolean insert( HttpServletRequest aRequest) throws ExceptionIS 
	{
		if( !aSight.hasUserRights( sTableName, 1) ){
			aSight.setError( 6011, "User has no rights to insert table " + sTableName);
			return false;
		}
		aSight.clearError();
		boolean bRet = false;
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
		DbAccess aDB = null;
		try {
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, getPKeyName());
			aDB.addNew();
			if( insert( aRequest, aDB) ){
		       	if( addInsert( aRequest, aDB) ){
		       		if( sFieldName != null )
		       			bRet = updatePicture(  aRequest, aDB, lSTime);
		       		if( !bRet ) bRet = aDB.update();
		       		if( bRet ){
			       		iID = aDB.getID();
			       	}
		       	}else bRet = true;
		       	if( bRet ) aSight.removeList( getListName());
			}
/*		} catch( MException aE) {
			aSight.setError( "Baasi lisamisel juhtus viga - täpsemalt logis"); */
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
			aDB.close();
	    	if( aSight.isDebug( 1)) log( "DB insert " + bRet + " time=" + (( new Date()).getTime() - lSTime) + "ms");
/*	    	if( !bRet ){ // ei tea miks?
	    		log( aSight.getErrorNr() + ": " + aSight.getError());
	    		aSight.clearError();
	    	} */
	    }
		return bRet;
	}
	public boolean insert( HttpServletRequest Request, DbAccess aDB) throws MException
	{
		ResultSet aMetaData = aDB.getMetaData( aDB.isOracle()? sTableName.toUpperCase(): sTableName);
       	try{
       		Geom aCoord = null;
			while( aMetaData.next())
			if( aMetaData.getString( "TABLE_NAME").equalsIgnoreCase( sTableName) ){
				String columnName = aMetaData.getString( "COLUMN_NAME").toLowerCase();
				String columnType = aMetaData.getString( "TYPE_NAME");
				int iColumnType = aMetaData.getShort("DATA_TYPE");
				if( isUpdateable( columnName, iColumnType)){
					if( isGeom( columnName, columnType) ){
						aCoord = getCoord( Request, columnName, null);
						if( aSight.isError()) return false;
						else if( aCoord != null ) aDB.setValue( columnName, aCoord.toString(), true); 
        			}else{
        				if( !setUpdateValue( Request, aDB, columnName, iColumnType, 
        					aMetaData.getString( "IS_NULLABLE").equals( "YES")? ResultSetMetaData.columnNullable: 0,
        					false, true) ) return false;
			   		}
      			}else if( columnType.equalsIgnoreCase( "bytea") && ( Request instanceof MultipartRequest )){
       				if( sFieldName == null ) sFieldName = columnName;
				}
			}
			
		}catch( Exception aE ){
			aSight.log( aE);
			aSight.setError( aE);
			return false;
		}
		return true;
	}
	public boolean isUpdateable( String columnName, int iColumnType)
	{
		if( columnName.equalsIgnoreCase( getPKeyName()) ) return isPKeyChar();
		return !isBinary( iColumnType); 
	}
	public int gotoObject( HttpServletRequest Request)
	{
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
		DbAccess aDB = null;
		try {
    		if( aDO != null ){
    			Class<? extends QServiceDO> aClass = aDO.getClass();
    			Method aMethod = null;
    			try {
    				aMethod = aClass.getMethod( "getCoord", ( Class<?>[])null);
    				if( aMethod != null ) {
        				Object aRet = aMethod.invoke( aDO, ( Object[])null);
        				if( aRet != null && aRet instanceof Point2D){
             				GeomPoint aGeom = new GeomPoint(  aSight.getDatabase().getProjection(), ( Point2D)aRet);
             				int iRet = (( GISSight)aSight).gotoGeom( aGeom)? 1: 0;
             				((GISSight)aSight).setMarkers( aGeom);
             				return iRet;
        				}
    				}
    			} catch(  Exception e) {
    				log( e);
    			}
    		}else{
    			
    		}

			int iID = getID();
			if( iID <= 0 && sWhere != null ) iID = getKeyFromWhere( sWhere);
			if( iID <= 0 ){
//				aSight.setError( 6007, "Wrong key value!");
				return 0;
			}
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, getPKeyName());
			String sSQL = getQuery( sTableName, null, null, null, 0, true);
			
			if( sSQL != null && aDB.select( sSQL) ){
		       	ResultSetMetaData rsmd = aDB.getMetaData();
	       		String columnName = rsmd.getColumnName( 1);
	       		
	    		Point2D.Double [] aPoints = aDB.getGeometryP( columnName);
	       		
//				Point2D aPoint = aDB.getGeometryPoint( columnName);
				if( aPoints != null && aPoints.length == 1 ){
      				GeomPoint aGeom = new GeomPoint(  aSight.getDatabase().getProjection(), aPoints[ 0]);
    				return (( GISSight)aSight).gotoGeom( aGeom)? 1: 0;
    						// ( (( GISSight)aSight).isBGRedraw()? 100: 1): 0;
 				}else if( aPoints != null && aPoints.length > 0 ){
      				GeomArea aGeom = new GeomArea(  aSight.getDatabase().getProjection(), aPoints);
    				return (( GISSight)aSight).gotoGeom( aGeom)? 1: 0;
    						// ( (( GISSight)aSight).isBGRedraw()? 100: 1): 0;
 				}
			}
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
			if( aDB != null ) aDB.close();
	    	if( aSight.isDebug( 1)) aSight.log( "DB goto time=" + (( new Date()).getTime() - lSTime) + "ms");
		}
		return 0;
	}
	public boolean update( HttpServletRequest Request, boolean bAll) throws ExceptionIS 
	{
		if( !aSight.hasUserRights( sTableName, 1) ){
			aSight.setError( 6010, "User has no rights to update table " + sTableName);
			return false;
		}
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
		DbAccess aDB = null;
		PreparedStatement ps = null;
		try {
//			int iID = getID();
			if( isPKeyChar() ){
				if( sWhere == null ){
					aSight.setError( 6001, "Wrong key value for update");
					return false;
				}
			}else{
				if( iID <= 0 && sWhere != null ) iID = getKeyFromWhere( sWhere);
				if( iID <= 0 ){
					aSight.setError( 6001, "Wrong key value for update");
					return false;
				}
			}
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, getPKeyName());
			String sSQL = getQuery( sTableName, isPKeyChar()? sWhere: null, null, null, 0, false, true);
			if( aDB.select( sSQL) ){
/*				if( aDB.next()){
	 				aSight.setError( 6005, "There is multiple records in table " + sTableName + " by condition " + sWhere);
	   				return false;
				}*/

			}else{
				boolean bRet = insert( Request);
  				if( !bRet ) aSight.setError( 6005, "There is no records in table " + sTableName + " by condition " + sWhere);
   				return	bRet; 
			}
			boolean bRet = update( Request, aDB, bAll);
/*			aDB.edit();
	       	ResultSetMetaData rsmd = aDB.getMetaData();
 	       	int colCount           = rsmd.getColumnCount();
       		for (int i = 1; i <= colCount; i++){
	       		String columnName = rsmd.getColumnName(i).toLowerCase();
	       		String columnType = rsmd.getColumnTypeName( i);
    			int iColumnType = rsmd.getColumnType( i);
    			if(  rsmd.isWritable( i)){
        			if( isUpdateable( columnName, iColumnType)){
            			if( isGeom( columnName, columnType) ){
            				String sOldValue = aDB.getString( columnName);
        					String sColName = getGeomName( columnName);
            				Geom aCoord = getCoord( Request, sColName, sOldValue);
            				if( aCoord != null){
            					aDB.setValue( sColName, aCoord.toString(), true); 
            				}
            			}else if( !rsmd.isReadOnly( i)){
            				if( !setUpdateValue( Request, aDB, columnName, iColumnType, rsmd.isNullable( i), bAll, false) )
            					return false;
               			}
          			}else if( columnType.equalsIgnoreCase( "bytea") && ( Request instanceof MultipartRequest )){
           				if( sFieldName == null ) sFieldName = columnName;
        			}
    			}
       		}
       		boolean bRet = false;
      		if( sFieldName != null)
       			bRet = updatePicture(  Request, aDB, lSTime);
      			if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
/*      	if( sFieldName != null){
      			byte [] aPic = getPicture( Request);
       			if( aPic == null ) aPic = (( MultipartRequest)Request).getBinaryData();
       			if( aPic != null  ){
           			aDB.setValue( sFieldName, "?", false);
           			String sQuery = aDB.getQuery();
           			if( aSight.isDebug( 2)) aSight.log( sQuery);
           			ps = aDB.getPreStatement( sQuery);
           			ps.setBytes( 1, aPic);
           			ps.executeUpdate();
     	       		if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
           			return true;
       			}else{
           			aSight.setError( 6003, "There is picture!");
           			return false;
       			}
       		}/
      		if( !bRet ){
      			addUpdate( aDB, Request);
      			if( aDB.isUpdateEmty() ) return true;
      			bRet = aDB.update();
      		}*/
       		if( bRet )	aSight.removeList( getListName());
       		return bRet;
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
/*			String s = aE.getMessage();
			int i = s.indexOf( "already exists");
			if( i > 0 ){
				int j = s.indexOf( "(");
				if( j > 0 ){
					int k = s.indexOf( ")", j);
					if( k > 0 ){
						String sName = s.substring( ++j, k);
						j = s.indexOf( "(", k);
						if( j > 0 ){
							k = s.indexOf( ")", j);
							if( k > 0 ){
								String sValue = s.substring( ++j, k);
								if( sName.equalsIgnoreCase( getDataName())){
									aSight.setError( "Nimetus '" + sValue + "' on juba kasutusel");
									return false;
								}
								aSight.setError( "Väljal '" + sName + "' peab olema unikaalne väärtus (" + sValue + ")");
								return false;
							}
						}
					}
				}
			} 
			aSight.setError( "Baasi salvetamisel juhtus viga - täpsemalt logis");*/
		} finally {
			if( ps != null )
				try {
					ps.close();
				} catch (SQLException e) {
				}
			if( aDB != null ) aDB.close();
	    	if( aSight.isDebug( 1)) aSight.log( "DB update time=" + (( new Date()).getTime() - lSTime) + "ms");
	    }
		return false;
	}
	public boolean update( HttpServletRequest Request, DbAccess aDB, boolean bAll) throws Exception
	{
		aDB.edit();
       	ResultSetMetaData rsmd = aDB.getMetaData();
	       	int colCount           = rsmd.getColumnCount();
   		for (int i = 1; i <= colCount; i++){
       		String columnName = rsmd.getColumnName(i).toLowerCase();
       		String columnType = rsmd.getColumnTypeName( i);
			int iColumnType = rsmd.getColumnType( i);
			if(  rsmd.isWritable( i)){
    			if( isUpdateable( columnName, iColumnType)){
        			if( isGeom( columnName, columnType) ){
        				String sOldValue = aDB.getString( columnName);
    					String sColName = getGeomName( columnName);
        				Geom aCoord = getCoord( Request, sColName, sOldValue);
        				if( aCoord != null){
        					aDB.setValue( sColName, aCoord.toString(), true); 
        				}
        			}else if( !rsmd.isReadOnly( i)){
        				if( !setUpdateValue( Request, aDB, columnName, iColumnType, rsmd.isNullable( i), bAll, false) )
        					return false;
           			}
      			}else if( columnType.equalsIgnoreCase( "bytea") && ( Request instanceof MultipartRequest )){
       				if( sFieldName == null ) sFieldName = columnName;
    			}
			}
   		}
   		boolean bRet = false;
  		if( sFieldName != null) {
  			long lSTime = ( new Date()).getTime();
   			bRet = updatePicture(  Request, aDB, lSTime);
  		}
/*      	if( sFieldName != null){
  			byte [] aPic = getPicture( Request);
   			if( aPic == null ) aPic = (( MultipartRequest)Request).getBinaryData();
   			if( aPic != null  ){
       			aDB.setValue( sFieldName, "?", false);
       			String sQuery = aDB.getQuery();
       			if( aSight.isDebug( 2)) aSight.log( sQuery);
       			ps = aDB.getPreStatement( sQuery);
       			ps.setBytes( 1, aPic);
       			ps.executeUpdate();
 	       		if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
       			return true;
   			}else{
       			aSight.setError( 6003, "There is picture!");
       			return false;
   			}
   		}*/
  		if( !bRet ){
  			addUpdate( aDB, Request);
  			if( aDB.isUpdateEmty() ) return true;
  			bRet = aDB.update();
  		}
  		return bRet;
	}
	public Geom getCoord( HttpServletRequest aRequest, String columnName, String sOldValue)
	{
		if( !( aSight instanceof GISSight) ) return null;
		int iMapProjection = (( GISSight)aSight).getProjection();
		int iDbProjection = aSight.getDatabase().getProjection();
		
		if( ISServlet.hasParameterValue( aRequest, "ID_IX") && ISServlet.hasParameterValue( aRequest, "ID_IY")){
			int jX = ISServlet.getParameterInt( aRequest, "ID_IX");
		    int jY = ISServlet.getParameterInt( aRequest, "ID_IY");
		    Point2D aPoint = (( GISSight)aSight).getCoord( new Point( jX, jY));
		    if( aPoint != null ){
		    	(( GISSight)aSight).setMarkers( aPoint); // 20-04-2017 - HIS-is ei tulnud ilma selleta markeri muutus
		    	if( iMapProjection != iDbProjection ){
			    	try{
						Point2D aPointLL = aSight.getDatabase().callGeometry( aPoint, iMapProjection, Projection.CS_LL);
						GeomPointLL aCoordLL =  new GeomPointLL( iMapProjection, aPoint, aPointLL);
						aCoordLL.toProjection( iDbProjection);
						return aCoordLL;
					}catch( MException aE ){
					}
		    	}else return new GeomPoint( iDbProjection, aPoint);
		    }
		}
/*		if( sOldValue != null ){
			int i = sOldValue.indexOf( '(');
			if( i >= 0 ){
				int j = sOldValue.indexOf( ')');
				if( j > 0) sOldValue = sOldValue.substring( ++i, j);
				else return null;
			}
		}*/
		Point2D aCoord = Geom.getCoord( sOldValue);
		if( ISServlet.hasParameter( aRequest, columnName) ){
			String sValue = aRequest.getParameter( columnName);
      		if( aCoord != null ) sOldValue = getCoordXY( aCoord); 
 			Log.info( 98, "LEst " + sValue + " " + sOldValue + " " + aCoord);			
			if( sValue != null && ( sOldValue == null || !sValue.equals( sOldValue)) ){
				Point2D aPoint = GlobalGIS.getCoordFromXY( sValue);
				if( aPoint != null &&  GlobalGIS.isLL( aPoint) ){
					aPoint = Projection.getCoord( aPoint, Projection.CS_LL, Projection.CS_L_EST);
				}
				if( aPoint != null ) return new GeomPoint( iDbProjection, aPoint);
			}
		}
		if( ISServlet.hasParameter( aRequest, columnName + "_ll") ){
			String sValueLL = aRequest.getParameter( columnName + "_ll");
			if( sValueLL != null ){
				String sOldValueLL = null;
				if( aCoord != null ){
	       			Point2D aCoordLL = Projection.getCoord( aCoord, Projection.CS_L_EST, Projection.CS_LL);
	 	       		sOldValueLL = getCoordLL( aCoordLL);
	 			}
				Log.info( 98, "LL " + sValueLL + " " + sOldValueLL);			
				if( sOldValueLL == null || !sValueLL.equals( sOldValueLL) ){
					Point2D aPoint = GlobalGIS.getCoordLL( sValueLL);
					if( aPoint != null){
						GeomPointLL aCoordLL = new GeomPointLL( Projection.CS_LL, aPoint);
						aCoordLL.toProjection( iDbProjection);
						return aCoordLL;
					}
				}
			}
		}
		return null;
	}
	public String getCoordXY( Point2D aCoord)
	{
		return GlobalGIS.getAsXY( aCoord);
	}
	public String getCoordLL( Point2D aCoord)
	{
		return GlobalGIS.getAsLL( aCoord);
	}
	public boolean delete( HttpServletRequest Request) throws ExceptionIS 
	{
		if( !aSight.hasUserRights( sTableName, 2) ){
			aSight.setError( 6012, "User has no rights to remove from table " + sTableName);
			return false;
		}
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
		DbAccess aDB = null;
		try {
			int iID = getID();
			if( iID <= 0 && sWhere != null ) iID = getKeyFromWhere( sWhere);
			if( iID <= 0 ){
				aSight.setError( 6006, "Wrong key value for delete!");
				return false;
			}
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, getPKeyName());
			if( canDelete( aDB) && aDB.delete( iID) ){
	       		aSight.removeList( getListName());
				return true;
			}
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
			aDB.close();
	    	if( aSight.isDebug( 1)) aSight.log( "DB delete time=" + (( new Date()).getTime() - lSTime) + "ms");
	    }
		return false;
	}
	public boolean canDelete( DbAccess aDb) throws ExceptionIS 
	{
		return true;
	}
	public byte [] getPicture( HttpServletRequest Request) throws ExceptionIS 
	{
		try{
			if( ISServlet.hasParameter( Request, "FILE")){
				String sFileName = Request.getParameter( "FILE");
				return DbAccess.getBytes( new FileReader( sFileName));
			}else if( ISServlet.hasParameter( Request, "URL")){
				String sURL = Request.getParameter( "URL");
				URL aURL = new URL( sURL);
				URLConnection aCon = aURL.openConnection();
				return DbAccess.getBytes( new InputStreamReader( aCon.getInputStream()));
			}else if( Request instanceof MultipartRequest )
				return (( MultipartRequest)Request).getBinaryData();
		}catch( Exception aE ){
			aSight.log( aE);
		}
		return null;
	}
	public boolean getPicture( HttpServletRequest Request, HttpServletResponse aResponse) 
	{
		DbAccess aDB = null;
		BufferedImage aImage = null;
		try{
			byte [] aPic = getPicture( Request);
			if( aPic == null){
				aDB = new DbAccess( aSight.getDatabase());
				String sQuery = "SELECT " + sFieldName + " FROM " + sTableName + " WHERE " + sWhere; 
				if( aDB.select( sQuery)){
	//				InputStream aIn 
					aPic = aDB.getBytes( 1);
					InputStream aIn = aDB.getBinaryStream(1);
	/*				FileOutputStream aOut = new FileOutputStream( new File( aSight.getTempPath( "vrakk.png")));
					aOut.write( aPic);
					aOut.close();*/
//					String [] aFormats = ImageIO.getReaderFormatNames();
					ImageInputStream aImageIn = ImageIO.createImageInputStream( aIn);
					
					aImage = ImageIO.read( aImageIn);

					if( aImage == null ){
//						ImageInputStream iis = new FileImageInputStream(file);
						try {
						    for (Iterator<ImageReader> i = ImageIO.getImageReaders( aIn); 
						         aImage == null && i.hasNext(); ) {
						        ImageReader r = i.next();
						        try {
						            r.setInput( aIn);
						            aImage = r.read(0);
						        } catch (IOException e) {}
						    }
						} finally {
						    aIn.close();
						}
					}
				}
			}else{
				aImage = ImageIO.read( new ByteArrayInputStream( aPic));
			}
			if( aImage != null ){
				if( ISServlet.hasParameter( Request, "WIDTH") ){
					int iNewWidth = ISServlet.getParameterInt( Request, "WIDTH");
					int iWidth = aImage.getWidth();
					if( iWidth > iNewWidth ){
						int iNewHeight = (int) (aImage.getHeight() * ( (double)iNewWidth) / iWidth);
						aImage = Scalr.resize( aImage, Scalr.Method.ULTRA_QUALITY, iNewWidth, iNewHeight);
/*						BufferedImage aNewImage = new BufferedImage( iNewWidth, iNewHeight, BufferedImage.TYPE_INT_RGB);
						Graphics2D g = aNewImage.createGraphics();
						g.drawImage( aImage, 0, 0, iNewWidth, iNewHeight, null);
						g.dispose();
						aImage = aNewImage;*/

						
//						writeImageToOutput( aResponse, aImage.getScaledInstance( iNewWidth, iNewHeight, Image.SCALE_SMOOTH), "image/jpeg");
					}
				}
				writeImageToOutput( aResponse, aImage, (aImage.getType() == BufferedImage.TYPE_4BYTE_ABGR)? "image/png": "image/jpeg" );
				return true;
			}else{
	   			aSight.setError( 6003, "There is picture!");			
			}
		}catch( Exception aE){
			aSight.log( aE);
		}finally{
			aDB.close();
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
/*	public void loadMetaData( Database aDB)
	{
		try {
			aMetaData = aDB.getMetaData( sTableName);
		} catch( Exception aE) {
			if( aSight != null ){
				aSight.log( aE);
				aSight.setError( aE);
			}else{
				Log.info( aE);
			}
		} finally {
		}
	}*/
	public boolean isDate( String sColumnName)
	{
		try {
			ResultSet aMetaData = aSight.getDatabase().getMetaData( isOracle()? sTableName.toUpperCase(): sTableName);
/*			if( isOracle() ){
			}else if( aMetaData != null ){
				aMetaData.beforeFirst();
			}*/
			if( aMetaData != null ) while( aMetaData.next())
 	       	{
//	       		String sValue = null;
       			String sName = aMetaData.getString( "COLUMN_NAME");
       			if( sName.equalsIgnoreCase( sColumnName) ){
           			String columnType = aMetaData.getString( "TYPE_NAME");
           			return columnType.equalsIgnoreCase( "date" );
       			}
     	    }
		} catch( Exception aE) {
			if( aSight != null ){
				aSight.log( aE);
				aSight.setError( aE);
			}else{
				Log.info( aE);
			}
		}
		return false;
	}
	public String getDate( DbAccess aDb, String columnName) throws MException
	{
		String sValue = GlobalDate.getDateString( aDb.getString( columnName));
		if( sValue != null ){
			int iOldDateType = aDb.getInt( columnName + "_type");
			int iComma = sValue.indexOf( ".");
			if( iComma > 0 ){
				if( iOldDateType == 1 ) sValue = sValue.substring( ++iComma);
				else if( iOldDateType == 2 ){
					iComma = sValue.indexOf( ".", ++iComma);
					if( iComma > 0 )
						sValue = sValue.substring( ++iComma);
				}
			}
		}
		return sValue;
	}
	public String getDate( ResultSet aRS, String columnName) throws MException
	{
		String sValue = "";
		int iOldDateType = 0;
		try{
			sValue = GlobalDate.getDateString( aRS.getString( columnName));
			if( sValue == null ) return null;
			iOldDateType = aRS.getInt( columnName + "_type");
		}catch( SQLException aE1 ){
		}
		int iComma = sValue.indexOf( ".");
		if( iComma > 0 ){
			if( iOldDateType == 1 ) sValue = sValue.substring( ++iComma);
			else if( iOldDateType == 2 ){
				iComma = sValue.indexOf( ".", ++iComma);
				if( iComma > 0 )
					sValue = sValue.substring( ++iComma);
			}
		}
		return sValue;
	}
	public String getDateTime( ResultSet aRS, String columnName) throws MException
	{
		String sValue = "";
		int iOldDateType = 0;
		try{
			sValue = GlobalDate.getDateTimeString( aRS.getString( columnName));
			if( sValue == null ) return null;
			iOldDateType = aRS.getInt( columnName + "_type");
		}catch( SQLException aE1 ){
		}
		int iComma = sValue.indexOf( ".");
		if( iComma > 0 ){
			if( iOldDateType == 1 ) sValue = sValue.substring( ++iComma);
			else if( iOldDateType == 2 ){
				iComma = sValue.indexOf( ".", ++iComma);
				if( iComma > 0 )
					sValue = sValue.substring( ++iComma);
			}
		}
		return sValue;
	}
	public String setDate( DbAccess aDb, String columnName, String sValue, int iNewDateType) throws MException
	{
		if( iNewDateType >= 0 ){
			if( iNewDateType == 1) sValue = "01." + sValue;
			else if( iNewDateType == 2) sValue = "01.01." + sValue;
			sValue = GlobalDate.getDateSQLString( GlobalDate.parseDate( sValue, true));
			aDb.setValue( columnName, sValue, true);
			if( iID > 0 ){
				if( aDb.hasColumn( columnName + "_type")) aDb.setInt( columnName + "_type", iNewDateType);
			}else{
				if( aDb.getDatabase().hasTableColumn( sTableName,  columnName + "_type"))
					aDb.setInt( columnName + "_type", iNewDateType);
			}
		}
		return sValue;
	}
	public String setTime( DbAccess aDb, String columnName, String sValue) throws MException
	{
		aDb.setValue( columnName, sValue, true);
		return sValue;
	}
	public int getDateType( String sValue) throws MException
	{
		int iNewDateType = 0;
		if( sValue.indexOf( "-") > 0 ) sValue = GlobalDate.getDateString( sValue);
		else{
			int iComma = sValue.indexOf( ".");
			if( iComma > 0 ){
				if( sValue.indexOf( ".", ++iComma) < 0 ) iNewDateType = 1;
			}else if( sValue.length() > 0 ) iNewDateType = 2;
			else iNewDateType = -1;	// not exists
		}
		return iNewDateType;
	}
	public String getQueryWhere()
	{
/*		if( aMetaData == null ) try{
			aMetaData = aSight.getDatabase().getMetaData( isOracle()? sTableName.toUpperCase(): sTableName);
		}catch( MException aE ){
		}*/
		return getQueryWhere( sWhere);
	}
	public String getQueryWhere(  String sWhere)
	{
		if( sWhere == null ) sWhere = getWhere();
		if( sWhere == null ) return null;

		int iS = sWhere.indexOf( "("), iE = iS;
		if( iS >= 0 ){
			int iCount = 1;
		
			int n = sWhere.length();
			for ( ; ++iE < n; ){
				char cSymb = sWhere.charAt( iE);
				if( cSymb == ')' ){
					if( --iCount == 0 ) break;
				}else if( cSymb == '(' ){
					++iCount;
				}
			}
			if( iCount > 0 ){
				log( " Syntax error " + sWhere );
				return sWhere;
			}
		}
		if( iS > 0 ){ // leidsime sulu, kuid kas on veel eespool midagi ( 2017)
			int i = sWhere.indexOf( " AND "), j = 0;
			if( i < 0 ) i = sWhere.indexOf( " and ");

			if( i > 0 && i < iS ) j = i + 5;
			else{
				i = sWhere.indexOf( " OR ");			
				if( i < 0 ) i = sWhere.indexOf( " or ");
				if( i > 0 ) j = i + 4;
			}
			if( i > 0 && i < iS ){
				String s1 = getQueryWhere( sWhere.substring( 0, i));
				String s2 = getQueryWhere( sWhere.substring( j));
				return s1 + sWhere.substring( i, j) + s2;
			}
		}
		int j = ( iS >= 0 )? iE + 1: 0;
		int i = sWhere.indexOf( " AND ", j);
		if( i < 0 ) i = sWhere.indexOf( " and ", j);

		if( i > 0 ) j = i + 5;
		else{
			i = sWhere.indexOf( " OR ", j);			
			if( i < 0 ) i = sWhere.indexOf( " or ", j);
			if( i > 0 ) j = i + 4;
		}
		if( i > 0 ){
			String s1 = getQueryWhere( sWhere.substring( 0, i));
			String s2 = getQueryWhere( sWhere.substring( j));
			return s1 + sWhere.substring( i, j) + s2;
		}else{
			i = sWhere.indexOf( "NOT ");
			if( i < 0 ) i = sWhere.indexOf( "not ");
			if( i > 0 && i < iS){ 
				return "NOT " + getQueryWhere( sWhere.substring( i+4));
			}
			i = sWhere.indexOf( " IN ");			
			if( i < 0 ) i = sWhere.indexOf( " in ");
			if( i > 0 && i < iS) return sWhere; 
		}
		if( sWhere.indexOf( "ST_Intersects") >= 0 ) return sWhere;
		if( iS >= 0){
			j = sWhere.indexOf( " in ");
			if( j > 0 && j < iS )  return sWhere;
			String s = getQueryWhere( sWhere.substring( ++iS, iE));
			return "(" + s + ")";
		}
		return query( sWhere);
	}
	public String query( String sWhere)
	{ // <attr><op><date>
		if( sWhere.indexOf( "is null") > 0 ) return sWhere;
		
		int i = sWhere.indexOf( ">="), j = 0, iOper = 0;
		if( i > 0){
			j = i+2;
			iOper = 2;
		}else{
			i = sWhere.indexOf( "<=");
			if( i > 0){
				j = i+2;
				iOper = -2;
			}else{
				i = sWhere.indexOf( "<>");
				if( i > 0){
					j = i+2;
					iOper = 3;
				}else{
					i = sWhere.indexOf( "=");
					if( i > 0) j = i+1;
					else{
						i = sWhere.indexOf( ">");
						if( i > 0){
							j = i+1;
							iOper = 1;
						}else{
							i = sWhere.indexOf( "<");
							if( i > 0){
								j = i+1;
								iOper = -1;
							}else{
								i = sWhere.indexOf( "~");
								if( i > 0 ){
									j = i+1;
									iOper = 4;
								}
							}
						}
					}
				}				
			}
		}
		if( i > 0 ){
			String sAttr = sWhere.substring( 0, i).trim();
			if( !isDate( sAttr)) return sWhere;
			StringBuffer aWhere = new StringBuffer();
			String sOp = sWhere.substring( i, j);
			if( iOper == 4){
				sOp = "=";
				iOper = 0;
			}
			String sDate = sWhere.substring( j).trim();
			int iDate = GlobalDate.DateToInt( sDate);
			int iY = iDate /10000;
			int iM = (iDate - iY * 10000) / 100;
			int iD = iDate%100;
			if( iD > 0 ){
				aWhere.append( sAttr);
				aWhere.append( sOp);
				aWhere.append( "'" + iY + "-" + iM + "-" + iD + "'");
			}else if( iOper == 3 ){ // <>
				aWhere.append( sAttr);
				aWhere.append( "<'");
				if( iM > 0 ){
					aWhere.append( iY + "-" + iM + "-1' OR ");
					aWhere.append( sAttr);
					aWhere.append( ">='");
					if( ++iM > 12 ){ ++iY; iM = 1;}
					aWhere.append( iY + "-" + iM + "-1'");
				}else{
					aWhere.append( iY + "-1-1' OR ");
					aWhere.append( sAttr);
					aWhere.append( ">='");
					++iY;
					aWhere.append( iY + "-1-1'");
				}
			}else if( iOper == 0 ){
				aWhere.append( sAttr);
				aWhere.append( ">='");
				if( iM > 0 ){
					aWhere.append( iY + "-" + iM + "-1' AND ");
					aWhere.append( sAttr);
					aWhere.append( "<'");
					if( ++iM > 12 ){ ++iY; iM = 1;}
					aWhere.append( iY + "-" + iM + "-1'");
				}else{
					aWhere.append( iY + "-1-1' AND ");
					aWhere.append( sAttr);
					aWhere.append( "<'");
					aWhere.append( (++iY) + "-1-1'");
				}
			}else if( iOper > 0){
				aWhere.append( sAttr);
				aWhere.append( ">='");
				if( iM > 0 ){
					if( iOper == 1 ) if( ++iM > 12 ){ ++iY; iM = 1;}
					aWhere.append( iY + "-" + iM + "-1'");
				}else{
					if( iOper == 1 ) ++iY;
					aWhere.append( iY + "-1-1'");
				}
			}else{
				aWhere.append( sAttr);
				aWhere.append( "<'");
				if( iM > 0 ){
					if( iOper == -2 ) if( ++iM > 12 ){ ++iY; iM = 1;}
					aWhere.append( iY + "-" + iM + "-1'");
				}else{
					if( iOper == -2 ) ++iY;
					aWhere.append( iY + "-1-1'");
				}
			}
			return aWhere.toString();
		}
		return sWhere;
	}
/*	public static void main(String[] args) 
	{
		try{
			QService aQ = new QService( null);
			aQ.setTableName( "vrakk");
			
			String s1 = aQ.getQueryWhere( "hukk_aeg<1900 AND hukk_aeg=1800");
			String s2 = aQ.getQueryWhere( "hukk_aeg <= 11.1900 OR hukk_aeg>=1800");
			String s3 = aQ.getQueryWhere( "hukk_aeg <= 1.2.1900 OR hukk_aeg<1900 AND hukk_aeg>=1800");
			String s4 = aQ.getQueryWhere( "hukk_aeg <1900-12 or hukk_aeg=1900 AND hukk_aeg>=1800");
			String s5 = aQ.getQueryWhere( "hukk_aeg<1900-2-1 and hukk_aeg<1900 AND hukk_aeg>=1800");
			String s6 = aQ.getQueryWhere( "hukk_aeg < 02.03.1900");
			String s7 = aQ.getQueryWhere( "1900");
		}catch( Exception aE ){
			aE.printStackTrace();
		}
	}*/
	public boolean addInsert( HttpServletRequest aRequest, DbAccess aDbIn)  throws MException
	{
		return true;
	}
	public void addUpdate( DbAccess aDbIn, HttpServletRequest aRequest)  throws MException
	{
		addUpdate( aDbIn);
	}
	public void addUpdate( DbAccess aDbIn)  throws MException
	{
	}
	public String getName() {
		return getValue();
	}
	public String getUniqueName() {
		return getValue();
	}
	public String getValue() {
		return getListName();
//				( getListWhere() == null )? getTableName(): getTableName() + "?" + getListWhere();
	}
	public int getIntValue(){ return getID();}
	public boolean isOracle()
	{
		return aSight != null && aSight.getDatabase() != null && aSight.getDatabase().isOracle();
	}
	public boolean setUpdateValue( HttpServletRequest Request, DbAccess aDB, String columnName, int iColumnType, 
			int iNullable, boolean bAll, boolean bInsert) throws MException
	{
//		if( ISServlet.hasParameter( Request, columnName)){
			String sValue = null; //getUpdateValue( columnName, Request);
			if( ISServlet.hasParameter( Request, columnName)){
				sValue = getUpdateValue( columnName, Request);
				if( sValue != null ){
				     for( int i = sValue.length(); --i >= 0;)
				          if( !Character.isWhitespace( sValue.charAt( i))) break;
//					sValue = sValue.trim();
					if( sValue.length() == 0 ) sValue = null;
				}
			}else if( !bInsert ){
				return true;
	       	}else if( sWhere != null ){ // ei saa enam aru? 
 // kui siin on m�te, siis ei peaks ka siin t�hja korral minema otse tagasi
       			int i = sWhere.indexOf( columnName + "=");
       			if( i == 0 ){ // see on �ige �ksnes siis kui seal on �ks v��rtus
       				sValue = sWhere.substring( columnName.length() + 1);
       			}
	       	}else if( sListWhere != null ){ 
       			int i = sListWhere.indexOf( columnName + "=");
       			if( i == 0 ){ // see on �ige �ksnes siis kui seal on �ks v��rtus
       				sValue = sListWhere.substring( columnName.length() + 1);
       			}
			}else
				return true; // see on uus
   			String sOldValue = bInsert? null: aDB.getString( columnName);
// see on muidugi mitte vajalik, sest kui kirje on olemas siis tuleb kontrollida �ksnes seda,
// et seda v��rtust t�hjaks ei tehta ning kui on tinimus, siis see old on alati nullist erinev   			
			if( sValue != null ){
				if( isString( iColumnType)){
					sValue = sValue.replaceAll( "\r\n",  "\r");
       				if( bInsert || !sValue.equals( sOldValue) ) aDB.setValue( columnName, sValue, true);
   				}else if( isTime( iColumnType)){
   					String sOldDateValue = getDate( aDB, columnName);
       				if( bInsert || !sValue.equals( sOldDateValue) ) setTime( aDB, columnName, sValue);
   				}else if( isDateTime( iColumnType)){
   					int iNewDateType = getDateType( sValue);
   					String sOldDateValue = getDate( aDB, columnName);
       				if( bInsert || !sValue.equals( sOldDateValue) ) setDate( aDB, columnName, sValue, iNewDateType);
   				}else if( isInt( iColumnType) ){
					long lValue = GlobalData.getLong( sValue);
					long lOldValue = aDB.getLong( columnName);
					if( bInsert || lValue != lOldValue || aDB.isNull( columnName))	aDB.setLong( columnName, lValue);
   				}else if( isBoolean( iColumnType) ){
       				if( bInsert || !sValue.substring( 0, 1).equals( sOldValue) ) aDB.setValue( columnName, sValue, true);
				}else{
					double dValue = GlobalData.getDouble( sValue);
					double dOldValue = aDB.getDouble( columnName);
					if( bInsert || Math.abs( dValue - dOldValue) > 0.0000001  || aDB.isNull( columnName) ) aDB.setDouble( columnName, dValue);
				}
/*			}else if( isBoolean( iColumnType) ){ // see pole �ige kuna vormil ei tarvitsegi olla seda
       			String sOldValue = aDB.getString( columnName); // checkbox on olemas �ksnes siis kui on linnuke
   				if( sOldValue.equals( "t") ) aDB.setValue( columnName, "false", true);*/
	 		}else if( iNullable != ResultSetMetaData.columnNoNulls ){
	   			if( !aDB.isNull( columnName) ) aDB.setNull( columnName);
   			}else{
   				aSight.setError( "Ei õnnestu muuta. Väli " + columnName + " peab olema täidetud");
   				return false;
   			}
/*  		}else if( bAll && iNullable == ResultSetMetaData.columnNullable ){
   			if( !aDB.isNull( columnName) ) aDB.setNull( columnName); 
		}*/
		return true;
	}
	public String getUpdateValue( String sColumnName, HttpServletRequest aRequest)
	{
		return aRequest.getParameter( sColumnName);
	}
	public int getReturn( HttpServletRequest aRequest)
	{
		return Sight.getReturn( aRequest);
	}
	public int getLimit( HttpServletRequest aRequest)
	{
		int iLimit = 500; 
		if( ISServlet.hasParameter( aRequest, "LIMIT") ){
			int iNewLimit = ISServlet.getParameterInt( aRequest, "LIMIT");
			if( iNewLimit < iLimit && iLimit > 0 ) iLimit = iNewLimit;
		}
		return iLimit;
	}
	public void getCSV( OutputStream outdata, Database Db, String sTableName, String sQuery)
	{
		String sSeparator = ";";
		long lSTime = 0;
		PrintWriter aWriter = new PrintWriter( outdata);
		
		if( aSight.isDebug( 1)){
			aSight.log( sQuery);
			lSTime = ( new Date()).getTime();
		}
		setRefs();
    	Statement aSt = null;
    	ResultSet aRS = null;
    	try
    	{
    	    aSt = Db.getStatement( true);
    	    aSt.execute( sQuery);
 	       	aRS = aSt.getResultSet();

 	       	boolean bSeparator = false;
 	       	ResultSetMetaData rsmd = aRS.getMetaData();
 	       	int colCount           = rsmd.getColumnCount();
	       	for (int i = 1; i <= colCount; i++)
 	       	{
 	       		String columnName = rsmd.getColumnName(i).toLowerCase();
 	       		String columnType = rsmd.getColumnTypeName( i);
 //	       		int iType = rsmd.getColumnType( i);
 	       		if( !columnType.equalsIgnoreCase( "bytea" )){
 	       			if( columnType.equalsIgnoreCase( "text")){
 	       				int s = columnName.indexOf( "geom_");
 	       				if( s == 0){
 	       					continue;
 	       				}else{
 	       				}
 	       			}else if( columnType.equalsIgnoreCase( "date")){
 	       			}else{
 	       			}
 	       			if( bSeparator ) aWriter.print( sSeparator);
 	       			aWriter.print( columnName);
 	       			bSeparator = true;
 	       		}
 	       	}
 	       	while( aRS.next())
 	       	{
 	       		bSeparator = false;
 	       		for (int i = 1; i <= colCount; i++)
 	       		{
 	       			String columnName = rsmd.getColumnName(i).toLowerCase();
 	       			String columnType = rsmd.getColumnTypeName( i);
 //	       			int iType = rsmd.getColumnType( i);
 	       			if( !columnType.equalsIgnoreCase( "bytea" )){
	 	       			Object aValue = aRS.getObject(i);
	 	       			{
		 	       			String sValue = ( aValue != null )? aValue.toString(): ""; 
	 	 	       			
		 	       			if( columnType.equalsIgnoreCase( "text")){
		 	       				int s = columnName.indexOf( "geom_");
		 	       				if( s == 0){
		 	       					continue;
		 	       				}else{
		 	       				}
		 	       			}else if( columnType.equalsIgnoreCase( "date")){
		 	       			}else{
		 	 	       			if( columnType.equalsIgnoreCase( "int4") && aRefs != null && aValue != null ){
		 	 	       				QService aFService = ( QService)aRefs.get( columnName);
		 	 	       				if( aFService != null ){
		 	 	       					int iID = Integer.parseInt(  sValue);
		 	 	       					QServiceDO aDO = aFService.getFromList( iID);
		 	 	       					if( aDO != null ){
		 	 	       						sValue = aDO.getName();
		 	 	       					}
		 	 	       				}
		 	 	       			}
		 	       			}
		 	       			if( bSeparator ) aWriter.print( sSeparator);
	 	 	       			sValue = sValue.replaceAll( "\r\n",  "\r");
	 	 	       			sValue = sValue.replaceAll( ";",  ",");
		 	       			aWriter.print( sValue);
		 	       			bSeparator = true;
	 	       			}
 	       			}
 	       		}
    	    }
 	       	if( aSight.isDebug( 1)) aSight.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
 	    }
    	catch (Exception aE)
    	{
    		aSight.log( aE);
    	}finally{
			try {
				if( aRS != null ) aRS.close();
	        	if( aSt != null ) aSt.close();
	    		aWriter.flush();
			} catch (SQLException e) {
			}
    	}
	}
	public DataObject createDataObject()
	{
		return new QServiceDO( this);
	}
	public boolean isClass( String className) {
	    try  {
	        Class.forName( className);
	        return true;
	    }  catch (ClassNotFoundException e) {
	        return false;
	    }
	}
	public void log( String sMsg){ if( aSight != null) aSight.log( sMsg); else aServlet.log( sMsg);}
	public void log( int iDebug, String sMsg){  
		if( aSight != null) aSight.log( iDebug, sMsg); else aServlet.log( iDebug, sMsg);}
	public void log( Throwable aE){ if( aSight != null) aSight.log( aE); else aServlet.log( aE); ; }
	/*
	 * 
   				
    				
       			}else if( ISServlet.hasParameter( Request, columnName)){
       				String sValue = Request.getParameter( columnName);	       				
       				if( sValue != null ){
       					if( isString( iColumnType)){
       						sValue = sValue.replaceAll( "\r\n\r\n",  "\r");
                			String sOldValue = aDB.getString( columnName);
               				if( !sValue.equals( sOldValue) )
               					aDB.setValue( columnName, sValue, true);
           				}else if( isDateTime( iColumnType)){
           					int iNewDateType = getDateType( sValue);
           					String sOldValue = getDate( aDB, columnName);
               				if( !sValue.equals( sOldValue) ){
               					setDate( aDB, columnName, sValue, iNewDateType);
               				}
           				}else if( isInt( iColumnType) ){
       						long lValue = Long.parseLong( sValue);
       						long lOldValue = aDB.getLong( columnName);
       						if( lValue != lOldValue || aDB.isNull( columnName)){
       							aDB.setValue( columnName, sValue, false);
       						}
       					}else{
       						double dValue = Double.parseDouble( sValue);
       						double dOldValue = aDB.getDouble( columnName);
       						if( Math.abs( dValue - dOldValue) > 0.0001 ){
       							aDB.setValue( columnName, sValue, false);
       						}
       					}
	       			}
          		}else if( bAll && rsmd.isNullable( i) == ResultSetMetaData.columnNullable ){
           			if( !aDB.isNull( columnName) ) aDB.setNull( columnName);
	 */
	
	public boolean load( QServiceDO aObject)
	{
		DbAccess DbIn = null;
		try {
			DbIn =  new DbAccess( getSight().getDatabase());
			String sQuery = getQuery();
			if( DbIn.select( sQuery) ){
				return aObject.load( DbIn);
			}
		} catch (MException e) {
		} finally{
			if( DbIn != null ) DbIn.close();
		}
		return false;
	}
	public OptionsList createDatas() 
	{
		QServiceDO aDO = ( QServiceDO)createDataObject();
		aDO.createDataObjects( getSight(), true);
		return aDO.getDataObjects();
	}
	public void setXML( DOMData aDoc, Node aRoot)
    {
    	if( iID > 0){
			String sQuery = getQuery( sTableName, null, null, null, 0, false);
			addXML( aDoc, aRoot, aSight.getDatabase(), null, sQuery);
    	}
    }
	public boolean update( String sTableName, String sKeyName, int iKeyId, String sAttrName, int iValue) 
	{
		return update( sTableName, sKeyName, iKeyId, sAttrName, Integer.toString( iValue));
/*	    DbAccess aDbIn = null;
		try {
			aDbIn = new DbAccess( aSight.getDatabase());
			aDbIn.setTable( sTableName, sKeyName);
			if( iKeyId > 0 ){
				aDbIn.edit( iKeyId);
				aDbIn.setInt( sAttrName, iValue);
				aDbIn.update();
				return true;
			}
		} catch ( Exception aE) {
			log( aE);
		} finally{
			if( aDbIn != null ) aDbIn.close();
		}
		return false;*/
	}
	public boolean update( String sTableName, String sKeyName, int iKeyId, String sAttrName, String sValue) 
	{
	    DbAccess aDbIn = null;
		try {
			aDbIn = new DbAccess( aSight.getDatabase());
			aDbIn.setTable( sTableName, sKeyName);
			if( iKeyId > 0 ){
				aDbIn.edit( iKeyId);
				if( sValue != null ) aDbIn.setString( sAttrName, sValue);
				else aDbIn.setNull( sAttrName);
				aDbIn.update();
				return true;
			}
		} catch ( Exception aE) {
			log( aE);
		} finally{
			if( aDbIn != null ) aDbIn.close();
		}
		return false;
	}
	public int getTableRecCount() 
	{
		String query = "select count(id) as count from " + sTableName;
		DbAccess aDbIn = null;
		try {
            aDbIn = new DbAccess( getSight().getDatabase());
            if ( aDbIn.select( query) ) return aDbIn.getInt( "count");
        } catch (MException aE) {
        } finally{
            if( aDbIn != null ) aDbIn.close();
        }
		return 0;
	}
	public void addRefs( DOMData aDoc, Node aRoot, boolean bEdit, int iRet)
	{
		if( addRef( bEdit, iRet) ){
			for( int i = aRefs.size(); --i >= 0;){
   				QService aFService = ( QService)aRefs.getValue( i);
   				if( addList( aFService) ){
   					aDoc.addChildNode( aRoot, aFService.getTableName(), aFService.getList(), 0, false, false); 
   				}
			}
		}
		
	}
	public boolean setEdit( DOMData aDoc, HttpServletRequest aRequest)
	{
		boolean bEdit = aSight.hasUserRights( sTableName, 0);
		if( bEdit && ISServlet.hasParameter( aRequest, "EDIT"))
			bEdit = ISServlet.getParameterBoolean( aRequest, "EDIT");
		Node aRoot = aDoc.getRootNode();
		aDoc.addChildNode( aRoot, "edit", bEdit);
		return bEdit;
	}
	public Geom getGeom( ResultSet aRS, String colname, int iProjection)  throws MException 
	{
		 Geom aGeom = Geom.getGeom( getClob( aRS, colname));
		 if( aGeom != null && aGeom.getSRID() == 0 ) 
			 if( GlobalGIS.isLL( aGeom.getCenter()) ) aGeom.setSRID( Projection.CS_LL);
			 else aGeom.setSRID( iProjection);
		 return aGeom;
	}
	public String getClob( ResultSet aRS, String colname)  throws MException 
	{
		try {
			Object aObj = aRS.getObject( colname);
			if( aObj instanceof PGobject ){
				PGgeometry aGeom = new PGgeometry( ((PGobject) aObj).getValue());
				return aGeom.toString();
			}else if( aObj instanceof PGgeometry ){
				PGgeometry aGeom = (PGgeometry)aRS.getObject( colname); 
				return aGeom.toString();
			}else return aRS.getString( colname);
		} catch( Exception aE) {
			Log.error( aE);
		}
		return null;
	}
}

