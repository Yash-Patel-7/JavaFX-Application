package stages.primary.albums;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Album;
import model.Model;
import photos.Photos;

import java.text.SimpleDateFormat;

/**
 * controller for the albums scene
 * @author Parth Patel, Yash Patel
 */
public class Controller {
    /**
     * rename an album
     */
    @FXML
    private Button rename;
    /**
     * tilepane for the albums
     */
    @FXML
    private TilePane albumsPane;
    /**
     * back to go previous scene
     */
    @FXML
    private Button back;
    /**
     * logout to login scene
     */
    @FXML
    private Button logout;
    /**
     * search field for photos
     */
    @FXML
    private TextField searchField;
    /**
     * search button to search photos
     */
    @FXML
    private Button search;
    /**
     * warning incorrect format for search
     */
    @FXML
    private Text searchWarning;
    /**
     * delete an album
     */
    @FXML
    private Button delete;
    /**
     * show add album components
     */
    @FXML
    private Button promptAdd;
    /**
     * name of album label
     */
    @FXML
    private Text nameOfAlbum;
    /**
     * number of photos label
     */
    @FXML
    private Text numPhotos;
    /**
     * dateRange label
     */
    @FXML
    private Text dateRange;
    /**
     * open the album button
     */
    @FXML
    private Button openAlbum;
    /**
     * new album label
     */
    @FXML
    private Text newAlbumLabel;
    /**
     * album name field
     */
    @FXML
    private TextField albumName;
    /**
     * warning for error
     */
    @FXML
    private Text warning;
    /**
     * add new album button
     */
    @FXML
    private Button sendAdd;
    /**
     * selected album box that has border
     */
    private VBox selectedAlbumBox;
    /**
     * selected Album
     */
    private Album selectedAlbum;
    /**
     * whether rename button is allowed (true if and only if an album is selected)
     */
    private boolean renameAllowed;

    /**
     * initialize albums scene
     */
    public void initialize() {
        if (!Model.dataTransfer.isEmpty()) {
            selectedAlbum = (Album) Model.dataTransfer.get(0);
        }
        sendAdd.setDisable(true);
        albumName.setDisable(true);

        createElements();

        this.back.setOnAction(actionEvent -> {
            Model.initPreviousScene();
            Photos.changeScene("primary", "/stages/primary/main/main.fxml");
        });
        this.logout.setOnAction(actionEvent -> Photos.changeScene("primary", "/stages/primary/main/main.fxml"));
        this.search.setOnAction(actionEvent -> searchPhotos());
        this.delete.setOnAction(actionEvent -> deleteAlbum());
        this.promptAdd.setOnAction(actionEvent -> promptAdd());
        this.openAlbum.setOnAction(actionEvent -> openAlbum());
        this.sendAdd.setOnAction(actionEvent -> addAlbum());
        this.rename.setOnAction(actionEvent -> renameAlbum());
    }

    /**
     * create the albums thumbnails
     */
    public void createElements() {
        albumsPane.getChildren().clear();
        albumsPane.setPrefColumns(3);
        albumsPane.setHgap(10);
        albumsPane.setVgap(10);
        for (Album a: Model.currentUser.albums) {
            albumsPane.getChildren().add(createElement(a));
        }
    }

    /**
     *
     * @param a the album
     * @return component containing the album thumbnail
     */
    public VBox createElement(Album a) {
        ImageView img = new ImageView();
        if (a.photos.size() == 0) {
            // placeholder image when we create new album
            img.setImage(Photos.getLogo());
        } else {
            img.setImage(new Image("file:" + a.photos.get(a.photos.size() - 1).path));
        }
        img.setFitWidth(175);
        img.setFitHeight(175);

        Text albumName = new Text(a.name);
        albumName.setFont(Font.font(18));

        VBox element = new VBox();
        element.getChildren().add(img);
        element.getChildren().add(albumName);
        element.setAlignment(Pos.CENTER);

        Border b = new Border(new BorderStroke(Paint.valueOf("#4285F4"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)));

        if (selectedAlbum != null && selectedAlbum.equals(a)) {
            selectedAlbumBox = element;
            selectedAlbumBox.setBorder(b);
            updateDetailDisplay();
            renameAllowed = true;
        }

        element.setOnMouseClicked(mouseEvent -> {
            if (selectedAlbumBox != null) {
                selectedAlbumBox.setBorder(Border.stroke(Paint.valueOf("white")));
            }
            selectedAlbum = a;
            selectedAlbumBox = element;
            selectedAlbumBox.setBorder(b);
            Model.dataTransfer.clear();
            Model.dataTransfer.add(selectedAlbum);
            updateDetailDisplay();
            if (promptAdd.getText().equals("Close")) {
                promptAdd();
            }
            renameAllowed = true;
        });

        return element;
    }

    /**
     * update the details of the selected album
     */
    public void updateDetailDisplay() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.nameOfAlbum.setText(selectedAlbum.name);
        this.numPhotos.setText(String.valueOf(selectedAlbum.photos.size()));
        this.dateRange.setText(formatter.format(selectedAlbum.start.getTime()) + " TO " + formatter.format(selectedAlbum.end.getTime()));
    }

    /**
     * search all the unique photos of the user
     */
    public void searchPhotos() {
        if (searchField.getText().isEmpty() || searchField.getText().matches("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2} TO \\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}") || searchField.getText().matches("\\S+=\\S+") || searchField.getText().matches("\\S+=\\S+ AND \\S+=\\S+") || searchField.getText().matches("\\S+=\\S+ OR \\S+=\\S+")) {
            searchWarning.setOpacity(0);
            Model.initNextScene(true);
            Model.dataTransfer.add(searchField.getText());
            Photos.changeScene("primary", "/stages/primary/search/search.fxml");
        } else {
            searchWarning.setOpacity(0.69);
        }
    }

    /**
     * delete album
     */
    public void deleteAlbum() {
        if (selectedAlbum == null) {
            return;
        }
        try {
            Model.currentUser.deleteAlbum(selectedAlbum.name);
            Model.persist();
            selectedAlbum = null;
            this.nameOfAlbum.setText("N/A");
            this.numPhotos.setText("N/A");
            this.dateRange.setText("N/A TO N/A");
            createElements();
        } catch (Exception ignored) {}
    }

    /**
     * show add album components
     */
    public void promptAdd() {
        if (promptAdd.getText().equals("Edit")) {
            promptAdd.setText("Close");
            sendAdd.setDisable(false);
            newAlbumLabel.setOpacity(1);
            albumName.setOpacity(1);
            albumName.setDisable(false);
            sendAdd.setOpacity(1);
            if (renameAllowed) {
                rename.setDisable(false);
                rename.setOpacity(1);
            } else {
                rename.setDisable(true);
                rename.setOpacity(0.59);
            }
        } else {
            promptAdd.setText("Edit");
            sendAdd.setDisable(true);
            newAlbumLabel.setOpacity(0);
            albumName.setOpacity(0);
            albumName.setDisable(true);
            sendAdd.setOpacity(0);
            warning.setOpacity(0);
            rename.setOpacity(0);
            rename.setDisable(true);
        }
    }

    /**
     * go to the photoslist scene
     */
    public void openAlbum() {
        if (selectedAlbum == null) {
            return;
        }
        Model.initNextScene(true);
        Model.dataTransfer.add(selectedAlbum);
        Photos.changeScene("primary", "/stages/primary/photoslist/photoslist.fxml");
    }

    /**
     * add album
     */
    public void addAlbum() {
        try {
            if (albumName.getText().isEmpty()) {
                throw new Exception("Enter album name");
            }
            Model.currentUser.createAlbum(albumName.getText());
            Model.persist();
            createElements();
            promptAdd();
        } catch (Exception e) {
            warning.setText(e.getMessage());
            warning.setOpacity(0.69);
        }
    }

    /**
     * rename the album
     */
    public void renameAlbum() {
        if (selectedAlbum == null) {
            warning.setText("Select an album first");
            warning.setOpacity(0.69);
            return;
        }
        try {
            if (albumName.getText().isEmpty()) {
                throw new Exception("Enter album name");
            }
            Model.currentUser.renameAlbum(selectedAlbum.name, albumName.getText());
            Model.persist();
            createElements();
            promptAdd();
        } catch (Exception e) {
            warning.setText(e.getMessage());
            warning.setOpacity(0.69);
        }
    }
}



