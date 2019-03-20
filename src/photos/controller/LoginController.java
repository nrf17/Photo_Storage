package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import photos.model.User;

/**
 * Class controller to log a user into the desired location, given the correct username and password
 * 
 * @author Nick Fasullo
 *
 */


public class LoginController {
	
	/**
	 * button to confirm login info
	 */
	@FXML Button login;

	
	/**
	 * area to enter the user name desired to log into
	 */
	@FXML TextField userName;

	
	/**
	 * area to enter the password for the corresponding user name
	 */
	@FXML PasswordField password;

	
	/**
	 * list of all the users in the program
	 */
	private List<User> userList;

	
	/**
	 * stage that will be displayed
	 */
	private Stage stage;

	
	/**
	 * start the login scene
	 * @param mainStage login screen
	 * @param listOfUsers list of program's users
	 */
	public void start(Stage mainStage, List<User> listOfUsers) {
	
		stage = mainStage;
		stage.setTitle("Photo Album");
		userList = listOfUsers;
	
		Platform.runLater(() -> userName.requestFocus());

		login.setDisable(true);

		userName.textProperty().addListener((observable, oldValue, newValue) -> {
			login.setDisable(newValue.isEmpty());
		});
	}

	
	/**
	 * login button click, will check to make sure the user name and password, exist or match
	 * <p>
	 * if login credentials match, loads into the users album list
	 * @throws Exception could not log into user
	 */
	public void login() throws Exception {
		User loggingInUser;
		int indexOfUser;

		if (userName.getText().isEmpty())
			return;

		loggingInUser = new User(userName.getText(), password.getText());

		indexOfUser = userList.indexOf(loggingInUser);

		if (indexOfUser == -1) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Invalid credentials");
			alert.setHeaderText("The username or password entered is incorrect");
			alert.setContentText("Check your credentials and try again. Contact your administrator for help.");

			alert.showAndWait();
			return;
		}

		loggingInUser = userList.get(indexOfUser);

		if (loggingInUser.getUserName().equals("admin")) {
			FXMLLoader loader = new FXMLLoader();
			FileInputStream inputStream = new FileInputStream(new File("src/photos/view/admin_panel.fxml"));
			AnchorPane root = (AnchorPane) loader.load(inputStream);

			AdminPanelController adminController = loader.getController();
			adminController.start(stage, userList);

			Scene adminScene = new Scene(root, 640, 400);
			stage.setScene(adminScene);
			stage.sizeToScene();
			stage.show();

			return;
		}

		// open user album
		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/users_albums.fxml"));
		AnchorPane root = (AnchorPane) loader.load(inputStream);

		UsersAlbumsController usersAlbumsController = loader.<UsersAlbumsController>getController();
		usersAlbumsController.start(stage, userList, loggingInUser);

		Scene loginScene = new Scene(root, 640, 400);
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.show();
	}


}