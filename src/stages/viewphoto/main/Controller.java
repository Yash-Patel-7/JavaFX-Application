package stages.viewphoto.main;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Album;
import model.Model;
import model.Photo;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * controller for view photo scene
 * @author Parth Patel, Yash Patel
 */
public class Controller {
    /**
     * caption label
     */
    @FXML
    private Text caption;
    /**
     * date taken label
     */
    @FXML
    private Text dateTaken;
    /**
     * display image
     */
    @FXML
    private ImageView displayImage;
    /**
     * previous photo in current album
     */
    @FXML
    private Button previous;
    /**
     * next photo in current album
     */
    @FXML
    private Button next;
    /**
     * list of tags
     */
    @FXML
    private ListView<String> tagsList;
    /**
     * current album
     */
    private Album currentAlbum;
    /**
     * current photo
     */
    private Photo currentPhoto;

    /**
     * initialize the view photo scene
     */
    public void initialize() {
        currentAlbum = (Album) Model.dataTransfer.get(0);
        currentPhoto = (Photo) Model.dataTransfer.get(1);
        updatePrevNext();
        updateDisplay();
        updateTagsList();
        this.previous.setOnAction(actionEvent -> previousPhoto());
        this.next.setOnAction(actionEvent -> nextPhoto());
    }

    /**
     * update the tags list
     */
    public void updateTagsList() {
        this.tagsList.setItems(FXCollections.observableList(currentPhoto.tags.stream().map(t -> t.type+"="+t.value).collect(Collectors.toList())));
    }

    /**
     * update the prev / next buttons to disable / enable
     */
    public void updatePrevNext() {
        this.previous.setDisable((currentAlbum.photos.indexOf(currentPhoto)) == 0);
        this.next.setDisable(currentAlbum.photos.indexOf(currentPhoto) == (currentAlbum.photos.size() - 1));
    }

    /**
     * update the display
     */
    public void updateDisplay() {
        if (!currentPhoto.path.isEmpty()) displayImage.setImage(new Image("file:" + currentPhoto.path));
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.caption.setText(currentPhoto.caption);
        this.dateTaken.setText(formatter.format(currentPhoto.dateTaken.getTime()));
    }

    /**
     * go to previous photo in current album
     */
    public void previousPhoto() {
        int index = currentAlbum.photos.indexOf(currentPhoto) - 1;
        currentPhoto = currentAlbum.photos.get(index);
        updatePrevNext();
        updateDisplay();
        updateTagsList();
    }

    /**
     * go to next photo in current album
     */
    public void nextPhoto() {
        int index = currentAlbum.photos.indexOf(currentPhoto) + 1;
        currentPhoto = currentAlbum.photos.get(index);
        updatePrevNext();
        updateDisplay();
        updateTagsList();
    }
}
