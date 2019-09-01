package com.smanager;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private AssignmentSolutionRepository assignmentSolutionRepository;
    private CourseRepository courseRepository;
    private GroupRepository groupRepository;

    @Autowired
    public DataLoader(StudentRepository studentRepository, TeacherRepository teacherRepository, AssignmentRepository assignmentRepository,
                      SolutionRepository solutionRepository, AssignmentSolutionRepository assignmentSolutionRepository,
                      CourseRepository courseRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.assignmentSolutionRepository = assignmentSolutionRepository;
        this.courseRepository = courseRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (studentRepository.findAll().isEmpty())
            initializeStudents();
        if (teacherRepository.findAll().isEmpty())
            initializeTeachers();
        if (assignmentRepository.findAll().isEmpty())
            initializeAssignments();
        if (solutionRepository.findAll().isEmpty())
            initializeSolutions();
        if (assignmentSolutionRepository.findAll().isEmpty())
            initializeAssignmentSolutions();
        if (courseRepository.findAll().isEmpty())
            initializeCourses();
        if (groupRepository.findAll().isEmpty())
            initializeGroups();
    }

    private void initializeStudents() {
        List<Student> students = Arrays.asList(new Student[] {
                new Student("Jan", "Kowalski", "Fizyka", 1),
                new Student("Ewa", "Kowalska", "Astronomia", 2),
                new Student("Krystian", "Brodaty", "Informatyka", 1),
                new Student("Agata", "Herbata", "Fizyka", 1),
                new Student("Tomasz", "Plecak", "Fizyka", 2),
                new Student("Ewelina", "Lina", "Informatyka", 1),
                new Student("Eustachy", "Rzeka", "Fizyka", 2)
        });

        studentRepository.saveAll(students);
    }

    private void initializeTeachers() {
        List<Teacher> teachers = Arrays.asList(new Teacher[] {
                new Teacher("Marian", "Jezioro"),
                new Teacher("Joanna", "Talerz"),
                new Teacher("Edmund", "Gruszka")
        });

        teacherRepository.saveAll(teachers);
    }

    private void initializeAssignments() {
        List<Assignment> assignments = Arrays.asList(new Assignment[] {
                new Assignment("Wstęp do sortowania", "Zaimplementuj 3 dowolne metody sortowania"),
                new Assignment("Title", "Content")
        });

        assignments.get(0).setCourse(courseRepository.getOne(1L));
        assignments.get(1).setCourse(courseRepository.getOne(2L));

        assignmentRepository.saveAll(assignments);
    }

    private void initializeSolutions() {
        List<Solution> solutions = Arrays.asList(new Solution[] {
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution 1", assignmentSolutionRepository.getOne(1L).getId()),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution 2", assignmentSolutionRepository.getOne(1L).getId()),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution2 1", assignmentSolutionRepository.getOne(1L).getId()),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution2 2", assignmentSolutionRepository.getOne(2L).getId()),
                createSolution(studentRepository.getOne(2L).getId(), "Ewa solution 1", assignmentSolutionRepository.getOne(2L).getId())
        });

        solutionRepository.saveAll(solutions);
    }

    private void initializeAssignmentSolutions() {
        List<AssignmentSolution> assignmentSolutions = Arrays.asList(new AssignmentSolution[] {
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(1L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(2L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(5L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(2L).getId(), solutionRepository.getOne(3L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(2L).getId(), solutionRepository.getOne(4L).getId())
        });

        assignmentSolutionRepository.saveAll(assignmentSolutions);
    }

    private void initializeCourses() {
        List<Course> courses = Arrays.asList(new Course[] {
                new Course("Analiza matematyczna 1", 5),
                new Course("Podstawy programowania", 3),
                new Course("Matematyka dyskretna", 4)
        });

        courseRepository.saveAll(courses);
    }

    private void initializeGroups() {
        List<Group> groups = Arrays.asList(new Group[] {
                createGroup(courseRepository.getOne(1L).getId(), teacherRepository.getOne(1L).getId()),
                createGroup(courseRepository.getOne(1L).getId(), teacherRepository.getOne(2L).getId()),
                createGroup(courseRepository.getOne(2L).getId(), teacherRepository.getOne(1L).getId()),
                createGroup(courseRepository.getOne(2L).getId(), teacherRepository.getOne(2L).getId()),
                createGroup(courseRepository.getOne(3L).getId(), teacherRepository.getOne(2L).getId())
        });

        groupRepository.saveAll(groups);
    }

    private AssignmentSolution createAssignmentSolution(Long assignmentId, Long solutionId) {
        Assignment assignment = assignmentRepository.getOne(assignmentId);
        Solution solution = solutionRepository.getOne(solutionId);

        return new AssignmentSolution(assignment, solution);
    }

    private Solution createSolution(Long studentId, String content, Long assignmentId) {
        Solution solution = new Solution();
        Student student = studentRepository.getOne(studentId);
        //solution.setAssignment(assignmentSolutionRepository.getOne(assignmentId));
        solution.setAssignment(assignmentRepository.getOne(assignmentId));
        solution.setContent(content);
        solution.setStudent(student);

        return solution;
    }

    private Group createGroup(Long courseId, Long teacherId) {
        Course course = courseRepository.getOne(courseId);
        Teacher teacher = teacherRepository.getOne(teacherId);

        return new Group(course, teacher);
    }
}
