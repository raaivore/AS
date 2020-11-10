
import org.w3c.dom.Node;

import ee.or.db.DbAccess;
import ee.or.is.DOMData;
import ee.or.is.DataObject;
import ee.or.is.MException;

public class Serv extends DataObject 
{
	public int 	App_code = 0;
	public int 	Service_code = 0;
	public String 	Name;
	public String 	Type;
	public String 	Sub_type;
	public String 	Description;
	public String 	Last_modified;
	public Boolean  All = false;
;	public Boolean	InDb = false;

	public Serv() 
	{
		super();
	}

	public Serv(Boolean All ) 
	{
		super();
		this.All = All;
	}

	public String getTableName() {
		return "app_service";
	}
	public String getDataName(){
		return "name";
	}
	public String getWhere()
	{
		if(All)	return null;
		else	return  "app_code = " +  String.valueOf(AppServSight.GetAppCode());
	}
	public DataObject createDataObject()
	{
		return new Serv();
	}
	public boolean load( DbAccess DbIn) throws MException 
	{
		super.load( DbIn);
		
		App_code = DbIn.getInt( "app_code" );
		Service_code = DbIn.getInt( "service_code" );
		Name = DbIn.getString( "name");
		Type = DbIn.getString( "type");
		Sub_type = DbIn.getString( "sub_type");
		Description = DbIn.getString( "description");
		Last_modified = DbIn.getStringByType( "last_modified");


		return true;
	}
}
