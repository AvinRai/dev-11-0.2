package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    /*
    Button functionality to load Language define page
    Also close the active stage
     */

    @FXML
    protected void onProgramDefine(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("DefineLanguage.fxml")); // Updated to Main-page.fxml to language define page file
        Scene scene = new Scene(fxmlLoader.load(), 900, 230);
        Stage stage = new Stage();
        stage.setTitle("Programming Language Define");
        stage.setScene(scene);
        stage.show();

        Stage activestage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        activestage.close();
    }

     // Open the Show Stored Programming Languages page

    @FXML
    protected void onShowLanguages(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ShowLanguages.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
        Stage stage = new Stage();
        stage.setTitle("Stored Programming Languages");
        stage.setScene(scene);
        stage.show();
    }
}
