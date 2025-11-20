package cs151.application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void initialize() {

        studentrow.setItems(data);
        try {
            Files.lines(Paths.get("data/Student_Records.csv"))
                    .skip(1)
                    .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)) // Split CSV
                    .map(fields -> new StudentRow(
                            clean(fields[1]),
                            clean(fields[2]),
                            clean(fields[3]),
                            clean(fields[4]),
                            parseBoolean(clean(fields[5])),
                            parseBoolean(clean(fields[6]))
                    ))
                    .sorted((a, b) -> a.fullName.get().compareToIgnoreCase(b.fullName.get()))
                    .forEach(masterList::add);
        } catch (IOException e) {
            System.out.println("Failed to load student records: " + e.getMessage());
        }

        nameColumn.setCellValueFactory(cell -> cell.getValue().fullName);
        academicColumn.setCellValueFactory(cell -> cell.getValue().academicStatus);
        employmentColumn.setCellValueFactory(cell -> cell.getValue().employmentStatus);
        jobColumn.setCellValueFactory(cell -> cell.getValue().jobDetails);
        whitelistColumn.setCellValueFactory(cell -> cell.getValue().whitelisted);
        blacklistColumn.setCellValueFactory(cell -> cell.getValue().blacklisted);
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


    // This shows a row in the table
    public static class StudentRow {
        SimpleStringProperty fullName, academicStatus, employmentStatus, jobDetails;
        SimpleBooleanProperty whitelisted, blacklisted;

        public StudentRow(String fullName, String academicStatus, String employmentStatus, String jobDetails, boolean whitelisted, boolean blacklisted) {
            this.fullName = new SimpleStringProperty(fullName);
            this.academicStatus = new SimpleStringProperty(academicStatus);
            this.employmentStatus = new SimpleStringProperty(employmentStatus);
            this.jobDetails = new SimpleStringProperty(jobDetails);
            this.whitelisted = new SimpleBooleanProperty(whitelisted);
            this.blacklisted = new SimpleBooleanProperty(blacklisted);
        }

        public boolean isBlacklisted(){
            return blacklisted.get();
        }
        public boolean isWhitelisted(){
            return whitelisted.get();
        }
    }
}
