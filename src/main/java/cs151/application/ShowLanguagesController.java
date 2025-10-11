package cs151.application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;


public class ShowLanguagesController {

    @FXML
    private TableView<String> languageTable;

    @FXML
    private TableColumn<String, String> languageColumn;

    @FXML
    public void initialize() {
        LanguagesData.getInstance().loadLanguagesFromFile();

        ObservableList<String> data = LanguagesData.getInstance().getLanguages();

        // Creates table column
        languageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue()));

        // Shows the list
        languageTable.setItems(data);
    }
}
