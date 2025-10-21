package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import java.io.IOException;

public class SearchStudentsController {
    @FXML private ListView<String> languageList;
    @FXML private ListView<String> databaseList;

    @FXML
    private void initialize() {
        languageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databaseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

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

    @FXML
    protected void OnSearch(ActionEvent event) throws IOException {
    }
}
