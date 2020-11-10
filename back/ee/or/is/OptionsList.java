/*
 * 
 */
package ee.or.is;

import java.util.ArrayList;
//import java.util.Vector;

//import ee.or.plan.GraphNode;
//import ee.or.plan.GraphNodeID;
/**
 * The Class OptionsList.
 * 
 * @author Administrator
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class OptionsList extends ArrayList<OptionElement>
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The b equals. */
	private boolean bEquals = true;
	
	/** The b order. */
	private boolean bOrder = true;
	
	/** The b num. */
	private boolean bNum = false;
	 
	/**
	 * Instantiates a new options list.
	 */
	public OptionsList() {
		super();
	}
	
	/**
	 * Instantiates a new options list.
	 * 
	 * @param iSize the i size
	 */
	public OptionsList( int iSize) {
		super( iSize);
	}
	
	/**
	 * Cmp values.
	 * 
	 * @param s1 the s1
	 * @param s2 the s2
	 * 
	 * @return the int
	 */
	public int cmpValues( String s1, String s2)
	{
		if( bNum ){
			int i1 = 0, iS1 = 0;
			for( ; i1 < s1.length(); ++i1 ){
				char c = s1.charAt( i1);
				if( !Character.isDigit( c) ) break;
				iS1 *= 10;
				iS1 += Character.digit( c, 10);
			}
			if( iS1 == 0){
				iS1 = 0;
			}
			int i2 = 0, iS2 = 0;
			for( ; i2 < s2.length(); ++i2 ){
				char c = s2.charAt( i2);
				if( !Character.isDigit( c) ) break;
				iS2 *= 10;
				iS2 += Character.digit( c, 10);
			}
			if( i1 == 0 && i2 > 0 ) return 1;
			else if( i2 == 0 && i1 > 0 ) return -1;
			else if( i1 > 0 && i2 > 0){
				iS1 -= iS2;
				if( iS1 != 0 ) return iS1;
			}
			if( i1 >= s1.length() ) 	return (i2>=s2.length())? 0: -1;
			if( i2 >= s2.length() ) 	return 1;
			
			iS1 = Character.valueOf( s1.charAt( i1));
			iS2 = Character.valueOf( s2.charAt( i2));
			iS1 -= iS2;
			if( iS1 != 0 ) return iS1;
			
			if( ( i1+1) < s1.length() && (i2 + 1) < s2.length())
				return cmpValues( s1.substring( ++i1), s2.substring( ++i2));
			else
				return s1.substring( i1).compareTo( s2.substring( i2));
		}
		return s1.compareTo( s2);
	}
	
	/**
	 * Insert.
	 * 
	 * @param Obj the obj
	 * 
	 * @return true, if successful
	 */
	public boolean insert( OptionElement Obj)
	{
		String sName = Obj.getUniqueName();
		if( sName == null ) return false;
		if( bOrder ){
			int i = size();
			for( ; --i>=0;){
				String s = (( OptionElement)get( i)).getUniqueName();
				if( !bEquals && cmpValues( s, sName) == 0 ) return false;
				if( cmpValues( s, sName) < 0 ) break;
			}
			add( ++i, Obj);
		}else add( Obj);
		return true;
	}
	
	/**
	 * Insert.
	 * 
	 * @param sName the s name
	 * @param sCode the s code
	 * 
	 * @return true, if successful
	 */
	public boolean insert( String sName, String sCode)
	{
	    return insert( new OptionElementImpl( sName, sCode));
	}
	
	/**
	 * Gets the value.
	 * 
	 * @param i the i
	 * 
	 * @return the value
	 */
	public String getValue( int i)
	{
		OptionElement Obj;
		try {
			Obj = ( OptionElement)get( i);
			return Obj.getValue();
		} catch (RuntimeException e) {
		}
		return null;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @param i the i
	 * 
	 * @return the name
	 */
	public String getName( int i)
	{
		OptionElement Obj;
		try {
			Obj = ( OptionElement)get( i);
			return Obj.getName();
		} catch (RuntimeException e) {
		}
		return null;
	}
	
	/**
	 * Gets the object index by value.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the object index by value
	 */
	public int getObjectIndexByValue( String sValue)
	{
		int i = -1;
		if( sValue != null ) for( i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getValue();
			if( s != null && sValue.equals( s) ) break;
		}
		return i;
	}
	
	/**
	 * Gets the object index by value.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the object index by value
	 */
	public int getObjectIndexByValue( int iValue)
	{
		return getObjectIndexByValue( Integer.toString( iValue));
	}
	
	/**
	 * Gets the object by value.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the object by value
	 */
	public OptionElement getObjectByValue( String sValue)
	{
		if( sValue != null ) for( int i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getValue();
			if( s != null && sValue.equals( s) ) return Obj;
		}
		return null;
	}
	public OptionElement getObjectLast()
	{
		int i = size(); 
		return ( --i >= 0 )? (( OptionElement)get( i)): null;
	}
	
	/**
	 * Sets the object by value.
	 * 
	 * @param NewObj the new obj
	 * 
	 * @return true, if successful
	 */
	public boolean setObjectByValue( OptionElement NewObj)
	{
		if( NewObj != null ){
			String sValue = NewObj.getValue();
			if( sValue != null ) for( int i = size(); --i>=0;){
				OptionElement Obj = (( OptionElement)get( i));
				if( Obj != null ){
					String s = Obj.getValue();
					if( s != null && sValue.equals( s) ){
						set( i, NewObj );
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the object by value.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the object by value
	 */
	public OptionElement getObjectByValue( int iValue)
	{
		return getObjectByValue( Integer.toString( iValue));
	}
	
	/**
	 * Gets the name by value.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the name by value
	 */
	public String getNameByValue( String sValue)
	{
		OptionElement Obj = getObjectByValue( sValue);
		return ( Obj != null)? Obj.getName(): null;
	}
	
	/**
	 * Gets the object by name.
	 * 
	 * @param sName the s name
	 * 
	 * @return the object by name
	 */
	public OptionElement getObjectByName( String sName)
	{
		if( sName != null ) for( int i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getName();
			if( s != null && sName.equalsIgnoreCase( s) ) return Obj;
		}
		return null;
	}
	public OptionsList getObjectsByName( String sName)
	{
		OptionsList aList = null;
		if( sName != null ) for( int i = size(); --i>=0;){
			OptionElement aObj = (( OptionElement)get( i));
			if( aObj != null ){
				String s = aObj.getName();
				if( s != null && s.startsWith( sName)){
					if( aList == null ) aList = new OptionsList();
					aList.insert( aObj);
				}
			}
		}
		return aList;
	}
	
	/**
	 * Gets the name by value.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the name by value
	 */
	public String getNameByValue( int iValue)
	{
		return getNameByValue( Integer.toString( iValue));
	}
	
	/**
	 * Sets the equals.
	 * 
	 * @param bEquals the new equals
	 */
	public void setEquals( boolean bEquals)
	{
		this.bEquals = bEquals;
	}
    
    /**
     * Checks if is unique.
     * 
     * @return true, if is unique
     */
    public boolean isUnique() {
        return !bEquals;
    }
    
    /**
     * Checks if is order.
     * 
     * @return Returns the bOrder.
     */
    public boolean isOrder() {
        return bOrder;
    }
    
    /**
     * Sets the order.
     * 
     * @param order the new order
     */
    public void setOrder(boolean order) {
        bOrder = order;
    }
    
    /**
     * Gets the data object.
     * 
     * @param CurDataObject the cur data object
     * @param sClassName the s class name
     * @param bEdit the b edit
     * 
     * @return the data object
     */
    public DataObject getDataObject( DataObject CurDataObject, String sClassName, boolean bEdit)
    {
		if( CurDataObject == null || !CurDataObject.isDataObject( sClassName) ){
//			if( bEdit ){
				try {
					CurDataObject = ( DataObject)( Class.forName( sClassName).newInstance());
					CurDataObject.setDataObjects( this);
				} catch( Exception E) {
					Log.error( E, true);
					CurDataObject = null;
				}
//			}else if( size() > 0 )
//				CurDataObject = ( DataObject)get( 0);
//			}else
//				CurDataObject = null;
		}
		return CurDataObject;
    }
/*	public boolean createIndex()
	{
		int iSize = 100;
		if( size() > iSize ) iSize = size();
		Index = new Vector( iSize, iSize/10); // index of vector	
		return true;
	}
	public void reindex()
	{
		Index.removeAllElements();
		for( int i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getValue();
			if( s != null && sValue.equals( s) ) return Obj;
		}
		
		for( int i = DataObjects.size(); --i>=0; )
		{ 
			GraphNode Node = DataObjects.getAt( i);
			if( Node == null ) continue;
			GraphNodeID NodeID = new GraphNodeID( iNode, Node.getID());
			int iN = findIndex( Node.getID()); 
			NodesInd.add( iN, NodeID);		//
		} 
	} */
	/**
 * Checks if is b num.
 * 
 * @return true, if is b num
 */
    public boolean isNum() {
		return bNum;
	}
	
	/**
	 * Sets the b num.
	 * 
	 * @param num the new b num
	 */
	public void setNum(boolean num) {
		bNum = num;
	}
	public int getMaxValue()
	{
		int iMaxValue = 0;
	
		for( int i = size(); --i>=0;){
			OptionElement aObj = (( OptionElement)get( i));
			if( aObj != null && Integer.parseInt( aObj.getValue()) > iMaxValue )
				iMaxValue = Integer.parseInt( aObj.getValue());
		}
		return iMaxValue;

	}
	public void removeObjectByValue( int iValue)
	{
		removeObjectByValue( Integer.toString( iValue));
	}
	public void removeObjectByValue( String sValue)
	{
		if( sValue != null ) for( int i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getValue();
			if( s != null && sValue.equals( s) ){ remove( i); return;}
		}
	}
	public void removeObjectByName( String sValue)
	{
		if( sValue != null ) for( int i = size(); --i>=0;){
			OptionElement Obj = (( OptionElement)get( i));
			if( Obj == null ) continue;
			String s = Obj.getName();
			if( s != null && sValue.equals( s) ){ remove( i); return;}
		}
	}

}
