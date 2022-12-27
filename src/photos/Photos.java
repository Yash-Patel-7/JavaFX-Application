package photos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Model;

/**
 * main application class
 * @author Parth Patel, Yash Patel
 */
public final class Photos extends Application {
    /**
     * logo
     */
    private static Image logo;
    /**
     * primary stage
     */
    private static Stage primaryStage;
    /**
     * view photo stage
     */
    private static Stage viewPhotoStage;
    /**
     * is primary stage showing
     */
    private static boolean primaryShowing;
    /**
     * is view photo stage showing
     */
    private static boolean viewPhotoShowing;

    /**
     * get the logo
     * @return the image of logo
     */
    public static Image getLogo() {
        return Photos.logo;
    }

    /**
     * get primary stage
     * @return the primary stage
     */
    public static Stage getPrimaryStage() {
        return Photos.primaryStage;
    }

    /**
     * get the view photo stage
     * @return the view photo stage
     */
    public static Stage getViewPhotoStage() {
        return Photos.viewPhotoStage;
    }

    /**
     * is the primary stage showing
     * @return whether the primary stage is showing
     */
    public static boolean isPrimaryShowing() {
        return Photos.primaryShowing;
    }

    /**
     * is the view photo stage showing
     * @return whether the view photo stage is showing
     */
    public static boolean isViewPhotoShowing() {
        return Photos.viewPhotoShowing;
    }

    /**
     * set the logo
     */
    private static void setLogo() {
        Photos.logo = new Image("file:data/resources/logo.jpg");
    }

    /**
     * set the primary stage
     * @param primaryStage the primary stage
     */
    private static void setPrimaryStage(Stage primaryStage) {
        Photos.primaryStage = primaryStage;
    }

    /**
     * set the view photo stage
     * @param viewPhotoStage the view photo stage
     */
    private static void setViewPhotoStage(Stage viewPhotoStage) {
        Photos.viewPhotoStage = viewPhotoStage;
    }

    /**
     * set if primary stage is showing
     * @param primaryShowing is primary stage showing
     */
    private static void setPrimaryShowing(boolean primaryShowing) {
        Photos.primaryShowing = primaryShowing;
    }

    /**
     * set if view photo stage is showing
     * @param viewPhotoShowing is view photo stage showing
     */
    private static void setViewPhotoShowing(boolean viewPhotoShowing) {
        Photos.viewPhotoShowing = viewPhotoShowing;
    }

    /**
     * main method to run Photos application
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * start the Photos application
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        Model.init();
        setLogo();
        initShowing();
        initPrimaryStage(primaryStage);
        initViewPhotoStage();
        initCloseStageHandlers();
        changeScene("primary", "/stages/primary/main/main.fxml");
    }

    /**
     * stop the application after this method
     */
    @Override
    public void stop() {
        Model.persist();
    }

    /**
     * initialize showing variables
     */
    public static void initShowing() {
        setPrimaryShowing(false);
        setViewPhotoShowing(false);
    }

    /**
     * initialize the primary stage
     * @param primaryStage the primary stage
     */
    public static void initPrimaryStage(Stage primaryStage) {
        setPrimaryStage(primaryStage);
        primaryStage.getIcons().add(getLogo());
        primaryStage.setTitle("Photos Application");
        primaryStage.setResizable(true);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
    }

    /**
     * initialize the view photo stage
     */
    public static void initViewPhotoStage() {
        Stage viewPhotoStage = new Stage();
        viewPhotoStage.initModality(Modality.NONE);
        setViewPhotoStage(viewPhotoStage);
        viewPhotoStage.getIcons().add(getLogo());
        viewPhotoStage.setTitle("View Photo");
        viewPhotoStage.setResizable(true);
        viewPhotoStage.setWidth(1280);
        viewPhotoStage.setHeight(720);
    }

    /**
     * initialize the close stage event handlers
     */
    public static void initCloseStageHandlers() {
        getPrimaryStage().setOnCloseRequest((event) -> {
            setPrimaryShowing(false);
            Platform.exit();
        });
        getViewPhotoStage().setOnCloseRequest((event) -> {
            getViewPhotoStage().hide();
            setViewPhotoShowing(false);
        });
    }

    /**
     * show the primary stage
     */
    public static void showPrimaryStage() {
        if (isPrimaryShowing()) {
            return;
        }
        getPrimaryStage().show();
        setPrimaryShowing(true);
    }

    /**
     * show the view photo stage
     */
    public static void showViewPhotoStage() {
        if (isViewPhotoShowing()) {
            return;
        }
        getViewPhotoStage().show();
        setViewPhotoShowing(true);
    }

    /**
     * close the view photo stage
     */
    public static void closeViewPhotoStage() {
        if (!isViewPhotoShowing()) {
            return;
        }
        getViewPhotoStage().hide();
        setViewPhotoShowing(false);
    }

    /**
     * change the scene
     * @param stage the stage
     * @param newScene the new scene to change on the stage
     */
    public static void changeScene(String stage, String newScene) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Photos.class.getResource(newScene));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            if (stage.equals("primary")) {
                getPrimaryStage().setScene(scene);
                showPrimaryStage();
            } else if (stage.equals("viewphoto")) {
                getViewPhotoStage().setScene(scene);
                showViewPhotoStage();
            } else {
                throw new RuntimeException("incorrect stage");
            }
        } catch (Exception e) {
            throw new RuntimeException("can not change scene");
        }
    }
}


