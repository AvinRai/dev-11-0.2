package cs151.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class StudentReportComment {

    private final String commentText;
    private final String date;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("MM/dd");

    private StudentReportComment(LocalDate date, String text) {
        String trimmed = (text == null) ? "" : text.trim();

        if (trimmed.startsWith("[")) {
            int indexOfFirstBracket = text.indexOf('[');
            int indexOfLastBracket = text.indexOf(']');
            this.date = text.substring(indexOfFirstBracket + 1, indexOfLastBracket);
            this.commentText = trimmed;
        } else {
            this.date = date.format(FORMAT_DATE);
            this.commentText = "[" + this.date + "] " + trimmed;
        }
    }

    public static StudentReportComment createComment(String raw) {
        if (raw == null) {
            return new StudentReportComment(LocalDate.now(), "");
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return new StudentReportComment(LocalDate.now(), "");
        }

        if (trimmed.startsWith("[")) {
            return fromSavedString(trimmed);
        }

        return new StudentReportComment(LocalDate.now(), trimmed);
    }

    // getters
    public String getDate() {
        return date;
    }

    public String getCommentText() {
        return commentText;
    }

    public String dateWithComment() {
        return commentText;
    }

    public String dateWithCommentEscaped() {
        return dateWithComment().replace("\"", "");
    }


    public static StudentReportComment fromSavedString(String raw) {
        if (raw == null || raw.isBlank()) return null;

        String s = raw.trim();

        int firstBracket = s.indexOf('[');
        if (firstBracket > 0) {
            s = s.substring(firstBracket);
        }

        try {
            int end = s.indexOf(']');
            if (firstBracket == -1 || end <= 1) {
                return new StudentReportComment(LocalDate.now(), s);
            }

            String dateStr = s.substring(1, end).trim();
            String comment = s.substring(end + 1).trim();

            LocalDate parsed = LocalDate.parse(dateStr, FORMAT_DATE);

            return new StudentReportComment(parsed, comment);

        } catch (Exception e) {
            return new StudentReportComment(LocalDate.now(), s);
        }
    }
}

