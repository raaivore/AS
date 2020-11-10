/*
 * Created on 5.05.2006 by or
 *
 */
package ee.or.is;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;
import ee.or.db.DbAccess;


/**
 * The Class Person.
 */
public class Person extends DataObject // implements OptionElement
{
    /** The first name. */
    private String sFName = null;
    public String getFName(){	return sFName;}
	public void setFName( String sFName){ this.sFName = sFName;}

	/** The last name. */
    private String sLName = null;
	public String getLName(){	return sLName;}
	public void setLName(String sLName){ this.sLName = sLName;}
    
    /** The s phone. */
    private String sPhone = null;
    
    /** The s mail. */
    private String sMail = null;
    
    /** The s person code. */
    private String sPersonCode = null;
    public void setPersonCode( String sPersonCode){	this.sPersonCode = sPersonCode;}
	public String toString()
	{
		return sPersonCode + " " + sFName + " " + sLName;
	}

	/** The Last date. */
    private java.sql.Date LastDate = null;

	/**
	 * Instantiates a new person.
	 */
	public Person() 
    {
        super();
    }
	
	/**
	 * Instantiates a new person.
	 * 
	 * @param Persons the persons
	 */
	public Person( OptionsList Persons) 
	{
		super( Persons);
	}
	public synchronized void set( DataObject DO)
	{
		Person aPerson = ( Person)DO;
		sFName = aPerson.sFName;
		sLName = aPerson.sLName;
		sPhone = aPerson.sPhone;
		sMail = aPerson.sMail;
		sPersonCode = aPerson.sPersonCode;
	}
	public boolean load( DbAccess DbIn) throws MException  
	{
		setID( DbIn.getInt( "id"));
		sFName = DbIn.getString( "fname");
		sLName = DbIn.getString( "lname");
		sPhone = DbIn.getString( "phone");
		sMail = DbIn.getString( "mail");
		sPersonCode = DbIn.getString( "code");
		return true;
	}
	public void save( DbAccess DbIn) throws MException 
	{
		DbIn.setString( "fname", sFName);
		DbIn.setString( "lname", sLName);
		DbIn.setString( "code", sPersonCode);
		DbIn.setString( "phone", sPhone);
		DbIn.setString( "mail", sMail);
	}
	public void save( HttpServletRequest Request, Sight Sight)
	{
	    sFName = Request.getParameter( "FNAME");
	    sLName = Request.getParameter( "LNAME");
	    sPhone = Request.getParameter( "PHONE");
	    sMail = Request.getParameter( "MAIL");
	    sPersonCode = Request.getParameter( "CODE");
	}
	public void setXML( DOMData Doc, Node Root)
    {
		Doc.addChildNode( Root, "id", getID()); 
		Doc.addChildNode( Root, "name", getFullName()); 
		Doc.addChildNode( Root, "f_name", sFName); 
		Doc.addChildNode( Root, "l_name", sLName); 
		Doc.addChildNode( Root, "pcode", getPersonCode()); 
		Doc.addChildNode( Root, "phone", sPhone); 
		Doc.addChildNode( Root, "mail", sMail); 
    }
	public String getName()
	{
		return getFullName();
	}
	public void setName( String sName)
	{
		sFName = "";
		sLName = sName;
	}
	public String getUniqueName()
	{
		return getFullName();
	}
	public String getFullName() 
	{
		return sFName + " " + sLName;
	}
    public String getPersonCode() {
        return sPersonCode;
    }
    public String getMail() {
        return sMail;
    }
    public String getPhone() {
        return sPhone;
    }
	public int getPersonID() {
		return getID();
	}
	public boolean isOld()
	{
		return LastDate != null;
	}
	public java.sql.Date getLastDate() {
		return LastDate;
	}
	public void setLastDate( java.sql.Date LastDate) 
	{
		this.LastDate = LastDate;
	}
	public void setLastDate( ) 
	{
		LastDate = GlobalDate.getCurrentDate();
	}

}
