/*
 * 
 */
package ee.or.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

// import moritz.db.*;
import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConMsSqlServer.
 * 
 * @author toivo
 * 
 * Andmebaasi yhendus mis arvestab Postgres baasi eripära.
 * 
 * august 2003
 */
public class DbConMsSqlServer extends DbConnection {

	/**
	 * Constructor for DbConPostgres.
	 * 
	 * @param con the con
	 * @param db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbConMsSqlServer( Database db, Connection con) throws MException {
		super( db, con);
	}


	/**
	 * Gets the post key.
	 * 
	 * @param stmt the stmt
	 * 
	 * @return the post key
	 * 
	 * @throws MException the m exception
	 * 
	 * @see moritz.db.DbConnection#getPostKey(Statement)
	 */
	public long getPostKey(Statement stmt) throws MException{

		try {
			ResultSet rs=stmt.executeQuery("SELECT @@IDENTITY");
			if (rs.next()) {
				long key = rs.getInt(1);
				return key;
			}
			// see lihtsalt ei tööta - ms pole seda realiseerinud
	//		ResultSet keys = stmt.getGeneratedKeys();
	//		printout( keys);
		} catch (SQLException e) {
			throw new MException( "getgeneratedkeys.error",
				"Viga genereeritud võtmete lugemisel", null, null, e);
		}
		return -1;
	}

	/**
	 * Supports post key.
	 * 
	 * @return true, if supports post key
	 * 
	 * @throws MException the m exception
	 * 
	 * @see moritz.db.DbConnection#supportsPostKey()
	 */
	public boolean supportsPostKey() throws MException {
		return true;
	}

	/**
	 * Method printout.
	 * 
	 * @param keys the keys
	 * 
	 * @throws MException the m exception
	 */
	public void printout(ResultSet keys) throws MException {
	
		try {
			ResultSetMetaData rsmeta = keys.getMetaData();
			int nrofcols = rsmeta.getColumnCount();
			
			while ( keys.next() ) {
			
				for ( int i=1; i <= nrofcols; i++) {
					String colname = rsmeta.getColumnName(i);
					String colvalue = keys.getString(i);
					System.out.print( " " + colname + "=" + colvalue );
				}
				System.out.println();
			}
		} catch (SQLException e) {
			throw new MException( "dbmeta.read.error", "Viga metadata luegmisel", null, null, e);
		}
	}


}
