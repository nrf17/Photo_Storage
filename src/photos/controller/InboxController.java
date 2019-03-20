package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
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
 * Class controller for the extra credit feature, handles action peformed in the inbox area of recieved photos
 * 
 * @author Nick Fasullo
 *
 */


public class InboxController {

	
	/**
	 * tile pane to display the photos
	 */
	@FXML TilePane tileP;
	
	
	/**
	 * button to return back to the list of albums
	 */
	@FXML Button back;
	
	
	/**
	 * button to delete a photo in the inbox
	 */
	@FXML Button delete;
	
	
	/**
	 * button to copy the photo to one of the user's albums
	 */
	@FXML Button copy;
	
	
	/**
	 * button to move a photo in the inbox to one one of the user's albums
	 */
	@FXML Button move;
	
	
	/**
	 * stage that will be displayed
	 */
	private Stage stage;
    
	
	/**
	 * list of all the users in the program
	 */
	private List<User> userList;
	
	
	/**
	 * user currently logged in
	 */
	private User currUser;
	
	
	/**
	 * photo currently selected
	 */
	private Photo currPic;
	
	
	
	/**
	 * sets the stage title and will display the photos currently in the user's inbox
	 * @param mainStage inbox scene currently being shown
	 * @param listOfUsers user list
	 * @param user current user
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user){
		userList = listOfUsers;
		currUser = user;
		stage = mainStage;
		stage.setTitle("Inbox");
		displayAlbum();
	}
	
	
	
	/**
	 * creates the thumbnail for the photo
	 * @param pic photo to create a image of
	 * @return reduced image of photo
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
	 * displays all the photos with their captions in the album
	 * <p>
	 * casts a shadow around the selected photo, if photo is double clicked, brings up slide show view
	 */
	public void displayAlbum(){
		tileP.getChildren().clear();
		for(Photo pic: currUser.getInbox().getPhotos()){
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
        		    			try { slideShowController.start(newStage, currUser.getInbox().getPhotos(), pic); }
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
	 * returns the user back to their albums screen
	 * @param e back button clicked
	 * @throws IOException could not return to user's albums screen
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
	 * displays pop-up asking user to select the album they wish to copy the selected photo to
	 * @param e copy button clicked
	 */
	public void copyTo(ActionEvent e){	
		if (currPic != null) {
			List<Album> choices = new ArrayList<Album>(currUser.getAlbums());
			ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, choices);
			dialog.setTitle("Copy Photo");
			dialog.setHeaderText("Choose an album to copy the photo to");
			
			if (choices.size() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Copying Photo");
				alert.setHeaderText("No albums to copy to");
				alert.setContentText("There are no albums to copy the photo to, create an album and try again");
				alert.showAndWait();

				return;
			}
			
			Optional<Album> result = dialog.showAndWait();
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
	 * deletes the selected photo from the inbox
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
			currUser.getInbox().removePhoto(currPic);
			currPic = null;
			displayAlbum();
		}
	}
	
	
	/**
	 * displays pop-up with the choice to take the selected photo and move it into another album
	 * @param e move button clicked
	 */
	public void movePic(ActionEvent e){
		if (currPic != null) {
			List<Album> choices = new ArrayList<Album>(currUser.getAlbums());
			ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, choices);
			
			if (choices.size() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Moving Photo");
				alert.setHeaderText("No albums to move to");
				alert.setContentText("There are no albums to move the photo to, create an album and try again");
				alert.showAndWait();

				return;
			}
			
			dialog.setTitle("Move Photo");
			dialog.setHeaderText("Choose an album to move the photo to");
			Optional<Album> result = dialog.showAndWait();
			Album target = result.get();
			if(target.getPhotos().contains(currPic)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Moving Photo");
				alert.setHeaderText("The photo already exists in this album");
				alert.showAndWait();	
			}
			else{
				target.getPhotos().add(currPic);
				currUser.getInbox().removePhoto(currPic);
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
