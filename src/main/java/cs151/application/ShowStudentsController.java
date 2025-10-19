package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShowStudentsController {
  
    // Table and columns from the FXML
    @FXML
    private TableView<StudentRecordRow> studentTable;

    @FXML
    private TableColumn<StudentRecordRow, String> nameColumn;

    @FXML
    private TableColumn<StudentRecordRow, String> academicColumn;

    @FXML
    private TableColumn<StudentRecordRow, String> employmentColumn;

    @FXML
    private TableColumn<StudentRecordRow, String> jobColumn;

    @FXML
    private TableColumn<StudentRecordRow, String> whitelistColumn;

    @FXML
    private TableColumn<StudentRecordRow, String> blacklistColumn;

    @FXML
    public void initialize() {
        ObservableList<StudentRecordRow> data = FXCollections.observableArrayList();

        try {
            Files.lines(Paths.get("data/Student_Records.csv"))
                    .skip(1) 
                    .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)) // Split CSV
                    .map(fields -> new StudentRecordRow(
                            clean(fields[1]),
                            clean(fields[2]),
                            clean(fields[3]),
                            clean(fields[4]),
                            clean(fields[5]),
                            clean(fields[6])
                    ))
                    .sorted((a, b) -> a.fullName.get().compareToIgnoreCase(b.fullName.get()))
                    .forEach(data::add);
        } catch (IOException e) {
            System.out.println("Failed to load student records: " + e.getMessage());
        }

        nameColumn.setCellValueFactory(cell -> cell.getValue().fullName);
        academicColumn.setCellValueFactory(cell -> cell.getValue().academicStatus);
        employmentColumn.setCellValueFactory(cell -> cell.getValue().employmentStatus);
        jobColumn.setCellValueFactory(cell -> cell.getValue().jobDetails);
        whitelistColumn.setCellValueFactory(cell -> cell.getValue().whitelisted);
        blacklistColumn.setCellValueFactory(cell -> cell.getValue().blacklisted);

        studentTable.setItems(data);
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace("\"", "").trim();
    }

    // This shows a row in the table
    public static class StudentRecordRow {
        SimpleStringProperty fullName, academicStatus, employmentStatus, jobDetails, whitelisted, blacklisted;

        public StudentRecordRow(String fullName, String academicStatus, String employmentStatus, String jobDetails, String whitelisted, String blacklisted) {
            this.fullName = new SimpleStringProperty(fullName);
            this.academicStatus = new SimpleStringProperty(academicStatus);
            this.employmentStatus = new SimpleStringProperty(employmentStatus);
            this.jobDetails = new SimpleStringProperty(jobDetails);
            this.whitelisted = new SimpleStringProperty(whitelisted);
            this.blacklisted = new SimpleStringProperty(blacklisted);
        }
    }
}
