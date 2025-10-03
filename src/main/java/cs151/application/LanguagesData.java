package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LanguagesData {
    private static final LanguagesData INSTANCE = new LanguagesData();
    public static LanguagesData getInstance() { return INSTANCE; }

    private final ObservableList<String> languages = FXCollections.observableArrayList();

    private LanguagesData() {} // private constructor (no external new)

    public ObservableList<String> getLanguages() {
        return languages;
    }

    // Add a new language 
    public void addLanguage(String lang) {
        languages.add(lang);
    }

    // Delete a language
    public void deleteLanguage(String lang) {
        languages.remove(lang);
    }
}
