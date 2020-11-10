/*
 * 
 */
package ee.or.db;

import java.sql.Connection;
import oracle.jdbc.OracleResultSetMetaData;
//import oracle.sql.converter_xcharset;
//import java.sql.DatabaseMetaData;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import ee.or.is.*;

/**
 * The Class DbConOracle.
 * 
 * @author or 16.11.2004
 * 
 * Andmebaasi yhendus mis arvestab Oracle baasi erip‰ra.
 */
public class DbConOracle extends DbConnection {

	/**
	 * Instantiates a new db con oracle.
	 * 
	 * @param db the db
	 * @param con the con
	 * 
	 * @throws MException the m exception
	 */
	public DbConOracle( Database db, Connection con) throws MException {
		super( db, con);
		try {
			this.con.setAutoCommit( false);		// NB !!! Vaikimisi on Autocommit off-i lykatud.
		} catch (SQLException e) {
			throw new MException( "dbc2502", "setAutoCommit( false) ebaınnestus", null, null, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#close()
	 */
	public void close() throws MException 
	{
	    Log.info( 2, "Close Connection " + iCon);
		try {
			con.rollback();
			con.clearWarnings();
			con.close();		// tagastab antud yhenduse pooli 
			con = null;
		} catch (SQLException e) {
			throw new MException( "dbc2506", "Andmebaasi yhenduse sulgemine ebaınnestus", null,null, e);
		}
	}
	
	/**
	 * Gets the pre key old.
	 * 
	 * @param tabname the tabname
	 * 
	 * @return the pre key old
	 * 
	 * @throws Exception the exception
	 */
	public long getPreKeyOlad( String tabname) throws Exception {
	//  Uut unikaalset v‰‰rtust saame lisada Oracle korral siis kui
	// antud tabeli jaoks on defineeritud andmebaasis vastav sequence
		String seqname = tabname + "_seq";
		if( !db.sequenceExist( seqname)	) return -1;
		Statement stmt = null;
		ResultSet rs = null;
		String qry = "select " + seqname + ".nextval from dual";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery( qry);
			if( rs.next() ) return rs.getLong( 1);
//		} catch (SQLException e2) {
//			throw new MException( "Katse Oracle-st uut vıtit k‰tte saada ebaınnestus"
//				+ ", query='" + qry + "'" + "\n" + e2.getMessage() );
		} finally {
//			try {
				if( rs != null ) rs.close();
				if( stmt != null ) stmt.close();
//			} catch (SQLException e) {
//				throw new MException( "Viga Statement-i sulgemisel + \n " + e.getMessage());
//			}
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getColumnType(java.lang.String, java.lang.String)
	 */
	public int getColumnType( String tabname, String colname) throws MException 
	{
		int type = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
		    rs = stmt.executeQuery( "SELECT * from " + tabname);
		    OracleResultSetMetaData rmd = (OracleResultSetMetaData)rs.getMetaData();
		    int n = rmd.getColumnCount();
	        for( int i=1; i <= n; ++i) {
// LogOR.info( "Key find " + i + " Name='" + colname + "' ? '" + rmd.getColumnName( i) + "'");
	        	if( colname.equalsIgnoreCase( rmd.getColumnName( i)) ){
	    		    type = rmd.getColumnType( i);			
// LogOR.info( "Key Name=" + colname + " Type=" + type);
	    		    break;
	        	}			
			}
			rs.close();
			stmt.close();
		} catch( Exception e ){
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, colname, e);
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
		return type;
	}
	
	public ResultSet getMetaData(  String sSchemaName, String tabname) throws MException 
	{
		return super.getMetaData( sSchemaName, tabname.toUpperCase());
	}
	public ResultSet getMetaDataOld( String tabname) throws MException 
	{ // ei kasutata
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getColumns( null, null, tabname, "%");
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}
	}
	public boolean hasTable(  String sSchemaName, String tabname) throws MException 
	{
		return super.hasTable( sSchemaName, tabname.toUpperCase());
	}
	public boolean hasTableOld( String tabname) throws MException 
	{ // ei kasutata
		boolean bRet = false;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
		    rs = stmt.executeQuery( "SELECT * from " + tabname);
		    if( rs != null ){
			    OracleResultSetMetaData rmd = (OracleResultSetMetaData)rs.getMetaData();
			    if( rmd != null ){
					DatabaseMetaData dbmeta = getConnection().getMetaData();
					if( dbmeta != null ) bRet = true;
			    }
				rs.close();
		    }
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
		return bRet;
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#supportsPreKey()
	 */
	public boolean supportsPreKey() throws MException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#supportsPostKey()
	 */
	public boolean supportsPostKey() throws MException {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getSimpleDateFormat()
	 */
	public SimpleDateFormat getSimpleDateFormat() throws MException {
		return new SimpleDateFormat( "yyyy-MM-dd");
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getSimpleTimeFormat()
	 */
	public SimpleDateFormat getSimpleTimeFormat() throws MException {
		return new SimpleDateFormat( "yyyy-MM-dd H.m.s"); // 'YYYY-MM-DD HH24:MI:SS'
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getTimeFormat()
	 */
	public String getTimeFormat(){
		return "YYYY-MM-DD HH24.MI.SS"; // 
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getDateFormat()
	 */
	public String getDateFormat(){
		return "YYYY-MM-DD"; // 
	}
	
	/**
	 * Exec insert generated keys.
	 * 
	 * @param query the query
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public int execInsertGeneratedKeys( String query) throws MException {
		Statement stmt = null;
		int iID = 0;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if( rs.next() ) {    // Retrieve the auto generated key(s).    
				iID = ( int)rs.getLong( 1); 
			}
		} catch( Exception e) {
			String msg = "Viga p‰ringu: " + query + "; t‰itmisel.";
			try {
                con.rollback();
            } catch( Exception E) {
            } 
			throw new MException( "dbc2544", msg, null, null, e);
		} finally {
			try {
				if ( stmt != null ) stmt.close();
			} catch (SQLException e) {
				Log.info( "Viga Statement-i sulgemisel + \n " + e.getMessage());
			}
		}
		return iID;
	}	
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#execInsert(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public /* synchronized */ Integer execInsert( String sTableName, String sKeyName, 
			String sQuery, String sValues) throws Exception
	{
		Integer PKey = null;
		int iID = ( int)getPreKey( sTableName);
		if( iID != -1 ){
			execUpdate( sQuery + ", " + sKeyName + sValues + ", " + iID + ")");
			PKey = new Integer( iID);
		}
		return PKey;
	}
	
	/** The a tables. */
	private static HashMap<String,Long> aTables = new HashMap<String,Long>( 20);
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getPreKey(java.lang.String)
	 */
	public long getPreKey( String sTableName) throws Exception 
	{
		long lKey = 0;
		synchronized ( aTables ){
			Long aKey = ( Long)aTables.get( sTableName);
			if( aKey != null ) lKey = aKey.longValue();
			if( lKey <= 0 ){
				Statement stmt = null;
				ResultSet rs = null;
				String qry = "select max( id) as last_id from " + sTableName;
				try {
					stmt = con.createStatement();
					rs = stmt.executeQuery( qry);
					if( rs.next() ) lKey = rs.getLong( 1);
				} catch ( Exception aE) {
				} finally {
//					try {
					if( rs != null ) rs.close();
					if( stmt != null ) stmt.close();
//					} catch (SQLException e) {
//						throw new MException( "Viga Statement-i sulgemisel + \n " + e.getMessage());
//					}
				}
			}
			if( lKey >= 0 ) aTables.put( sTableName, new Long( ++lKey));
		}
		return lKey;
	}

}
