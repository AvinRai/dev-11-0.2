package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    // used to add new comments to a student
    @FXML
    private void onAddComment() {
        // checks if we have selected a student for adding a comment
        if (student == null) {
            new Alert(Alert.AlertType.ERROR,
                    "A student hasn't been selected to add a comment").showAndWait();
            return;
        }

        // creates a new space to type the new comment for the student
        TextInputDialog commentWindow = new TextInputDialog();
        commentWindow.setTitle("Create New Comment");
        commentWindow.setHeaderText("Adding new comment for Student "
                + (student.getFullName() != null ? student.getFullName() : student.getId()));
        commentWindow.setContentText("Enter The Comment:");

        // gets the user's input (Ok/Cancel button)
        Optional<String> resultChoice = commentWindow.showAndWait();
        // checks if the user pressed cancel button
        if (resultChoice.isEmpty()) {
            return;
        }

        // trims and gets the comment
        String commentText = resultChoice.get().trim();
        // checks if the new comment is blank
        if (commentText.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "The new comment cannot be empty").showAndWait();
            return;
        }

        // creates a new comment
        StudentReportComment commentVal = StudentReportComment.createComment(commentText);

        // adds the comment to the csv file
        StudentCommentRepository.getInstance().addComment(student.getId(), commentVal);

        // will refresh the table to show changes
        commentTable.getItems().add(commentVal);
    }

}
