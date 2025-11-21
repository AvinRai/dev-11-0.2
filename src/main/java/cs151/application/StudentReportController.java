package cs151.application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

public class StudentReportController {

    @FXML
    private TableView<StudentRow> studentrow;

    @FXML
    private TableColumn<StudentRow, String> nameColumn;

    @FXML
    private TableColumn<StudentRow, String> academicColumn;

    @FXML
    private TableColumn<StudentRow, String> employmentColumn;

    @FXML
    private TableColumn<StudentRow, String> jobColumn;

    @FXML
    private TableColumn<StudentRow, Boolean> whitelistColumn;

    @FXML
    private TableColumn<StudentRow, Boolean> blacklistColumn;

    private static String clean(String s) {
        return s == null ? "" : s.replace("\"", "").trim();
    }

    private final ObservableList<StudentRow> masterList = FXCollections.observableArrayList();
    private final ObservableList<StudentRow> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        studentrow.setItems(data);

        // Load all rows from Student_Records.csv into masterList
        masterList.clear();
        try {
            Files.lines(Paths.get("data/Student_Records.csv"))
                    .skip(1) // header
                    .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
                    .map(fields -> new StudentRow(
                            clean(fields[0]), // id
                            clean(fields[1]), // full name
                            clean(fields[2]), // academic
                            clean(fields[3]), // employment
                            clean(fields[4]), // job details
                            parseBoolean(clean(fields[5])), // whitelisted
                            parseBoolean(clean(fields[6]))  // blacklisted
                    ))
                    .sorted(Comparator.comparing(row -> row.fullName.get().toLowerCase()))
                    .forEach(masterList::add);
        } catch (IOException e) {
            System.out.println("Failed to load student records: " + e.getMessage());
        }

        data.setAll(masterList);

        // Double-click on a row -> open new combined profile + comments view
        studentrow.setOnMouseClicked(this::handleRowDoubleClick);

        // Column bindings
        nameColumn.setCellValueFactory(cell -> cell.getValue().fullName);
        academicColumn.setCellValueFactory(cell -> cell.getValue().academicStatus);
        employmentColumn.setCellValueFactory(cell -> cell.getValue().employmentStatus);
        jobColumn.setCellValueFactory(cell -> cell.getValue().jobDetails);
        whitelistColumn.setCellValueFactory(cell -> cell.getValue().whitelisted);
        blacklistColumn.setCellValueFactory(cell -> cell.getValue().blacklisted);
    }

    private void handleRowDoubleClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 2) {
            return;
        }

        StudentRow selected = studentrow.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        StudentProfile profile = StudentProfileRepository.getInstance().getAllProfiles()
                .stream()
                .filter(p -> p.getId().equals(selected.getId()))
                .findFirst()
                .orElse(null);

        if (profile == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Could not find full profile for selected student.").showAndWait();
            return;
        }

        try {
            openStudentDetailsPage(profile);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Failed to open student details: " + ex.getMessage()).showAndWait();
        }
    }

    private void openStudentDetailsPage(StudentProfile studentProfile) throws IOException {
        if (studentProfile == null) return;

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("StudentReportDetails.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);

        StudentReportDetailsController ctrl = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Student: " +
                (studentProfile.getFullName() != null
                        ? studentProfile.getFullName()
                        : studentProfile.getId()));
        stage.setScene(scene);

        ctrl.setStage(stage);
        ctrl.setStudent(studentProfile);

        stage.show();
    }

    @FXML
    private void onBlacklistClicked(ActionEvent event) {
        // Replace current 'data' with only students where isBlacklisted() == true
        data.setAll(
                masterList.stream()
                        .filter(StudentRow::isBlacklisted)
                        .toList()
        );
    }

    @FXML
    private void onWhitelistedClicked(ActionEvent event) {
        // Replace 'data' with only the rows from masterList where isWhitelisted() == true
        data.setAll(
                masterList.stream()
                        .filter(StudentRow::isWhitelisted)
                        .toList()
        );
    }

    private boolean parseBoolean(String s) {
        return !s.equalsIgnoreCase("false");
    }

    // Represents a row in the report table
    public static class StudentRow {
        private final String id;
        SimpleStringProperty fullName, academicStatus, employmentStatus, jobDetails;
        SimpleBooleanProperty whitelisted, blacklisted;

        public StudentRow(String id,
                          String fullName,
                          String academicStatus,
                          String employmentStatus,
                          String jobDetails,
                          boolean whitelisted,
                          boolean blacklisted) {
            this.id = id;
            this.fullName = new SimpleStringProperty(fullName);
            this.academicStatus = new SimpleStringProperty(academicStatus);
            this.employmentStatus = new SimpleStringProperty(employmentStatus);
            this.jobDetails = new SimpleStringProperty(jobDetails);
            this.whitelisted = new SimpleBooleanProperty(whitelisted);
            this.blacklisted = new SimpleBooleanProperty(blacklisted);
        }

        public String getId() {
            return id;
        }

        public boolean isBlacklisted() {
            return blacklisted.get();
        }

        public boolean isWhitelisted() {
            return whitelisted.get();
        }
    }
}
