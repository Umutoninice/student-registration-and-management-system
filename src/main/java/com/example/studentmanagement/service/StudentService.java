package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;


    public StudentService(StudentRepository studentRepository) {

        this.studentRepository = studentRepository;
    }

    public void registerStudent(Student student) {
        student.setEmail(student.getEmail().trim().toLowerCase());
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {

        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(int id) {

        return studentRepository.findById(id);
    }
}
