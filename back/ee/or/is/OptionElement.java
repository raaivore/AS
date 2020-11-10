/*
 * 
 */
package ee.or.is;

/**
 * The Interface OptionElement.
 * 
 * @author Administrator
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface OptionElement {

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue();
	public int getIntValue();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the unique name.
	 * 
	 * @return the unique name
	 */
	public String getUniqueName();
}
