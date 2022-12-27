package stages.primary.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Model;
import photos.Photos;

import java.util.stream.Collectors;

/**
 * controller for admin scene
 * @author Parth Patel, Yash Patel
 */
public class Controller {
    /**
     * back button to previous scene
     */
    @FXML
    private Button back;
    /**
     * logout to login page
     */
    @FXML
    private Button logout;
    /**
     * warning message for error
     */
    @FXML
    private Text warning;
    /**
     * name of the user
     */
    @FXML
    private TextField username;
    /**
     * add a new user
     */
    @FXML
    private Button addUser;
    /**
     * delete a user
     */
    @FXML
    private Button deleteUser;
    /**
     * list of users
     */
    @FXML
    private ListView<String> usersList;

    /**
     * initialize admin scene
     */
    public void initialize() {
        this.back.setOnAction(actionEvent -> {
            Model.initPreviousScene();
            Photos.changeScene("primary", "/stages/primary/main/main.fxml");
        });
        this.logout.setOnAction(actionEvent ->Photos.changeScene("primary", "/stages/primary/main/main.fxml"));
        this.addUser.setOnAction(actionEvent -> addUser());
        this.deleteUser.setOnAction(actionEvent -> deleteUser());
        updateUsersList();
    }

    /**
     * update the users list
     */
    public void updateUsersList() {
        this.usersList.setItems(FXCollections.observableList(Model.users.stream().map(u -> u.username).collect(Collectors.toList())));
    }

    /**
     * add user
     */
    public void addUser() {
        try {
            if (username.getText().isEmpty()) {
                throw new Exception("empty");
            }
            Model.addUser(username.getText());
            Model.persist();
            updateUsersList();
            warning.setOpacity(0);
        } catch (Exception e) {
            if (e.getMessage().equals("empty")) {
                warning.setText("Please type a username");
            } else {
                warning.setText(e.getMessage());
            }
            warning.setOpacity(0.69);
        }
    }

    /**
     * delete a user from the list
     */
    public void deleteUser() {
        if (this.usersList.getSelectionModel().isEmpty()) {
            return;
        }
        try {
            Model.deleteUser(this.usersList.getSelectionModel().getSelectedItem());
            Model.persist();
            updateUsersList();
        } catch (Exception ignored) {}
    }
}
