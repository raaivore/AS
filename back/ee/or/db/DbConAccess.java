/*
 * 
 */
package ee.or.db;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import ee.or.is.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DbConAccess.
 * 
 * @author or 09.04.2009
 * Andmebaasi yhendus mis arvestab Access baasi eripära.
 */
public class DbConAccess extends DbConnection 
{
	
	/**
	 * Instantiates a new db con access.
	 * 
	 * @param db the db
	 * @param con the con
	 * 
	 * @throws MException the m exception
	 */
	public DbConAccess( Database db, Connection con) throws MException 
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
