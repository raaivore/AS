/*
 * 
 */
package ee.or.db;

import java.awt.geom.Point2D;
// import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.*;

import org.postgresql.util.PSQLException;
//import org.postgresql.largeobject.*;
import org.w3c.dom.*;

import ee.or.is.*;

/**
 * The Class Database.float
 */

public class Database implements Serializable
{
	private static final long serialVersionUID = 1L;
	/** The dbcon. */
	DbConnection dbcon = null;
	
	/** The dbdescr. */
	private DbDescr dbdescr = null;
	public DbDescr getDbDescr(){	return dbdescr;}
	public void setDbDescr( DbDescr dbdescr){	this.dbdescr = dbdescr;}
	
	/** The s pro. */
	private String sPro = "";
	
	/** The s codebase. */
	private String sCodebase = null;
	
	/** The s encoding. */
	private String sEncoding = null;
	
	/** The Date f. */
	private SimpleDateFormat DateF = null;
	
	/** The Time f. */
	private SimpleDateFormat TimeF = null;
	
	/** The Date time z. */
	private SimpleDateFormat DateTimeZ = null;
//	private User User = null;
	/** The s tf. */
	private String sTF = null;
	
	/** The s date format. */
	private String sDateFormat = null;
	
	/** The s date time format. */
	private String sDateTimeFormat = null;
	
	/** The a sight. */
	private Sight aSight = null;
//	private int iUserID = 0;

	public Database()
	{
	}
	public Database( Sight aSight)
	{
		this.aSight = aSight;
	}
	public Database( DbDescr dbdescr) throws MException
	{
		init( dbdescr);
	}
	
	/**
	 * Instantiates a new database.
	 * 
	 * @param dbdescr the dbdescr
	 * @param aSight the a sight
	 * 
	 * @throws MException the m exception
	 */
	public Database( DbDescr dbdescr, Sight aSight) throws MException
	{
		this.aSight = aSight;
		init( dbdescr);
	}
	
	/**
	 * Instantiates a new database.
	 * 
	 * @param dbdescr the dbdescr
	 * @param sUser the s user
	 * @param sPSW the s psw
	 * 
	 * @throws MException the m exception
	 */
	public Database( DbDescr dbdescr, String sUser, String sPSW) throws MException
	{
		this.dbdescr = dbdescr.copy();
		this.dbdescr.setDbuser( sUser);
		this.dbdescr.setDbpasswd( sPSW);	
		dbcon = getDbConnection();
/*		DbConnection dbcon = getDbConnection();
		dbcon.close();
		dbcon = null; */
		init( dbdescr);
/*		if( dbdescr.getCodebase() != null ) setCodebase( dbdescr.getCodebase());
		if( dbdescr.getDateFormat() != null ){
		    sDateFormat = dbdescr.getDateFormat();
		    DateF = new SimpleDateFormat( sDateFormat);
		}
		if( dbdescr.getDateTimeFormat() != null ){
		    sDateTimeFormat = dbdescr.getDateTimeFormat();
		    TimeF = new SimpleDateFormat( sDateTimeFormat);
		}
		setEncoding(); */
	}
	
	/**
	 * Inits the.
	 * 
	 * @param dbdescr the dbdescr
	 * 
	 * @throws MException the m exception
	 */
	public void init( DbDescr dbdescr)
	{
		try {
			this.dbdescr = dbdescr.copy();
			dbcon = getDbConnection();
/*		DbConnection dbcon = getDbConnection();
			dbcon.close();
			dbcon = null; */
			if(dbdescr.getCodebase() != null ) setCodebase( dbdescr.getCodebase());
			if( dbdescr.getDateFormat() != null ){
			    sDateFormat = dbdescr.getDateFormat();
			    DateF = new SimpleDateFormat( sDateFormat);
			}
			if( dbdescr.getDateTimeFormat() != null ){
			    sDateTimeFormat = dbdescr.getDateTimeFormat();
			    TimeF = new SimpleDateFormat( sDateTimeFormat);
			}
			setEncoding();
		} catch( Exception aE ) {
			log( aE);		
		}
//		if( dbdescr.getSchemaName() != null ) exec( "SET SCHEMA '" + dbdescr.getSchemaName() + "'");
	}
	
	/**
	 * Gets the sQL date.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL date
	 */
	public synchronized String getSQLDate( java.sql.Date dValue) //throws MException 
	{
	    if( isOracle() ){
			SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd");
			return "to_date('" + DF.format( dValue) + "','YYYY-MM-DD')";
	    }else{
	        return ( DateF != null)? DateF.format( dValue): null;
	    }
	}
	
	/**
	 * Gets the sQL time.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL time
	 */
	public synchronized String getSQLDate() //throws MException 
	{
		return GlobalDate.getDateSQLString();
	}
	public synchronized String getSQLTime( java.sql.Date dValue) //throws MException 
	{
	    if( isOracle() ){
			SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd H.m.s");
			return "to_date('" + DF.format( dValue) + "','YYYY-MM-DD HH24.MI.SS')";
	    }else{
	        return ( TimeF != null)? TimeF.format( dValue): null;
	    }
	}
/*	public synchronized String getSQLTime( java.sql.Date dValue)  
	{
		if( dValue != null ) try {
            SimpleDateFormat DF = getSimpleTimeFormat();
            return isOracle()? "to_date('" + DF.format( dValue) + "','" + getTimeFormat() + "')": 
            	DF.format( dValue);
        } catch( MException E) {
        } catch( Exception E) {
            log( " Exception" + E.getMessage());
        }
        return null;
	}*/
	/**
 * Gets the db geometry.
 * 
 * @return the db geometry
 */
	public String getDbGeometry()
	{
		return (dbdescr!=null)? dbdescr.getDbGeometry(): null;
	}
	public int getProjection() {
		return (dbdescr!=null)? dbdescr.getProjection(): 0;
	}
	
	/**
	 * Checks if is oracle.
	 * 
	 * @return true, if is oracle
	 */
	public boolean isOracle()
	{
		return dbdescr.getDbtypestr().indexOf("oracle") >= 0;
	}
	
	/**
	 * Checks if is paradox.
	 * 
	 * @return true, if is paradox
	 */
	public boolean isParadox()
	{
		return dbdescr.getDbtypestr().indexOf("paradox") >= 0;
	}
	
	/**
	 * Gets the db connection.
	 * 
	 * @return the db connection
	 * 
	 * @throws MException the m exception
	 */
	private DbConnection getDbConnection() throws MException {
		Connection newcon = null;
		String sURL = dbdescr.getDatabaseUrl();
		String sUser = dbdescr.getDbuser();
		String sPSW = dbdescr.getPSW();

		String msg = "Viga andmebaasi driveri ? laadimisel";
		try {
			Class.forName( dbdescr.getDrivername()).newInstance();
		} catch (InstantiationException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		} catch (IllegalAccessException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		} catch (ClassNotFoundException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		}
		try {
			if( isParadox() ){
				if( dbdescr.getCodebase() != null ){
					Properties aProperties=new Properties();
					aProperties.setProperty( "charSet", dbdescr.getCodebase());
					newcon = DriverManager.getConnection( sURL, aProperties);
				}else newcon = DriverManager.getConnection( sURL);
			}else{
				Properties aProperties = new Properties();
				aProperties.setProperty( "user", sUser);
				aProperties.setProperty( "password", sPSW);
//				aProperties.setProperty( "loginTimeout", "180");
				aProperties.setProperty( "socketTimeout", dbdescr.getTimeout());
				newcon = DriverManager.getConnection( sURL, aProperties);
			}
			String sSchemaName = dbdescr.getSchemaName();
			if( sSchemaName != null ) newcon.setCatalog( sSchemaName);
		} catch( SQLException aE) {
			log( aE);
			try {
				if ( newcon != null ) newcon.close();
			} catch (SQLException e2) {
				// Siin me enam eraldi veast ette ei kanna
			}
			throw new MException( "db2012",
						"Viga ? andmebaasiga ühenduse loomisel ", sURL, null, aE);
		}
		return _getDbConObject( newcon);
	}
	public void testDbConnection() throws Exception 
	{
		Connection newcon = null;
		String sURL = dbdescr.getDatabaseUrl();
		String sUser = dbdescr.getDbuser();
		String sPSW = dbdescr.getPSW();

//		String msg = "Viga andmebaasi driveri ? laadimisel";
		Class.forName( dbdescr.getDrivername()).newInstance();
/*		try {
		} catch (InstantiationException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		} catch (IllegalAccessException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		} catch (ClassNotFoundException e) {
			throw new MException( "db2010", msg, sURL, null, e);
		}*/
		try {
			if( isParadox() ){
				if( dbdescr.getCodebase() != null ){
					Properties aProperties = new Properties();
					aProperties.setProperty( "charSet", dbdescr.getCodebase());
					newcon = DriverManager.getConnection( sURL, aProperties);
				}else newcon = DriverManager.getConnection( sURL);
			}else	newcon = DriverManager.getConnection( sURL, sUser, sPSW);
		}finally{
			if ( newcon != null ) newcon.close();
		}
	}
	public boolean isExists()
	{
		try{
			testDbConnection();
			return true;
		}catch( Exception aE ){
		}
		return false;
	}
	/**
	 * _get db con object.
	 * 
	 * @param con the con
	 * 
	 * @return the db connection
	 * 
	 * @throws MException the m exception
	 */
	private final DbConnection _getDbConObject( Connection con) throws MException {
		String dname = "";
		DatabaseMetaData dbmeta;
		DbConnection newdbcon = null;
		try {
			dbmeta = con.getMetaData();
			dname = dbmeta.getDatabaseProductName().toLowerCase();

		} catch (SQLException e) {
			throw new MException( "db2014",
						"Andmebaasi driveri nime lugemine eba�nnestus", null, null, e);
		}
		finally {
			dbmeta = null;
		}

		if ( dname.indexOf( "mysql") >= 0) {
			newdbcon = new DbConMysql( this, con);
		}else if ( dname.indexOf("postgres") >= 0) {
			newdbcon = new DbConPostgres( this, con);
		}else if ( dname.indexOf("microsoft") >= 0) {
			newdbcon = new DbConMsSqlServer( this, con);
		}else if ( dname.indexOf("progress") >= 0) {
			newdbcon = new DbConProgress( this, con);
		}else if ( dname.indexOf("odbc") >= 0) {
			newdbcon = new DbConODBC( this, con);
		}else if ( dname.indexOf("oracle") >= 0) {
			newdbcon = new DbConOracle( this, con);
		}else if ( isParadox() ){
			newdbcon = new DbConParadox( this, con);
		}else if ( dname.indexOf( "stelsmdb") >= 0 ){
			newdbcon = new DbConAccess( this, con);
		}else 
			throw new MException( "db2016", "Tundmatu andmebaasi t��bi string ?", dname, null);
		if( newdbcon != null ){
			sPro = newdbcon.getTablePro();
//			sCodebase = newdbcon.getCodeBase();
			TimeF = newdbcon.getSimpleTimeFormat();
			DateTimeZ = newdbcon.getSimpleTimeZFormat();
			sTF = newdbcon.getTimeFormat();
			if( DateF == null ){
				sDateFormat = newdbcon.getDateFormat();
				DateF = newdbcon.getSimpleDateFormat();
			}
		}
		return newdbcon;
	}
	
	/**
	 * Gets the statement.
	 * 
	 * @param bRead the b read
	 * 
	 * @return the statement
	 * 
	 * @throws Exception the exception
	 */
	public Statement getStatement( boolean bRead)  throws Exception
	{
		try {
			if( dbcon == null ) dbcon = getDbConnection();
			Connection Con = dbcon.getConnection();
			if( Con != null ){
				if( bRead ){
					return Con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				}else{
				}
			}
		} catch( Exception e ){
		    throw e;
		}
		return null;
	}
	public CallableStatement getCallStatement( String sCall)  throws Exception
	{
		try {
			if( dbcon == null ) dbcon = getDbConnection();
			Connection Con = dbcon.getConnection();
			if( Con != null ){
				return Con.prepareCall( sCall);
			}
		} catch( Exception e ){
		    throw e;
		}
		return null;
	}
	
	/**
	 * Gets the pre statement.
	 * 
	 * @param sPreSQL the s pre sql
	 * 
	 * @return the pre statement
	 * 
	 * @throws Exception the exception
	 */
	public PreparedStatement getPreStatement( String sPreSQL)  throws Exception
	{
		try {
			if( dbcon == null ) dbcon = getDbConnection();
			Connection Con = dbcon.getConnection();
			if( Con != null ){
				return Con.prepareStatement( sPreSQL);
			}
		} catch( Exception e ){
		    throw e;
		}
		return null;
	}
	
	/**
	 * Exec.
	 * 
	 * @param query the query
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public synchronized int exec( String query) throws MException 
	{
		if( isDebug( 1)){
			if( !isDebug( 98) && query.length() > 100 ){
				log( query.substring(0, 100) + " ...");
			}else log( query);
		}
		long lSTime = ( new Date()).getTime();
		int iRet = 0;
		int i = 0;
//		for( int i = 2; --i>=0; )  // teeme veel  �he �rituse;
		try {
			if( dbcon == null ){
				dbcon = getDbConnection();
				int j = query.indexOf( "SET SCHEMA");
				if( j < 0 && getSchemaName() != null ) exec( "SET SCHEMA '" + getSchemaName() + "'");
			}
			iRet = dbcon.execUpdate( query);
			commit();
//			break;
		} catch( Exception e ){
			String s = e.getMessage();
			if( s != null ){
				int k = s.indexOf( "duplicate key value");
				if( k >= 0 ){
//					log( s);
				i = 0;
				}else
					log( s);
			}
		    if( i == 0 ){
				String msg = "Viga päringu: " + query + "; täitmisel.";
				throw new MException( "dbc2544", msg, null, null, e); 
//		    	throw e;
		    }
/*		    try {
                dbcon.rollback();
            } catch( Exception E ){
                log( E);
            }*/
            try {
                dbcon.close();
            } catch (MException E1) {
            }
            dbcon = null; 
		} finally {
		}
		lSTime = ( new Date()).getTime() - lSTime;
		if( isDebug( 1)) log( "DB time=" + lSTime + "ms");
	    return iRet;
	}
	public synchronized int execBatch( String sSql) throws MException 
	{
		ArrayList<String> aBatch = new ArrayList<String>( 10);
		String[] aS = sSql.split( ";");
		for( String s: aS){
			aBatch.add( s + ";");
		}
		return exec( aBatch);
	}
	public synchronized int exec( ArrayList<String> aBatch) throws MException 
	{
		if( isDebug( 1)){
			for( String query: aBatch) {
				if( !isDebug( 98) && query.length() > 100 ){
					log( query.substring(0, 100) + " ...");
				}else log( query);
			}
		}
		long lSTime = ( new Date()).getTime();
		int iRet = 0;
		try {
			if( dbcon == null ){
				dbcon = getDbConnection();
			}
			iRet = dbcon.exec( aBatch);
			commit();
		} catch( Exception e ){
			String msg = "Viga päringupaki täitmisel.";
			throw new MException( "dbc2544", msg, null, null, e); 
		} finally {
		}
		lSTime = ( new Date()).getTime() - lSTime;
		if( isDebug( 1)) log( "DB time=" + lSTime + "ms");
	    return iRet;
	}
	public synchronized ResultSet execSelect( String sQuery) throws ExceptionIS 
	{
		long lSTime = ( new Date()).getTime();
		if( isDebug( 5)) log( sQuery);
		try{
			for( int i=2; --i>=0;){
		    	Statement aSt = null;
		    	ResultSet aRS = null;
				try {
				    aSt = getStatement( true);
				    aSt.execute( sQuery);
			       	aRS = aSt.getResultSet();
			       	return aRS;
				} catch( Exception aE) {
					log( aE);
					try {
		 				if( aRS != null ) aRS.close();
		 		       	if( aSt != null ) aSt.close();
		 			} catch( Exception aE2) {
		 			}
			        if( aE instanceof SQLException || aE instanceof PSQLException ){
						String msg = "error while requesting query '" + sQuery + "'";
						throw new ExceptionIS( aE, msg);
			        }
			        
				}
				close();
			}
//		}catch( Exception aE ){
		}finally{
			lSTime = ( new Date()).getTime() - lSTime;
			if( isDebug( 1)) log( "DB time=" + lSTime + "ms");
		}
    	return null;
	}	
	public final String getString( String sTableName, String colname, int iId) throws MException 
	{
		return getString( sTableName, colname, "id=" + iId);
	}
	public int getInt( String sTableName, String colname, int iId) throws MException 
	{
		return getInt( sTableName, colname, "id=" + iId);
	}
	public int getInt( String sTableName, String colname, String sWhere) throws MException 
	{
		String s = getString(  sTableName, colname, sWhere);
		if( s == null ) return 0;
		s = s.trim();
		if( s.length() == 0 ) return 0; 
		try {
			return (s != null)? Integer.parseInt( s): 0;
		} catch (NumberFormatException aE) {
			try {
				return ( int)Math.round( Double.parseDouble( s));
			} catch (NumberFormatException aE1) {
				log( aE1);
			}
			log( aE);
		}
		return 0;
	}
	public java.sql.Date getDate( String sTableName, String colname, int iId) throws MException 
	{
		return parseDate( getString(  sTableName, colname, iId));
	}
	public final String getString( String sTableName, String colname, String sWhere) throws MException 
	{
		if( colname == null) return null;
		try {
			ResultSet aRS = execSelect( "select " + colname + " from " + sTableName + " WHERE " + sWhere);
			if( aRS.next()) {
				String s = aRS.getString( colname);
				return ( s != null )? new String( s): "";
			}
		} catch ( Exception aE) {
			log( aE);
		}
		return null;
	}
	public synchronized java.sql.Date parseDate( String sDate) throws MException 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		java.util.Date date = null;
		SimpleDateFormat DF = getSimpleDateFormat();
		try{
			date = DF.parse( sDate);  
		} catch ( Exception e) {
		    log( 99, sDate + " " + DF.toPattern() + " " + e.getMessage());
			DF = new SimpleDateFormat( "yyyy-MM-dd");	
			try{
				date = DF.parse( sDate);  
			} catch ( Exception e1) {
				try {
					DF = new SimpleDateFormat( "HH:mm:ssss");
					date = DF.parse( sDate);
				} catch ( Exception e2) {	
					log( sDate + " " + DF.toPattern() + " " + e1.getMessage() + " " + e2.getMessage());
				}
			}
		}
		return (date!=null)? new java.sql.Date( date.getTime()): null; 
	}
	public synchronized int execCount( String sQuery) throws ExceptionIS 
	{// eeldusel, et SQL lauses on count( ) ja esimesel kohal
		long lSTime = ( new Date()).getTime();
		if( isDebug( 5)) log( sQuery);
		Statement aSt = null;
    	ResultSet aRS = null;
		try {
		    aSt = getStatement( true);
		    aSt.execute( sQuery);
	       	aRS = aSt.getResultSet();
	       	if( aRS.next())
	       		return aRS.getInt( 1);
		} catch( Exception aE) {
			log( aE);
			try {
 				if( aRS != null ) aRS.close();
 		       	if( aSt != null ) aSt.close();
 			} catch( Exception aE2) {
 			}
	        if( !(aE instanceof SQLException) ){
				String msg = "Error while requesting query '" + sQuery + "'";
				new ExceptionIS( aE, msg);
	        }
		}finally{
			try {
				if( aRS != null ) aRS.close();
				if( aSt != null ) aSt.close();
			} catch (SQLException e) {
			}
			lSTime = ( new Date()).getTime() - lSTime;
			if( isDebug( 1)) log( "DB time=" + lSTime + "ms");
		}
    	return 0;
	}	
	public synchronized void close( ResultSet aRS)
	{
		if( aRS != null ){
			try {
				Statement aSt = aRS.getStatement();
 				if( aRS != null ) aRS.close();
 		       	if( aSt != null ) aSt.close();
 			} catch( Exception aE) {
 			}
		}
	}
	/**
	 * Exec insert.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * @param sQuery the s query
	 * @param sValues the s values
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public int execInsert( String sTableName, String sKeyName, String sQuery, String sValues) throws MException
	{
		String sUQuery = sQuery + ", " + sKeyName + sValues + ", ?" + "; " + sTableName;
		if( isDebug( 1)) log( sUQuery);
		Integer PKey = null;
		try {
			PKey = dbcon.execInsert( sTableName, sKeyName, sQuery, sValues);
			if( PKey == null ){
				String msg = "Error while requesting query : '" + sUQuery + "'";
				throw new MException( "", msg);
			}
		} catch ( Exception e) {
			String msg = "Error while requesting query '" + sUQuery + "'";
			throw new MException( "", msg, null, null, e);
		}
		return PKey.intValue(); 
	}
	
	/**
	 * Commit.
	 * 
	 * @throws MException the m exception
	 */
	public void commit() throws MException 
	{
	    if( dbcon != null ){
	    	try{
				if( !dbcon.con.getAutoCommit() ) dbcon.commit();
			}catch( SQLException aE ){
			}
	    }
	} 
	
	/**
	 * Rollback.
	 * 
	 * @throws MException the m exception
	 */
	public void rollback() throws MException 
	{
	    if( dbcon != null ) dbcon.rollback();
	} 
	
	/**
	 * Gets the table pro.
	 * 
	 * @return the table pro
	 * 
	 * @throws MException the m exception
	 */
	public String getTablePro()  throws MException
	{
		return sPro;
	}
	
	/**
	 * Gets the code base.
	 * 
	 * @return the code base
	 * 
	 * @throws MException the m exception
	 */
	public String getCodeBase() throws MException
	{
		return sCodebase;
	}
	
	/**
	 * Gets the simple date format.
	 * 
	 * @return the simple date format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleDateFormat() throws MException
	{
		if( DateF == null ) DateF = new SimpleDateFormat( "yyyy-MM-dd");
		return DateF;
	}
	
	/**
	 * Gets the simple time format.
	 * 
	 * @return the simple time format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleTimeFormat() throws MException
	{
		return TimeF;
	}
	
	/**
	 * Gets the simple time z format.
	 * 
	 * @return the simple time z format
	 * 
	 * @throws MException the m exception
	 */
	public SimpleDateFormat getSimpleTimeZFormat() throws MException
	{
		return DateTimeZ;
	}
	
	/**
	 * Gets the time format.
	 * 
	 * @return the time format
	 * 
	 * @throws MException the m exception
	 */
	public String getTimeFormat() throws MException
	{
		return sTF;
	}
	
	/**
	 * Gets the date format.
	 * 
	 * @return the date format
	 * 
	 * @throws MException the m exception
	 */
	public String getDateFormat() throws MException
	{
		return sDateFormat;
	}
	
	/**
	 * Close.
	 */
	public void close()
	{
//		dbdescr = null;
		try {
            if( dbcon != null ) dbcon.close();
        } catch( Exception aE) {
        	log( aE.getLocalizedMessage());
        }
        dbcon = null;
	}
	
	/**
	 * Gets the column type.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * 
	 * @return the column type
	 * 
	 * @throws MException the m exception
	 */
	public int getColumnType( String sTableName, String sKeyName) throws MException
	{
//		DbConnection dbcon = null;

		int iRet = 0;
		try{
			if( dbcon == null ) dbcon = getDbConnection();
			iRet = ( dbcon!=null )? dbcon.getColumnType( sTableName, sKeyName): 0;
		} finally {
//			if( dbcon != null) dbcon.close();
		}
		return iRet;
	}	
	
	/**
	 * Sequence exist.
	 * 
	 * @param seqname the seqname
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean sequenceExist( String seqname) throws MException {

		if ( seqname == null)
			throw new IllegalArgumentException( "kysitava sequence nimi ei tohi olla null");
			
//		DbConnection dbcon = null;
		ResultSet	rs = null;
		try {
			if( dbcon == null ) dbcon = getDbConnection();
			DatabaseMetaData dbmeta = dbcon.getConnection().getMetaData();
			
			String [] types = { "SEQUENCE" };
			rs = dbmeta.getTables(null, null, null, types);
			
			while ( rs.next() ) {
				String name = rs.getString("TABLE_NAME");
				if ( seqname.equalsIgnoreCase( name) )
					return true;
			}
		} catch (SQLException e) {
			throw new MException( "db2014",
						"Viga tabelite nimede lugemisel baasist", null, null, e);
		} finally {
			try {
				if( rs != null ) rs.close();
			} catch (SQLException e) {
			}
//			if ( dbcon != null){
//			    Log.info( 99, "sequenceExist");
//			    dbcon.close();
//			}
		}

		return false;
	}	
	
	/**
	 * Gets the table primary key.
	 * 
	 * @param sTableName the s table name
	 * 
	 * @return the table primary key
	 * 
	 * @throws MException the m exception
	 */
	public String getTablePrimaryKey( String sTableName) throws MException
	{
//		DbConnection dbcon = null;
		try{
		    if( dbcon == null ) dbcon = getDbConnection();
			return (dbcon!=null)? dbcon.getTablePrimaryKey( sTableName): null;
		} finally {
		}
	}
	public ResultSet getTableKeys( String sTableName) throws MException
	{
		try{
		    if( dbcon == null ) dbcon = getDbConnection();
			return (dbcon!=null)? dbcon.getTableKeys(  dbdescr.getSchemaName(), sTableName): null;
		} finally {
		}
	}
	public ResultSet getTableKeys( String sSchemaName, String sTableName) throws MException
	{
		try{
		    if( dbcon == null ) dbcon = getDbConnection();
			return (dbcon!=null)? dbcon.getTableKeys( sSchemaName, sTableName): null;
		} finally {
		}
	}
	
	/**
	 * Supports pre key.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean supportsPreKey() throws MException
	{
	    if( dbcon == null ) dbcon = getDbConnection();
		return (dbcon!=null)? dbcon.supportsPreKey(): false;
	}
	
	/**
	 * Gets the pre key.
	 * 
	 * @param sTableName the s table name
	 * 
	 * @return the pre key
	 * 
	 * @throws MException the m exception
	 */
	public long getPreKey( String sTableName) throws MException
	{
		long lKey = 0;
		long lSTime = ( new Date()).getTime();
		try {
			if( dbcon == null ) dbcon = getDbConnection();
			if( dbcon != null) lKey = dbcon.getPreKey( sTableName);
			return lKey;
		} catch (Exception e) {
			throw new MException( "Katse uut võtit kätte saada ebaõnnestus"
					+ ", table='" + sTableName + "'" + "\n" + e.getMessage() );
		}finally{
			if( isDebug( 99)) log( "DB get key " + lKey + " time=" + ( ( new Date()).getTime() - lSTime) + "ms");
		}
	}
	
	/**
	 * Supports post key.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean supportsPostKey() throws MException
	{
//		DbConnection dbcon = null;
		try{
		    if( dbcon == null ) dbcon = getDbConnection();
			return (dbcon!=null)? dbcon.supportsPostKey(): false;
		} finally {
//			if( dbcon != null) dbcon.close();
		}
	}
	
	/**
	 * Gets the post key.
	 * 
	 * @param S the s
	 * 
	 * @return the post key
	 * 
	 * @throws MException the m exception
	 */
	public long getPostKey( Statement S) throws MException
	{
//		DbConnection dbcon = null;
		try{
		    if( dbcon == null ) dbcon = getDbConnection();
			return (dbcon!=null)? dbcon.getPostKey( S): 0;
		} finally {
//			if( dbcon != null) dbcon.close();
		}
	} 
	
	/**
	 * Checks for table.
	 * 
	 * @param tabname the tabname
	 * 
	 * @return true, if successful
	 */
	public boolean hasTable( String sTableName) 
	{
        try{
            if( dbcon == null ) dbcon = getDbConnection();
          	if( dbcon != null ) return dbcon.hasTable(  dbdescr.getSchemaName(), sTableName);
       }catch( MException E){
       } finally {
       }
        return false;
	}
	public boolean hasTable( String sSchemaName, String sTableName) 
	{
        try{
            if( dbcon == null ) dbcon = getDbConnection();
          	if( dbcon!=null ) return dbcon.hasTable(  sSchemaName, sTableName);
       }catch( MException E){
       } finally {
       }
        return false;
	}
	public boolean hasIndex( String sTableName, String sIndexName) 
	{
       try{
            if( dbcon == null ) dbcon = getDbConnection();
          	if( dbcon != null ) return dbcon.hasIndex(  dbdescr.getSchemaName(), sTableName, sIndexName);
       }catch( MException E){
       } finally {
       }
       return false;
	}
	public boolean hasColumn( String sTableName, String sColumnName) 
	{
		return hasColumn( dbdescr.getSchemaName(), sTableName, sColumnName);
	}
	public boolean hasColumn( String sSchemaName, String sTableName, String sColumnName) 
	{
        try{
            if( dbcon == null ) dbcon = getDbConnection();
          	if( dbcon!=null ) return dbcon.hasColumn( sSchemaName, sTableName, sColumnName);
       }catch( MException E){
       } finally {
       }
        return false;
	}
	public ResultSet getTables() throws MException 
	{
       try{
            if( dbcon == null ) dbcon = getDbConnection();
          	return ( dbcon!=null )? dbcon.getTables(  dbdescr.getSchemaName()): null;
       }catch( MException E){
       } finally {
       }
       return null;
	}
	public ResultSet getTables( String sSchemaName, String sTableName) throws MException 
	{
       try{
            if( dbcon == null ) dbcon = getDbConnection();
          	return ( dbcon!=null )? dbcon.getTables( sSchemaName, sTableName): null;
       }catch( MException E){
       } finally {
       }
       return null;
	}
	public boolean hasTableColumn( String tabname, String colname) 
	{
        try{
            if( dbcon == null ) dbcon = getDbConnection();
          	return ( dbcon!=null )? dbcon.hasColumn( tabname, colname): false;
       }catch( MException E){
       } finally {
       }
        return false;
	}
	public ResultSet getSchemas( String sDatabaseName) throws MException 
	{
       try{
            if( dbcon == null ) dbcon = getDbConnection();
          	return ( dbcon != null )? dbcon.getSchemas( sDatabaseName): null;
       }catch( MException E){
       } finally {
       }
       return null;
	}
	public ResultSet getDatabases() throws MException 
	{
       try{
            if( dbcon == null ) dbcon = getDbConnection();
          	return ( dbcon != null )? dbcon.getDatabases(): null;
       }catch( MException E){
       } finally {
       }
       return null;
	}
	
	
	
	
	/**
	 * Gets the user id.
	 * 
	 * @return the user id
	 */
	public int getUserID() {
		return (aSight != null && aSight.getUser() != null )? aSight.getUser().getUserID(): 0;
	}
	
	/**
	 * Gets the user name.
	 * 
	 * @return the user name
	 */
	public String getUserName() {
		return ( aSight != null)? aSight.getUserName(): null;
	}
/*	public void setUser( User User) {
		this.User = User;
	} */
    /**
 * Sets the codebase.
 * 
 * @param codebase the new codebase
 */
public void setCodebase( String codebase) {
        sCodebase = codebase;
    }
    
    /**
     * Sets the encoding.
     * 
     * @throws MException the m exception
     */
    public void setEncoding() throws MException
    {
    	if( dbdescr != null && dbcon != null ){
    		if( dbdescr.getEncoding() != null ){
    			exec( "SET NAMES '" + dbdescr.getEncoding() + "'");
    		}
    	}
    }
	
	/**
	 * Gets the sight.
	 * 
	 * @return the sight
	 */
	public Sight getSight() {
		return aSight;
	}
	
	/**
	 * Sets the sight.
	 * 
	 * @param sight the new sight
	 */
	public void setSight(Sight sight) {
		aSight = sight;
	}
	public boolean isUserLog( Sight aSight){ return aSight != null && aSight.getUser() != null;}	
	public void log( String sMsg)
	{
		if( Thread.currentThread() instanceof ThreadLog )
			(( ThreadLog)Thread.currentThread()).log( sMsg);
		else if( isUserLog( aSight) ) aSight.log( sMsg);
		else Log.info( sMsg);
	}
	public void log( int iDebug, String sMsg)
	{
		if( Thread.currentThread() instanceof ThreadLog )
			(( ThreadLog)Thread.currentThread()).log( iDebug, sMsg);
		else if( isUserLog( aSight) ) aSight.log( iDebug, sMsg);
		else Log.info( iDebug, sMsg);
	}
	public boolean isDebug( int iDebug)
	{
		if( aSight != null ) return aSight.isDebug( iDebug);
		else return Log.isDebug( iDebug);
	}
	public void log( Throwable aE)
	{
		if( isUserLog( aSight) ) aSight.log( aE);
		else Log.info( aE);
	}
	public String getError()
	{
		return  ( aSight != null )? aSight.getError(): null;
	}
	
	/**
	 * Sets the error.
	 * 
	 * @param sError the new error
	 */
	public void setError( String sError)
	{
		if ( aSight != null ) aSight.setError( sError);
	}
	
	/**
	 * Checks if is closed.
	 * 
	 * @return true, if is closed
	 */
	public boolean isClosed() // sellel on m�te �ksnes vahetult p�rast commit-i
	{
		return dbcon == null;
	}
	
	/**
	 * Call geometry.
	 * 
	 * @param aP the a p
	 * @param sFromP the s from p
	 * @param sToP the s to p
	 * 
	 * @return the point2 d. double
	 * 
	 * @throws MException the m exception
	 */
	public Point2D callGeometry( Point2D P, int iFromP, int iToP)  throws MException 
	{
		return callGeometry( P, Integer.toString( iFromP), Integer.toString( iToP));
	}
	public Point2D callGeometry( Point2D aP, String sFromP, String sToP)
	{
		final String sCallS = "select astext( transform( setsrid( 'POINT(";
		final String sCallE = ")'::geometry, " + sFromP + "), " + sToP + "))";
		String sQuery = sCallS + aP.getX() + " " + aP.getY() + sCallE;
		ResultSet RS = null;
		try{
			RS = execSelect( sQuery);
	   	    if( RS != null && RS.next() ){
 				double [] D = _getGeometry( RS.getString( 1)); //"transform"));
				if( D != null && D.length/2 > 0 ){ 
					return  new Point2D.Double( D[ 0], D[ 1]);
				}
     	    }else log( "callGeometry statment error: no results");
	 	}catch (Exception E){
    	    log( "callGeometry error: " + E.getMessage());
    	}finally{
			close( RS);
    	}
    	return null;
	}
	public Point2D[] callGeometry( Point2D[] aPs, String sFromP, String sToP) 
	{
		return callGeometry( aPs, Integer.valueOf( sFromP), Integer.valueOf( sToP));
	}
	public Point2D [] callGeometry( Point2D [] Points, int iFromP, int iToP) 
	{
	    if( Points == null ) return null;
		int iNum = Points.length; 
		if( iNum > 1 ){
			StringBuffer sCoord = new StringBuffer( "select astext( transform( setsrid( 'LINESTRING(");
			for( int i = 0; i < iNum; ++i){
				if( i > 0 ) sCoord.append( ",");
				Point2D aP = Points[ i];
				sCoord.append( aP.getX());
				sCoord.append( " ");
				sCoord.append( aP.getY());
			}
			sCoord.append( ")'::geometry, ");
			sCoord.append( iFromP);
			sCoord.append( "), ");
			sCoord.append( iToP);
			sCoord.append( "))");
			ResultSet RS = null;
			try{
				RS = execSelect( sCoord.toString());
		   	    if( RS != null && RS.next() ){
	 				double [] dP = _getGeometry( RS.getString( 1)); 
	 				if( dP.length == iNum*2){
	 					Point2D [] aPs = new Point2D[ iNum];
	 					for( int i = iNum; --i >= 0; ) aPs[ i] = new Point2D.Double( dP[ i*2], dP[ i*2+1]);
	 					return aPs;
	 				}
	 				log( "callGeometry dimension error: " + dP.length + " != 2*" + iNum);
	     	    }else log( "callGeometry statment error: no results");
		 	}catch (Exception E){
	    	    log( "callGeometry error: " + E.getMessage());
	    	}finally{
				close( RS);
	    	}
		}
	    return null;
	}		
	public double getDistance( Point2D aP0, int iP0, Point2D aP1, int iP1)  
	{
		StringBuffer aCall = new StringBuffer( "select distance( setsrid( 'POINT(");
		aCall.append( aP0.getX());
		aCall.append( " ");
		aCall.append( aP0.getY());
		aCall.append( ")'::geometry, ");
		aCall.append( iP0);
		aCall.append( "), setsrid( 'POINT(");	
		aCall.append( aP1.getX());
		aCall.append( " ");
		aCall.append( aP1.getY());
		aCall.append( ")'::geometry, ");
		aCall.append( iP1);
		aCall.append( "))");	
		ResultSet RS = null;
		try{
			RS = execSelect( aCall.toString());
	   	    if( RS != null && RS.next() ){
	   	    	return RS.getDouble( 1); 
     	    }else log( "getDistance error: no results");
	 	}catch (Exception E){
    	    log( "getDistance error: " + E.getMessage());
    	}finally{
			close( RS);
    	}
    	return -1;
	}	
	/**
	 * _get geometry.
	 * 
	 * @param sCoord the s coord
	 * 
	 * @return the double[]
	 * 
	 * @throws MException the m exception
	 */
	public double[] _getGeometry( String sCoord)  throws MException 
	{
		if( sCoord != null && sCoord.length() > 0 ){
			int iComma = sCoord.indexOf( '(', 0);
			if( iComma < 0 ){
				if( sCoord.indexOf( ' ') < 0 ){ // binary form
					
				}
				iComma = 0;
			}else if( sCoord.charAt( iComma+1) == '(' ) ++iComma;
			int iStart = iComma;
			int iNum = 0;
			
			for( ;; ){
				++iNum;
				iComma = sCoord.indexOf( ',', ++iComma);
				if( iComma < 0 ) break;
			}
			if( iNum > 0 ){ 
			    iNum *= 2;
				double[] P = new double[ iNum];
				
				for( int i=0; i<iNum; ){
					int iEnd = sCoord.indexOf( ' ', ++iStart); 
					P[ i++] = Double.parseDouble( sCoord.substring( iStart, iEnd));
					iStart = iEnd + 1;
					iComma = sCoord.indexOf( ',', iStart);
					if( iComma < 0 ) iComma = sCoord.indexOf( ')', iStart);
					iEnd = sCoord.indexOf( ' ', iStart);
					if( iEnd < 0 ){
						iEnd = iComma;
						if( iEnd < 0 ){
							P[ i++] = Float.parseFloat( sCoord.substring( iStart));
							break;
						}
					}else if( iEnd > iComma && iComma >= 0 ) iEnd = iComma; 
					P[ i++] = Double.parseDouble( sCoord.substring( iStart, iEnd));
					if( iComma < 0 ) break;
					iStart = iComma;
				}
				return P;
			}
		}
		return null;
	}
	
	/**
	 * Sets the encoding.
	 * 
	 * @param sEncoding the new encoding
	 */
	public void setEncoding(String sEncoding) {
		this.sEncoding = sEncoding;
	}
	
	/**
	 * Gets the encoding.
	 * 
	 * @return the encoding
	 */
	public String getEncoding() {
		return sEncoding;
	}
	public DOMData getXML( String sTableName, String sWhere, String sOrder)
	{
		long lSTime = 0;
		DOMData aDOMData = null;
		StringBuffer aCall = new StringBuffer( "select * from ");
		aCall.append( sTableName);
		if( sWhere != null){
			aCall.append( " where ");
			aCall.append( sWhere);
		}
		if( sOrder != null){
			aCall.append( " order by ");
			aCall.append( sOrder);
		}
		String sQuery = aCall.toString();
		
		if( isDebug( 1)){
			log( sQuery);
			lSTime = ( new Date()).getTime();
		}
		
    	Statement aSt = null;
    	ResultSet aRS = null;
    	try
    	{
    	    aSt = getStatement( true);
    	    aSt.execute( sQuery);
 	       	aRS = aSt.getResultSet();
 	       	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 	       	DocumentBuilder builder        = factory.newDocumentBuilder();
 	       	Document doc                   = builder.newDocument();

 	       	Element results = doc.createElement( "data");
 	       	doc.appendChild(results);

 	       	ResultSetMetaData rsmd = aRS.getMetaData();
 	       	int colCount           = rsmd.getColumnCount();
 	       	while( aRS.next())
 	       	{
 	       		Element row = doc.createElement( sTableName);
 	       		results.appendChild( row);

 	       		for (int i = 1; i <= colCount; i++)
 	       		{
 	       			String columnType = rsmd.getColumnTypeName( i);
 	       			if( !columnType.equalsIgnoreCase( "geometry") && !columnType.equalsIgnoreCase( "bytea" )){
 	 	       			String columnName = rsmd.getColumnName(i);
 	 	       			Element node      = doc.createElement( columnName);
	 	       			node.setAttribute( "type", columnType);
 	 	       			
	 	       			if( !columnType.equalsIgnoreCase( "text")){
	 	 	       			int iSize = rsmd.getColumnDisplaySize( i);
	 	 	       			node.setAttribute( "size", Integer.toString( iSize));
	 	       			}
 	 	       			Object value      = aRS.getObject(i);
 	 	       			if( value != null) node.appendChild(doc.createTextNode( value.toString()));

 	 	       			row.appendChild( node);
 	       			}
 	       		}
    	    }
 	       	aDOMData = new DOMData( doc);
 	       	if( isDebug( 1)) log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms");
 	    }
    	catch (Exception E)
    	{
    	    log( "getXML error: " + E.getMessage());
    	}finally{
			try {
				if( aRS != null ) aRS.close();
	        	if( aSt != null ) aSt.close();
			} catch (SQLException e) {
			}
    	}
		return aDOMData;
	}
	public ResultSet getMetaData( String sTableName) throws MException
	{
		String sSchemaName = null;
		int i = sTableName.indexOf( '.');
		if( i > 0){
			sSchemaName = sTableName.substring( 0, i);
			sTableName = sTableName.substring( ++i);
		}else{
			sSchemaName = dbdescr.getSchemaName();
			if( sSchemaName == null ) sSchemaName = "public";
		}
		return getMetaData( sSchemaName, sTableName);
	}	
	public ResultSet getMetaData( String sSchemaName , String sTableName) throws MException
	{
		if( dbcon == null ) dbcon = getDbConnection();
		return ( dbcon != null )? dbcon.getMetaData( sSchemaName, sTableName): null;
	}	
/*	public LargeObject getLO( InputStream aIn)
	{
		LargeObject aLO = null;
		try{
			if( dbcon == null ) dbcon = getDbConnection();
			LargeObjectManager aLOM = ((org.postgresql.PGConnection)dbcon).getLargeObjectAPI();
			long lOID = aLOM.createLO();
			aLO = aLOM.open( lOID, LargeObjectManager.WRITE);
			byte buf[] = new byte[2048];
			int s;//, e = 0;
			while ((s = aIn.read(buf, 0, 2048)) > 0){
				aLO.write( buf, 0, s);
//				e += s;
			}
		}catch( Exception aE ){
			Log.log( aE);
		}finally{
			try{
				if( aLO != null) aLO.close();
			}catch( SQLException aE ){
			}
		}
		return aLO;
	}*/
	public void setAutoCommit( boolean b) throws MException
	{
		if( dbcon == null ) dbcon = getDbConnection();
		try{
			if( dbcon != null ) dbcon.con.setAutoCommit( b);
		}catch( SQLException aE ){
		}	
	}
	public String getDatabaseName(){ return ( dbdescr != null)? dbdescr.getDbname(): null;}
	public String getName(){ return ( dbdescr != null)? dbdescr.getDbclass(): null;}
	public void setDatabaseName( String sName)
	{ 
		if( sName != null ){
			if( sName.equalsIgnoreCase( getDatabaseName())) return;
		}
		dbdescr.setName( sName);
		if( dbcon != null ){
			try {
				dbcon.close();
			} catch (MException e) {
			}
			dbcon = null;
		}
	}
	public void setSchema( String sName)
	{ 
		if( sName != null ){
			if( sName.equalsIgnoreCase( getSchemaName())) return;
		}
		dbdescr.setSchemaName( sName);
		if( dbcon != null ) dbcon.setCatalog( sName);
	}
	public String getSchemaName()
	{
		return (dbdescr != null && dbdescr.getSchemaName() != null )? dbdescr.getSchemaName(): "public";
	}
	public String getGeometryType( String sTableName, String sColumnName)
	{
    	StringBuffer aQuery = new StringBuffer( "SELECT type FROM geometry_columns where f_table_catalog = '");
    	aQuery.append( getDatabaseName());
    	aQuery.append( "' and f_table_schema='");
    	aQuery.append( getSchemaName());
    	aQuery.append( "' and f_table_name='");
    	aQuery.append( sTableName); 
    	aQuery.append( "' and f_geometry_column='");
    	aQuery.append( sColumnName);
    	aQuery.append( "'");

    	ResultSet aRS = null;
    	try
    	{
 			aRS = execSelect( aQuery.toString());
			if( aRS != null && aRS.next() ){
				return aRS.getString( 1);
	 	    }
    	}
	    catch (Exception aE)
    	{
    		log( aE);
    	}finally{
			close( aRS);
    	}
		return null;
	}
	public void setTimeoutXXX( int iTimeout)
	{ 
		dbcon.setTimeoutXXX( iTimeout);
	}
}

