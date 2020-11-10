/*
 * 
 */
package ee.or.db;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConParadox.
 * 
 * @author or 15.10.2007
 * Andmebaasi yhendus mis arvestab Paradox baasi eripära.
 */
public class DbConParadox extends DbConnection 
{
	
	/**
	 * Constructor for DbConProgres.
	 * 
	 * @param con the con
	 * @param db the db
	 * 
	 * @throws MException the m exception
	 */
	public DbConParadox( Database db, Connection con) throws MException 
	{
		super( db, con);
	}
	
	/* (non-Javadoc)
	 * @see or.db.DbConnection#supportsPreKey()
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
}
