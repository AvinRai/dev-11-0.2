package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The DefineLangPage page is used to define the programming languages used.
 *  This class uses javafx and the page contains text box to enter the programming language.
 *  This page also has buttons to save/cancel the language information.
 *  Refered from "GUI Programming Part 1 to 3" on the Professor's Youtube channel for learning
 *  the GUI.
 */

public class DefineLangPage extends Application {
    // Used to set the title of the page
    private static final String pageTitle = "DEFINE PROGRAMMING LANGUAGES";

    // We need to override this method to define our page
    @Override
    public void start(Stage primaryStage) {
        // Use a try catch block for testing or in case any errors arises
        try {
            // Creates currentScene which also sets the width and height
            FXMLLoader fxmlLoad = new FXMLLoader(DefineLangPage.class.getResource("DefineLanguage.fxml"));
            Scene currentScene = new Scene(fxmlLoad.load(), 900, 230);

            // We also add the page title and display the page
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
