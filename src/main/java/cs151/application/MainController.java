package cs151.application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    protected void onProgramDefine(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml")); // Update hello-view.fxml to language define page file
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = new Stage();
        stage.setTitle("Programming Language Define");
        stage.setScene(scene);
        stage.show();
    }
}
