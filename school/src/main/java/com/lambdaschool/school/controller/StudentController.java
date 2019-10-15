package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.Course;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    @Autowired
    private StudentService studentService;

    // Please note there is no way to add students to course yet!

    //students/students/paging?page=1&size=10&sort=studname
    @ApiOperation(value = "Rerturn All Students With Pages", response = Student.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List Of All Students", response = Student.class)})
    @GetMapping(value = "/students/paging", produces = {"application/json"})
    public ResponseEntity<?> listAllStudentsByPage(
            @PageableDefault(page = 1,
                    size = 3)
                    Pageable pageable)
    {
        List<Student> myStudents = studentService.findAllPageable(pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "List of Students", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List Of All Students", response = Student.class)})
    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents()
    {
        List<Student> myStudents = studentService.findAll();
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Student By ID", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Found Student", response = Student.class),
            @ApiResponse(code = 404, message = "Student Not Found", response = EntityNotFoundException.class)})
    @GetMapping(value = "/Student/{StudentId}",
            produces = {"application/json"})
    public ResponseEntity<?> getStudentById(
            @ApiParam(value = "ID of Student to Return", required = true, example = "1")
            @PathVariable
                    Long StudentId)
    {
        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Student By Name", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Found Student", response = Student.class),
            @ApiResponse(code = 404, message = "Student Not Found", response = EntityNotFoundException.class)})
    @GetMapping(value = "/student/namelike/{name}",
            produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(
            @ApiParam(value = "Name of Student to Return", required = true, example = "Bob Student")
            @PathVariable String name)
    {
        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "Create New Student", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Student Created", response = Student.class),
            @ApiResponse(code = 400, message = "Incorrect Information", response = URISyntaxException.class)})
    @PostMapping(value = "/Student",
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseEntity<?> addNewStudent(@ApiParam(value = "Student Info", required = true, example = "studname:Student Name")
                                           @Valid
                                           @RequestBody
                                                   Student newStudent) throws URISyntaxException
    {
        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update Student By ID", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Student Updated", response = Student.class),
            @ApiResponse(code = 400, message = "ID Not Found", response = EntityNotFoundException.class)})
    @PutMapping(value = "/Student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @ApiParam(value = "Student Info", required = true, example = "studname:Student Name")
            @RequestBody
                    Student updateStudent,
            @ApiParam(value = "Student ID", required = true, example = "1")
            @PathVariable
                    long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Student By ID", response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Student Deleted", response = Student.class),
            @ApiResponse(code = 400, message = "ID Not Found", response = EntityNotFoundException.class)})
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @ApiParam(value = "Student ID", required = true, example = "1")
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
