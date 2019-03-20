package photos.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import photos.controller.LoginController;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;

/**
 * Class acts as the driver for the program, also where the data is set up to be loaded and saved
 * 
 * @author Nick Fasullo
 *
 */



public class Photos extends Application {
	
	/**
	 * array list containing all the people who have created user instance in the program
	 */
	private ArrayList<User> users;
	
	
	/**
	 * folder name where data file will b saved
	 */
	private static final String dir = "data";
	
	
	/**
	 * data file that will save and load all the actions performed in the program
	 */
	private static final String filename = "data.dat";
	
	
	/**
	 * current stage of the program
	 */
	private Stage stage;

	
	/**
	 * sets and displays the login screen, loading or creating a data file instance
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		stage = primaryStage;
		dataCaller();
		
		primaryStage.setTitle("Photo Album");
		primaryStage.setResizable(false);

		FXMLLoader loader = new FXMLLoader();
		FileInputStream inputStream = new FileInputStream(new File("src/photos/view/login.fxml"));
		Parent root = (Parent) loader.load(inputStream);

		LoginController loginController = loader.<LoginController>getController();
		loginController.start(primaryStage, users);

		Scene loginScene = new Scene(root, 640, 400);
		primaryStage.setScene(loginScene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	/**
	 * gets the stock photos and puts them in an album and adds it to the stock user 
	 */
	private void addStockPhotos() {
		User stockUser = new User("stock", "");
		users.add(stockUser);

		Album stockAlbum = new Album("Stock Photos");
		stockUser.addAlbum(stockAlbum);

		File beebop = new File("stock/beebop.gif");
		ArrayList<Tag> list1 = new ArrayList<Tag>();
		Tag t0 = new Tag("person", "ed");
		Tag t1 = new Tag("animal", "dog");
		Tag t2 = new Tag("location", "mars");
		Tag t3 = new Tag("show", "cowboy beebop");
		list1.add(t0);list1.add(t1);list1.add(t2);list1.add(t3);
		Collections.sort(list1, (a, b) -> a.getName().compareTo(b.getName()));
		
		File celtics = new File("stock/celtics.png");
		ArrayList<Tag> list2 = new ArrayList<Tag>();
		Tag t4 = new Tag("person", "kevin garnett");
		Tag t5 = new Tag("person", "paul pierce");
		Tag t6 = new Tag("person", "ray allen");
		Tag t7 = new Tag("location", "boston");
		Tag t8 = new Tag("team", "celtics");
		list2.add(t4);list2.add(t5);list2.add(t6);list2.add(t7);list2.add(t8);
		Collections.sort(list2, (a, b) -> a.getName().compareTo(b.getName()));
		
		File snowboard = new File("stock/snowboard.jpeg");
		ArrayList<Tag> list3 = new ArrayList<Tag>();
		Tag t9 = new Tag("person", "mark");
		Tag t10 = new Tag("location", "mountain creek");
		Tag t11 = new Tag("weather", "cloudy");
		Tag t12 = new Tag("season", "winter");
		Tag t13 = new Tag("activity", "snowboarding");
		list3.add(t9);list3.add(t10);list3.add(t11);list3.add(t12);list3.add(t13);
		Collections.sort(list3, (a, b) -> a.getName().compareTo(b.getName()));
		
		File ssj1 = new File("stock/ssj1.gif");
		ArrayList<Tag> list4 = new ArrayList<Tag>();
		Tag t14 = new Tag("person", "goku");
		Tag t15 = new Tag("show", "dragon ball z");
		Tag t16 = new Tag("form", "ssj1");
		Tag t17 = new Tag("location", "earth");
		list4.add(t14);list4.add(t15);list4.add(t16);list4.add(t17);
		Collections.sort(list4, (a, b) -> a.getName().compareTo(b.getName()));
		
		
		File supra = new File("stock/supra.jpg");
		ArrayList<Tag> list5 = new ArrayList<Tag>();
		Tag t18 = new Tag("make", "toyota");
		Tag t19 = new Tag("model", "supra");
		Tag t20 = new Tag("transmission", "manual");
		Tag t21 = new Tag("color", "red");
		Tag t22 = new Tag("object", "car");
		list5.add(t18);list5.add(t19);list5.add(t20);list5.add(t21);list5.add(t22);
		Collections.sort(list5, (a, b) -> a.getName().compareTo(b.getName()));
		
		
		
		try {
			Photo pic1 = new Photo(beebop, "Beebop", list1);
			Photo pic2 = new Photo(celtics, "The Big Three", list2);
			Photo pic3 = new Photo(snowboard, "Snowboard", list3);
			Photo pic4 = new Photo(ssj1, "Super Saiyan", list4);
			Photo pic5 = new Photo(supra, "Toyota Supra", list5);
			stockAlbum.addPhoto(pic1);
			stockAlbum.addPhoto(pic2);
			stockAlbum.addPhoto(pic3);
			stockAlbum.addPhoto(pic4);
			stockAlbum.addPhoto(pic5);
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Start Error");
			alert.setHeaderText("Error loading stock photos");
			alert.setContentText("Can not load stock photos, make sure you have the stock photos folder");

			alert.showAndWait();
		}
	}

	
	/**
	 * starts and launches the program
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	
	
	
	
	
	
	/**
	 * sets the program so save all the data when the user performs quit application
	 * <p>
	 * calls load data, so the user can still access the progess from previous sessions
	 */
	public void dataCaller(){
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event) { saveData(); }
		});
		
		loadData();
	}
	
	
	
	
	/**
	 * creates an input stream to read in the data from previous sessions
	 * <p>
	 * if a data file is not present, it is the first run of the program, so it will create the file
	 * and then make an admin user, along with stock user, with some photos pre-loaded into the user
	 */
	@SuppressWarnings("unchecked")
	public void loadData(){
		try{
			FileInputStream fileIn = new FileInputStream(dir + File.separator + filename);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			users = (ArrayList<User>) objIn.readObject();
			if (users == null){
				users = new ArrayList<User>();
				users.add(new User("admin", "admin"));
				addStockPhotos();
			}
			objIn.close();
			fileIn.close();
		}
		catch(FileNotFoundException e){
			users = new ArrayList<User>();
			users.add(new User("admin", "admin"));
			addStockPhotos();
		}
		catch(IOException e){ e.printStackTrace(); }
		catch(ClassNotFoundException e){ e.printStackTrace(); }
	}
	
	
	
	/**
	 * creates a data file to save to if one does not already exist
	 * <p>
	 * makes an output stream to write all of the information to, upon a quit application action
	 */
	public void saveData(){
		File file = new File(dir);
		if (!file.exists()){ file.mkdir(); }
		try{
			FileOutputStream fileOut = new FileOutputStream(dir + File.separator + filename);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(users);
			objOut.close();
			fileOut.close();
		}
		catch(IOException e){ e.printStackTrace(); }
	}

	
}
