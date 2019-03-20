package photos.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * Class controller used to operate the album view screen
 * 
 * @author Nick Fasullo
 *
 */


public class SingleAlbumController {

	/**
	 * tile pane to display the photos
	 */
	@FXML TilePane tileP;
	
	
	/**
	 * button to return to album list screen
	 */
	@FXML Button back;
	
	
	/**
	 * button to add a new photo to the album
	 */
	@FXML Button add;
	
	
	/**
	 * button to delete selected photo
	 */
	@FXML Button delete;
	
	
	/**
	 * button to copy the selected photo to another album
	 */
	@FXML Button copy;
	
	
	/**
	 * button to move the selected photo to another album
	 */
	@FXML Button move;
	
	
	/**
	 * button to send photo to another user
	 */
	@FXML Button send;
	
	
	/**
	 * button to log the user out
	 */
	@FXML Button logout;
	
	
	/**
	 * stage being displayed
	 */
	private Stage stage;
    
	
	/**
	 * list of all the users from the program
	 */
	private List<User> userList;
	
	
	/**
	 * current user that is logged in
	 */
	private User currUser;
	
	
	/**
	 * album the user is currently in
	 */
	private Album currAlbum;
	
	
	/**
	 * Selected photo
	 */
	private Photo currPic;
	
	
	
	/**
	 * set the stage title to album name, and dispays the photos in the album
	 * @param mainStage current stage
	 * @param listOfUsers user list
	 * @param user current user
	 * @param temp current album
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user, Album album) {
		userList = listOfUsers;
		currUser = user;
		currAlbum = album;
		stage = mainStage;
		stage.setTitle(currAlbum.getName());
		displayAlbum();	
	}

	public void sort() {
		ArrayList<String> choices = new ArrayList<String>();
		
		String alphaAscend = "Alphabetically - Ascending";
		choices.add(alphaAscend);
		String alphaDescend = "Alphabetically - Descending";
		choices.add(alphaDescend);
		String dateAscend = "Date - Newest to Oldest";
		choices.add(dateAscend);
		String dateDescend = "Date - Oldest to Newest";
		choices.add(dateDescend);
		
		ChoiceDialog<String> dialog = new ChoiceDialog<String>(alphaAscend, choices);
		dialog.setTitle("Sort Album");
		dialog.setHeaderText("Choose how to sort");
		dialog.setContentText("Sort by:");
		Optional<String> result = dialog.showAndWait();
		
		if(!result.isPresent())
			return;
		
		if(alphaAscend.equals(result.get())) {
			Collections.sort(currAlbum.getPhotos(), (a, b) -> a.getCaption().compareTo(b.getCaption()));
			displayAlbum();
			return;
		}
		
		if(alphaDescend.equals(result.get())){
			Collections.sort(currAlbum.getPhotos(), (b, a) -> a.getCaption().compareTo(b.getCaption()));
			displayAlbum();
			return;
		}
		
		if(dateAscend.equals(result.get())) {
			Collections.sort(currAlbum.getPhotos(), (a, b) -> a.getDate().compareTo(b.getDate()));
			displayAlbum();
			return;
		}

		if (dateDescend.equals(result.get())) {
			Collections.sort(currAlbum.getPhotos(), (b, a) -> a.getDate().compareTo(b.getDate()));
			displayAlbum();
			return;
		}
	}

	/**
	 * enters add new photo screen
	 * @param e add button clicked
	 * @throws IOException could not enter add photo scene
	 */
	public void addPhoto(ActionEvent e) throws IOException {                		
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/add_photo.fxml"));
		Parent root = (Parent) loader.load(inputStream);
		AddPhotoController photoController = loader.<AddPhotoController>getController();
		photoController.start(stage, currAlbum, userList, currUser);
		Scene addScene = new Scene(root, 640, 400);
		stage.setScene(addScene);
		stage.sizeToScene();
		stage.show();
	}
	
	
	
	/**
	 * helper method to create a thumb nail of the enter photo
	 * @param pic picture to resize
	 * @return thumb nail of the picture
	 */
	private ImageView createImageView(Photo pic) {
		final File imageFile = pic.getFile();
        ImageView imageView = null;
        try {   	
        	//thumb nail for picture
            final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
            imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setUserData(pic);
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        return imageView;
    }
	


	
	/**
	 * back button is clicked to exit the album and return to album list screen
	 * @param e back button clicked
	 * @throws IOException could not go back to album list screen
	 */
	public void goBack(ActionEvent e) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/users_albums.fxml"));
		AnchorPane root = (AnchorPane) loader.load(inputStream);
		UsersAlbumsController usersAlbumsController = loader.<UsersAlbumsController>getController();
		usersAlbumsController.start(stage, userList, currUser);
		Scene albumsScene = new Scene(root, 640, 400);
		stage.setScene(albumsScene);
		stage.sizeToScene();
		stage.show();
	}
	
	
	/**
	 * edit button clicked, takes the selected photo and its information to edit by entering the edit photo screen
	 * @throws IOException could not enter edit picture screen
	 */
	public void editPic() throws IOException {
		if (currPic == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Editing Photo");
			alert.setHeaderText("No photo selected");
			alert.setContentText("To edit a photo select it by clicking on it and then click the edit button");
			alert.showAndWait();
		} else {
			FXMLLoader loader = new FXMLLoader();
			FileInputStream inputStream = new FileInputStream(new File("src/photos/view/edit_photo.fxml"));
			Parent root = (Parent) loader.load(inputStream);

			EditPhotoController editPhotoController = loader.<EditPhotoController>getController();
			editPhotoController.start(stage, userList, currUser, currAlbum, currPic);

			Scene loginScene = new Scene(root, 640, 400);
			stage.setScene(loginScene);
			stage.sizeToScene();
			stage.show();
		}
	}

	
	/**
	 * search button clicked, enters the search screen for searching within a single album
	 * @throws IOException could not enter screen
	 */
	public void search() throws IOException {
		if (currAlbum.getPhotos().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error searching photos");
			alert.setHeaderText("No photos to search");
			alert.setContentText("You must have at least one photo in " + currAlbum.getName() + " to search");

			alert.showAndWait();
			return;
		}

		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/search_screen.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SearchScreenController searchScreenController = loader.<SearchScreenController>getController();
		searchScreenController.start(stage, userList, currUser, currAlbum);

		Scene loginScene = new Scene(root, 640, 400);
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.setTitle("Search For Photos");
		stage.show();
	}

	
	/**
	 * user clicks button and logs out of their account
	 * @param e logout button clicked
	 * @throws IOException could not logout
	 */
	public void userLogout(ActionEvent e) throws IOException {
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
	 * deleted the selected photo from the album
	 * @param e delete button clicked
	 */
	public void deletePic(ActionEvent e){
		if(currPic == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Deleting Photo");
			alert.setHeaderText("No photo selected");
			alert.showAndWait();
		}
		else{
			currAlbum.removePhoto(currPic);
			currPic = null;
			displayAlbum();
		}
	}
	
	
	/**
	 * takes each photo in the album and creates a thumb nail for it, and sets it as select-able, displays thumb nail and caption
	 * <p>
	 * double clicking a photo will bring up slide show view
	 */
	public void displayAlbum(){
		tileP.getChildren().clear();
		for(Photo pic: currAlbum.getPhotos()){
			ImageView imgV = createImageView(pic);
			VBox v = new VBox();
			Label cap = new Label(pic.getCaption());
			v.getChildren().addAll(imgV, cap);
			v.setAlignment(Pos.CENTER);
			tileP.getChildren().add(v);
			
			v.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    
                	for (Node n : tileP.getChildren()){
						n.setEffect(null);
					}
                	v.setEffect(new DropShadow(20, Color.RED));
					currPic = (Photo) imgV.getUserData();
					
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    	//double click, bring up enlarged image in slide show mode
                        if(mouseEvent.getClickCount() == 2){
                            	FXMLLoader loader = new FXMLLoader();
        		            	AnchorPane root = null;
        		            	FileInputStream inputStream = null;
        						try { inputStream = new FileInputStream(new File("src/photos/view/slide_show.fxml")); }
        						catch (FileNotFoundException e) { e.printStackTrace(); }
        						try { root = (AnchorPane) loader.load(inputStream); }
        						catch (IOException e) { e.printStackTrace(); }
        						Stage newStage = new Stage();
        						SlideShowController slideShowController = loader.getController();
        		    			try { slideShowController.start(newStage, currAlbum.getPhotos(), pic); }
        		    			catch (FileNotFoundException e) { e.printStackTrace(); }
        		    			Scene slideShowScene = new Scene(root, 640, 400);
        		    			newStage.setScene(slideShowScene);
        		    			newStage.setResizable(false);
        		    			newStage.sizeToScene();
        		    			newStage.show();
                       }
                    }
                }
            });
		}
	}
	
	
	
	/**
	 * displays pop-up with list of the users to send the slected photo to
	 * @param e send button clicked
	 */
	public void sendPhoto(ActionEvent e){
		if (currPic != null) {
			List<User> choices = new ArrayList<User>(userList);
			choices.remove(currUser);
			User admin = null;
			for(User u: choices){
				if(u.getUserName().equals("admin")){ admin = u; }
			}
			choices.remove(admin);
			ChoiceDialog<User> dialog = new ChoiceDialog<>(null, choices);
			dialog.setTitle("Send Photo");
			dialog.setHeaderText("Choose a user to send the photo to");
			Optional<User> result = dialog.showAndWait();
			User target = result.get();
			if(target.getInbox().getPhotos().contains(currPic)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Sending Photo");
				alert.setHeaderText("This user has already recieved this photo");
				alert.showAndWait();	
			}
			else{ target.getInbox().addPhoto(currPic); }
		}
		
		
		else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Sending Photo");
			alert.setHeaderText("No photo selected");
			alert.showAndWait();
		}
	
	
	}
	
	
	
	
	
	
	/**
	 * displays pop-up with a list of albums to copy the selected photo to, if it is already present in that album an error reported
	 * @param e copy button clicked
	 */
	public void copyPic(ActionEvent e){	
		if (currPic != null) {
			List<Album> choices = new ArrayList<Album>(currUser.getAlbums());
			choices.remove(currAlbum);
			
			if(choices.size() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Copying Photo");
				alert.setHeaderText("No albums to copy to");
				alert.setContentText("There are no other albums to copy the photo to");
				alert.showAndWait();
				
				return;
			}
			
			ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, choices);
			dialog.setTitle("Copy Photo");
			dialog.setHeaderText("Choose an album to copy the photo to");
			Optional<Album> result = dialog.showAndWait();
			
			if(!result.isPresent())
				return;
			
			Album target = result.get();
			if(target.getPhotos().contains(currPic)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Copying Photo");
				alert.setHeaderText("The photo already exists in this album");
				alert.showAndWait();
			}
			else{ target.getPhotos().add(currPic); }	
		}
		
		
		
		else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Copying Photo");
			alert.setHeaderText("No photo selected");
			alert.showAndWait();
		}
	}
	
	
	
	
	/**
	 * displays pop-up with list of albums to move the selected photo into, if the photo already exist in the album an error is reported
	 * @param e move button clicked
	 */
	public void movePic(ActionEvent e){
		if (currPic != null) {
			List<Album> choices = new ArrayList<Album>(currUser.getAlbums());
			choices.remove(currAlbum);
			
			if(choices.size() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Moving Photo");
				alert.setHeaderText("No albums to move to");
				alert.setContentText("There are no other albums to move the photo to");
				alert.showAndWait();
				
				return;
			}
			
			ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, choices);
			dialog.setTitle("Move Photo");
			dialog.setHeaderText("Choose an album to move the photo to");
			Optional<Album> result = dialog.showAndWait();
			
			if(!result.isPresent())
				return;
			
			Album target = result.get();
			if(target.getPhotos().contains(currPic)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Moving Photo");
				alert.setHeaderText("The photo already exists in this album");
				alert.showAndWait();	
			}
			else{
				target.getPhotos().add(currPic);
				currAlbum.removePhoto(currPic);
				currPic = null;
				displayAlbum();
				}	
			}
		
		
		
		else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Moving Photo");
			alert.setHeaderText("No photo selected");
			alert.showAndWait();
			}
		}
	

}
