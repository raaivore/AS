/*
 * 
 */
package ee.or.is;

import java.util.List;
import java.awt.*;

/**
 * The Class FunHTML.
 * 
 * @author administrator
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class FunHTML 
{
	/**
	 * Toggle image button.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sImage the s image
	 * @param bUp the b up
	 * @param sScript the s script
	 * 
	 * @return the string
	 */
	public static String ToggleImageButton( String sName, String sAlt, String sImage, 
			boolean bUp, String sScript)
	{
		return "\n<SPAN CLASS=\"" + (bUp? "tb_up": "tb_down") + 
			"\" id=\"" + sName + "\" name=\"" + sName + "\"" + ((sScript!=null)? " onclick=" + sScript: "") + 
//			" style`\" height=100%; \">" + 
			" >" + 
//			"\" LANGUAGE=javascript onclick=\"return " + sName + "_onclick()\">" +
			"\n<IMG CLASS=\"tb_image\" alt=\"" + sAlt + "\" src=\"" + sImage + "\" />" +
			"\n</SPAN>";   
	}
	
	/**
	 * Toggle text button.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param bUp the b up
	 * 
	 * @return the string
	 */
	public static String ToggleTextButton( String sName, String sAlt, 
		String sText, boolean bUp)
	{
		return "\n<SPAN CLASS=\"" + (bUp? "tb_up": "tb_down") + 
			"\" id=\"" + sName + "\" alt=\"" + sAlt + "\" name=\"" + sName + "\" >" + sText + 
			"\n</SPAN>";   
	}
	
	/**
	 * Toggle image button.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param bUp the b up
	 * 
	 * @return the string
	 */
	public static String ToggleImageButton( String sName, String sAlt, 
			String sText, boolean bUp)
	{
		return ToggleImageButton( sName, sAlt, sText, bUp, null);
	}
	
	/**
	 * Text button control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param bUp the b up
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String TextButtonControl( String sName, String sAlt, 
		String sText, boolean bUp, boolean bHidden)
	{
		return "\n<SPAN CLASS=\"" + (bUp? "tbt_up": "tbt_down") + 
			"\" id=\"" + sName + "\" alt=\"" + sAlt + "\" name=\"" + sName +  "\"" + 
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
			" >" + sText + 			
			"\n</SPAN>";   
	}
	
	/**
	 * Text button control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String TextButtonControl( String sName, String sAlt, 
		String sText, boolean bHidden)
	{
		return "\n<BUTTON CLASS=\"INPUT_BUTTON\"" + 
			" id=\"" + sName + "\" alt=\"" + sAlt + "\" name=\"" + sName + "\" type=\"submit\"" +  
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
			" >" + sText + 			
			"\n</BUTTON>";   
	}
	
	/**
	 * Text button control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param sScript the s script
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String TextButtonControl( String sName, String sAlt, 
		String sText, String sScript, boolean bHidden)
	{
		return "\n<INPUT TYPE=\"button\" CLASS=\"INPUT_BUTTON\"" + 
			" id=\"" + sName + "\" alt=\"" + sAlt + "\" name=\"" + sName + "\" " +  
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
			((sScript!=null)? sScript: "") + " VALUE=\"" + sText +  "\" >" +  			
			"</INPUT>";   
	}
	
	/**
	 * Text button control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sText the s text
	 * @param sScript the s script
	 * 
	 * @return the string
	 */
	public static String TextButtonControl( String sName, String sAlt, 
		String sText, String sScript)
	{
		return TextButtonControl( sName, sAlt, sText, sScript, false);
	}
	
	/**
	 * Label control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param sLabel the s label
	 * 
	 * @return the string
	 */
	public static String LabelControl( String sName, String sAlt, String sValue, String sLabel)
	{
		return	
			"\n<P align=left >" + sLabel + 
			((sValue != null)? " = " + sValue: "") + 
//			"\n	<LABEL>" + sLabel + "</LABEL>" +
//			"\n	<INPUT id=\"" + sName + "\" name=\"" + sName + "\"" +
//			" value=\"" + sValue + "\" WIDTH=30>" + 
			"</P>";
	}
	
	/**
	 * Label control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param sLabel the s label
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String LabelControl( String sName, String sAlt, String sValue, String sLabel, boolean bHidden)
	{
		return	
			"\n<P align=left id=\"" + sName + "\" name=\"" + sName + "\"" + 
			(bHidden? " style=\"VISIBILITY: hidden\" >": " >") + 
			sLabel + 
			((sValue != null)? " = " + sValue: "") + 
			"</P>";
	}
	
	/**
	 * Label control c.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param sLabel the s label
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String LabelControlC( String sName, String sAlt, String sValue, String sLabel, boolean bHidden)
	{
		return	
			"\n<P align=center id=\"" + sName + "\" name=\"" + sName + "\"" + 
			(bHidden? " style=\"VISIBILITY: hidden\" >": " >") + 
			sLabel + 
			((sValue != null)? " = " + sValue: "") + 
			"</P>";
	}
	
	/**
	 * Text area control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String TextAreaControl( String sName, String sAlt, String sValue, boolean bHidden)
	{
		return	"\n<TEXTAREA id=\"" + sName + "\" name=\"" + sName + "\"" +
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
//			(bHidden? " type=\"hidden\"": "") + 
			" value=\"" + ((sValue==null)? "": sValue) + "\" />";
	}
	
	/**
	 * Input control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String InputControl( String sName, String sAlt, String sValue, boolean bHidden)
	{
		return	"\n<INPUT id=\"" + sName + "\" name=\"" + sName + "\"" +
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
//			(bHidden? " type=\"hidden\"": "") + 
			" value=\"" + ((sValue==null)? "": sValue) + "\" />";
	}
	
	/**
	 * Input control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param BBox the b box
	 * 
	 * @return the string
	 */
	public static String InputControl( String sName, String sAlt, Rectangle BBox)
	{
		StringBuffer HTML = new StringBuffer();
		HTML.append( InputControl( sName+"L", sAlt, BBox.x, false));
		HTML.append( InputControl( sName+"T", sAlt, BBox.y, false));
		HTML.append( InputControl( sName+"W", sAlt, BBox.width, false));
		HTML.append( InputControl( sName+"H", sAlt, BBox.height, false));
		return HTML.toString();
	}
	
	/**
	 * Input control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param iValue the i value
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String InputControl( String sName, String sAlt, int iValue, boolean bHidden)
	{
		return InputControl( sName, sAlt, Integer.toString( iValue), bHidden);
	}
	
	/**
	 * Input control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param dValue the d value
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String InputControl( String sName, String sAlt, double dValue, boolean bHidden)
	{
		return InputControl( sName, sAlt, Double.toString( dValue), bHidden);
	}
	
	/**
	 * Input text control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param iSize the i size
	 * 
	 * @return the string
	 */
	public static String InputTextControl( String sName, String sAlt, 
		String sValue, int iSize)
	{
		return	"\n<INPUT \" name=\"" + sName + "\" class=\"INPUT_TEXT\"" +
			" value=\"" + sValue + "\" SIZE=" + iSize + " align=left>";
	}
	
	/**
	 * Input number control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param iSize the i size
	 * 
	 * @return the string
	 */
	public static String InputNumberControl( String sName, String sAlt, 
		String sValue, int iSize)
	{
		return	"\n<INPUT \" name=\"" + sName + "\" class=\"INPUT_NUMBER\"" +
			" value=\"" + sValue + "\" SIZE=" + iSize + " WIDTH=" + iSize + " align=right>";
	}
	
	/**
	 * Input number control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param iValue the i value
	 * @param iSize the i size
	 * 
	 * @return the string
	 */
	public static String InputNumberControl( String sName, String sAlt, 
		int iValue, int iSize)
	{
		return InputNumberControl( sName, sAlt, Integer.toString( iValue), iSize);
	}
	
	/**
	 * Input number control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param lValue the l value
	 * @param iSize the i size
	 * 
	 * @return the string
	 */
	public static String InputNumberControl( String sName, String sAlt, 
		long lValue, int iSize)
	{
		return InputNumberControl( sName, sAlt, Long.toString( lValue), iSize);
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValues the s values
	 * @param sNames the s names
	 * @param sDValue the s d value
	 * @param sControl the s control
	 * @param bEmpty the b empty
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		String sValues[], String sNames[], String sDValue, String sControl, boolean bEmpty, boolean bHidden)
	{
		if( sDValue != null ) sDValue = sDValue.trim();
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
			"\" name=\"" + sName + "\"" + 
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + 
			" WIDTH=30" + 
			((sControl != null)? " onchange=\"" + sControl + "\"": "") +
			">");
		if( bEmpty  ) sRet.append( "\n<OPTION/>"); 
		if( sValues != null ){
			for( int i = 0; i < sValues.length; ++i){
				sRet.append( "\n<OPTION value =\"" + sValues[ i] + "\"");
				if( sDValue != null && sDValue.equals( sValues[ i]) ) sRet.append( " selected");
				 
				sRet.append(  ">" +  sNames[ i] + "</OPTION>");
			}
		}else if( sNames != null ){
			int iDValue = -1;
			if( sDValue != null ) 
				try {
					iDValue = Integer.parseInt( sDValue);
				} catch( Exception e ){
				}
			
			for( int i = 0; i < sNames.length; ++i){
				sRet.append( "\n<OPTION value =\"" + i + "\"");
				if( iDValue == i ) sRet.append( " selected");
				 
				sRet.append(  ">" +  sNames[ i] + "</OPTION>");
			}
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValues the s values
	 * @param sNames the s names
	 * @param iDValue the i d value
	 * @param sControl the s control
	 * @param bEmpty the b empty
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		String sValues[], String sNames[], int iDValue, String sControl, boolean bEmpty, boolean bHidden)
	{
		return SelectControl( sName, sAlt, sValues, sNames, Integer.toString( iDValue), 
			sControl, bEmpty, bHidden);
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param sDValue the s d value
	 * @param sControl the s control
	 * @param bEmpty the b empty
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		String sValue[], String sDValue, String sControl, boolean bEmpty, boolean bHidden)
	{
		return SelectControl( sName, sAlt, sValue, sValue, sDValue, sControl, bEmpty, bHidden);
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param i0 the i0
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		int sValue[], int i0, boolean bHidden)
	{
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
			"\" name=\"" + sName + "\"" + 
			(bHidden? " style=\"VISIBILITY: hidden\"": "") + " WIDTH=30>");
//			(bHidden? "\" type=\"hidden\"": "") + " WIDTH=30>"); 
		if( sValue != null ) for( int i = i0; i < sValue.length; i+=2){
			sRet.append( "\n<OPTION>" + Integer.toString( sValue[ i]) + "</OPTION>");
//			
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param i0 the i0
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		double sValue[], int i0, boolean bHidden)
	{
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
			"\" name=\"" + sName + "\"" + 
			(bHidden? "\" type=\"hidden\"": "") + " WIDTH=100>"); 
		for( int i = i0; i < sValue.length; i+=2){
			sRet.append( "\n<OPTION>" + Double.toString( sValue[ i]) + "</OPTION>");
//			
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}
/*	public static String SelectControl( String sName, String sAlt, 
		OrderedVector OrderedValues, int iOrder, boolean bHidden)
	{
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
			"\" name=\"" + sName + "\"" + 
			(bHidden? "\" type=\"hidden\"": "") + " WIDTH=100>"); 
		if( iOrder < 0 ) sRet.append( "\n<OPTION selected/>"); 
		for( int i = 0; i < OrderedValues.size(); ++i){	
			if( OrderedValues.getOrderID( i) == iOrder ) 
				sRet.append( "\n<OPTION selected>" + OrderedValues.getOrderName( i)); 
			else
				sRet.append( "\n<OPTION>" + OrderedValues.getOrderName( i)); 
			sRet.append( "</OPTION>");
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}*/
	/**
 * Select control.
 * 
 * @param sFName the s f name
 * @param sAlt the s alt
 * @param OrderedValues the ordered values
 * @param OrderedNames the ordered names
 * @param sDValue the s d value
 * @param sControl the s control
 * @param bEmpty the b empty
 * @param bHidden the b hidden
 * 
 * @return the string
 */
public static String SelectControl( String sFName, String sAlt, 
		List<String> OrderedValues, List<String> OrderedNames, String sDValue, String sControl, boolean bEmpty, boolean bHidden)
	{
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sFName + 
			"\" name=\"" + sFName + "\"" + 
			(bHidden? "\" type=\"hidden\"": "") + " WIDTH=100" + 
			((sControl != null)? " onchange=\"" + sControl + "\"": "") +
			">"); 
		if( bEmpty )	
			if( sDValue == null || sDValue.length() == 0 ) 
				sRet.append( "\n<OPTION selected/>"); 
			else
				sRet.append( "\n<OPTION/>"); 
		if( OrderedValues != null ){
			for( int i = 0; i < OrderedValues.size(); ++i){	
				String sValue = ( String)OrderedValues.get( i);
				String sName = (OrderedNames!=null)? ( String)OrderedNames.get( i): sValue;
				sRet.append( "\n<OPTION value =\"" + sValue + "\""); 
				if( /* sDValue == null && i == 0 || */ sDValue != null && sDValue.equals( sValue) ) 
					sRet.append( " selected"); 
				sRet.append( ">" + sName + "</OPTION>");
			}
		}else if( OrderedNames != null ){
			for( int i = 0; i < OrderedNames.size(); ++i){	
				String sName = ( String)OrderedNames.get( i);
				String sValue = sName;				
				sRet.append( "\n<OPTION value =\"" + sValue + "\""); 
				if( sDValue != null && sDValue.equals( sValue) ) sRet.append( " selected"); 
				sRet.append( ">" + sName + "</OPTION>");
			}
		}else if( sDValue != null && sDValue.length() > 0 ){
			sRet.append( "\n<OPTION selected>" + sDValue + "</OPTION>"); 
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}
	
	/**
	 * Select control.
	 * 
	 * @param sFName the s f name
	 * @param sAlt the s alt
	 * @param OrderedValues the ordered values
	 * @param OrderedNames the ordered names
	 * @param sDValue the s d value
	 * @param bEmpty the b empty
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sFName, String sAlt, 
		List<String> OrderedValues, List<String> OrderedNames, String sDValue, boolean bEmpty, boolean bHidden)
	{
		return SelectControl( sFName, sAlt, OrderedValues, OrderedNames, sDValue, null, bEmpty, bHidden);	
	}
	
	/**
	 * Select control.
	 * 
	 * @param sFName the s f name
	 * @param sAlt the s alt
	 * @param OrderedValues the ordered values
	 * @param OrderedNames the ordered names
	 * @param iDValue the i d value
	 * @param sControl the s control
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sFName, String sAlt, 
		List<String> OrderedValues, List<String> OrderedNames, int iDValue, String sControl, boolean bHidden)
	{
		return SelectControl( sFName, sAlt, OrderedValues, OrderedNames, Integer.toString( iDValue),
			sControl, false, bHidden);	
	}
	
	/**
	 * Select control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param OrderedValues the ordered values
	 * @param sDValue the s d value
	 * @param sControl the s control
	 * @param bEmpty the b empty
	 * 
	 * @return the string
	 */
	public static String SelectControl( String sName, String sAlt, 
		List<String> OrderedValues, String sDValue, String sControl, boolean bEmpty)
	{
		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
			"\" name=\"" + sName + "\" WIDTH=100 " +
			((sControl != null)? " onchange=\"" + sControl + "\"": "") + ">"); 
		if( sDValue == null ) sRet.append( "\n<OPTION selected/>"); 
		else if( bEmpty ) sRet.append( "\n<OPTION/>");
		if( OrderedValues != null ) for( int i = 0; i < OrderedValues.size(); ++i){	
			String sValue = ( String)OrderedValues.get( i);
			if( /* sDValue == null && i == 0 || */ sDValue != null && sValue.equals( sDValue) ) 
				sRet.append( "\n<OPTION value =\"" + sValue + "\" selected>" + sValue); 
			else
				sRet.append( "\n<OPTION value =\"" + sValue + "\">" + sValue); 
			sRet.append( "</OPTION>");
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
	}
	
	/**
	 * Select radio control.
	 * 
	 * @param sName the s name
	 * @param sGroup the s group
	 * @param sValueAlt the s value alt
	 * @param sValue the s value
	 * @param iControl the i control
	 * 
	 * @return the string
	 */
	public static String SelectRadioControl( String sName, String sGroup, 
		String sValueAlt[], String sValue[], int iControl){

//		StringBuffer sRet = new StringBuffer( "\n<SELECT id=\"" + sName + 
//			"\" name=\"" + sName + "\"> "); 

		StringBuffer sRet = new StringBuffer( ""); 
	    sRet.append( FunHTML.LabelControl( "L_" + sName, "", null, sGroup));
		sRet.append( "\n<P>"); 
		for( int i = 0; i < sValue.length; ++i){
			sRet.append( "\n<INPUT type=text id=\"" + sName  + "\" name=\"" + 
				sName + "\"" + " value=\"" + i + "\"" +
				((i==iControl)? " CHECKED": "") + 
				((sValueAlt != null )? "\" alt=\"" + sValueAlt[ i]: "") + 
				" type=radio />" + sValue[ i] + "<BR>");
		}
//		sRet.append( "\n</SELECT>");
		sRet.append( "\n</P>");
		return sRet.toString();
	}	
	
	/**
	 * List control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param Values the values
	 * @param iNum the i num
	 * 
	 * @return the string
	 */
	public static String ListControl( String sName, String sAlt, List<String> Values, int iNum)
	{
		return ListControl( sName, sAlt, Values, iNum, false);
	}
	
	/**
	 * List control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param Values the values
	 * @param iNum the i num
	 * @param bHidden the b hidden
	 * 
	 * @return the string
	 */
	public static String ListControl( String sName, String sAlt, List<String> Values, int iNum, boolean bHidden)
	{
		StringBuffer sRet = new StringBuffer( 
			"\n<SELECT id=\"" + sName +	"\" name=\"" + sName + "\"" + 
			(bHidden? " style=\"VISIBILITY:hidden\"": "") + 
			" WIDTH=100 SIZE='" + iNum + "' >");
		for( int i = 0; i < Values.size(); ++i){	
			String sValue = ( String)Values.get( i);
			sRet.append( "\n<OPTION>" + sValue + "</OPTION>");
		}
		sRet.append( "\n</SELECT>");
		return sRet.toString();
/*		StringBuffer sRet = new StringBuffer( "\n<UL id=\"" + sName + 
			"\" name=\"" + sName + "\"" + 
			(bHidden? "\" type=\"hidden\"": "") + " WIDTH=100>"); 
		for( int i = 0; i < Values.size(); ++i){	
			String sValue = ( String)Values.get( i);
			sRet.append( "\n<LI>" + sValue); 
			sRet.append( "</LI>");
		}
		sRet.append( "\n</UL>");
		return sRet.toString(); */
	} 
/*	public static String getTdStart()
	{
		return "<td class=\"textmenusmall\" bgcolor=\"#91CB3C\">";
	}
	public static String getTdEnd()
	{
		return "</td>";
	} */
	/**
 * Gets the head start.
 * 
 * @param sTitle the s title
 * @param sCSS the s css
 * 
 * @return the head start
 */
public static String getHeadStart( String sTitle, String sCSS)
	{
		return 
				"\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0  Transitional//EN\">" +
				"\n<HTML>" +
				"\n<HEAD>" + 
				"\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-15\">" +
				"\n<link rel=\"stylesheet\" type=\"text/css\" href=\"" + sCSS + "\">" +
				"\n<TITLE>" + sTitle + "</TITLE>";
	}
	
	/**
	 * Gets the head end.
	 * 
	 * @return the head end
	 */
	public static String getHeadEnd()
	{
		return "</HEAD>";
	}
	
	/**
	 * Gets the script.
	 * 
	 * @param sScript the s script
	 * 
	 * @return the script
	 */
	public static String getScript( String sScript)
	{
		return "<script type=\"text/javascript\" src=\"" + sScript + "\" ></script>";
		
	}
	
	/**
	 * Gets the form start.
	 * 
	 * @param sBodyClass the s body class
	 * @param sFormClass the s form class
	 * @param sName the s name
	 * @param iFormW the i form w
	 * @param iFormH the i form h
	 * 
	 * @return the form start
	 */
	public static String getFormStart( String sBodyClass, String sFormClass, String sName, int iFormW, int iFormH)
	{
	 	return	"\n<BODY " + ((sBodyClass!=null)? " class=\"" + sBodyClass + "\">": ">") +
	    		"\n<FORM method=\"get\"" + 
	    		((sFormClass!=null)? " class=\"" + sFormClass + "\"": "") + 
	    		" id=\"" + sName + "\"" +  "\" name=\"" + sName + "\"" +
        		" style=\" width:" + iFormW + "px; height:" + iFormH + "px \" >";
	}
	
	/**
	 * Gets the body start.
	 * 
	 * @param sClass the s class
	 * 
	 * @return the body start
	 */
	public static String getBodyStart( String sClass)
	{
	 	return	"\n<BODY class=\"" + sClass + "\">";
//	 	return	"\n<BODY bgcolor=\"#E8F5D9\">" +
	}
	
	/**
	 * Gets the body start.
	 * 
	 * @return the body start
	 */
	public static String getBodyStart()
	{
	 	return	"\n<BODY class=\"data_form\">";
//	 	return	"\n<BODY bgcolor=\"#E8F5D9\">" +
	}
	
	/**
	 * Gets the form start.
	 * 
	 * @param sClass the s class
	 * @param sName the s name
	 * @param iFormW the i form w
	 * @param iFormH the i form h
	 * 
	 * @return the form start
	 */
	public static String getFormStart( String sClass, String sName, int iFormW, int iFormH)
	{
	 	return	"\n<BODY class=\"data_form\">" +
//	 	return	"\n<BODY bgcolor=\"#E8F5D9\">" +
	    		"\n<FORM method=\"get\"" + 
	    		((sClass!=null)? " class=\"" + sClass + "\"": "") + 
	    		" id=\"" + sName + "\"" +  "\" name=\"" + sName + "\"" +
        		" style=\" width:100%; height:100% \" >";
	}
	
	/**
	 * Gets the form end.
	 * 
	 * @return the form end
	 */
	public static String getFormEnd()
	{
		return	"\n</FORM>" +
       			"\n</BODY>" +
       			"\n</HTML>";
	}
	
	/**
	 * Gets the table start.
	 * 
	 * @return the table start
	 */
	public static String getTableStart()
	{
		return	"\n<table cellpadding=\"2\" cellspacing=\"2\" align=\"center\">";
	}	
	
	/**
	 * Gets the table start.
	 * 
	 * @param sCaptionClass the s caption class
	 * @param sCaption the s caption
	 * 
	 * @return the table start
	 */
	public static String getTableStart( String sCaptionClass, String sCaption)
	{
		return	"\n<table cellpadding=\"2\" cellspacing=\"2\" align=\"center\">" +
			"\n<CAPTION " +
			((sCaptionClass != null)? "class=\"" + sCaptionClass + "\"": "") +
			" >" + sCaption + "</CAPTION>";
	}	
	
	/**
	 * Gets the table end.
	 * 
	 * @return the table end
	 */
	public static String getTableEnd()
	{
		return	"\n</table>";
	}	
	
	/**
	 * Gets the row start.
	 * 
	 * @return the row start
	 */
	public static String getRowStart()
	{
		return	"\n<tr>";
	}	
	
	/**
	 * Gets the row end.
	 * 
	 * @return the row end
	 */
	public static String getRowEnd()
	{
		return	"\n</tr>";
	}	
	
	/**
	 * Gets the col start.
	 * 
	 * @param sClass the s class
	 * @param sColor the s color
	 * @param iColspan the i colspan
	 * 
	 * @return the col start
	 */
	public static String getColStart( String sClass, String sColor, int iColspan)
	{
		return	"\n<td class=\"" + sClass + 
			((sColor!=null)? "\" bgcolor=\"" + sColor: "") +
			((iColspan>0)? "\" colspan=\"" + iColspan: "") +
			"\">";
	}	
	
	/**
	 * Gets the table row.
	 * 
	 * @param sLabel the s label
	 * @param sID the s id
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * @param bHidden the b hidden
	 * 
	 * @return the table row
	 */
	public static String getTableRow( String sLabel, String sID, String sAlt, String sValue, boolean bHidden)
	{
		return
			getRowStart() + 
			FunHTML.getColStart( "textSubHeader", null, 0) +
			sLabel + 
			FunHTML.getColEnd() +
			FunHTML.getColStart( "textSubHeader", null, 0) + 
		    FunHTML.InputControl( sID, sAlt, sValue, bHidden) +
			FunHTML.getColEnd() +
			FunHTML.getRowEnd(); 
	}
	
	/**
	 * Gets the table row.
	 * 
	 * @param sLabel the s label
	 * @param sID the s id
	 * @param sAlt the s alt
	 * @param sValue the s value
	 * 
	 * @return the table row
	 */
	public static String getTableRow( String sLabel, String sID, String sAlt, String sValue)
	{
		return getTableRow( sLabel, sID, sAlt, sValue, false);
	}
	
	/**
	 * Gets the col end.
	 * 
	 * @return the col end
	 */
	public static String getColEnd()
	{
		return	"\n</td>";
	}	
	
	/**
	 * Gets the table caption.
	 * 
	 * @param sTitle the s title
	 * 
	 * @return the table caption
	 */
	public static String getTableCaption( String sTitle)
	{
		return	"\n<h3>" + sTitle + "</h3>";
	}	
	
	/**
	 * Checkbox control.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * @param bValue the b value
	 * 
	 * @return the string
	 */
	public static String CheckboxControl(  String sName, String sAlt, boolean bValue)
	{
		return "<INPUT TYPE=checkbox" +
    		" id=\"" + sName + "\"" +  "\" name=\"" + sName + "\"" +
    		(bValue? " CHECKED": "") +
    		((sAlt!=null)? " alt=\"" + sAlt + "\"": "") + 
    		" />";
	}
	
	/**
	 * Select control colors.
	 * 
	 * @param sName the s name
	 * @param sAlt the s alt
	 * 
	 * @return the string
	 */
	public static String SelectControlColors(  String sName, String sAlt)
	{
		return	"<select " +
    		" id=\"" + sName + "\"" +  "\" name=\"" + sName + "\"" +
    		((sAlt!=null)? " alt=\"" + sAlt + "\"": "") + 
    		" >" +
			"<option selected />" + 
			"<option value=\"0000ff\" style=\"background-color=#0000ff\" >Sinine" + "<option>" +
			"<option value=\"00ff00\" style=\"background-color=#00ff00\" >Roheline" + "<option>" +
			"<option value=\"ff0000\" style=\"background-color=#ff0000\" >Punane" + "<option>" +
			"<option value=\"000000\" style=\"background-color=#000000\" >Must" + "<option>" +
			"<option value=\"c0c0c0\" style=\"background-color=#c0c0c0\" >Hall" + "<option>" +
			"<option value=\"fffffe\" style=\"background-color=#ffffff\" >Valge" + "<option>" +
			"<option value=\"5c3317\" style=\"background-color=#5c3317\" >Pruun" + "<option>" +
			"<option value=\"00ffff\" style=\"background-color=#00ffff\" >Tsüaan" + "<option>" +
			"<option value=\"ee9a00\" style=\"background-color=#ee9a00\" >Oran?" + "<option>" +
			"<option value=\"ff1493\" style=\"background-color=#ff1493\" >Roosa" + "<option>" +
			"<option value=\"c000c0\" style=\"background-color=#c000c0\" >Lilla" + "<option>" +
			"<option value=\"ffff00\" style=\"background-color=#ffff00\" >Kollane" + "<option>" +
			"</select>"; 
	}
}


