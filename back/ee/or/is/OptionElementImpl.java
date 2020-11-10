/*
 * 
 */
package ee.or.is;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionElementImpl.
 * 
 * @author or 30.12.2004
 */
public class OptionElementImpl implements OptionElement 
{
	
	/** The s name. */
	String sName;
	
	/** The s value. */
	String sValue;
	
	/**
	 * Instantiates a new option element impl.
	 * 
	 * @param sName the s name
	 * @param sValue the s value
	 */
	public OptionElementImpl( String sName, String sValue)
	{
		this.sName = sName;
		this.sValue = sValue;
	}
	
	/**
	 * Instantiates a new option element impl.
	 * 
	 * @param sName the s name
	 * @param iValue the i value
	 */
	public OptionElementImpl( String sName, int iValue)
	{
		this.sName = sName;
		this.sValue = Integer.toString( iValue);
	}
	
	/* (non-Javadoc)
	 * @see or.is.OptionElement#getValue()
	 */
	public String getValue() {
		return sValue;
	}
	
	/* (non-Javadoc)
	 * @see or.is.OptionElement#getName()
	 */
	public String getName() {
		return sName;
	}
	
	/* (non-Javadoc)
	 * @see or.is.OptionElement#getUniqueName()
	 */
	public String getUniqueName(){ return sName;}

	/**
	 * Gets the int value.
	 * 
	 * @return the int value
	 */
	public int getIntValue() {
		try {
            return Integer.parseInt( sValue);
        } catch (NumberFormatException E) {
        }
        return 0;
	}
}
