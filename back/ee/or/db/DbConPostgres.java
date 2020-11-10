/*
 * 
 */
package ee.or.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;

import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConPostgres.
 * 
 * @author toivo
 * 
 * Andmebaasi yhendus mis arvestab Postgres baasi erip‰ra.
 */
public class DbConPostgres extends DbConnection {

	/**
	 * Constructor for DbConPostgres.
	 * 
	 * @param con the con
	 * @param db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbConPostgres( Database db, Connection con) throws MException {
		super( db, con);
	}
	
	/**
	 * Method skipEmpty.	Kas tegemist on sellist tyypi columniga kus tyhja v‰‰rtuse korral
	 * insert/update pole soovitav vıi vıimalik.
	 * 
	 * @param tabname the tabname
	 * @param colname the colname
	 * 
	 * @return boolean
	 * 
	 * @throws MException the m exception
	 */
	protected boolean skipEmpty(String tabname, String colname) throws MException {

		// J‰rgnevalt kıik t¸¸bid mille v‰‰rtus ei tohi baasi kirjutamisel olla null ega tyhi.
		// Antud loetelu ei pruugi olla t‰ielik - vıimalik et veel mınedel t¸¸pidel pole
		// null ega tyhi v‰‰rtus lubatud. K¸ll aga on siin kirjas kıik t¸¸bid (ja rohkemgi veel)
		// mida hetkel kasutatakse. toivo 15 mar 2003
		int type = getColumnType( tabname, colname);
		switch (type) {
			case Types.TINYINT :
			case Types.SMALLINT :
			case Types.INTEGER :
			case Types.BIGINT :
			case Types.FLOAT :
			case Types.REAL :
			case Types.DOUBLE :
			case Types.NUMERIC :
			case Types.DECIMAL :
			case Types.DATE :
			case Types.TIME :
			case Types.TIMESTAMP :
			case Types.BOOLEAN :
				return true;

			default :
				return false;
		}
	}

	/* (non-Javadoc)
	 * @see or.db.DbConnection#addLimitToQuery(java.lang.StringBuffer, int, int)
	 */
	public void addLimitToQuery ( StringBuffer querybase, int startRow, int nrOfRows) {
	
		if ( (startRow >= 0) && (nrOfRows >= 0) ) {
			//	J‰rgnev on postgresql 'versioon'....
			querybase.append (" limit " + nrOfRows + " offset " + startRow);
		// Kuju mis uuemate Postgres versioonide korral ei pruugi toimida.	
		//	querybase.append (" limit " + nrOfRows + ", " + startRow);
		}			
	}
	
	/**
	 * Gets the pre key.
	 * 
	 * @param tabname the tabname
	 * 
	 * @return the pre key
	 * 
	 * @throws MException the m exception
	 * 
	 * @see moritz.db.DbConnection#getPreKey(String)
	 */
	public long getPreKey(String tabname) throws MException {

		//  Uut unikaalset v‰‰rtust saame lisada Postgressi korral siis kui
		// antud tabeli jaoks on defineeritud andmebaasis vastav sequence
		String seqname = tabname + "_id_seq";

		if ( db.sequenceExist( seqname)	== false)	
			return -1;

		Statement stmt = null;
		String qry = "SELECT nextval('" + tabname + "_id_seq') as key";
		try {
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery( qry);
			if ( rs.next() ) {
				long keyvalue = rs.getLong( "key");
				return keyvalue;
			}
		} catch (SQLException e2) {
			Log.info( "Katse postgres-st uut vıtit k‰tte saada ebaınnestus"
				+ ", query='" + qry + "'" + "\n" + e2.getMessage() );
		} finally {
			try {
				if ( stmt != null)
					stmt.close();
			} catch (SQLException e) {
				Log.info( "Viga Statement-i sulgemisel + \n " + e.getMessage());
			}
		}

		return -1;
	}

	/**
	 * Supports pre key.
	 * 
	 * @return true, if supports pre key
	 * 
	 * @throws MException the m exception
	 * 
	 * @see moritz.db.DbConnection#supportsPreKey()
	 */
	public boolean supportsPreKey() throws MException {
		return true;
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
		return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#getSimpleTimeZFormat()
	 */
	public SimpleDateFormat getSimpleTimeZFormat() throws MException {
		return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssZ");
	}
/*	public String getCodeBase()
	{
		return "ISO-8859-1";
	} */	
}
