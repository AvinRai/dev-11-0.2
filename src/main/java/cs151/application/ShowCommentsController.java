package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;


 // This shows all comments that are written for a specific student.

public class ShowCommentsController {

    @FXML private TableView<StudentReportComment> commentTable;
    @FXML private TableColumn<StudentReportComment, String> dateColumn;
    @FXML private TableColumn<StudentReportComment, String> textColumn;

    private StudentProfile student;
    private Stage stage;

    @FXML
    private void initialize() {
        // Display the date column in MM/dd format
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd"))
                )
        );

        // Display the text part of the comment
        textColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCommentText()
                )
        );
    }

    // Saves the current window

    public void setStage(Stage s) {
        this.stage = s;
    }


    // This loads the comments for the student and fills the table out.
    public void setStudent(StudentProfile sp) {
        this.student = sp;
        if (student == null) return;

        List<StudentReportComment> comments =
                StudentCommentRepository.getInstance().getCommentsFor(student.getId());

        ObservableList<StudentReportComment> data = FXCollections.observableArrayList(comments);
        commentTable.setItems(data);
    }

    @FXML
    private void onClose() {
        if (stage != null) stage.close();
    }
}
