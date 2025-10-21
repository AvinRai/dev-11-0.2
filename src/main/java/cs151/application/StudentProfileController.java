package cs151.application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.List;


public class StudentProfileController {
    @FXML
    protected void GoBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 320);
        Stage stage = new Stage();
        stage.setTitle("Student Knowledge for Faculty");
        stage.setScene(scene);
        stage.show();

        Stage CurrentActiveStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CurrentActiveStage.close();
    }
    @FXML
    private ListView<String> languagelist;
    @FXML
    private  ListView<String> databaselist;
    @FXML
    private CheckBox WhiteList;
    @FXML
    private  CheckBox BlackList;
    @FXML
    private TextField studentName;
    @FXML
    private ComboBox<String> academicStatusCombo;
    @FXML
    private ToggleGroup employmentGroup;
    @FXML
    private TextField studentJob;
    @FXML
    private ComboBox<String> roleCombo;

    // after saving the values, we use this to clear the values from the page
    @FXML
    private TextArea commentsentry;


    @FXML
    public void initialize(){
        LanguagesData.getInstance().loadLanguagesFromFile();

        ObservableList<String> data = LanguagesData.getInstance().getLanguages();

        languagelist.setItems(data);
        languagelist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databaselist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        WhiteList.selectedProperty().addListener((obs, oldVal, newVal) ->{
            if(newVal) BlackList.setSelected(false);
        });

        BlackList.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) WhiteList.setSelected(false);
        });

        // used to add the choices to the dropdown
        if (academicStatusCombo != null) {
            academicStatusCombo.getItems().setAll("Freshman","Sophomore","Junior","Senior","Graduate");
        }
        if (roleCombo != null) {
            roleCombo.getItems().setAll("Front-End","Back-End","Full-Stack","Data","Other");
        }
    }

    // the following is used to save the profile
    @FXML
    protected void saveProfile(ActionEvent event) {
        // we need to first build the current student's profile
        StudentProfile profile = buildModelFromForm();

        // the list will contain errors if it isn't empty
        // we use the list to get all the errors and display them
        List<String> errors = StudentProfileValidator.validate(profile);
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors));
            return;
        }

        try {
            // We will try to save the profile
            // If there are no profiles with the same name or any other errors,
            // we will be able to save the profile
            // We will then clear the page so that new student profiles can be created
            StudentProfileRepository.getInstance().saveProfile(profile);
            new Alert(Alert.AlertType.INFORMATION, "The student profile was saved.").showAndWait();
            clearForm();
        } catch (IllegalArgumentException anotherSameName) {
            showError(anotherSameName.getMessage());
        } catch (RuntimeException e) {
            showError("failed to save: " + e.getMessage());
        }
    }

    // the following is used to create an object of a StudentProfile
    // using the Student Profile page
    private StudentProfile buildModelFromForm() {
        String name = safe(studentName);


        String statusStr = val(academicStatusCombo);
        StudentProfile.AcademicStatus status = null;
        // sets the academic status of the student
        if (statusStr != null) {
            status = switch (statusStr) {
                case "freshman"  -> StudentProfile.AcademicStatus.Freshman;
                case "sophomore" -> StudentProfile.AcademicStatus.Sophomore;
                case "junior"    -> StudentProfile.AcademicStatus.Junior;
                case "senior"    -> StudentProfile.AcademicStatus.Senior;
                case "graduate"  -> StudentProfile.AcademicStatus.Graduate;
                default          -> null;
            };
        }

        // sets the status to employed/unemployed
        StudentProfile.EmploymentStatus emp = null;
        if (employmentGroup != null) {
            Toggle sel = employmentGroup.getSelectedToggle();
            if (sel instanceof RadioButton rb) {
                String label = rb.getText();
                if (label != null) {
                    String l = label.trim().toLowerCase();
                    if (l.equals("employed")) {
                        emp = StudentProfile.EmploymentStatus.Employed;
                    } else if (l.equals("unemployed")) {
                        emp = StudentProfile.EmploymentStatus.Unemployed;
                    }
                }
            }
        }

        String job = safe(studentJob);


        List<String> langs = new ArrayList<>(languagelist.getSelectionModel().getSelectedItems());
        List<String> dbs   = new ArrayList<>(databaselist.getSelectionModel().getSelectedItems());


        String roleStr = val(roleCombo);
        StudentProfile.PreferredRole role = null;
        // sets the preferred role of the student
        if (roleStr != null) {
            role = switch (roleStr) {
                case "front-end"  -> StudentProfile.PreferredRole.Front_End;
                case "back-end"   -> StudentProfile.PreferredRole.Back_End;
                case "full-stack" -> StudentProfile.PreferredRole.Full_Stack;
                case "data"       -> StudentProfile.PreferredRole.Data;
                case "other"      -> StudentProfile.PreferredRole.Other;
                default           -> null;
            };
        }

        // sets the values of the whitelist/blacklist
        boolean wl = WhiteList.isSelected();
        boolean bl = BlackList.isSelected();

        // we will return a new StudentProfile object with all the values
        return new StudentProfile(
                null, name, status, emp, job, langs, dbs, role, wl, bl
        );
    }



    private static String safe(TextInputControl c){ return c==null? "" : (c.getText()==null? "" : c.getText().trim()); }
    private static String val(ComboBox<String> cb){ return cb==null||cb.getValue()==null? null : cb.getValue().trim().toLowerCase(); }

    // used to show the error message alerts
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Cannot Save Student Profile");
        a.setHeaderText("Please fix the following:");
        a.setContentText(msg);
        a.showAndWait();
    }

    // the following is used to ensure that the page is empty after saving
    private void clearForm() {
        if (studentName != null) studentName.clear();
        if (academicStatusCombo != null) academicStatusCombo.getSelectionModel().clearSelection();
        if (employmentGroup != null) employmentGroup.selectToggle(null);
        if (studentJob != null) studentJob.clear();
        if (languagelist != null) languagelist.getSelectionModel().clearSelection();
        if (databaselist != null) databaselist.getSelectionModel().clearSelection();
        if (roleCombo != null) roleCombo.getSelectionModel().clearSelection();
        if (WhiteList != null) WhiteList.setSelected(false);
        if (BlackList != null) BlackList.setSelected(false);
        if (commentsentry != null) commentsentry.clear();
    }

}
