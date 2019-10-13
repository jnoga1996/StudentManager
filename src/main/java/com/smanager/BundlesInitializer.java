package com.smanager;

import com.smanager.dao.models.Bundle;
import com.smanager.dao.repositories.BundleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BundlesInitializer {
    private BundleRepository bundleRepository;

    @Autowired
    public BundlesInitializer(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    public void initialize() {
        initializePolishBundles();
        initializeEnglishBundles();
    }

    public boolean isEmpty() {
        return bundleRepository.findAll().isEmpty();
    }

    private void initializePolishBundles() {
        String language = "pl";

        List<Bundle> polishBundles = Arrays.asList(
                new Bundle(language, "menu", "home", "Strona główna"),
                new Bundle(language, "menu", "myWork", "Moja praca"),
                new Bundle(language, "menu", "course", "Kursy"),
                new Bundle(language, "menu", "assignment", "Zadania"),
                new Bundle(language, "menu", "solution", "Rozwiązania"),
                new Bundle(language, "menu", "features", "Funkcje"),
                new Bundle(language, "menu", "assignmentSolution", "Rozwiązania zadań"),
                new Bundle(language, "menu", "group", "Grupy"),
                new Bundle(language, "menu", "student", "Studenci"),
                new Bundle(language, "menu", "teacher", "Nauczyciele"),
                new Bundle(language, "menu", "fileHistory", "Historia plików"),
                new Bundle(language, "menu", "signOut", "Wyloguj"),

                new Bundle(language, "login", "username", "Nazwa użytkownika"),
                new Bundle(language, "login", "password", "Hasło"),
                new Bundle(language, "login", "signIn", "Zaloguj"),

                new Bundle(language, "assignment", "title", "Tytuł"),
                new Bundle(language, "assignment", "content", "Treść"),
                new Bundle(language, "assignment", "course", "Kurs"),
                new Bundle(language, "assignment", "teacher", "Nauczyciel"),
                new Bundle(language, "assignment", "attachment", "Załącznik"),
                new Bundle(language, "assignment", "submit", "Wyślij"),
                new Bundle(language, "assignment", "createAssignment", "Utwórz nowe zadanie"),
                new Bundle(language, "assignment", "id", "Id"),
                new Bundle(language, "assignment", "edit", "Edytuj"),
                new Bundle(language, "assignment", "delete", "Usuń"),
                new Bundle(language, "assignment", "uploadedFiles", "Pliki"),

                new Bundle(language, "assignmentSolution", "checkSolutions", "Sprawdź rozwiązania"),

                new Bundle(language, "comment", "id", "Id"),
                new Bundle(language, "comment", "content", "Treść"),
                new Bundle(language, "comment", "creationDate", "Data utworzenia"),
                new Bundle(language, "comment", "solution", "Rozwiązanie"),
                new Bundle(language, "comment", "student", "Student"),
                new Bundle(language, "comment", "teacher", "Nauczyciel"),
                new Bundle(language, "comment", "noComments", "Brak komentarzy"),
                new Bundle(language, "comment", "submit", "Wyślij")

        );

        bundleRepository.saveAll(polishBundles);
    }

    private void initializeEnglishBundles() {
        String language = "en";

        List<Bundle> englishBundles = Arrays.asList(
                new Bundle(language, "menu", "home", "Home"),
                new Bundle(language, "menu", "myWork", "My Work"),
                new Bundle(language, "menu", "course", "Course"),
                new Bundle(language, "menu", "assignment", "Assignment"),
                new Bundle(language, "menu", "solution", "Solution"),
                new Bundle(language, "menu", "features", "Features"),
                new Bundle(language, "menu", "assignmentSolution", "Assignment solution"),
                new Bundle(language, "menu", "group", "Group"),
                new Bundle(language, "menu", "student", "Student"),
                new Bundle(language, "menu", "teacher", "Teacher"),
                new Bundle(language, "menu", "fileHistory", "File history"),
                new Bundle(language, "menu", "signOut", "Sign out"),

                new Bundle(language, "login", "username", "User name"),
                new Bundle(language, "login", "password", "Password"),
                new Bundle(language, "login", "signIn", "Sign in"),

                new Bundle(language, "assignment", "title", "Title"),
                new Bundle(language, "assignment", "content", "Content"),
                new Bundle(language, "assignment", "course", "Course"),
                new Bundle(language, "assignment", "teacher", "Teacher"),
                new Bundle(language, "assignment", "attachment", "Attachment"),
                new Bundle(language, "assignment", "submit", "Submit"),

                new Bundle(language, "assignment", "title", "Title"),
                new Bundle(language, "assignment", "content", "Content"),
                new Bundle(language, "assignment", "course", "Course"),
                new Bundle(language, "assignment", "teacher", "Teacher"),
                new Bundle(language, "assignment", "attachment", "Attachment"),
                new Bundle(language, "assignment", "submit", "Submit"),
                new Bundle(language, "assignment", "createAssignment", "Create new assignment"),
                new Bundle(language, "assignment", "id", "Id"),
                new Bundle(language, "assignment", "edit", "Edit"),
                new Bundle(language, "assignment", "delete", "Delete"),
                new Bundle(language, "assignment", "uploadedFiles", "Files"),

                new Bundle(language, "assignmentSolution", "checkSolutions", "Check solutions"),

                new Bundle(language, "comment", "id", "Id"),
                new Bundle(language, "comment", "content", "Content"),
                new Bundle(language, "comment", "creationDate", "Creation date"),
                new Bundle(language, "comment", "solution", "Solution"),
                new Bundle(language, "comment", "student", "Student"),
                new Bundle(language, "comment", "teacher", "Teacher"),
                new Bundle(language, "comment", "noComments", "No comments available"),
                new Bundle(language, "comment", "submit", "Submit")

        );

        bundleRepository.saveAll(englishBundles);
    }
}
