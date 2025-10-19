package cs151.application;

import java.util.ArrayList;
import java.util.List;

// this class is used to check if the required fields are filled
// It also collects all the errors as a list (if there are any)
// the list will be used to display the error messages
public final class StudentProfileValidator {
    public static List<String> validate(StudentProfile profile) {
        List<String> error = new ArrayList<>();

        // we have two different checks since the text field could be empty
        // or there could be a text field with just spaces
        if (profile.getFullName() == null || profile.getFullName().trim().isEmpty())
            error.add("The full name is necessary.");

        if (profile.getAcademicStatus() == null)
            error.add("The academic status is necessary.");

        if (profile.getEmploymentStatus() == null)
            error.add("The employment status is necessary.");

        // If the person is employed, we must also check if the job details were
        // filled out for the person
        if (profile.getEmploymentStatus() == StudentProfile.EmploymentStatus.Employed) {
            if (profile.getJobDetails() == null || profile.getJobDetails().trim().isEmpty())
                error.add("The job details are necessary when employment status is shown as Employed.");
        }

        if (profile.getLanguages() == null || profile.getLanguages().isEmpty())
            error.add("The known programming languages is required to be selected.");

        if (profile.getDatabases() == null || profile.getDatabases().isEmpty())
            error.add("The known databases is required to be selected.");

        if (profile.getPreferredRole() == null)
            error.add("The preferred professional role is necessary.");

        // we will make sure that both can't be selected at the same time
        if (profile.isWhitelisted() && profile.isBlacklisted())
            error.add("The whitelist and blacklist can't be selected at the same time.");

        // we will return the list of errors to be displayed
        return error;
    }
}
