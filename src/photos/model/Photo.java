package photos.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;

/**
 * A class that represents an individual photo in the Photo's application.
 * 
 * @author Nick Fasullo
 *
 */
public class Photo implements Serializable{

	/**
	 * Value for Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Caption of the photo.
	 */
	private String caption;

	/**
	 * The date of the photo. The last time the photo was modified.
	 */
	private Calendar date;

	/**
	 * Represents the file where the photo is located.
	 */
	private File file;

	/**
	 * The structure that holds all of the photo's {@link Tag Tag} objects.
	 */
	private ArrayList<Tag> tags;

	/**
	 * Creates a new <code>Photo</code> instance from the given file with the given
	 * caption.
	 * 
	 * @param file
	 *            a File containing a photo
	 * @param caption
	 *            the caption for the photo
	 * @throws FileNotFoundException
	 *             if the given file doesn't exist or cannot be read
	 */
	public Photo(File file, String caption) throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException();

		this.file = file;

		date = Calendar.getInstance();
		date.setTimeInMillis(file.lastModified());

		this.caption = caption;

		tags = new ArrayList<Tag>();
	}

	/**
	 * Creates a new <code>Photo</code> instance from the given file with the given
	 * caption and tags.
	 * 
	 * @param file
	 *            a File containing a photo
	 * @param caption
	 *            the caption for the photo
	 * @param tags
	 *            list of Tag objects for the Photo
	 * @throws FileNotFoundException
	 *             if the given file doesn't exist or cannot be read
	 */
	public Photo(File file, String caption, ArrayList<Tag> tags) throws FileNotFoundException {
		this(file, caption);

		this.tags.addAll(tags);
	}

	/**
	 * Returns a <code>BufferedImage</code> of the Photo by reading the image at the
	 * {@link #file file} location.
	 * 
	 * @return the BufferedImage of the photo
	 * @throws IOException
	 *             if the {@link #file file} cannot be read
	 */
	public BufferedImage getImage() throws IOException {
		return ImageIO.read(file);
	}

	/**
	 * Returns a <code>BufferedImage</code> thumbnail of the Photo by reading the
	 * image at the {@link #file file} location.
	 * 
	 * <p>
	 * The thumbnail is a scaled down version (100x100) of the actual photo.
	 * </p>
	 * 
	 * @return the BufferedImage thumbnail of the photo
	 * @throws IOException
	 *             if the {@link #file file} cannot be read
	 */
	public BufferedImage getThumbnail() throws IOException {
		BufferedImage thumb = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		thumb.createGraphics().drawImage(getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH), 0, 0, null);

		return thumb;
	}

	/**
	 * Change the photo's caption.
	 * 
	 * @param newCaption
	 *            the new caption for the photo
	 */
	public void recaption(String newCaption) {
		caption = newCaption;
	}

	/**
	 * Compares this <code>Photo</code> to the specified <code>Object</code>.
	 * 
	 * <p>
	 * The result is <code>true</code> if the argument is a <code>Photo</code>
	 * object and has the same path, obtained from {@link #file file}, as this
	 * object.
	 * </p>
	 * 
	 * @param o
	 *            object to compare
	 * @return true if this object is equal to o; false otherwise.
	 **/
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Photo))
			return false;

		Photo photo = (Photo) o;

		try {
			if (!file.getCanonicalPath().equals(photo.getFile().getCanonicalPath()))
				return false;
		} catch (Exception e) {
			if (!file.getPath().equals(photo.getFile().getPath()))
				return false;
		}

		return true;
	}

	/**
	 * Adds given tag to the photo.
	 * 
	 * <p>
	 * If the given tag is already in the photo it will not be added and will return
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param tag
	 *            tag to add to the photo
	 * @return true if tag is added; false otherwise.
	 */
	public boolean addTag(Tag tag) {
		if (tags.contains(tag))
			return false;

		tags.add(tag);

		return true;
	}

	/**
	 * Removes given tag from the photo.
	 * 
	 * <p>
	 * If the photo doesn't have the tag, the tags remain unchanged and returns
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param tag
	 *            tag to remove from the photo
	 * @return true if photo is removed; false otherwise.
	 */
	public boolean removeTag(Tag tag) {
		return tags.remove(tag);
	}

	/**
	 * Gets photo's caption.
	 * 
	 * @return photo's caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets photo's caption.
	 * 
	 * @param caption
	 *            caption's new value
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Gets the date of the photo.
	 * 
	 * @return date of the photo
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Gets the file of the photo.
	 * 
	 * @return file of the photo
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets photo's tags.
	 * 
	 * @return photo's tags
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}
}
