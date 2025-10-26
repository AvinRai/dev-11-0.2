package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

public class SearchStudentsController {
    
    @FXML private TextField nameField;
    @FXML private ListView<String> languageList;
    @FXML private ListView<String> databaseList;

    // TableView & Columns
    @FXML private TableView<StudentProfile> resultsTable;
    @FXML private TableColumn<StudentProfile, String> nameColumn;
    @FXML private TableColumn<StudentProfile, String> statusColumn;
    @FXML private TableColumn<StudentProfile, String> roleColumn;

    @FXML
    private void initialize() {
        // Allow selecting multiple items in both list views
        languageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databaseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Set how table columns display student data
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getAcademicStatus() != null
                                ? cellData.getValue().getAcademicStatus().toString()
                                : ""));

        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getPreferredRole() != null
                                ? cellData.getValue().getPreferredRole().toString()
                                : ""));
    }

    // How the back button works
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

    // How the Search button works
    @FXML
    protected void OnSearch(ActionEvent event) {
        // Load all profiles
        ObservableList<StudentProfile> allProfiles = FXCollections.observableArrayList(
                StudentProfileRepository.getInstance().getAllProfiles()
        );

        String nameInput = nameField.getText().trim().toLowerCase();

        ObservableList<StudentProfile> filtered = allProfiles.filtered(profile ->
                nameInput.isEmpty() || profile.getFullName().toLowerCase().contains(nameInput)
        );

        resultsTable.setItems(filtered);
    }

    // used when delete is selected
    @FXML
    protected void OnDeleteSelected(ActionEvent event) {
        // used to get which rows are currently being selected for deletion
        ObservableList<StudentProfile> selectedVal = resultsTable.getSelectionModel().getSelectedItems();
        // sends alert if nothing was selected to delete
        if (selectedVal == null || selectedVal.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Must select AT LEAST one row to delete").showAndWait();
            return;
        }
        // Sends alert to be used to verify whether to delete ids
        Alert check = new Alert(Alert.AlertType.CONFIRMATION,
                "Going to delete selected student profile(s)? This procedure is permanent.",
                ButtonType.OK, ButtonType.CANCEL);
        java.util.Optional<ButtonType> resultOfCheck = check.showAndWait();
        if (resultOfCheck.isEmpty() || resultOfCheck.get() != ButtonType.OK) return;

        //used to get the selected rows and only takes the ids of the rows
        java.util.List<StudentProfile> toDelete = new java.util.ArrayList<>(selectedVal);
        java.util.List<String> ids = toDelete.stream().map(StudentProfile::getId).toList();

        try {
            // calls to delete the ids from the files
            StudentProfileRepository.getInstance().deleteProfilesByIds(ids);
            // used to refresh, things that were deleted are not seen
            OnSearch(null);
            // Sends a message alert to show number of filed deleted
            new Alert(Alert.AlertType.INFORMATION,
                    "Have deleted " + toDelete.size() + " profile(s).").showAndWait();
        } catch (RuntimeException ex) {
            // shows alert message if failed to delete something
            new Alert(Alert.AlertType.ERROR, "Have failed to delete: " + ex.getMessage()).showAndWait();
        }

    }

}
