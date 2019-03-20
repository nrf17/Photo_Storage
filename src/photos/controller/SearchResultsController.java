package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

/**
 * Class controller to perform actions on the resulting search photos results
 * 
 * @author Nick Fasullo
 *
 */



public class SearchResultsController {

	
	/**
	 * tile pane to display the photos
	 */
	@FXML TilePane tileP;

	
	/**
	 * list of all the photos from the search
	 */
	private List<Photo> photos;

	
	/**
	 * list containing all the program's users
	 */
	private List<User> userList;

	
	/**
	 * current user who preformed the search
	 */
	private User user;

	
	/**
	 * current album being searched
	 */
	private Album album;

	
	/**
	 * stage to be displayed
	 */
	private Stage stage;

	
	/**
	 * starts the search results scene and display the resulting photos
	 * @param mainStage search results scene
	 * @param listOfUsers user list
	 * @param user current user
	 * @param photos resulting photo from the search
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user, List<Photo> photos) {
		stage = mainStage;
		stage.setTitle("Search Results");
		userList = listOfUsers;
		this.user = user;
		this.photos = photos;

		displayAlbum();
	}

	
	/**
	 * displays the search results from the album
	 * @param mainStage search results scene
	 * @param listOfUsers listOfUsers user list
	 * @param user current user
	 * @param album album from which the search was performed on
	 * @param photos resulting photo from the search
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user, Album album, List<Photo> photos) {
		stage = mainStage;
		userList = listOfUsers;
		this.user = user;
		this.album = album;
		this.photos = photos;

		displayAlbum();
	}

	
	
	/**
	 * back button clicked, go back to search screen
	 * @throws IOException could not return to previous search screen
	 */
	public void back() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/search_screen.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SearchScreenController searchScreenController = loader.<SearchScreenController>getController();
		if (album == null)
			searchScreenController.start(stage, userList, user);
		else
			searchScreenController.start(stage, userList, user, album);

		Scene loginScene = new Scene(root, 640, 400);
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.show();
	}

	
	
	/**
	 * create button clicked, create an album based off the results, can not have to albums with the same name
	 * <p>
	 * if a album was created from the results, entered into album view for the created album of search results
	 * @throws IOException could not create the album from the results
	 */
	public void createAlbum() throws IOException {
		Album newAlbum;

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New Album");
		dialog.setHeaderText("Create a new album");
		dialog.setContentText("Enter the name of the new album:");

		Optional<String> output = dialog.showAndWait();

		if (!output.isPresent())
			return;

		if (output.get().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error creating album");
			alert.setHeaderText("No album name was entered");
			alert.setContentText("To add a album, click the create button and enter a name for the album");

			alert.showAndWait();
			return;
		}

		newAlbum = new Album(output.get());

		if (user.getAlbums().contains(newAlbum)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error creating album");
			alert.setHeaderText("Album already exists");
			alert.setContentText("The entered album name matches an album that already exists");

			alert.showAndWait();
			return;
		}

		user.getAlbums().add(newAlbum);
		newAlbum.getPhotos().addAll(photos);

		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/SingleAlbum.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SingleAlbumController singleAlbumController = loader.<SingleAlbumController>getController();
		singleAlbumController.start(stage, userList, user, newAlbum);

		Scene albumScene = new Scene(root, 640, 400);
		stage.setScene(albumScene);
		stage.sizeToScene();
		stage.show();
	}

	
	/**
	 * for each photo in the results list, a thumb nail will be created and displayed
	 */
	private void displayAlbum() {
		tileP.getChildren().clear();
		for (Photo photo : photos) {
			ImageView imgV = createImageView(photo);
			VBox v = new VBox();
			Label cap = new Label(photo.getCaption());
			v.getChildren().addAll(imgV, cap);
			v.setAlignment(Pos.CENTER);
			tileP.getChildren().add(v);
		}
	}
	

	
	/**
	 * helper method to create the thumb nail of the picture
	 * @param photo picture to create a thumb nail of
	 * @return resulting thumb nail of the photo
	 */
	private ImageView createImageView(Photo photo) {
		final File imageFile = photo.getFile();
		ImageView imageView = null;
		try {
			final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
			imageView = new ImageView(image);
			imageView.setFitWidth(150);
			imageView.setUserData(photo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return imageView;
	}
}
