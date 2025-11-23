package cs151.application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

// this is the controller for displaying the full comment
public class StudentCommentDetailsController {

    @FXML private Label date;
    @FXML private TextArea commentTextArea;
    private Stage stageVal;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("MM/dd");

    public void setStage(Stage stage) {
        stageVal = stage;
    }

    // use to set the values to the page
    // add comment to the text area
    public void setComment(StudentReportComment comment) {
        if (comment == null) return;

        // gets the text of the comment
        // then adds the comment text to the text area
        String commentText = comment.getCommentText();
        commentTextArea.setText(commentText == null ? "" : commentText);
    }

    // when closing page
    @FXML
    private void onClose() {
        if (stageVal != null) {
            stageVal.close();
        }
    }
}
