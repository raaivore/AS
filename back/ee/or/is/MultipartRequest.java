package ee.or.is;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultipartRequest implements HttpServletRequest 
{
	private HttpServletRequest 	aRequest = null;
	private ServletFileUpload 	upload = null;
	private List<FileItemIterator> 	items  = null;
//	private int	   				iMaxSize = 0;			// max info suurus mida vastu v�tame
	private String	   			sTempPath = null;
	
	public MultipartRequest( HttpServletRequest aRequest, int iMaxSize, String sTempPath) throws Exception
	{
		super();
		this.aRequest = aRequest;
		this.sTempPath = sTempPath;
		parse();
	}
	@SuppressWarnings("unchecked")
	private void parse() throws Exception 
	{
		if( aRequest.getCharacterEncoding() == null ){
			try {
				aRequest.setCharacterEncoding( DOMData.getDefaultEncoding());
			} catch (UnsupportedEncodingException e) {
				Log.error( e);
			}
		}
//		String encoding = aRequest.getCharacterEncoding(); 
//		aRequest.setCharacterEncoding( "utf-8");
		DiskFileItemFactory factory = new DiskFileItemFactory();
//		factory.setSizeThreshold( 1000000);
//		long l = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD; //.DEFAULT_SIZE_THRESHOLD;
		factory.setRepository( new File( sTempPath));
		upload = new ServletFileUpload( factory);
//		Log.log( "Limits file size max and size max " + upload.getFileSizeMax() + " " + upload.getSizeMax());
//		upload.setSizeMax( 10*1024*1024*1024); // iMaxSize);
		upload.setHeaderEncoding( DOMData.getDefaultEncoding());
//		items = ( List<FileItemIterator>)upload.parseRequest( aRequest);
	}
	
	public String getParameter( String sParamName)
	{		
		String sParamValue = null; // aRequest.getParameter( sParamName);
//		if ( sParamValue == null){
		    Iterator<FileItemIterator> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
		        if (item.isFormField()) {				// fail meid siin ei huvita ...
					String s = item.getFieldName();	// n�iteks acro v�i nextacro ,... v�i muu 
					if( s != null && s.equalsIgnoreCase( sParamName) ) {
						try{
							sParamValue = new String ( item.get(), DOMData.getDefaultEncoding());;
						}catch( UnsupportedEncodingException aE ){
							
							Log.error( aE);
						}
					/*	if ( sParamValue.length() > 10000 ) {
							String msg = "Pikkus yle 10000, tagastame vaid esimesed 10000";
							Log.log( msg);
							sParamValue = sParamValue.substring( 0, 10000);
						}*/
					}
				}
			}
//		}
		return sParamValue;
	}
	public String[] getParameterValues( String sParamName)
	{		
		ArrayList<String> aParamValues = new ArrayList<String>();
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
	        if (item.isFormField()) {				// fail meid siin ei huvita ...
				String s = item.getFieldName();	// n�iteks acro v�i nextacro ,... v�i muu 
				if( s != null && s.equalsIgnoreCase( sParamName) ) {
					aParamValues.add( item.getString());
				}
			}
		}
		int n = aParamValues.size();
		if( n > 0 ){
			String [] sParamValues = new String [ n];
			for( int i = n; --i >= 0; ) sParamValues[ i] = aParamValues.get( i);
			return  sParamValues;
		}
		return null;
	}

	public ServletInputStream getInputStream() throws IOException 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
	        if( !item.isFormField()){	
				return ( ServletInputStream)item.getInputStream();
			}
		}
		return aRequest.getInputStream();
	}
	public InputStream getInputStream2() throws IOException 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
	        if( !item.isFormField()){	
				return item.getInputStream();
			}
		}
		return aRequest.getInputStream();
	}
	public FileItem getFileItem() throws IOException 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = ( FileItem) iter.next();
	        if( !item.isFormField()) return item;
		}
		return null;
	}
	public FileItem getFileItem(  String sName) throws IOException 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = ( FileItem) iter.next();
	        if( !item.isFormField() && ( sName == null || item.getFieldName().equalsIgnoreCase( sName)) ) return item;
		}
		return null;
	}
	public File getFile( File aDir) throws Exception 
	{	
		return getFile( aDir, false);
	}
	public File getFile( File aDir, boolean bExists) throws Exception 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = ( FileItem) iter.next();
	        if( !item.isFormField()){
	        	String sName = item.getName();
	        	int i = sName.lastIndexOf( '/');
	        	if( i < 0 ) i = sName.lastIndexOf( '\\');
	        	File aOutFile = new File( aDir, sName.substring( ++i));
	        	if( bExists && aOutFile.exists() ) return null;
	        	
	        	item.write( aOutFile);
				Log.log( "download end " + sName + " size = " + item.getSize());        	
	        	return aOutFile;
	        }
		}
		return null;
	}
	public String getFileName() throws Exception 
	{	
	    Iterator<FileItemIterator> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = ( FileItem) iter.next();
	        if( !item.isFormField()){
	        	String sName = item.getName();
	        	int i = sName.lastIndexOf( '/');
	        	if( i < 0 ) i = sName.lastIndexOf( '\\');
	        	return sName.substring( ++i);
	        }
		}
		return null;
	}
	public byte[] getBinaryData() throws MException 
	{
		byte[]	data = null;
	    Iterator<FileItemIterator> iter = items.iterator();
	    while (iter.hasNext()) {
	        FileItem item = (FileItem) iter.next();
	
	        if( !item.isFormField()) {
//				String contenttype = item.getContentType();
				try {
					InputStream instream = item.getInputStream();
					int len = (int) item.getSize();
					if( len > 0 ){
						data = new byte[ len];
						instream.read( data, 0, len);
					}
				} catch (IOException e) {
					String msg = "Viga IputStream-i kysimisel ja/v�i andmete lugemisel";
					Log.log( msg + "\n" + e.getMessage() );
					throw new MException( msg, e);
				}
	        }	    
		}
		return data;
	}
	public byte[] getBinaryData( String sName) throws MException 
	{
		byte[]	data = null;
	    Iterator<FileItemIterator> iter = items.iterator();
	    while (iter.hasNext()) {
	        FileItem item = (FileItem) iter.next();
	
	        if( !item.isFormField() && ( sName == null || item.getFieldName().equalsIgnoreCase( sName)) ){
//				String contenttype = item.getContentType();
				try {
					InputStream instream = item.getInputStream();
					int len = (int) item.getSize();
					if( len > 0 ){
						data = new byte[ len];
						instream.read( data, 0, len);
					}
				} catch (IOException e) {
					String msg = "Viga IputStream-i kysimisel ja/v�i andmete lugemisel";
					Log.log( msg + "\n" + e.getMessage() );
					throw new MException( msg, e);
				}
	        }	    
		}
		return data;
	}

	public String getAuthType() { 					return aRequest.getAuthType();}
	public String getContextPath() { 				return aRequest.getContextPath();}
	public Cookie[] getCookies() { 					return aRequest.getCookies();}
	public long getDateHeader( String arg0) { 		return aRequest.getDateHeader( arg0);}
	public String getHeader( String arg0) {			return aRequest.getHeader( arg0);}
	public Enumeration<String> getHeaderNames() {			return aRequest.getHeaderNames();}
	public Enumeration<String> getHeaders( String arg0) {	return aRequest.getHeaders( arg0);}
	public int getIntHeader( String arg0) {			return aRequest.getIntHeader( arg0);}
	public String getMethod() {						return aRequest.getMethod();}
	public String getPathInfo() {					return aRequest.getPathInfo();}
	public String getPathTranslated() {				return aRequest.getPathTranslated();}
	public String getQueryString() {				return aRequest.getQueryString();}
	public String getRemoteUser() {					return aRequest.getRemoteUser();}
	public String getRequestURI() {					return aRequest.getRequestURI();}
	public StringBuffer getRequestURL() {			return aRequest.getRequestURL();}
	public String getRequestedSessionId() {			return aRequest.getRequestedSessionId();}
	public String getServletPath() {				return aRequest.getServletPath();}
	public HttpSession getSession() {				return aRequest.getSession();}
	public HttpSession getSession( boolean arg0) {	return aRequest.getSession( arg0);}
	public Principal getUserPrincipal() {			return aRequest.getUserPrincipal();}
	public boolean isRequestedSessionIdFromCookie(){return aRequest.isRequestedSessionIdFromCookie();}
	public boolean isRequestedSessionIdFromURL() {	return aRequest.isRequestedSessionIdFromURL();}
	public boolean isRequestedSessionIdFromUrl() {	return false;}
	public boolean isRequestedSessionIdValid() {	return aRequest.isRequestedSessionIdValid();}
	public boolean isUserInRole( String arg0) {		return aRequest.isUserInRole( arg0);}
	public Object getAttribute( String arg0) {		return aRequest.getAttribute( arg0);}
	public Enumeration<String> getAttributeNames() {		return aRequest.getAttributeNames();}
	public String getCharacterEncoding() {			return aRequest.getCharacterEncoding();}
	public int getContentLength() {					return aRequest.getContentLength();}
	public String getContentType() {				return aRequest.getContentType();}
	public String getLocalAddr() {					return aRequest.getLocalAddr();}
	public String getLocalName() {					return aRequest.getLocalName();}
	public int getLocalPort() {						return aRequest.getLocalPort();}
	public Locale getLocale() {						return aRequest.getLocale();}
	public Enumeration<Locale> getLocales() {		return aRequest.getLocales();}
	public Map<String,String[]> getParameterMap() {					return aRequest.getParameterMap();}
	public Enumeration<String> getParameterNames() {		return aRequest.getParameterNames();}
	public String getProtocol() {					return aRequest.getProtocol();}
	public BufferedReader getReader() throws IOException {	return aRequest.getReader();}
	public String getRealPath( String arg0) {		return null;}
	public String getRemoteAddr() {					return aRequest.getRemoteAddr();}
	public String getRemoteHost() {					return aRequest.getRemoteHost();}
	public int getRemotePort() {					return aRequest.getRemotePort();}
	public RequestDispatcher getRequestDispatcher( String arg0) {	return aRequest.getRequestDispatcher( arg0);}
	public String getScheme() {						return aRequest.getScheme();}
	public String getServerName() {					return aRequest.getServerName();}
	public int getServerPort() {					return aRequest.getServerPort();}
	public boolean isSecure() {						return aRequest.isSecure();}
	public void removeAttribute( String arg0) {		aRequest.removeAttribute( arg0);}
	public void setAttribute( String arg0, Object arg1) { aRequest.setAttribute( arg0, arg1);}
	public void setCharacterEncoding( String arg0) throws UnsupportedEncodingException { aRequest.setCharacterEncoding( arg0);}
	@Override
	public AsyncContext getAsyncContext() {
		return aRequest.getAsyncContext();
	}
	@Override
	public DispatcherType getDispatcherType() {
		
		return aRequest.getDispatcherType();
	}
	@Override
	public ServletContext getServletContext() {
		return aRequest.getServletContext();	
	}
	@Override
	public boolean isAsyncStarted() {
		return aRequest.isAsyncStarted();
	}
	@Override
	public boolean isAsyncSupported() {
		return aRequest.isAsyncSupported();
	}
	@Override
	public AsyncContext startAsync() {
		return aRequest.startAsync();
	}
	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		return aRequest.startAsync();
	}
	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		return aRequest.authenticate(arg0);
	}
	@Override
	public Part getPart(String arg0) throws IOException, IllegalStateException,
			ServletException {
		return aRequest.getPart(arg0);
	}
	@Override
	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		return aRequest.getParts();
	}
	@Override
	public void login(String arg0, String arg1) throws ServletException {
		aRequest.login(arg0, arg1);
	}
	@Override
	public void logout() throws ServletException {
		aRequest.logout();
	}
	@Override
	public long getContentLengthLong() {
		return aRequest.getContentLengthLong();
	}
	@Override
	public String changeSessionId() {
		return aRequest.changeSessionId();
	}
	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0)
			throws IOException, ServletException {
		return aRequest.upgrade(arg0);
	}
}
