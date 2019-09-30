package com.smanager;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader implements ApplicationRunner {

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private AssignmentSolutionRepository assignmentSolutionRepository;
    private CourseRepository courseRepository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    @Autowired
    public DataLoader(StudentRepository studentRepository, TeacherRepository teacherRepository, AssignmentRepository assignmentRepository,
                      SolutionRepository solutionRepository, AssignmentSolutionRepository assignmentSolutionRepository,
                      CourseRepository courseRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.assignmentSolutionRepository = assignmentSolutionRepository;
        this.courseRepository = courseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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
        if (courseRepository.findAll().isEmpty()) {
            initializeCourses();
            registerStudents();
        }
        if (groupRepository.findAll().isEmpty())
            initializeGroups();
        if (!userRepository.findAll().isEmpty()) {
            userRepository.deleteAll();
        }
        initializeUsers();
    }

    private void initializeStudents() {
        List<Student> students = Arrays.asList(
                new Student("Jan", "Kowalski", "Fizyka", 1),
                new Student("Ewa", "Kowalska", "Astronomia", 2),
                new Student("Krystian", "Brodaty", "Informatyka", 1),
                new Student("Agata", "Herbata", "Fizyka", 1),
                new Student("Tomasz", "Plecak", "Fizyka", 2),
                new Student("Ewelina", "Lina", "Informatyka", 1),
                new Student("Eustachy", "Rzeka", "Fizyka", 2));

        studentRepository.saveAll(students);
    }

    private void initializeTeachers() {
        List<Teacher> teachers = Arrays.asList(
                new Teacher("Marian", "Jezioro"),
                new Teacher("Joanna", "Talerz"),
                new Teacher("Edmund", "Gruszka"));

        teacherRepository.saveAll(teachers);
    }

    private void initializeAssignments() {
        List<Assignment> assignments = Arrays.asList(
                new Assignment("Wstęp do sortowania", "Zaimplementuj 3 dowolne metody sortowania"),
                new Assignment("Title", "Content"));

        assignments.get(0).setCourse(courseRepository.getOne(1L));
        assignments.get(1).setCourse(courseRepository.getOne(2L));

        assignmentRepository.saveAll(assignments);
    }

    private void initializeSolutions() {
        List<Solution> solutions = Arrays.asList(
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution 1", assignmentSolutionRepository.getOne(1L).getId(), Grades.A, LocalDateTime.now()),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution 2", assignmentSolutionRepository.getOne(1L).getId(), Grades.E, LocalDateTime.now()),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution2 1", assignmentSolutionRepository.getOne(1L).getId(), Grades.B, LocalDateTime.of(2019, 7, 28, 22, 10)),
                createSolution(studentRepository.getOne(1L).getId(), "Jan solution2 2", assignmentSolutionRepository.getOne(2L).getId(), Grades.D, LocalDateTime.of(2019, 6, 15, 16, 25)),
                createSolution(studentRepository.getOne(2L).getId(), "Ewa solution 1", assignmentSolutionRepository.getOne(2L).getId(), Grades.C, LocalDateTime.of(2019, 9, 25, 21, 37))
        );

        solutionRepository.saveAll(solutions);
    }

    private void initializeAssignmentSolutions() {
        List<AssignmentSolution> assignmentSolutions = Arrays.asList(
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(1L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(2L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(1L).getId(), solutionRepository.getOne(5L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(2L).getId(), solutionRepository.getOne(3L).getId()),
                createAssignmentSolution(assignmentRepository.getOne(2L).getId(), solutionRepository.getOne(4L).getId()));

        assignmentSolutionRepository.saveAll(assignmentSolutions);
    }

    private void initializeCourses() {
        List<Course> courses = Arrays.asList(
                new Course("Analiza matematyczna 1", 5),
                new Course("Podstawy programowania", 3),
                new Course("Matematyka dyskretna", 4));

        courseRepository.saveAll(courses);
    }

    private void initializeGroups() {
        List<Group> groups = Arrays.asList(
                createGroup(courseRepository.getOne(1L).getId(), teacherRepository.getOne(1L).getId()),
                createGroup(courseRepository.getOne(1L).getId(), teacherRepository.getOne(2L).getId()),
                createGroup(courseRepository.getOne(2L).getId(), teacherRepository.getOne(1L).getId()),
                createGroup(courseRepository.getOne(2L).getId(), teacherRepository.getOne(2L).getId()),
                createGroup(courseRepository.getOne(3L).getId(), teacherRepository.getOne(2L).getId()));

        groupRepository.saveAll(groups);
    }

    private AssignmentSolution createAssignmentSolution(Long assignmentId, Long solutionId) {
        Assignment assignment = assignmentRepository.getOne(assignmentId);
        Solution solution = solutionRepository.getOne(solutionId);

        return new AssignmentSolution(assignment, solution);
    }

    private Solution createSolution(Long studentId, String content, Long assignmentId, Grades grade, LocalDateTime localDateTime) {
        Solution solution = new Solution();
        Student student = studentRepository.getOne(studentId);
        solution.setAssignment(assignmentRepository.getOne(assignmentId));
        solution.setContent(content);
        solution.setStudent(student);
        solution.setGrade(grade.getGrade());
        solution.setCreationDate(localDateTime);

        return solution;
    }

    private Group createGroup(Long courseId, Long teacherId) {
        Course course = courseRepository.getOne(courseId);
        Teacher teacher = teacherRepository.getOne(teacherId);

        return new Group(course, teacher);
    }

    private void initializeUsers() {
        List<User> users = Arrays.asList(
                new User("student", "student", UserType.STUDENT.toString(), true),
                new User("teacher", "teacher", UserType.TEACHER.toString(), true),
                new User("admin", "admin", UserType.ADMIN.toString(), true)
        );

        userRepository.saveAll(users);
        userRepository.saveAll(getStudentUsers());
        userRepository.saveAll(getTeacherUsers());
    }

    private List<User> getStudentUsers() {
        List<User> students = new LinkedList<>();
        for (Student student : studentRepository.findAll()) {
            String username = student.getUsername();
            String password = "123456";
            User user = new User(username, password, UserType.STUDENT.toString(), true, student);
            students.add(user);
        }

        return students;
    }

    private List<User> getTeacherUsers() {
        List<User> teachers = new LinkedList<>();
        for (Teacher teacher : teacherRepository.findAll()) {
            String username = teacher.getUsername();
            String password = "123456";
            User user = new User(username, password, UserType.TEACHER.toString(), true, teacher);
            teachers.add(user);
        }

        return teachers;
    }

    private void registerStudentToCourse(Long studentId, Long courseId) {
        Course course = courseRepository.getOne(courseId);
        Student student = studentRepository.getOne(studentId);
        Set<Student> registeredStudents = course.getStudents();

        if (registeredStudents == null) {
            registeredStudents = new HashSet<>();
        }

        if (student != null) {
            registeredStudents.add(student);
        }

        if (course != null) {
            course.setStudents(registeredStudents);
            courseRepository.save(course);
        }
    }

    private void registerStudents() {
        List<Course> courses = courseRepository.findAll();
        for (Student student : studentRepository.findAll()) {
            registerStudentToCourse(student.getId(), courses.get(0).getId());
            if (student.getId() % 2 == 0) {
                registerStudentToCourse(student.getId(), courses.get(1).getId());
            }
        }
    }

}
