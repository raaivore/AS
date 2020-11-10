package ee.or.is;
/**
 * The Class ExternalProcess
 * Created on 14.03.2012 by OR
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.w3c.dom.Node;

public class ExternalProcess 
{
	private Sight aSight = null;
	private ISServlet aServlet = null;
	private ExternalLog aErrStream = null;
	private ExternalLog aOutStream = null;
	private Process aProcess = null;
	private ThreadLog aThread = null;
	
	public ExternalProcess( Sight aSight) //ISServlet aServlet)
	{
		this.aSight = aSight;
		this.aServlet = aSight.getServlet();
	}
	public ExternalProcess( ISServlet aServlet, ThreadLog aThread)
	{
		this.aServlet = aServlet;
		this.aThread = aThread;
	}
	public ExternalProcess( ISServlet aServlet) 
	{
		this.aServlet = aServlet;
		this.aThread = aServlet.getThreadLog();
	}
	public int exec( String sCmd, File aWorkDir, int iTimeOut) // timeout in seconds
	{
		int iRet = -1;
		long lStartTime = GlobalDate.getCurrentDate().getTime();
		try {
			log( "external start " + sCmd);
			Runtime runtime = Runtime.getRuntime();
			aProcess = runtime.exec( sCmd, null, aWorkDir);
			if( aSight != null ){
				aErrStream = new ExternalLog( aSight, "err", aProcess.getErrorStream());
				aOutStream = new ExternalLog( aSight, "out", aProcess.getInputStream());
			}else{
				aErrStream = new ExternalLog( aServlet, aThread, aProcess.getErrorStream());
				aOutStream = new ExternalLog( aServlet, aThread, aProcess.getInputStream());
			}
			int iOldPriority = Thread.currentThread().getPriority();
			Thread.currentThread().setPriority( Thread.MIN_PRIORITY);
			for(;;){      
				Thread.sleep( 10 );
				if( isAlive( aProcess) ){
					if( ( GlobalDate.getCurrentDate().getTime() - lStartTime) / 1000 > iTimeOut){
						iRet = -2; // timeout end
						break;
					}
				}else{
					iRet = aProcess.exitValue();
					break;
				}
			}
			Thread.currentThread().setPriority( iOldPriority);
//			iRet = aProcess.waitFor();
		} catch( Exception aE) {
			log( aE);
			aSight.setError( aE);
			try{
				iRet = aProcess.exitValue();
			}catch( Exception aE1 ){
			}
		} finally {
			exit();
			log( "external end " + iRet + " in " + 
				(( GlobalDate.getCurrentDate().getTime() - lStartTime) / 1000) + " seconds");
		}
		return iRet;
	}
	public boolean isAlive( Process aProcess)
	{
		try{
			aProcess.exitValue();
			return false; 
		} catch( IllegalThreadStateException e) {
			return true;
		}
	} 
	public int exec( String [] sCmd, File aWorkDir)
	{
		int iRet = -1;
		try {
			log( "external start " + sCmd);
			Runtime runtime = Runtime.getRuntime();
			aProcess = runtime.exec( sCmd, null, aWorkDir);
			aErrStream = new ExternalLog( aServlet, "err", aProcess.getErrorStream());
			aErrStream.setLogCatName( aWorkDir);
			aOutStream = new ExternalLog( aServlet, "out", aProcess.getInputStream());
			aOutStream.setLogCatName( aWorkDir);
			iRet = aProcess.waitFor();
		} catch( Exception aE) {
			log( aE);
		} finally {
			exit();
			log( "external end " + iRet);
		}
		return iRet;
	}
	public void exit()
	{
		if( aProcess != null ) aProcess.destroy();
		aProcess = null;
		aOutStream = null;
		aErrStream = null;
	}
	public void setXML( DOMData aDoc, Node aRoot)
    {
		aOutStream.setXML( aDoc, aRoot);
		aErrStream.setXML( aDoc, aRoot);
    }
	private void log( String sMsg){ if( aSight != null) aSight.log( sMsg); else aServlet.log( sMsg);}
	private void log( Exception aE){ if( aSight != null) aSight.log( aE); else aServlet.log( aE);}
	
	public File unpackZip( File aFile){
		String sPath = aFile.getPath();
		int i = sPath.indexOf( '.');
		String sCatName = sPath.substring( 0, i);
		File aCat = new File( sCatName);
		aCat.mkdir();
		try{
			unpackZip( aCat, new FileInputStream( aFile));
			File [] aFiles = aCat.listFiles();
			if( aFiles != null && aFiles.length == 1 && aFiles[ 0].isDirectory() ) return aFiles[ 0];
			return aCat;
		}catch( FileNotFoundException aE ){
			log( aE);
		}
		return null;
	}
	private void unpackZip( File dir, InputStream instream){

		if ( instream == null)
			throw new IllegalArgumentException( "instream ei tohi olla null");

		log( "unpackZip " + dir.getName());
/*		if ( dir.exists() )
			throw new UserError( "dir.already.exist",
				"Antud nimega ? kataloog on juba olemas", dir.getAbsolutePath(),null); */

		// Käime ykshaaval läbi kõik streamis olevad elemendid - nii failid kui ka kataloogid.
		try {
			ZipEntry Entry = null;
			ZipInputStream ZipStream = new ZipInputStream( new BufferedInputStream( instream));
			
			while ( (Entry = ZipStream.getNextEntry()) != null ) {			
				writeZipEntry( dir, ZipStream, Entry);
			}
		} catch( Exception e ){
			log( e);
		}
	}
	private void writeZipEntry( File dir, ZipInputStream zipstream, ZipEntry entry) throws Exception
	{

		long onefilesize;
		String filename = entry.getName();
		log( "Zip entry " + filename );
//		filename = Utils.strippath( filename);
		File outfile = new File( dir, filename );

		// Kas tegemist on kataloogiga? Kui jaa siis loome vastava kataloogi.
		if ( entry.isDirectory() ) {
			if ( !outfile.exists() )
				outfile.mkdirs();
		} else {
			// Tegemist on failiga, salvestame selle.
			onefilesize = (int) entry.getSize();
			if ( onefilesize <= 0) {
			}
			int bufsize = 256*1024;
			FileOutputStream out = null;
			try{
				out = new FileOutputStream( outfile);
				byte[] datas = new byte[bufsize];
				int len = 0;
				while ((len=zipstream.read(datas, 0, bufsize)) != -1)
					out.write(datas, 0, len);
				out.flush();
				out.close();
			}finally {
				try {
					if( out != null ) out.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
