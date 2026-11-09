package com.example.studentmanagement.controller;

import com.example.studentmanagement.exception.StudentPersistenceException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;


    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        return "register";
    }


    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            studentService.registerStudent(student);
        } catch (StudentPersistenceException e) {
            switch (e.getReason()) {
                case DUPLICATE_EMAIL -> bindingResult.rejectValue("email", "duplicate", e.getMessage());
                case INVALID_AGE -> bindingResult.rejectValue("age", "tooYoung", e.getMessage());
                default -> bindingResult.reject("databaseError", e.getMessage());
            }
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                "Student " + student.getFullName() + " was registered successfully!");
        return "redirect:/student/register";
    }


    @GetMapping("/all")
    public String listStudents(Model model) {
        try {
            List<Student> students = studentService.getAllStudents();
            model.addAttribute("students", students);
        } catch (StudentPersistenceException e) {
            model.addAttribute("students", List.of());
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "students";
    }


    @GetMapping("/{id}")
    public String showStudent(@PathVariable("id") int id, Model model) {
        try {
            studentService.getStudentById(id).ifPresentOrElse(
                    student -> model.addAttribute("students", List.of(student)),
                    () -> {
                        model.addAttribute("students", List.of());
                        model.addAttribute("notFoundMessage", "No student found with ID " + id + ".");
                    });
        } catch (StudentPersistenceException e) {
            model.addAttribute("students", List.of());
            model.addAttribute("errorMessage", e.getMessage());
        }
        model.addAttribute("singleView", true);
        return "students";
    }
}
