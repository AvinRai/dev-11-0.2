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

    
    // Makes the comment safe to store in the CSV file.
   
    public String dateWithCommentEscaped() {
        return dateWithComment().replace("\"", "\\\"");
    }


    // Changes the one saved comment line from the CSV file back to an object
    public static StudentReportComment fromSavedString(String raw) {
        if (raw == null || raw.isBlank()) return null;

        try {
            // extract date from the beginning
            int end = raw.indexOf("]");
            String dateStr = raw.substring(1, end).trim();

            // remaining text is the comment
            String comment = raw.substring(end + 1).trim();

            LocalDate parsed = LocalDate.parse(dateStr, FORMAT_DATE);

            return new StudentReportComment(parsed, comment);

        } catch (Exception e) {
            return new StudentReportComment(LocalDate.now(), raw);
        }
    }

}
