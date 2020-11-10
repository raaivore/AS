/*
 * 
 */
package ee.or.db;

import java.sql.Connection;
import java.sql.SQLException;

import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConMysql.
 * 
 * @author toivo
 * 
 * Andmebaasi yhendus mis arvestab Mysql baasi eripära.
 */
public class DbConMysql extends DbConnection {

	/**
	 * Constructor for DbConMysql.
	 * 
	 * @param con the con
	 * @param db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbConMysql( Database db, Connection con) throws MException {
		super( db, con);
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#close()
	 */
	public void close() throws MException 
	{
	    Log.info( 2, "Close Connection " + iCon);
		try {
			con.clearWarnings();
			con.close();		// tagastab antud yhenduse pooli 
			con = null;
		} catch (SQLException e) {
			throw new MException( "dbc2506", "Andmebaasi yhenduse sulgemine ebaõnnestus", null,null, e);
		}
	}

	/* (non-Javadoc)
	 * @see or.db.DbConnection#addLimitToQuery(java.lang.StringBuffer, int, int)
	 */
	public void addLimitToQuery ( StringBuffer querybase, int startRow, int nrOfRows) {
	
		if ( (startRow >= 0) && (nrOfRows >= 0) ) {
			//	Järgnev on MySql 'versioon'....
			querybase.append (" limit " + startRow + ", " + nrOfRows);
		}			
	}


}
