package cs151.application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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

    @FXML
    private ListView<String> languagelist;

    @FXML
    private CheckBox WhiteList;

    @FXML
    private  CheckBox BlackList;

    @FXML
    public void initialize(){
        LanguagesData.getInstance().loadLanguagesFromFile();

        ObservableList<String> data = LanguagesData.getInstance().getLanguages();

        languagelist.setItems(data);
        languagelist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        WhiteList.selectedProperty().addListener((obs, oldVal, newVal) ->{
            if(newVal) BlackList.setSelected(false);
        });

        BlackList.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) WhiteList.setSelected(false);
        });

    }




}
