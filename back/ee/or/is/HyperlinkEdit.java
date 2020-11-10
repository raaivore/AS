/*
 * 
 */
package ee.or.is;
// TODO: Auto-generated Javadoc

/**
 * The Class HyperlinkEdit.
 * 
 * @author or 20.01.2005
 */
public class HyperlinkEdit {
	
	/** The s hyperlink. */
	String sHyperlink = null;
	
	/** The s name. */
	String sName = "puudub";
	
	/** The s link. */
	String sLink = " ";
	
	/**
	 * Instantiates a new hyperlink edit.
	 * 
	 * @param sText the s text
	 */
	public HyperlinkEdit( String sText)
	{
		sHyperlink = sText;
		if( sText == null ) return;
		int iS = sText.indexOf( '#');
		if( iS > 0 ){
			sName = sText.substring( 0, iS);
			int iE = sText.indexOf( '#', ++iS);
			if( iE > 0 ) sLink = sText.substring( iS, iE);
		}
	}
	
	/**
	 * Gets the link.
	 * 
	 * @return the link
	 */
	public String getLink() {
		return sLink;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Gets the hyperlink.
	 * 
	 * @return the hyperlink
	 */
	public String getHyperlink() {
		return sHyperlink;
	}
	
	
}
