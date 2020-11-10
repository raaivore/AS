import ee.or.db.DbAccess;
import ee.or.is.DataObject;
import ee.or.is.MException;



public class Appl extends DataObject 
{
	public int 		App_code = 0;
	public String 	Name;
	public String 	App_group;
	public String 	App_type;
	public String 	Description;
	public float 	App_cost;
	public String 	Last_modified;
	public Boolean	InDb = false;
;
	public Appl() 
	{
		super();
	}
	public String getTableName() {
		return "application";
	}
	public String getDataName(){
		return "name";
	}
	public String getWhere()
	{
		return null;
	}
	public DataObject createDataObject()
	{
		return new Appl();
	}
	public boolean load( DbAccess DbIn) throws MException 
	{
		super.load( DbIn);
		
		App_code = DbIn.getInt( "app_code" );
		Name = DbIn.getString( "name");
		App_group = DbIn.getString( "app_group");
		App_type = DbIn.getString( "app_type");
		Description = DbIn.getString( "description");
		App_cost = (float) DbIn.getFloat( "app_cost");
		Last_modified = DbIn.getStringByType( "last_modified");

		return true;
	}
}
