package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.PageResponse;
import com.example.demo.entity.Student;
import com.example.demo.excelExport.ExcelExporter;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("student")
public class StudentPaginationController {

	private final StudentService studentService;
	private final StudentRepository studentRepository;

	public StudentPaginationController(StudentService studentService, StudentRepository studentRepository) {
		this.studentService = studentService;
		this.studentRepository = studentRepository;
	}

	// Pagination using offset and limit
	@GetMapping("/getByOffsetAndLimit")
	public PageResponse<Student> getStudents(@RequestParam(defaultValue = "0") int offset,
	                                         @RequestParam(defaultValue = "10") int limit) {
	    return studentService.getStudentsWithOffset(offset, limit);
	}
	
	
	@GetMapping("/exportByPage")
	public void exportStudentsByPage(@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int limit,HttpServletResponse response) throws IOException {

		 // Set Excel response
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=students_page.xlsx");

	    // Fetch page
	    PageResponse<Student> pageResponse = studentService.getStudentsWithOffset(offset, limit);
	    List<Student> students = pageResponse.getContent();

	    // Use existing ExcelExporter
	    ExcelExporter.exportStudentList(response, students, "Students_Page");

	}

	@GetMapping("/Search")
	public ResponseEntity<List<Student>> searchStudent(@RequestParam("keyword") String keyword) {
		List<Student> students = studentService.searchStudents(keyword);
		return ResponseEntity.ok(students);
	}

	@GetMapping("/checkEmail")
	public ResponseEntity<Boolean> checkEmailExists(@RequestParam("email") String email) {
		boolean exists = studentService.checkEmailExists(email);
		return ResponseEntity.ok(exists);
	}

	@GetMapping("/checkNrcNo")
	public ResponseEntity<Boolean> checkNrcNoExists(@RequestParam("nrcNo") String nrcNo) {
		boolean exists = studentService.checkNrcNoExists(nrcNo);
		return ResponseEntity.ok(exists);
	}

	// Download file image
	@GetMapping("/download/{studentId}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long studentId) {
		Student file = studentService.getByID(studentId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.header(HttpHeaders.CONTENT_TYPE, file.getFileType()).body(file.getFileData());
	}
	
	@GetMapping("/exportAll")
	public void exportAllStudent(HttpServletResponse response)throws IOException {
		List<Student> students=studentRepository.findAll();
		ExcelExporter.exportStudentList(response, students, "All Students");
	}
	@GetMapping("/export/{id}")
	public void exportStudentByID(@PathVariable Long id,HttpServletResponse response)throws IOException{
		Student student =studentRepository.findById(id).orElse(null);
		if (student==null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("Student with this ID not found");
			return;
		}
		ExcelExporter.exportStudentList(response, List.of(student), "Student_ID-"+id);
	}
	@GetMapping("/exportBysearch")
	public void exportStudentBySearch(@RequestParam("keyword") String keyword,HttpServletResponse response)throws IOException{
		List<Student> students=studentService.searchStudents(keyword);
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=searched_students.xlsx");

		ExcelExporter.exportStudentList(response, students, "Searched Students");
	}

}
