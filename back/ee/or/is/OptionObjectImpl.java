/*
 * 
 */
package ee.or.is;

import java.util.ArrayList;


/**
 * The Class OptionElementImpl.
 * 
 * @author or 30.12.2004
 */
public class OptionObjectImpl extends OptionElementImpl 
{
	private OptionElement aObject = null;

	public OptionObjectImpl( OptionElement aObject, String sValue)
	{
		super( aObject.getName(), sValue);
		this.aObject = aObject;
	}
	public OptionObjectImpl( OptionElement aObject, int iValue)
	{
		super( aObject.getName(), iValue);
		this.aObject = aObject;
	}
	public OptionElement getObject(){ return aObject;}
	
	public static boolean insertOrder( ArrayList<OptionObjectImpl>aList , int iLimit, OptionElement aObject, int iValue)
	{
		if( aList != null ){
			for( int i = 0; i < aList.size(); ++i){
				OptionObjectImpl aCurObject = aList.get( i);
				int iCurValue = aCurObject.getIntValue();
				if( iCurValue > iValue ){
					aList.add( i, new OptionObjectImpl( aObject, iValue));
					if(  iLimit > 0 && aList.size() > iLimit ) aList.remove( iLimit);
					return true;
				}
			}
			if( iLimit <= 0 || aList.size() < iLimit ) 
				aList.add( new OptionObjectImpl( aObject, iValue));
		}
		return true;
	}
	public static OptionObjectImpl getByName( ArrayList<OptionObjectImpl> aList, String sName)
	{
		if( aList != null ){
			for( int i = 0; i < aList.size(); ++i){
				OptionObjectImpl aCurObject = aList.get( i);
				if( aCurObject.getName().equalsIgnoreCase( sName) ) return aCurObject;
			}
		}
		return null;
	}
	public static OptionElement getObjectByName( ArrayList<OptionObjectImpl> aList, String sName)
	{
		OptionObjectImpl aCurObject = OptionObjectImpl.getByName( aList, sName);
		return ( aCurObject != null )? aCurObject.getObject(): null;
	}
	public static OptionElement getObject( ArrayList<OptionObjectImpl> aList, int i)
	{
		return ( aList != null && i >= 0 && i < aList.size() )?  aList.get( i).aObject: null;
	}
	public static String getValue( ArrayList<OptionObjectImpl> aList, int i)
	{
		return ( aList != null && i >= 0 && i < aList.size() )?  aList.get( i).getValue(): null;
	}
	public static int getIntValue( ArrayList<OptionObjectImpl> aList, int i)
	{
		return ( aList != null && i >= 0 && i < aList.size() )?  aList.get( i).getIntValue(): 0;
	}
	public static ArrayList<OptionObjectImpl> getSubList( ArrayList<OptionObjectImpl> aList, int iStart, int iEnd)
	{
		ArrayList<OptionObjectImpl> aSubList = null;
		if( aList != null && iEnd > iStart ){
			aSubList = new ArrayList<OptionObjectImpl> ();
			for( int i = 0; i < aList.size(); ++i){
				if( i >= iStart && i < iEnd ){
					OptionObjectImpl aCurObject = aList.get( i);
					if( aCurObject != null ) aSubList.add( aCurObject);
				}
			}
		}
		return aSubList;
	}
	public static ArrayList<OptionElement> getObjects( ArrayList<OptionObjectImpl> aList, int n)
	{
		ArrayList<OptionElement> aObjects = null;
		if( aList != null ){
			aObjects = new ArrayList<OptionElement> ();
			if( aList.size() < n) n = aList.size();
			for( int i = 0; i < n; ++i){
				OptionObjectImpl aCurObject = aList.get( i);
				if( aCurObject != null ) aObjects.add( aCurObject.getObject());
			}
		}
		return aObjects;
	}
}
