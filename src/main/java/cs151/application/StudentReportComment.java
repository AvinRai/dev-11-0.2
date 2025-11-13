package cs151.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//the class is used to create the comments for students.
//the professor’s comments will be used to report each
// student’s progress, including the date (with no time).
public final class StudentReportComment {

    // what the instructor wrote
    private final String commentText;
    // the date it was written (no time component)
    private final LocalDate date;

    // used to make the date in the MM/dd format
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("MM/dd");

    // constructor used to initialize the values
    private StudentReportComment(LocalDate date, String text) {
        this.date = date;
        commentText = text;
    }

    // used to create new comment
    public static StudentReportComment createComment(String originalText) {
        String trimmedText = originalText == null ? "" : originalText.trim();
        return new StudentReportComment(LocalDate.now(), trimmedText);
    }

    // have getters for the date and comment
    public LocalDate getDate() { return date; }
    public String getCommentText() { return commentText; }

    //give the date and comment in a formatted manner
    public String dateWithComment() {
        return "[" + date.format(FORMAT_DATE) + "] " + commentText;
    }
}
