package ee.or.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;

public abstract class GlobalFile {

    public static File createCatalog( String sCatName)
    {
    	File aCat = null;
		if( sCatName != null ){
			aCat = new File( sCatName);
			if( !aCat.exists() ){
				int i = sCatName.lastIndexOf( "/");
				if( i > 0 ){
					File aMainCat = createCatalog( sCatName.substring( 0, i)); 
					if( aMainCat != null ){
						aCat = new File( aMainCat, sCatName.substring( ++i));
						aCat.mkdir();
					}
				}
			}
			if( !( aCat != null && aCat.exists() && aCat.isDirectory())){
				aCat = null;
			}
		}
    	return aCat;
    }
    public static File createCatalog( String sCatName, String sSubCatName)
    {
    	return createCatalog( sCatName + "/" + sSubCatName);
    }
    public static boolean renameFile( File aCat, String sName, String sNewName)
    {
    	if( aCat != null && aCat.isDirectory() ){
        	File aFile = new File( aCat, sName);
        	File aNewFile = new File( aCat, sNewName);
        	if( aFile != null && aNewFile != null )	return aFile.renameTo( aNewFile);
    	}
    	return false;
    }
	public static void setFile( InputStream in, File outFile) throws Exception 
	{
		OutputStream out=new FileOutputStream( outFile);
		byte buf[]=new byte[1024];
		int len;
		while( (len=in.read(buf))>0 ) out.write(buf,0,len);
		out.close();
		in.close();
	}
	public static void copyFile( String sFileIn, String sFileOut) throws ExceptionIS
	{
		copyFile( new File( sFileIn), new File( sFileOut));
	}
	public static void copyFile( File inFile, File outFile) throws ExceptionIS 
	{
		try {
			copyFile(  new FileInputStream( inFile), outFile);
		} catch(FileNotFoundException aE) {
			throw new ExceptionIS( aE);
		}
	}
	public static void copyFile( FileInputStream in, File outFile) throws ExceptionIS 
	{
		try {
			FileChannel sourceChannel = in.getChannel();
			FileOutputStream out = new FileOutputStream( outFile);
			FileChannel destinationChannel = out.getChannel();
			sourceChannel.transferTo( 0, sourceChannel.size(), destinationChannel);
			destinationChannel.close();
			destinationChannel = null;
			out.close();
			out = null;
			sourceChannel.close();
			sourceChannel = null;
			in.close();
			in = null;
			System.gc();
		} catch( Exception aE) {
			throw new ExceptionIS( aE);
		} 
	}
	
	public static PrintStream getPrintStream( String sFileName, boolean bAppend) throws Exception
    {
		File aFile = new File( sFileName);
		if( !aFile.exists() ){
			int i = sFileName.lastIndexOf( '/');
			if( i > 0 ) {
				File aCat = new File( sFileName.substring( 0, i));
				if( !aCat.exists() ) aCat.mkdir();
			}
		}
		return new PrintStream( new FileOutputStream( aFile, bAppend), true, "UTF-8");
    }    
	public static boolean copyFiles( String sCatIn, String sCatOut) throws ExceptionIS
	{
		return copyFiles( new File( sCatIn), new File( sCatOut));
	}
	public static boolean copyFiles( File aCatIn, File aCatOut) throws ExceptionIS 
	{
		if( aCatIn.exists() && aCatIn.isDirectory()) {
			if( !aCatOut.exists() ){
				aCatOut.mkdir();
			}else if( !aCatOut.isDirectory() ){
				Log.log( "This file is not a directory: " + aCatOut.getAbsolutePath());
				return false;
			}
			File[] aFilesIn = aCatIn.listFiles();
			if ( aFilesIn == null || aFilesIn.length == 0 ){
				Log.log( "There are no files in the directory: " + aCatOut.getAbsolutePath());
				return false;
			}
			Log.log( "moveFiles " + aCatIn.getName() + " to " + aCatOut.getName());
			for( int i = aFilesIn.length;  --i >= 0; ) 
			{
				File aFileIn = aFilesIn[i];
				String sFileName = aFileIn.getName();
				File aFileOut = new File( aCatOut, sFileName);
				copyFile( aFileIn, aFileOut);
			}
		}else{
			Log.log( "This file is not a directory or does not exist: " + aCatIn.getAbsolutePath());
		}
		return true;
	}
}
