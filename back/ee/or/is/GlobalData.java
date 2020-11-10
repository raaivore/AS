/*
 * 
 */
package ee.or.is;

import java.util.ArrayList;



/**
 * 
 * 
 * 
 * 
 * The Class GlobalData.
 */
public abstract class GlobalData {

	/**
	 * Checks if is l equal.
	 * 
	 * @param s1 the s1
	 * @param s2 the s2
	 * 
	 * @return true, if is l equal
	 */
	public static boolean isLEqual( String s1, String s2) 
	{
		if( s2 == null ) return true;
		if( s1 != null ){
			String sS1 = s1.trim().toLowerCase();
			int n1 = sS1.length();
			String sS2 = s2.trim().toLowerCase();
			int n2 = sS2.length();
			if( n1 >= n2 ){
//				if( sS1.substring( 0, n2).equalsIgnoreCase( sS2) ) return true;
				return sS1.indexOf( sS2) >= 0;
			}
		}
		return false;
	}
	public static boolean isStartsWith( String s1, String s2) 
	{
		if( s2 == null ) return true;
		if( s1 != null ){
			String sS1 = s1.trim().toLowerCase();
			int n1 = sS1.length();
			String sS2 = s2.trim().toLowerCase();
			int n2 = sS2.length();
			if( n1 >= n2 ){
//				if( sS1.substring( 0, n2).equalsIgnoreCase( sS2) ) return true;
				return sS1.indexOf( sS2) == 0;
			}
		}
		return false;
	}
	
	/**
	 * Int to string.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the string
	 */
	public static String IntToString( int iValue) 
	{
		return Integer.toString( iValue);
	}  
	public static String StringToLength( String sValue, int iLength) 
	{
		int n = (sValue != null)? sValue.length(): 0;
		if( n == iLength ) return sValue;
		if( n > iLength ) return sValue.substring( 0, iLength);
		StringBuffer aString = ( n > 0 )? new StringBuffer( sValue): new StringBuffer();
		for( int i = n; i < iLength; ++i ) aString.append( ' ');
		return aString.toString();
	}  
	
	/**
	 * Int to string2.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the string
	 */
	public static String IntToString2( int iValue) 
	{
		return (iValue>=10)? Integer.toString( iValue): "0" + iValue;
	}  
	
	/**
	 * Int to string zero.
	 * 
	 * @param iValue the i value
	 * @param iLen the i len
	 * 
	 * @return the string
	 */
	public static String IntToStringZero( int iValue, int iLen) 
	{
		String	sValue = IntToString( iValue); 
		int		n = (sValue!=null)? sValue.length(): 0;
		if( n > 0 && n < iLen ){
			StringBuffer sBuf = new StringBuffer();
			for( int i = iLen-n;  --i >= 0; ) sBuf.append( '0');
			sBuf.append( sValue);
			return sBuf.toString();
		}
		return sValue;
	}
	
	/**
	 * Gets the int.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the int
	 */
	public static int getInt( String sValue)
	{
		int iRet = 0;
		try {
			if( sValue != null ) iRet = ( int)Math.round( getDouble( sValue));
		} catch ( Exception e) {
		}
        return iRet;
	}
	public static long getLong( String sValue)
	{
		long lRet = 0;
		try {
			if( sValue != null ) lRet = Math.round( getDouble( sValue));
		} catch ( Exception e) {
		}
        return lRet;
	}
	public static double getDouble( String sValue)
	{
		double dRet = 0;
		try {
			if( sValue != null ){
				dRet = Double.parseDouble( sValue);
			}
		} catch ( Exception e) {
			try{
				boolean bComma = false;
				sValue = sValue.replace( ',', '.');
				for( int i = 0; i < sValue.length(); ++i){
					if( Character.isDigit( sValue.charAt( i)));
					else if( sValue.charAt( i) == '.' && !bComma ) bComma = true;
					else if( ( sValue.charAt( i) == ' ' || sValue.charAt( i) == '+' || sValue.charAt( i) == '-') && !bComma );
					else{
						return getDouble( sValue.substring( 0, i));
					}
				}
				dRet = Double.parseDouble( sValue);
			}catch( Exception aE ){
				Log.log( sValue);
				Log.log( aE);
			}
		}
        return dRet;
	}
	
	/**
	 * Round to int.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the int
	 */
	public static int RoundToIntXXX( double dValue)
	{
		return ( int)((dValue >= 0)? dValue + 0.5: dValue - 0.5);
	}
	
	/**
	 * Double to string.
	 * 
	 * @param d the d
	 * @param iP the i p
	 * 
	 * @return the string
	 */
	public static String DoubleToString( double d, int iP) 
	{
		if( d < 0 )	return "-" + DoubleToString( -d, iP);
		String sBuf = new String("");
		long i, iDec = 1;
		int	iLen;
		for( i = iP; --i >= 0;) iDec *= 10;
		
		i = Math.round( d*iDec);
		sBuf = String.valueOf( i);
		if( iP > 0 ){
			if( (iLen = sBuf.length()) > iP ) 
				sBuf = sBuf.substring( 0, iLen - iP) + "." + sBuf.substring( iLen - iP);
			else{ 
				String sBuf1 = "0.";
				for( i = iP-iLen; --i >= 0; ) sBuf1 += "0";
				sBuf = sBuf1 + sBuf;
			}
		}
		return sBuf;
	}
	
	/**
	 * Byte to hex.
	 * 
	 * @param sBuf the s buf
	 * @param b the b
	 */
	public static void ByteToHex( StringBuffer sBuf, int b)
	{
		int i = b;
		if( i < 0 ) i += 256;
		int i1 = i / 16;
		int i2 = i % 16;
		sBuf.append( ( char)((i1 <= 9)? '0'+i1 : 'a'+i1-10));
		sBuf.append( ( char)((i2 <= 9)? '0'+i2 : 'a'+i2-10));
	}
	public static StringBuffer BytesToHexString( byte [] bBytes)
	{
		if( bBytes == null ) return null;
		StringBuffer sBuf = new StringBuffer();
		for( int i = 0; i < bBytes.length; ++i) ByteToHex( sBuf, bBytes[ i]);
		return sBuf;
	}	
	/**
	 * Byte to hex.
	 * 
	 * @param b the b
	 * 
	 * @return the string
	 */
	public static  String ByteToHex( int b)
	{
		StringBuffer sBuf = new StringBuffer();
		ByteToHex( sBuf, b);
		return sBuf.toString();
	}
	
	/**
	 * Int2 to hex.
	 * 
	 * @param sBuf the s buf
	 * @param i the i
	 */
	public static void Int2ToHex( StringBuffer sBuf, int i)
	{
		ByteToHex( sBuf, i / 0x100);
		ByteToHex( sBuf, i % 0x100);
	}
	
	/**
	 * Int2 to hex.
	 * 
	 * @param i the i
	 * 
	 * @return the string
	 */
	public static  String Int2ToHex( int i)
	{
		StringBuffer sBuf = new StringBuffer();
		Int2ToHex( sBuf, i);
		return sBuf.toString();
	}
	
	/**
	 * Int4 to hex.
	 * 
	 * @param sBuf the s buf
	 * @param i the i
	 */
	public static void Int4ToHex( StringBuffer sBuf, int i)
	{
		Int2ToHex( sBuf, i / 0x1000);
		Int2ToHex( sBuf, i % 0x1000);
	}
	
	/**
	 * Int4 to hex.
	 * 
	 * @param i the i
	 * 
	 * @return the string
	 */
	public static  String Int4ToHex( int i)
	{
		StringBuffer sBuf = new StringBuffer();
		Int4ToHex( sBuf, i);
		return sBuf.toString();
	}
	
	/**
	 * Int8 to hex.
	 * 
	 * @param l the l
	 * 
	 * @return the string
	 */
	public static  String Int8ToHex( long l)
	{
		StringBuffer sBuf = new StringBuffer();
		Int8ToHex( sBuf, l);
		return sBuf.toString();
	}
	
	/**
	 * Int8 to hex.
	 * 
	 * @param sBuf the s buf
	 * @param l the l
	 */
	public static void Int8ToHex( StringBuffer sBuf, long l)
	{
		Int4ToHex( sBuf, ( int)( l / 0x100000000L));
		Int4ToHex( sBuf, ( int)( l % 0x100000000L));
	}
	
	/**
	 * Memory dump.
	 * 
	 * @param aSight the a sight
	 * @param aBuf the a buf
	 * @param iCount the i count
	 */
	public static void MemoryDump( Sight aSight, byte [] aBuf, int iCount)
	{
		for( int j0=0; j0<iCount;){
			StringBuffer sBuf = new StringBuffer(); 
			for( int j=30; --j>=0 && j0<iCount;){ 
				sBuf.append( " 0x");
				sBuf.append( ByteToHex( aBuf[ j0++]));		 			
			}
			if( aSight != null) aSight.log( sBuf.toString()); Log.info( sBuf.toString());
		}
	}
	public static void MemoryDump( Sight aSight, ArrayList<Integer> aBuf)
	{
		int iCount = aBuf.size();
		for( int j0=0; j0<iCount;){
			StringBuffer sBuf = new StringBuffer(); 
			for( int j=30; --j>=0 && j0<iCount;){
				int c = (( Integer)aBuf.get( j0++) ).intValue();
				sBuf.append( " 0x");
				sBuf.append( ByteToHex( c));
				sBuf.append( " ");
				sBuf.append( (char) (c & 0x000000FF));
			}
			if( aSight != null) aSight.log( sBuf.toString()); Log.info( sBuf.toString());
		}
	}
	public static String getFormatedString( String sFormat, String sPar1, String sPar2, String sPar3){
	    String sFormated = null;
	    if( sFormat != null){
		if( sPar1 != null ){
		    sFormated = sFormat.replaceAll( "$1", sPar1);
		    if( sPar2 != null ){
			sFormated = sFormated.replaceAll( "$2", sPar2);
			if( sPar3 != null ) sFormated = sFormated.replaceAll( "$3", sPar3);
		    }
		}else sFormated = sFormat; 
	    }
	    return sFormated;
	}
	/**
	 * String to int.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the int
	 */
	public static int getFirstIntFromString( String sValue)
	{
		int iSign = 1;
		int iRet = 0;
		if( sValue != null ) for( int i = 0; i < sValue.length(); ++i){
			char c = sValue.charAt( i);
			int iC = Character.digit( c, 10);
			if( iC < 0 ){
				 if( iRet == 0 ){
					if( c == '-' ) iSign = -1;
					else if( c == '+' ) iSign = 1;
//					else if( c != ' ' ) break;
				 }else break;
			}else iRet = iRet * 10 + iC;
		}
		return iRet * iSign;
	}
	public static String replaceLink( String sValue)
	{
		int i = sValue.indexOf( "http");
		if( i >= 0 ){
			String sBefore = sValue.substring( 0, i);
			String sLink = null;
			String sAfter = null;
			int n = sValue.length(), k = i;
			for( ; ++k < n; ) if( Character.isWhitespace( sValue.charAt( k))) break;
			if( k < n ){
				sLink = sValue.substring( i, k);
				sAfter = replaceLink( sValue.substring( k));
			}else
				sLink = sValue.substring( i);
			return sBefore + "<a href=" + sLink + " target='_blank'>" + sLink 
				+ "</a>" + ((sAfter != null)? sAfter: "");
		}
		return sValue;
	}
	public static boolean isInList( String sListNames, String sName)
	{
		if( sListNames != null ){
			String [] sNames = sListNames.split( ",");
			for( int i = sNames.length; --i >= 0; ) 
				if( sNames[ i].equalsIgnoreCase( sName) ) return true;
		}
		return false;
	}
	public static void StringDump( Sight aSight, String sName)
	{
		StringBuffer aBuf = new StringBuffer(); 
		for( int i = 0; i < sName.length(); ){
			aBuf.append( " 0x");
			Int2ToHex( aBuf, sName.codePointAt( i++));
			aBuf.append( " ");
		}
		aSight.log( aBuf.toString());
	}
	public static String convertUTF8( String sName)
	{
		if( sName == null ) return null;
		int i = sName.indexOf( 0x00c3);
		if( i < 0 ) return sName;
		StringBuffer aNewString = new StringBuffer( sName.substring( 0, i)); 
		for( ; i < sName.length(); ++i) {
			if( sName.charAt( i) == 0xc3 ){
				switch( sName.charAt( ++i)){
				case 0x00b5 : aNewString.append( 'µ'); break;
				case 0x00a4 : aNewString.append( '¤'); break;
				case 0x00b6 : aNewString.append( '¶'); break;
				case 0x00bc : aNewString.append( '¼'); break;
				case 0x0095 : aNewString.append( '•'); break;
				case 0x0084 : aNewString.append( '„'); break;
				case 0x0096 : aNewString.append( '–'); break;
				case 0x009c : aNewString.append( 'œ'); break;
				default:
					aNewString.append( sName.charAt( i));
				}
			}else  aNewString.append( sName.charAt( i));
		}
		return aNewString.toString();
	}
	public static int[] get( Integer[] IntegerArray) {

		int[] result = new int[IntegerArray.length];
		for (int i = 0; i < IntegerArray.length; i++) {
			result[i] = IntegerArray[i].intValue();
		}
		return result;
	}
	public static String toString( ArrayList<String> aObjects)
	{
		boolean bFirst = true;
		StringBuffer aBuf = new StringBuffer();
		for( Object aObject: aObjects){
			if( bFirst) bFirst = false;
			else aBuf.append( ", ");
			aBuf.append( aObject.toString());
		}
		return aBuf.toString();
	}
}
