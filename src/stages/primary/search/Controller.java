package stages.primary.search;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import model.Album;
import model.Model;
import model.Photo;
import photos.Photos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * controller for search scene
 * @author Parth Patel, Yash Patel
 */
public class Controller {
    /**
     * tilepane for photos thumbnails
     */
    @FXML
    private TilePane photosPane;
    /**
     * back to previous scene
     */
    @FXML
    private Button back;
    /**
     * logout to login page
     */
    @FXML
    private Button logout;
    /**
     * search field for photos search
     */
    @FXML
    private TextField searchField;
    /**
     * search button
     */
    @FXML
    private Button search;
    /**
     * search warning error messages
     */
    @FXML
    private Text searchWarning;
    /**
     * new album name field
     */
    @FXML
    private TextField newAlbumName;
    /**
     * warning error messages
     */
    @FXML
    private Text warning;
    /**
     * create new album button containing search results
     */
    @FXML
    private Button createAlbum;
    /**
     * caption field
     */
    @FXML
    private Text caption;
    /**
     * date taken field
     */
    @FXML
    private Text dateTaken;
    /**
     * display photo button
     */
    @FXML
    private Button display;
    /**
     * search results
     */
    private ArrayList<Photo> searchResults;
    /**
     * selected photo box with border
     */
    private VBox selectedPhotoBox;
    /**
     * selected photo
     */
    private Photo selectedPhoto;

    /**
     * initialize the search scene
     */
    public void initialize() {
        String searchQuery = (String) Model.dataTransfer.get(0);
        searchField.setText(searchQuery);
        getSearchedImages(searchQuery);

        createElements();

        this.back.setOnAction(actionEvent -> {
            Model.initPreviousScene();
            Photos.changeScene("primary", "/stages/primary/albums/albums.fxml");
        });
        this.logout.setOnAction(actionEvent -> Photos.changeScene("primary", "/stages/primary/main/main.fxml"));
        this.search.setOnAction(actionEvent -> searchPhotos());
        this.createAlbum.setOnAction(actionEvent -> addAlbum());
        this.display.setOnAction(actionEvent -> displayPhoto());
    }

    /**
     * create elements for photos thumbnails
     */
    public void createElements() {
        photosPane.getChildren().clear();
        photosPane.setPrefColumns(3);
        photosPane.setHgap(10);
        photosPane.setVgap(10);
        for (Photo p: searchResults) {
            photosPane.getChildren().add(createElement(p));
        }
    }

    /**
     *
     * @param p photo
     * @return component containing photo thumbnail
     */
    public VBox createElement(Photo p) {
        ImageView img = new ImageView();
        img.setImage(new Image("file:" + p.path));
        img.setFitWidth(175);
        img.setFitHeight(175);

        VBox element = new VBox();
        element.getChildren().add(img);
        element.setOnMouseClicked(mouseEvent -> {
            if (selectedPhotoBox != null) {
                selectedPhotoBox.setBorder(Border.stroke(Paint.valueOf("white")));
            }
            selectedPhotoBox = element;
            Border b = new Border(new BorderStroke(Paint.valueOf("#4285F4"),
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)));

            selectedPhotoBox.setBorder(b);
            selectedPhoto = p;
            updateDetailDisplay();
        });
        return element;
    }

    /**
     * update detail display for selected photo
     */
    public void updateDetailDisplay() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.caption.setText(selectedPhoto.caption);
        this.dateTaken.setText(formatter.format(selectedPhoto.dateTaken.getTime()));
    }

    /**
     * add new album containing search results
     */
    public void addAlbum() {
        try {
            if (newAlbumName.getText().isEmpty()) {
                throw new Exception("Enter album name");
            }
            Model.currentUser.createAlbum(newAlbumName.getText(), searchResults);
            Model.persist();
            warning.setOpacity(0);
        } catch (Exception e) {
            warning.setText(e.getMessage());
            warning.setOpacity(0.69);
        }
    }

    /**
     * display photo
     */
    public void displayPhoto() {
        if (selectedPhoto == null) {
            return;
        }
        Model.initNextScene(false);
        Album results = new Album("Search Results", searchResults);
        Model.dataTransfer.add(results);
        Model.dataTransfer.add(selectedPhoto);
        Photos.changeScene("viewphoto", "/stages/viewphoto/main/main.fxml");
    }

    /**
     * search user's unique photos
     */
    public void searchPhotos() {
        if (searchField.getText().isEmpty() || searchField.getText().matches("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2} TO \\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}") || searchField.getText().matches("\\S+=\\S+") || searchField.getText().matches("\\S+=\\S+ AND \\S+=\\S+") || searchField.getText().matches("\\S+=\\S+ OR \\S+=\\S+")) {
            searchWarning.setOpacity(0);
            getSearchedImages(searchField.getText());
            if (!searchResults.isEmpty()) {
                selectedPhoto = searchResults.get(0);
            }
            createElements();
        } else {
            searchWarning.setOpacity(0.69);
        }
    }

    /**
     * gets search results
     * @param searchQuery the search query
     */
    public void getSearchedImages(String searchQuery) {
        if (searchQuery.isEmpty()) {
            searchResults = Model.currentUser.getAllPhotos();
        } else if (searchQuery.matches("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2} TO \\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            Pattern p = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}) TO (\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2})");
            Matcher m = p.matcher(searchQuery);
            if (m.find()) {
                try {
                    start.setTime(formatter.parse(m.group(1)));
                    end.setTime(formatter.parse(m.group(2)));
                    searchResults = Model.currentUser.getPhotosInRange(start, end);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing searchQuery for date range");
                }
            }
        } else if (searchQuery.matches("\\S+=\\S+")) {
            Pattern p = Pattern.compile("(\\S+)=(\\S+)");
            Matcher m = p.matcher(searchQuery);
            if (m.find()) {
                try {
                    searchResults = Model.currentUser.getPhotosByTag(m.group(1), m.group(2));
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing searchQuery for a tag");
                }
            }
        } else if (searchQuery.matches("\\S+=\\S+ AND \\S+=\\S+")) {
            Pattern p = Pattern.compile("(\\S+)=(\\S+) AND (\\S+)=(\\S+)");
            Matcher m = p.matcher(searchQuery);
            if (m.find()) {
                try {
                    searchResults = Model.currentUser.getPhotosByTags(m.group(1), m.group(2), m.group(3),m.group(4), true);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing searchQuery for tags using AND");
                }
            }
        } else if (searchQuery.matches("\\S+=\\S+ OR \\S+=\\S+")) {
            Pattern p = Pattern.compile("(\\S+)=(\\S+) OR (\\S+)=(\\S+)");
            Matcher m = p.matcher(searchQuery);
            if (m.find()) {
                try {
                    searchResults = Model.currentUser.getPhotosByTags(m.group(1), m.group(2), m.group(3),m.group(4), false);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing searchQuery for tags using OR");
                }
            }
        }
    }
}