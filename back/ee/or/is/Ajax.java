package ee.or.is;

import java.io.DataInputStream;
// import java.io.File;
// import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Ajax 
{
    private HttpURLConnection urlConn = null;
    
	public Ajax()
	{
		
	}
	public void exit()
	{
		if( urlConn != null ) urlConn.disconnect();
	}
	public DOMData makeHttpRequestDOM( String sURL, String sMsg, String sEncoding) 
	{
	    return makeHttpRequest( sURL, sMsg, sEncoding, true, null);
	}
	public DOMData makeHttpRequest( String sURL, String sMsg, String sEncoding, boolean bGet, Authenticator Auth) 
	{
		return ( DOMData)makeHttpRequest( sURL, sMsg, sEncoding, bGet, Auth, true);
	}
	public Object makeHttpRequest( String sURL, String sMsg, String sEncoding, boolean bGet, Authenticator Auth, boolean bDOM) 
	{
		return makeHttpRequest( sURL, (sMsg != null)? sMsg.getBytes(): null, sEncoding, bGet, Auth, bDOM);
	}
	public Object makeHttpRequest( String sURL, byte [] aBytes, String sEncoding, boolean bGet, Authenticator Auth, boolean bDOM) 
	{
	    URL conURL = null;
	    OutputStream os = null;
   		try {
 			String sFullURL = sURL.replaceAll( " ", "%20");
 			Log.info( 95, sFullURL);
   			if( Auth != null ) Authenticator.setDefault( Auth);
   			conURL = new URL( sFullURL);
   			if( sURL.indexOf( "https") == 0) urlConn = ( HttpsURLConnection)conURL.openConnection();
   			else urlConn = ( HttpURLConnection)conURL.openConnection();
   			urlConn.setReadTimeout( 30000);
   			if( aBytes != null ){
       			urlConn.setRequestMethod( "POST");
     			urlConn.setRequestProperty( "Content-Type", "text/xml; charset=" + 
     				(( sEncoding != null)? sEncoding: "ISO-8859-1"));
   				urlConn.setDoOutput( true);
   				urlConn.connect();
   				os = urlConn.getOutputStream();
   				os.write( aBytes);
   			}else{
   			    urlConn.setRequestMethod( "GET");
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
	   			    		try {
								if( In != null ) return new DOMData( In, encoding);
							} catch (Exception e) {
// nii see ei tööta ja tuleks teha kuidagi kavalamalt
// eesmärk on saada ikkagi xml kätte ja enne parsimist see parandada kui on vigu
// võib-olla soduda see 98, kus asjad kõigepealt loetakse ja siis alles parsitakse
/*								In.reset();
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
							    }*/
							}
	  			    		return null; // new DOMData();
	   			    	}else{
//	   			    		int i = 0;
	   			    		DataInputStream is = new DataInputStream( In);
	   			    		int ch;
	   			    		StringBuffer sb = new StringBuffer();
	   			    		while ((ch = is.read()) != -1){	sb.append( (char)ch); } //++i;
	   			    		return sb.toString();
	   			    	}
  			    	} catch ( Exception aE){
   			    		Log.info( 95, "Exception: " + sURL);
   			    		Log.info( 95, aE.getMessage());
   			    	}finally{
   			    		if( In != null ) In.close();
   			    	}
   			    }else return null;
   			}
   			Log.info( 98, sURL);
   			Log.info( 98, "Http connection ret="+iRes);
   		} catch (Exception e) {
   			Log.info( 95, sURL);
   		    Log.info( 95, "Exception : " + e.getMessage());
   		}finally{
			try {
				if( os != null ) os.close();
			} catch (IOException e) {
	  		    Log.info("Exception : " + e.getMessage());
	  		}
   		    if( urlConn != null ) urlConn.disconnect();
   		    urlConn = null;
   		}
   		return null;
   	}


}
