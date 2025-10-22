package cs151.application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;


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
        try {
            // ignores spaces and is not case-sensitive
            return Files.lines(FILE_RECORDS)
                    .skip(1) // header
                    .map(line -> line.split(",", 3))
                    .filter(cols -> cols.length >= 2)
                    .map(cols -> cols[1].replace("\"","").trim().toLowerCase())
                    .anyMatch(target::equals);
        } catch (IOException e) {
            return false;
        }
    }
    public List<StudentProfile> getAllProfiles() {
        return StudentProfileReader.loadProfiles();
    }
}
