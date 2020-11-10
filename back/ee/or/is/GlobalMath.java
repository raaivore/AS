/*
 * 
 */
package ee.or.is;

import java.util.ArrayList;

/**
 * The Class GlobalMath.
 */
public abstract class GlobalMath {

	/**
	 * Adds the ints.
	 * 
	 * @param Lines the lines
	 * @param iLineID the i line id
	 * 
	 * @return the int[]
	 */
	public static int [] addInts( int [] Lines, int iLineID) 
	{
		int [] NewLines = null;
		if( Lines == null ){
			NewLines = new int[ 1];
			NewLines[ 0] = iLineID;
		}else{
			int n = Lines.length;
			int i;
			for( i = n; --i >= 0; ){
				if( Lines[ i] == iLineID ) break;
				if( Lines[ i] < 0 )
					i = i + 1 - 1;
			}
			if( i >= 0 ) NewLines = Lines;
			else{
				NewLines = new int[ n+1];
				for( i = n; --i >= 0; ) NewLines[ i] = Lines[ i];
				NewLines[ n] = iLineID;
			}
		}
		return NewLines;
	}
	
	/**
	 * Adds the ints.
	 * 
	 * @param Lines the lines
	 * @param AddLines the add lines
	 * 
	 * @return the int[]
	 */
	public static int [] addInts( int [] Lines, int [] AddLines) 
	{ // liita kaks hulka nii, et j‰‰ksid ainult erinevad
		int [] NewLines = null;
		if( Lines == null ){
			NewLines = AddLines;
		}else if( AddLines == null ){
			NewLines = Lines;
		}else{
			ArrayList<Integer> NewLine = new ArrayList<Integer>();
			int n = Lines.length;
			int m = AddLines.length;
			for( int i = 0; i < n; ++i) NewLine.add( new Integer( Lines[ i]));
			for( int j = 0; j < m; ++j ){
				int jA = AddLines[ j];
				int i;
				for( i = n; --i >= 0; ) if(  Lines[ i] == jA ) break;
				if( i < 0 ) NewLine.add( new Integer( jA));
			}
			n = NewLine.size();
			if( n > 0 ){
				NewLines = new int[ n];
				for( int i = n;  --i >= 0;  ){
					NewLines[ i] = (( Integer)NewLine.get( i)).intValue();
				}
			}
		}
		return NewLines;
	}


}
