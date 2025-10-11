package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LanguagesData {
    private static final LanguagesData INSTANCE = new LanguagesData();
    public static LanguagesData getInstance() { return INSTANCE; }

    private final ObservableList<String> languages = FXCollections.observableArrayList();

    private LanguagesData() {} 

    public ObservableList<String> getLanguages() {
        return languages;
    }

    // Add a new language 
    public void addLanguage(String lang) {
        if (lang.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/languages.csv", true));){
            writer.write(lang);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Delete a language
    public void deleteLanguage(String lang) {
        languages.remove(lang);
    }
}
