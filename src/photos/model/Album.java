package photos.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A class that represents an album in the Photos application.
 * 
 * @author Nick Fasullo
 *
 */
public class Album implements Serializable{

	/**
	 * Value for Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the album.
	 */
	private String name;

	/**
	 * The structure that holds all of the album's {@link Photo Photo} objects.
	 */
	private ArrayList<Photo> photos;

	/**
	 * Creates a new <code>Album</code> instance with the given name.
	 * 
	 * @param name
	 *            album name
	 */
	public Album(String name) {
		this.name = name;

		photos = new ArrayList<Photo>();
	}

	/**
	 * Returns the date range of the album in a <code>String</code>
	 * 
	 * <p>
	 * Uses the date format <code>MM/DD/YYYY - MM/DD/YYYY</code>. If the album has
	 * no photos returns an empty string. If the album has one photo returns one
	 * date in the format <code>MM/DD/YYYY</code>.
	 * </p>
	 * 
	 * @return date range of the photos in the album
	 */
	public String getDateRange() {
		String output;
		Photo newest, oldest;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		if (photos.size() == 0)
			return "";

		if (photos.size() == 1) {
			newest = photos.get(0);
			output = dateFormat.format(newest.getDate().getTime());
			return output;
		}

		// find oldest photo
		oldest = photos.get(0);

		for (int i = 0; i < photos.size(); i++) {
			if (oldest.getDate().compareTo(photos.get(i).getDate()) > 0)
				oldest = photos.get(i);
		}

		// find newest photo
		newest = photos.get(0);

		for (int i = 0; i < photos.size(); i++) {
			if (newest.getDate().compareTo(photos.get(i).getDate()) < 0)
				newest = photos.get(i);
		}

		output = dateFormat.format(oldest.getDate().getTime()) + " - " + dateFormat.format(newest.getDate().getTime());

		return output;
	}

	/**
	 * Renames the album to the given name.
	 * 
	 * @param newName
	 *            new name for the album
	 */
	public void rename(String newName) {
		name = newName;
	}

	/**
	 * Adds given photo to the album.
	 * 
	 * <p>
	 * If the given photo is already in the album it will not be added and will
	 * return <code>false</code>.
	 * </p>
	 * 
	 * @param photo
	 *            photo to add to the album
	 * @return true if photo is added; false otherwise.
	 */
	public boolean addPhoto(Photo photo) {
		if (photos.contains(photo))
			return false;

		photos.add(photo);

		return true;
	}

	/**
	 * Removes given photo from the album.
	 * 
	 * <p>
	 * If the album doesn't have the photo, the album remain unchanged and returns
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param photo
	 *            photo to remove from the album
	 * @return true if album is removed; false otherwise.
	 */
	public boolean removePhoto(Photo photo) {
		return photos.remove(photo);
	}

	/**
	 * Compares this <code>Album</code> to the specified <code>Object</code>.
	 * 
	 * <p>
	 * The result is <code>true</code> if the argument is a <code>Album</code>
	 * object and has the same {@link #name name} as this object.
	 * </p>
	 * 
	 * @param o
	 *            object to compare
	 * @return true if this object is equal to o; false otherwise.
	 **/
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Album))
			return false;

		Album album = (Album) o;

		if (!name.equals(album.getName()))
			return false;

		return true;
	}

	/**
	 * Gets album's name.
	 * 
	 * @return album's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets album's photos.
	 * 
	 * @return album's photos
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	
	/**
	 * Prints the album's name
	 * @return album name
	 */
	public String toString(){
		return this.name;
	}
	
	
	
}
