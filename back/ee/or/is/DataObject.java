package ee.or.is;
/*
 * Created on 5.05.2006 by or
 *
 */
import java.sql.ResultSet;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Node;
import ee.or.db.Database;
import ee.or.db.DbAccess;
import ee.or.geom.GeomPoint;
import ee.or.gis.GISSight;
import ee.or.gis.MapGraphic;
import ee.or.is.DOMData;
import ee.or.is.ISServlet;
import ee.or.is.MException;
import ee.or.is.OptionElement;
import ee.or.is.OptionsList;

public class DataObject  implements OptionElement, Cloneable
{
	private int iID = 0;
    public int getID(){   	return iID;}
	public void setID( int iID){	this.iID = iID;}
    public int getId(){   	return iID;}
	public void setId( int iId){	this.iID = iId;}

	public String sValue = null;
	public String getValue(){	return (sValue != null)? sValue: Integer.toString( iID);}
	public int getIntValue(){ return iID;}
	public void setValue( DbAccess aDbIn) throws MException
	{
		iID = aDbIn.getInt( getPKeyName());
	}
	public boolean isPKeyChar()
	{
		return false;
	}
   
    private String sName = "";
	public String getName(){	return sName;}
	public String getUniqueName(){ return sName;}
    public void setName( String name){  sName = (name != null)? name.trim(): null;}
    public void setName(){    sName = Integer.toString( iID);}
    
    private OptionsList DataObjects = null;
    
	public String getTableName(){  	return null;}
	public String getPKeyName(){  	return "id";}
	public String getDataName(){  	return "name";}

	public String getListQuery( String sWhere, String sOrder)
	{  	
		StringBuffer aCall = new StringBuffer( "select ");
		aCall.append( getPKeyName());
		aCall.append( ",");
		aCall.append( getDataName());
		aCall.append( " from ");
		aCall.append( getTableName());
		if( sWhere != null){
			aCall.append( " where ");
			aCall.append( sWhere);
		}
		aCall.append( " order by ");
		if( sOrder == null ){
			sOrder = getDataName();
	    	int i = sOrder.indexOf( " as ");
	    	if( i > 0 ) sOrder = sOrder.substring( 0, i);
		}
		aCall.append( sOrder);
		return aCall.toString();
	}
    public DataObject() 
    {
        super();
    }
	public DataObject( OptionsList DataObjects) 
    {
        super();
        this.DataObjects = DataObjects;
    }
    public DataObject( DbAccess DbIn) 
    {
        super();
        try {
//            if( DbIn.hasColumn( "id") ) 
            load( DbIn);
        } catch (MException E) {
        }
    }
    public DataObject( String sValue, String sName)
    {
    	this.sValue = sValue;
    	this.sName = sName;
    }
	public DataObject createDataObject()
	{
		return new DataObject();
	}
	public Object clone()
	{
		try {
			DataObject aNewDO = ( DataObject)super.clone();
			return aNewDO;
		} catch (CloneNotSupportedException E) {
			Log.error( E, true);
		}
		return null;
	}
	public boolean equals( DataObject aDO)
	{
		return aDO != null && aDO.getID() == iID && isDataObject( aDO);
	}
	public synchronized void set( DataObject DO)
	{
		sName = DO.getName();
	}
    public String getWhere()
    {
    	return null;
    }
    public String getSelect()
    {
    	return null;
    }
    public boolean load( DbAccess DbIn) throws MException  
	{
        if( getPKeyName() != null ) {
        	if( isPKeyChar() ) {
        		iID = 0;
        		sValue = DbIn.getString( getPKeyName());
        	}else {
               	iID = DbIn.getInt( getPKeyName());
                sValue = null;       		
        	}
        }
		String sDataName = getDataName(); 
		if( sDataName != null ) {
	    	int i = sDataName.indexOf( " as ");
	    	if( i > 0 ) sDataName = sDataName.substring( i+4);
	    	sName = DbIn.getStringByType( sDataName);
		}
		return true;
	}
	public boolean load( Sight aSight) 
	{
		return true;
	}
	public String getQuery()
	{
		return "SELECT * FROM " + getTableName() + " WHERE " + getPKeyName() + "=" + iID;
	}
	public boolean load( Database Db)
	{
		DbAccess DbIn = null;
		try {
			DbIn =  new DbAccess( Db);
			String sQuery = getQuery();
			if( DbIn.select( sQuery) ){
				return load( DbIn);
			}
		} catch (MException e) {
		} finally{
			if( DbIn != null ) DbIn.close();
		}
		return false;
	}
	public boolean load( Database Db, int iID)
	{
		setID( iID);
		return load( Db);
	}
	public void save( DbAccess DbIn) throws MException 
	{
		DbIn.setString( "name", sName);
	}
	public boolean save( Sight Sight) 
	{
		try {
			return save( Sight.Db);
		} catch ( MException E) {
		    Sight.setError( E);
		}
		return false;
	}
	public boolean save( ISServlet Servlet) 
	{
		boolean bRet = false;
        Database Db = null;
        try {
			Db = new Database( Servlet.getDbDesc());
			bRet = save( Db);
		} catch (MException e) {
		}
        if( Db != null ) Db.close();
		return bRet;
	}
	public boolean save( Database Db) throws MException 
	{
	    DbAccess DbIn = null;
		try {
			DbIn = new DbAccess( Db);
			DbIn.setTable( getTableName(), getPKeyName());
			if( iID > 0 ){
				DbIn.edit( iID);
			}else{
				if( getName() == null ) setName();
				DbIn.addNew();
			}
			save( DbIn);
			DbIn.update();
			if( iID <= 0 ){
				iID = DbIn.getID();
				if( iID <= 0 ) return false;
				if( DataObjects != null ) DataObjects.insert( this);
			} 
			return true;
		} catch ( MException E) {
			throw E;
		} finally{
			if( DbIn != null ) DbIn.close();
		}
	}
	public void saveMore( DbAccess DbIn) throws MException 
	{
	}
	public boolean saveMore( Sight Sight) 
	{
		boolean bRet = false;
	    DbAccess DbIn = null;
		try {
			DbIn = new DbAccess( Sight.Db);
			DbIn.setTable( getTableName(), getPKeyName());
			if( iID > 0 ){
				DbIn.edit( iID);
			}
			saveMore( DbIn);
			DbIn.update();
			bRet = true;
		} catch (MException E) {
		    Sight.setError( E);
		}
		if( DbIn != null ) DbIn.close();
		return bRet;
	}
	public void save( HttpServletRequest Request, Sight Sight)
	{
	    setName( Request.getParameter( "ID_NAME"));
	}
	public void setXML( DOMData Doc, Node Root, boolean bAddList)
    {
		if( bAddList ) Doc.addChildNode( Root, "DataObjects", getDataObjects(), iID, false, false); 
		Doc.addChildNode( Root, "id", getValue()); 
		if( getDataName() != null ) Doc.addChildNode( Root, "name", getName()); 
    }
	public void setXML( DOMData Doc, Node Root)
    {
		Doc.addChildNode( Root, "id", getValue()); 
		String sDataName = getDataName();
		if( sDataName != null ) {
			int i = sDataName.indexOf( " as ");
			Doc.addChildNode( Root, (i>0)? sDataName.substring( i+4): sDataName, getName()); 
		}
    }
	public boolean isOK( Sight Sight)
	{
		if( getName() == null || getName().length() == 0){
			Sight.setError( "Nimi peab olema olemas!");
			Sight.setErrorNr( 901);
			return false;
		}
		if( DataObjects != null && DataObjects.isUnique() && !isUnique() ){
			Sight.setError( "Nimi peab olema unikaalne!");
			Sight.setErrorNr( 902);
			return false;
		}
		return true;
	}
	public int ctrlFormXML( HttpServletRequest Request, Sight Sight, boolean bEdit)
	{
		int iRet = 0;
		if( Request != null ){
//			sRet = Request.getParameter( "ID_RETURN");
			if( ISServlet.hasParameter( Request, "RETURN"))
				iRet = ISServlet.getParameterInt( Request, "RETURN");
			else
				iRet = ISServlet.getParameterInt( Request, "ID_RETURN");
		}
	    if( iRet == 9 ){ 
	    	Sight.closeForm();
	    	return -9;
	    }
		if( mustSave( iRet)){
			save( Request, Sight);
			if( Sight.isError() ){
//				Sight.User = null;
				iRet = 0;
			}
		}
		if( iRet == -1 ){
		}else if( iRet == 1 ){
//	        if( iID == (( CarUser)Sight.User).getDataObjectID() ) (( CarUser)Sight.User).setDataObject( this);
	       
	        if( isOK( Sight) ){
	        	boolean bOld = iID > 0;
		        if( save( Sight) ){
		        	if( bOld ){
		        		resetClone( Request, Sight);// reloadDataObjects(); // Sight.Db);		        	
		        	}
//		        	if( bNew ) DataObjects.insert( this);
//		        	else  reloadDataObjects(); // Sight.Db);
		        }else iRet = 0;
	        }else iRet = 0;
		}else if( iRet == 2 ){
			if( iID <= 0){
				Sight.setError( "Ei saa eemaldada - objekt valimata!");
				Sight.setErrorNr( 903);
			}else if( remove( Sight)){
//                DataObjects.remove( this);
//				iID = 0;
			}else iRet = 0;
		}
		return iRet;
	}
	
	/**
	 * Must save.
	 * 
	 * @param iRet the i ret
	 * 
	 * @return true, if successful
	 */
	public boolean mustSave( int iRet)
	{
		return iRet == 1;
	}
	
	/**
	 * Form xml.
	 * 
	 * @param Request the request
	 * @param Sight the sight
	 * @param bEdit the b edit
	 * 
	 * @return the dOM data
	 */
	public DOMData FormXML( HttpServletRequest Request, Sight Sight, boolean bEdit) throws ExceptionIS
	{
		int iRet = ctrlFormXML( Request, Sight, bEdit);
		return getFormXML( Request, Sight, bEdit, iRet);
	}
	
	/**
	 * Gets the form xml.
	 * 
	 * @param Request the request
	 * @param Sight the sight
	 * @param bEdit the b edit
	 * @param iRet the i ret
	 * 
	 * @return the form xml
	 */
	public DOMData getFormXML( HttpServletRequest Request, Sight Sight, boolean bEdit, int iRet) throws ExceptionIS
	{
//		String sRet = null;
		DOMData Doc = Sight.getTemplate();
		Node Root = Doc.getRootNode();
		Doc.addChildNode( Root, "edit", bEdit); 
//		if( bEdit ){
//		Doc.addChildNode( Root, "DataObjects", getDataObjects(), iID, false, false); 
//		}else{
//			Doc.addChildNode( Root, "DataObject_id", iDataObjectID); 
//		}
		setXML( Doc, Root);
		Sight.addError( Doc);
		Doc.addChildNode( Root, "return", iRet);
		return Doc;
	}
	
	/**
	 * Exit form.
	 */
	public void exitForm() // siia tuleb kui kuvavorm suletakse
	{
	}
	
	/**
	 * Removes the.
	 * 
	 * @param Sight the sight
	 * 
	 * @return true, if successful
	 */
	public boolean remove( Sight Sight)
	{
		DbAccess DbIn = null;
	    try {
            DbIn = new DbAccess( Sight.Db);
            DbIn.setTable( getTableName(), getPKeyName());
            DbIn.delete( iID);
            DbIn.commit();
            if( DataObjects != null ) DataObjects.remove( this);
			iID = 0;
            return true;
        } catch (MException E) {
            Sight.setError( E);
        } finally {
        	if( DbIn != null ) DbIn.close();
        }
        return false;
	}
	
	/**
	 * Removes the all.
	 * 
	 * @param Sight the sight
	 * @param sWhere the s where
	 * 
	 * @return true, if successful
	 */
	public boolean removeAll( Sight Sight, String sWhere)
	{
		DbAccess DbIn = null;
	    if( sWhere != null )try {
            DbIn = new DbAccess( Sight.Db);
            DbIn.delete( getTableName(), sWhere);
            return true;
        } catch (MException E) {
            Sight.setError( E);
        } finally {
        	if( DbIn != null ) DbIn.close();
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see or.is.OptionElement#getName()
	 */
      
      /**
       * Gets the iD.
       * 
       * @return the iD
       */
	
	/**
	 * Sets the data objects.
	 * 
	 * @param DOs the new data objects
	 */
	public void setDataObjects( OptionsList DOs)
	{
		DataObjects = DOs;
	}
	
	/**
	 * Gets the data object.
	 * 
	 * @param iDataObjectID the i data object id
	 * 
	 * @return the data object
	 */
	public DataObject getDataObject( int iDataObjectID)
	{
	    if( DataObjects != null ){
	        return ( DataObject)DataObjects.getObjectByValue( iDataObjectID);
	    }
	    return null;
	}
	
	/**
	 * Creates the data objects.
	 * 
	 * @param Db the db
	 */
	public synchronized void createDataObjects( Database Db)
	{
		DataObjects = new OptionsList();
		loadDataObjects( Db);
	}
	
	/**
	 * Creates the data objects.
	 * 
	 * @param aSight the a sight
	 */
	public synchronized void createDataObjects( Sight aSight)
	{
		DataObjects = new OptionsList();
		loadDataObjects( aSight);
	}
	
	/**
	 * Creates the data objects.
	 * 
	 * @param aSight the a sight
	 * @param bUnique the b unique
	 */
	public synchronized void createDataObjects( Sight aSight, boolean bUnique)
	{
		DataObjects = new OptionsList();
	    DataObjects.setEquals( !bUnique);
		loadDataObjects( aSight);
	}
	
	/**
	 * Creates the data objects.
	 */
	public synchronized void createDataObjects()
	{
		DataObjects = new OptionsList();
	}
	
	/**
	 * Reset clone.
	 * 
	 * @param Request the request
	 * @param aSight the a sight
	 * 
	 * @return true, if successful
	 */
	public synchronized boolean resetClone( HttpServletRequest Request, Sight aSight)
	{
		DataObject aObject = ( DataObject)DataObjects.getObjectByValue( iID);
		if( aObject != null ){
			aObject.save( Request, aSight);
			aObject.set( this);
			if( DataObjects.setObjectByValue( aObject) ){
				reloadDataObjects();
				return true;
			}
		}
		if( DataObjects.setObjectByValue( this) ){
			reloadDataObjects();
			return true;
		}
		Log.error( "setObjectByValue return false on '" + toString() + "'", true);
/*		DataObject aDO = getDataObject( iID);
		if( aDO != null ){
			aDO.set( this);
			aDO.reloadDataObjects();
			return true;
		}*/
		return false;
	}
	
	/**
	 * Reload data objects.
	 */
	public synchronized void reloadDataObjects() // Database Db)
	{
		if( DataObjects != null  ){
			Object [] Objects = DataObjects.toArray();
			DataObjects.clear();
			for( int i = Objects.length; --i >=0; ){
				DataObject Obj = ( DataObject)Objects[ i];
				DataObjects.insert( Obj);
			}
		}
	}
	
	/**
	 * Load data objects.
	 * 
	 * @param aSight the a sight
	 */
	public synchronized void loadDataObjects( Sight aSight)
	{
		loadDataObjects( aSight, null);
	}
	
	/**
	 * Load data objects.
	 * 
	 * @param Db the db
	 */
	public void loadDataObjects( Database Db)
	{
		loadDataObjects( null, Db);
	}
	
	/**
	 * Load data objects.
	 * 
	 * @param aSight the a sight
	 * @param Db the db
	 * @throws MException 
	 */
	private synchronized void loadDataObjects( Sight aSight, Database Db)
	{
		DbAccess DbIn = null;
		int iCount = 0;
		if( DataObjects == null ){
			DataObjects = new OptionsList();
			DataObjects.setOrder( false);
		}
		if( DataObjects != null  ) try {
			DbIn =  new DbAccess( ( aSight != null )? aSight.Db: Db);
			String sQuery = getSelect();
			String TName = getTableName();
			if( sQuery == null ) sQuery = "SELECT * FROM " + TName; 
			if( getWhere() != null ) sQuery += " WHERE " + getWhere();
			if( DbIn.select( sQuery) )do{
				++iCount;
				DataObject DO =  createDataObject();
				if( DO == null ) break;
				try {
					DO.setValue( DbIn); //.iID = DbIn.getInt( getPKeyName());
					DO.DataObjects = DataObjects;
					DataObjects.add(DO);
					if( DO.load( DbIn) ){
						if( aSight != null ) DO.load( aSight);
//						if( !DataObjects.insert( DO) )
//							log( aSight, " object '" + DO.toString() + "' insert error");
					}else 
						log( aSight, " object '" + DO.toString() + "' load error");
//					}
				} catch (MException e) {
				}
			}while( DbIn.next());
		} catch (MException e) {
		}
		if( DbIn != null ) DbIn.close();
		if( isDebug( aSight, 98))
			log( aSight, "From table " + getTableName() + " read " + iCount + " and loaded " 
				+ DataObjects.size());
//		return DataObjects;
	}
	public void loadList( Sight aSight, String sWhere, String sOrder)
	{
		if( sOrder != null ) DataObjects.setOrder( false);
		loadList( aSight, getListQuery( sWhere, sOrder));
	}
	public void loadList( Sight aSight, String sQuery)
	{
		DataObjects = new OptionsList();
		DbAccess DbIn = null;
		int iCount = 0;
		try {
			DbIn =  new DbAccess( aSight.getDatabase());
			if( DbIn.select( sQuery) )do{
				++iCount;
				DataObject DO =  createDataObject();
				try {
					DO.setValue( DbIn);
					DO.DataObjects = DataObjects;
					if( DO.load( DbIn) ){
						if( !DataObjects.insert( DO) )
							log( aSight, " object '" + DO.toString() + "' insert error");
					}else 
						log( aSight, " object '" + DO.toString() + "' load error");
				} catch (MException e) {
				}
			}while( DbIn.next());
		} catch (MException e) {
		} catch (Throwable e) {
			log( aSight, e);
		}
		if( DbIn != null ) DbIn.close();
		if( isDebug( aSight, 98))
			log( aSight, "From table " + getTableName() + " read " + iCount + " and loaded " + DataObjects.size());
//		return DataObjects;
	}
	
	/**
	 * Log.
	 * 
	 * @param aSight the a sight
	 * @param sMsg the s msg
	 */
	public boolean isUserLog( Sight aSight){ return aSight != null && aSight.getUser() != null;}
	public boolean isDebug( Sight aSight, int iDebug)
	{
		Thread T = Thread.currentThread();
		if( T instanceof ThreadLog ) return (( ThreadLog)T).isDebug( iDebug);
		else if( isUserLog( aSight) ) return aSight.isDebug( iDebug);
		return Log.isDebug( iDebug);
	}
	public void log( Sight aSight, String sMsg)
	{
		Thread T = Thread.currentThread();
		if( T instanceof ThreadLog ) (( ThreadLog)T).log( sMsg);
		else if( isUserLog( aSight) ) aSight.log( sMsg);
		else Log.info( sMsg);
	}
	public void log( Sight aSight, Throwable aE)
	{
		Thread T = Thread.currentThread();
		if( T instanceof ThreadLog ) (( ThreadLog)T).log( aE);
		else if( isUserLog( aSight) ) aSight.log( aE);
		else Log.info( aE);
	}
/*	synchronized public void loadDataObjects( Database Db)
	{
		int iCount = 0;
		DbAccess DbIn = null;
		if( DataObjects != null  ) try {
			DbIn =  new DbAccess( Db);
			String sQuery = getSelect();
			if( sQuery == null ) sQuery = "SELECT * FROM " + getTableName(); 
			if( getWhere() != null ) sQuery += " WHERE " + getWhere();
			if( DbIn.select( sQuery) )do{
				++iCount;
				DataObject DO =  createDataObject();
				try {
					DO.iID = DbIn.getInt( "id");
					DO.DataObjects = DataObjects;
					if( DO.load( DbIn) ){
						DataObjects.insert( DO);
					}
				} catch (MException e) {
				}
			}while( DbIn.next());
		} catch (MException e) {
		}
		if( DbIn != null ) DbIn.close();
		Log.info( "From table " + getTableName() + " loaded " + DataObjects.size());
//		return DataObjects;
	} */
	/**
 * Save data objects.
 * 
 * @param Db the db
 */
public synchronized void saveDataObjects( Database Db)
	{
		DbAccess DbIn = null;
		if( DataObjects != null  ) try {
			DbIn =  new DbAccess( Db);
			DbIn.setTable( getTableName(), getPKeyName());
			for( int i = DataObjects.size(); --i >= 0; ){
				DataObject DO = ( DataObject)DataObjects.get( i);
				DbIn.addNew();
				DO.save( DbIn);
				DbIn.update();
			}
			Db.log( 50, "To table " + getTableName() + " saved " + DataObjects.size());
		} catch (MException e) {
		}
		if( DbIn != null ) DbIn.close();
	}
	
	/**
	 * Gets the data objects.
	 * 
	 * @return the data objects
	 */
	public OptionsList getDataObjects() 
	{
		return DataObjects;
	}
	
	/**
	 * Checks if is data object.
	 * 
	 * @param sClassName the s class name
	 * 
	 * @return true, if is data object
	 */
	public boolean isDataObject( String sClassName)
	{
		return getClass().getName().equalsIgnoreCase( sClassName);
	}
	
	/**
	 * Checks if is data object.
	 * 
	 * @param Obj the obj
	 * 
	 * @return true, if is data object
	 */
	public boolean isDataObject( DataObject Obj)
	{
		return Obj != null && getClass().getName().equalsIgnoreCase( Obj.getClass().getName());
	}
	
	/**
	 * Insert.
	 * 
	 * @return true, if successful
	 */
	public boolean insert()
	{
		if( DataObjects == null ) createDataObjects();
		DataObjects.insert( this);
		return true;
	}
/*	public DataObject getDataObject( Database Db, int iDataObjectID)
	{
	    if( DataObjects == null ) createDataObjects( Db);  
	    if( DataObjects != null ){
	        return ( DataObject)DataObjects.getObjectByValue( iDataObjectID);
	    }
	    return null;
	}
	public String getName( Database Db, int iDataObjectID)
	{
	    DataObject P = getDataObject( Db, iDataObjectID);
	    return (P!=null)? P.getName(): null;
	}*/
	/**
 * Sets the iD.
 * 
 * @param iid the new iD
 */
    
    /**
     * Checks if is unique.
     * 
     * @return true, if is unique
     */
    public boolean isUnique() 
    {
        if( DataObjects != null && DataObjects.isUnique() ){
        	DataObject aDO = ( DataObject)DataObjects.getObjectByName( getName());
        	return aDO == null || aDO.getID() == iID;
        }
        return true;
    }
    
    /**
     * Sets the unique.
     */
    public void setUnique() 
    {
        if( DataObjects == null ) createDataObjects(); 
        DataObjects.setEquals( false);
    }
    
    /**
     * Checks if is new form.
     * 
     * @return true, if is new form
     */
    public boolean isNewForm()
    {
    	return true;
    }
    public String toString()
    {
    	return iID + " " + sName;
    }
	public DOMData getEmpty( HttpServletRequest aRequest, DOMData aDoc, Sight aSight) throws ExceptionIS 
	{
		long lSTime = 0;
		if( aSight.isDebug( 1)) lSTime = ( new Date()).getTime();
//		setRefs();
		if( aDoc == null ) aDoc = aSight.getTemplate();
		Node aRoot = aDoc.getRootNode();
		aDoc.addChildNode( aRoot, "table", getTableName());
//		if( bAddList) addList( aDoc, aRoot);
		aDoc.addChildNode( aRoot, "edit", aSight.hasUserRights( getTableName(), 0));

//		DbAccess aDB = null;
		try {
//			aDB = new DbAccess( aSight.getDatabase());
			Node aRow = aDoc.addChildNode( aRoot, getTableName());
 	       	ResultSet rsmd = aSight.getDatabase().getMetaData( getTableName());
	       	while( rsmd.next())
 	       	{
	       		String sValue = null;
       			String columnName = rsmd.getString( "COLUMN_NAME").toLowerCase();
       			String columnType = rsmd.getString( "TYPE_NAME");
       			String columnDef = rsmd.getString( "COLUMN_DEF");
       			int iType = rsmd.getShort("DATA_TYPE");

       			String sSize = rsmd.getString( "COLUMN_SIZE");
       			if( aSight.isDebug( 98)) aSight.log( columnName + " " + columnType + " " + columnDef);
       			if( columnType.equalsIgnoreCase( "geometry" ) ){
/*       				if( ISServlet.getParameterInt( Request, "RETURN") == 3 ){
       					GeomPoint aCoord = getCoord( Request, columnName, null);
       					if( aCoord != null){
       						sValue = GlobalGIS.toString( aCoord.getCenter()); 
	 		 	       		Point2D aCoordLL = Projection.getCoord( aCoord.getCenter(), Projection.CS_L_EST, Projection.CS_LL);
	 	 	 	 	       	Node aNode = aDoc.addChildNode( aRow, columnName + "_ll", GlobalGIS.getAsLL( aCoordLL));
 		 	       			aDoc.setElemAttr( aNode, "type", "geometry_ll");
       					}else sValue = "";
       				}else*/ 
       				sValue = "";
       				sSize = "24";
       			}else if( columnType.equalsIgnoreCase( "bytea" )){
       			}else if( aRequest != null && ISServlet.hasParameter( aRequest, columnName)){
       				sValue = aRequest.getParameter( columnName);	
 	       		}else if( columnName.equalsIgnoreCase( getPKeyName())){
 	       			sValue = "0";
 	       		}else{
 	       			sValue = columnDef; // DEFAULT
 	       			if( sValue == null ) sValue = "";
 	       		}
       			if( sValue != null){
 	       			Node aNode = aDoc.addChildNode( aRow, columnName, sValue);
 	       			aDoc.setElemAttr( aNode, "type", columnType);
 	       			if( !columnType.equalsIgnoreCase( "text")){
 	 	       			aDoc.setElemAttr( aNode, "size", sSize);
 	       			}
 	       			aDoc.setElemAttr( aNode, "type_id", iType);
       			}
    	    }
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
//			aDB.close();
		}
		if( aRequest != null ){
			String sXSL = getTableName();
			if( ISServlet.hasParameter( aRequest, "XSL") ) sXSL = aRequest.getParameter( "XSL");
			aDoc.setFileXSL( aSight, sXSL);
		}
    	if( aSight.isDebug( 1)) aSight.log( "DB empty time=" + (( new Date()).getTime() - lSTime) + "ms");
		return aDoc;
	}
	public void draw( MapGraphic aMapGr)
	{
	}
	public int addXML( GISSight aSight, DOMData aDoc, GeomPoint aP)
	{
		return 0;
	}
}
