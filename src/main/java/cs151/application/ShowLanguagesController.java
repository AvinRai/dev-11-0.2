package cs151.application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;


public class ShowLanguagesController {

    // Reference to the TableView in the FXML file
    @FXML
    private TableView<String> languageTable;

    // The column that will show each programming language
    @FXML
    private TableColumn<String, String> languageColumn;

    @FXML
    public void initialize() {
        // Load saved languages from file into memory
        LanguagesData.getInstance().loadLanguagesFromFile();

        // Get the in-memory list of languages
        ObservableList<String> data = LanguagesData.getInstance().getLanguages();

        // Set up the table column to show the actual string values
        languageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue()));

        // Display the list in the table
        languageTable.setItems(data);
    }
}
