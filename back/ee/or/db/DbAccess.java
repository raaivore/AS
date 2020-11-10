package ee.or.db;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.UUID;
import java.awt.geom.Point2D;
import java.awt.*;
import java.nio.*;

import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OraclePreparedStatement;
import ee.or.is.*;
import ee.or.geom.*;
import ee.or.gis.Projection;


/**
 * The Class DbAccess.
 * 
 * @author ta
 * 
 * Tegemist ei ole Access andmebaasi liidesega ega ole ka mingil muul moel seotud Access baasiga.
 * (sorry MS)
 * DbAccess on hoopis Database Access Object - ehk siis objekt mis teenindab p�ringuid.
 * Mingit erilist imet DbAccess ei tee - eesm�rk on vaid teha p�ringute t�itmine
 * v�imalikult mugavaks.
 * P�hiliselt vahendame sql.Statement ja sql.ResultSet teenuseid.
 */
public class DbAccess{

	//	Kas peaks olema eraldi DbSelect ja DbUpdate objektid??
	// Siis ei oleks DbUpdate korral osasid muutujaid ja meetodeid vaja.
	// Ja vastupidi - DbSelect j�lle ei omaks neid mis on DbUpdate puhul vajalikud.
	// Ka m�ned kontrollid j��ksid siis �ra.

	/** The Db. */
	public  Database Db = null;
	
	public Sight getSight() { return Db.getSight();}

//	private DbConnection dbcon = null;
	/** The statement. */
	private Statement	statement = null;
	
	/** The result. */
	private ResultSet	result = null;	// v�ib olla ka null - n�iteks execUpdate() korral....
	public ResultSet getResultSet(){ return result;}
	
//	private boolean bDbClose;
	/** The s s query. */
	private String		sSQuery;			// J�tame meelde viimase query
	
	/** The s u query. */
	private String		sUQuery;			// J�tame meelde viimase query
	public String getQuerySet(){ return sUQuery;}
	public void setQuerySet( String sUQuery){	this.sUQuery = sUQuery;}
	
	/** The s values. */
	private String		sValues;
	public String getQueryValues(){ return sValues;}
	public void setQueryValues( String sValues){ this.sValues = sValues;}

	/** The s table name. */
	private String 	sTableName;
	
	/** The s key name. */
	private String 	sKeyName;
	
	/** The i key type. */
	int iKeyType = 0;
	
	/** The b start. */
	private boolean 	bStart;
	
	/** The b update. */
	private boolean 	bUpdate;
	
	/** The b eof. */
	private boolean	bEOF;
	
	/** The Foreign keys. *///
//	private ArrayList 	ForeignKeys = null;
	
	/** The i foreign keys. */
//	int 	iForeignKeys;
	
	/** The P key. */
	private Object     PKey = null;
	
	/** The b first. */
//	private boolean bFirst = true;
	
	/** The s projection. */
//	private String sProjection = null;	

	private int iFetchSize = 0;
	public int getFetchSize() {	return iFetchSize;}
	public void setFetchSize( int iFetchSize){ this.iFetchSize = iFetchSize;}
	
/*	public DbAccess( DbDescr Db) throws MException
	{
		this( new Database( Db));
		bDbClose = true;
	} */
/**
 * Instantiates a new db access.
 * 
 * @param Db the db
 * @param sTableName the s table name
 * 
 * @throws MException the m exception
 */
	public DbAccess( Database Db, String sTableName ) throws MException
	{
		this( Db);
		setTable( sTableName);
	}
	
	/**
	 * Instantiates a new db access.
	 * 
	 * @param Db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbAccess( Database Db ) throws MException
	{
		if ( Db == null) throw new MException("Db on null");
		this.Db = Db;
	
//		statement = Db.getStatement();
//			bDbClose = false;
	} 
	
	/**
	 * Instantiates a new db access.
	 * 
	 * @param DbA the db a
	 * 
	 * @throws MException the m exception
	 */
	public DbAccess( DbAccess DbA) throws MException
	{
		this( DbA.Db);
//		bFirst = false;
//		dbcon = DbA.dbcon;
	} 

/**
 * Sulgeme statement'i
 * Kui �henduse loomisel loodi ka Connection, siis sulgeme ka selle.
 */
	private void _reset()
	{
		try {
			if( result != null ){ result.close(); result = null;}
			if( statement != null ){ statement.close(); statement = null;}
		} catch( Exception e ){
			new MException( "yld2040", "Viga reset's ", null, null, e);
		}
	}
	
	/**
	 * _close.
	 */
	public void _close()   
	{
//		try {
			try {
				_reset();
				PKey = null;
//				if( bFirst && dbcon != null ){ dbcon.close(); dbcon = null;}
			} catch ( Exception e ){
				new MException( "yld2040", "Viga close's ", null, null, e);
			}
//			if ( Db != null /* && bDbClose */ ){ Db.close(); Db = null;}
//		} catch (MException e) {
//		}
	}
	
	/**
	 * Close.
	 */
	public void close()   
	{
		_close();
	}	
/*	public void refresh()  throws MException
	{
		if( Db != null ) Db.refresh();
	} */
	/**
 * Sets the table.
 * 
 * @param sTableName the new table
 * 
 * @throws MException the m exception
 */
public void setTable( String sTableName) throws MException
	{
/*		try {
			if( result != null ){ result.close(); result = null;}
		} catch (SQLException e) {
			new MException( "yld2040", "setTable '" + sTableName + "'", null, null, e);
		}
		PKey = null;
//		iID = 0; */
		if( this.sTableName != sTableName ){
//			iOID = 0;
			this.sTableName = sTableName;
//			sKeyName = getIDName();
			setTable( sTableName, getIDName()); 
		}
	}
	
	/**
	 * Sets the table.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * 
	 * @throws MException the m exception
	 */
	public void setTable( String sTableName, String sKeyName) throws MException
	{
		_reset();
		PKey = null;
		this.sTableName = sTableName;
		this.sKeyName = sKeyName;
	}
	
	/**
	 * Gets the table.
	 * 
	 * @return the table
	 */
	public String getTable()
	{
		return sTableName;
	}
	
	/**
	 * Gets the key where.
	 * 
	 * @return the key where
	 */
	public String getKeyWhere()
	{
		if( sKeyName != null && PKey != null ){
			if( PKey instanceof Integer )
				return sKeyName + "=" + (( Integer)PKey).intValue();
			else if( PKey instanceof String )
				return sKeyName + "='" + (( String)PKey) + "'";
			else if( PKey instanceof Long )
				return sKeyName + "=" + (( Long)PKey).longValue();
		}
		return null;
	}
	
	/**
	 * Select by key.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	private final boolean selectByKey() throws MException 
	{
		if( result != null ) return true;
		if( sTableName != null && sTableName.length() > 0 && sKeyName != null && PKey != null ){
//			iID > 0 ){
//			String sKeyName = getIDName();
//			if( sKeyName != null )
			return select( sTableName, getKeyWhere(), sKeyName);
		}
//		return result != null;
		return false;
	}
	
	/**
	 * Select by id.
	 * 
	 * @param iID the i id
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public final boolean selectByID( int iID) throws MException 
	{
		setID( iID);
		return selectByKey();
	} 
	
	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 * 
	 * @throws MException the m exception
	 */
	public int getID() throws MException
	{
		if( PKey != null && PKey instanceof Integer )
 			return (( Integer)PKey).intValue();
		return -1;

//		if( iID <= 0 ){
//			String sKeyName = getIDName();
//			iID = (sKeyName != null)? getInt( sKeyName): 0;	
//		}
//		return ( int)iID;
//		String sKeyName = getIDName();
//		return (sKeyName != null)? getInt( sKeyName): 0;	
//		String s = getString( "id");
//		if( s == null ) s = getString( 0);
//		return (s != null)? Integer.parseInt( s): 0;
	}
	
	/**
	 * Gets the key.
	 * 
	 * @return the key
	 * 
	 * @throws MException the m exception
	 */
	public Object getKey() throws MException 
	{
		if( PKey == null && getKeyName() != null ){
			if( iKeyType == 0 ) iKeyType = Db.getColumnType( sTableName, sKeyName);
// LogOR.info( sTableName + " " + sKeyName + " " + Integer.toString( iKeyType));
			switch( iKeyType ){
				case 0 : // see on k�ll jama , kuid oracle?
				case java.sql.Types.INTEGER :
				case java.sql.Types.SMALLINT :
				case java.sql.Types.DECIMAL :
				case java.sql.Types.NUMERIC :
					return (PKey = getInteger( sKeyName));	
				case java.sql.Types.CHAR :
				case java.sql.Types.VARCHAR :
					return (PKey = getString( sKeyName));
			}
		}
		return PKey;
	}
	
	/**
	 * Sets the iD.
	 * 
	 * @param iID the new iD
	 * 
	 * @throws MException the m exception
	 */
	public void setID( int iID) throws MException
	{
//		result = null;

		iKeyType = java.sql.Types.INTEGER;
		setKey( new Integer( iID));
	}
	
	/**
	 * Sets the key.
	 * 
	 * @param PKey the new key
	 * 
	 * @throws MException the m exception
	 */
	public void setKey( Object PKey) throws MException
	{
//		try {
		    _reset();
//			if( result != null ){ result.close(); result = null;}
//			if( statement != null ){ statement.close(); statement = null;} 
//		} catch (SQLException e) {
//			throw new MException( "yld2040", null, null, null, e);
//		}
		this.PKey = PKey;
	}
/*	public int getOID()
	{
		return iOID;
	} */
	/**
 * Gets the key name.
 * 
 * @return the key name
 * 
 * @throws MException the m exception
 */
public String getKeyName() throws MException
	{
		if( Db == null || sTableName == null ) return null;
		if( sKeyName == null ){
			sKeyName = Db.getTablePrimaryKey( sTableName);
//			sKeyName = dbcon.getTablePrimaryKey( sTableName);
			iKeyType = 0;
		}
//		if( sTableName.compareTo( "aadress") == 0 ) return "hkood";
		return sKeyName; 
	}
	
	/**
	 * Gets the iD name.
	 * 
	 * @return the iD name
	 * 
	 * @throws MException the m exception
	 */
	public String getIDName() throws MException
	{
		return getKeyName();
	}
	
	/**
	 * Checks if is primary key.
	 * 
	 * @param sAttrName the s attr name
	 * 
	 * @return true, if is primary key
	 * 
	 * @throws MException the m exception
	 */
	public boolean isPrimaryKey( String sAttrName) throws MException
	{
//		if( Db == null || sTableName == null ) return false;
		String pkey = getIDName();
		return ( pkey != null)? pkey.equalsIgnoreCase( sAttrName): false;
//		return Db.getTablePrimaryKey( sTableName).equalsIgnoreCase( sAttrName);
//		return Db.isTablePrimaryKey( sTableName, sAttrName);
	}
/*	public DbConnection getDbConnection()
	{
		return dbcon;
	}	*/
	/**
 * J�rgnevat meetodit v�ib t�ita korduvalt - anda j�lle uue query ja tuld...
 * Tulenevalt sellest hoiame statement-i ikka avatuna.
 * Ja sulgeda tuleb statement eraldi close() kaudu siis kui antud DbAccess objekti
 * enam ei kasutata.
 * 
 * @param sTableName the s table name
 * @param sKeyName the s key name
 * @param sWhere the s where
 * @param sOrder the s order
 * 
 * @return true, if select
 * 
 * @throws MException the m exception
 */
/*	public void execSelect( String query) throws MException {
		this.query = query;
		try {
			sTableName = null;
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			String msg = "Viga p�ringu '" + query + "' t�itmisel";
			throw new MException( "yld2040", msg, null, null, e);
		}
		
	} */
	public boolean select( String sTableName, String sKeyName, String sWhere, String sOrder) throws MException
	{
		setTable( sTableName, sKeyName);
		String sQuery = "SELECT * FROM " + Db.getTablePro() + sTableName + 
			((sWhere != null)? " WHERE " + sWhere: "") +
			((sOrder != null)? " ORDER BY " + sOrder: ""); // + ";");
		return select( sQuery);
	}
	
	/**
	 * Select.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * @param sAttr the s attr
	 * @param Value the value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean select( String sTableName, String sKeyName, String sAttr, java.sql.Date Value) throws MException
	{
		return select( sTableName, sKeyName, sAttr + "=" + getSQLDateWhere( Value), ( String)null);
	}
	
	/**
	 * Select.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * @param sAttr the s attr
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean select( String sTableName, String sKeyName, String sAttr, int iValue) throws MException
	{
		return select( sTableName, sKeyName, sAttr + "=" + iValue, ( String)null);
	}
	
	/**
	 * Select.
	 * 
	 * @param sAttr the s attr
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean select( String sAttr, int iValue) throws MException
	{
		return select( sTableName, sKeyName, sAttr + "=" + iValue, ( String)null);
	}
	
	/**
	 * Select.
	 * 
	 * @param sTableName the s table name
	 * @param sWhere the s where
	 * @param sOrder the s order
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean select( String sTableName, String sWhere, String sOrder) throws MException
	{
		setTable( sTableName);
//		boolean bRet = false;
		String sQuery = "SELECT * FROM " + Db.getTablePro() + sTableName + 
			((sWhere != null)? " WHERE " + sWhere: "") +
			((sOrder != null)? " ORDER BY " + sOrder: ""); // + ";");
		return select( sQuery);

/*		this.sTableName = sTableName;
		try {
			result = statement.executeQuery( query);
			if( result != null ) bRet = next();
			return bRet;
		} catch (SQLException e) {
			String msg = "Viga p�ringu '" + query + "' t�itmisel";
			throw new MException( "yld2040", msg, null, null, e);
		} */
	}
	
	/**
	 * Checks if is exist.
	 * 
	 * @param sTableName the s table name
	 * @param sWhere the s where
	 * 
	 * @return true, if is exist
	 */
	public boolean isExist( String sTableName, String sWhere) 
	{
		try {
            String sQuery = "SELECT * FROM " + Db.getTablePro() + sTableName + 
            	((sWhere != null)? " WHERE " + sWhere: "");
            return select( sQuery);
        } catch (MException E) {
        }
        return false;
	}
	
	/**
	 * Pre select.
	 * 
	 * @param sQuery the s query
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean preSelect( String sQuery) throws MException
	{
		bEOF = true;
		sSQuery = new String( sQuery);
	    Db.log( 5, sQuery);
		_reset();
		PKey = null;
		try {
			statement = Db.getPreStatement( sQuery);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	/**
	 * Pre set int.
	 * 
	 * @param i the i
	 * @param iValue the i value
	 */
	public void preSetInt( int i, int iValue)
	{
		try {
			(( PreparedStatement)statement).setInt( i, iValue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pre set string.
	 * 
	 * @param i the i
	 * @param sValue the s value
	 */
	public void preSetString( int i, String sValue)
	{
		try {
			(( PreparedStatement)statement).setString( i, sValue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Exec pre.
	 * 
	 * @return true, if successful
	 */
	public boolean execPre()
	{
		try {
			if( statement != null ){
				result = (( PreparedStatement)statement).executeQuery();
				return next();
			}
		} catch( MException e) {
		} catch( Exception e) {
			String msg = "Viga p�ringu t�itmisel";
			new MException( "yld2040 ", msg, null, null, e);
		}
		try {
            if( statement != null ) statement.close();
            statement = null;
        } catch (SQLException E) {
        }
		return false;
	}
	
	/**
	 * Select.
	 * 
	 * @param sQuery the s query
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean select( String sQuery) throws MException
	{
		long lSTime = ( new Date()).getTime();
		bEOF = true;
		sSQuery = new String( sQuery);
	    Db.log( 5, sQuery);
//		for( int i=2; --i>=0;){
			try {
				_reset();
				PKey = null;
				statement = Db.getStatement( true); 
				if( iFetchSize > 0 ) statement.setFetchSize( iFetchSize);
				if( statement != null ){
					result = statement.executeQuery( sQuery);
					return next();
				}
			} catch( MException e) {
				try {
		            if( statement != null ) statement.close();
		            statement = null;
		        } catch ( Exception E) {
		        }
			} catch( Exception aE) {
//				String msg = "Viga päringu '" + sQuery + "' täitmisel";
				String sErr = aE.getMessage();
				if( sErr.indexOf( "canceling") >= 0 ) return false;
				Db.log( sErr);
				try {
		            if( statement != null ) statement.close();
		            statement = null;
		        } catch ( Exception E) {
		        }
			}finally{
				lSTime = ( new Date()).getTime() - lSTime;
				if( Db.isDebug( 1)) Db.log( "DB time=" + lSTime + "ms");
			}
//			lSTime = ( new Date()).getTime();
			Db.close();
//		}
		return false;
	}
	
	/**
	 * ResultSet.next() wrapper
	 * 
	 * @return true, if next
	 * 
	 * @throws MException the m exception
	 */
	public boolean next() throws MException {
		try {
			PKey = null;
			if( result != null && result.next() ){
				getKey();
				bEOF = false;
				return true;
			}else{
				bEOF = true;
				return false;
			}
		} catch (SQLException e) {
			String msg = "Viga järgmisele reale liikumisel, päring '" + sSQuery + "'";
			throw new MException( "yld2040", msg, null, null, e);
		}
	}
	/**
	 * Goto top.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean gotoTop() throws MException {
		try {
			PKey = null;
			if( result != null ){
				result.beforeFirst();
//				bBOF = true;
				bEOF = false;
				return true;
			}else{
//				iID = 0;
				bEOF = true;
				if( sTableName != null && sTableName.length() > 0 ){
					return select( sTableName, null, null);
				}else
					return false;
			}
		} catch (SQLException e) {
			String msg = "Viga järgmisele reale liikumisel, päring: " + sSQuery + ";";
			Db.log( msg + " \n " + e.getMessage());
			throw new MException( msg, e);
		}
	}
	
	/**
	 * _get string.
	 * 
	 * @param colname the colname
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public final String _getString( String colname) throws MException 
	{
		if( colname == null /* || result == null */ ) return null;
		try {
			if( selectByKey() ){
				String s = result.getString( colname);
				if( s != null ) return new String( s);
			}
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		}
		return null;
	}
	
	/**
	 * _get string.
	 * 
	 * @param iCol the i col
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public final String _getString( int iCol) throws MException 
	{
		try {
			if( selectByKey() ){
				String s = result.getString( iCol);
				if( s != null ) return new String( s);
			}
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		}
		return null;
	}
	
	/**
	 * Gets the bytes.
	 * 
	 * @param colname the colname
	 * 
	 * @return the bytes
	 */
	public final byte [] getBytes( String colname)
	{
	    byte [] bs = null;
	    try {
	        bs = result.getBytes( colname);
        } catch ( Exception E) {
            Db.log( E);
        } 
        if( bs == null ){
	        int ch;
	        ByteBuffer buf = ByteBuffer.allocate( 100);
	        try {
                Reader reader = result.getCharacterStream ( colname);
                if( reader == null ) return null;
                while ( (ch = reader.read()) >= 0 ) buf.put( ( byte)ch);
                bs = buf.array();
            } catch (Exception E1) {
                Db.log( E1);
            }
//	        result.getCharacterStream();
        }
        return bs;
	}
	public final byte [] getBytes( int iCol)
	{
	    byte [] bs = null;
	    try {
	        bs = result.getBytes( iCol);
        } catch ( Exception E) {
            Db.log( E.getMessage());
        } 
        if( bs == null ){
	        int ch;
	        ByteBuffer buf = ByteBuffer.allocate( 100);
	        try {
                Reader reader = result.getCharacterStream ( iCol);
                while ( (ch = reader.read()) >= 0 ) buf.put( ( byte)ch);
                bs = buf.array();
            } catch (SQLException E1) {
                Db.log( E1.getMessage());
            } catch (IOException E1) {
                Db.log( E1.getMessage());
            }
//	        result.getCharacterStream();
        }
        return bs;
	}
	
	/**
	 * Gets the string.
	 * 
	 * @param colname the colname
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public final String getString( String colname) throws MException 
	{
		if( colname == null /* || result == null */ ) return null;
		try {
			String sCodeBase = Db.getCodeBase();
//			String sCodeBase = dbcon.getCodeBase();
			if( selectByKey() ){
				if( sCodeBase == null ){
					String s = result.getString( colname);
					return ( s != null )? new String( s): "";
				}else{
					byte [] bs = result.getBytes( colname);
					return ( bs != null )? new String( bs, sCodeBase): "";
				}
			}
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		} catch (UnsupportedEncodingException e) {
//			throw new MException( "yld2060", "Viga kodeerimisel", null, null, e);
		}
		return null;
	}
	public final String getStringByType( String colname) throws MException 
	{
		if( colname == null /* || result == null */ ) return null;
		try {
			String sCodeBase = Db.getCodeBase();
//			String sCodeBase = dbcon.getCodeBase();
			if( selectByKey() ){
				int iCol = result.findColumn( colname);
				ResultSetMetaData aMeta = result.getMetaData();
				aMeta.getColumnType( iCol);
				String columnType = aMeta.getColumnTypeName( iCol);
				if( columnType.indexOf( "timestamp") >= 0){
					return GlobalDate.getDateTimeString( result.getString( iCol));					
				}else if( sCodeBase == null ){
					String s = result.getString( iCol);
					return ( s != null )? new String( s): "";
				}else{
					byte [] bs = result.getBytes( iCol);
					return ( bs != null )? new String( bs, sCodeBase): "";
				}
			}
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		} catch (UnsupportedEncodingException e) {
//			throw new MException( "yld2060", "Viga kodeerimisel", null, null, e);
		}
		return null;
	}
	
	/**
	 * Gets the string.
	 * 
	 * @param iCol the i col
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public final String getString( int iCol) throws MException 
	{
		try {
/*			if( result == null && iOID > 0 ){
				select( sTableName, "oid=" + Integer.toString( iOID), null);
			}
			return (result != null)? result.getString( iCol): null; */
			return selectByKey()? result.getString( iCol): null;
		} catch (SQLException e) {
			String msg = "Viga columni '" + iCol + "' v��rtuse k�simisel, p�ring '" + sSQuery + "'";
			throw new MException( "yld2040", msg, null, null, e);
		}
	}
	
	/**
	 * Gets the boolean.
	 * 
	 * @param colname the colname
	 * 
	 * @return the boolean
	 * 
	 * @throws MException the m exception
	 */
	public final boolean getBoolean( String colname) throws MException {
		String s = _getString( colname);
		return s != null && (s.equalsIgnoreCase( "t") || s.equalsIgnoreCase( "1"));
	}
	
	/**
	 * Gets the int.
	 * 
	 * @param colname the colname
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public final int getInt( String colname) throws MException {
		String s = _getString( colname);
		if( s == null ) return 0;
		s = s.trim();
		if( s.length() == 0 ) return 0; // 25.01.2005 v�imaldab saada int'i ka t�hjalt tekstiv�ljalt
		try {
			return (s != null)? Integer.parseInt( s): 0;
		} catch (NumberFormatException e) {
			try {
				return ( int)Math.round( Double.parseDouble( s));
			} catch (NumberFormatException e1) {
				return 0;
			}
		}
	}
	
	/**
	 * Checks if is null.
	 * 
	 * @param colname the colname
	 * 
	 * @return true, if is null
	 * 
	 * @throws MException the m exception
	 */
	public final boolean isNull( String colname) throws MException {
		return _getString( colname) == null;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @param colname the colname
	 * 
	 * @return true, if is empty
	 * 
	 * @throws MException the m exception
	 */
	public final boolean isEmpty( String colname) throws MException 
	{
		String s = _getString( colname);
		return s == null || s.length() == 0;
	}
	
	/**
	 * Gets the integer.
	 * 
	 * @param colname the colname
	 * 
	 * @return the integer
	 * 
	 * @throws MException the m exception
	 */
	public final Integer getInteger( String colname) throws MException {
		String s = _getString( colname);
		if ( s != null) s = s.trim();
		return (s != null)? new Integer( s): null;
	}
	
	/**
	 * Gets the long.
	 * 
	 * @param colname the colname
	 * 
	 * @return the long
	 * 
	 * @throws MException the m exception
	 */
	public final long getLong( String colname) throws MException {
		String s = _getString( colname);
		try{
			return (s != null)? Long.parseLong( s): 0;
		} catch (NumberFormatException e) {
			try {
				return ( int)Math.round( Double.parseDouble( s));
			} catch (NumberFormatException e1) {
				return 0;
			}
		}
	}
	
	/**
	 * Gets the float.
	 * 
	 * @param colname the colname
	 * 
	 * @return the float
	 * 
	 * @throws MException the m exception
	 */
	public final double getFloat( String colname) throws MException {
		String s = _getString( colname);
		return (s != null)? Float.parseFloat( s): 0;
	}
	
	/**
	 * Gets the double.
	 * 
	 * @param colname the colname
	 * 
	 * @return the double
	 */
	public final double getDouble( String colname){ // throws MException {
		try {
			String s = _getString( colname);
			return (s != null)? Double.parseDouble( s): 0;
		} catch (Exception e) {
			return 0.0;
		}
	}
	public final double getMoney( String colname){ // throws MException {
		try {
			String s = _getString( colname);
			if( s != null && s.charAt( 0) == '$' ) return Double.parseDouble( s.substring( 1));
			return (s != null)? Double.parseDouble( s): 0;
		} catch (Exception e) {
			return 0.0;
		}
	}
	
	/**
	 * Parses the time.
	 * 
	 * @param sDate the s date
	 * 
	 * @return the java.sql. date
	 * 
	 * @throws MException the m exception
	 */
	public synchronized java.sql.Date parseTime( String sDate) throws MException 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		SimpleDateFormat DF = Db.getSimpleTimeFormat();
		try{
			java.util.Date date = DF.parse( sDate);  
			return new java.sql.Date( date.getTime()); 
		} catch (ParseException e) {
		}
		return parseDate( sDate);
	}
	
	/**
	 * Gets the date time.
	 * 
	 * @param colname the colname
	 * 
	 * @return the date time
	 * 
	 * @throws MException the m exception
	 */
	public java.sql.Date getDateTime( String colname) throws MException 
	{
		return parseTime( _getString( colname));
	}
	
	/**
	 * Parses the date time z.
	 * 
	 * @param sDate the s date
	 * 
	 * @return the java.sql. date
	 * 
	 * @throws MException the m exception
	 */
	public synchronized java.sql.Date parseDateTimeZ( String sDate) throws MException 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		SimpleDateFormat DF = Db.getSimpleTimeZFormat();
		if( DF != null )try{
			java.util.Date date = DF.parse( sDate);  
			return new java.sql.Date( date.getTime()); 
		} catch (ParseException e) {
		}
		return parseDate( sDate);
	}
	
	/**
	 * Gets the date time z.
	 * 
	 * @param colname the colname
	 * 
	 * @return the date time z
	 * 
	 * @throws MException the m exception
	 */
	public java.sql.Date getDateTimeZ( String colname) throws MException 
	{
		return parseDateTimeZ( _getString( colname));
	}
	
	/**
	 * Gets the time.
	 * 
	 * @param colname the colname
	 * 
	 * @return the time
	 * 
	 * @throws MException the m exception
	 */
	public final int getTime( String colname) throws MException 
	{ // aeg sekundites
		java.sql.Date D = getDateTime( colname);
		if( D == null ) return 0;
		long lDate = D.getTime();
		return ( int)( ((lDate + 60000 * 60 * 2 + 500) % (60000 * 60 * 24)) / 1000);
	}
	
	/**
	 * Gets the time m.
	 * 
	 * @param colname the colname
	 * 
	 * @return the time m
	 * 
	 * @throws MException the m exception
	 */
	public final int getTimeM( String colname) throws MException 
	{ // aeg minutites
		return ( getTime( colname) + 30) / 60;
	}
	
	/**
	 * Parses the date.
	 * 
	 * @param sDate the s date
	 * 
	 * @return the java.sql. date
	 * 
	 * @throws MException the m exception
	 */
	public synchronized java.sql.Date parseDate( String sDate) throws MException 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		java.util.Date date = null;
		SimpleDateFormat DF = Db.getSimpleDateFormat();
//		SimpleDateFormat DF = dbcon.getSimpleDateFormat();
//new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssss");
		try{
			date = DF.parse( sDate);  
		} catch ( Exception e) {
		    Db.log( 99, sDate + " " + DF.toPattern() + " " + e.getMessage());
			DF = new SimpleDateFormat( "yyyy-MM-dd");	
			try{
				date = DF.parse( sDate);  
			} catch ( Exception e1) {
				try {
					DF = new SimpleDateFormat( "HH:mm:ssss");
					date = DF.parse( sDate);
				} catch ( Exception e2) {	
Db.log( sDate + " " + DF.toPattern() + " " + e1.getMessage() + " " + e2.getMessage());
				}
			}
		}
		return (date!=null)? new java.sql.Date( date.getTime()): null; 
	}
	
	/**
	 * Gets the date.
	 * 
	 * @param colname the colname
	 * 
	 * @return the date
	 * 
	 * @throws MException the m exception
	 */
	public java.sql.Date getDate( String colname) throws MException 
	{
/*		String s = _getString( colname); 
		if( s != null ){
			StringBuffer sb = new StringBuffer();
			byte [] b = s.getBytes();
			for( int i = 0; i < s.length(); ++i ){
				sb.append( Integer.toHexString( b[ i]) + ":" );
			}
			LogOR.info( " Date buffer is '" + sb.toString() + "'" );
		}  */
		return parseDate( _getString( colname));
/*		String sDate = getString( colname);
		SimpleDateFormat DF = new SimpleDateFormat( "yyyy.MM.dd");
		try{
			java.util.Date date = DF.parse( sDate);  
			return new java.sql.Date( date.getTime()); 
		} catch (ParseException e) {
		}
		return null; */
	}
	
	/**
	 * Gets the geometry.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geometry
	 * 
	 * @throws MException the m exception
	 */
	public double[] getGeometry( String colname)  throws MException 
	{
		return _getGeometry( _getString( colname));
	}
	
	/**
	 * Gets the geometry.
	 * 
	 * @param iCol the i col
	 * 
	 * @return the geometry
	 * 
	 * @throws MException the m exception
	 */
	public double[] getGeometry( int iCol)  throws MException 
	{
		return _getGeometry( _getString( iCol));
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
	public static double[] _getGeometry( String sCoord)  throws MException 
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
					P[ i++] = Float.parseFloat( sCoord.substring( iStart, iEnd));
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
					P[ i++] = Float.parseFloat( sCoord.substring( iStart, iEnd));
					if( iComma < 0 ) break;
					iStart = iComma;
				}
				return P;
			}
		}
		return null;
	}	
	
	/**
	 * Gets the geometry p.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geometry p
	 * 
	 * @throws MException the m exception
	 */
	public Point2D.Double [] getGeometryP( String colname)  throws MException 
	{
		double [] D = getGeometry( colname);
		if( D == null ) return null;
		int i = D.length/2;
		if( i > 0 ){
			Point2D.Double [] P = new Point2D.Double [ i];
			for( ; --i >= 0;) P[ i] = new Point2D.Double( D[ i*2], D[ i*2+1]); 	
			return P;
		}
		return null;
	}
	
	/**
	 * Gets the geometry p.
	 * 
	 * @param colname the colname
	 * @param sProjection the s projection
	 * 
	 * @return the geometry p
	 * 
	 * @throws MException the m exception
	 */
	public Point2D [] getGeometryP( String colname, String sProjection)  throws MException 
	{
		Point2D [] P = getGeometryP( colname);
		String sFrom = Db.getDbGeometry();
		if( sProjection != null && sFrom != null && !sProjection.equals( sFrom) )
			for( int i = P.length; --i >= 0;) P[ i] = callGeometry( P[ i], sFrom, sProjection);
		return P;
	}
	
	/**
	 * Gets the geometry point.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geometry point
	 * 
	 * @throws MException the m exception
	 */
	public Point2D getGeometryPoint( String colname)  throws MException 
	{
		Point2D.Double [] P = getGeometryP( colname);
	    return (P != null && P.length > 0)? P[ 0]: null;
	}
	
	/**
	 * Gets the polygon.
	 * 
	 * @param colname the colname
	 * 
	 * @return the polygon
	 * 
	 * @throws MException the m exception
	 */
	public Polygon getPolygon( String colname)  throws MException 
	{
		double [] D = getGeometry( colname);
		int n = D.length/2;
		if( n > 0 ){
			int [] x = new int[ n];
			int [] y = new int[ n];
			
			for( int i=0, j=0; i < n; ++i){
				x[ i] = ( int)Math.round( D[ j++]); 	
				y[ i] = ( int)Math.round( D[ j++]); 
			}	
			return new Polygon( x, y, n);
		}
		return null;
	}
	
	/**
	 * ResultSet.wasNull() wrapper
	 * 
	 * @return true, if was null
	 * 
	 * @throws MException the m exception
	 */
	public final boolean wasNull() throws MException {
		if( result == null ) return true;
		try {
			return result.wasNull();
		} catch (SQLException e) {
			String msg = "Viga wasNull() k�simisel, p�ring: " + sSQuery + ";";
			throw new MException( "yld2040", msg, null, null, e);
		}
	}  
	
	/**
	 * Checks for column.
	 * 
	 * @param colname the colname
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public final boolean hasColumn( String colname) throws MException 
	{
		try {
/*			if( result == null && iOID > 0 ){
				select( sTableName, "oid=" + Integer.toString( iOID), null);
			}
			if( result != null ){ */
			if( selectByKey() ){
				result.findColumn( colname);
				return true;
			}else if( sTableName != null ){
				return Db.hasTableColumn( sTableName, colname);
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	/**
	 * Gets the column count.
	 * 
	 * @return the column count
	 * 
	 * @throws MException the m exception
	 */
	public final int getColumnCount( ) throws MException 
	{
		try {
/*			if( result == null && iOID > 0 ){
				select( sTableName, "oid=" + Integer.toString( iOID), null);
			}
			if( result != null){ */
			if( selectByKey() ){
				 ResultSetMetaData rsmd = result.getMetaData();
     			return rsmd.getColumnCount();
			}			
			
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		}
		return 0;
	}
	
	/**
	 * Gets the column name.
	 * 
	 * @param iCol the i col
	 * 
	 * @return the column name
	 * 
	 * @throws MException the m exception
	 */
	public final String getColumnName( int iCol) throws MException 
	{
		try {
/*			if( result == null && iOID > 0 ){
				select( sTableName, "oid=" + Integer.toString( iOID), null);
			}
			if( result != null){ */

			if( selectByKey() ){
				 ResultSetMetaData rsmd = result.getMetaData();
     			return rsmd.getColumnName( iCol);
			}			
			
		} catch (SQLException e) {
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		}
		return null;
	}
	
	/**
	 * Checks if is eOF.
	 * 
	 * @return true, if is eOF
	 * 
	 * @throws MException the m exception
	 */
	public boolean isEOF() throws MException {
/*		assert ( result != null);
		try {
			return result.isAfterLast();
		} catch (SQLException e) {
			String msg = "Viga isAfterLast() k�simisel, p�ring '" + query + "'";
			Log.logger.warning( msg + " \n " + e.getMessage());
			throw new MException( msg, e);
		} */
		return bEOF;
	}  
//	Kui tegemist on primaarse v�tmega mille nimi on "id" (kahjuks erand)
// siis see ab leiab sellele v��rtuse
	/**
 * Checks if is auto column.
 * 
 * @param sAttrName the s attr name
 * 
 * @return true, if is auto column
 * 
 * @throws MException the m exception
 */
public boolean isAutoColumn( String sAttrName) throws MException
	{
		return isPrimaryKey( sAttrName) && 
			sAttrName.equalsIgnoreCase( "id");
	}
	
	/**
	 * Edits the.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean edit() throws MException
	{ 
// LogOR.info( "edit");
		boolean bRet = false;
		if( sTableName != null && sTableName.length() > 0 /* && PKey != null */ ){
			sUQuery = "UPDATE " + Db.getTablePro() + sTableName + " SET ";
		}else{ // see on arusaamatu, et mis asi see on
			sUQuery = "";
		}
		sValues = "";
		bStart = bRet = bUpdate = true;
// LogOR.info( query);
// LogOR.info( sValues);
		return bRet;
	}
	
	/**
	 * Edits the.
	 * 
	 * @param iID the i id
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean edit( int iID) throws MException
	{
		setID( iID);
		return edit();
	}
	
	/**
	 * Adds the new.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean addNew() throws MException
	{
// LogOR.info( "addNew");
		boolean bRet = false;
		_reset();
		sUQuery = "INSERT INTO " + Db.getTablePro() + sTableName + " (";
		sValues = ") VALUES ("; 
   		bStart = bRet = true; 
   		bUpdate = false;
   		PKey = null;
// LogOR.info( query);
// LogOR.info( sValues);
   		return bRet;
	}
	
	/**
	 * Can delete.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean canDelete() throws MException
	{
		return true;
	}	
	
	/**
	 * Delete.
	 * 
	 * @param sTableName the s table name
	 * @param sWhere the s where
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete( String sTableName, String sWhere) throws MException
	{
		boolean bRet = false;
		if( sTableName != null && sTableName.length() > 0 ){			;
			sUQuery = "DELETE FROM " + Db.getTablePro() + sTableName + " WHERE " + sWhere; 
			execUpdate( sUQuery);
			bRet = true;
		}
		return bRet;
	}	
	
	/**
	 * Delete.
	 * 
	 * @param sTableName the s table name
	 * @param sKeyName the s key name
	 * @param sWhere the s where
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete( String sTableName, String sKeyName, String sWhere) throws MException
	{
		return delete( sTableName, sWhere);
	}	
	
	/**
	 * Delete.
	 * 
	 * @param sWhere the s where
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete( String sWhere) throws MException
	{
		return delete( sTableName, sWhere);
	}	
	
	/**
	 * Delete.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete() throws MException
	{
		if( PKey != null && getIDName() != null && canDelete() )
			return delete( sTableName, getKeyWhere());
		return false;
	}
	
	/**
	 * Delete.
	 * 
	 * @param iID the i id
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete( int iID) throws MException
	{
		if( getIDName() != null && canDelete() ){
			setID( iID);
			return delete( sTableName, getKeyWhere());
		}	
		return false;
	}
	
	/**
	 * Delete_all.
	 * 
	 * @param sTableName the s table name
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean delete_all() throws MException
	{
		return delete_all( sTableName);
	}
	public boolean delete_all( String sTableName) throws MException
	{

		boolean bRet = false;
		if( sTableName != null && sTableName.length() > 0 ){			;
			sUQuery = "DELETE FROM " + Db.getTablePro() + sTableName ; 
//			_close();  
Db.log( sUQuery);
//			Db.
			execUpdate( sUQuery); 
		}
		return bRet;
	}	
/*	public void test() throws MException
	{

		int i = 0;
		if( select( "_test", null, "a_int") ) do {
			++i;
			if( edit() ){
//			setInt( "a_int", i);
				setString( 	"a_char", "char " + Integer.toString( i*11));
				setString( 	"a_text", "text " + Integer.toString( i*111));
				setDouble( 	"a_float", 1.111 * i);
				setBoolean( "a_bool", false);
				setInt( "a_dec", 1111*i);
//			setString( "start_date", "2003-01-01");
//			setString( "end_date", "2003-03-04");
				GregorianCalendar Cal = new GregorianCalendar();
				java.sql.Date date = new java.sql.Date( Cal.getTimeInMillis());  
				if( i > 2 ) setDate( "start_date"); 
				else setDate( "start_date", date);
				Cal.set( 2003, 3, i);
				date.setTime( Cal.getTimeInMillis());  
				setDate( "end_date", date);
				update();
				commit();
			}
		}while( next());
/*		select( "_test", "a_int=3", null);
		addNew(); // "_test");
		setInt( "a_int", 9);
		setString( 	"a_char", "char 9");
		setString( 	"a_text", "text 9");
		setDouble( 	"a_float", 9.9);
		setBoolean( "a_bool", false);
		setInt( "a_dec", 9999);
		update(); 

//		int iDec = getInt( "a_dec");

		int iID = getInt( "a_int");
		double d = getDouble( "a_float");
		String sText = getString( "a_char");
		String sMemo = getString( "a_text");
		edit();
		setString( "a_char", "char 6666666");
		setString( "a_text", "text 6666666");
		setInt( "a_dec", 66666666);
		update();
		select( "_test", "a_int=3", null);
		edit();
		setString( "a_char", "char 3333333333");
		setString( "a_text", "text 3333333333");
		update();
		String sText1 = getString( "a_char");
		String sMemo1 = getString( "a_text"); /
					
// INSERT INTO _test (a_int, a_char, a_text, a_float, a_bool, a_dec)
//VALUES (1,'string 1', 'memo 1', 1.1, 't', 11.11);		
	} */
	/**
 * Can update.
 * 
 * @param sName the s name
 * @param sNewValue the s new value
 * 
 * @return true, if successful
 * 
 * @throws MException the m exception
 */
public boolean canUpdate( String sName, String sNewValue) throws MException
	{
		return true;
	}	
	
	/**
	 * _set string.
	 * 
	 * @param sName the s name
	 * @param sValue the s value
	 * @param bComma the b comma
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	private String _getStringValue( String sValue)
	{
	    return ( sValue != null )?  sValue.replaceAll( "'", "''"): null;
	}
	private boolean _setString( String sName, String sValue, boolean bComma) throws MException 
	{
		boolean bRet = false;
		if( bUpdate ){
			if( sUQuery != null && sUQuery.length() > 0 && canUpdate( sName, sValue) ){
				if( sValue == null || sValue.length() <= 0 ){
					sValue = "NULL";
					bComma = false;
				}
				sUQuery = sUQuery + (bStart? " ": ", ") + sName + "=" + 
					( bComma? "'" + _getStringValue( sValue) + "'": sValue);
				bStart = false;
				bRet = true;
			}
		}else{
			if( sValue != null && sValue.length() > 0 ){
				sUQuery = sUQuery + (bStart? " ": ", ") + sName;
				sValues = sValues + (bStart? " ": ", ") +	( bComma? "'" + _getStringValue( sValue) + "'": sValue); 
				bStart = false;
				if( sName.equals( sKeyName) ) 
					PKey = bComma? ( Object)( new String( sValue)): ( Object)( new Integer( sValue));
// LogOR.info( query);
// LogOR.info( sValues);
// LogOR.info( sValue);
			}
			bRet = true;
		}
		return bRet;
	}
	
	/**
	 * Sets the string.
	 * 
	 * @param sName the s name
	 * @param sValue the s value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setString( String sName, String sValue) throws MException 
	{
		return _setString( sName, sValue, true); 
	}
	public boolean setString( String sName, String sValue, boolean bComma) throws MException 
	{
		return _setString( sName, sValue, bComma); 
	}
	public boolean setValue( String sName, int iValue) throws MException 
	{
		return _setString( sName, Integer.toString( iValue), false); 
	}
	public boolean setValue( String sName, String sValue, boolean bComma) throws MException 
	{
		return _setString( sName, sValue, bComma); 
	}
	/**
	 * Sets the string convert.
	 * 
	 * @param sName the s name
	 * @param sValue the s value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setStringConvert( String sName, String sValue) throws MException 
	{
		return _setString( sName, sValue, false);
	}
	
	/**
	 * Sets the boolean.
	 * 
	 * @param sName the s name
	 * @param bValue the b value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setBoolean( String sName, boolean bValue) throws MException 
	{
		return _setString( sName, bValue? "t": "f", true); 
	}
	
	/**
	 * Sets the int.
	 * 
	 * @param sName the s name
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setInt( String sName, int iValue) throws MException 
	{
		return _setString( sName, Integer.toString( iValue), false); 
	}
	
	/**
	 * Sets the int key.
	 * 
	 * @param sName the s name
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setIntKey( String sName, int iValue) throws MException 
	{
		return ( iValue>0)? setInt( sName, iValue): setNull( sName);
	}
	
	/**
	 * Sets the int.
	 * 
	 * @param sName the s name
	 * @param dValue the d value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setInt( String sName, double dValue) throws MException 
	{
		return _setString( sName, Long.toString( Math.round( dValue)), false); 
	}
	
	/**
	 * Sets the long.
	 * 
	 * @param sName the s name
	 * @param lValue the l value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setLong( String sName, long lValue) throws MException 
	{
		return _setString( sName, Long.toString( lValue), false); 
	}
	
	/**
	 * Sets the long.
	 * 
	 * @param sName the s name
	 * @param sValue the s value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setLong( String sName, String sValue) throws MException 
	{
		return _setString( sName, sValue, false); 
	}
//	public boolean setDecimal( String sName, int iValue) throws MException '
//	{
//		_setString( sName, Decimal.toString( iValue)); 
//		return true;
//	}
	/**
 * Sets the double.
 * 
 * @param sName the s name
 * @param dValue the d value
 * 
 * @return true, if successful
 * 
 * @throws MException the m exception
 */
public boolean setDouble( String sName, double dValue) throws MException 
	{
		return _setString( sName, Double.toString( dValue), false); 
	}
	
	/**
	 * Sets the date to null.
	 * 
	 * @param sName the s name
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setDateToNull( String sName) throws MException 
	{
		return _setString( sName, "NULL", false); 
	}
	
	/**
	 * Gets the sQL date.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL date
	 * 
	 * @throws MException the m exception
	 */
	public String getSQLDate(java.sql.Date dValue) throws MException 
	{
/*		SimpleDateFormat DF = Db.getSimpleDateFormat();
		return Db.isOracle()? "to_date('" + DF.format( dValue) + "','" + Db.getDateFormat() + "')": 
			DF.format( dValue); */
	    return Db.getSQLDate( dValue);
	}
	
	/**
	 * Gets the sQL date where.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL date where
	 * 
	 * @throws MException the m exception
	 */
	public String getSQLDateWhere( java.sql.Date dValue) throws MException 
	{
		return Db.isOracle()? getSQLDate( dValue): "'" + getSQLDate( dValue) + "'";
	}
	
	/**
	 * Sets the date.
	 * 
	 * @param sName the s name
	 * @param dValue the d value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setDate( String sName, java.sql.Date dValue) throws MException 
	{
		return ( dValue != null)? _setString( sName, getSQLDate( dValue), !Db.isOracle()): setNull( sName); 
//		return _setString( sName, GlobalFunc.DateToSQL( dValue), true); 
	}
	public boolean setDate( String sName, java.util.Date dValue) throws MException 
	{
		return ( dValue != null)? _setString( sName, getSQLDate( new java.sql.Date( dValue.getTime())), !Db.isOracle()): setNull( sName); 
//		return _setString( sName, GlobalFunc.DateToSQL( dValue), true); 
	}

	/**
	 * Gets the sQL time.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL time
	 */
	public synchronized String getSQLTime(java.sql.Date dValue)  
	{
		if( dValue != null ) try {
            SimpleDateFormat DF = Db.getSimpleTimeFormat();
            return Db.isOracle()? "to_date('" + DF.format( dValue) + "','" + Db.getTimeFormat() + "')": 
            	DF.format( dValue);
        } catch( MException E) {
        } catch( Exception E) {
            Db.log( " Exception" + E.getMessage());
        }
        return null;
	}
	
	/**
	 * Gets the sQL time where.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the sQL time where
	 * 
	 * @throws MException the m exception
	 */
	public String getSQLTimeWhere( java.sql.Date dValue) throws MException 
	{
		return Db.isOracle()? getSQLTime( dValue): "'" + getSQLTime( dValue) + "'";
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param sName the s name
	 * @param dValue the d value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setTime( String sName, java.sql.Date dValue) throws MException 
	{
		return _setString( sName, getSQLTime( dValue), !Db.isOracle()); 
//		return _setString( sName, GlobalFunc.DateToSQL( dValue), true); 
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param sName the s name
	 * @param lValue the l value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setTime( String sName, long lValue) throws MException 
	{
		return setTime( sName, new java.sql.Date( lValue)); 
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param sName the s name
	 * @param dValue the d value
	 * @param iTime the i time
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setTime( String sName, java.sql.Date dValue, int iTime) throws MException 
	{
		long lDate = (dValue.getTime() + 60000 * 60 * 2) / (60000 * 60 * 24); 		// Time zone 2
		return setTime( sName, (lDate*60*60*24 - 60 * 60 * 2 + iTime) * 1000); 
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param sName the s name
	 * @param Value the value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setTime( String sName, java.util.Date Value) throws MException 
	{
		return setTime( sName, new java.sql.Date( Value.getTime())); 
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param sName the s name
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setTime( String sName) throws MException 
	{
		return setTime( sName, new java.util.Date()); 
//		return _setString( sName, getCurrentDate(), true); 
	}
	
	/**
	 * Sets the null.
	 * 
	 * @param sName the s name
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setNull( String sName) throws MException 
	{
		return setString( sName, ( String)null); 
	}
	
	/**
	 * Sets the geometry.
	 * 
	 * @param colname the colname
	 * @param Coord the coord
	 * @param iNum the i num
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeometry( String colname, double[] Coord, int iNum)  throws MException 
	{
		if( iNum >= 2 ){
			StringBuffer sCoord = new StringBuffer( "SRID=" + Db.getDbGeometry() + ";LINESTRING(");
			for( int i = 0;;){
				sCoord.append( Double.toString( Coord[ i*2]) + " ");
				sCoord.append( Double.toString( Coord[ i*2+1]));
				if( ++i < iNum )
					sCoord.append( ",");
				else{
					sCoord.append( ")");
					break;
				}					
			}
			return _setString( colname, sCoord.toString(), true);
		}else if( iNum > 0 ){
			return setGeometry( colname, new Point2D.Double( Coord[ 0], Coord[ 1]));
		}else
			return _setString( colname, null, true);
	}	
	public boolean setGeometryFromGML( String colname, String sGML, boolean bMulti)  throws MException 
	{
		if( bMulti && sGML.indexOf( "gml:Polygon") > 0 )
			return _setString( colname, "ST_Multi( ST_GeomFromGML('" + sGML +"'))", false);
		else
			return _setString( colname, "ST_GeomFromGML('" + sGML +"')", false);
	}
/*	public boolean setGeometry( String colname, ArrayList Points)  throws MException 
	{
		int iNum = Points.size(); 
		if( iNum > 0 ){
			StringBuffer sCoord = new StringBuffer( "SRID=" + Db.getDbGeometry() + ";LINESTRING(");
			for( int i = 0;;){
				Point2D.Double P = ( Point2D.Double)Points.get( i);
				sCoord.append( Double.toString( P.x) + " " + Double.toString( P.y));
				if( ++i < iNum ) sCoord.append( ",");
				else	break;
			}
			sCoord.append( ")");
			return _setString( colname, sCoord.toString(), true);
		}else
			return _setString( colname, null, true);
	} */	
	/**
 * Sets the geometry polygon.
 * 
 * @param colname the colname
 * @param Points the points
 * 
 * @return true, if successful
 * 
 * @throws MException the m exception
 */
public boolean setGeometryPolygon( String colname, ArrayList<Point2D.Double> Points)  throws MException 
	{
		int iNum = Points.size(); 
		if( iNum > 0 ){
			StringBuffer sCoord = new StringBuffer( "SRID=" + Db.getDbGeometry() + ";POLYGON((");
			for( int i = 0;;){
				Point2D.Double P = ( Point2D.Double)Points.get( i);
				sCoord.append( Double.toString( P.x) + " " + Double.toString( P.y));
				if( ++i < iNum ) sCoord.append( ",");
				else	break;
			}
			sCoord.append( "))");
//LogOR.info( "setGeometryPolygon " + sCoord.toString());
			return _setString( colname, sCoord.toString(), true);
		}else
			return _setString( colname, null, true);
	}	
	
	/**
	 * Sets the geometry.
	 * 
	 * @param colname the colname
	 * @param Points the points
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeometry( String colname, Point2D [] Points)  throws MException 
	{
	    if( Points == null ){
	        if( isUpdate() ) return _setString( colname, null, true);
	        return true;
	    }
		int iNum = Points.length; 
		if( iNum > 1 ){
			StringBuffer sCoord = new StringBuffer( "SRID=" + Db.getDbGeometry() + ";LINESTRING(");
			for( int i = 0;;){
				Point2D P = Points[ i];
				sCoord.append( Double.toString( P.getX()) + " " + Double.toString( P.getY()));
				if( ++i < iNum ) sCoord.append( ",");
				else	break;
			}
			sCoord.append( ")");
			return _setString( colname, sCoord.toString(), true);
		}else if( iNum > 0 ){
			return setGeometry( colname, Points[ 0]);
		}else
			return _setString( colname, null, true);
	}		
	
	/**
	 * Sets the geometry.
	 * 
	 * @param colname the colname
	 * @param P the p
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeometry( String colname, Point2D P)  throws MException 
	{
		if( P != null ){
			String sCoord = "SRID=" + Db.getDbGeometry() + ";POINT(" + Double.toString( P.getX()) + " " + Double.toString( P.getY()) + ")";
			return _setString( colname, sCoord, true);
		}else
			return _setString( colname, null, true);
	}
	
	/**
	 * Sets the geometry proj.
	 * 
	 * @param colname the colname
	 * @param P the p
	 * @param iProj the i proj
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeometryProj( String colname, Point2D P, int iProj)  throws MException 
	{
		if( P != null ){
			int iFormat = ( iProj == Projection.CS_LL )? 7: 2;
			StringBuffer Coords = new StringBuffer();
			Coords.append( "SRID=");
			Coords.append( iProj);
			Coords.append( ";");
			Coords.append( "POINT(");
			Coords.append( GlobalData.DoubleToString( P.getX(), iFormat));
			Coords.append( " ");
			Coords.append( GlobalData.DoubleToString( P.getY(), iFormat)); 
			Coords.append( ")");
			return _setString( colname, Coords.toString(), true);
/*			StringBuffer Call = new StringBuffer( "setsrid( 'POINT(");
			Call.append( P.getX());
			Call.append( " ");
			Call.append( P.getY());
			Call.append( ")'::geometry, ");
			Call.append( iProj);
			Call.append( ")");
			return _setString( colname, Call.toString(), false);*/
		}else
			return _setString( colname, null, true);
	}
	
	/**
	 * Sets the geometry.
	 * 
	 * @param colname the colname
	 * @param P the p
	 * @param iProj the i proj
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeometry( String colname, Point2D P, int iProj)  throws MException 
	{
		if( P != null ){
			StringBuffer Call = new StringBuffer( "transform( setsrid( 'POINT(");
			Call.append( P.getX());
			Call.append( " ");
			Call.append( P.getY());
			Call.append( ")'::geometry, ");
			Call.append( iProj);
			Call.append( "), ");
			Call.append( Db.getDbGeometry());
			Call.append( ")");
			return _setString( colname, Call.toString(), false);
		}else
			return _setString( colname, null, true);
	}
	
	/**
	 * Sets the user id.
	 * 
	 * @param colname the colname
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setUserID( String colname) throws MException 
	{
		return setInt( colname, Db.getUserID());
	}
	
	/**
	 * Gets the current date.
	 * 
	 * @return the current date
	 * 
	 * @throws MException the m exception
	 */
	public String getCurrentDate() throws MException 
	{
		GregorianCalendar Cal = new GregorianCalendar();
		java.sql.Date date = new java.sql.Date( Cal.getTimeInMillis());  
		return date.toString(); 
	}
	
	/**
	 * Checks if is update.
	 * 
	 * @return true, if is update
	 */
	public boolean isUpdate(){
		return bUpdate;
	}
	public boolean isUpdateEmty(){
		return bStart;
	}
	
	/**
	 * Update.
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean update() throws MException
	{
		boolean bRet = false;
		if( bStart || sKeyName == null ) return bRet;
		try {
			if( bUpdate ){
				if( PKey == null ) return false;
				sUQuery = sUQuery + " WHERE " + getKeyWhere();
			}else{
				if( PKey == null && Db.supportsPreKey() ){
					int iID = ( int)getPreKey( sTableName);
					if( iID != -1 ){
					 	if( !alreadyInInsert(sKeyName) ) _setString( sKeyName, Integer.toString( iID), false);
						PKey = new Integer( iID);
					}
				} 
				sUQuery = sUQuery + sValues + ")";
			}
			bRet = Db.exec( sUQuery) > 0;
			sValues = "";
//   		   	bRet = true; 
		} catch ( MException e ){
			throw e;
		} catch ( Exception e ){
			String msg = "Viga update's: " + sUQuery + ";";
			throw new MException( "yld2040", msg, null, null, e);
		}
		return bRet;
	}
	public boolean update( String sWhere) throws MException
	{
		boolean bRet = false;
		try {
			if( bUpdate ){
//				if( PKey == null ) return false;
				sUQuery = sUQuery + " WHERE " + sWhere;
			}else{ // nii lisatakse v�tmeta kirjeid
				sUQuery += sValues + ")";
			}
			Db.exec( sUQuery);
			sValues = "";
   		   	bRet = true; 
		} catch ( MException e ){
			throw e;
		} catch ( Exception e ){
			String msg = "Viga update's: " + sUQuery + ";";
			throw new MException( "yld2040", msg, null, null, e);
		}
		return bRet;
	}
	public String getQuery() throws MException
	{
		return getQuery( true);
	}
	public String getQuery( boolean bAddKey) throws MException
	{
		if( bStart || bAddKey && sKeyName == null ) return null;
		try {
			if( bUpdate ){
				if( PKey == null ) return null;
				sUQuery = sUQuery + " WHERE " + getKeyWhere() + "; ";
			}else{
				if( bAddKey && PKey == null && Db.supportsPreKey() ){
					int iID = ( int)getPreKey( sTableName);
					if( iID != -1 ){
					 	if( !alreadyInInsert( sKeyName) ) _setString( sKeyName, Integer.toString( iID), false);
						PKey = new Integer( iID);
					}
				} 
				sUQuery += sValues + "); ";
			}
			sValues = "";
   			return sUQuery;
		} catch ( MException e ){
			throw e;
		} catch ( Exception e ){
			String msg = "Viga update's: " + sUQuery + ";";
			throw new MException( "yld2040", msg, null, null, e);
		}
	}
	
	/**
	 * Method alreadyInInsert.	Kas antud columni nimi on juba insert lauses olemas?
	 * 
	 * @param sKeyName the s key name
	 * 
	 * @return boolean
	 */
	private boolean alreadyInInsert(String sKeyName) {

		if( sUQuery == null || sKeyName == null ) 	// kui query-yt pole siis pole ka midagi uurida
			return false;
		// K�ige kindlam oleks uurida lisatavate columnite nimede loetelu.
		// Kuna seda siin eraldi ei ole siis uurime query lauset.
		String colpart = sUQuery.substring( sUQuery.indexOf('('));
		if( colpart == null ) return false;
			
		StringTokenizer st = new StringTokenizer( colpart, " \t\n\r\f,()");
		while( st.hasMoreTokens() ) {
			String colname = st.nextToken();	
			if ( sKeyName.equalsIgnoreCase( colname) ) return true;
		}
		return false;
	}
	
	/**
	 * Commit.
	 * 
	 * @throws MException the m exception
	 */
	public void commit() throws MException {
		Db.commit();
	} 
	
	/**
	 * Rollback.
	 * 
	 * @throws MException the m exception
	 */
	public void rollback() throws MException {
		Db.rollback();
	} 
	
	/**
	 * Find.
	 * 
	 * @param sAttr the s attr
	 * @param sValue the s value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean find( String sAttr, String sValue)  throws MException
	{
		if( gotoTop() ) while( next() ){
			String sDBValue;
			try {
				sDBValue = getString( sAttr);
				if( sDBValue.equals( sValue) ) return true;
			} catch (MException e) {
			}
		};
		return false;
	}
	
	/**
	 * Find.
	 * 
	 * @param sAttr the s attr
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean find( String sAttr, int iValue)  throws MException
	{
		if( gotoTop() ) while( next() ){
			int iDBValue;
			try {
				iDBValue = getInt( sAttr);
				if( iDBValue == iValue ) return true;
			} catch (MException e) {
			}
		};
		return false;
	}
	
	/**
	 * Find_next.
	 * 
	 * @param sAttr the s attr
	 * @param iValue the i value
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean find_next( String sAttr, int iValue)  throws MException
	{
		while( next() ){
			int iDBValue;
			try {
				iDBValue = getInt( sAttr);
				if( iDBValue == iValue ) return true;
			} catch (MException e) {
			}
		};
		return false;
	}
	
	/**
	 * Call geometry.
	 * 
	 * @param P the p
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
	public Point2D callGeometry( Point2D P, String sFromP, String sToP)  throws MException 
	{
		final String sCallS = "select astext( transform( setsrid( 'POINT(";
		final String sCallE = ")'::geometry, " + sFromP + "), " + sToP + "))";
		String sQuery = sCallS + P.getX() + " " + P.getY() + sCallE;
		
		Db.log( 5, sQuery);
    	Statement St = null;
    	ResultSet RS = null;
    	try
    	{
    	    St = Db.getStatement( true);
    	    St.execute( sQuery);
 	       	RS = St.getResultSet();
    	    if( RS != null ) while( RS.next() )
        	{
    	    	double [] D = _getGeometry( RS.getString( 1)); //"transform"));
				if( D != null && D.length/2 > 0 ){ 
					return  new Point2D.Double( D[ 0], D[ 1]);
				}
        	}
 	    }
    	catch (Exception E)
    	{
    	    Db.log( "callGeometry error: " + E.getMessage());
    	}finally{
			try {
				if( RS != null ) RS.close();
			} catch (SQLException e) {
			}
			try {
	        	if( St != null ) St.close();
			} catch (SQLException e) {
			}
    	}
    	return null;
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
	public int execUpdate( String query) throws MException 
	{
	    return Db.exec( query);
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
	public int exec( String query) throws MException 
	{
	    return Db.exec( query);
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
	    return Db.getPreKey( sTableName);
	}
	
	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public String getError()
	{
		return  Db.getError();
	}
	
	/**
	 * Sets the error.
	 * 
	 * @param sError the new error
	 */
	public void setError( String sError)
	{
		Db.setError( sError);
	}
	
	/**
	 * Log meta data.
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public final int logMetaData( ) throws MException 
	{
		try {
			if( selectByKey() ){
				ResultSetMetaData rsmd = result.getMetaData();
     			for( int i = 1; i <= rsmd.getColumnCount(); ++i){
     				StringBuffer Buf = new StringBuffer();
     				Buf.append( rsmd.getSchemaName( i));
     				Buf.append( " ");
     				Buf.append( rsmd.getTableName( i));
     				Buf.append( " ");
//     				Buf.append( rsmd.getColumnClassName( i));
//     				Buf.append( " ");
     				Buf.append( rsmd.getColumnName( i));
     				Buf.append( " ");
//     				Buf.append( rsmd.getColumnLabel( i));
//     				Buf.append( " ");
     				Buf.append( rsmd.getColumnType( i));
     				Buf.append( " ");
     				Buf.append( rsmd.getColumnDisplaySize( i));
     				Buf.append( " ");
     				Db.log( Buf.toString());
    			}
 			}			
			
		} catch (SQLException E) {
			Log.info( E);
//			String msg = "Viga columni '" + colname + "' v��rtuse k�simisel, p�ring '" + query + "'";
//			throw new MException( "yld2040", msg, null, null, e);
		}
		return 0;
	}
	
	/**
	 * Gets the clob.
	 * 
	 * @param colname the colname
	 * 
	 * @return the clob
	 * 
	 * @throws MException the m exception
	 */
	public String getClob( String colname)  throws MException 
	{
		try {
			if( result.getType() == java.sql.Types.CLOB ){
				Log.info( "CLOB get");
				Clob aCLob = ((OracleResultSet)result).getClob( colname);
				if( aCLob != null ){
					Log.info( "CLOB not null");
				    InputStreamReader is = new InputStreamReader( aCLob.getAsciiStream());   
				    StringWriter aSW = new StringWriter();
//				    FileOutputStream fos = null;       
				    try {
//						fos = new FileOutputStream("c:/TEMP/xxx.txt");
						char[] data = new char[ 1024];      
						int i = 0;       
						while ((i = is.read(data)) != -1) { 
							aSW.write( data, 0, i);
//							  fos.write(data, 0, i);       
						}
					} catch (Exception aE) {
						Log.error( aE);
					}
//					int iLen = ( int)aCLob.length();
//					sGeom = aCLob.getSubString( 0L, iLen);
					return aSW.toString();
				}
			}else{
				Object aObj = result.getObject( colname);
				if( aObj instanceof PGobject ){
					PGgeometry aGeom = new PGgeometry( ((PGobject) aObj).getValue());
					return aGeom.toString();
				}else if( aObj instanceof PGgeometry ){
					PGgeometry aGeom = (PGgeometry)result.getObject( colname); 
					return aGeom.toString();
				}else return _getString( colname);
			}
		} catch( Exception aE) {
			Log.error( aE);
		}
		return null;
	}
	public boolean isOracle()
	{
		return Db != null && Db.isOracle();
	}
	/**
	 * Gets the geom.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geom
	 * 
	 * @throws MException the m exception
	 */
	public Geom getGeom( String colname)  throws MException 
	{
		 Geom aGeom = Geom.getGeom( getClob( colname));
		 if( aGeom != null && aGeom.getSRID() == 0 ) 
			 if( GlobalGIS.isLL( aGeom.getCenter()) ) aGeom.setSRID( Projection.CS_LL);
			 else aGeom.setSRID( Integer.parseInt( Db.getDbGeometry()));
		 return aGeom;
	}
	
	/**
	 * Gets the geom point.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geom point
	 * 
	 * @throws MException the m exception
	 */
	public GeomPoint getGeomPoint( String colname)  throws MException 
	{
		Geom aGeom = getGeom( colname);
		return ( aGeom != null && aGeom.isPoint())? ( GeomPoint)aGeom: null;
	}
	
	/**
	 * Gets the geom line.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geom line
	 * 
	 * @throws MException the m exception
	 */
	public GeomLine getGeomLine( String colname)  throws MException 
	{
		Geom aGeom = getGeom( colname);
		return ( aGeom != null && aGeom.isLine())? ( GeomLine)aGeom: null;
	}
	
	/**
	 * Gets the geom area.
	 * 
	 * @param colname the colname
	 * 
	 * @return the geom area
	 * 
	 * @throws MException the m exception
	 */
	public GeomArea getGeomArea( String colname)  throws MException 
	{
		Geom aGeom = getGeom( colname);
		if( aGeom instanceof GeomMArea ){
			int n = (( GeomMArea) aGeom).getSize();
			if( n == 1 ) return (( GeomMArea) aGeom).getArea( 0);
			else{
				++n;
			}
		}
		return ( aGeom != null && aGeom.isArea())? ( GeomArea)aGeom: null;
	}
	/**
	 * Sets the geom.
	 * 
	 * @param colname the colname
	 * @param aGeom the a geom
	 * 
	 * @return true, if successful
	 * 
	 * @throws MException the m exception
	 */
	public boolean setGeom( String colname, Geom aGeom)  throws MException 
	{
		if( aGeom != null ){
			String sGeom = aGeom.toString();
			if( sGeom.length() > 4000 && isOracle()){
				long lSTime = ( new Date()).getTime();
				setAutoCommit( false);
				PreparedStatement ps = null;
				_setString( colname, "?", false);
       			String sQuery = getQuery();
       			if( Db.isDebug( 2)) Db.log( sQuery);
       			try{
					ps = getPreStatement( sQuery);
       				if( isOracle() ){
       					(( OraclePreparedStatement)ps).setStringForClob( 1, sGeom);
       				}else
       					ps.setBytes( 1, sGeom.getBytes());
					ps.executeUpdate();
					Db.commit();
					bStart = true;
					return true;
				}catch( Exception aE ){
					Db.log( aE);
				}finally{
					try{
						ps.close();
					}catch( SQLException aE ){
						Db.log( aE);
					}
					setAutoCommit( true);
					if( Db.isDebug( 1)) Db.log( "DB time=" + (( new Date()).getTime() - lSTime) + "ms " + sGeom.length());
				}
			}else
				return setString( colname, sGeom);
		}else return setNull( colname);
		return false;
	}
	
	/**
	 * Gets the database.
	 * 
	 * @return the database
	 */
	public Database getDatabase() {
		return Db;
	}
	
	/**
	 * Checks if is database closed.
	 * 
	 * @return true, if is database closed
	 */
	public boolean isDatabaseClosed() // sellel on m�te �ksnes vahetult p�rast commit-i
	{ // kui baasiga midagi juhtus, siis pannakse k�ik asjad kinni
		return (Db != null)? Db.isClosed(): true;
	}
	
	/**
	 * Sets the foreign keys.
	 * 
	 * @param foreignKeys the new foreign keys
	 */
/*	public void setForeignKeysX(ArrayList foreignKeys) {
		ForeignKeys = foreignKeys;
	}*/
	
	/**
	 * Gets the foreign keys.
	 * 
	 * @return the foreign keys
	 */
/*	public ArrayList getForeignKeys() {
		return ForeignKeys;
	}*/
	public void setAutoCommit( boolean b) throws MException
	{
		if( Db != null) Db.setAutoCommit( b);
	}
	public PreparedStatement getPreStatement( String sPreSQL) throws Exception
	{
		return ( Db != null)? Db.getPreStatement( sPreSQL): null;
	}
	public ResultSetMetaData getMetaData() throws MException
	{
		try{
			return ( result != null)? result.getMetaData(): null;
		}catch( SQLException aE ){
		}
		return null;
	}	
	public ResultSet getMetaData( String sTableName) throws MException
	{
		return ( Db != null )? Db.getMetaData( sTableName): null;
	}	
	public ResultSet getTableKeys( String sTableName) throws MException
	{
		return ( Db != null )? Db.getTableKeys( sTableName): null;
	}	
	public static byte [] getBytes( Reader reader)
	{
        int ch;
        ByteBuffer buf = ByteBuffer.allocate( 1000);
        try {
            while ( (ch = reader.read()) >= 0 ) buf.put( ( byte)ch);
            return buf.array();
        } catch (IOException E1) {
//            log( E1.getMessage());
        }
        return null;
	}

	public void setUUID( String sName, UUID aUUID) throws MException
	{
		if( isOracle() ) _setString( sName, "HEXTORAW( REPLACE( '" + aUUID.toString() + "','-',''))", false); 		
	}
	public final UUID getUUID( String colname) throws MException {
		byte [] bBytes = getBytes( colname);
		StringBuffer sbUUID = GlobalData.BytesToHexString( bBytes);
		if( sbUUID == null) return null;
		sbUUID.insert( 8, "-");
		sbUUID.insert( 13, "-");
		sbUUID.insert( 18, "-");
		sbUUID.insert( 23, "-");
		//576ecb40-425f-11e0-b8fc-001d7d0d3b58
		return UUID.fromString( sbUUID.toString());
	}

	public InputStream getBinaryStream( int iCol) 
	{
	    try {
	        return result.getBinaryStream( iCol);
        } catch ( Exception E) {
            Db.log( E.getMessage());
        } 
        return null;
	}
	public boolean existsTable( String sTableName)
	{
		return ( Db != null)? Db.hasTable( sTableName): false;
	}
	public boolean existsIndex( String sTableName, String sIndexName)
	{
		return ( Db != null)? Db.hasIndex( sTableName, sIndexName): false;
	}
	public boolean existsColumn( String sTableName, String sColumnName)
	{
		return ( Db != null)? Db.hasColumn( sTableName, sColumnName): false;
	}
	public int [] getIntsX( String colname) throws MException
	{
		String sInts = getString( colname);
		if( sInts != null && sInts.length() > 0 ){
			String [] saInts = sInts.substring( 1, sInts.length()-1).split( ",");
			int n = saInts.length; 
			int [] iaInts = new int[ n];
			for( int i = n; --i >= 0; ) iaInts[ i] = Integer.parseInt( saInts[ i]);
			return iaInts;
		}
		return null;
	}
	public int [] getInts( String colname) throws MException
	{
		try {
			Array aArray = result.getArray( colname);
			Integer[] aInts = (Integer[])aArray.getArray();
			return GlobalData.get( aInts);
		} catch (SQLException aE) {
			 Db.log( aE.getMessage());
		}
		 return null;
	}
	public int [] getInts( int iCol) throws MException
	{
		try {
			Array aArray = result.getArray( iCol);
			Integer[] aInts = (Integer[])aArray.getArray();
			return GlobalData.get( aInts);
		} catch (SQLException aE) {
			 Db.log( aE.getMessage());
		}
		 return null;
	}
	public void log( Exception aE){ Db.log( aE);}
	public void log( String s){ Db.log( s);}
	public void exit() {
		Db.log( 98, "statement cancel " + ( statement != null) );
		if( statement != null)
			try {
				statement.cancel();
			} catch (SQLException aE) {
				if( Db.isDebug( 98) ) log( aE.getMessage());
			}
	}
}
