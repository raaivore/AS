package ee.or.is;
import ee.or.db.DbAccess;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel{
	private String sFileName = null;
	private List<List<HSSFCell>> sheetData = null;
	
	public Excel()
	{
	}
	public Excel( String sFileName)
	{
		this.setFileName( sFileName);
	}
	public boolean read( InputStream fis)
	{
		sheetData = new ArrayList<List<HSSFCell>>();
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				Iterator<Cell> cells = row.cellIterator();
				List<HSSFCell> data = new ArrayList<HSSFCell>();
				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					data.add(cell);
				}
				sheetData.add( data);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try{
					fis.close();
				}catch( IOException aE ){
				}
//				showExcelData(); // sheetData); 
			}
		}
		return false;
	}
	public void showExcelData()//List sheetData) 
	{
		for (int i = 0; i < sheetData.size(); i++) {
			List<HSSFCell> list = sheetData.get(i);
			for (int j = 0; j < list.size(); j++) {
				HSSFCell cell = (HSSFCell) list.get(j);
				System.out.print(
					cell.getRichStringCellValue().getString());
				if (j < list.size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
	public boolean save( Sight aSight, String sTableName, int iPlanID, String[] sOrderCSV)
	{
		boolean bRet = false;
		DbAccess aDB = null;
		try {
			aDB = new DbAccess( aSight.getDatabase());
			aDB.setTable( sTableName, "id");
			for( int i = 0; i < sheetData.size(); i++) {
				List<HSSFCell> list = sheetData.get(i);
				HSSFCell cell = (HSSFCell) list.get( 0);
				String sValue = cell.getStringCellValue();
				int iID = GlobalData.getFirstIntFromString( sValue);
				if( iID > 0 ){
					aDB.edit( iID);
					for( int j = 0; ++j < list.size();) {
						cell = (HSSFCell) list.get(j);
						if( j < sOrderCSV.length ){
							sValue = cell.getStringCellValue();
							int n = sValue.length()-1;
							if( n > 0){
								if( Character.isLetterOrDigit( sValue.charAt( n)) ) aDB.setString( sOrderCSV[ j], sValue);
								else aDB.setString( sOrderCSV[ j], sValue.substring( 0, n));
							}else
								aDB.setNull( sOrderCSV[ j]);
						}else break;
					}
					bRet = aDB.update();
					if( !bRet ) break;
				}
			}
		} catch( Exception aE) {
			aSight.log( aE);
			aSight.setError( aE);
		} finally {
			if( aDB != null ) aDB.close();
//			if( aSight.isDebug( 1)) aSight.log( "DB update time=" + (( new Date()).getTime() - lSTime) + "ms");
		}
		return bRet;
	}
	public void setFileName( String sFileName) {
		this.sFileName = sFileName;
	}
	public String getFileName() {
		return sFileName;
	}
}
