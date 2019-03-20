package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import photos.model.Photo;
import photos.model.Tag;


/**
 * Class controller to perform actions and display the information in the slide show screen
 * 
 * @author Nick Fasullo
 *
 */


public class SlideShowController {

	
	/**
	 * button to slide back one photo in the album
	 */
	@FXML Button prev;
	
	
	/**
	 * button to slide forward one photo in the album
	 */
	@FXML Button next;
	
	
	/**
	 * area to display the enlarged photo
	 */
	@FXML ImageView pic;
	
	
	/**
	 * area to display photo name
	 */
	@FXML Label picName;
	
	
	/**
	 * area to display the photo date
	 */
	@FXML Label picDate;
	
	
	/**
	 * area to display the photo time occurrence
	 */
	@FXML Label picTime;
	
	
	/**
	 * area to display all of the photos tags
	 */
	@FXML Label tagVals;
	
	
	/**
	 * stage being displayed
	 */
	private Stage stage;
	
	/**
	 * list of all the photos in the album
	 */
	private ArrayList<Photo> album = new ArrayList<Photo>();
	
	
	/**
	 * current index in the photo list
	 */
	private int indx;
	
	
	/**
	 * 
	 * @param mainStage current stage
	 * @param temp list of photos in the album
	 * @param picture current picture being displayed
	 * @throws FileNotFoundException could not display the information
	 */
	public void start(Stage mainStage, ArrayList<Photo> temp, Photo picture) throws FileNotFoundException {
		album = temp;
		indx = album.indexOf(picture);
		stage = mainStage;
		setPic(picture);
	}
	
	 
	
	
	/**
	 * takes the photo creates an enlarged image and sets all of its information in the appropriate spots on the screen
	 * @param picture photo to display
	 * @throws FileNotFoundException could not display photo
	 */
	public void setPic(Photo picture) throws FileNotFoundException{
		File imageFile = picture.getFile();
		Image img = new Image(new FileInputStream(imageFile));
		pic.setImage(img);
		pic.setFitHeight(325);
		pic.setFitWidth(525);
		pic.setPreserveRatio(true);
        pic.setSmooth(true);
        pic.setCache(true);
        int pos = imageFile.getName().lastIndexOf(".", imageFile.getName().length()-1);
		String extnd = imageFile.getName().substring(pos, imageFile.getName().length());
		stage.setTitle(picture.getCaption() + extnd);
        Calendar cal = picture.getDate();
        Date day = cal.getTime();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(day);
        String time = new SimpleDateFormat("hh:mm:ss a").format(day);
        picName.setText(picture.getCaption());
        picDate.setText("Date: " + date);
        picTime.setText("Time: " + time);
        Collections.sort(picture.getTags(), (a, b) -> a.getName().compareTo(b.getName()));
        String out = "| ";
		for(Tag t: picture.getTags()){ out = out + t + " | "; }
		if(out.equals("| ")){ out = "None"; }
		tagVals.setText(out);
	}
	
	
	
	
	/**
	 * Displays the previous photo in the album, if at the fist photo in the list, wraps around and gets the last photo in the list
	 * @param e previous photo button click
	 * @throws FileNotFoundException could not go back one photo
	 */
	public void slideBack(ActionEvent e) throws FileNotFoundException{
		if(indx > 0){
			Photo pic = album.get(indx - 1);
			indx = indx - 1;
			setPic(pic);
		}
		else{
			Photo pic = album.get(album.size()-1);
			indx = album.size()-1;
			setPic(pic);
		}
	}
	
	
	
	
	/**
	 * Displays the next photo in the album, if at the last photo in the list, wraps around and gets the first photo in the list
	 * @param e next photo button click
	 * @throws FileNotFoundException could not go forward one photo
	 */
	public void slideFoward(ActionEvent e) throws FileNotFoundException{
		if(indx < album.size() - 1){
			Photo pic = album.get(indx + 1);
			indx = indx + 1;
			setPic(pic);
		}
		else{
			Photo pic = album.get(0);
			indx = 0;
			setPic(pic);
		}
	}
	
	
}
