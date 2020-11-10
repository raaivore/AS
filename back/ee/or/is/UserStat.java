/*
 * 
 */
package ee.or.is;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Node;
import ee.or.db.DbAccess;
import ee.or.is.MException;


// TODO: Auto-generated Javadoc
/**
 * The Class UserStat.
 */
public class UserStat 
{
	Sight aSight = null; /** The a sight. */
	private int iID = 0; /** The i id. */
	private Date aSTime = null;				// session start time
	private int iTime = 0;					// session length in seconds
	private int iSaveTime = 0;
	private int iRequest = 0;
	private int iError = 0;
	private String sBrowser = null;
	private String sIP = null;
	/**
	 * Instantiates a new user stat.
	 * 
	 * @param aSight the a sight
	 * @param aRequest the a request
	 */
	public UserStat(){}
	public UserStat( Sight aSight,  HttpServletRequest aRequest) 
	{
		this.aSight = aSight;
		aSTime = new Date();
		save( aRequest);
	}
	
	/**
	 * Save.
	 * 
	 * @param DbIn the db in
	 * 
	 * @throws MException the m exception
	 */
	public void save( DbAccess DbIn) throws MException 
	{
	}
	public void saveMore( DbAccess DbIn) throws MException 
	{
	}
	
	
	/**
	 * Request.
	 * 
	 * @param aRequest the a request
	 */
	public void request( HttpServletRequest aRequest) 
	{
		iTime = ( int)(( new Date()).getTime() - aSTime.getTime()) / 1000;
		++iRequest;
		if( iTime - iSaveTime > 3600 ){
			save( aRequest);
			iSaveTime = iTime;
		}
	}
	
	/**
	 * Save.
	 * 
	 * @param aRequest the a request
	 */
	public void load( DbAccess DbIn) throws MException 
	{
		iID = DbIn.getInt( "id");
		aSTime = DbIn.getDateTime( "stime");
		if( aSTime == null ) Log.log_error( DbIn.getString( "stime"));
		iTime = DbIn.getInt( "ltime");
		sBrowser = DbIn.getString( "browser");
		sIP = DbIn.getString( "user_ip");
		iRequest = DbIn.getInt( "request");
		iError = DbIn.getInt( "error");
	}
	public void save( HttpServletRequest aRequest)
	{
		DbAccess DbIn = null;
		try {
			DbIn =  new DbAccess( aSight.getDatabase());
			DbIn.setTable( "stat_user", "id");
			if( iID > 0 ){
				DbIn.edit( iID);
				DbIn.setInt( "ltime", iTime);
				DbIn.setInt( "error", iError);
				DbIn.setInt( "request", iRequest);
			}else{
				DbIn.addNew();
				DbIn.setTime( "stime", aSTime);
				if( aRequest != null ) DbIn.setString( "browser", aRequest.getHeader("User-Agent"));
				if( aSight.getUser() != null ){
					if( aSight.getUser().getID() > 0 ) DbIn.setInt( "user_id", aSight.getUser().getID());
					DbIn.setString( "user_ip", aSight.getUser().getUserIP());
					if( aSight.getUser().getUserGroupID() > 0 ) DbIn.setInt( "user_group_id", aSight.getUser().getUserGroupID());
				}else if( aRequest != null ){
					DbIn.setString( "user_ip", aRequest.getRemoteAddr());
				}
			}
			save( DbIn);
			DbIn.update();
			if( iID <= 0 ) iID = DbIn.getID();
			saveMore( DbIn);
		} catch (MException e) {
		}
		if( DbIn != null ) DbIn.close();
	}
	public void add( UserStat aStat) throws MException 
	{
		iRequest += aStat.iRequest;
		iError += aStat.iError;
	}
	public void setXML( DOMData Doc, Node Root)
    {
		Doc.addChildNode( Root, "id", iID); 
		Doc.addChildNode( Root, "request", iRequest); 
		Doc.addChildNode( Root, "error", iError); 
		Doc.addChildNode( Root, "browser", sBrowser); 
		Doc.addChildNode( Root, "user_ip", sIP); 
		if( aSTime != null ) Doc.addChildNodeDateTime( Root, "start_time", aSTime); 
		Doc.addChildNodeTimeS( Root, "ltime", iTime); 
	}
	
	/**
	 * Save.
	 */
	public void save(){
		save( (  HttpServletRequest)null);
	}
	
	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 */
	public int getID() {
		return iID;
	}
	
	/**
	 * Gets the sight.
	 * 
	 * @return the sight
	 */
	public Sight getSight() {
		return aSight;
	}
	public void incErrors(){ ++iError;}
}
