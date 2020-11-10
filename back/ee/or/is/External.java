/*
 * 
 */
package ee.or.is;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Class External.
 */
public class External {
	int iRet = 0;
	public int getRet(){ return iRet;}
	
	/**
	 * Instantiates a new external.
	 * 
	 * @param cmd the cmd
	 */
	@SuppressWarnings("unused")
	public External( String[] cmd)
	{
		Process aProcess = null;
		StreamGobbler aErrStream = null;
		StreamGobbler aOutStream = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			log( "external start");
			aProcess = runtime.exec( cmd);
			aErrStream = new StreamGobbler( aProcess.getErrorStream(), "err", this);
			aOutStream = new StreamGobbler( aProcess.getInputStream(), "out", this);
			iRet = aProcess.waitFor();
			log( "external end");
		} catch( Exception aE) {
			log( aE);
		} finally {
			if ( aProcess != null )	aProcess.destroy(); // Kui kõik on tehtud
		}
		
	}
	
	/**
	 * Log.
	 * 
	 * @param sMsg the s msg
	 */
	public void log( String sMsg)
	{
		Log.log( sMsg);
	}
	
	/**
	 * Log.
	 * 
	 * @param aE the a e
	 */
	public void log( Exception aE)
	{
		Log.log( aE);
	}
	
	/**
	 * The Class StreamGobbler.
	 */
	public class StreamGobbler extends Thread 
	{
		
		/** The is. */
		private InputStream is;
		
		/** The type. */
		private String type;
		
		/** The a external. */
		private External aExternal = null;

		/**
		 * Instantiates a new stream gobbler.
		 * 
		 * @param is the is
		 * @param type the type
		 * @param aExternal the a external
		 */
		public StreamGobbler(InputStream is, String type, External aExternal) {
			this.is = is;
			this.type = type;
			this.aExternal = aExternal;
			start();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() 
		{
			try {
				BufferedReader aBR = new BufferedReader( new InputStreamReader(is));
				String line = null;
				while ((line = aBR.readLine()) != null) {
					aExternal.log( type + " > " + line);
				}
			} catch( Exception aE) {
				aExternal.log( aE);
			} 
		}
	}

}
