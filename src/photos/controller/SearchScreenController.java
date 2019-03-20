package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import photos.model.Album;
import photos.model.User;
import photos.model.Photo;
import photos.model.Tag;


/**
 * Class controller to perform a search on photos
 * 
 * @author Nick Fasullo
 *
 */



public class SearchScreenController {

	/**
	 * Vbox to display information
	 */
	@FXML VBox vBox;

	
	/**
	 * check box to choose to search by a range date
	 */
	private CheckBox dateRangeCheckBox;

	
	/**
	 * drop down to select a single to search on
	 */
	private DatePicker singleDatePicker;

	
	/**
	 * drop down to select the beggining date to search in
	 */
	private DatePicker startDatePicker;

	
	/**
	 * drop down to select the ending date to search in
	 */
	private DatePicker endDatePicker;

	
	/**
	 * area to enter value for default tag of Person
	 */
	private TextField personValue;

	
	/**
	 * area to enter value for default tag of Location
	 */
	private TextField locationValue;

	
	/**
	 * list to add the correct matching photos from the search 
	 */
	private List<Photo> photosToSearch;

	
	/**
	 * list of the program's users
	 */
	private List<User> userList;

	
	/**
	 * current user doing the search
	 */
	private User user;

	
	/**
	 * album to search in
	 */
	private Album album;

	
	/**
	 * stage to be displayed
	 */
	private Stage stage;

	
	/**
	 * combo of name value text areas for custom tag
	 */
	private List<Pair<TextField, TextField>> customTags;

	
	/**
	 * display and generates information to search on
	 * @param mainStage stage currently being displayed
	 * @param listOfUsers user list
	 * @param user current user
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user) {
		stage = mainStage;
		stage.setTitle("Search For Photos");
		userList = listOfUsers;
		this.user = user;

		photosToSearch = new ArrayList<Photo>();

		for (int i = 0; i < user.getAlbums().size(); i++) {
			Album albumToAdd = user.getAlbums().get(i);
			for (int j = 0; j < albumToAdd.getPhotos().size(); j++) {
				Photo photoToAdd = albumToAdd.getPhotos().get(j);
				if (!photosToSearch.contains(photoToAdd))
					photosToSearch.add(photoToAdd);
			}
		}

		customTags = new ArrayList<Pair<TextField, TextField>>();

		populate();
	}

	
	
	/**
	 * creates and displays the information to search on
	 * @param mainStage current stage
	 * @param listOfUsers user list
	 * @param user current user
	 * @param album album being search on
	 */
	public void start(Stage mainStage, List<User> listOfUsers, User user, Album album) {
		stage = mainStage;
		userList = listOfUsers;
		this.user = user;
		this.album = album;

		photosToSearch = album.getPhotos();

		customTags = new ArrayList<Pair<TextField, TextField>>();

		populate();
	}

	
	
	/**
	 * search button clicked, will gather the information enter and perform a search based on the criteria
	 * <p>
	 *  each tag name can have multiple values in its value field, just comma separate the values
	 *  <p>
	 *  Example -> [Person] [Matt, Nick,Tom, Jason], will trim any leading and trailing spaces
	 *  <p>
	 * after search is performed and the results are gathered, search results screen is displayed
	 * @throws IOException could not perform the search
	 */
	public void search() throws IOException {
		List<Photo> dateSearchResults, tagSearchResults, searchResults;
		ArrayList<Tag> tagsToSearch;
		LocalDate startDate, endDate, singleDate, photoDate;

		if (!validCustomTags())
			return;

		if (noSearchCriteria()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error searching");
			alert.setHeaderText("No conditions to search");
			alert.setContentText("There is no entered search criteria. A search condition is needed to search.");

			alert.showAndWait();
			return;
		}

		dateSearchResults = new ArrayList<Photo>();

		startDate = startDatePicker.getValue();
		endDate = endDatePicker.getValue();
		singleDate = singleDatePicker.getValue();

		if (dateRangeCheckBox.isSelected()) {
			if ((startDate != null) && (endDate != null)) {
				for (int i = 0; i < photosToSearch.size(); i++) {
					Calendar photoCalendar = photosToSearch.get(i).getDate();

					photoDate = photoCalendar.toInstant().atZone(photoCalendar.getTimeZone().toZoneId()).toLocalDate();

					if (photoDate.isAfter(startDate) && photoDate.isBefore(endDate))
						dateSearchResults.add(photosToSearch.get(i));
				}
			} else {
				dateSearchResults = new ArrayList<Photo>(photosToSearch);
			}
		} else if (singleDate != null) {
			for (int i = 0; i < photosToSearch.size(); i++) {
				Calendar photoCalendar = photosToSearch.get(i).getDate();

				photoDate = photoCalendar.toInstant().atZone(photoCalendar.getTimeZone().toZoneId()).toLocalDate();

				if (photoDate.isEqual(singleDate))
					dateSearchResults.add(photosToSearch.get(i));
			}
		} else
			dateSearchResults = new ArrayList<Photo>(photosToSearch);

		tagSearchResults = new ArrayList<Photo>();
		tagsToSearch = new ArrayList<Tag>();

		if (!personValue.getText().isEmpty()) {
			StringTokenizer tokenizer = new StringTokenizer(personValue.getText().trim(), ",");

			while (tokenizer.hasMoreTokens())
				tagsToSearch.add(new Tag("person", tokenizer.nextToken().trim().toLowerCase()));
		}

		if (!locationValue.getText().isEmpty()) {
			StringTokenizer tokenizer = new StringTokenizer(locationValue.getText().trim(), ",");

			while (tokenizer.hasMoreTokens())
				tagsToSearch.add(new Tag("location", tokenizer.nextToken().trim().toLowerCase()));
		}

		for (int i = 0; i < customTags.size(); i++) {
			Pair<TextField, TextField> tagInput = customTags.get(i);
			String name = tagInput.getKey().getText();
			String value = tagInput.getValue().getText();

			if ((!name.isEmpty()) && (!value.isEmpty())) {
				StringTokenizer tokenizer = new StringTokenizer(value, ",");

				while (tokenizer.hasMoreTokens())
					tagsToSearch.add(new Tag(name.trim().toLowerCase(), tokenizer.nextToken().trim().toLowerCase()));
			}
		}

		if (tagsToSearch.size() != 0) {
			for (int i = 0; i < photosToSearch.size(); i++) {
				Photo photo = photosToSearch.get(i);

				for (int j = 0; j < tagsToSearch.size(); j++) {
					if (photo.getTags().contains(tagsToSearch.get(j))) {
						tagSearchResults.add(photo);
						break;
					}
				}
			}
		} else {
			tagSearchResults = new ArrayList<Photo>(photosToSearch);
		}

		// searchResults is the intersection of tagSearchResults and dateSearchResults
		searchResults = new ArrayList<Photo>(dateSearchResults);
		searchResults.retainAll(tagSearchResults);

		if (searchResults.size() == 0) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Search complete");
			alert.setHeaderText("No photos found");

			if (photosToSearch.size() > 1)
				alert.setContentText(
						"None of the " + photosToSearch.size() + " photos searched match the search criteria");
			else
				alert.setContentText("The one photo searched doesn't match the search criteria");

			alert.showAndWait();
			return;
		}

		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/search_results.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		SearchResultsController searchResultsController = loader.<SearchResultsController>getController();

		if (album == null)
			searchResultsController.start(stage, userList, user, searchResults);
		else
			searchResultsController.start(stage, userList, user, album, searchResults);

		Scene resultsScene = new Scene(root, 640, 400);
		stage.setScene(resultsScene);
		stage.sizeToScene();
		stage.show();
	}

	
	/**
	 * will determine if the user can search based on the given information
	 * @return true if able to search by the entered information, false if not
	 */
	private boolean noSearchCriteria() {
		if (dateRangeCheckBox.isSelected()) {
			if ((startDatePicker.getValue() != null) || (endDatePicker.getValue() != null))
				return false;
		} else {
			if (singleDatePicker.getValue() != null)
				return false;
		}

		if (!personValue.getText().isEmpty())
			return false;

		if (!locationValue.getText().isEmpty())
			return false;

		for (int i = 0; i < customTags.size(); i++) {
			Pair<TextField, TextField> tagInput = customTags.get(i);
			String name = tagInput.getKey().getText();
			String value = tagInput.getValue().getText();

			if ((!name.isEmpty()) && (!value.isEmpty()))
				return false;
		}

		return true;
	}

	
	/**
	 * 
	 * @return true if the custom tag can be search, false if not
	 */
	private boolean validCustomTags() {
		for (int i = 0; i < customTags.size(); i++) {
			Pair<TextField, TextField> tagInput = customTags.get(i);
			String name = tagInput.getKey().getText();
			String value = tagInput.getValue().getText();

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
				alert.setHeaderText("The \"" + value + "\" tag has no type");
				alert.setContentText("The entered custom tag needs a corresponding type");

				alert.showAndWait();
				return false;
			}
		}

		return true;
	}

	
	/**
	 * back button clicked, returns back to according album view or user's album screen
	 * @throws IOException cant return to album view screen
	 */
	public void back() throws IOException {
		if (album == null) {
			// go back to all of users albums
			FXMLLoader loader = new FXMLLoader();
			FileInputStream inputStream = new FileInputStream(new File("src/photos/view/users_albums.fxml"));
			Parent root = (Parent) loader.load(inputStream);

			UsersAlbumsController usersAlbumsController = loader.<UsersAlbumsController>getController();
			usersAlbumsController.start(stage, userList, user);

			Scene albumScene = new Scene(root, 640, 400);
			stage.setScene(albumScene);
			stage.sizeToScene();
			stage.show();
		} else {
			// go back to individual album
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
	}

	
	
	/**
	 * will create and display all the credentials in the search screen
	 */
	private void populate() {
		vBox.setAlignment(Pos.TOP_CENTER);

		Label mainLabel = new Label();
		mainLabel.setFont(new Font(18));
		mainLabel.setPadding(new Insets(15, 0, 10, 0));

		if (album == null) {
			if (photosToSearch.size() != 1)
				mainLabel.setText("Search all of your " + photosToSearch.size() + " photos");
			else
				mainLabel.setText("Search your 1 photo");
		} else {
			mainLabel.setText("Search " + album.getName() + " with " + photosToSearch.size() + " photo");

			if (photosToSearch.size() != 1)
				mainLabel.setText(mainLabel.getText() + "s");
		}

		vBox.getChildren().add(mainLabel);

		GridPane dateGrid = new GridPane();
		dateGrid.setPadding(new Insets(10, 0, 10, 0));
		dateGrid.setHgap(10);
		dateGrid.setVgap(10);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(30);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(15);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(25);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(30);
		dateGrid.getColumnConstraints().addAll(col1, col2, col3, col4);

		Text dateText = new Text("Search by Date");
		dateText.setFont(Font.font(null, FontWeight.BOLD, 12));
		dateGrid.add(dateText, 1, 0);

		dateRangeCheckBox = new CheckBox("Date range");
		GridPane.setHalignment(dateRangeCheckBox, HPos.RIGHT);
		dateGrid.add(dateRangeCheckBox, 2, 0);

		Text singleDatePrompt = new Text("Date:");
		dateGrid.add(singleDatePrompt, 1, 1);

		singleDatePicker = new DatePicker();
		dateGrid.add(singleDatePicker, 2, 1);

		Text startDatePrompt = new Text("Start date:");
		startDatePicker = new DatePicker();
		Text endDatePrompt = new Text("End date:");
		endDatePicker = new DatePicker();

		EventHandler<ActionEvent> dateRangeEvent = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (dateRangeCheckBox.isSelected()) {
					dateGrid.getChildren().remove(singleDatePrompt);
					dateGrid.getChildren().remove(singleDatePicker);

					dateGrid.add(startDatePrompt, 1, 1);
					dateGrid.add(startDatePicker, 2, 1);
					dateGrid.add(endDatePrompt, 1, 2);
					dateGrid.add(endDatePicker, 2, 2);
				} else {
					dateGrid.getChildren().remove(startDatePrompt);
					dateGrid.getChildren().remove(startDatePicker);
					dateGrid.getChildren().remove(endDatePrompt);
					dateGrid.getChildren().remove(endDatePicker);

					dateGrid.add(singleDatePrompt, 1, 1);
					dateGrid.add(singleDatePicker, 2, 1);
				}
			}
		};

		dateRangeCheckBox.setOnAction(dateRangeEvent);

		vBox.getChildren().add(dateGrid);

		GridPane tagGrid = new GridPane();
		tagGrid.setPadding(new Insets(10, 0, 10, 0));
		tagGrid.setHgap(10);
		tagGrid.setVgap(10);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(30);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(15);
		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(25);
		ColumnConstraints column4 = new ColumnConstraints();
		column4.setPercentWidth(30);
		tagGrid.getColumnConstraints().addAll(column1, column2, column3, column4);

		Text tagText = new Text("Search by Tags");
		tagText.setFont(Font.font(null, FontWeight.BOLD, 12));
		tagGrid.add(tagText, 1, 0);

		Text personText = new Text("Person:");
		tagGrid.add(personText, 1, 1);

		Text locationText = new Text("Location:");
		tagGrid.add(locationText, 1, 2);

		personValue = new TextField();
		personValue.setPromptText("alice, bob");
		tagGrid.add(personValue, 2, 1);

		locationValue = new TextField();
		locationValue.setPromptText("usa, colorado");
		tagGrid.add(locationValue, 2, 2);

		Button addTag = new Button("Add Tag");

		EventHandler<ActionEvent> addTagTextFields = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextField newName = new TextField();
				TextField newValue = new TextField();

				int indexToAddNextTag = GridPane.getRowIndex(addTag);
				tagGrid.getChildren().remove(addTag);

				tagGrid.add(newName, 1, indexToAddNextTag);
				tagGrid.add(newValue, 2, indexToAddNextTag);

				Pair<TextField, TextField> newTagInput = new Pair<TextField, TextField>(newName, newValue);
				customTags.add(newTagInput);

				tagGrid.add(addTag, 1, indexToAddNextTag + 1);
			}
		};

		addTag.setOnAction(addTagTextFields);

		tagGrid.add(addTag, 1, 3);

		vBox.getChildren().add(tagGrid);
	}
}
