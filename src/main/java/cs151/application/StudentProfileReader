package cs151.application;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// the class is used to read the 2 files values (student records and skills).
// it then combines the data using their ids, and finally will return the
// student profiles to be displayed (is used as the results of the search
// feature in the UI)
final class StudentProfileReader {
    //gets the file paths
    private static final Path dirPath = Paths.get("data");
    private static final Path studentFileRecords = dirPath.resolve("Student_Records.csv");
    private static final Path studentFileSkills = dirPath.resolve("Student_Skills.csv");

    //the function is used to send the student profiles
    static List<StudentProfile> loadProfiles() {
        // reads the skill values
        Map<String, Skills> skillsUsingId = readSkillsById();
        List<StudentProfile> resultVal = new ArrayList<>();
        // checks if the file exists
        if (Files.notExists(studentFileRecords)) return resultVal;

        try{
            // reads each line is the student records file
            for (String eachLine : Files.readAllLines(studentFileRecords)) {
                // skip the lines if they are empty
                if (eachLine == null || eachLine.isBlank()) continue;
                //used to skip the header
                if (eachLine.startsWith("id,")) continue;

                // splits the csv files into columns
                String[] eachVal = splitTheCSV(eachLine);
                if (eachVal.length < 7) continue;

                // used to get the name, id, academic status, employment,
                // whether whitelisted/blacklisted
                String id= format(eachVal[0]);
                String name = format(eachVal[1]);
                StudentProfile.AcademicStatus acad = parseAcademic(format(eachVal[2]));
                StudentProfile.EmploymentStatus empl = parseEmployment(format(eachVal[3]));
                String job = format(eachVal[4]);
                boolean wl = parseBool(format(eachVal[5]));
                boolean bl = parseBool(format(eachVal[6]));

                //uses id to get the skills as well
                Skills studentSkills = skillsUsingId.getOrDefault(id, new Skills(null, List.of(), List.of()));
                StudentProfile.PreferredRole role = parseRole(studentSkills.role);

                // adds the values to the result
                resultVal.add(new StudentProfile(
                        id, name, acad, empl, job, studentSkills.languages, studentSkills.databases, role, wl, bl));
            }
        } catch (IOException ignored) {}
        // we can ignore error exception, and just try to get all the data we can get for now
        // returns the student profiles
        return resultVal;
    }


    // used to hold the skills values (in the row)
    private static class Skills {
        final String role; final List<String> languages; final List<String> databases;
        Skills(String role, List<String> languages, List<String> databases) {
            this.role = role; 
            this.languages = languages; this.databases = databases;
        }
    }

    // used to read the skills using the id
    private static Map<String, Skills> readSkillsById() {
        // returns a blank map is there are no skills added
        Map<String, Skills> map = new HashMap<>();
        if (Files.notExists(studentFileSkills)) return map;
        try {
            for (String eachLine : Files.readAllLines(studentFileSkills)) {
                if (eachLine == null || eachLine.isBlank()) continue;
                if (eachLine.startsWith("id,")) continue;

                //splits the csv file into columns
                String[] eachVal = splitTheCSV(eachLine);
                if (eachVal.length < 4) continue;

                //gets the skill values and puts them in the map
                String id =format(eachVal[0]);
                String role = format(eachVal[1]);
                List<String> lang = split(format(eachVal[2]));
                List<String> dbs= split(format(eachVal[3]));
                map.put(id, new Skills(role, lang, dbs));
            }
        } catch (IOException ignored) {}
        // used to return the map
        return map;
    }

    // the function splits the csv into columns
    private static String[] splitTheCSV(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
    // Used to trim any of the spaces as well as remove the quotes
    private static String format(String s) { return s == null ? "" : s.replace("\"","").trim(); }
    //gets the boolean value
    private static boolean parseBool(String s) { return "true".equalsIgnoreCase(s); }
    // used to turn the string into the list
    private static List<String> split(String s) {
        if (s == null || s.isBlank()) return new ArrayList<>();
        return Arrays.stream(s.split("\\|"))
                .map(String::trim).filter(t -> !t.isEmpty()).collect(Collectors.toList());
    }

    //used to get the academic status
    private static StudentProfile.AcademicStatus parseAcademic(String s) {
        return switch (s) {
            case "Freshman"  -> StudentProfile.AcademicStatus.Freshman;
            case "Sophomore" -> StudentProfile.AcademicStatus.Sophomore;
            case "Junior"    -> StudentProfile.AcademicStatus.Junior;
            case "Senior"    -> StudentProfile.AcademicStatus.Senior;
            case "Graduate"  -> StudentProfile.AcademicStatus.Graduate;
            default          -> null;
        };
    }
    //used to get the employment status
    private static StudentProfile.EmploymentStatus parseEmployment(String s) {
        return switch (s) {
            case "Employed"   -> StudentProfile.EmploymentStatus.Employed;
            case "Unemployed" -> StudentProfile.EmploymentStatus.Unemployed;
            default           -> null;
        };
    }
    //used to get the preferred role for the student
    private static StudentProfile.PreferredRole parseRole(String s) {
        if (s == null) return null;
        return switch (s) {
            case "Front_End"  -> StudentProfile.PreferredRole.Front_End;
            case "Back_End"   -> StudentProfile.PreferredRole.Back_End;
            case "Full_Stack" -> StudentProfile.PreferredRole.Full_Stack;
            case "Data"       -> StudentProfile.PreferredRole.Data;
            case "Other"      -> StudentProfile.PreferredRole.Other;
            default           -> null;
        };
    }
}
