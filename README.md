### Student Registration and Management System

A small Spring MVC web application that registers students through an HTML form, stores them in PostgreSQL using plain JDBC,
and lists them on a separate page. Built to demonstrate Spring Beans, Dependency Injection, controller handler mappings and
manual SQL.

### Technologies Used
Java 17
Spring Boot 3.3.5 
Thymeleaf 
Jakarta Bean Validation
PostgreSQL + JDBC 
Maven

No Spring Data JPA or Hibernate. spring-boot-starter-jdbc is included only to provide the pooled DataSource;
every SQL statement is hand-written.


### IoC, Bean Creation and Dependency Injection
Inversion of Control (IoC): instead of our code creating and wiring its own objects with new, 
the Spring IoC container (the ApplicationContext) does it.

Bean creation: @SpringBootApplication enables component scanning of the com.example.studentmanagement package.

The DataSource bean is auto-configured by Spring Boot from the spring.datasource.* properties.

Constructor-based Dependency Injection: each class declares what it needs as constructor parameters:

DataSource -> StudentRepository -> StudentService -> StudentController
