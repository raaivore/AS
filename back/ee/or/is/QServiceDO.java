package ee.or.is;

public class QServiceDO extends DataObject 
{
	private QService aQService = null;
	public QService getQService(){	return aQService;}
	      
	public QServiceDO( QService aQService) 
    {
        super();
        this.aQService = aQService;
    }
	public QServiceDO( Sight aSight) 
    {
        super();
        this.aQService = aSight.createQService();
    }
	public void setQService( QService aQService){  this.aQService = aQService;}
	public String getTableName(){  	return aQService.getTableName();}
	public void setTableName( String sTableName){ aQService.setTableName( sTableName);}
	public String getPKeyName()
	{  	
		String sListField = aQService.getListField();
		if( sListField != null ){
			int i = sListField.indexOf( ',');
			if( i > 0 ) return sListField.substring( 0, i);
		}
		return (aQService.getTableName() != null )? aQService.getPKeyName(): super.getPKeyName();
	}
	public boolean isPKeyChar()
	{  	
		return (aQService.getTableName() != null )? aQService.isPKeyChar(): super.isPKeyChar();
	}
	public String getDataName()
	{  	
		String sListField = aQService.getListField();
		if( sListField != null ){
			int i = sListField.indexOf( ',');
			if( i > 0 ) return sListField.substring( ++i);
		}
		return (aQService.getTableName() != null )? aQService.getDataName(): super.getDataName();
	}
	public String getListQuery(){  	return aQService.getListQuery();}
	public String getListQuery( String sWhere){  	return aQService.getListQuery( sWhere);}
	public String getListQuery( String sWhere, String sOrder){  	
		return aQService.getListQuery( sWhere, sOrder);}
	public DataObject createDataObject(){ return aQService.createDataObject();	}
	public Sight getSight() {	return aQService.getSight();}
	public ISServlet getServlet() {	return aQService.getServlet();}
	public String getSelect(){ return aQService.getQuery();}
	public void log( String sMsg){	aQService.log( sMsg);}
	public void log( int iDebug, String sMsg){	aQService.log( iDebug, sMsg);}
	public void log( Exception aE){aQService.log( aE);}
}
