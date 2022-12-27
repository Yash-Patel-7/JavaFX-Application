package stages.primary.main;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Model;
import photos.Photos;

/**
 * controller for main scene
 * @author Parth Patel, Yash Patel
 */
public class Controller {
    /**
     * logo
     */
    @FXML
    private ImageView logo;
    /**
     * name of user
     */
    @FXML
    private TextField username;
    /**
     * continue button
     */
    @FXML
    private Button proceed;
    /**
     * warning error message
     */
    @FXML
    private Text warning;

    /**
     * initialize main login scene
     */
    public void initialize() {
        Model.logOut();
        this.logo.setImage(Photos.getLogo());
        this.proceed.setOnAction(actionEvent -> continueAction());
    }

    /**
     * continue with the username
     */
    public void continueAction() {
        try {
            Model.setCurrentUser(username.getText());
            warning.setOpacity(0);

            Model.initNextScene(true);
            if (username.getText().equals("admin")) {
                Photos.changeScene("primary", "/stages/primary/admin/admin.fxml");
            } else {
                Photos.changeScene("primary", "/stages/primary/albums/albums.fxml");
            }
        } catch (Exception e) {
            warning.setOpacity(0.69);
        }
    }
}

