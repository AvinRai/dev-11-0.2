package cs151.application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;


// This class is used to write to the 2 csv files
// (Student_Records.csv and Student_Skills.csv)
// Both of the files are coordinated using the id
public final class StudentProfileRepository {
    // used to get the paths for the files
    private static final Path DIR = Paths.get("data");
    private static final Path FILE_RECORDS = DIR.resolve("Student_Records.csv");
    private static final Path FILE_SKILLS  = DIR.resolve("Student_Skills.csv");

    // these are the headers for each of the csv files
    // respectively (Student_Records.csv and Student_Skills.csv)
    private static final String studentRecords = String.join(",",
            "id","fullName","academicStatus","employmentStatus","jobDetails","whitelisted","blacklisted");
    private static final String studentSkills = String.join(",",
            "id","preferredRole","languages","databases");

    // we will create the instance for the profile
    // helps to make sure that only one is being created/used at the same time
    private static final StudentProfileRepository instProfile = new StudentProfileRepository();
    public static StudentProfileRepository getInstance() { return instProfile; }
    private StudentProfileRepository() {}

    // used to add a new profile to both the csv files (append the new profile)
    public synchronized void saveProfile(StudentProfile profile) {
        ensureFiles();

        // used to check if a student already exists in the csv files
        if (nameExists(profile.getFullName())) {
            throw new IllegalArgumentException("A student profile with this name already exists.");
        }

        try (BufferedWriter wr = Files.newBufferedWriter(FILE_RECORDS, StandardOpenOption.APPEND);
             BufferedWriter ws = Files.newBufferedWriter(FILE_SKILLS,  StandardOpenOption.APPEND)) {

            // used to add the student's profile values to Student_Records.csv
            wr.write(String.join(",",
                    q(profile.getId()),
                    q(profile.getFullName()),
                    q(enumStr(profile.getAcademicStatus())),
                    q(enumStr(profile.getEmploymentStatus())),
                    q(profile.getJobDetails()),
                    String.valueOf(profile.isWhitelisted()),
                    String.valueOf(profile.isBlacklisted())));
            wr.newLine();

            // used to add the student's profile values to Student_Skills.csv
            ws.write(String.join(",",
                    q(profile.getId()),
                    q(enumStr(profile.getPreferredRole())),
                    q(join(profile.getLanguages())),
                    q(join(profile.getDatabases()))));
            ws.newLine();

        } catch (IOException e) {
            // Error message in case we have trouble saving the new profile
            throw new RuntimeException("Have failed to save the new student profile", e);
        }
    }

    // this function is to delete a single profile id
    // If the id is a valid id, it will convert it into a list
    // with one id to be used in deleteProfilesByIds
    // also using "synchronized" to make sure that files aren't accessed
    // at the same time
    public synchronized void deleteProfileById(String id) {
        if (id == null || id.isBlank()) return;
        // will make the single id into a list with one id
        deleteProfilesByIds(java.util.List.of(id));
    }

    // this function is used to delete multiple ids that were selected
    //
    public synchronized void deleteProfilesByIds(List<String> idsDelete) {
        // checks if the files exist
        ensureFiles();
        // checks if the ids are valid ids
        if (idsDelete == null || idsDelete.isEmpty()) return;

        Set<String> ids = new HashSet<>(idsDelete.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.replace("\"","").trim())
                .collect(java.util.stream.Collectors.toSet()));

        try {
            // used to rewrite the 2 csv files (with the ids
            // that need to be deleted removed from both files)
            rewriteFilesDeletingIds(FILE_RECORDS, studentRecords, ids);
            rewriteFilesDeletingIds(FILE_SKILLS,  studentSkills,  ids);
        } catch (IOException e) {
            // shows error message if failed to delete
            throw new RuntimeException("Have failed in deleting the student profiles selected.", e);
        }
    }

    // function used to rewrite the file without the ids needed to be deleted
    private static void rewriteFilesDeletingIds(Path file, String currentHeader, Set<String> ids) throws IOException {
        if (Files.notExists(file)) return;

        // creates the temporary file for having all the necessary changes.
        // We will have to swap the temporary and the old file,
        // only doing so when all changes have been added to the temporary file
        Path temp = Files.createTempFile(DIR, file.getFileName().toString(), ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(temp)) {
            // be used to write header
            w.write(currentHeader);
            w.newLine();

            // the following is used to add all the ids to the temporary file.
            // it will skip over the lines that contain the ids needed to be deleted
            // to make sure that the completed temporary file only has the
            // ids that should remain
            java.util.List<String> allLines = Files.readAllLines(file);
            for (String eachLine : allLines) {
                if (eachLine == null || eachLine.isBlank()) continue;
                if (eachLine.startsWith("id,")) continue; // header
                String[] fields = eachLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                String rowId = fields.length > 0 ? fields[0].replace("\"","").trim() : "";
                if (!ids.contains(rowId)) {
                    w.write(eachLine);
                    w.newLine();
                }
            }
        }
        // uses atomic to make sure that the entire temporary file is going to be
        // swapped with the old file (preventing the partial file data)
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }


    //the following is used to create the csv files if they don't already exist
    private static void ensureFiles() {
        try {
            if(Files.notExists(DIR)) Files.createDirectories(DIR);
            if (Files.notExists(FILE_RECORDS))
                Files.writeString(FILE_RECORDS, studentRecords + System.lineSeparator());
            if(Files.notExists(FILE_SKILLS))
                Files.writeString(FILE_SKILLS, studentSkills + System.lineSeparator());
        } catch (IOException e) {
            //an error message if we couldn't create the files
            throw new RuntimeException("Have failed to create both the csv files", e);
        }
    }

    // used to make the data for each profile fit in a cell (for the .csv)
    // takes a list as a parameter
    private static String join(List<String> xs) {
        return xs == null ? "" : xs.stream().map(String::trim).filter(s->!s.isEmpty())
                .collect(Collectors.joining("|"));
    }


    // used to get the enum as a string
    private static String enumStr(Enum<?> e) { return e == null ? "" : e.name(); }

    // used to make each field have quotations in the csv files
    // used for helping in readability
    private static String q(String s) {
        if (s == null) return "";
        String esc = s.replace("\"","\"\"");
        boolean quote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");

        return quote ? "\"" + esc + "\"" : esc;
    }

    // the method is used to check if a name already exists in the csv files
    // helps in preventing the same person from having another profile
    private static boolean nameExists(String fullName) {
        if (fullName == null || fullName.isBlank() || Files.notExists(FILE_RECORDS)) return false;
        String target = fullName.trim().toLowerCase();
        try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(FILE_RECORDS)) {
            return lines
                    .skip(1)
                    .map(line -> line.split(",", 3))
                    .filter(cols -> cols.length >= 2)
                    .map(cols -> cols[1].replace("\"","").trim().toLowerCase())
                    .anyMatch(target::equals);
        } catch (java.io.IOException e) {
            return false;
        }
    }
    public List<StudentProfile> getAllProfiles() {
        return StudentProfileReader.loadProfiles();
    }

    // used to update the student profile after the edits
    // It also checks if a name exists for students with different ids after edit
    // (to ensure that we don't change the name to a person who already exists
    // after making edits)
    public synchronized void updateProfile(StudentProfile profile) {
        ensureFiles();

        // Prevents duplicate names by checking the ids
        if (nameExistsForOtherId(profile.getFullName(), profile.getId())) {
            throw new IllegalArgumentException("Another student already uses this full name.");
        }

        // used for making the new rows after the edits
        String newRecordsRow = String.join(",",
                q(profile.getId()),
                q(profile.getFullName()),
                q(enumStr(profile.getAcademicStatus())),
                q(enumStr(profile.getEmploymentStatus())),
                q(profile.getJobDetails()),
                String.valueOf(profile.isWhitelisted()),
                String.valueOf(profile.isBlacklisted()));

        String newSkillsRow = String.join(",",
                q(profile.getId()),
                q(enumStr(profile.getPreferredRole())),
                q(join(profile.getLanguages())),
                q(join(profile.getDatabases())));

        // replaces row where the id is the same
        try {
            rewriteFileReplacingId(FILE_RECORDS, studentRecords, profile.getId(), newRecordsRow);
            rewriteFileReplacingId(FILE_SKILLS,  studentSkills,  profile.getId(), newSkillsRow);
        } catch (java.io.IOException e) {
            // check for what the error message is
            throw new RuntimeException("failed updating student profile: " + e.getMessage(), e);
        }
    }

    // used to find out if different student has the same name
    private static boolean nameExistsForOtherId(String fullName, String currentId) {
        if (fullName == null || fullName.isBlank() || java.nio.file.Files.notExists(FILE_RECORDS)) return false;
        //used in checking the name
        String checkName = fullName.trim().toLowerCase();

        // the id we are editing
        String editId = currentId == null ? "" : currentId.trim();

        // we also have this to ensure that the file is closed if an error occurs
        try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(FILE_RECORDS)) {
            return lines
                    .skip(1)
                    .map(line -> line.split(",", 3))
                    .filter(cols -> cols.length >= 2)
                    .anyMatch(cols -> {
                        String rowId = cols[0].replace("\"","").trim();
                        String name  = cols[1].replace("\"","").trim().toLowerCase();
                        return !rowId.equals(editId) && name.equals(checkName);
                    });
        } catch (java.io.IOException e) {
            return false;
        }
    }

    // use a temporary file for the edits to a student
    // after the edits have been made, we can swap the old file with
    // the temporary file
    private static void rewriteFileReplacingId(java.nio.file.Path file, String header,
                                               String id, String replacementRow) throws java.io.IOException {
        // if the file is not there, we will return
        if(java.nio.file.Files.notExists(file)) return;

        // makes the temporary file
        java.nio.file.Path temp = java.nio.file.Files.createTempFile(DIR, file.getFileName().toString(), ".tmp");
        // checks if the row was replaced
        boolean replace = false;

        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(temp)) {
            // writes the header
            w.write(header);
            w.newLine();

            for (String eachLine : java.nio.file.Files.readAllLines(file)) {
                if (eachLine == null || eachLine.isBlank()) continue;
                if (eachLine.startsWith("id,")) continue;
                String[] fieldVal = eachLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                String rowId = fieldVal.length > 0 ? fieldVal[0].replace("\"","").trim() : "";
                // checks if this is the row we need to replace for the edits
                if (rowId.equals(id)) {
                    w.write(replacementRow);
                    w.newLine();
                    replace = true;
                } else {
                    // leaves the row the same
                    w.write(eachLine);
                    w.newLine();
                }
            }
        }

        // If the name doesn't exist in the file, we will add the new row to the file
        if (!replace) {
            try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                    temp, java.nio.file.StandardOpenOption.APPEND)) {
                w.write(replacementRow);
                w.newLine();
            }
        }

        // used in replacing the old file with the temporary one
        safeReplace(temp, file);
    }

    // this method is for replacing the old file with the temporary one
    // Makes sure that atomic changes can work for the file change.
    // In case of errors, the temporary file with be deleted
    private static void safeReplace(java.nio.file.Path temp, java.nio.file.Path file) throws java.io.IOException {
        // used in keeping the last error
        java.io.IOException lastError = null;
        // tries to replace the file (will do this 3 times for now)
        // This was used in troubleshooting due to file change issue.
        // The following is used to try both regular saving and atomic saving for the files
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                try {
                    java.nio.file.Files.move(
                            temp, file,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                            java.nio.file.StandardCopyOption.ATOMIC_MOVE
                    );
                } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
                    java.nio.file.Files.move(
                            temp, file,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                }
                // able to move the file
                return;
            } catch (java.io.IOException e) {
                // keeps the error
                lastError = e;
                try { Thread.sleep(60); } catch (InterruptedException ignore){Thread.currentThread().interrupt();
                }
            }
        }
        // this was used to notify if the all the attempts did not work
        try { java.nio.file.Files.deleteIfExists(temp); } catch (java.io.IOException ignore) {}
        // shows the latest error
        throw (lastError != null ? lastError : new java.io.IOException("Failed to replace " + file));
    }

}
