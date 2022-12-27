package stages.primary.edit;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import model.Album;
import model.Model;
import model.Photo;
import photos.Photos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Parth Patel, Yash Patel
 */
public class Controller {
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
     * caption field
     */
    @FXML
    private TextField caption;
    /**
     * update caption button
     */
    @FXML
    private Button updateCaption;
    /**
     * delete a selected tag button
     */
    @FXML
    private Button deleteTag;
    /**
     * displays the tags
     */
    @FXML
    private ListView<String> tagsList;
    /**
     * list of tag types and properties
     */
    @FXML
    private ChoiceBox<String> presets;
    /**
     * label for tag type
     */
    @FXML
    private Text tagTypeLabel;
    /**
     * tag type field
     */
    @FXML
    private TextField tagType;
    /**
     * label for tag property
     */
    @FXML
    private Text tagPropertyLabel;
    /**
     * tag property choice box
     */
    @FXML
    private ChoiceBox<String> tagProperty;
    /**
     * tag value field
     */
    @FXML
    private TextField tagValue;
    /**
     * tag warning message for error
     */
    @FXML
    private Text tagWarning;
    /**
     * add a new tag button
     */
    @FXML
    private Button addTag;
    /**
     * destination album for move to or copy to
     */
    @FXML
    private TextField destinationAlbum;
    /**
     * warning error message
     */
    @FXML
    private Text warning;
    /**
     * copy photo to destination album
     */
    @FXML
    private Button copyTo;
    /**
     * move photo to destination album
     */
    @FXML
    private Button moveTo;
    /**
     * current album
     */
    private Album currentAlbum;
    /**
     * selected photo in current album
     */
    private Photo selectedPhoto;

    /**
     * initialize the edit scene
     */
    public void initialize() {
        currentAlbum = (Album) Model.dataTransfer.get(0);
        selectedPhoto = (Photo) Model.dataTransfer.get(1);
        caption.setText(selectedPhoto.caption);
        tagProperty.setDisable(true);
        tagType.setDisable(true);
        updateTagsList();
        updatePresets();
        tagProperty.getItems().addAll("Single", "Multi");
        presets.setOnAction(this::selectTagType);
        this.back.setOnAction(actionEvent -> {
            Model.initPreviousScene();
            Photos.changeScene("primary", "/stages/primary/photoslist/photoslist.fxml");
        });
        this.logout.setOnAction(actionEvent -> Photos.changeScene("primary", "/stages/primary/main/main.fxml"));
        this.deleteTag.setOnAction(actionEvent -> deleteTag());
        this.updateCaption.setOnAction(actionEvent -> updateCaption());
        this.addTag.setOnAction(actionEvent -> addTag());
        this.copyTo.setOnAction(actionEvent -> copyTo());
        this.moveTo.setOnAction(actionEvent -> moveTo());
    }

    /**
     * update the preset list
     */
    public void updatePresets() {
        this.presets.setItems(FXCollections.observableList(Model.currentUser.tagPreset.stream().map(p -> p.getKey()+" - "+p.getValue()).collect(Collectors.toList())));
    }

    /**
     * update the tags list
     */
    public void updateTagsList() {
        this.tagsList.setItems(FXCollections.observableList(selectedPhoto.tags.stream().map(t -> t.type+"="+t.value).collect(Collectors.toList())));
    }

    /**
     * delete a selected tag
     */
    public void deleteTag() {
        if (this.tagsList.getSelectionModel().isEmpty()) {
            return;
        }
        Pattern p = Pattern.compile("(\\S*)=(\\S*)");
        Matcher m = p.matcher(this.tagsList.getSelectionModel().getSelectedItem());
        if (m.find()) {
            try {
                selectedPhoto.removeTag(m.group(1), m.group(2));
                updateTagsList();
            } catch (Exception e) {
                throw new RuntimeException("Error removing tag from photo");
            }
        }
        Model.persist();
    }

    /**
     * update the caption of the photo
     */
    public void updateCaption() {
        selectedPhoto.caption = caption.getText();
        this.caption.setText(selectedPhoto.caption);
        Model.persist();
    }

    /**
     * select tag type
     * @param event event
     */
    public void selectTagType(Event event) {
        tagPropertyLabel.setOpacity(0);
        tagProperty.setOpacity(0);
        tagProperty.setDisable(true);
        tagTypeLabel.setOpacity(0);
        tagType.setDisable(true);
        tagType.setOpacity(0);
        if (this.presets.getValue() == null) return;
        Pattern p = Pattern.compile("(\\S*) - (\\S*)");
        Matcher m = p.matcher(this.presets.getValue());
        if (!m.find()) return;
        if (m.group(1).equals("Other")) {
            tagPropertyLabel.setOpacity(1);
            tagProperty.setOpacity(1);
            tagProperty.setDisable(false);
            tagTypeLabel.setOpacity(1);
            tagType.setDisable(false);
            tagType.setOpacity(1);
        }
    }

    /**
     * add a new tag
     */
    public void addTag() {
        if (presets.getValue() == null || presets.getValue().isEmpty() || (!tagType.isDisabled() && tagType.getText().isEmpty()) || (!tagProperty.isDisabled() && tagProperty.getValue() == null) || (!tagProperty.isDisabled() && tagProperty.getValue().isEmpty()) || tagValue.getText().isEmpty()) {
            tagWarning.setText("Fill in all the details to add a tag.");
            tagWarning.setOpacity(0.69);
            return;
        }
        if (tagValue.getText().contains("=") || tagValue.getText().contains("-")) {
            tagWarning.setText("Tag value cannot contain '=' or '-'");
            tagWarning.setOpacity(0.69);
            return;
        }
        if (!tagType.isDisabled() && !tagType.getText().isEmpty() && (tagType.getText().contains("=") || tagType.getText().contains("-"))) {
            tagWarning.setText("Tag type cannot contain '=' or '-'");
            tagWarning.setOpacity(0.69);
            return;
        }
        tagWarning.setOpacity(0);

        Pattern p = Pattern.compile("(\\S*) - (\\S*)");
        Matcher m = p.matcher(this.presets.getValue());
        if (!m.find()) return;

        try {
            if (m.group(1).equals("Other")) {
                selectedPhoto.addTag(tagType.getText(), tagValue.getText(), tagProperty.getValue().equals("Single"));
                updatePresets();
            } else {
                selectedPhoto.addTag(m.group(1), tagValue.getText());
            }
            updateTagsList();
            Model.persist();
        } catch (Exception e) {
            tagWarning.setText(e.getMessage());
            tagWarning.setOpacity(0.69);
        }
    }

    /**
     * copy to destination album
     */
    public void copyTo() {
        try {
            Model.currentUser.albums.get(Model.currentUser.albums.indexOf(new Album(destinationAlbum.getText()))).addPhoto(selectedPhoto.path, selectedPhoto.caption);
            Model.persist();
            warning.setOpacity(0);
        } catch (IndexOutOfBoundsException e) {
            warning.setText("album does not exist");
            warning.setOpacity(0.69);
        } catch (Exception e) {
            warning.setText(e.getMessage());
            warning.setOpacity(0.69);
        }
    }

    /**
     * move to destination album
     */
    public void moveTo() {
        try {
            Model.currentUser.albums.get(Model.currentUser.albums.indexOf(new Album(destinationAlbum.getText()))).addPhoto(selectedPhoto.path, selectedPhoto.caption);
            currentAlbum.removePhoto(selectedPhoto.path);
            Model.persist();
            warning.setOpacity(0);
        } catch (IndexOutOfBoundsException e) {
            warning.setText("album does not exist");
            warning.setOpacity(0.69);
        } catch (Exception e) {
            warning.setText(e.getMessage());
            warning.setOpacity(0.69);
        }
    }
}