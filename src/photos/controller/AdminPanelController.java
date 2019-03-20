package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import photos.model.User;

/**
 * Class controller for the admin panel
 * 
 * @author Nick Fasullo
 *
 */



public class AdminPanelController {

	/**
	 * button to create a new user in the program
	 */
	@FXML Button createUser;

	
	/**
	 * button to delete a user from the program
	 */
	@FXML Button deleteUser;

	
	/**
	 * button to logout of admin user
	 */
	@FXML Button logout;

	
	/**
	 * table to display information for each user
	 */
	@FXML TableView<User> table;

	
	/**
	 * first column of the table, displays user name for each user
	 */
	@FXML TableColumn<User, String> userNameColumn;

	
	/**
	 * second column of the table, displays password for the corresponding user name
	 */
	@FXML TableColumn<User, String> passwordColumn;

	
	/**
	 * list to store and display all the users' information
	 */
	private ObservableList<User> userList;

	/**
	 * stage that is being shown
	 */
	private Stage stage;

	
	/**
	 * starts the admin scene, displaying all the information into the table and setting information as click-able
	 * @param mainStage current stage being shown
	 * @param listOfUsers list of all the users in the program
	 */
	public void start(Stage mainStage, List<User> listOfUsers) {
		stage = mainStage;
		stage.setTitle("Admin Screen");

		userList = FXCollections.observableList(listOfUsers);

		table.setItems(userList);
		table.getSortOrder().add(userNameColumn);
		userNameColumn.setSortable(true);

		userNameColumn.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> u) {
				return new ReadOnlyObjectWrapper<String>(u.getValue().getUserName());
			}
		});

		passwordColumn.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> u) {
				return new ReadOnlyObjectWrapper<String>(u.getValue().getPassword());
			}
		});
	}

	
	/**
	 * add user button is clicked, displays pop-up to enter user information and checks to make sure there is not a duplicate, then updates the list
	 */
	public void addUser() {
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle("Add User");
		dialog.setHeaderText("Enter the new user's details");

		ButtonType buttonType = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonType);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));

		TextField username = new TextField();
		PasswordField password = new PasswordField();

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		Node addButton = dialog.getDialogPane().lookupButton(buttonType);
		addButton.setDisable(true);

		username.textProperty().addListener((observable, oldValue, newValue) -> {
			addButton.setDisable(newValue.isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> username.requestFocus());

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == buttonType) {
				return new User(username.getText(), password.getText());
			}
			return null;
		});

		Optional<User> output = dialog.showAndWait();

		if (!output.isPresent())
			return;

		User newUser = output.get();

		// check if user has the same username of an existing user
		for (int i = 0; i < userList.size(); i++) {
			if (newUser.getUserName().equals(userList.get(i).getUserName())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error adding user");
				alert.setHeaderText("User already exists");
				alert.setContentText("The entered username matches a user that already exists");

				alert.showAndWait();
				return;
			}
		}

		userList.add(newUser);
		table.getSortOrder().add(userNameColumn);
		userNameColumn.setSortable(true);
	}

	
	/**
	 * delete button clicked, deletes the selected user, if one is not selected an error is reported
	 */
	public void deleteUser() {
		if (table.getSelectionModel().getSelectedIndex() == -1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error deleting user");
			alert.setHeaderText("No user selected");
			alert.setContentText("To delete a user, select the user and click the delete button");

			alert.showAndWait();
		}

		userList.remove(table.getSelectionModel().getSelectedItem());
		userNameColumn.setSortable(true);
	}

	
	/**
	 * logs out of the admin user and returns to the login screen
	 * @param e logout button click
	 * @throws IOException could not return to login screen
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
}
