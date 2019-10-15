package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.Course;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.CourseService;
import com.lambdaschool.school.view.CountStudentsInCourses;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/courses")
public class CourseController
{
    @Autowired
    private CourseService courseService;

    //courses/course/?page=1&size=10&sort=coursename
    @ApiOperation(value="Return all Courses", response=Course.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @GetMapping(value = "/courses/paging",
            produces = {"application/json"})
    public ResponseEntity<?> listAllCoursesByPage(
            @PageableDefault(page = 0,
                    size = 3)
                    Pageable pageable)
    {
        List<Course> myCourses = courseService.listPageableCourses(pageable);
        return new ResponseEntity<>(myCourses, HttpStatus.OK);
    }


    @ApiOperation(value="List All Courses", response= Course.class)
    @ApiResponses(value={@ApiResponse(code=200,message="List of All Courses",response = Course.class)})
    @GetMapping(value = "/courses", produces = {"application/json"})
    public ResponseEntity<?> listAllCourses()
    {
        ArrayList<Course> myCourses = courseService.findAll();
        return new ResponseEntity<>(myCourses, HttpStatus.OK);
    }

    @ApiOperation(value="Count Students By Course", response=Course.class)
    @ApiResponses(value={@ApiResponse(code=200,message="Returned Student Counts",response = CountStudentsInCourses.class)})
    @GetMapping(value = "/studcount", produces = {"application/json"})
    public ResponseEntity<?> getCountStudentsInCourses()
    {
        return new ResponseEntity<>(courseService.getCountStudentsInCourse(), HttpStatus.OK);
    }

    @ApiOperation(value="Get Student By ID", response=Course.class)
    @ApiResponses(value={@ApiResponse(code=200,message="Found Student",response = Course.class),
            @ApiResponse(code=404, message="Student Not Found", response= EntityNotFoundException.class)})
    @DeleteMapping("/courses/{courseid}")
    public ResponseEntity<?> deleteCourseById(
            @ApiParam(value="Id of Course",required=true,example="1")
            @PathVariable long courseid)
    {
        courseService.delete(courseid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
