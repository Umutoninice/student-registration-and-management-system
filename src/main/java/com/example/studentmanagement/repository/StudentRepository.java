package com.example.studentmanagement.repository;

import com.example.studentmanagement.exception.StudentPersistenceException;
import com.example.studentmanagement.exception.StudentPersistenceException.Reason;
import com.example.studentmanagement.model.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private static final String INSERT_SQL =
            "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT id, full_name, age, email FROM students ORDER BY id";
    private static final String SELECT_BY_ID_SQL =
            "SELECT id, full_name, age, email FROM students WHERE id = ?";


    private static final String UNIQUE_VIOLATION = "23505";
    private static final String CHECK_VIOLATION = "23514";

    private final DataSource dataSource;


    public StudentRepository(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public void save(Student student) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, student.getFullName()); // firstName + lastName -> full_name
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
                throw new StudentPersistenceException(Reason.DUPLICATE_EMAIL,
                        "A student with this email already exists.", e);
            }
            if (CHECK_VIOLATION.equals(e.getSQLState())) {
                throw new StudentPersistenceException(Reason.INVALID_AGE,
                        "Age must be greater than 18.", e);
            }
            throw new StudentPersistenceException(Reason.DATABASE_ERROR,
                    "Could not save the student. Please try again later.", e);
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new StudentPersistenceException(Reason.DATABASE_ERROR,
                    "Could not load students from the database.", e);
        }
        return students;
    }

    public Optional<Student> findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new StudentPersistenceException(Reason.DATABASE_ERROR,
                    "Could not load the student from the database.", e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String firstName = fullName;
        String lastName = "";
        int space = fullName.indexOf(' ');
        if (space >= 0) {
            firstName = fullName.substring(0, space);
            lastName = fullName.substring(space + 1).trim();
        }
        return new Student(rs.getInt("id"), firstName, lastName,
                rs.getString("email"), rs.getInt("age"));
    }
}
