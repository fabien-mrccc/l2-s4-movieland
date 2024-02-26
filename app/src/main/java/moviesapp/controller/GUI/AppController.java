package moviesapp.controller.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import moviesapp.model.movies.Movie;
import moviesapp.viewer.buttons.*;
import moviesapp.viewer.left_panel.LeftPanelView;
import moviesapp.viewer.left_panel.WithTitlePanelView;
import moviesapp.viewer.left_panel.WithoutTitlePanelView;
import moviesapp.viewer.right_panel.ImagePanelView;
import moviesapp.viewer.right_panel.RightPanelView;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        turnOnSearchWithTitleMode();
        setGUIComponents();
    }

    private void setGUIComponents(){
        leftPanelViewGUI = new LeftPanelView(mainAnchorPane, leftPane, appTitle, selectModePane, withTitleButton, withoutTitleButton);

        withTitlePanelViewGUI = new WithTitlePanelView(leftPane, appTitle, titleAndSearchPane, title, searchBar, yearPane, yearLabel, yearField,
                favoritesWithTitlePane, favoritesWithTitleButton, goWithTitlePane, goWithTitleButton );

        withoutTitlePanelViewGUI = new WithoutTitlePanelView(leftPane, appTitle, yearsPane, years, from, singleOrMinYearField,
                to, maxYearField, genresPane, genres, ratingPane, rating, atLeast, ratingField, searchBar, genreListView);

        withoutTitleButtonsGUI = new WithoutTitleButtons(buttonsWithoutTitlePane, leftPane, ratingPane, favoritesWithoutTitleButton, goWithoutTitleButton);

        rightPanelViewGUI = new RightPanelView(leftPane, mainAnchorPane, rightStackPane, rightScrollPane);

        imagePanelViewGUI = new ImagePanelView(gridPane, rightScrollPane);
    }

    @FXML
    private void turnOnSearchWithTitleMode(){
        withTitlePane.setVisible(true);
        withTitlePane.setDisable(false);
        withoutTitlePane.setVisible(false);
        withoutTitlePane.setDisable(true);
    }

    @FXML
    private void turnOnSearchWithoutTitleMode(){
        withTitlePane.setVisible(false);
        withTitlePane.setDisable(true);
        withoutTitlePane.setVisible(true);
        withoutTitlePane.setDisable(false);
    }

    @FXML
    public void favoritesButtonClicked(){
        //imagePanelView.distributeImages(Favorites.instance.getFavorites());
    }

    public static void handleClickOnImage(Movie movie) {
        new DetailsButton(movie);
    }


    /////////////////////////////////////////////////////////// Begin GUI Components

    private LeftPanelView leftPanelViewGUI;
    private WithTitlePanelView withTitlePanelViewGUI;
    private FavoritesWithTitleButton favoritesWithTitleButtonGUI;
    private GoWithTitleButton goWithTitleButtonGUI;
    private WithoutTitlePanelView withoutTitlePanelViewGUI;
    private WithoutTitleButtons withoutTitleButtonsGUI;
    private RightPanelView rightPanelViewGUI;
    private ImagePanelView imagePanelViewGUI;

    /////////////////////////////////////////////////////////// End GUI Components


    /////////////////////////////////////////////////////////// Begin FXML Identifiers
    public AnchorPane mainAnchorPane;
    public Pane leftPane;
    public Label appTitle;
    public Pane titleAndSearchPane;
    public Label title;
    public TextField searchBar;
    public Label years;
    public Label from;
    public Label to;
    public TextField singleOrMinYearField;
    public TextField maxYearField;
    public Pane yearsPane;
    public Pane genresPane;
    public Pane ratingPane;
    public Label genres;
    public Label rating;
    public Label atLeast;
    public TextField ratingField;
    public Pane buttonsWithoutTitlePane;
    public Button goWithoutTitleButton;
    public Button favoritesWithoutTitleButton;
    public ScrollPane rightScrollPane;
    public StackPane rightStackPane;
    public Pane yearPane;
    public Label yearLabel;
    public TextField yearField;
    public Pane selectModePane;
    public Button withTitleButton;
    public Button withoutTitleButton;
    public Pane favoritesWithTitlePane;
    public Pane goWithTitlePane;
    public Button favoritesWithTitleButton;
    public Button goWithTitleButton;
    public Pane withTitlePane;
    public Pane withoutTitlePane;
    public ListView<String> genreListView;
    public GridPane gridPane;

    /////////////////////////////////////////////////////////// End FXML Identifiers
}
