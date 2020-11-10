/*
 * 
 */
package ee.or.is;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.*;
import javax.servlet.http.*;

import org.xml.sax.*;
//import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.*;

import java.awt.Point;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.util.*;

//import javax.xml.parsers.*;
import ee.or.db.*;
// import sun.net.www.http.ChunkedInputStream;


/**
 * 
 * 
 * cccc
 * The Class DOMData.
 */
public class DOMData {
	/** The Doc. */
	public Document	   Doc				  = null;

	/** The dtd validate. */
	static boolean		dtdValidate;

	/** The xsd validate. */
	static boolean		xsdValidate;

	/** The ignore whitespace. */
	static boolean		ignoreWhitespace;

	/** The ignore comments. */
	static boolean		ignoreComments;

	/** The put cdata into text. */
	static boolean		putCDATAIntoText;

	/** The create entity refs. */
	static boolean		createEntityRefs;

	/** The schema source. */
	static String		 schemaSource;

	/** The s filename. */
	public String		 sFilename = null;

	/** The s file xsl. */
	private String		sFileXSL			 = null;

	/** The s lang. */
	private String		sLang				= null;

	/** The Nodes. */
	private NodeList	  Nodes				= null;

	/** The i node. */
	private int		   iNode;

	/** All output will use this encoding. */
	private static String sDefaultEncoding	 = "UTF-8";
	public static String getDefaultEncoding(){	return sDefaultEncoding;	}
	public static void setDefaultEncoding( String outputEncoding) {
		DOMData.sDefaultEncoding = outputEncoding;
	}

	/** The s encoding. */
	private String		sEncoding			= null;

	/** The s media type. */
//	private String		sMediaType		   = null;

	/** The b encoded. */
	private boolean	   bEncoded			 = false;

	/** Constants used for JAXP 1.2 */
	static final String   JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/** The Constant W3C_XML_SCHEMA. */
	static final String   W3C_XML_SCHEMA	   = "http://www.w3.org/2001/XMLSchema";

	/** The Constant JAXP_SCHEMA_SOURCE. */
	static final String   JAXP_SCHEMA_SOURCE   = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	/** The b html. */
	private boolean	   bHTML				= true;

	/** The b excel. */
	private boolean	   bExcel			   = false;
	public void setExcel( boolean bExcel){
		this.addChildNode( getRootNode(), "excel", true);
		this.bExcel = bExcel;
	}
	public boolean isExcel(){ 	return bExcel;}

	/** The s excel name. */
	private String		sOutFileName		   = null;
	public String getOutFileName( String sFormat) {
		if( sOutFileName != null && sOutFileName.indexOf( '.') > 0 ) return sOutFileName;
		return (( sOutFileName != null)? sOutFileName + "." : "report.") + sFormat;
	}
	public void setOutFileName( String sOutFileName) {
		this.sOutFileName = sOutFileName;
	}
	private int iNodeID = 0;
	public int getNodeID(){ return ++iNodeID;}
	/**
	 * Instantiates a new empty DOM Data.
	 */
	private DocumentBuilder parser = null;

	public DOMData() 
	{
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
 //       DocumentBuilder docBuilder;
		try{
			parser = dbfac.newDocumentBuilder();
			Doc = parser.newDocument();
			Element aRoot = Doc.createElement( "data");
			Doc.appendChild( aRoot);
		}catch( Exception aE ){
			Log.error( aE);
		}
	}

	/**
	 * Instantiates a new dOM data.
	 * 
	 * @param Doc
	 *            the doc
	 */
	public DOMData( Document Doc) {
		this.Doc = Doc;
	}

	/**
	 * Instantiates a new DOM Data from file.
	 * 
	 * @param sFilename
	 *            the file name
	 */
	public DOMData(String sFileName) throws ExceptionIS {
		super();
		loadFromFile( sFileName);
	}

	/**
	 * Instantiates a new DOM data.
	 * 
	 * @param In
	 *            the input stream
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public DOMData(InputStream In) throws ExceptionIS {
		this.sFilename = null;
		load( In);
	}
	public DOMData( InputStream In, String sEncoding) throws ExceptionIS {
		this.sFilename = null;
		load( In, sEncoding, true);
	}
	public DOMData( InputStream In, String sEncoding, boolean bNamespace) throws ExceptionIS {
		this.sFilename = null;
		load( In, sEncoding, bNamespace);
	}
	public DOMData( String sFileName, String sEncoding, boolean bNamespace) throws ExceptionIS {
		this.sFilename = null;
		loadFromFile( sFileName, sEncoding, bNamespace);
	}

	/**
	 * Instantiates a new DOM Data.
	 * 
	 * @param sFilename
	 *            the file name
	 * @param sRootName
	 *            the root name
	 */
	public DOMData(String sFilename, String sEncoding, String sRootName) throws ExceptionIS {
		this.sFilename = sFilename;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			parser = dbf.newDocumentBuilder();
			Doc = parser.getDOMImplementation().createDocument( null, sRootName, null);
		}catch( ParserConfigurationException aE ){
			throw new ExceptionIS( aE);
		}
	}

	/**
	 * Clone doc.
	 * 
	 * @return the DOM data
	 */
	public DOMData cloneDoc() {
		return new DOMData( Doc);
	}

	/**
	 * Close.
	 */
	public void close() {
		Doc = null;
	}

	public void loadFromFile( String sFileName) throws ExceptionIS 
	{
		loadFromFile( sFileName, null, false);
	}
	public void loadFromFile( String sFileName, String sEncoding) throws ExceptionIS 
	{
		loadFromFile( sFileName, sEncoding, false);
	}
	public boolean loadFromFile( String sFileName, String sEncoding, boolean bNamespace) throws ExceptionIS {
		this.sFilename = sFileName;
		FileInputStream aIn = null;
		try{
			aIn = new FileInputStream( sFilename);
/*			try {
				BufferedReader aReader = new BufferedReader( new InputStreamReader( aIn, sEncoding));
				while( aReader.ready())
					System.out.println( aReader.readLine());
				aReader.close();
			} catch (Exception e1) {
				Log.log( e1);
			}
			aIn = new FileInputStream( sFilename); */
			load( aIn, sEncoding, bNamespace);
		}catch( IOException e ){
			String msg = "Viga faili '" + sFilename + "' avamisel";
			new ExceptionIS( e, "Viga faili '$1' avamisel", sFilename);
			return false;
		}finally{
			try {
				if( aIn != null ) aIn.close();
			} catch (IOException e) {
			}
		}
		return true;
	}

	/**
	 * Gets the doc.
	 * 
	 * @param In
	 *            the InputStream load the xml document
	 * 
	 * @return the doc
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public void load( InputStream In) throws ExceptionIS {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments( true);  // We want to ignore comments
		dbf.setCoalescing( true);        // Convert CDATA to Text nodes
		dbf.setNamespaceAware( true);   // No namespaces: this is default
		dbf.setValidating( false);       // Don't validate DTD: also default


		
//		dbf.setNamespaceAware( true);
		try{
			parser = dbf.newDocumentBuilder();

			Doc = parser.parse( In);
		}catch( SAXException e ){
			throw new ExceptionIS( e, "Viga failis '$1' ( vigane xml ?)", sFilename);
		}catch( IOException e ){
			throw new ExceptionIS( e, "Viga failist '$1' lugemisel", sFilename);
		}catch( ParserConfigurationException e ){
			throw new ExceptionIS( e,
					"Viga faili lugemiseks valmistumisel, ei saa luua uut parserit");
		}catch( Exception e ){
			throw new ExceptionIS( e);
		}finally{
		}
	}
	public void load( InputStream In, String sEncoding, boolean bNamespace) throws ExceptionIS 
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments( true);  // We want to ignore comments
		dbf.setCoalescing( true);        // Convert CDATA to Text nodes
		dbf.setNamespaceAware( bNamespace);   // No namespaces: this is default
		dbf.setValidating( false);       // Don't validate DTD: also default
//		dbf.setNamespaceAware( true);
		try{
			parser = dbf.newDocumentBuilder();
			if( sEncoding != null ){
				InputSource aIn = new InputSource( new InputStreamReader( In, sEncoding));
				aIn.setEncoding( sEncoding);
				Doc = parser.parse( aIn);
				setEncoding( sEncoding);
			}else Doc = parser.parse( In);
		}catch( SAXException e ){
			try {
				BufferedReader aReader = new BufferedReader( new InputStreamReader( In, sEncoding));
				while( aReader.ready())
					Log.log( aReader.readLine());
			} catch (Exception e1) {
				Log.log( e1);
			}
			throw new ExceptionIS( e, "Viga failis '$1' ( vigane xml ?)", sFilename);
		}catch( IOException e ){
			throw new ExceptionIS( e, "Viga failist '$1' lugemisel", sFilename);
		}catch( ParserConfigurationException e ){
			throw new ExceptionIS( e,
					"Viga faili lugemiseks valmistumisel, ei saa luua uut parserit");
		}catch( Exception e ){
			throw new ExceptionIS( e);
		}finally{
		}
	}

	/**
	 * Gets the doc.
	 * 
	 * @param sURL
	 *            the URL to load the xml document
	 * 
	 * @return the doc
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public void load( String sURL) throws ExceptionIS 
	{
		load( sURL, null, null);
	}
	public void set( String sXML) throws ExceptionIS
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setIgnoringComments( true);  // We want to ignore comments
//		dbf.setCoalescing( true);        // Convert CDATA to Text nodes
		dbf.setNamespaceAware( false);   // No namespaces: this is default
		dbf.setValidating( false);       // Don't validate DTD: also default
//		dbf.setNamespaceAware( true);
//		DocumentBuilder parser;
		try{
			parser = dbf.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream( new StringReader( sXML));
			
			Doc = parser.parse( inStream);
		}catch( SAXException e ){
			throw new ExceptionIS( e, "Viga failis '$1' ( vigane xml ?)", sFilename);
		}catch( IOException e ){
			throw new ExceptionIS( e, "Viga failist '$1' lugemisel", sFilename);
		}catch( ParserConfigurationException e ){
			throw new ExceptionIS( e,
					"Viga faili lugemiseks valmistumisel, ei saa luua uut parserit");
		}catch( Exception e ){
			throw new ExceptionIS( e);
		}finally{
		}
//		
	}
	public void addXML( Node aRoot, String sXML) throws Exception
	{
		if( parser != null && sXML != null ){
			InputSource aIn = new InputSource();
			aIn.setCharacterStream( new StringReader( sXML));
			Document aDoc = parser.parse( aIn);
			aRoot.appendChild( Doc.importNode( aDoc.getDocumentElement(), true));
		}
	}
	public void load( String sURL, DOMData aCall, String sCharset) throws ExceptionIS 
	{
		load( sURL, aCall, sCharset, true);
	}
	public void load( String sURL, DOMData aCall, String sCharset, boolean bDOM) throws ExceptionIS 
	{
		if( sCharset == null ) sCharset = "UTF-8";
		URL conURL = null;
		HttpURLConnection urlConn = null;
	    OutputStream os = null;
		InputStream In = null;
		try{
			String sFullURL = sURL.replaceAll( " ", "%20");
			Log.info( 95, sFullURL);
			conURL = new URL( sFullURL);
			urlConn = ( HttpURLConnection)conURL.openConnection();
			urlConn.setReadTimeout( 30000);
 			urlConn.setRequestProperty( "Content-Type", "text/xml; charset=" + sCharset);
   			if( aCall != null ){
				ByteArrayOutputStream aBos = new ByteArrayOutputStream(); // 
   				aCall.write( aBos);
       			urlConn.setRequestMethod( "POST");
   				urlConn.setDoOutput( true);
   				urlConn.connect();
   				os = urlConn.getOutputStream();
    			os.write( aBos.toByteArray());
   			}else{
   				urlConn.setRequestMethod( "GET");
   				urlConn.connect();
   			}
			int iRes = urlConn.getResponseCode();
			if( iRes == HttpURLConnection.HTTP_ACCEPTED || iRes == HttpURLConnection.HTTP_OK ){
				try{
					In = urlConn.getInputStream();
					if( In != null ){
						sFilename = null;
						if( bDOM ) load( new DataInputStream( In));
						else{
							// nii see ei tï¿½ï¿½ta ja tuleks teha kuidagi kavalamalt
							// eesmï¿½rk on saada ikkagi xml kï¿½tte ja enne parsimist see parandada kui on vigu
							// vï¿½ib-olla soduda see 98, kus asjad kï¿½igepealt loetakse ja siis alles parsitakse
							FileWriter aOut = null;
							BufferedReader aReader = null;

							try {
								aReader = new BufferedReader( new  InputStreamReader( In, sCharset));
								aOut = new FileWriter( new File( ISServlet.getRealPath( "parse_error.xml")));
								String sLine = aReader.readLine();
						        while( sLine != null){
						        	boolean bChanged = false;
						        	for( int i = 0; ( i = sLine.indexOf( '&', i)) >= 0; ++i){
						        		if( !sLine.substring( i, i+5).equalsIgnoreCase( "&amp;")){
						        			sLine = sLine.substring( 0, i) + "&amp;" + sLine.substring( i+1);
						        			bChanged = true;
						        		}
						        		if( bChanged ) Log.info( sLine);
						        	}
						        	aOut.write( sLine);
						        	sLine = aReader.readLine();
					            }           
/*								int read = 0;
								byte[] bytes = new byte[1024];
						 
								while ((read = In.read(bytes)) != -1) {
									aOut.write( bytes, 0, read);
								}*/
							} catch (Exception aE) {
								Log.error( aE);;
							} finally {
								if( aOut != null ){
									try {
										aOut.close();
									} catch (IOException aE) {
										Log.error( aE);
									}
								}
						    }
						}
						return;
					}
				}finally{
					if( In != null )
						In.close();
				}
			}
			Log.info( sURL);
			throw new ExceptionIS( iRes, "Http connection return on: $1", sURL);
		}catch( Exception e ){
			Log.info( sURL);
			Log.info( "Exception : " + e.getMessage()); 
			throw new ExceptionIS( e);
//			if( bDOM ) load( sURL, aCall, sCharset, false);
		}finally{
			try {
				if( os != null ) os.close();
			} catch (IOException e) {
	  		    Log.info("Exception : " + e.getMessage());
	  		}
			if( urlConn != null ) urlConn.disconnect();
		}
//		return;
	}

	/**
	 * Gets the root node.
	 * 
	 * @return the root node
	 */
	public Node getRootNode() {
		return (Doc != null)? Doc.getDocumentElement() : null;
	}

	/**
	 * Creates the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * 
	 * @return the node
	 */
	public Node createChildNode( String sTag) 
	{
		return createChildNode( getRootNode(), sTag);
	}
	public Node createChildNode( Node NodeP, String sTag) {
		Node NodeC = ( Node)Doc.createElement( sTag);
		NodeP.appendChild( NodeC);
		return NodeC;
	}
	public Node createRootNode( String sTag) {
		Node NodeC = ( Node)Doc.createElement( sTag);
		Doc.appendChild( NodeC);
		return NodeC;
	}
	public Node removeNode( Node aRoot, String sNodeName) 
	{
		return removeNode( aRoot, findChildNode( aRoot, sNodeName));
	}
	public Node removeNode( Node aRoot, Node aNode) 
	{
		if( aNode != null ) return aRoot.removeChild( aNode);
		return null;
	}
	public void renameNodeXXX( Node aNode, String sNewName) 
	{ 
		// see millegi pï¿½rast nii ei tï¿½ï¿½ta
		// Doc.renameNode( aNode, aNode.getNamespaceURI(), sNewName);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * 
	 * @return the node
	 */
	public void addNodes( Node NodeP, DOMData aDoc)
	{
		if( aDoc != null ) getRootNode().appendChild( Doc.importNode( aDoc.getRootNode(), true));
	}
	public void addNodes( Node aNodeTo, ArrayList<Node> aNodes)
	{
		if( aNodes != null ) for( Node aNode : aNodes){
			aNodeTo.appendChild( Doc.importNode( aNode, true));
		}
	}
	public void addNodes( Node aNodeP, Node aNode)
	{
		aNodeP.appendChild( Doc.importNode( aNode, true));
	}
	public void addNodeChildren( Node aNodeTo, Node aNodeFrom)
	{
		NodeList aList = aNodeFrom.getChildNodes();
		if( aList != null ) for( int i = 0; i < aList.getLength(); ++i ){
			Node aNode = aList.item( i);
			aNodeTo.appendChild( Doc.importNode( aNode, true));
		}
	}
	public Node addChildNode( Node NodeP, String sTag) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		return NewNode;
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Value
	 *            the value
	 * 
	 * @return the node
	 */
	public Node addChildNode( Node NodeP, String sTag, StringBuffer Value) {
		return (Value != null)? addChildNode( NodeP, sTag, Value.toString()) : null;
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 * 
	 * @return the node
	 */
	public Node addChildNode( Node NodeP, String sTag, String sValue) {
		if( NodeP != null ){
			Element aRoot = ( Element)NodeP;
			int i = sTag.indexOf( '/');
			if( i > 0 ){
				iNode = 0;
				aRoot = getElement( sTag.substring( 0, i));
				if( aRoot == null ) return null;
				sTag = sTag.substring( ++i);
			}
			Element E = Doc.createElement( sTag);
			if( E != null ){
				Node NewNode = aRoot.appendChild( E);
				NewNode.appendChild( Doc
						.createTextNode( (sValue != null)? (isEncoded()? encode( sValue) : sValue)
								: ""));
				return NewNode;
			}
		}
		return null;
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param cValue
	 *            the c value
	 * 
	 * @return the node
	 */
	public Node addChildNode( Node NodeP, String sTag, char cValue) {
		return addChildNode( NodeP, sTag, Character.toString( cValue));
	}

	/**
	 * Adds the child node hyperlink.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 */
	public void addChildNodeHyperlink( Node NodeP, String sTag, String sValue) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		HyperlinkEdit H = new HyperlinkEdit( sValue);
		Node NodeHL = NewNode.appendChild( Doc.createElement( "href"));
		NodeHL.appendChild( Doc.createTextNode( H.getHyperlink()));
		Node NodeName = NewNode.appendChild( Doc.createElement( "name"));
		NodeName.appendChild( Doc.createTextNode( H.getName()));
		Node NodeLink = NewNode.appendChild( Doc.createElement( "link"));
		NodeLink.appendChild( Doc.createTextNode( H.getLink()));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param DbIn
	 *            the db in
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public void addChildNode( Node NodeP, String sTag, DbAccess DbIn) throws MException {
		addChildNode( NodeP, sTag, DbIn.getString( sTag));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param DbIn
	 *            the db in
	 * @param sFormat
	 *            the s format
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public void addChildNode( Node NodeP, String sTag, DbAccess DbIn, String sFormat)
			throws MException {
		addChildNode( NodeP, sTag, DbIn.getDateTime( sTag), sFormat);
	}

	/**
	 * Adds the child node time m.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iTime
	 *            the i time
	 */
	public void addChildNodeTimeM( Node NodeP, String sTag, int iTime) {
		double dTime = iTime / 60;
		dTime += (iTime - iTime / 60 * 60) * 0.01;
		String sTime = GlobalFunc.DoubleToString( dTime, 2);
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		NewNode.appendChild( Doc.createTextNode( (sTime != null)? sTime : ""));
	}

	/**
	 * Adds the child node time s.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iTime
	 *            the i time
	 */
	public void addChildNodeTimeS( Node NodeP, String sTag, int iTime) {
		/*
		 * double dTime = iTime/60; dTime += (iTime - iTime/60*60)*0.01; String
		 * sTime = GlobalFunc.DoubleToString( dTime, 2);
		 */
		String sTime = GlobalFunc.getTimeHM2( iTime);
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		NewNode.appendChild( Doc.createTextNode( (sTime != null)? sTime : ""));
	}

	/**
	 * Adds the child node length km.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iLength
	 *            the i length
	 */
	public void addChildNodeLengthKM( Node NodeP, String sTag, int iLength) {
		addChildNode( NodeP, sTag, GlobalFunc.DoubleToString( iLength * 0.001, 1));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iValue
	 *            the i value
	 */
	public void addChildNode( Node NodeP, String sTag, int iValue) {
		addChildNode( NodeP, sTag, Integer.toString( iValue));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param lValue
	 *            the l value
	 */
	public void addChildNode( Node NodeP, String sTag, long lValue) {
		addChildNode( NodeP, sTag, Long.toString( lValue));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param dValue
	 *            the d value
	 */
	public void addChildNode( Node NodeP, String sTag, double dValue) {
		addChildNode( NodeP, sTag, Double.toString( dValue));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param bValue
	 *            the b value
	 */
	public Node addChildNode( Node NodeP, String sTag, boolean bValue) {
		return addChildNode( NodeP, sTag, bValue? "true" : "false");
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param DateValue
	 *            the date value
	 */
	public void addChildNode( Node NodeP, String sTag, Date DateValue) {
		addChildNode( NodeP, sTag, GlobalFunc.DateToString2( DateValue));
	}

	/**
	 * Adds the child node date time.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param DateValue
	 *            the date value
	 */
	public void addChildNodeDate( Node NodeP, String sTag, Date DateValue) {
		addChildNode( NodeP, sTag, GlobalDate.getDateString( DateValue.getTime()));
	}
	public void addChildNodeDateTime( Node NodeP, String sTag, Date DateValue) {
		addChildNode( NodeP, sTag, GlobalDate.getDateTimeString( DateValue.getTime()));
	}
	public void addChildNodeDateTime( Node NodeP, String sTag, long lDateValue) {
		addChildNode( NodeP, sTag, GlobalDate.getDateTimeString( lDateValue));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param DateValue
	 *            the date value
	 * @param sFormat
	 *            the s format
	 */
	public void addChildNode( Node NodeP, String sTag, java.sql.Date DateValue, String sFormat) {
		if( DateValue == null || sFormat == null )
			return;
		addChildNode( NodeP, sTag, GlobalDate.getDateTime( DateValue, sFormat));
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, String sValue, boolean bHidden) {

		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		NewNode.appendChild( Doc.createTextNode( sValue));
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param List
	 *            the list
	 * @param sDValue
	 *            the s d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, OrderedVector List, String sDValue,
			boolean bEmpty, boolean bHidden) {
		addChildNode( NodeP, sTag, List.getOptionsList(), sDValue, bEmpty, bHidden);
	}
	public void addChildNode( Node NodeP, String sTag, OrderedVector List, int iValue,
			boolean bEmpty, boolean bHidden) {
		addChildNode( NodeP, sTag, List.getOptionsList(), Integer.toString( iValue), bEmpty, bHidden);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param OrderedValues
	 *            the ordered values
	 * @param OrderedNames
	 *            the ordered names
	 * @param sDValue
	 *            the s d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, List<String> OrderedValues, List<String> OrderedNames,
			String sDValue, boolean bEmpty, boolean bHidden) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		// addChildNode( NewNode, "ALT", sAlt);
		// if( sControl != null ) addChildNode( NewNode, "JSCRIPT", sControl);
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
		Node Options = createChildNode( NewNode, "options");
		if( bEmpty ){
			Node Option = Options.appendChild( Doc.createElement( "option"));
			if( sDValue == null || sDValue.length() == 0 )
				addChildNode( Option, "selected", "true");
		}
		if( OrderedValues != null )
			for( int i = 0; i < OrderedValues.size(); ++i){
				String sValue = ( String)OrderedValues.get( i);
				String sName = (OrderedNames != null)? ( String)OrderedNames.get( i) : sValue;

				Node Option = Options.appendChild( Doc.createElement( "option"));
				addChildNode( Option, "value", sValue);
				addChildNode( Option, "name", sName);
				if( sDValue != null && sDValue.equals( sValue) )
					addChildNode( Option, "selected", "true");
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param List
	 *            the list
	 * @param sDValue
	 *            the s d value
	 */
	public void addChildNode( Node NodeP, String sTag, OptionsList List, String sDValue) {
		addChildNode( NodeP, sTag, List, sDValue, false, false);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param List
	 *            the list
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, OptionsList List, boolean bHidden) {
		Node NList = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NList, "HIDDEN", "true");
		if( List != null )
			for( int i = 0; i < List.size(); ++i){
				Node Elem = NList.appendChild( Doc.createElement( "option"));
				addChildNode( Elem, "value", List.getValue( i));
				addChildNode( Elem, "name", List.getName( i));
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param List
	 *            the list
	 * @param iID
	 *            the i id
	 */
	public void addChildNode( Node NodeP, String sTag, String[] List, int iID) {
		Node NList = NodeP.appendChild( Doc.createElement( sTag));
		if( List != null )
			for( int i = 0; i < List.length; ++i){
				Node Elem = NList.appendChild( Doc.createElement( "element"));
				addChildNode( Elem, "value", i + 1);
				addChildNode( Elem, "name", List[i]);
				if( iID == i )
					addChildNode( Elem, "selected", "true");
			}
		addChildNode( NList, "value", iID);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param List
	 *            the list
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, OrderedVector List, boolean bHidden) {
		Node NList = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NList, "HIDDEN", "true");
		if( List != null )
			for( int i = 0; i < List.size(); ++i){
				Node Elem = NList.appendChild( Doc.createElement( "element"));
				addChildNode( Elem, "value", List.getValue( i));
				addChildNode( Elem, "name", List.getName( i));
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Options
	 *            the options
	 * @param sDValue
	 *            the s d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, OptionsList Options, String sDValue,
			boolean bEmpty, boolean bHidden) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
		addChildNode( NewNode, Options, sDValue, bEmpty, bHidden);
		/*
		 * Node Opts = createChildNode( NewNode, "options"); if( bEmpty ){ Node
		 * Option = Opts.appendChild( Doc.createElement( "option") ); if(
		 * sDValue == null || sDValue.length() == 0 ) addChildNode( Option,
		 * "selected", "true"); } if( Options != null ) for( int i = 0; i <
		 * Options.size(); ++i){ Node Option = Opts.appendChild(
		 * Doc.createElement( "option") ); String sValue = Options.getValue( i);
		 * addChildNode( Option, "value", sValue); addChildNode( Option, "name",
		 * Options.getName( i)); if( sDValue != null && sDValue.equals( sValue)
		 * ) addChildNode( Option, "selected", "true"); }
		 */
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param Options
	 *            the options
	 * @param sDValue
	 *            the s d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, OptionsList Options, String sDValue, boolean bEmpty,
			boolean bHidden) {
		addChildNode( NodeP, "value", sDValue);
		Node Opts = createChildNode( NodeP, "options");
		if( bEmpty ){
			Node Option = Opts.appendChild( Doc.createElement( "option"));
			if( sDValue == null || sDValue.length() == 0 )
				addChildNode( Option, "selected", "true");
		}
		if( Options != null )
			for( int i = 0; i < Options.size(); ++i){
				Node Option = Opts.appendChild( Doc.createElement( "option"));
				String sValue = Options.getValue( i);

				addChildNode( Option, "value", sValue);
				addChildNode( Option, "name", Options.getName( i));
				if( sDValue != null && sDValue.equals( sValue) )
					addChildNode( Option, "selected", "true");
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Options
	 *            the options
	 * @param iDValue
	 *            the i d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, OptionsList Options, int iDValue,
			boolean bEmpty, boolean bHidden) {
		addChildNode( NodeP, sTag, Options, Integer.toString( iDValue), bEmpty, bHidden);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Options
	 *            the options
	 * @param iDValue
	 *            the i d value
	 */
	public void addChildNode( Node NodeP, String sTag, OptionsList Options, int iDValue) {
		addChildNode( NodeP, sTag, Options, Integer.toString( iDValue), false, false);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iValue
	 *            the i value
	 * @param i0
	 *            the i0
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, int iValue[], int i0, boolean bHidden) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
		Node Options = createChildNode( NewNode, "options");
		if( iValue != null )
			for( int i = i0; i < iValue.length; i += 2){
				Node Option = Options.appendChild( Doc.createElement( "option"));
				addChildNode( Option, "value", iValue[i]);
				addChildNode( Option, "name", iValue[i]);
			}
	}
	public void addChildNode( Node NodeP, String sTag, int iValue[], boolean bHidden) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
		Node Options = createChildNode( NewNode, "options");
		if( iValue != null )
			for( int i = 0; i < iValue.length; ++i){
				Node Option = Options.appendChild( Doc.createElement( "option"));
				addChildNode( Option, "value", iValue[i]);
				addChildNode( Option, "name", iValue[i]);
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Options
	 *            the options
	 * @param sDValue
	 *            the s d value
	 * @param bEmpty
	 *            the b empty
	 * @param bHidden
	 *            the b hidden
	 */
	public void addChildNode( Node NodeP, String sTag, Marray Options, String sDValue,
			boolean bEmpty, boolean bHidden) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		if( bHidden )
			addChildNode( NewNode, "HIDDEN", "true");
		Node Opts = createChildNode( NewNode, "options");
		if( bEmpty ){
			Node Option = Opts.appendChild( Doc.createElement( "option"));
			if( sDValue == null || sDValue.length() == 0 )
				addChildNode( Option, "selected", "true");
		}
		if( Options != null )
			try{
				for( int i = 0; i < Options.size(); ++i){
					Node Option = Opts.appendChild( Doc.createElement( "option"));
					String sValue = Options.getKey( i);
					addChildNode( Option, "value", sValue);
					addChildNode( Option, "name", ( String)Options.getValue( i));
					if( sDValue != null && sDValue.equals( sValue) )
						addChildNode( Option, "selected", "true");
				}
			}catch( Exception e ){
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Ps
	 *            the ps
	 */
	public void addChildNode( Node NodeP, String sTag, Point Ps[]) {
		addChildNode( NodeP, sTag, Ps, null);
		/*
		 * Node NewNode = NodeP.appendChild( Doc.createElement( sTag) ); if( Ps
		 * != null ) for( int i = 0; i < Ps.length; ++i){ if( Ps[ i] != null ){
		 * Node NC = NewNode.appendChild( Doc.createElement( "coord"));
		 * addChildNode( NC, "x", Ps[ i].x); addChildNode( NC, "y", Ps[ i].y); }
		 * }
		 */
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param Ps
	 *            the ps
	 * @param Tooltips
	 *            the tooltips
	 */
	public void addChildNode( Node NodeP, String sTag, Point Ps[], String[] Tooltips) {
		Node NewNode = NodeP.appendChild( Doc.createElement( sTag));
		if( Ps != null )
			for( int i = 0; i < Ps.length; ++i){
				if( Ps[i] != null ){
					Node NC = NewNode.appendChild( Doc.createElement( "coord"));
					addChildNode( NC, "x", Ps[i].x);
					addChildNode( NC, "y", Ps[i].y);
					if( Tooltips != null && i < Tooltips.length )
						addChildNode( NewNode, "tooltip", Tooltips[i]);
				}
			}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param P
	 *            the p
	 */
	public void addChildNode( Node NodeP, String sTag, Point2D P) {
		// Node NewNode = NodeP.appendChild( Doc.createElement( sTag) );
		Node NC = NodeP.appendChild( Doc.createElement( sTag));
		if( P != null ){
			addChildNode( NC, "x", Math.round( P.getX()));
			addChildNode( NC, "y", Math.round( P.getY()));
		}
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param P
	 *            the p
	 */
	public void addChildNode( Node NodeP, String sTag, Point P) {
		// Node NewNode = NodeP.appendChild( Doc.createElement( sTag) );
		Node NC = NodeP.appendChild( Doc.createElement( sTag));
		if( P != null ){
			addChildNode( NC, "ix", P.x);
			addChildNode( NC, "iy", P.y);
		}
	}

	/*
	 * public void addChildNode( Node NodeP, String sTag, Node NodeC ) { Node
	 * NewNode = NodeP.appendChild( Doc.createElement( sTag) );
	 * NewNode.appendChild( NodeC ); }
	 */
	/**
	 * Find node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sName
	 *            the s name
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 * 
	 * @return the node
	 */
	public Node findNode( Node NodeP, String sName, String sTag, String sValue) {
		// NodeList Nodes = getNodes( NodeP, sName);

		// if( Nodes != null )

		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node Node = Nodes.item( iNode);
			if( Node.getNodeName().equals( sName) && findChildNode( Node, sTag, sValue) != null )
				return Node;
		}
		return null;
	}

	/**
	 * Find node2.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sName
	 *            the s name
	 * @param sTag1
	 *            the s tag1
	 * @param sValue1
	 *            the s value1
	 * @param sTag2
	 *            the s tag2
	 * @param sValue2
	 *            the s value2
	 * 
	 * @return the node
	 */
	public Node findNode2( Node NodeP, String sName, String sTag1, String sValue1, String sTag2,
			String sValue2) {
		// NodeList Nodes = getNodes( NodeP, sName);

		// if( Nodes != null )

		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node Node = Nodes.item( iNode);
			if( Node.getNodeName().equals( sName)
					&& findChildNode2( Node, sTag1, sValue1, sTag2, sValue2) != null )
				return Node;
		}
		return null;
	}

	/**
	 * Find child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * 
	 * @return the node
	 */
	public Node findChildNode( Node NodeP, String sTag) {
		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node NodeC = Nodes.item( iNode);
			if( NodeC.getNodeName().equals( sTag) )
				return NodeC;
		}
		return null;
	}
	public ArrayList<Node> findChildNodes( Node NodeP, String sTag) 
	{
		NodeList Nodes = NodeP.getChildNodes();
		ArrayList<Node> aNodes = new ArrayList<Node>();

		int nNode = Nodes.getLength();
		for( int iNode = 0; iNode < nNode; ++iNode ){
			Node aNode = Nodes.item( iNode);
			if( aNode.getNodeName().equals( sTag) ) aNodes.add( aNode);
		}
		return aNodes;
	}

	/**
	 * Find child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iNext
	 *            the i next
	 * 
	 * @return the node
	 */
	public Node findChildNode( Node NodeP, String sTag, int iNext) {
		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node NodeC = Nodes.item( iNode);
			if( NodeC.getNodeName().equals( sTag) && iNext-- == 0 )
				return NodeC;
		}
		return null;
	}

	/**
	 * Find child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 * 
	 * @return the node
	 */
	public Node findChildNode( Node NodeP, String sTag, String sValue) {
		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node NodeC = Nodes.item( iNode);
			if( NodeC.getNodeName().equals( sTag) )
				if( NodeC.hasChildNodes()
						&& NodeC.getChildNodes().item( 0).getNodeValue().equals( sValue) )
					return NodeC;
		}
		return null;
	}

	/**
	 * Find child node2.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag1
	 *            the s tag1
	 * @param sValue1
	 *            the s value1
	 * @param sTag2
	 *            the s tag2
	 * @param sValue2
	 *            the s value2
	 * 
	 * @return the node
	 */
	public Node findChildNode2( Node NodeP, String sTag1, String sValue1, String sTag2,
			String sValue2) {
		NodeList Nodes = NodeP.getChildNodes();
		int iRet = 0;

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node NodeC = Nodes.item( iNode);
			if( NodeC.getNodeName().equals( sTag1) && NodeC.hasChildNodes()
					&& NodeC.getChildNodes().item( 0).getNodeValue().equals( sValue1) )
				iRet |= 1;
			else if( NodeC.getNodeName().equals( sTag2) && NodeC.hasChildNodes()
					&& NodeC.getChildNodes().item( 0).getNodeValue().equals( sValue2) )
				iRet |= 2;
			else
				continue;
			if( iRet == 3 )
				return NodeP;
		}
		return null;
	}

	/**
	 * Gets the child value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * 
	 * @return the child value
	 */
	public String getChildValue( Node NodeP, String sTag) {
		NodeList Nodes = NodeP.getChildNodes();

		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node NodeC = Nodes.item( iNode);
			if( NodeC.getNodeName().equals( sTag) )
				if( NodeC.hasChildNodes() )
					return NodeC.getChildNodes().item( 0).getNodeValue();
				else
					return "";
		}
		// for ( Node NodeC = NodeP.getFirstChild(); NodeC != null; NodeC =
		// NodeP.getNextSibling())
		// if( NodeC.getNodeName().equals( sTag) ) return NodeC.getNodeValue();

		// NodeList Nodes = getNodes( NodeP, sTag);
		// if( Nodes != null && Nodes.getLength() > 0 ) return Nodes.item(
		// 0).getNodeValue();

		/*
		 * for ( Node NodeC = NodeP.getFirstChild(); NodeC != null; NodeC =
		 * NodeP.getNextSibling()) if( NodeC.getNodeName().equals( sTag) )
		 * return NodeC.getNodeValue();
		 */
		return null;
		// return getText( getChildbyTagName( node, TagName ));
	}

	/*
	 * public Node getChildbyTagName( Node node, String TagName ) { Node n; for
	 * ( n = node.getFirstChild(); n != null; n = n.getNextSibling()) if(
	 * n.getNodeName().equals(TagName) ) break; return n; } public void
	 * setText(Node node, String Value ) { // Document Doc =
	 * node.getOwnerDocument(); NodeList list = node.getChildNodes(); if (
	 * list.getLength() == 1 && list.item(0).getNodeType() == Node.TEXT_NODE){
	 * list.item(0).setNodeValue(Value); } else { for (int i=0; i <
	 * list.getLength(); i++) node.removeChild(list.item(i)); node.appendChild(
	 * Doc.createTextNode(Value) ); } } /* public String getText(Node node) {
	 * StringBuffer result = new StringBuffer(); if ( node == null ||
	 * !node.hasChildNodes()) return null; NodeList list = node.getChildNodes();
	 * for (int i=0; i < list.getLength(); i++) { Node subnode = list.item(i);
	 * if (subnode.getNodeType() == Node.TEXT_NODE)
	 * result.append(subnode.getNodeValue()); else if (subnode.getNodeType() ==
	 * Node.CDATA_SECTION_NODE) result.append(subnode.getNodeValue()); else if
	 * (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) { // Recurse into
	 * the subtree for text // (and ignore comments)
	 * result.append(getText(subnode)); } } return result.toString(); }
	 */
	/**
	 * Find element.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sName
	 *            the s name
	 * @param sAttr
	 *            the s attr
	 * @param sValue
	 *            the s value
	 * 
	 * @return the element
	 */
	public Element findElement( Element NodeP, String sName, String sAttr, String sValue) {
		NodeList Nodes = NodeP.getElementsByTagName( sName);
		if( Nodes != null )
			for( int iNode = Nodes.getLength(); --iNode >= 0;){
				Element Node = ( Element)Nodes.item( iNode);
				if( sValue.equals( Node.getAttribute( sAttr)) )
					return Node;
			}
		return null;
	}

	/**
	 * Find element.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sName
	 *            the s name
	 * 
	 * @return the element
	 */
	public Element findElement( Element NodeP, String sName) {
		if( NodeP != null ){
			NodeList Nodes = NodeP.getChildNodes(); // ..getElementsByTagName(
			// sName);
			if( Nodes != null )
				for( int i = 0, j = iNode; i < Nodes.getLength(); ++i){
					Node N = Nodes.item( i);
					if( N instanceof Element && (( Element)N).getTagName().equals( sName) ){
						if( --j < 0 )
							return ( Element)Nodes.item( i);
					}
				}
		}
		return null;
	}

	/**
	 * Gets the elem attr.
	 * 
	 * @param elem
	 *            the elem
	 * @param attrname
	 *            the attrname
	 * 
	 * @return the elem attr
	 */
	public String getElemAttr( Element elem, String attrname) {

		if( elem != null && elem.hasAttribute( attrname) )
			return elem.getAttribute( attrname);
		// String attr = elem.getAttribute( attrname);
		// if ( (attr != null) /* && ( attr.length() >= 1) */ ) return attr;
		return null;
	}

	/**
	 * Gets the elem attr.
	 * 
	 * @param nodename
	 *            the nodename
	 * @param attrname
	 *            the attrname
	 * 
	 * @return the elem attr
	 */
	public String getElemAttr( String nodename, String attrname) {
		return getElemAttr( getElement( nodename), attrname);
	}

	/**
	 * Gets the elem attr.
	 * 
	 * @param elem
	 *            the elem
	 * @param attrname
	 *            the attrname
	 * @param stderrmsg
	 *            the stderrmsg
	 * 
	 * @return the elem attr
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public String getElemAttr( Element elem, String attrname, String stderrmsg) throws MException {
		String attr;

		if( elem == null )
			return null;
		attr = elem.getAttribute( attrname);
		if( (attr != null) /* && ( attr.length() >= 1) */)
			return attr;
		else{
			throw new MException( stderrmsg + attrname);
		}
	}

	/**
	 * Gets the elem attr int.
	 * 
	 * @param E
	 *            the e
	 * @param attrname
	 *            the attrname
	 * 
	 * @return the elem attr int
	 */
	public int getElemAttrInt( String nodename, String attrname) {
		return getElemAttrInt( getElement( nodename), attrname);
	}
	public int getElemAttrInt( Element E, String attrname) {
		String s = getElemAttr( E, attrname);
		return (s != null && s.length() > 0)? Integer.parseInt( s) : 0;
	}

	/**
	 * Gets the elem attr boolean.
	 * 
	 * @param E
	 *            the e
	 * @param attrname
	 *            the attrname
	 * 
	 * @return the elem attr boolean
	 */
	public boolean getElemAttrBoolean( Element E, String attrname) {
		String s = getElemAttr( E, attrname);
		return s != null && s.equalsIgnoreCase( "true");
	}

	/**
	 * Gets the elem attr double.
	 * 
	 * @param E
	 *            the e
	 * @param attrname
	 *            the attrname
	 * 
	 * @return the elem attr double
	 */
	public double getElemAttrDouble( Element E, String attrname) {
		String s = getElemAttr( E, attrname);
		return (s != null && s.length() > 0)? Double.parseDouble( s) : 0;
	}

	/**
	 * Sets the elem attr.
	 * 
	 * @param elem
	 *            the elem
	 * @param attrname
	 *            the attrname
	 * @param value
	 *            the value
	 */
	public void setElemAttr( Node elem, String attrname, String value) {
		(( Element)elem).setAttribute( attrname, value);
	}
	public void setElemAttr( Element elem, String attrname, String value) {
		elem.setAttribute( attrname, value);
	}
	public void setElemAttr( Node elem, String attrname, int value) {
		(( Element)elem).setAttribute( attrname, Integer.toString( value));
	}
	public void setElemAttr( Element elem, String attrname, int value) {
		elem.setAttribute( attrname, Integer.toString( value));
	}

	/**
	 * Write file.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public boolean writeFile() throws  Exception {
		return writeFile( sFilename);
	}

	/**
	 * Write file.
	 * 
	 * @param sPath
	 *            the s path
	 * @param sFile
	 *            the s file
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public boolean writeFile( String sPath, String sFile) throws  Exception 
	{
		if( sPath == null )	return false;
		String sFileName = sPath + "/" + sFile;
		if( sFile.indexOf( '.') < 0 ) sFileName += ".xml";
		Log.info( " writeFile " + sFileName);
		return writeFile( sFileName);
	}

	/**
	 * Write xsl.
	 * 
	 * @param Response
	 *            the response
	 * @param sTempPath
	 *            the s temp path
	 * 
	 * @return true, if successful
	 * @throws Exception 
	 */
	private String sFormat = null;
	public boolean writeXSL( HttpServletResponse Response, Sight aSight, String sFormat) throws Throwable
	{
/*		String sFileName = ISServlet.getRealPath( ( (sLang == null)? "WEB-INF/xsl/"
				: "WEB-INF/xsl_" + sLang + "/")
				+ sFileXSL + ".xsl"); */
//		PrintWriter Out = null;
		this.sFormat = sFormat;
		try{
			if( !bExcel && ( sFormat == null || sFormat.length() == 0 || sFormat.equalsIgnoreCase( "html"))){
				Response.setContentType( "text/html; charset=" + getEncoding());
				Response.setHeader( "Pragma", "no-cache");
				Response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate");
				// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
//				Response.addHeader( "Cache-Control", "post-check=0, pre-check=0");
				// Set standard HTTP/1.0 no-cache header.
				Response.setHeader( "Expires", "Sat, 6 May 2000 12:00:00 GMT");
				sFormat = "html";
				// Set standard HTTP/1.1 no-cache headers.
			}else if( bExcel || sFormat.equalsIgnoreCase( "excel") ){
				Response.setContentType( "application/msexcel; charset=" + getEncoding());
				Response.setHeader( "Content-Disposition", "inline; filename=\"" + getOutFileName( "xsl")
						+ "\"");
				Response.setHeader( "Cache-control", "must-revalidate");
				Response.setHeader( "Pragma", "public");
			}else if( sFormat.equalsIgnoreCase( "csv") || sFormat.equalsIgnoreCase( "meta")){
				Response.setContentType( "application/octet-stream; charset=" + getEncoding());
				Response.setHeader( "Content-Disposition", "inline; filename=\"" + getOutFileName( "csv") + "\"");
				Response.setHeader( "Cache-control", "must-revalidate");
				Response.setHeader( "Pragma", "public");
				addChildNode( getRootNode(), "format", "csv");
//				sFileXSL += "CSV";
	
			}else if( sFormat.equalsIgnoreCase( "kml")){
				Response.setContentType( "application/vnd.google-earth.kml+xml");
		        Response.setHeader( "Content-Disposition", "attachment; filename=\"" + getOutFileName( "kml") + "\"");
				Response.setHeader( "Cache-control", "must-revalidate");
				Response.setHeader( "Pragma", "public");
				addChildNode( getRootNode(), "format", "csv");
			}
		}catch( Throwable aE ){
			if( aSight != null && aSight.isDebug( 50)){
				writeFile( aSight, null, null);
			}
			throw aE;
		}
		return writeXSL( Response.getWriter(), aSight);
	}

	/**
	 * Write xsl.
	 * 
	 * @param Out
	 *            the out
	 * @param sTempPath
	 *            the s temp path
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException
	 *             the m exception
	 */
	public boolean writeXSL( PrintWriter aOut, Sight aSight) throws Throwable
	{
//		String sFileName = ISServlet.getRealPath( "WEB-INF/xsl/" + sFileXSL + ".xsl");
		try{
			Transformer xformer = null;
			DOMSource aSource = null;
			File aXSLFile = new File( ISServlet.getRealPath( "WEB-INF/xsl"), sFileXSL + ".xsl");
			boolean bExists = aXSLFile.exists();
			if( !bExists ){
				int i = sFileXSL.indexOf( "/");
				if( i > 0 ){
					aXSLFile =  new File( ISServlet.getRealPath( "WEB-INF/xsl"), sFileXSL.substring( ++i) + ".xsl");
					bExists = aXSLFile.exists();
				}
			}
			if( bExists ){
				aSource = new DOMSource( Doc);
				javax.xml.transform.sax.SAXTransformerFactory factory =
					( SAXTransformerFactory)javax.xml.transform.sax.SAXTransformerFactory.newInstance();
				FileInputStream  FIPS =  new FileInputStream( aXSLFile );
				StreamSource 	 SS   =  new StreamSource( FIPS, ISServlet.getRealPath());
				Templates template 	  = factory.newTemplates( SS );
				if( template == null ){
					aSight.log( factory.getClass().getName());
					aSight.log( aXSLFile.getName());
				}
				xformer = template.newTransformer(); 
				if( aSight != null && aSight.isDebug( 98))	writeFile( aSight, xformer, aSource);
				if( xformer != null ){
					Result aResult = new StreamResult( aOut);
					if( sFormat != null && ( sFormat.equalsIgnoreCase( "kml") || sFormat.equalsIgnoreCase( "csv")) ){
						xformer.setOutputProperty( OutputKeys.INDENT, "yes");
						xformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4");
					}
					xformer.transform( aSource, aResult);
					return true;
				}
			}
		}catch( Throwable aE ){
			if( aSight != null && aSight.isDebug( 50)){
				aSight.log( aE);
				aSight.log( (javax.xml.transform.sax.SAXTransformerFactory.newInstance()).getClass().getName());
				writeFile( aSight, null, null);
			}
		}finally{
			if( aOut != null) aOut.close();
		}
		return false;
	}

	public boolean writeFile( Sight aSight, Transformer xformer, DOMSource Source)
	{
		String sTempPath = aSight.getLogCatName();
		if( sTempPath != null ){
			String sTempFile = sFileXSL;
			int i = sTempFile.indexOf( '/');
			if( i > 0 )	sTempFile = sFileXSL.substring( ++i);
			PrintWriter aPOut = null;
			try{
				if( xformer != null && Source != null && aSight.isDebug( 99) ){
					aPOut = getWriter( sTempPath + "/" + sTempFile + "." + ((sFormat!= null)? sFormat: "html"));
					Result aResult = new StreamResult( aPOut);
					xformer.transform( Source, aResult);
				}
			}catch( Exception aE ){
				Log.error( aSight, aE, false);
			}finally{
				if( aPOut != null ) aPOut.close();
			}
			return writeFile( sTempPath + "/" + sTempFile + ".xml");
		}
		return false;
	}
	public boolean writeFile( String sFileName) 
	{
		boolean bRet = false;
		if( Doc != null && sFileName != null ){
			try{
				write( new FileOutputStream( new File( sFileName)));
			}catch( Exception aE ){
				Log.error( aE);
			}
		}
		return bRet;
	}
	public void write( OutputStream Out) throws Exception{	write( new StreamResult(( Out)));}
	public void write( HttpServletResponse Response) throws Exception 
	{
		Response.setContentType( "text/xml; charset=" + getEncoding());
		Response.setHeader( "Pragma", "no-cache");
		Response.setHeader( "Expires", "0");
		Response.setCharacterEncoding( getEncoding());
		write( Response.getWriter());
	}
	public void write( HttpServletResponse Response, String sDocType) throws Exception {
		Response.setContentType( sDocType); // + "; charset=" + getEncoding());
		Response.setHeader( "Pragma", "no-cache");
		Response.setHeader( "Expires", "0");
		Response.setCharacterEncoding( getEncoding());
		write( Response.getWriter());
	}
	public void write( StringWriter Out) throws Exception { write( new StreamResult( Out));}
	public void write( PrintWriter Out) throws Exception {	write( new StreamResult( Out));}
	public void write( StreamResult Result) throws Exception 
	{
// xslt ei lÃ¤he siit lÃ¤bi
		TransformerFactory tFactory = TransformerFactory.newInstance();
//		tFactory.setAttribute( "indent-number", 4); 
		Transformer Transformer = tFactory.newTransformer();
		Transformer.setOutputProperty( OutputKeys.METHOD, "xml");
		Transformer.setOutputProperty( OutputKeys.ENCODING, getEncoding());
		Transformer.setOutputProperty( OutputKeys.INDENT, "yes");
		Transformer.setOutputProperty( OutputKeys.STANDALONE, "yes");
		Transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4");
		// Transformer.setOutputProperty( OutputKeys.MEDIA_TYPE,
		// "application/x-www-form-urlencoded");OutputKeys
//		Transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); 
		Transformer.setOutputProperty( OutputKeys.MEDIA_TYPE, "text/xml");
		// Transformer xformer = tFactory.newTransformer();
		DOMSource Source = new DOMSource( Doc);
		Transformer.transform( Source, Result);
	}
	public static PrintWriter getWriter( String sFilename) throws Exception {
		return new PrintWriter( new FileOutputStream( new File( sFilename)));
	}
	public String getValue( Node aNode)
	{
		String sValue = null;
		if( aNode != null && aNode.hasChildNodes() ) {
			sValue = aNode.getChildNodes().item( 0).getNodeValue();
			if( sValue != null ) sValue = sValue.trim();
		}
		return sValue;
	}
	public boolean hasValue( Node aNode)
	{
		String sValue = getValue( aNode);
		return sValue != null && sValue.length() > 0;
	}
	public void setValue( Node aNode, String sValue) {
		if( sValue == null ) sValue = "";
		if( aNode.hasChildNodes() )
			aNode.getChildNodes().item( 0).setNodeValue( sValue);
		else
			aNode.appendChild( Doc.createTextNode( sValue));
	}
	public String getValue( Node aNode, String sName) 
	{
		int i = sName.indexOf( '/');
		if( i > 0 ){
			iNode = 0;
			Node aChildNode = findChildNode( aNode, sName.substring( 0, i));
			return getValue( aChildNode, sName.substring( ++i));
		}
		iNode = 0;
		Node aChildNode = findChildNode( aNode, sName);
		return getValue( aChildNode);
	}

	/**
	 * Gets the values.
	 * 
	 * @param NodeC
	 *            the node c
	 * @param sName
	 *            the s name
	 * 
	 * @return the values
	 */
	public String[] getValues( Node NodeC, String sName) {
		ArrayList<Element> Nodes = getElements( ( Element)NodeC, sName);
		if( Nodes != null && Nodes.size() > 0 ){
			String[] Values = new String[ Nodes.size()];
			for( int i = Nodes.size(); --i >= 0;)
				Values[i] = Nodes.get( i).getChildNodes().item( 0).getNodeValue();
			return Values;
		}
		return null;
	}

	/**
	 * Gets the int value.
	 * 
	 * @param NodeC
	 *            the node c
	 * @param sName
	 *            the s name
	 * 
	 * @return the int value
	 */
	public int getIntValue( Node NodeC, String sName) {
		String sValue = getValue( NodeC, sName);
		try{
			if( sValue != null )
				return Integer.parseInt( sValue);
		}catch( NumberFormatException E ){
		}
		return 0;
	}

	/**
	 * Gets the long value.
	 * 
	 * @param NodeC
	 *            the node c
	 * @param sName
	 *            the s name
	 * 
	 * @return the long value
	 */
	public long getLongValue( Node NodeC, String sName) {
		String sValue = getValue( NodeC, sName);
		try{
			if( sValue != null )
				return Long.parseLong( sValue);
		}catch( NumberFormatException E ){
		}
		return 0;
	}

	/**
	 * Gets the double value.
	 * 
	 * @param NodeC
	 *            the node c
	 * @param sName
	 *            the s name
	 * 
	 * @return the double value
	 */
	public double getDoubleValue( Node NodeC, String sName) {
		String sValue = getValue( NodeC, sName);
		if( sValue != null )
			return Double.parseDouble( sValue);
		return 0;
	}

	/**
	 * Gets the coord value.
	 * 
	 * @param NodeC
	 *            the node c
	 * @param sName
	 *            the s name
	 * 
	 * @return the coord value
	 */
	public Point2D.Double getCoordValue( Node NodeC, String sName) {
		String sValue = getValue( NodeC, sName);
		if( sValue != null ){
			int i = sValue.indexOf( ',');
			if( i < 0 )
				i = sValue.indexOf( ' ');
			if( i > 0 ){
				double dX = Double.parseDouble( sValue.substring( 0, i));
				double dY = Double.parseDouble( sValue.substring( ++i));
				return new Point2D.Double( dX, dY);
			}
		}else{
			getElement( ( Element)NodeC, sName);
		}
		return null;
	}

	/**
	 * Gets the int value.
	 * 
	 * @param NodeC
	 *            the node c
	 * 
	 * @return the int value
	 */
	public int getIntValue( Node NodeC) {
		String sValue = getValue( NodeC);
		try{
			if( sValue != null )
				return Integer.parseInt( sValue);
		}catch( NumberFormatException E ){
		}
		return 0;
	}
	public double getDoubleValue( Node NodeC) {
		String sValue = getValue( NodeC);
		try{
			if( sValue != null )
				return Double.parseDouble( sValue);
		}catch( NumberFormatException E ){
		}
		return 0;
	}

	/**
	 * Checks if is node.
	 * 
	 * @param Root
	 *            the root
	 * @param sName
	 *            the s name
	 * 
	 * @return true, if is node
	 */
	public boolean isNode( Node Root, String sName) {
		return Root.getNodeName().equalsIgnoreCase( sName);
	}

	/**
	 * Adds the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 */
	public void addChildNode( Node NodeP, String sTag, String sValue[]) {
		Node Options = NodeP.appendChild( Doc.createElement( sTag));
		if( sValue != null )
			for( int i = 0; i < sValue.length; ++i){
				Node Option = Options.appendChild( Doc.createElement( "option"));
				addChildNode( Option, "value", sValue[i]);
				addChildNode( Option, "name", sValue[i]);
			}
	}

	/**
	 * Gets the file xsl.
	 * 
	 * @return the file xsl
	 */
	public String getFileXSL() {
		return sFileXSL;
	}

	/**
	 * Sets the file xsl.
	 * 
	 * @param sFileXSL
	 *            the new file xsl
	 */
	public void setFileXSL( String sPathXSL, String sFileXSL) {
		this.sFileXSL =  (sPathXSL != null)? sPathXSL + "/" + sFileXSL : sFileXSL;
	}
	public void setFileXSL( Sight aSight, String sFileXSL) {
		String sPathXSL = ( aSight != null)? aSight.getPathXSL(): null;
		this.sFileXSL =  ( sPathXSL != null)? sPathXSL + "/" + sFileXSL : sFileXSL;
	}

	/**
	 * Gets the element.
	 * 
	 * @param sName
	 *            the s name
	 * 
	 * @return the element
	 */
	public Element getElement( String sName) {
		Element Root = ( Element)getRootNode();
		iNode = 0;
		return findElement( Root, sName);
	}

	/**
	 * Gets the element.
	 * 
	 * @param Root
	 *            the root
	 * @param sName
	 *            the s name
	 * 
	 * @return the element
	 */
	public Element getElement( Element Root, String sName) {
		iNode = 0;
		return findElement( Root, sName);
	}
	public Element getElement( Node Root, String sName) {
		iNode = 0;
		return findElement( ( Element)Root, sName);
	}
	public Element getElement( Node aRoot, String sTagName, String sName) 
	{
		NodeList aList = (( Element)aRoot).getElementsByTagNameNS( sTagName, sName);
		if( aList != null && aList.getLength() > 0 ) return ( Element)aList.item( 0);
		return null;
	}
	/**
	 * Gets the next element.
	 * 
	 * @param Root
	 *            the root
	 * @param sName
	 *            the s name
	 * @param iNode
	 *            the i node
	 * 
	 * @return the next element
	 */
	public Element getNextElement( Element Root, String sName, int iNode) {
		this.iNode = iNode;
		return findElement( Root, sName);
	}
	/**
	 * Gets the all elements.
	 * 
	 * @param Root
	 *            the root
	 * @param sName
	 *            the s name
	 * 
	 * @return the all elements
	 */
	public NodeList getAllElements( Element Root, String sName) {
		return Root.getElementsByTagName( sName);
	}

	/**
	 * Gets the elements.
	 * 
	 * @param Root
	 *            the root
	 * @param sName
	 *            the s name
	 * 
	 * @return the elements
	 */
	public ArrayList<Element> getElements( Node Root, String sName) {
		ArrayList<Element> NNodes = null;
		if( Root != null ){
			NodeList Nodes = Root.getChildNodes();
			if( Nodes != null )
				for( int i = 0; i < Nodes.getLength(); ++i){
					if( Nodes.item( i) instanceof Element
							&& (( Element)Nodes.item( i)).getTagName().equals( sName) ){
						if( NNodes == null )
							NNodes = new ArrayList<Element>();
						NNodes.add( ( Element)Nodes.item( i));
					}
				}
		}
		return NNodes;
	}

	/**
	 * Sets the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param P
	 *            the p
	 */
	public Node setChildNode( Node NodeP, String sTag, Point2D P) {
		iNode = 0;
		Node aNode = findChildNode( NodeP, sTag);
		if( aNode == null )
			addChildNode( NodeP, sTag, P);
		else if( P != null ){
			setChildNodeValue( aNode, "x", Math.round( P.getX()));
			setChildNodeValue( aNode, "y", Math.round( P.getY()));
		}else
			removeNode( NodeP, aNode);
		return aNode;
	}

	/**
	 * Sets the child node.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param P
	 *            the p
	 */
	public Node setChildNode( Node NodeP, String sTag, Point P) {
		iNode = 0;
		Node aNode = findChildNode( NodeP, sTag);
		if( aNode == null )
			addChildNode( NodeP, sTag, P);
		else if( P != null ){
			setChildNodeValue( aNode, "ix", Math.round( P.x));
			setChildNodeValue( aNode, "iy", Math.round( P.y));
		}else
			removeNode( NodeP, aNode);
		return aNode;
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param sValue
	 *            the s value
	 */
	public Node setChildNodeValue( Node NodeP, String sTag, String sValue) {
		iNode = 0;
		Node aNode = findChildNode( NodeP, sTag);
		if( aNode != null && aNode.hasChildNodes() )
			aNode.getChildNodes().item( 0).setNodeValue( (sValue != null)? sValue : "");
		else{
			if( aNode == null )
				aNode = ( Element)NodeP.appendChild( Doc.createElement( sTag));
			aNode.appendChild( Doc.createTextNode( (sValue != null)? sValue : ""));
		}
		return aNode;
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iValue
	 *            the i value
	 */
	public Node setChildNodeValue( Node NodeP, String sTag, int iValue) {
		return setChildNodeValue( NodeP, sTag, Integer.toString( iValue));
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param lValue
	 *            the l value
	 */
	public Node setChildNodeValue( Node NodeP, String sTag, long lValue) {
		return setChildNodeValue( NodeP, sTag, Long.toString( lValue));
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param dValue
	 *            the d value
	 */
	public Node setChildNodeValue( Node NodeP, String sTag, double dValue) {
		return setChildNodeValue( NodeP, sTag, Double.toString( dValue));
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param bValue
	 *            the b value
	 */
	public Node  setChildNodeValue( Node NodeP, String sTag, boolean bValue) {
		return setChildNodeValue( NodeP, sTag, bValue? "true" : "false");
	}

	/**
	 * Sets the child node value.
	 * 
	 * @param NodeP
	 *            the node p
	 * @param sTag
	 *            the s tag
	 * @param iValue
	 *            the i value
	 * @param i0
	 *            the i0
	 * @param bHidden
	 *            the b hidden
	 */
	public Node setChildNodeValue( Node NodeP, String sTag, int iValue[], int i0, boolean bHidden) {
		iNode = 0;
		Element Node = findElement( ( Element)NodeP, sTag);
		if( Node != null )
			while( Node.hasChildNodes() )
				Node.removeChild( Node.getChildNodes().item( 0));
		else{
			Node = ( Element)NodeP.appendChild( Doc.createElement( sTag));
		}
		if( bHidden )
			addChildNode( Node, "HIDDEN", "true");
		Node Options = createChildNode( Node, "options");
		if( iValue != null )
			for( int i = i0; i < iValue.length; i += 2){
				Node Option = Options.appendChild( Doc.createElement( "option"));
				addChildNode( Option, "value", iValue[i]);
				addChildNode( Option, "name", iValue[i]);
			}
		return Node;
	}

	/**
	 * Gets the encoding.
	 * 
	 * @return the encoding
	 */
	public String getEncoding() {
		return (sEncoding != null)? sEncoding : sDefaultEncoding;
	}

	/**
	 * Gets the doc encoding.
	 * 
	 * @return the doc encoding
	 */
	public String getDocEncoding() {
		return (Doc != null)? Doc.getInputEncoding() : null;
	}

	/**
	 * Sets the default encoding.
	 * 
	 * @param outputEncoding
	 *            the new default encoding
	 */

	/**
	 * Sets the encoding.
	 * 
	 * @param outputEncoding
	 *            the new encoding
	 */
	public void setEncoding( String outputEncoding) {
		sEncoding = outputEncoding;
	}

	/*
	 * public void setContentType( String sContentType) { if( Doc != null )
	 * Doc.setTextContent( sContentType); }
	 */
	/**
	 * Checks if is hTML.
	 * 
	 * @return true, if is hTML
	 */
	public boolean isHTML() {
		return bHTML;
	}

	/**
	 * Sets the hTML.
	 * 
	 * @param bhtml
	 *            the new hTML
	 */
	public void setHTML( boolean bhtml) {
		bHTML = bhtml;
	}

	/**
	 * Gets the lang.
	 * 
	 * @return the lang
	 */
	public String getLang() {
		return sLang;
	}

	/**
	 * Sets the lang.
	 * 
	 * @param lang
	 *            the new lang
	 */
	public void setLang( String lang) {
		sLang = lang;
	}

	/**
	 * Checks if is lang.
	 * 
	 * @param sL
	 *            the s l
	 * 
	 * @return true, if is lang
	 */
	public boolean isLang( String sL) {
		return (sL != null)? sL.equalsIgnoreCase( "est") && sLang == null || sLang != null
				&& sLang.equalsIgnoreCase( sL) : sLang == null;
	}

	/** The map char2 html entity. */
	private static HashMap<Character,String>			mapChar2HTMLEntity = null;

	/** The Constant characters. */
	private final static char[]   characters = { 'œ', '„', '–', '•', '¼', '¤', '¶', 'µ' };

	/** The Constant entities. */
	private final static String[] entities		   = { "&Uuml;", "&Auml;", "&Ouml;", "&Otilde;",
			"&uuml;", "&auml;", "&ouml;", "&otilde;" };

	/*
	 * public HTMLEncoder() { }
	 */
	/**
	 * Encode.
	 * 
	 * @param s
	 *            the s
	 * 
	 * @return the string
	 */
	public String encode( String s) {
		if( mapChar2HTMLEntity == null ){
			mapChar2HTMLEntity = new HashMap<Character,String>();
			int longueur = characters.length;
			for( int i = 0; i < longueur; i++)
				mapChar2HTMLEntity.put( new Character( characters[i]), entities[i]);

		}
		int longueur = s.length();
		StringBuffer sb = new StringBuffer( longueur * 2);
		char ch;
		for( int i = 0; i < longueur; ++i){
			ch = s.charAt( i);
			if( (ch >= 63 && ch <= 90) || (ch >= 97 && ch <= 122) )
				sb.append( ch);
			else{
				String ss = ( String)mapChar2HTMLEntity.get( new Character( ch));
				if( ss == null )
					sb.append( ch);
				else
					sb.append( ss);
			}
		}
		return sb.toString();
	}

	/**
	 * Checks if is encoded.
	 * 
	 * @return true, if is encoded
	 */
	public boolean isEncoded() {
		return bEncoded;
	}

	/**
	 * Sets the encoded.
	 */
	public void setEncoded() {
		bEncoded = true;
	}

	/**
	 * Sets the excel.
	 * 
	 * @param bExcel
	 *            the new excel
	 */
/*	public void setExcel0( boolean bExcel) {
		this.bExcel = bExcel;
	}*/

	/**
	 * Gets the excel name.
	 * 
	 * @return the excel name
	 */

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return Doc == null;
	}

	/**
	 * Sets the nodes.
	 * 
	 * @param nodes
	 *            the new nodes
	 */
	public void setNodes( NodeList nodes) {
		Nodes = nodes;
	}

	/**
	 * Gets the nodes.
	 * 
	 * @return the nodes
	 */
	public NodeList getNodes() {
		return Nodes;
	}
	public int getChildNodesCount( Node NodeP, int iLevel) 
	{
		--iLevel;
		int iCount = 0;
		NodeList Nodes = NodeP.getChildNodes();
		for( int iNode = Nodes.getLength(); --iNode >= 0;){
			Node aNode = Nodes.item( iNode);
			int iType = aNode.getNodeType();
			if( iType == 1 ){
				if(  iLevel > 0 ) iCount += getChildNodesCount( aNode, iLevel);
				else if( iLevel < 0 ) ++iCount;
				else if( getChildNodesCount( aNode, 0) > 0 ) ++iCount;
			}
		}
		return iCount;
	}
	/**
	 * Sets the media type.
	 * 
	 * @param sMediaType
	 *            the new media type
	 */
/*	public void setMediaTypeX( String sMediaType) {
		this.sMediaType = sMediaType;
	}*/

	/**
	 * Gets the media type.
	 * 
	 * @return the media type
	 */
/*	public String getMediaTypeX() {
		return sMediaType;
	}*/
    public String getInnerText( Node aNode)
    {
	    String xmlString = "";
	    try {
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	
	        Source source = new DOMSource( aNode);
	
	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	
	        transformer.transform(source, result);
	        xmlString = sw.toString ();
	        xmlString = xmlString.replaceFirst( "<" + aNode.getNodeName() +">","");
	        xmlString = xmlString.replaceFirst( "</" + aNode.getNodeName() +">","");

	    } catch (Exception ex) {
	        ex.printStackTrace ();
	    }
	    return xmlString;
	}
    public class XslResolver implements URIResolver
    {
        public Source resolve( String fileName, String base) throws TransformerException
        {
            URL url = getClass().getClassLoader().getResource(fileName);
            StreamSource jarFileSS = new StreamSource();

            try
            {
                InputStream jarfileIS = url.openStream();
                jarFileSS.setInputStream(jarfileIS);
            }
            catch(IOException ioExp)
            {
                throw new TransformerException(ioExp);
            }
            return jarFileSS;
        }
    }
	public Node findNodeByAttr( Node aNodeP, String sName, String sAttr, String sValue) 
	{ 
		iNodeID = 0;
		ArrayList<Node> aNodes = findChildNodes( aNodeP, sName);
		if( aNodes != null ) for( Node aNode: aNodes){
			if( (( Element)aNode).getAttribute( sAttr).equals( sValue) ) return aNode;
			++iNodeID;
		}
		return null;
	}
	public void addChildNode( Node aRoot, String sTag, ArrayList<DataObject> List, String sValue, boolean bEmpty) {
		Node aNode = aRoot.appendChild( Doc.createElement( sTag));
		addChildNode( aNode, "value", sValue);
		Node Opts = createChildNode( aNode, "options");
		if( bEmpty ){
			Node Option = Opts.appendChild( Doc.createElement( "option"));
			if( sValue == null || sValue.length() == 0 )
				addChildNode( Option, "selected", "true");
		}
		if( List != null ) for( DataObject aDO: List){
			Node aOption = Opts.appendChild( Doc.createElement( "option"));
			String s = aDO.getValue();
			addChildNode( aOption, "value", s);
			addChildNode( aOption, "name", aDO.getName());
			if( sValue != null && sValue.equals( s) ) addChildNode( aOption, "selected", "true");
		}
	}
}



