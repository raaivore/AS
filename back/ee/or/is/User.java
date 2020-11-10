/*
 * @author or
 */
package ee.or.is;

import javax.servlet.http.*;

import org.w3c.dom.Node;
import ee.or.db.*;

public class User extends DataObject 
{
    private String 	sRoleName = null;
	public String getRoleName(){	return (sRoleName == null)? (isAdmin()? "admin": "user"): sRoleName;}
	public void setRoleName( String sName){	sRoleName = sName;}

    private int 	iRights = 0;
	public int 		iUserGroupID = 0;
	private String 	sUserIP = null;
	private String 	sPSW = null;
	private int 	iDebug = 0;
	private Person 	aPerson = null;
	private OptionsList Persons = null;
	private boolean bComp = false;
	private String sLang = null;
	
	public String getQuery()
	{
		return "SELECT * FROM users WHERE name='" + getName() + "'";
	}

	public User() {	super();}
	public User( OptionsList Persons) 
	{
		super();
		this.Persons = Persons;
	}
	public User( HttpServletRequest aRequest) 
	{
		super();
		init( aRequest);
	}
	public User( HttpServletRequest aRequest, Database aDb) 
	{
		super();
		init( aRequest);
		load( aDb);
	}
	public void init( HttpServletRequest aRequest) 
	{
		setName( aRequest.getRemoteUser());
		if( getName() == null ) setName( aRequest.getSession().getId());
		sUserIP = aRequest.getRemoteAddr();
		iRights = 0;
		if( aRequest.isUserInRole( "admin") ){
		    iRights = 3;
		}else if( aRequest.isUserInRole( "user") ){
		    iRights = 2;
		}
	}
    public boolean load( DbAccess aDbIn) throws MException  
	{
    	boolean bRet = super.load( aDbIn);
    	if( bRet ){
    		String sPersonName = aDbIn.getString( "person_name");
    		if( sPersonName != null ){
    			aPerson = new Person();
    			aPerson.setName( sPersonName);
    		}
    	}
    	return bRet;
	}	
	/**
	 * Sets the person.
	 * 
	 * @param Db the db
	 * @param iPersonID the i person id
	 */
	public void setPerson( Database Db, int iPersonID)
	{
		if( Persons != null ) aPerson = ( Person)Persons.getObjectByValue( iPersonID);
		else{
			aPerson = new Person();
			aPerson.load( Db, iPersonID);
		}
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#createDataObject()
	 */
	public DataObject createDataObject()
	{
		return new User( Persons);
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#set(or.is.DataObject)
	 */
	public synchronized void set( DataObject DO)
	{
		setName( DO.getName());
	}
	public String getLang() {
		return sLang;
	}

	public void setLang(String sLang) {
		this.sLang = sLang;
	}
	/**
	 * Gets the user group id.
	 * 
	 * @return the user group id
	 */
	public int getUserGroupID()
	{
		return iUserGroupID;
	}
	
	/**
	 * Checks if is admin.
	 * 
	 * @return true, if is admin
	 */
	public boolean isAdmin()
	{
		return (iRights&1) != 0;
	}
	
	/**
	 * Checks if is user.
	 * 
	 * @return true, if is user
	 */
	public boolean isUser()
	{
		return (iRights&2) != 0;
	}
	
	/**
	 * Checks if is graph.
	 * 
	 * @return true, if is graph
	 */
	public boolean isGraph()
	{
		return (iRights&2) != 0;
	}
	
	/**
	 * Checks for rights.
	 * 
	 * @return true, if successful
	 */
	public boolean hasRights()
	{
		return iRights != 0;
	}
	/**
 * Update.
 * 
 * @param DbOut the db out
 * @param Request the request
 * 
 * @return true, if successful
 * 
 * @throws MException the m exception
 */
public boolean update( DbAccess DbOut, HttpServletRequest Request) throws MException
	{
		String sRet = Request.getParameter( "ID_RETURN");
		String sUser = Request.getParameter( "ID_USER");
		String sRoll = Request.getParameter( "ID_ROLL");
		String sPSW = Request.getParameter( "ID_PSW");
		int iUserID = 0;
		if( sRet.equals( "0") ){
			DbOut.setTable( "kasutaja");
			DbOut.addNew();
			DbOut.setInt( "isik_id", 1); 
			DbOut.setInt( "user_group_id", iUserGroupID); 
			DbOut.setString( "tunnus", sUser); 
			DbOut.setString( "roll", sRoll); 
			DbOut.setString( "parool", sPSW); 
			DbOut.update();
			DbOut.commit();
			iUserID = DbOut.getInt( "id");
		}else{
			iUserID = Integer.parseInt( sRet);
			if( iUserID > 0 && DbOut.select( "kasutaja", "id=" + iUserID, null) ){
				DbOut.edit();
				DbOut.setString( "tunnus", sUser); 
				DbOut.setString( "roll", sRoll); 
				DbOut.setString( "parool", sPSW); 
				DbOut.update();
				DbOut.commit();
				if( iUserID == getID() ) setName( sUser);
			}
		}
		if( iUserID <= 0 ) return false;
		DbAccess DbE = new DbAccess( DbOut);
		DbE.select( "car_event_type", null, null);
		boolean bRec = DbOut.select( "car_event_user", "isik_id=" + iUserID, null);
		for( int i=0; i<6; ++i){
			String sEvent = Request.getParameter( "ID_EVENT" + i);
			String sMail = Request.getParameter( "ID_MAIL" + i);
			if( sEvent != null && sEvent.length()>0 && sMail != null && sMail.length()>0 && 
				DbE.find( "name", sEvent) ){
				
				int iEventID = DbE.getInt( "id");
				if( bRec )	DbOut.edit();
				else{
					DbOut.addNew();
					DbOut.setInt( "isik_id", iUserID); 
				}
				DbOut.setInt( "car_event_type_id", iEventID); 
				DbOut.setString( "email", sMail); 
				DbOut.update();
				DbOut.commit();
				if( bRec ) bRec = DbOut.next();
			}	
		}
		if( bRec ) do{
			DbOut.delete();
			DbOut.commit();
		}while( DbOut.next() );
		DbE.close();
		return true;
	}
	
	/**
	 * Logout.
	 * 
	 * @param aSight the a sight
	 */
	public void logout( Sight aSight)
	{
	    aSight.log( "logout User " + getName() );
	}	
	
	/**
	 * Gets the user id.
	 * 
	 * @return the user id
	 */
	public int getUserID() {
		return getID();
	}
	
	/**
	 * Gets the rights.
	 * 
	 * @return the rights
	 */
	public int getRights() 
	{
		return iRights;
	}
	
	/**
	 * Sets the rights.
	 * 
	 * @param iRights the new rights
	 */
	public void setRights( int iRights) 
	{
		this.iRights = iRights;
	}
	
	/**
	 * Sets the user id.
	 * 
	 * @param userID the new user id
	 */
	public void setUserID( int userID) {
		setID( userID);
	}
/*	public String getName()
	{
		return sUser;
	}
	public void setName( String sName)
	{
		sUser = sName;
	}*/
	/**
 * Gets the full name.
 * 
 * @return the full name
 */
	public String getFullName()
	{
		return getName();
	}
	public String getLogName()
	{
		return getName();
	}
	
	/**
	 * Gets the role name.
	 * 
	 * @return the role name
	 */
/*	public String getValue()
	{
		return Integer.toString( iUserID);
	} */
/*	public DOMData FormXML(  HttpServletRequest Request, Sight Sight)
	{
	    return null;
	} */
    /**
 * Gets the user ip.
 * 
 * @return the user ip
 */
public String getUserIP() {
        return sUserIP;
    }
    
    /**
     * Gets the name ip.
     * 
     * @return the name ip
     */
    public String getNameIP() {
        StringBuffer Name = new StringBuffer();
        if( getName() != null ) Name.append( getName());
        Name.append( '[');
        Name.append( sUserIP);
        Name.append( ']');
        return Name.toString();
    }
    
    /**
     * Gets the pSW.
     * 
     * @return the pSW
     */
    public String getPSW() {
        return sPSW;
    }
    
    /**
     * Sets the pSW.
     * 
     * @param spsw the new pSW
     */
    public void setPSW(String spsw) {
        sPSW = spsw;
    }
	
	/**
	 * Sets the user ip.
	 * 
	 * @param Request the new user ip
	 */
	public void setUserIP( HttpServletRequest Request) {
		sUserIP = Request.getRemoteAddr();
	}
	
	/**
	 * Gets the debug.
	 * 
	 * @return the debug
	 */
	public int getDebug() {
		return iDebug;
	}
	
	/**
	 * Sets the debug.
	 * 
	 * @param debug the new debug
	 */
	public void setDebug(int debug) {
		iDebug = debug;
	}
    
    /**
     * Gets the person id.
     * 
     * @return the person id
     */
    public int getPersonID() {
        return (aPerson!=null)? aPerson.getID(): 0;
    }
    
    /**
     * Gets the person.
     * 
     * @return the person
     */
    public Person getPerson() {
        return aPerson;
    }
    
    /**
     * Sets the person.
     * 
     * @param person the new person
     */
    public void setPerson( Person person) {
        aPerson = person;
    } 
    
    /**
     * Gets the persons.
     * 
     * @return the persons
     */
    public OptionsList getPersons()
    {
    	return Persons;
    }
    
    /**
     * Sets the persons.
     * 
     * @param Persons the new persons
     */
    public void setPersons( OptionsList Persons)
    {
    	this.Persons = Persons;
    }
	
	/**
	 * Checks if is comp.
	 * 
	 * @return true, if is comp
	 */
	public boolean isComp()
	{
		return bComp;
	}
	public void setXML( DOMData Doc, Node Root)
    {
		super.setXML( Doc, Root);
		Doc.addChildNode( Root, "role_name", getRoleName()); 
		Doc.addChildNode( Root, "rights", getRights()); 
		if( aPerson != null ) aPerson.setXML( Doc, Doc.addChildNode( Root, "person"));
    }
}
