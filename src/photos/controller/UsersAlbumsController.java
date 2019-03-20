package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

/**
 * Class controller to perform actions and display the information in the albums list screen
 * 
 * @author Nick Fasullo
 *
 */



public class UsersAlbumsController {
	
	
	/**
	 * button to enter user's inbox
	 */
	@FXML Button inbox;
	
	
	/**
	 * button to create a new album
	 */
	@FXML Button createAlbum;

	
	/**
	 * button to rename selected album
	 */
	@FXML Button renameAlbum;

	
	/**
	 * button to delete the selected album
	 */
	@FXML Button deleteAlbum;

	
	/**
	 * button to search on all photos
	 */
	@FXML Button search;

	
	/**
	 * button to logout user
	 */
	@FXML Button logout;

	
	/**
	 * table to display album information
	 */
	@FXML TableView<Album> table;

	
	/**
	 * first column, displays album name
	 */
	@FXML TableColumn<Album, String> nameColumn;

	
	/**
	 * second column, displays number of photos in the album
	 */
	@FXML TableColumn<Album, String> numPhotosColumn;

	
	/**
	 * third column, diplays the date range of the photos in the album
	 */
	@FXML TableColumn<Album, String> dateColumn;

	
	/**
	 * list containing all of the users albums, to display
	 */
	private ObservableList<Album> albumList;

	
	/**
	 * stage being displayed
	 */
	private Stage stage;

	
	/**
	 * list of all the users belonging to the program
	 */
	private List<User> userList;
	
	
	/**
	 * currently logged in user
	 */
	private User currUser;


 	
	/**
	 * starts the stage and sets each column up with the corresponding information
	 * @param mainStage current stage
	 * @param listOfUsers user list
	 * @param user current user
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user) {
		
		stage = mainStage;
		userList = listOfUsers;
		currUser = user;
		stage.setTitle(currUser.getUserName() + "'s Albums");

		albumList = FXCollections.observableList(user.getAlbums());

		table.setItems(albumList);
		table.getSortOrder().add(nameColumn);
		nameColumn.setSortable(true);

		nameColumn.setStyle("-fx-alignment: CENTER;");
		numPhotosColumn.setStyle("-fx-alignment: CENTER;");
		dateColumn.setStyle("-fx-alignment: CENTER;");

		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<Album, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Album, String> a) {
				return new ReadOnlyObjectWrapper<String>(a.getValue().getName());
			}
		});

		numPhotosColumn.setCellValueFactory(new Callback<CellDataFeatures<Album, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Album, String> a) {
				return new ReadOnlyObjectWrapper<String>("" + a.getValue().getPhotos().size());
			}
		});

		dateColumn.setCellValueFactory(new Callback<CellDataFeatures<Album, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Album, String> a) {
				return new ReadOnlyObjectWrapper<String>(a.getValue().getDateRange());
			}
		});
	

		enterAlbum();
	}

	
	/**
	 * create album button clicked, displays pop-up with a field for album name, cannot create duplicates
	 */
	public void createAlbum() {
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

		if (albumList.contains(newAlbum)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error creating album");
			alert.setHeaderText("Album already exists");
			alert.setContentText("The entered album name matches an album that already exists");

			alert.showAndWait();
			return;
		}

		albumList.add(newAlbum);
		table.sort();
	}

	
	/**
	 * displays pop-up to rename the selected album
	 */
	public void renameAlbum() {
		Album album;

		if (table.getSelectionModel().getSelectedIndex() == -1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error renaming album");
			alert.setHeaderText("No album selected");
			alert.setContentText("To rename a album, select the album and click the rename button");

			alert.showAndWait();
			return;
		}

		album = table.getSelectionModel().getSelectedItem();

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Rename Album");
		dialog.setHeaderText("Rename " + album.getName() + " with " + album.getPhotos().size() + " photos");
		dialog.setContentText("Enter the new name for the album:");

		Optional<String> output = dialog.showAndWait();

		if (!output.isPresent())
			return;

		if (output.get().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error renaming album");
			alert.setHeaderText("No album name was entered");
			alert.setContentText(
					"To rename a album: select the album, click the rename button, and enter a name for the album");

			alert.showAndWait();
			return;
		}

		Album newAlbum = new Album(output.get());

		if (albumList.contains(newAlbum)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error renaming album");
			alert.setHeaderText(newAlbum.getName() + " already exists");
			alert.setContentText("The entered new album name matches an album that already exists");

			alert.showAndWait();
			return;
		}

		album.rename(output.get());
		table.refresh();
		table.sort();
	}

	
	/**
	 * deletes the currently selected album
	 */
	public void deleteAlbum() {
		if (table.getSelectionModel().getSelectedIndex() == -1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error deleting album");
			alert.setHeaderText("No album selected");
			alert.setContentText("To delete an album, select the album and click the delete button");

			alert.showAndWait();
			return;
		}

		albumList.remove(table.getSelectionModel().getSelectedItem());
	}

	
	/**
	 * search button clicked, makes sure the user has albums and photos to search, will enter search screen, to search on all photos
	 * @throws IOException could not enter screen
	 */
	public void search() throws IOException {
		if (currUser.getAlbums().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error searching photos");
			alert.setHeaderText("No albums to search");
			alert.setContentText("You must have at least one album to search");

			alert.showAndWait();
			return;
		}

		boolean hasAtLeastOnePhoto = false;

		for (int i = 0; i < currUser.getAlbums().size(); i++) {
			if (currUser.getAlbums().get(i).getPhotos().size() > 0) {
				hasAtLeastOnePhoto = true;
				break;
			}
		}

		if (!hasAtLeastOnePhoto) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error searching photos");
			alert.setHeaderText("No photos to search");
			alert.setContentText("You must have at least one photo, in any of your albums, to search");

			alert.showAndWait();
			return;
		}

		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/search_screen.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SearchScreenController searchScreenController = loader.<SearchScreenController>getController();
		searchScreenController.start(stage, userList, currUser);

		Scene loginScene = new Scene(root, 640, 400);
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.show();
	}

	
	/**
	 * logs out the current user, returns to log in screen
	 * @param e logout button clicked
	 * @throws IOException could not logout
	 */
	public void logout(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/login.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		LoginController loginController = loader.<LoginController>getController();
		loginController.start(stage, userList);

		Scene loginScene = new Scene(root, 640, 400);
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.show();
	}


	
	/**
	 * makes the table click-able, double clicking an album will enter the album and diplay the album view screen
	 */
	private void enterAlbum() {

		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		        	//double click, enters album
		            if(mouseEvent.getClickCount() == 2){
		            	Album album = table.getSelectionModel().getSelectedItem();
		            	
		            	if(album == null)
		            		return;
		            	
		            	FXMLLoader loader = new FXMLLoader();
		            	AnchorPane root = null;
		            	FileInputStream inputStream = null;
						try { inputStream = new FileInputStream(new File("src/photos/view/SingleAlbum.fxml")); }
						catch (FileNotFoundException e) { e.printStackTrace(); }
						try { root = (AnchorPane) loader.load(inputStream); }
						catch (IOException e) { e.printStackTrace(); }
		    			SingleAlbumController singleAlbumController = loader.getController();
		    			singleAlbumController.start(stage, userList, currUser, album);
		    			Scene singleAlbumScene = new Scene(root, 640, 400);
		    			stage.setScene(singleAlbumScene);
		    			stage.sizeToScene();
		    			stage.show();
		    			return;		            	
		           } //ends double click
		        } //ends which type of click
		    } //ends handler
		}); //ends click events
    }
	
	
	
	/**
	 * enters users inbox and displays the stage to show and received photos from another user
	 * @param e inbox button clicked
	 */
	public void goToInbox(ActionEvent e){
		FXMLLoader loader = new FXMLLoader();
    	AnchorPane root = null;
    	FileInputStream inputStream = null;
		try { inputStream = new FileInputStream(new File("src/photos/view/inbox.fxml")); }
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		try { root = (AnchorPane) loader.load(inputStream); }
		catch (IOException ex) { ex.printStackTrace(); }
		InboxController inboxController = loader.getController();
		inboxController.start(stage, userList, currUser);
		Scene inboxScene = new Scene(root, 640, 400);
		stage.setScene(inboxScene);
		stage.sizeToScene();
		stage.show();
		return;		
	}
	
	
	
	
	

}
