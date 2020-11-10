/*
 * 
 */
package ee.or.is;

import java.io.*;
import java.net.*;

import javax.net.ssl.*;
/**
 * The Class MessageToServlet.
 * 
 * @author Administrator
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MessageToServlet 
{
	
	/**
	 * Make http request.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * 
	 * @return true, if successful
	 */
	public static boolean makeHttpRequest( String sURL, String sMsg) 
	{
	    return makeHttpRequest( sURL, sMsg, false, null) != null;
	}
	public static DOMData makeHttpRequest( String sURL, DOMData Doc) throws Exception
	{
	    return makeHttpRequest( sURL, Doc, null, null);
	}
	public static DOMData makeHttpRequest( String sURL, DOMData Doc, BasicAuthenticator aAuth, String sContentType) throws Exception
	{
		if( Doc == null ) return makeHttpRequest( sURL, null, true, aAuth);
		ByteArrayOutputStream os = new ByteArrayOutputStream(); // 
		Doc.write( os);
	    return ( DOMData)getHttpRequest( sURL, os.toByteArray(), Doc.getEncoding(), true, aAuth, true, sContentType);
	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * 
	 * @return the http request
	 */
	public static String getHttpRequest( String sURL, String sMsg) 
	{
	    return getHttpRequest( sURL, sMsg, null);
	}
	
	/**
	 * Gets the http request dom.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * 
	 * @return the http request dom
	 */
	public static DOMData getHttpRequestDOM( String sURL, String sMsg) 
	{
	    return makeHttpRequest( sURL, sMsg, true, null);
	}
	
	/**
	 * Gets the http request dom.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * @param sUserName the s user name
	 * @param sPSW the s psw
	 * 
	 * @return the http request dom
	 */
	public static DOMData getHttpRequestDOM( String sURL, String sMsg, String sUserName, String sPSW) 
	{
	    BasicAuthenticator Auth = null;
	    if( sUserName != null ) Auth = new BasicAuthenticator(  sUserName, sPSW);
	    return makeHttpRequest( sURL, sMsg, true, Auth);
	}
	
	/**
	 * Make http request.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * @param bGet the b get
	 * @param Auth the auth
	 * 
	 * @return the dOM data
	 */
	public static DOMData makeHttpRequest( String sURL, String sMsg, boolean bGet, Authenticator Auth) 
	{
		return ( DOMData)getHttpRequest( sURL, sMsg, bGet, Auth, true);
	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * @param Auth the auth
	 * 
	 * @return the http request
	 */
	public static String getHttpRequest( String sURL, String sMsg, Authenticator Auth) 
	{
		return ( String)getHttpRequest( sURL, sMsg, true, Auth, false);
	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param sMsg the s msg
	 * @param bGet the b get
	 * @param Auth the auth
	 * @param bDOM the b dom
	 * 
	 * @return the http request
	 */
	public static Object getHttpRequest( String sURL, String sMsg, boolean bGet, Authenticator Auth, boolean bDOM) 
	{
		return getHttpRequest( sURL, (sMsg != null)? sMsg.getBytes(): null, null, bGet, Auth, bDOM, null);
	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param aBytes the a bytes
	 * @param bGet the b get
	 * @param Auth the auth
	 * @param bDOM the b dom
	 * 
	 * @return the http request
	 */
	public static Object getHttpRequest( String sURL, byte [] aBytes, String sEncoding, boolean bGet, Authenticator aAuth, boolean bDOM, String sContentType) 
	{
		if( sContentType == null ) sContentType = "text/xml";
	    URL conURL = null;
	    OutputStream os = null;
	    HttpURLConnection urlConn = null;
   		try {
 			String sFullURL = sURL.replaceAll( " ", "%20");
 			Log.info( 95, sFullURL);
   			if( aAuth != null ) Authenticator.setDefault( aAuth);
   			conURL = new URL( sFullURL);
   			if( sURL.indexOf( "https") == 0) urlConn = ( HttpsURLConnection)conURL.openConnection();
   			else urlConn = ( HttpURLConnection)conURL.openConnection();
   			urlConn.setReadTimeout( 300000);
   			if( aBytes != null ){
       			urlConn.setRequestMethod( "POST");
     			urlConn.setRequestProperty( "Content-Type", sContentType + "; charset=" + 
     				(( sEncoding != null)? sEncoding: "ISO-8859-1"));
     			if( aAuth != null && aAuth instanceof BasicAuthenticator ){
     				urlConn.setRequestProperty( "Cookie", "JSESSIONID=" + ((BasicAuthenticator)aAuth).getSessionId());
     			}
   				urlConn.setDoOutput( true);
   				urlConn.connect();
   				os = urlConn.getOutputStream();
   				os.write( aBytes);
   			}else{
   			    urlConn.setRequestMethod( "GET");
// ??? Siin tuleks midagi v�lja m�elda, et kust see nimi v�iks tulla ???
// probleem oli ilmajaamaga, kes seda n�udis  
   			    
   			    urlConn.setRequestProperty("User-Agent", "Tallinn");
//     			urlConn.setRequestProperty( "Content-Type", "text/xml; charset=ISO-8859-1");
   				urlConn.connect();
   			}
   			int iRes = urlConn.getResponseCode();
   			if( iRes == HttpURLConnection.HTTP_ACCEPTED || iRes == HttpURLConnection.HTTP_OK ){
   			    if( bGet ){
   			    	InputStream In = null;
			    	try {
			    		In = urlConn.getInputStream();
	  			    	if( bDOM ){
	  			    	    String contentType = urlConn.getContentType();
	  			    	    String encoding = null;
	  			    	    if( contentType != null ){
	  			    	    	if( Log.isDebug( 98) ) Log.info( "content: " + contentType);
		  			    	    int encodingStart = contentType.indexOf("charset=");
		  			    	    if( encodingStart > 0 ) {
		  			    	    	encoding = contentType.substring(encodingStart + 8);
		  			    	    	encoding = encoding.replaceAll( "\"", "");
/*		  			    	    }else{
		  			    	    	encodingStart = contentType.indexOf("encoding=");
		  			    	    	encoding = contentType.substring( encodingStart + 9);*/
		  			    	    }
	  			    	    }else{
	  			    	    	encoding = DOMData.getDefaultEncoding(); //"ISO-8859-1";
	  			    	    }
	  			    	    if( Log.isDebug( 98) ) Log.info( "encoding: " + encoding);
								if( In != null ) return new DOMData( In, encoding);
/*
								try {
							} catch (Exception e) {
// nii see ei t��ta ja tuleks teha kuidagi kavalamalt
// eesm�rk on saada ikkagi xml k�tte ja enne parsimist see parandada kui on vigu
// v�ib-olla soduda see 98, kus asjad k�igepealt loetakse ja siis alles parsitakse
								In.reset();
								FileOutputStream aOut = null;
								try {
									aOut = new FileOutputStream( new File( ISServlet.getRealPath( "parse_error.txt")));
							 
									int read = 0;
									byte[] bytes = new byte[1024];
							 
									while ((read = In.read(bytes)) != -1) {
										aOut.write(bytes, 0, read);
									}
								} catch (IOException aE) {
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
	  			    		return new DOMData();*/
	   			    	}else{
//	   			    		int i = 0;
	   			    		DataInputStream is = new DataInputStream( In);
	   			    		int ch;
	   			    		StringBuffer sb = new StringBuffer();
	   			    		while ((ch = is.read()) != -1){	sb.append( (char)ch); } //++i;
	   			    		return sb.toString();
	   			    		
 /*  			    			if( In != null ){
   			    				StringBuffer Buf = new StringBuffer();
   			    				for(;;){
   			    					int i = ( byte)In.read();
   			    					if( i < 0 ) break;
   			    					Buf.append( ( char)i);
   			    				}
   			    				String sBuf = Buf.toString();
   			    				Log.info( 95, sBuf);
   			    				return sBuf;
   			    			}*/
	   			    	}
//  			    	} catch (MException e) {
//   			    		Log.info( "MException: " + sURL);
  			    	} catch ( Exception aE){
   			    		Log.info( "Exception: " + sURL);
   			    		Log.info( aE);
   			    	}finally{
   			    		if( In != null ) In.close();
   			    	}
   			    }else return null; 
   			}
//   			Log.info( 95, sURL);
   			Log.info( 95, "Http connection ret="+iRes);
   		} catch (Exception aE) {
   			if( !Log.isDebug( 95) ) Log.info( sURL);
   		    Log.info( aE);
   		}finally{
			try {
				if( os != null ) os.close();
			} catch (IOException e) {
	  		    Log.info("Exception : " + e.getMessage());
	  		}
   		    if( urlConn != null ) urlConn.disconnect();
   		}
   		return null;
   	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param Doc the doc
	 * @param Auth the auth
	 * 
	 * @return the http request
	 */
	public static String getHttpRequest( String sURL, DOMData Doc, Authenticator Auth) 
	{
		if( Doc == null ) return null;
	    URL conURL = null;
	    HttpURLConnection urlConn = null;
   		try {
   			ByteArrayOutputStream os = new ByteArrayOutputStream(); // 
   			Doc.write( os);
   			Authenticator.setDefault( Auth);
  			String sFullURL = sURL.replaceAll( " ", "%20");
   	 		Log.info( 95, sFullURL);   			
   			conURL = new URL( sFullURL);
   			urlConn = ( HttpURLConnection)conURL.openConnection();
   			urlConn.setReadTimeout( 30000);
   			urlConn.setRequestMethod( "POST");
 			urlConn.setRequestProperty( "Content-Type", "text/xml; charset=" + Doc.getEncoding());
 			urlConn.setRequestProperty( "Content-Length", Integer.toString( os.size()));   				
			urlConn.setDoOutput( true);
			urlConn.setDoInput( true); 
			urlConn.setUseCaches( false);
			urlConn.setFixedLengthStreamingMode( os.size());
			OutputStream cos = urlConn.getOutputStream();
			cos.write( os.toByteArray());
			urlConn.connect();
			os.flush();
			os.close();
			if( Log.isDebug( 98) ) Doc.writeFile( ISServlet.getRealPath( "sended.xml"));
   			int iRes = urlConn.getResponseCode();
   			if( iRes == HttpURLConnection.HTTP_ACCEPTED || iRes == HttpURLConnection.HTTP_OK ){
   				InputStream In = urlConn.getInputStream();
   				if( Doc.isLang( "rus") || Doc.getEncoding().equalsIgnoreCase( "UTF-8")){
   	   				int b;
   	   				ByteArrayOutputStream baos = new ByteArrayOutputStream();

   	   				while( (b=In.read())!=-1 ) {
   	   				   baos.write( b );
   	   				}
   					return baos.toString( "UTF-8");
   				}else{
   	  	   		    StringBuffer Buf = new StringBuffer();
   	  	   		    for(;;){
   	  	   		    	int i = ( byte)In.read();
   	  	   		    	if( i < 0 ) break;
   	  	   		    	Buf.append( ( char)i);
   	  	   		    }
   	   		        return Buf.toString();
   				}
/*   				InputStreamReader RIn = new InputStreamReader( In, "ISO-8859-1");
   	   		    StringBuffer Buf = new StringBuffer();
   	   		    while( RIn.ready() ){
   	   		    	Buf.append( RIn.read());
   	   		    }*/
/*   	   		    for(;;){
   	   		        int i = ( byte)In.read();
   	   		        if( i < 0 ) break;
   	   		        Buf.append( ( char)i);
   	   		    } */
//   	   		    return Buf.toString();
   			}
   			Log.info( "Http connection ret="+iRes);
   		} catch (java.net.ConnectException e) {
   			Log.info( "Connection error : " + e.getMessage());
   		} catch (MalformedURLException k) {
   		    Log.info( "Bad formed URL of WMS server : " + k.getMessage());
   		} catch (IOException e) {
   		    Log.info( "IOException : " + e.getMessage());
   		} catch (Exception e) {
   		    Log.info( "Exception : " + e.getMessage());
   		}finally{
   		    if( urlConn != null ) urlConn.disconnect();
   		}
   		return null;
   	}
	
	/**
	 * Send message.
	 * 
	 * @param sURL the s url
	 * @param sName the s name
	 * @param sPSW the s psw
	 * @param sMsg the s msg
	 * @param bRet the b ret
	 * 
	 * @return the dOM data
	 */
	public static DOMData sendMessage( String sURL, String sName, String sPSW, String sMsg, boolean bRet)
	{
	    return makeHttpRequest( sURL, sMsg, bRet, new BasicAuthenticator(  sName, sPSW));
	}
	
	/**
	 * Send message.
	 * 
	 * @param sURL the s url
	 * @param sName the s name
	 * @param sPSW the s psw
	 * @param sMsg the s msg
	 * 
	 * @return true, if successful
	 */
	public static boolean sendMessage( String sURL, String sName, String sPSW, String sMsg)
	{
	    return makeHttpRequest( sURL, sMsg, false, new BasicAuthenticator(  sName, sPSW)) != null;
	}
/*	public static void test()
	{
		 sendMessage( "http://localhost:8080/Tulika/Test", "Hello World");
		 sendMessage( "http://localhost:8080/Tulika/Test?GetPath", null);
	} */
	/**
 * Gets the http request.
 * 
 * @param sURL the s url
 * @param sMsg the s msg
 * @param sName the s name
 * @param sPSW the s psw
 * 
 * @return the http request
 */
public static String getHttpRequest( String sURL, String sMsg, String sName, String sPSW)
	{
	    return getHttpRequest( sURL, sMsg, new BasicAuthenticator(  sName, sPSW));
	}
	
	/**
	 * Gets the http request.
	 * 
	 * @param sURL the s url
	 * @param Doc the doc
	 * @param sName the s name
	 * @param sPSW the s psw
	 * 
	 * @return the http request
	 * @throws Exception 
	 */
	public static String getHttpRequest( String sURL, DOMData Doc, String sName, String sPSW) throws Exception
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(); // 
		Doc.write( os);
	    getHttpRequest( sURL, os.toByteArray(), Doc.getEncoding(), false, new BasicAuthenticator(  sName, sPSW), false, null);
		return null;
//	    return getHttpRequest( sURL, Doc, new BasicAuthenticator(  sName, sPSW));
	}
	
	/**
	 * Gets the http request old.
	 * 
	 * @param sURL the s url
	 * @param Doc the doc
	 * @param sName the s name
	 * @param sPSW the s psw
	 * 
	 * @return the http request old
	 */
	public static String getHttpRequestOld( String sURL, DOMData Doc, String sName, String sPSW)
	{
	    return getHttpRequest( sURL, Doc, new BasicAuthenticator(  sName, sPSW));
	}
}

