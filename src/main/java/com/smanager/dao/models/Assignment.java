package com.smanager.dao.models;

import com.smanager.controllers.AssignmentController;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "ASSIGNMENTS")
@EntityListeners(AuditingEntityListener.class)
public class Assignment implements ISaveable {
    @Column(name = "assignment_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    private String path;

    @Transient
    private String filename;

    @OneToMany(mappedBy = "assignment")
    private List<Solution> solutions;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", foreignKey = @ForeignKey(name = "Assignment_Student_FK"))
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_course_id", referencedColumnName = "course_id", foreignKey = @ForeignKey(name = "Assignment_Course_FK"))
    private Course course;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "Assignment_Teacher_FK"))
    private Teacher teacher;

    public Assignment() {}

    public Assignment(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> assignmentSolutions) {
        this.solutions = solutions;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() { return course; }

    public void setCourse(Course course) { this.course = course; }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + ", title: " + getTitle() + ", content: " + getContent();
    }

    public String getDetailsUrl() {
        return AssignmentController.DETAILS_URL + "?id=" + getId();
    }

    public String getContentMultiline() {
        String content = getContent();
        StringBuilder stringBuilder = new StringBuilder();
        final int MAX_SIZE = 64;
        String[] words = content.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() > MAX_SIZE) {
                currentLine.append("\n");
            }
            currentLine.append(word);
            currentLine.append(" ");
            stringBuilder.append(currentLine.toString());
            currentLine = new StringBuilder();
        }

        return stringBuilder.toString();
    }

    public String getDivId() {
        return getCourse().getId() + "" + getId();
    }
}
