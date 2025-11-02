package cs151.application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class EditStudentController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> academicStatusCombo;
    @FXML private ListView<String> languageList;
    @FXML private ListView<String> databaseList;
    @FXML private ComboBox<String> roleCombo;
    @FXML private CheckBox whitelistedCheck;
    @FXML private CheckBox blacklistedCheck;
    @FXML private TextArea jobDetailsArea;
    private Stage stage;
    private boolean updatingToggles = false;
    // used for the original student profile
    private StudentProfile original;

    @FXML
    private void initialize() {
        if (languageList != null) languageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (databaseList != null) databaseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (whitelistedCheck != null && blacklistedCheck != null) {
            whitelistedCheck.selectedProperty().addListener((obs, oldV, newV) -> {
                if (updatingToggles) return;
                if (newV) {
                    updatingToggles = true;
                    blacklistedCheck.setSelected(false);
                    updatingToggles = false;
                }
            });
            blacklistedCheck.selectedProperty().addListener((obs, oldV, newV) -> {
                if (updatingToggles) return;
                if (newV) {
                    updatingToggles = true;
                    whitelistedCheck.setSelected(false);
                    updatingToggles = false;
                }
            });
        }
    }

    public void setStage(Stage s) { this.stage = s; }

    public void setStudent(StudentProfile sp) {
        this.original = sp;
        if (sp == null) return;
        if (nameField != null) nameField.setText(nn(sp.getFullName()));

        if (academicStatusCombo != null) {
            String display = prettyAcademic(sp.getAcademicStatus());
            selectComboItem(academicStatusCombo, display);
        }
        if (roleCombo != null) {
            String display = prettyRole(sp.getPreferredRole());
            selectComboItem(roleCombo, display);
        }
        if (languageList != null) {
            List<String> langs = nzList(sp.getLanguages());
            selectListItems(languageList, langs);
        }

        if (databaseList != null) {
            List<String> dbs = nzList(sp.getDatabases());
            selectListItems(databaseList, dbs);
        }

        if (whitelistedCheck != null) whitelistedCheck.setSelected(sp.isWhitelisted());
        if (blacklistedCheck != null) blacklistedCheck.setSelected(sp.isBlacklisted());
        if (jobDetailsArea != null) jobDetailsArea.setText(nn(sp.getJobDetails()));
    }

    @FXML
    private void onClose() {
        if (stage == null && nameField != null) {
            stage = (Stage) nameField.getScene().getWindow();
        }
        if (stage != null) stage.close();
    }

    private static String nn(String s) { return s == null ? "" : s; }
    private static List<String> nzList(List<String> xs) { return xs == null ? List.of() : xs; }

    private static void selectComboItem(ComboBox<String> combo, String targetDisplay) {
        if (targetDisplay == null || targetDisplay.isBlank()) {
            combo.getSelectionModel().clearSelection();
            return;
        }

        String normTarget = norm(targetDisplay);
        for (String item : combo.getItems()) {
            if (norm(item).equals(normTarget)) {
                combo.getSelectionModel().select(item);
                return;
            }
        }

        combo.getItems().add(targetDisplay);
        combo.getSelectionModel().select(targetDisplay);
    }

    private static void selectListItems(ListView<String> list, List<String> toSelect) {
        if (toSelect == null) return;

        Map<String, Integer> normIndex = new HashMap<>();
        for (int i = 0; i < list.getItems().size(); i++) {
            normIndex.put(norm(list.getItems().get(i)), i);
        }

        for (String v : toSelect) {
            if (v == null || v.isBlank()) continue;
            String nv = norm(v);
            Integer idx = normIndex.get(nv);
            if (idx == null) {
                list.getItems().add(v);
                idx = list.getItems().size() - 1;
                normIndex.put(nv, idx);
            }
            list.getSelectionModel().select(idx);
        }
    }

    private static String norm(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")           // strip diacritics
                .replace('_', ' ')
                .replace('-', ' ')
                .toLowerCase(Locale.ROOT).trim();
        return t.replaceAll("\\s+", " ");
    }

    private static String prettyAcademic(Object academicStatusEnum) {
        if (academicStatusEnum == null) return "";
        String raw = academicStatusEnum.toString(); // e.g., FRESHMAN
        String n = norm(raw);
        return switch (n) {
            case "freshman" -> "Freshman";
            case "sophomore" -> "Sophomore";
            case "junior" -> "Junior";
            case "senior" -> "Senior";
            case "graduate", "grad" -> "Graduate";
            default -> capitalizeWords(raw.replace('_', ' '));
        };
    }

    private static String prettyRole(Object roleEnum) {
        if (roleEnum == null) return "";
        String raw = roleEnum.toString(); // e.g., FRONT_END
        String n = norm(raw);
        return switch (n) {
            case "frontend", "front end" -> "Front-End";
            case "backend", "back end" -> "Back-End";
            case "fullstack", "full stack" -> "Full-Stack";
            case "data" -> "Data";
            default -> capitalizeWords(raw.replace('_', ' '));
        };
    }

    private static String capitalizeWords(String s) {
        if (s == null || s.isBlank()) return "";
        return Arrays.stream(s.trim().toLowerCase(Locale.ROOT).split("\\s+"))
            .map(w -> w.substring(0,1).toUpperCase(Locale.ROOT) + (w.length() > 1 ? w.substring(1) : ""))
            .collect(Collectors.joining(" "));
    }

    //helps nicely get the combo box value
    private static String val(ComboBox<String> cbVal){
        return cbVal==null||cbVal.getValue()==null? null : cbVal.getValue().trim().toLowerCase();
    }

    // used to get the academic status
    private static StudentProfile.AcademicStatus parseAcademic(String val) {
        if (val == null) return null;
        switch (val) {
            case "freshman":  return StudentProfile.AcademicStatus.Freshman;
            case "sophomore": return StudentProfile.AcademicStatus.Sophomore;
            case "junior":    return StudentProfile.AcademicStatus.Junior;
            case "senior":    return StudentProfile.AcademicStatus.Senior;
            case "graduate":  return StudentProfile.AcademicStatus.Graduate;
            default:          return null;
        }
    }

    // used to get the preferred role
    private static StudentProfile.PreferredRole parseRole(String val) {
        if (val == null) return null;
        switch (val) {
            case "front-end":  return StudentProfile.PreferredRole.Front_End;
            case "back-end":   return StudentProfile.PreferredRole.Back_End;
            case "full-stack": return StudentProfile.PreferredRole.Full_Stack;
            case "data":       return StudentProfile.PreferredRole.Data;
            case "other":      return StudentProfile.PreferredRole.Other;
            default:           return null;
        }
    }

    @FXML
    private void onSave() {
        // makes sure that the student actually exists
        if (original == null) {
            new Alert(Alert.AlertType.ERROR, "There is no student for editing.").showAndWait();
            return;
        }

        // used to create a student profile with the updated values for the student
        StudentProfile updatedProfile = new StudentProfile(
                original.getId(),
                nameField == null ? "" : nameField.getText().trim(),
                parseAcademic(val(academicStatusCombo)),
                original.getEmploymentStatus(),
                jobDetailsArea == null ? "" : jobDetailsArea.getText().trim(),
                new java.util.ArrayList<>(languageList == null ? java.util.List.<String>of()
                        : languageList.getSelectionModel().getSelectedItems()),
                new java.util.ArrayList<>(databaseList == null ? java.util.List.<String>of()
                        : databaseList.getSelectionModel().getSelectedItems()),
                parseRole(val(roleCombo)),
                whitelistedCheck != null && whitelistedCheck.isSelected(),
                blacklistedCheck != null && blacklistedCheck.isSelected()
        );

        // used to hold the error messages.
        // helpful for returning all the error messages that occurred
        // when updating the student profile after the edits
        java.util.List<String> error = StudentProfileValidator.validate(updatedProfile);
        if (!error.isEmpty()) {
            Alert message = new Alert(Alert.AlertType.ERROR, String.join("\n", error));
            message.setHeaderText("Cannot Save Edits");
            message.setTitle("Validation Errors");
            message.showAndWait();
            return;
        }

        // will update the profile and send alert to say if it was successful
        try {
            StudentProfileRepository.getInstance().updateProfile(updatedProfile);
            new Alert(Alert.AlertType.INFORMATION, "The changes are saved.").showAndWait();
            onClose();
        } catch (IllegalArgumentException nameClash) {
            new Alert(Alert.AlertType.ERROR, nameClash.getMessage()).showAndWait();
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Have failed to save changes: "+e.getMessage()).showAndWait();
        }
    }

}
