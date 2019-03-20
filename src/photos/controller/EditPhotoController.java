package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;

/**
 * Class controller to perform the actions in the edit photo scene
 * 
 * @author Nick Fasullo
 *
 */

public class EditPhotoController {
	
	
	/**
	 * V-box to display information
	 */
	@FXML VBox vBox;

	
	/**
	 * list to display the default tags options of the program
	 */
	private List<Pair<Text, TextField>> givenTags;

	
	/**
	 * list to display custom tags made by user
	 */
	private List<Pair<TextField, TextField>> customTags;

	
	/**
	 * text area to enter and edit the text
	 */
	private TextField captionField;

	
	/**
	 * current photo being edited
	 */
	private Photo photo;

	
	/**
	 * current user editing the photo
	 */
	private User user;

	
	/*
	 * current album the photo being edited is located in
	 */
	private Album album;

	
	/**
	 * list of all the users from the program
	 */
	private List<User> userList;

	
	/**
	 * stage that is displayed
	 */
	private Stage stage;

	
	/**
	 * starts the edit scene setting the titles and displaying text areas, allows more text areas to be created
	 * @param mainStage stage currently being shown
	 * @param listOfUsers program's user list
	 * @param user current user
	 * @param album current album
	 * @param photo current photo being edited
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user, Album album, Photo photo) {
		this.photo = photo;
		this.album = album;
		this.user = user;
		userList = listOfUsers;
		stage = mainStage;
		stage.setTitle("Edit Photo");

		givenTags = new ArrayList<Pair<Text, TextField>>();
		customTags = new ArrayList<Pair<TextField, TextField>>();

		for (int i = 0; i < photo.getTags().size(); i++) {
			Tag tag = photo.getTags().get(i);
			boolean newTagName = true;

			for (int j = 0; j < givenTags.size(); j++) {
				Text tagName = givenTags.get(j).getKey();
				TextField tagValue = givenTags.get(j).getValue();

				if (tag.getName().equals(tagName.getText())) {
					newTagName = false;

					tagValue.setText(tagValue.getText() + ", " + tag.getValue());

					break;
				}
			}

			if (newTagName) {
				Text tagName = new Text(tag.getName());
				TextField tagValue = new TextField(tag.getValue());
				givenTags.add(new Pair<Text, TextField>(tagName, tagValue));
			}
		}

		populate();
	}

	
	/**
	 * button clicked to save the changes made to the photo
	 * <p>
	 * each tag name can have multiple values in its value field, just comma separate the values
	 * <p>
	 * Example -> [Person] [Matt, Nick,Tom, Jason], will trim any leading and trailing spaces
	 * @throws IOException could not save the changes made to the photo
	 */
	public void save() throws IOException {
		if (!validCustomTags())
			return;

		photo.setCaption(captionField.getText());

		photo.getTags().clear();

		for (int i = 0; i < givenTags.size(); i++) {
			String tagName = givenTags.get(i).getKey().getText();
			String valueEntry = givenTags.get(i).getValue().getText().trim();

			if (!valueEntry.isEmpty()) {
				StringTokenizer tokenizer = new StringTokenizer(valueEntry, ",");

				while (tokenizer.hasMoreTokens())
					photo.getTags().add(new Tag(tagName, tokenizer.nextToken().trim().toLowerCase()));
			}
		}

		for (int i = 0; i < customTags.size(); i++) {
			String tagName = customTags.get(i).getKey().getText().trim().toLowerCase();
			String valueEntry = customTags.get(i).getValue().getText().trim();

			if (!tagName.isEmpty() && !valueEntry.isEmpty()) {
				StringTokenizer tokenizer = new StringTokenizer(valueEntry, ",");

				while (tokenizer.hasMoreTokens())
					photo.getTags().add(new Tag(tagName, tokenizer.nextToken().trim().toLowerCase()));
			}
		}

		Collections.sort(photo.getTags(), (a, b) -> a.getName().compareTo(b.getName()));
		
		back();
	}

	
	/**
	 * checks and creates new instances of tag names
	 * @return true if this new tag type can be created, false if not
	 */
	private boolean validCustomTags() {
		for (int i = 0; i < customTags.size(); i++) {
			Pair<TextField, TextField> tagInput = customTags.get(i);
			String name = tagInput.getKey().getText().trim();
			String value = tagInput.getValue().getText().trim();

			if ((!name.isEmpty() && value.isEmpty())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error searching");
				alert.setHeaderText("The \"" + name + "\" tag has no value");
				alert.setContentText("The entered custom tag needs a corresponding value");

				alert.showAndWait();
				return false;
			}

			if ((!value.isEmpty()) && name.isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error searching");
				alert.setHeaderText("The \"" + value + "\" tag(s) have no type");
				alert.setContentText("The entered custom tag needs a corresponding type");

				alert.showAndWait();
				return false;
			}
		}

		return true;
	}

	
	/**
	 * back button is clicked and loads back into the album view, no changes will be saved
	 * @throws IOException could not return album scene
	 */
	public void back() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/SingleAlbum.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SingleAlbumController albumController = loader.<SingleAlbumController>getController();
		albumController.start(stage, userList, user, album);

		Scene albumScene = new Scene(root, 640, 400);
		stage.setScene(albumScene);
		stage.sizeToScene();
		stage.show();
	}

	
	/**
	 * populates the scene, setting the image, caption, tags, and area for new tags to be made
	 */
	private void populate() {
		vBox.setAlignment(Pos.TOP_CENTER);

		Label mainLabel = new Label("Edit Photo");
		mainLabel.setFont(new Font(18));
		mainLabel.setPadding(new Insets(10, 0, 10, 0));

		vBox.getChildren().add(mainLabel);

		File imageFile = photo.getFile();
		ImageView imageView;
		try {
			Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
			imageView = new ImageView(image);
			imageView.setFitWidth(150);

			vBox.getChildren().add(imageView);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20, 0, 15, 0));
		grid.setHgap(10);
		grid.setVgap(10);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(30);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(15);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(25);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(30);
		grid.getColumnConstraints().addAll(col1, col2, col3, col4);

		Text captionPrompt = new Text("Caption:");
		grid.add(captionPrompt, 1, 0);

		captionField = new TextField(photo.getCaption());
		grid.add(captionField, 2, 0);

		Text tagText = new Text("Tags");
		tagText.setFont(Font.font(null, FontWeight.BOLD, 12));
		grid.add(tagText, 1, 1);
		
		Text tagHelpText = new Text("Commas separate multiple tag values");
		tagHelpText.setFont(Font.font(null, FontWeight.THIN, 9));
		grid.add(tagHelpText, 2, 1);

		int currentRow = 2;
		for (int i = 0; i < givenTags.size(); i++) {
			grid.add(givenTags.get(i).getKey(), 1, currentRow);
			grid.add(givenTags.get(i).getValue(), 2, currentRow);
			currentRow++;
		}

		Button addTag = new Button("Add Tag");

		EventHandler<ActionEvent> addTagTextFields = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextField newName = new TextField();
				TextField newValue = new TextField();

				int indexToAddNextTag = GridPane.getRowIndex(addTag);
				grid.getChildren().remove(addTag);

				grid.add(newName, 1, indexToAddNextTag);
				grid.add(newValue, 2, indexToAddNextTag);

				Pair<TextField, TextField> newTagInput = new Pair<TextField, TextField>(newName, newValue);
				customTags.add(newTagInput);

				grid.add(addTag, 1, indexToAddNextTag + 1);
			}
		};

		addTag.setOnAction(addTagTextFields);

		grid.add(addTag, 1, currentRow + 1);

		vBox.getChildren().add(grid);
	}
}
