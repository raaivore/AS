/*
 * 
 */
package ee.or.db;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConnection.
 * 
 * @author toivo
 * 
 * 
 * Andmebaasi yhenduse baasklass.
 * Konktreetne baas v�ib vajada oma alamklassi juhul kui on vaja arvestada
 * mingite antud baasi erip�radega.
 */
public class DbConnection {
	
	/** The db. */
	protected Database   db;		// Andmebaas millega antud yhendus seotud on.
	
	/** The con. */
	protected Connection con;		// Java sql (jdbc) connection objekt - yhendus baasiga.
	
	/** The i max con. */
	static int iMaxCon = 0;
	
	/** The i con. */
	public int iCon = 0;

	/**
	 * Instantiates a new db connection.
	 * 
	 * @param db the db
	 * @param con the con
	 * 
	 * @throws MException the m exception
	 */
	protected DbConnection( Database db, Connection con) throws MException {
		this.db = db;
		this.con = con;
		try {
			con.setAutoCommit( true);		// NB !!! Vaikimisi on Autocommit off-i lykatud.
		} catch (SQLException e) {
		    try {
                con.close();
            } catch (SQLException E) {
            }
			throw new MException( "dbc2502", "setAutoCommit( false) ebaõnnestus", null, null, e);
		} 
		iCon = ++iMaxCon;
		if( db.isDebug( 2)) db.log( "Create Connection " + iCon);
	}

	/**
	 * Tagastame java.sql.Connection objekti.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return con;
	}

	// =========================================================================
	/**
	 * Ytleme et muutused on l�plikud.
	 * 
	 * @throws MException the m exception
	 */
	public void commit() throws MException {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new MException( "dbc2504", "commit() eba�nnestus", null, null, e);
		}
	}

	/**
	 * Ytleme et muutused ei l�he arvesse.
	 * 
	 * @throws MException the m exception
	 */
	public void rollback() throws MException {
		try {
			con.rollback();
		} catch (SQLException e) {
			throw new MException( "dbc2506", "rollback() eba�nnestus", null, null, e);
		}
	}

	/**
	 * Close.
	 * 
	 * @throws MException the m exception
	 */
	public void close() throws MException 
	{
		if( db.isDebug( 2)) db.log( "Close Connection " + iCon);
		try {
			if( !con.getAutoCommit() ) con.rollback();
			con.clearWarnings();
			con.close();		// tagastab antud yhenduse pooli 
			con = null;
		} catch (SQLException e) {
			throw new MException( "dbc2506", "Andmebaasi yhenduse sulgemine eba�nnestus", null,null, e);
		}
	}
	
	// =========================================================================

	//	MySQL's LIMIT syntax:
	//
	//	SELECT * FROM table_name LIMIT 0, 30
	//
	//	PostgreSQL's LIMIT syntax:
	//	
	//	SELECT * FROM table_name LIMIT 30, 0
	//	
	//	These SQL queries both mean "give me all columns from table table_name, but only 30 records,
	//	starting at record number 0."
	//	MySQL treats the first parameter as the number of the starting row,
	//	and PostgreSQL treats it as the number of rows to return.
	//	It is pretty obvious now, but sometimes even I get confused on this.
	//
	//	In Oracle8i, release 8.1 -- yes.
	//
	//	select * 
	//	  from ( select a.*, rownum rnum
	//  	        from ( YOUR_QUERY_GOES_HERE -- including the order by ) a
	//    	      where rownum <= MAX_ROWS )
	//	  where rnum >= MIN_ROWS
	//
	//	that'll do it.  It will *not* work in 8.0 or before.
	// 
	//	Kahjuks erinevad baasid l�htuvad erinevalt ja osa tagastamine polegi nii lihtne.
	//	Oracle n�ide on eriti nutune - query peab olema query sees et yldse saaks k�tte.
	//	See tundub olevat eriti n�me. Sama jama paistab olevat ka MS SQL puhul....
	//	Midagi saab cursoritega ja JDBC kaudu teha.
	//
	//	SELECT TOP n a.*
	//	FROM
	//	 (SELECT TOP m+n * FROM
	//	 mytable ORDER BY uniqueID DESC
	//	 ) a
	//	ORDER BY uniqueID ASC
	//
	//	Also note that '+' cannot be used and so m+n should be
	//	evaluated before the query
	//
	//	Ja eelmine n�ide k�is siis MS SQL kohta. N�me see v�rk on aga mis sa hing teed?
	//	Sama n�ide k�ib v�ib-olla veel m�ne kohta (kas ka DB2 ??).
	//
	// -- OR -- (DB2 v6 or later)
	//   
	//   SELECT * FROM 
	//      (SELECT name, rownumber() over (order by name)
	//          AS rn FROM address)
	//          AS tr WHERE rn  between 10 and 20
	//   
	//   (The rownumber() function is documented in OLAP functions 
	//    [in the v7 SQL Reference] or in the release-notes for v6)
	//
	//	Siiski viimane n�ide oli n��d veel eraldi DB2 kohta ka .....
	//
	
	/**
	 * Adds the limit to query.
	 * 
	 * @param querybase the querybase
	 * @param startRow the start row
	 * @param nrOfRows the nr of rows
	 */
	public void addLimitToQuery ( StringBuffer querybase, int startRow, int nrOfRows) {
	
		// Vaikimisi me ei tee mitte midagi	
	}
	
	/**
	 * Exec update.
	 * 
	 * @param query the query
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public int execUpdate( String query) throws Exception {
		Statement stmt = null;
		int count;
		try {
			stmt = con.createStatement();
			count = stmt.executeUpdate( query);
		} catch( Exception aE) {
			try {
                con.rollback();
            } catch( Exception E) {
            } 
			throw aE;
//			String msg = "Viga p�ringu: " + query + "; t�itmisel.";
//			throw new MException( "dbc2544", msg, null, null, e); 
		} finally {
			try {
				if ( stmt != null ) stmt.close();
			} catch (SQLException e) {
				Log.info( "Viga Statement-i sulgemisel + \n " + e.getMessage());
			}
		}
		return count;
	}
	
	/**
	 * Exec insert.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * @param sQuery the s query
	 * @param sValues the s values
	 * 
	 * @return the integer
	 * 
	 * @throws Exception the exception
	 */
	public /* synchronized */ Integer execInsert( String sTableName, String sKeyName, 
			String sQuery, String sValues) throws Exception 
	{
		return null;
	}
	
	/**
	 * Method skipEmpty.	Kas tegemist on sellist tyypi columniga kus tyhja v��rtuse korral
	 * insert/update pole soovitav v�i v�imalik.
	 * 
	 * @param tabname the tabname
	 * @param colname the colname
	 * 
	 * @return boolean
	 * 
	 * @throws MException the m exception
	 */
	protected boolean skipEmpty(String tabname, String colname) throws MException {

		// Siia tuleb lisada andmetyybi kontroll ja vastavalt sellele otsus
		// T�psemalt peab alamklass seda tegema.
		return true;
	}

	/**
	 * Method isPrimaryKey.
	 * 
	 * @param pkeys the pkeys
	 * @param colname the colname
	 * 
	 * @return boolean
	 */
	public boolean isPrimaryKey( ArrayList<String> pkeys, String colname) {
		Iterator<String> iter = pkeys.iterator();
		while (iter.hasNext()) {
			String pkey = (String) iter.next();
			if ( colname.equals( pkey) )
				return true;
		}
		
		return false;
	}

	/**
	 * Kas antud andmebaas toetab unikaalsete v�tmete genereerimist enne
	 * lisamist (insert). N�iteks Postgres l�bi serial-i.
	 * Sellisel juhul tuleb kysida uus unikaalne v��rtus enne lisamist meetodiga
	 * getPreKey()
	 * 
	 * @return true, if supports pre key
	 * 
	 * @throws MException the m exception
	 */
	public boolean supportsPreKey() throws MException {

		// Alamklass peab yle kirjutama antud meetodi kui antud v�imalus on olemas		
		return false;
	}

	/**
	 * Kui v�imalik siis tagastab uue unikaalse v��rtuse.
	 * Kui antud tabeli jaoks ei ole defineeritud unikaalse v�tme generaatorit
	 * v�i genereeritud v�tme saamine eba�nnestub siis
	 * tagastab -1
	 * 
	 * @param tabname the tabname
	 * 
	 * @return the pre key
	 * 
	 * @throws Exception the exception
	 */
	public long getPreKey( String tabname) throws Exception {

		// Alamklass peab yle kirjutama antud meetodi kui antud v�imalus on olemas		
		throw new MException( "getprekey.not.implemented",
			"Alamklass peab realiseerima antud meetodi juhul kui unikaalse v�tme tekitamine on v�imalik");
	}

	/**
	 * Kas antud andmebaas toetab genereeritud unikaalsete v�tmete kysimist
	 * peale lisamist (inserti).
	 * Sellisel juhul tuleb kysida uus unikaalne v��rtus peale lisamist meetodiga
	 * getPostKey()
	 * 
	 * @return true, if supports post key
	 * 
	 * @throws MException the m exception
	 */
	public boolean supportsPostKey() throws MException {
	
		// Alamklass peab yle kirjutama antud meetodi kui antud v�imalus on olemas		
		return false;
	}

	/**
	 * Kui v�imalik siis tagastab uue unikaalse v��rtuse.
	 * Kui antud Statement-i jaoks ei ole genereeritud uut unikaalset v��rtust siis
	 * tagastab -1
	 * 
	 * @param stmt the stmt
	 * 
	 * @return the post key
	 * 
	 * @throws MException the m exception
	 */
	public long getPostKey( Statement stmt) throws MException {

		// Alamklass peab yle kirjutama antud meetodi kui antud v�imalus on olemas		
		throw new MException( "getpostkey.not.implemented",
			"Alamklass peab realiseerima antud meetodi juhul kui unikaalse v�tme kysimine on v�imalik");
	}
	
	/**
	 * Gets the simple date format.
	 * 
	 * @return the simple date format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleDateFormat() throws MException {
		return null;
	}
	
	/**
	 * Gets the simple time format.
	 * 
	 * @return the simple time format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleTimeFormat() throws MException {
		return null;
	}
		
		/**
		 * Tagastame antud tabeli primaarse v�tme.
		 * NB !!! Antud meetod eeldab et kysitavas tabelis primaarne v�ti koosneb vaid
		 * yhest columnist.
		 * Kui tabeli primaarne v�ti koosneb mitmest columnist siis antud meetod annab vea.
		 * 
		 * @param tabname the tabname
		 * 
		 * @return the table primary key
		 * 
		 * @throws MException the m exception
		 */
	public String getTablePrimaryKey( String tabname) throws MException {
		String pkey = null;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			ResultSet	rs = dbmeta.getPrimaryKeys(null, null, tabname);

			if ( rs.next() ) {
				pkey = rs.getString("COLUMN_NAME");
/*				if ( rs.next() ) { // teedegraafi rtr sattus siia 20.12.2018
					String msg = "Tabeli '" + tabname +
									"' primaarne v�ti koosneb rohkem kui yhest columnist!";
					Log.info( msg );
					throw new MException( "db2033",
						"Tabeli ? primaarne v�ti koosneb rohkem kui yhest columnist!", tabname, null);
				}*/
			}
			rs.close();
		} catch (SQLException e) {
			String msg = "Viga tabeli '" + tabname + "' primaarsete v�tmete metadata lugemisel. "
				+ e.getMessage();
			Log.info( msg );
//			throw new MException( "db2032", msg, null, null, e);
		}
		return pkey;
	}
	public ResultSet getTableKeys( String sSchemaName, String tabname) throws MException {
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getImportedKeys( null, sSchemaName, tabname);
		} catch (SQLException e) {
			String msg = "Viga tabeli '" + tabname + "' primaarsete v�tmete metadata lugemisel. "
				+ e.getMessage();
			Log.info( msg );
//			throw new MException( "db2032", msg, null, null, e);
		}
		return null;
	}
	
	/**
	 * Tagastab antud tabeli columni tyybi. Tyyp on m��ratud java.sql.Types j�rgi.
	 * 
	 * @param tabname the tabname
	 * @param colname the colname
	 * 
	 * @return the column type
	 * 
	 * @throws MException the m exception
	 */
	public int getColumnType( String tabname, String colname) throws MException {
		int type = 0;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			ResultSet		rs = dbmeta.getColumns(null, null, tabname, "%");
			while ( rs.next() ) {
				String xcolname = rs.getString("COLUMN_NAME");
				if ( colname.equals( xcolname) ){
					type = rs.getInt("DATA_TYPE");
					break;
				}
			}
			rs.close();
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
		return type;
	}
	public boolean  hasColumn( String tabname, String colname) throws MException 
	{
		boolean bRet = false;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			ResultSet rs =  dbmeta.getColumns( null, null, tabname, colname);
			bRet = rs.next();
			rs.close();
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}
		return bRet;
	}
	
	/**
	 * Checks for table.
	 * 
	 * @param tabname the tabname
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean hasTable(  String sSchemaName, String tabname) throws MException 
	{
		// siin on viga kui tabname sisaldab juba skeemi
		boolean bRet = false;
		ResultSet	rs = null;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			rs = dbmeta.getTables( null, sSchemaName, tabname, new String[] {"TABLE"} );
			bRet = rs.next();
			if( bRet && tabname != null && tabname.indexOf( "%") < 0 ){
				bRet = tabname.equalsIgnoreCase( rs.getString( 3));
			}
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}finally{
			try {
/*				if( bRet ){
					Log.info( rs.getString( 0) );
					Log.info( rs.getString( 1) );
					Log.info( rs.getString( 2) );
					Log.info( rs.getString( 3) );
					Log.info( rs.getString( 4) );
				} */
				if( rs != null ) rs.close();
			} catch (SQLException e) {
			}
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
		return bRet;
	}
	public boolean hasColumn( String sSchemaName, String sTableName, String sColumnName) throws MException 
	{
		// siin on viga kui tabname sisaldab juba skeemi
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			rs = dbmeta.getColumns( null, sSchemaName, sTableName, sColumnName);
			return rs.next();
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", sTableName, null, e);
		}finally{
			try {
				if( rs != null ) rs.close();
			} catch (SQLException e) {
			}
		}
	}
	public boolean hasIndex(  String sSchemaName, String sTableName, String sIndexName) throws MException 
	{
		boolean bRet = false;
		ResultSet	rs = null;
		try {
			DatabaseMetaData dbmeta = con.getMetaData();
			rs = dbmeta.getIndexInfo( null, sSchemaName, sTableName, false, false);
			while( rs.next()){
				String s = rs.getString( "INDEX_NAME");
				if( s != null && s.equalsIgnoreCase( sIndexName)){
					return true;
				}
			}
		} catch (SQLException e) {
			throw new MException( "db2026", "Viga tabeli ? index metadata lugemisel", sTableName, null, e);
		}finally{
			try {
				if( rs != null ) rs.close();
			} catch (SQLException e) {
			}
		}
		return bRet;
	}
	public ResultSet getTables(  String sSchemaName) throws MException 
	{
//		ResultSet	rs = null;
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getTables( null, sSchemaName, null, null);
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabelite metadata lugemisel", null, null, e);
		}finally{
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
	}
	public ResultSet getTables(  String sSchemaName, String sTableName) throws MException 
	{
//		ResultSet	rs = null;
		try {
			String[] types = {"TABLE"};
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getTables( null, sSchemaName, sTableName, types);
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabelite metadata lugemisel", null, null, e);
		}finally{
		}
//		throw new MException( "db2026", "Tabelis ? ei ole columnit ?", tabname, colname);
	}
	public ResultSet getSchemas(  String sDatabaseName) throws MException 
	{
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getSchemas( sDatabaseName, null);
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabelite metadata lugemisel", null, null, e);
		}finally{
		}
	}
	public ResultSet getDatabases() throws MException 
	{
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getCatalogs();
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabelite metadata lugemisel", null, null, e);
		}finally{
		}
	}
	
	
	/**
	 * Gets the table pro.
	 * 
	 * @return the table pro
	 */
	public String getTablePro()
	{
		return "";
	}
	
	/**
	 * Gets the code base.
	 * 
	 * @return the code base
	 */
	public String getCodeBase()
	{
		return null;
	}
	
	/**
	 * Gets the time format.
	 * 
	 * @return the time format
	 */
	public String getTimeFormat(){
		return null;
	}
	
	/**
	 * Gets the date format.
	 * 
	 * @return the date format
	 */
	public String getDateFormat(){
		return null;
	}
	
	/**
	 * Gets the simple time z format.
	 * 
	 * @return the simple time z format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleTimeZFormat() throws MException {
		return null;
	}
	public ResultSet getMetaData( String sSchemaName, String tabname) throws MException 
	{
		try {
			DatabaseMetaData dbmeta = getConnection().getMetaData();
			return dbmeta.getColumns( null, sSchemaName, tabname, null); // "%");
		} catch (SQLException e) {
			throw new MException( "db2024", "Viga tabeli ? metadata lugemisel", tabname, null, e);
		}
	}

	public void setCatalog( String sSchemaName)
	{
		try {
			if( con != null ) con.setCatalog( sSchemaName);
		} catch (SQLException e) {
		}
	}
	public void setTimeoutXXX( int iTimeout) // ei t��ta
	{ 
		try {
			Properties aProps = con.getClientInfo();
			Log.log(  aProps.toString());
			try {
				Log.log( Integer.toString( con.getNetworkTimeout()));
			} catch (Exception e) {
				Log.error( e);
			}
			try {
				Log.log( Integer.toString( con.getHoldability()));
			} catch (Exception e) {
				Log.error( e);
			}
			aProps = new Properties();
			aProps.setProperty( "connectTimeout", Integer.toString( iTimeout));
			con.setClientInfo( aProps);
		} catch (SQLException e) {
			Log.error( e);;
		}
	}
	public int exec( ArrayList<String> aBatch) throws Exception 
	{
		Statement stmt = null;
		int count = 0;
		try {
			stmt = con.createStatement();
//			con.setAutoCommit( false);
			for (String sSql: aBatch) {
				stmt.addBatch( sSql);
			}
			stmt.executeBatch();
//			con.commit();
//			con.setAutoCommit( true);
		} catch( BatchUpdateException aE) {
			String s = aE.getMessage();
			if( s.indexOf( "Too many update results were returned") > 0 ) return 1;
			throw aE;
		} catch( Exception aE) {
			try {
                con.rollback();
            } catch( Exception E) {
            } 
			throw aE;
//			String msg = "Viga p�ringu: " + query + "; t�itmisel.";
//			throw new MException( "dbc2544", msg, null, null, e); 
		} finally {
			try {
				if ( stmt != null ) stmt.close();
			} catch (SQLException e) {
				Log.info( "Viga Statement-i sulgemisel + \n " + e.getMessage());
			}
		}
		return count;
	}
}
