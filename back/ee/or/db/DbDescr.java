/*
 * 
 */
package ee.or.db;

import java.io.Serializable;
// TODO: Auto-generated Javadoc

import ee.or.is.GlobalData;

/**
 * The Class DbDescr.
 * 
 * @author TA
 * 
 * Andmebaasi kirjeldus, kasutatakse p�hiliselt vaid alguses
 * andmebaasi avamisel.
 */
public class DbDescr implements Cloneable, Serializable 
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The drivername. */
	protected String	drivername = "org.postgresql.Driver";		// näiteks "org.gjt.mm.mysql.Driver"
	
	/** The dbtypestr. */
	protected String	dbtypestr = "postgresql";	// see on tegelikult andmebaasi tüübist sõltuv, näiteks "jdbc:mysql://"
	
	/** The dbhost. */
	protected String	dbhost = null;
	
	/** The dbname. */
	protected String	dbname = null;
	public void setName( String sName){ this.dbname = sName;}
	
	/** The dbuser. */
	protected String	dbuser = null;
	
	/** The dbpasswd. */
	protected String	dbpasswd = null;
	
	/** The dbclass. */
	protected String	dbclass = null;
	
	/** The s geometry. */
	protected String	sGeometry = "3301";
	
	/** The s codebase. */
	protected String	sCodebase = null;
	
	/** The s encoding. */
	protected String	sEncoding = "UTF-8";
	
	/** The s date format. */
	protected String 	sDateFormat = null;
	
	/** The s time format. */
	protected String 	sTimeFormat = null;
	
	/** The a sub db descr. */
	protected DbDescr  aSubDbDescr = null;

	private String sSchemaName = null;
	public String getSchemaName(){	return sSchemaName;}
	public void setSchemaName( String sSchemaName){ this.sSchemaName = sSchemaName;}

	private String sTimeout = "300";
	public String getTimeout(){	return sTimeout;}
	public void setTimeout( String sTimeout){ this.sTimeout = sTimeout;}
	/**
	 * Instantiates a new db descr.
	 * 
	 * @param drivername the drivername
	 * @param dbtypestr the dbtypestr
	 * @param dbhost the dbhost
	 * @param dbname the dbname
	 * @param dbuser the dbuser
	 * @param dbpasswd the dbpasswd
	 */
	public DbDescr(String drivername, String dbtypestr, String dbhost, String dbname, String dbuser, String dbpasswd) {
		this.drivername = drivername;
		this.dbtypestr = dbtypestr;
		this.dbhost = dbhost;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpasswd = dbpasswd;
	}

	/**
	 * Constructor DbDescr.
	 */
	public DbDescr() {
	}

	/**
	 * Copy.
	 * 
	 * @return the db descr
	 */
	public DbDescr copy() {
		try {
			return (DbDescr) this.clone();
		} catch (CloneNotSupportedException e) {
			// Kuna DbDescr on juba defineeritud kui implements Cloneable siis pole vast vaja.
			return null;
		}
	}

	/**
	 * Gets the database url.
	 * 
	 * @return the database url
	 */
	public String getDatabaseUrl() {
		StringBuffer url = new StringBuffer(100);
		url.append( "jdbc:");
		url.append( dbtypestr);		
		if( dbtypestr.indexOf("progress") >= 0 || dbtypestr.indexOf( "oracle") >= 0 ){
			url.append( ":"); 
			url.append( dbhost);		
			if( dbname != null && dbname.length() > 0 ) url.append( ":"); 
		}else if( dbtypestr.indexOf("odbc") >= 0 || dbtypestr.indexOf("mdb") >= 0 ){
			url.append( ":");
// jdbc:jstels:mdb:c:/mdb_directory/test.mdb
		}else if ( dbtypestr.indexOf("paradox") >= 0) {
			url.append( ":/"); 
		}else{
			url.append( "://");
			url.append( dbhost);		
			url.append( "/");
		}
//		 jdbc:oracle:thin:@10.1.15.10:1521:stkeskus 
// type	= oracle:thin
// host = @10.1.15.10:1521 
// name = stkeskus
		url.append( dbname);
//		url.append( "?user=");		url.append( dbuser);
//		url.append( "&password=");	url.append( dbpasswd);

		return url.toString();
	}

	/**
	 * Sets the dbhost.
	 * 
	 * @param dbhost The dbhost to set
	 */
	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}

	/**
	 * Sets the dbname.
	 * 
	 * @param dbname The dbname to set
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	/**
	 * Sets the dbclass.
	 * 
	 * @param dbclass the new dbclass
	 */
	public void setDbclass(String dbclass) {
		this.dbclass = dbclass;
	}
	
	/**
	 * Sets the db geometry.
	 * 
	 * @param sGeometry the new db geometry
	 */
	public void setDbGeometry(String sGeometry) {
		this.sGeometry = sGeometry;
	}
	
	/**
	 * Gets the db geometry.
	 * 
	 * @return the db geometry
	 */
	public String getDbGeometry() {
		return sGeometry;
	}
	public int getProjection() {
		return GlobalData.getInt( sGeometry);
	}
	
	/**
	 * Gets the dbclass.
	 * 
	 * @return the dbclass
	 */
	public String getDbclass() {
		return dbclass;
	}

	/**
	 * Sets the dbpasswd.
	 * 
	 * @param dbpasswd The dbpasswd to set
	 */
	public void setDbpasswd(String dbpasswd) {
		this.dbpasswd = dbpasswd;
	}
	
	/**
	 * Sets the dbpasswd.
	 * 
	 * @param dbpasswd the new dbpasswd
	 */
	public void setDbpasswd( char[] dbpasswd) {
		this.dbpasswd = new String( dbpasswd);
		
//		this.dbpasswd = dbpasswd;
	}

	/**
	 * Sets the dbuser.
	 * 
	 * @param dbuser The dbuser to set.
	 */
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	
	/**
	 * Sets the drivername.
	 * 
	 * @param drivername the new drivername
	 */
	public void setDrivername( String drivername) {
		this.drivername = drivername;
	}

	/**
	 * Returns the dbhost.
	 * 
	 * @return String
	 */
	public String getDbhost() {
		return dbhost;
	}

	/**
	 * Returns the dbname.
	 * 
	 * @return String
	 */
	public String getDbname() {
		return dbname;
	}

	/**
	 * Returns the dbuser.
	 * 
	 * @return String
	 */
	public String getDbuser() {
		return dbuser;
	}
	
	/**
	 * Gets the pSW.
	 * 
	 * @return the pSW
	 */
	public String getPSW() {
		return dbpasswd;
	}

	/**
	 * Sets the dbtypestr.
	 * 
	 * @param dbtypestr The dbtypestr to set
	 */
	public void setDbtypestr(String dbtypestr) {
		this.dbtypestr = dbtypestr;
/*		if ( dbtypestr.equalsIgnoreCase( "postgresql") )
			drivername = "org.postgresql.Driver";
		else
			drivername = "com.mysql.jdbc.Driver"; */
	}

	/**
	 * Returns the dbtypestr.
	 * 
	 * @return String
	 */
	public String getDbtypestr() {
		return dbtypestr;
	}

	/**
	 * Returns the drivername.
	 * 
	 * @return String
	 */
	public String getDrivername() {
		return drivername;
	}

    /**
     * Gets the codebase.
     * 
     * @return the codebase
     */
    public String getCodebase() {
        return sCodebase;
    }
    
    /**
     * Sets the codebase.
     * 
     * @param codebase the new codebase
     */
    public void setCodebase( String codebase) {
        sCodebase = codebase;
    }
    
    /**
     * Gets the date format.
     * 
     * @return the date format
     */
    public String getDateFormat() {
        return sDateFormat;
    }
    
    /**
     * Sets the date format.
     * 
     * @param sDateFormat the new date format
     */
    public void setDateFormat(String sDateFormat) {
        this.sDateFormat = sDateFormat;
    }

	/**
	 * Gets the encoding.
	 * 
	 * @return the encoding
	 */
	public String getEncoding() {
		return sEncoding;
	}
    
    /**
     * Sets the encoding.
     * 
     * @param sEncoding the new encoding
     */
    public void setEncoding( String sEncoding) {
        this.sEncoding = sEncoding;
    }

	/**
	 * Gets the date time format.
	 * 
	 * @return the date time format
	 */
	public String getDateTimeFormat() {
		return sTimeFormat;
	}

	/**
	 * Sets the date time format.
	 * 
	 * @param timeFormat the new date time format
	 */
	public void setDateTimeFormat(String timeFormat) {
		sTimeFormat = timeFormat;
	}

	/**
	 * Gets the sub db descr.
	 * 
	 * @return the sub db descr
	 */
	public DbDescr getSubDbDescr() {
		return aSubDbDescr;
	}

	/**
	 * Sets the sub db descr.
	 * 
	 * @param subDbDescr the new sub db descr
	 */
	public void setSubDbDescr( DbDescr subDbDescr) {
		
		aSubDbDescr = subDbDescr;
	}
}
