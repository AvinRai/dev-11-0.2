package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.scene.input.MouseButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import java.io.IOException;



// Shows student's profile (form-style) at the top and their comments (tabular) at the bottom.
public class StudentReportDetailsController {

    // Profile header (form-style)
    @FXML private Label nameLabel;
    @FXML private Label academicLabel;
    @FXML private Label employmentLabel;
    @FXML private Label jobLabel;
    @FXML private Label whitelistLabel;
    @FXML private Label blacklistLabel;
    @FXML private Label roleLabel;
    @FXML private Label languagesLabel;
    @FXML private Label databasesLabel;

    // Comments table
    @FXML private TableView<StudentReportComment> commentTable;
    @FXML private TableColumn<StudentReportComment, String> dateColumn;
    @FXML private TableColumn<StudentReportComment, String> textColumn;

    private StudentProfile student;
    private Stage stage;

    @FXML
    private void initialize() {
        // Date in MM/dd
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDate()
                )
        );

        // Show only an excerpt of the comment in the table
        textColumn.setCellValueFactory(cellData -> {
            String full = cellData.getValue().getCommentText();
            if (full == null) full = "";
            String preview = full.length() > 60 ? full.substring(0, 60) + "..." : full;
            return new javafx.beans.property.SimpleStringProperty(preview);
        });
        // when clicking the comment table
        commentTable.setOnMouseClicked(this::onClickComment);
    }

    // when the comment table is being clicked
    private void onClickComment(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 1) {
            return;
        }

        // gets the selected comment
        StudentReportComment commentSelected = commentTable.getSelectionModel().getSelectedItem();
        // ensures a comment is selected
        if (commentSelected == null) {
            return;
        }

        // sends a message if failed to open the comment
        try {
            openCommentPage(commentSelected);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "failed to open comment: " + ex.getMessage()).showAndWait();
        }
    }

    // helps in creating the page for the full comment
    private void openCommentPage(StudentReportComment commentVal) throws IOException {
        // ensure that there is a comment
        if (commentVal == null) return;

        FXMLLoader loadVal = new FXMLLoader(Main.class.getResource("StudentCommentDetails.fxml"));
        Scene scene = new Scene(loadVal.load(), 500, 300);

        StudentCommentDetailsController commentController = loadVal.getController();

        Stage commentDetailStage = new Stage();
        String titleVal = "The Comment";
        // used to add the name of the student in the comment
        if (student != null && student.getFullName() != null && !student.getFullName().isBlank()) {
            titleVal += " for " + student.getFullName();
        }
        // adding the title and comment to the page
        commentDetailStage.setTitle(titleVal);
        commentDetailStage.setScene(scene);

        commentController.setStage(commentDetailStage);
        commentController.setComment(commentVal);

        // displaying the page
        commentDetailStage.show();
    }


    public void setStage(Stage s) {
        this.stage = s;
    }

    public void setStudent(StudentProfile sp) {
        this.student = sp;
        if (student == null) return;

        // Fill profile header
        nameLabel.setText(nonNull(student.getFullName()));
        academicLabel.setText(student.getAcademicStatus() != null
                ? student.getAcademicStatus().toString()
                : "");
        employmentLabel.setText(student.getEmploymentStatus() != null
                ? student.getEmploymentStatus().toString()
                : "");
        jobLabel.setText(nonNull(student.getJobDetails()));
        whitelistLabel.setText(student.isWhitelisted() ? "Yes" : "No");
        blacklistLabel.setText(student.isBlacklisted() ? "Yes" : "No");
        roleLabel.setText(student.getPreferredRole() != null
                ? student.getPreferredRole().toString()
                : "");
        languagesLabel.setText(String.join(", ", student.getLanguages()));
        databasesLabel.setText(String.join(", ", student.getDatabases()));

        // Load comments
        List<StudentReportComment> comments =
                StudentCommentRepository.getInstance().getCommentsFor(student.getId());

        ObservableList<StudentReportComment> data =
                FXCollections.observableArrayList(comments);
        commentTable.setItems(data);
    }

    private String nonNull(String s) {
        return s == null ? "" : s;
    }

    @FXML
    private void onClose() {
        if (stage != null) stage.close();
    }

    @FXML
    private void onAddComment() {
        if (student == null) {
            new Alert(Alert.AlertType.ERROR,
                    "A student hasn't been selected to add a comment").showAndWait();
            return;
        }

        // Same multi-line dialog behavior as ShowCommentsController
        Dialog<String> commentWindow = new Dialog<>();
        commentWindow.setTitle("Create New Comment");
        commentWindow.setHeaderText("Adding new comment for Student "
                + (student.getFullName() != null ? student.getFullName() : student.getId()));

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        commentWindow.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter the comment...");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        textArea.setPrefColumnCount(20);
        commentWindow.getDialogPane().setContent(textArea);

        commentWindow.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return textArea.getText();
            }
            return null;
        });

        Optional<String> resultChoice = commentWindow.showAndWait();
        if (resultChoice.isEmpty()) {
            return;
        }

        String commentText = resultChoice.get().trim();
        if (commentText.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "The new comment cannot be empty").showAndWait();
            return;
        }

        StudentReportComment commentVal = StudentReportComment.createComment(commentText);
        StudentCommentRepository.getInstance().addComment(student.getId(), commentVal);
        commentTable.getItems().add(commentVal);
    }
}
