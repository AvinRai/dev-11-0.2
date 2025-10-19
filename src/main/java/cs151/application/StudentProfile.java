package cs151.application;

import java.util.List;
// we will use this import to create unique ids
// as the user doesn't enter there student id in the requirements
import java.util.UUID;


public final class StudentProfile {
    // use enum since we want fixed constant values for each
    public enum AcademicStatus { Freshman, Sophomore, Junior, Senior, Graduate }
    public enum EmploymentStatus { Employed, Unemployed }
    public enum PreferredRole { Front_End, Back_End, Full_Stack, Data, Other }

    // the fields below are used for the data values of a student profile
    private final String id; // shared key across CSVs
    private final String name;
    private final List<String> languages;
    private final List<String> databases;
    private final AcademicStatus academic;
    private final EmploymentStatus employment;
    // boolean variables used to check if student is blacklist/whitelist
    private final boolean whitelisted;
    private final boolean blacklisted;
    // If the user is employed, the details for the job will be filled
    private final String detailsForJob;
    private final PreferredRole preferredRole;

    // The constructor instantiates the values of the
    // fields for the student page
    public StudentProfile(String id,
                          String name,
                          AcademicStatus academic,
                          EmploymentStatus employment,
                          String detailsForJob,
                          List<String> languages,
                          List<String> databases,
                          PreferredRole preferredRole,
                          boolean whitelisted,
                          boolean blacklisted) {
        this.id = (id == null ? UUID.randomUUID().toString() : id);
        this.name = name;
        this.academic = academic;
        this.employment = employment;
        this.detailsForJob = detailsForJob;
        this.languages = List.copyOf(languages);
        this.databases = List.copyOf(databases);
        this.preferredRole = preferredRole;
        this.whitelisted = whitelisted;
        this.blacklisted = blacklisted;
    }

    // We have getters to get the values of certain data (like the id, etc.)
    public String getId() { return id; }
    public String getFullName() { return name; }
    public boolean isWhitelisted() { return whitelisted; }
    public AcademicStatus getAcademicStatus() { return academic; }
    public String getJobDetails() { return detailsForJob; }
    public EmploymentStatus getEmploymentStatus() { return employment; }
    public boolean isBlacklisted() { return blacklisted; }
    public List<String> getLanguages() { return languages; }
    public List<String> getDatabases() { return databases; }
    public PreferredRole getPreferredRole() { return preferredRole; }
}
