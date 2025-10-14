package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentProfileController {
    @FXML
    protected void GoBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 320);
        Stage stage = new Stage();
        stage.setTitle("Student Knowledge for Faculty");
        stage.setScene(scene);
        stage.show();

        Stage CurrentActiveStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CurrentActiveStage.close();
    }
}
