package moviesapp.viewer;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import moviesapp.model.movies.Favorites;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionButton {
    @FXML private Button clearButton;
    @FXML private Scene globalScene;
    @FXML private Stage globalStage;
    @FXML private Button continueButton;
    @FXML private Button cancelButton;
    @FXML private Label confirmation;
    @FXML private AnchorPane clearConfirmationAnchorPane;

    public void initialize(URL location, ResourceBundle resourceBundle) {}

    /**
     * Create a new window in which a confirmation is required to clear the list of favorites
     */
    @FXML
    public void openClearConfirmationWindow() {
        globalStage = new Stage();
        clearConfirmationAnchorPane = new AnchorPane();
        clearConfirmationAnchorPane.prefHeight(100);
        clearConfirmationAnchorPane.prefWidth(230);

        initConfirmation();
        initContinueButton();
        initCancelButton();

        clearConfirmationAnchorPane.getChildren().add(confirmation);
        clearConfirmationAnchorPane.setStyle("-fx-background-color: #3D3D3D");
        clearConfirmationAnchorPane.getChildren().add(continueButton);
        clearConfirmationAnchorPane.getChildren().add(cancelButton);
        clearConfirmationAnchorPane.setVisible(true);
        globalScene = new Scene(clearConfirmationAnchorPane, 230, 100);
        globalStage.setTitle("Confirmation");
        globalStage.setScene(globalScene);
        globalStage.show();
    }

    @FXML
    private void initContinueButton(){
        continueButton = new Button("continue");
        continueButton.setLayoutX(20);
        continueButton.setLayoutY(60);
        continueButton.setPrefWidth(70);
        continueButton.setTextFill(Paint.valueOf("white"));
        continueButton.setStyle("-fx-background-color: #E50914;");
        continueButton.setOnAction(event -> continueButtonClicked());
    }

    @FXML
    private void initCancelButton(){
        cancelButton = new Button("cancel");
        cancelButton.setLayoutX(140);
        cancelButton.setLayoutY(60);
        cancelButton.setPrefWidth(70);
        cancelButton.setTextFill(Paint.valueOf("white"));
        cancelButton.setStyle("-fx-background-color: #E50914;");
        cancelButton.setOnAction(event -> closeClearConfirmationWindow());
    }

    @FXML
    private void initConfirmation(){
        confirmation = new Label("Do you want to continue?");
        confirmation.setLayoutX(20);
        confirmation.setLayoutY(15);
        confirmation.setTextFill(Paint.valueOf("white"));
        confirmation.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 17px;");
    }

    /**
     * close the confirmation window when the user click on "cancel"
     */
    @FXML
    public void closeClearConfirmationWindow(){
        globalStage.close();
    }

    @FXML
    public void continueButtonClicked(){
        Favorites.instance.clear();
        // TODO: add Numa's methode to update the user interface
        closeClearConfirmationWindow();
    }

    @FXML
    public Button getContinueButton(){
        return continueButton;
    }
}
