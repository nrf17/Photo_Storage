package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;


/**
 * Class controller to facilitate the actions performed in the add photo scene
 * 
 * @author Nick Fasullo
 *
 */



public class AddPhotoController {
	
	/**
	 * button to return to the album view scene
	 */
	@FXML Button back;
	
	
	/**
	 * button to complete the action of creating the new photo instance
	 */
	@FXML Button confirm;
	
	
	/**
	 * button to open up and selected a photo from file explorer
	 */
	@FXML Button browse;
	
	
	/**
	 * button to create a tag from the text fields and add it to the tag list
	 */
	@FXML Button add;
	
	
	/**
	 * button to delete the selected tag from the tag list
	 */
	@FXML Button delete;
	
	
	/**
	 * area where the user can see the file path of the photo they just chose, non-editable
	 */
	@FXML TextField filePath;
	
	
	/**
	 * area where the orginal photo name is displayed, is editable to chane the caption
	 */
	@FXML TextField caption;
	
	
	/**
	 * list that will display all the added tags in Name: Value form
	 */
	@FXML ListView<Tag> listView;
	
	
	/**
	 * area where user can enter the tag name for creating the name value tag pair
	 */
	@FXML TextField tagName;
	
	
	/**
	 * area where user can enter the tag value for creating the name value tag pair
	 */
	@FXML TextField tagVal;
	
	
	/**
	 * the observable list where all the tags will be stored to display in the list view
	 */
	private ObservableList<Tag> tagList = FXCollections.observableArrayList();
	
	
	/**
	 * the primary stage
	 */
	private Stage stage;
	
	
	/**
	 * the album that the photo is currently trying to be added to
	 */
	private Album currAlbum;
	
	
	/**
	 * list of all the users in the program
	 */
	private List<User> users;
	
	
	/**
	 * the user currently logged in
	 */
	private User currUser;
	
	
	/**
	 * the tag that is currently selected
	 */
	private Tag currTag = null;
	
	
	/**
	 * the file from the currently selected photo
	 */
	private File currFile = null;
	
	
	/**
	 * set the title of the stage
	 * <p>
	 * sets currently selected tag from the list, and fills in the text field with the tags info
	 * @param inStage add photo stage
	 * @param album current album
	 * @param listOfUsers user list
	 * @param user current user
	 */
	public void start(Stage inStage, Album album, List<User> listOfUsers, User user){
		stage = inStage;
		stage.setTitle("Add Photo");
		currAlbum = album;
		users = listOfUsers;
		currUser = user;
		
		listView.setOnMouseClicked((MouseEvent e) -> {
			currTag = listView.getSelectionModel().getSelectedItem();
			
			if(currTag == null)
				return;
			
			tagName.setText(currTag.getName());
			tagVal.setText(currTag.getValue());
		});
		
	}
	
	
	/**
	 * opens file explorer to select a photo and will check if that photo is already in the album
	 * <p>
	 * sets the file path and caption name, which can be re-named
	 * @param e browse button clicked
	 * @throws FileNotFoundException error could not perform the action
	 */
	public void openFiles(ActionEvent e) throws FileNotFoundException{
		FileChooser fc = new FileChooser();
		File sF = fc.showOpenDialog(null);
		if(sF != null){			
			Photo pic = new Photo(sF, " ");
			if(!currAlbum.getPhotos().contains(pic)){
				int pos = sF.getName().lastIndexOf(".", sF.getName().length()-1);
				String cap = sF.getName().substring(0, pos);
				filePath.setText(sF.getAbsolutePath());
				caption.setText(cap);
				tagList.clear();
				listView.setItems(tagList);
				tagName.clear();
				tagVal.clear();
				currTag = null;
				currFile = sF;
			}
			else{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Making Photo");
				alert.setHeaderText("This photo already exists in the album");
				alert.showAndWait();
			}
		}
	}
	
	
	/**
	 * back button is clicked to return back to the album view stage, no photo is added to the album
	 * @param e back button click
	 * @throws IOException could not load back into album screen
	 */
	public void goBack(ActionEvent e) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/SingleAlbum.fxml"));
		Parent root = (Parent) loader.load(inputStream);
		SingleAlbumController albumController = loader.<SingleAlbumController>getController();
		albumController.start(stage, users, currUser, currAlbum);
		Scene albumScene = new Scene(root, 640, 400);
		stage.setScene(albumScene);
		stage.sizeToScene();
		stage.show();
	}
	
	
	
	
	/**
	 * get the info from the tag name and value fields and checks if correct requirements are meant
	 * <p>
	 * checks to make sure tag is not a duplicate, if not it will create the tag add it to the list and clear the fields
	 * @param e add button clicked
	 */
	public void addTag(ActionEvent e){
		String name = tagName.getText().toLowerCase();
		String value = tagVal.getText().toLowerCase();
		if(name.length() < 1 || value.length() < 1){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Creating Tag");
			alert.setHeaderText("Must have both a name and value");
			alert.showAndWait();
		}

		else{
			Tag ins = new Tag(name, value);
			if(tagList.contains(ins)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Creating Tag");
				alert.setHeaderText("This tag already exists");
				alert.showAndWait();
			}else{
				tagList.add(ins);
				listView.setItems(tagList);
				tagName.clear();
				tagVal.clear();
				listView.getSelectionModel().select(ins);
				currTag = null;
			}
		}

		
	}
		
		
	
	
	/**
	 * deletes selected tag from the list, if no tag is selected reports error
	 * @param e delete button clicked
	 */
	public void deleteTag(ActionEvent e){
		if(currTag == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Deleting Tag");
			alert.setHeaderText("Select a tag");
			alert.showAndWait();
		}
		
		else{
			tagList.remove(currTag);
			listView.setItems(tagList);
			tagName.clear();
			tagVal.clear();
			if(tagList.size() > 0){ listView.getSelectionModel().select(0); }
			currTag = null;
		}
	
	}
	
	
	
	
	
	/**
	 * creates a photo, makes sure a photo is selected and has a name, if tags were created they are added to the photo, then returns to album stage
	 * @param e confirm button clicked
	 * @throws FileNotFoundException could not create photo
	 */
	public void makePhoto(ActionEvent e) throws FileNotFoundException{
		if(filePath.getText().length() <= 0 || caption.getText().length() <= 0){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Creating Photo");
			alert.setHeaderText("Must select and name the photo");
			alert.showAndWait();
			return;
		}
		
		if(tagList.size() > 0){
			ArrayList<Tag> tags = new ArrayList<Tag>();
			for(Tag t: tagList){ tags.add(t); }
			Photo newPic = new Photo(currFile, caption.getText(), tags);
			Collections.sort(newPic.getTags(), (a, b) -> a.getName().compareTo(b.getName()));
			currAlbum.addPhoto(newPic);
		}
		else{
			Photo newPic = new Photo(currFile, caption.getText());
			currAlbum.addPhoto(newPic);
		}
		
		
    	FXMLLoader loader = new FXMLLoader();
    	AnchorPane root = null;
    	FileInputStream inputStream = null;
		try { inputStream = new FileInputStream(new File("src/photos/view/SingleAlbum.fxml")); }
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		try { root = (AnchorPane) loader.load(inputStream); }
		catch (IOException ex) { ex.printStackTrace(); }
		SingleAlbumController singleAlbumController = loader.getController();
		singleAlbumController.start(stage, users, currUser, currAlbum);
		Scene singleAlbumScene = new Scene(root, 640, 400);
		stage.setScene(singleAlbumScene);
		stage.sizeToScene();
		stage.show();
	}
	
	
	
	
	
}
