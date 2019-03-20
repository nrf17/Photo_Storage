package photos.model;

import java.io.Serializable;

/**
 * A class that represents a tag in the Photo's application.
 * 
 * @author Nick Fasullo
 * 
 */
public class Tag implements Serializable{

	/**
	 * Value for Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The tag name.
	 */
	private String name;

	/**
	 * The tag value.
	 */
	private String value;

	/**
	 * Creates a new <code>Tag</code> instance with the given name and value.
	 * 
	 * @param name
	 *            the tag name
	 * @param value
	 *            the tag value
	 */
	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Compares this <code>Tag</code> to the specified <code>Object</code>.
	 * 
	 * <p>
	 * The result is <code>true</code> if the argument is a <code>Tag</code> object
	 * and has the same <code>name</code> and <code>value</code> as this.
	 * </p>
	 * 
	 * @param o
	 *            object to compare
	 * @return true if this object is equal to o; false otherwise.
	 **/
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tag))
			return false;

		Tag tag = (Tag) o;

		if (!name.equals(tag.getName()))
			return false;

		if (!value.equals(tag.getValue()))
			return false;

		return true;
	}

	/**
	 * Gets tag's value.
	 * 
	 * @return tag's value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets tag's value.
	 * 
	 * @param value
	 *            tag's new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets tag's name.
	 * 
	 * @return tag's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Prints the tag in Name: Value format
	 * 
	 * @return tag name value pair
	 */
	public String toString(){
		return this.name + ": " + this.value;
	}
	
	
}
