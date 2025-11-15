package cs151.application;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;


//Does the saving and loading student comments from the CSV file.
//Comments are grouped by student ID.

public class StudentCommentRepository {

    private static final Path FILE_PATH = Paths.get("data", "Student_Observations.csv");

    // Only one instance of this class is used (singleton pattern)
    private static final StudentCommentRepository INSTANCE = new StudentCommentRepository();

    // Stores the comments in memory: studentId -> list of comments
    private final Map<String, List<StudentReportComment>> commentsById = new HashMap<>();

    public static StudentCommentRepository getInstance() {
        return INSTANCE;
    }

    private StudentCommentRepository() {
        loadComments();
    }

    //Reads the CSV file and loads all comments
    
    private void loadComments() {
        commentsById.clear();
        if (Files.notExists(FILE_PATH)) return;

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);

            for (String line : lines) {
                if (line.isBlank() || line.startsWith("id,")) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String id = parts[0].trim();
                String raw = parts[1].trim().replaceAll("[{}]", "");

                // Splits the saved list of comments
                String[] arr = raw.split("(?<!\\\\)\",\\s*\"");

                List<StudentReportComment> list =
                        Arrays.stream(arr)
                                .map(s -> s.replaceAll("^\"|\"$", "").trim())
                                .filter(s -> !s.isBlank())
                                .map(StudentReportComment::fromSavedString)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                commentsById.put(id, list);
            }

        } catch (IOException e) {
            System.out.println("Failed loading comments: " + e.getMessage());
        }
    }


    public List<StudentReportComment> getCommentsFor(String id) {
        return commentsById.getOrDefault(id, new ArrayList<>());
    }

    // Adds a new comment for a student and saves the file

    public void addComment(String id, StudentReportComment comment) {
        if (id == null || id.isBlank() || comment == null) return;

        commentsById.computeIfAbsent(id, key -> new ArrayList<>()).add(comment);
        saveComments();
    }


     // Writes all the stored comments back in the CSV file.
    private void saveComments() {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {

            writer.write("id, comments\n");

            for (var entry : commentsById.entrySet()) {
                String id = entry.getKey();

                // Converts the list of comments into a single CSV-safe string
                String comments = entry.getValue().stream()
                        .map(StudentReportComment::dateWithCommentEscaped)
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.joining(", ", "{", "}"));

                writer.write(id + ", " + comments + "\n");
            }

        } catch (IOException e) {
            System.out.println("Error saving comments: " + e.getMessage());
        }
    }
}
