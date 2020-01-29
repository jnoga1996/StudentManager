package com.smanager.utils;

import com.smanager.dao.models.CustomGrade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GradesUtil {

    private String url;// = "jdbc:mysql://localhost:3306/smanager?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false";
    private String username;// = "jacor";
    private String password;// = "#Dupa123";
    private final static String QUERY = "select ifnull(avg(assignment_grade), 0) as course_grade, course from (" +
            "   select " +
            "          ROUND(AVG(grade))    as assignment_grade," +
            "          assignment_course_id as course" +
            "   from solutions s" +
            "            join assignments a on s.assignment_assignment_id = a.assignment_id" +
            "   where student_student_id = ?" +
            "     and assignment_course_id = ?" +
            "   group by assignment_id" +
            ") as Query group by course;";

    private final static String GRADES_QUERY = "select assignment_id, grade from solutions s join assignments a " +
            "on s.assignment_assignment_id = a.assignment_id " +
            "where student_student_id = ? and assignment_id = ?";

    private final static String GET_GRADE_IF_NULL_QUERY = "select round(avg(grade)) as grd, assignment_id as ass from ( " +
            GRADES_QUERY + ") as Query ";

    private DataSource dataSource;

    public GradesUtil(@Value("${spring.datasource.url}") String url, @Value("${spring.datasource.username}")String username,
                      @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        dataSource = initializeDataSource();
    }

    private DataSource initializeDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    public CustomGrade getCourseGrade(Long studentId, Long courseId) {
        ResultSet result;
        PreparedStatement statement;
        Connection connection;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, studentId);
            statement.setLong(2, courseId);
            result = statement.executeQuery();

            result.next();
            return new CustomGrade(result.getLong("course"), result.getDouble("course_grade"));
        } catch (SQLException ex) {
            return null;
        }
    }

    public CustomGrade getGradeIfNull(Long studentId, Long courseId) {
        ResultSet result;
        PreparedStatement statement;
        Connection connection;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(GET_GRADE_IF_NULL_QUERY);
            statement.setLong(1, studentId);
            statement.setLong(2, courseId);
            result = statement.executeQuery();

            result.next();
            return new CustomGrade(result.getLong("ass"), result.getDouble("grd"));
        } catch (SQLException ex) {
            return null;
        }
    }

    public List<Double> getAssignmentGrades(Long studentId, Long assignmentId) {
        ResultSet result;
        PreparedStatement statement;
        Connection connection;
        List<Double> grades = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(GRADES_QUERY);
            statement.setLong(1, studentId);
            statement.setLong(2, assignmentId);
            result = statement.executeQuery();

            while (result.next()) {
                grades.add(result.getDouble("grade"));
            }
        } finally {
            return grades;
        }
    }

}
