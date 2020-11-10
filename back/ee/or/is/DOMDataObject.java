/*
 * Created on 5.05.2006 by or
 *
 */
package ee.or.is;
	
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Node;
//import ee.or.is.ISServlet;
import ee.or.is.DOMData;
import ee.or.is.ISServlet;
//import ee.or.is.MException;
import ee.or.is.OptionsList;

// TODO: Auto-generated Javadoc
/**
 * The Class DOMDataObject.
 */
public class DOMDataObject extends DataObject
{

	/**
	 * Instantiates a new dOM data object.
	 */
	public DOMDataObject() 
    {
        super();
    }
	
	/**
	 * Instantiates a new dOM data object.
	 * 
	 * @param DataObjects the data objects
	 */
	public DOMDataObject( OptionsList DataObjects) 
    {
        super();
        setDataObjects( DataObjects);
    }
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#save(javax.servlet.http.HttpServletRequest, or.is.Sight)
	 */
	public void save( HttpServletRequest Request, Sight Sight)
	{
	    setName( Request.getParameter( "ID_NAME"));
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#setXML(or.is.DOMData, org.w3c.dom.Node)
	 */
	public void setXML( DOMData Doc, Node Root)
    {
		Doc.addChildNode( Root, "id", getID()); 
		Doc.addChildNode( Root, "name", getName()); 
    }
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#isOK(or.is.Sight)
	 */
	public boolean isOK( Sight Sight)
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#ctrlFormXML(javax.servlet.http.HttpServletRequest, or.is.Sight, boolean)
	 */
	public int ctrlFormXML( HttpServletRequest Request, Sight Sight, boolean bEdit)
	{
		int iRet = 0;
		if( Request != null ){
			iRet = ISServlet.getParameterInt( Request, "ID_RETURN");
		}
		if( iRet == -1 ){
		}else if( iRet == 1 ){
	        save( Request, Sight);
	        if( isOK( Sight) ){
	        }
		}else if( iRet == 2 ){
			if( getID() <= 0){
				Sight.setComment( "Ei saa kustutada - objekt valimata!");
			}else if( remove( Sight)){
			}
		}
		return iRet;
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#FormXML(javax.servlet.http.HttpServletRequest, or.is.Sight, boolean)
	 */
	public DOMData FormXML( HttpServletRequest Request, Sight Sight, boolean bEdit) throws ExceptionIS
	{
//		String sRet = null;
		int iRet = ctrlFormXML( Request, Sight, bEdit);
		DOMData Doc = Sight.getTemplate();
		Node Root = Doc.getRootNode();
		Doc.addChildNode( Root, "edit", bEdit); 
//		if( bEdit ){
		Doc.addChildNode( Root, "DataObjects", getDataObjects(), getID(), false, false); 
//		}else{
//			Doc.addChildNode( Root, "DataObject_id", iDataObjectID); 
//		}
		setXML( Doc, Root);
		Sight.addError( Doc);
		Doc.addChildNode( Root, "return", iRet);
		return Doc;
	}
	
	/* (non-Javadoc)
	 * @see or.is.DataObject#remove(or.is.Sight)
	 */
	public boolean remove( Sight Sight)
	{
        return false;
	}
 
}
