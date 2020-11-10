/*
 * 
 */
package ee.or.is;
/**
 * @author or
 *
 */
import java.util.Vector;
import java.util.*;

/**
 * The Class OrderedVector.
 */
public class OrderedVector extends Vector<Object> 
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Index. */
	Vector<Integer> Index = null;
	
	/** The b duplicates. */
	public boolean bDuplicates = false;

	/**
	 * Instantiates a new ordered vector.
	 * 
	 * @param iSize the i size
	 */
	public OrderedVector( int iSize)
	{
		super( iSize, 1); // vector
		Index = new Vector<Integer>( iSize, 1); // index of vector
	} 
	
	/**
	 * Instantiates a new ordered vector.
	 * 
	 * @param iSize the i size
	 * @param iAdd the i add
	 */
	public OrderedVector( int iSize, int iAdd)
	{
		super( iSize, iAdd); // vector
		Index = new Vector<Integer>( iSize, iAdd); // index of vector
	}
	
	/**
	 * Compare.
	 * 
	 * @param Key1 the key1
	 * @param Key2 the key2
	 * 
	 * @return the int
	 */
	public int compare( Object Key1, Object Key2)
	{
		if( Key1 instanceof Integer ){
			return (( Integer)Key1).compareTo( ( Integer)Key2);	
		}else if( Key1 instanceof String ){
			return ( ( String)Key1).compareTo( ( String)Key2);		
		}
		return 0;
	}
	
	/**
	 * Hash.
	 * 
	 * @param Key the key
	 * @param Key0 the key0
	 * @param KeyN the key n
	 * 
	 * @return the int
	 */
	public int hash( Object Key, Object Key0, Object KeyN)
	{
		int nN = Index.size();
		if( Key instanceof Integer ){
			int iV0 = (( Integer)Key0).intValue();
			int iVn = (( Integer)KeyN).intValue();
			int iV = (( Integer)Key).intValue();
			double d = ( ( double)( nN - 1)) / (iVn - iV0);
			return ( int)Math.round( (iV - iV0)*d);	
//		}else if( Key instanceof String ){
		}
		return 0;
	}
	
	/**
	 * Gets the key.
	 * 
	 * @param i the i
	 * 
	 * @return the key
	 */
	public Object getKey( int i)
	{
		i = (( Integer)Index.elementAt( i)).intValue();
		return (( OrderedVectorElement)elementAt( i)).getKey(); 
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
		i = (( Integer)Index.elementAt( i)).intValue();
		return (( OptionElement)elementAt( i)).getValue(); 
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
		i = (( Integer)Index.elementAt( i)).intValue();
		return (( OptionElement)elementAt( i)).getName(); 
	}
	
	/**
	 * Gets the code.
	 * 
	 * @param i the i
	 * 
	 * @return the code
	 */
	public String getCode( int i)
	{
		i = (( Integer)Index.elementAt( i)).intValue();
		return (( OrderedVectorElement)elementAt( i)).getCode(); 
	}
	
	/**
	 * Gets the object.
	 * 
	 * @param i the i
	 * 
	 * @return the object
	 */
	public Object getObject( int i)
	{
		i = (( Integer)Index.elementAt( i)).intValue();
		return elementAt( i); 
	}
	
	/**
	 * Find index.
	 * 
	 * @param Key the key
	 * 
	 * @return the int
	 */
	private int findIndex( Object Key) 
	{
		int nN = Index.size();
		
		if( nN == 0 ) return 0;
		Object KeyV0 = getKey( 0); 
		if( compare( Key, KeyV0) <= 0 ) return 0;
		if( nN == 1 ) return 1;
		Object KeyVn = getKey( nN -1);
		int iCmp = compare( Key, KeyVn);
		if( iCmp > 0 ) return nN;
		if( iCmp == 0 ) return nN-1;
		int iN = hash( Key, KeyV0, KeyVn); //( int)( (iNodeID - iV0)*d + 0.5);
		
		iCmp = compare( getKey( iN), Key);
		if( iCmp < 0 ){
			for( ; ++iN < nN; ){
				if( compare( getKey( iN), Key) >= 0 ) break;
			}
		}else if( iCmp > 0 ){
			for( ; --iN >= 0; ){
				if( ( iCmp = compare( getKey( iN), Key)) == 0 ) break;
				if( iCmp < 0 ){ ++iN; break;}
			}
		}
		return iN;
	}
	
	/**
	 * Gets the index.
	 * 
	 * @param Key the key
	 * 
	 * @return the index
	 */
	private int getIndex( Object Key)
	{
		int i = findIndex( Key);
		if( i < 0 || i >= Index.size() ) return -1; 
		Object KeyV = getKey( i);
		return ( compare( Key, KeyV) != 0 )? -1: i;
	}
	
	/**
	 * Gets the by key.
	 * 
	 * @param Key the key
	 * 
	 * @return the by key
	 */
	public Object getByKey( Object Key)
	{
/*		int iN = findIndex( Key);
		if( iN < 0 || iN >= Index.size() ) return null; 
		Object KeyV = getKey( iN);
		if( compare( Key, KeyV) != 0 ) return null; */
		if( Key == null ) return null;
		int iN = getIndex( Key);
		return (iN>=0)? getObject( iN): null;
	}
	
	/**
	 * Gets the by code.
	 * 
	 * @param sCode the s code
	 * 
	 * @return the by code
	 */
	public Object getByCode( String sCode)
	{
		int n = Index.size();
		if( sCode != null ) for( int i = 0; i < n; ++i ){
			String CurCode = getCode( i);
			if( sCode.equals( CurCode) ) return getObject( i);	
		}
		return null;		
	}
/*	public boolean find( Object Obj)
	{
		Object Key = (( OrderedVectorElement) Obj).getKey();
		return getByKey( Key) != null;
	} */
	/**
 * Insert.
 * 
 * @param Obj the obj
 * 
 * @return true, if successful
 */
public boolean insert( Object Obj)
	{
		int nN = size();
		Object Key = (( OrderedVectorElement) Obj).getKey();
		int iN = findIndex( Key);
		if( iN >= 0 && iN < Index.size() ){
			Object KeyV = getKey( iN);
			if( KeyV == null ) return false;
			if( !bDuplicates && compare( Key, KeyV) == 0 ) return false;
		}	
		if( add( Obj) ){
			Index.add( iN, new Integer( nN));
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the by key.
	 * 
	 * @param Key the key
	 * 
	 * @return true, if successful
	 */
	public boolean removeByKey( Object Key)
	{
		int iN = findIndex( Key);
		if( iN < 0 || iN >= Index.size() ) return false; 
		Object KeyV = getKey( iN);
		if( compare( Key, KeyV) != 0 ) return false;
		int i = (( Integer)Index.elementAt( iN)).intValue();
		setElementAt( ( OrderedVectorElement)null, i);
		Index.remove( iN);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove( Object Obj)
	{
		if( Obj == null ) return false;
		Object Key = (( OrderedVectorElement) Obj).getKey();
		return removeByKey( Key);
	}
	
	/**
	 * Gets the keys.
	 * 
	 * @return the keys
	 */
	public ArrayList<Object> getKeys()
	{
		int n = Index.size();
		ArrayList<Object> Keys = new ArrayList<Object>();
		for( int i = 0; i < n; ++i ){
			Object Key = getKey( i);
			Keys.add( Key);	
		}
		return Keys;		
	}
	
	/**
	 * Gets the codes.
	 * 
	 * @return the codes
	 */
	public ArrayList<Object> getCodes()
	{
		int n = Index.size();
		ArrayList<Object> Codes = new ArrayList<Object>();
		for( int i = 0; i < n; ++i ){
			Object Code = getCode( i);
			Codes.add( Code);	
		}
		return Codes;		
	}
	
	/**
	 * Gets the options list.
	 * 
	 * @param DefaultObj the default obj
	 * 
	 * @return the options list
	 */
	public OptionsList getOptionsList( Object DefaultObj)
	{
		boolean bFirst = true;
		int n = Index.size();
		OptionsList Opts = null;
		for( int i = 0; i < n; ++i ){
			Object Obj = getObject( i);
			if( Obj == null ) continue;
			if( bFirst ){ 
				Opts = new OptionsList();
				if( DefaultObj != null  && DefaultObj instanceof OptionElement ) Opts.add( ( OptionElement)DefaultObj);
				bFirst = false;
			}
			if( Obj != null && Obj instanceof OptionElement ) Opts.add( ( OptionElement)Obj);	
		}
		return Opts;
	}
	
	/**
	 * Gets the options list.
	 * 
	 * @return the options list
	 */
	public OptionsList getOptionsList( )
	{
		int n = Index.size();
		OptionsList Opts = new OptionsList();
		for( int i = 0; i < n; ++i ){
			Object Obj = getObject( i);
			if( Obj != null && Obj instanceof OptionElement ){
				Opts.insert( ( OptionElement)Obj);	
			}
		}
		return Opts;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize()
	{
		return Index.size();
	}
	
	/**
	 * Sets the object.
	 * 
	 * @param OldKey the old key
	 * @param Obj the obj
	 * 
	 * @return true, if successful
	 */
	public boolean setObject( Object OldKey, Object Obj){
		int iOld = getIndex( OldKey), iNew;
		if( iOld < 0 ) return false;

		Object Key = (( OrderedVectorElement) Obj).getKey();		// kas uus ei hakka dubleerima
		if( compare( OldKey, Key) != 0 ){
			iNew = findIndex( Key);
			if( iNew >= 0 && iNew < Index.size() ){
				Object KeyV = getKey( iNew);
				if( !bDuplicates && compare( Key, KeyV) == 0 ) return false;  // selline võti juba olemas
			}
		}else iNew = iOld;	
		int i = (( Integer)Index.elementAt( iOld)).intValue();
		set( i, Obj);
		if( iOld != iNew ){
			Index.remove( iOld);
			if( iNew > iOld ) --iNew;
			Index.add( iNew, new Integer( i));
		}
		return true;
	} 
	
	/**
	 * Sets the key.
	 * 
	 * @param NewKey the new key
	 * @param Obj the obj
	 * 
	 * @return true, if successful
	 */
	public boolean setKey( Object NewKey, Object Obj){
		Object OldKey = (( OrderedVectorElement) Obj).getKey();		// kas uus ei hakka dubleerima
		int iOld = getIndex( OldKey), iNew;
		if( iOld < 0 ) return false;
		if( compare( OldKey, NewKey) != 0 ){
			iNew = findIndex( NewKey);
			if( iNew >= 0 && iNew < Index.size() ){
				Object KeyV = getKey( iNew);
				if( !bDuplicates && compare( NewKey, KeyV) == 0 ) return false;  // selline võti juba olemas
			}
		}else iNew = iOld;	
		int i = (( Integer)Index.elementAt( iOld)).intValue();
		(( OrderedVectorElement) Obj).setKey( NewKey);
		if( iOld != iNew ){
			Index.remove( iOld);
			if( iNew > iOld ) --iNew;
			Index.add( iNew, new Integer( i));
		}
		return true;
	}	
}
