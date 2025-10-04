package s25.cs151.helloworld;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;

/**
 * The DefineLangPage page is used to define the programming languages used.
 *  This class uses javafx and the page contains text box to enter the programming language.
 *  This page also has buttons to save/cancel the language information.
 */

public class DefineLangPage extends Application {

    // Used to set the title of the page
    private static final String pageTitle = "DEFINE PROGRAMMING LANGUAGES";

    // We need to override this method to define our page
    @Override
    public void start(Stage primaryStage) {
        // Use a try catch block for testing or in case any errors arises
        try {
            // We will use VBox to add each of the nodes to the root (happens vertically)
            // Sets the spacing and padding as 15
            VBox root = new VBox(15);
            root.setPadding(new Insets(15));

            // Creates a HBox called languageRow with spacing as 10
            // We also position it center-left
            HBox languageRow = new HBox(10);
            languageRow.setAlignment(Pos.CENTER_LEFT);

            //Used to create a label called name that is next to the text area
            Label name = new Label("NAMES:");

            // Creates TextArea called languageEntry.
            // It sets the displayed size of the text area and allows the
            // text field to increase as more text is entered
            TextArea languageEntry = new TextArea();
            languageEntry.setPrefRowCount(7);
            languageEntry.setMaxWidth(Double.MAX_VALUE);
            languageEntry.setMaxHeight(Double.MAX_VALUE);
            // Used to take up the remaining space in the Hbox row
            HBox.setHgrow(languageEntry, Priority.ALWAYS);

            // Adds the label and text area to languageRow
            languageRow.getChildren().addAll(name, languageEntry);

            // Creates a HBox called buttonRow with spacing as 10 and padding as 15
            // We also position it center-right
            HBox buttonRow = new HBox(10);
            buttonRow.setAlignment(Pos.CENTER_RIGHT);
            buttonRow.setPadding(new Insets(15));
            // We created 2 buttons for saving and canceling
            // the programming language information.
            Button cancelInfo = new Button("Cancel");
            Button saveInfo   = new Button("Save information");

            // Adds both save/cancel buttons to the HBox
            buttonRow.getChildren().addAll(cancelInfo, saveInfo);

            // Add the languageRow and buttonRow to the Vbox root
            // Ensures it gets added in the vertical order
            root.getChildren().addAll(languageRow, buttonRow);

            // Creates currentScene which adds the root as well as sets the width and height
            // We also add the page title and display the page
            Scene currentScene = new Scene(root, 900, 230);
            primaryStage.setTitle(pageTitle);
            primaryStage.setScene(currentScene);
            primaryStage.show();
        } catch (Exception e) {
            // The catch part will notify if an error happened
            e.printStackTrace();
        }
    }
    // The main will run the page defined in the start method
    public static void main(String[] args) {
        launch(args);
    }
}