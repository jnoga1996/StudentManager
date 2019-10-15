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
                new Bundle(language, "assignment", "createNewAssignment", "Utwórz nowe zadanie"),
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
                new Bundle(language, "comment", "submit", "Wyślij"),

                new Bundle(language, "course", "id", "Id"),
                new Bundle(language, "course", "title", "Tytuł"),
                new Bundle(language, "course", "ects", "ECTS"),
                new Bundle(language, "course", "solution", "Rozwiązanie"),
                new Bundle(language, "course", "course", "Kurs"),
                new Bundle(language, "course", "student", "Student"),
                new Bundle(language, "course", "edit", "Edytuj"),
                new Bundle(language, "course", "delete", "Usuń"),
                new Bundle(language, "course", "create", "Utwórz kurs"),
                new Bundle(language, "course", "removeFromCourse", "Usuń z kursu"),
                new Bundle(language, "course", "registerToCourse", "Zapisz na kurs"),
                new Bundle(language, "course", "submit", "Wyślij"),

                new Bundle(language, "file", "id", "Id"),
                new Bundle(language, "file", "filename", "Nazwa pliku"),
                new Bundle(language, "file", "path", "Ścieżka"),
                new Bundle(language, "file", "modificationDate", "Data modyfikacji"),
                new Bundle(language, "file", "fileType", "Typ pliku"),
                new Bundle(language, "file", "referencedId", "Id referencji"),
                new Bundle(language, "file", "fileToUpload", "Plik do załadowania"),

                new Bundle(language, "home", "loggedAs", "Zalogowany jako"),

                new Bundle(language, "solution", "id", "Id"),
                new Bundle(language, "solution", "content", "Zawartość"),
                new Bundle(language, "solution", "finished", "Ukończone"),
                new Bundle(language, "solution", "grade", "Ocena"),
                new Bundle(language, "solution", "student", "Student"),
                new Bundle(language, "solution", "assignment", "Zadanie"),
                new Bundle(language, "solution", "attachment", "Załącznik"),
                new Bundle(language, "solution", "creationDate", "Data utworzenia"),
                new Bundle(language, "solution", "file", "Plik"),
                new Bundle(language, "solution", "submit", "Wyślij"),
                new Bundle(language, "solution", "delete", "Usuń"),
                new Bundle(language, "solution", "edit", "Edytuj"),
                new Bundle(language, "solution", "create", "Utwórz"),
                new Bundle(language, "solution", "showComments", "Pokaż komentarze"),
                new Bundle(language, "solution", "addComment", "Dodaj komentarz"),
                new Bundle(language, "solution", "uploadedFiles", "Załączone pliki"),

                new Bundle(language, "student", "id", "Id"),
                new Bundle(language, "student", "firstName", "Imię"),
                new Bundle(language, "student", "lastName", "Nazwisko"),
                new Bundle(language, "student", "faculty", "Wydział"),
                new Bundle(language, "student", "year", "Rok"),
                new Bundle(language, "student", "courses", "Kursy"),

                new Bundle(language, "teacher", "id", "Id"),
                new Bundle(language, "teacher", "firstName", "Imię"),
                new Bundle(language, "teacher", "lastName", "Nazwisko"),

                new Bundle(language, "work", "showSolutions", "Pokaż rozwiązania")
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
                new Bundle(language, "assignment", "createNewAssignment", "Create new assignment"),
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
                new Bundle(language, "comment", "submit", "Submit"),

                new Bundle(language, "course", "id", "Id"),
                new Bundle(language, "course", "title", "Title"),
                new Bundle(language, "course", "ects", "ECTS"),
                new Bundle(language, "course", "solution", "Solution"),
                new Bundle(language, "course", "course", "Course"),
                new Bundle(language, "course", "student", "Student"),
                new Bundle(language, "course", "edit", "Edit"),
                new Bundle(language, "course", "delete", "Delete"),
                new Bundle(language, "course", "create", "Create new course"),
                new Bundle(language, "course", "removeFromCourse", "Remove from course"),
                new Bundle(language, "course", "registerToCourse", "Register to course"),
                new Bundle(language, "course", "submit", "Submit"),

                new Bundle(language, "file", "id", "Id"),
                new Bundle(language, "file", "filename", "File name"),
                new Bundle(language, "file", "path", "Path"),
                new Bundle(language, "file", "modificationDate", "Modification date"),
                new Bundle(language, "file", "fileType", "File type"),
                new Bundle(language, "file", "referencedId", "Referenced id"),
                new Bundle(language, "file", "fileToUpload", "File to upload"),

                new Bundle(language, "home", "loggedAs", "Logged as"),

                new Bundle(language, "solution", "id", "Id"),
                new Bundle(language, "solution", "content", "Content"),
                new Bundle(language, "solution", "finished", "Finished"),
                new Bundle(language, "solution", "grade", "Grade"),
                new Bundle(language, "solution", "student", "Student"),
                new Bundle(language, "solution", "assignment", "Assignment"),
                new Bundle(language, "solution", "attachment", "Attachment"),
                new Bundle(language, "solution", "creationDate", "Creation date"),
                new Bundle(language, "solution", "file", "File"),
                new Bundle(language, "solution", "submit", "Submit"),
                new Bundle(language, "solution", "delete", "Delete"),
                new Bundle(language, "solution", "edit", "Edit"),
                new Bundle(language, "solution", "create", "Create"),
                new Bundle(language, "solution", "showComments", "Show comments"),
                new Bundle(language, "solution", "addComment", "Add comment"),
                new Bundle(language, "solution", "uploadedFiles", "Uploaded files"),

                new Bundle(language, "student", "id", "Id"),
                new Bundle(language, "student", "firstName", "First name"),
                new Bundle(language, "student", "lastName", "Last name"),
                new Bundle(language, "student", "faculty", "Faculty"),
                new Bundle(language, "student", "year", "Year"),
                new Bundle(language, "student", "courses", "Courses"),

                new Bundle(language, "teacher", "id", "Id"),
                new Bundle(language, "teacher", "firstName", "First name"),
                new Bundle(language, "teacher", "lastName", "Last name"),

                new Bundle(language, "work", "showSolutions", "Show solutions")
        );

        bundleRepository.saveAll(englishBundles);
    }
}
