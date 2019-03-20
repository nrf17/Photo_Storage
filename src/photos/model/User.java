package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * A class that represents a user in the Photos application.
 * 
 * @author Nick Fasullo
 *
 */
public class User implements Serializable{

	/**
	 * Value for Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The user's login username.
	 */
	private String userName;

	/**
	 * The user's login password.
	 */
	private String password;

	/**
	 * The structure that holds all of the user's {@link Album Album} objects.
	 */
	private ArrayList<Album> albums;
	
	/**
	 * inbox location for when another user send a photo to you
	 */
	private Album inbox;

	/**
	 * Creates a new <code>User</code> instance with the given username and
	 * password.
	 * 
	 * @param userName
	 *            username for user
	 * @param password
	 *            password for user
	 */
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;

		albums = new ArrayList<Album>();
		inbox = new Album("Inbox");
	}

	/**
	 * Adds given album to the user.
	 * 
	 * <p>
	 * If the user already has the given album it will not be added and will return
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param album
	 *            album to add to the user
	 * @return true if album is added; false otherwise.
	 */
	public boolean addAlbum(Album album) {
		if (albums.contains(album))
			return false;

		albums.add(album);

		return true;
	}

	/**
	 * Removes given album from the user.
	 * 
	 * <p>
	 * If the user doesn't have the album, the user's albums remain unchanged and
	 * returns <code>false</code>.
	 * </p>
	 * 
	 * @param album
	 *            album to remove from the user
	 * @return true if album is removed; false otherwise.
	 */
	public boolean removeAlbum(Album album) {
		return albums.remove(album);
	}

	/**
	 * Gets user's username.
	 * 
	 * @return user's username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Compares this <code>User</code> to the specified <code>Object</code>.
	 * 
	 * <p>
	 * The result is <code>true</code> if the argument is a <code>User</code> object
	 * and has the same {@link #userName userName} and {@link #password password} as
	 * this object.
	 * </p>
	 * 
	 * @param o
	 *            object to compare
	 * @return true if this object is equal to o; false otherwise.
	 **/
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User))
			return false;

		User user = (User) o;

		if (!userName.equals(user.getUserName()))
			return false;

		if (!password.equals(user.getPassword()))
			return false;

		return true;
	}

	/**
	 * Gets user's password.
	 * 
	 * @return user's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets user's albums.
	 * 
	 * @return user's albums
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}

	/**
	 * Gets the user's inbox album
	 * @return inbox album
	 */
	public Album getInbox(){
		return this.inbox;
	}
	
	
	
	/**
	 * Prints the user's username
	 * @return username
	 */
	public String toString(){
		return this.userName;
	}
	

}
