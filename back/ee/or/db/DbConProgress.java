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
 * The Class DbConProgress.
 * 
 * @author toivo
 * 
 * Andmebaasi yhendus mis arvestab Progress baasi eripära.
 */
public class DbConProgress extends DbConnection {

	/**
	 * Constructor for DbConProgres.
	 * 
	 * @param con the con
	 * @param db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbConProgress( Database db, Connection con) throws MException {
		super( db, con);
		try {
			con.setAutoCommit( false);		// NB !!! Vaikimisi on Autocommit on-i lykatud.
		} catch (SQLException e) {
			throw new MException( "dbc2502", "setAutoCommit( true) ebaõnnestus", null, null, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#close()
	 */
	public void close() throws MException {
		try {

			// Siiani lihtsalt tegime con.close() mis tagastas antud yhenduse pooli.
			// Nüüd üritame võidelda prahiga ja teeme kõik et mittevajalikest asjadest lahti saada.
			// Näiteks teem rollback-i ja puhatsma hoiatused.
			// Ka üritame teatud aja tagant yhenduse täitsa kinni panemist.
			// toivo 21 sept 2003

//			con.rollback();
			con.clearWarnings();
			con.close();		// tagastab antud yhenduse pooli 
			con = null;
/*
			if (con instanceof org.apache.commons.dbcp.PoolableConnection) {
				org.apache.commons.dbcp.PoolableConnection pcon = (org.apache.commons.dbcp.PoolableConnection) con;

				// Kahjuks ei saa niisama sulgeda, midagi peaks veel tegema et automaatselt
				// peale sulgemist toimuks ka uuesti avamine.
		//		pcon.reallyClose();
			}
*/		
		} catch (SQLException e) {
			throw new MException( "dbc2506", "Andmebaasi yhenduse sulgemine ebaõnnestus", null,null, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#skipEmpty(java.lang.String, java.lang.String)
	 */
	protected boolean skipEmpty(String tabname, String colname) throws MException {

		// Järgnevalt kõik tüübid mille väärtus ei tohi baasi kirjutamisel olla null ega tyhi.
		// Antud loetelu ei pruugi olla täielik - võimalik et veel mõnedel tüüpidel pole
		// null ega tyhi väärtus lubatud. Küll aga on siin kirjas kõik tüübid (ja rohkemgi veel)
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

		//  Uut unikaalset väärtust saame lisada Postgressi korral siis kui
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
			Log.info( "Katse postgres-st uut võtit kätte saada ebaõnnestus"
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
		return false;
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
	 * @see or.db.DbConnection#getTablePro()
	 */
	public String getTablePro()
	{
		return "PUB.";
	}

	
}
