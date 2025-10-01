package com.example.demo.controller;

import org.springframework.http.HttpHeaders;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.PageResponse;
import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;

@RestController
@RequestMapping("student")
public class StudentPaginationController {

	private final StudentService studentService;

	public StudentPaginationController(StudentService studentService) {
		this.studentService = studentService;
	}

	// Pagination using offset and limit
	@GetMapping("/getByOffset")
	public PageResponse<Student> getStudents(@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int limit) {

		return studentService.getStudentsWithOffset(offset, limit);
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

	// Download file
	@GetMapping("/download/{studentId}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long studentId) {
		Student file = studentService.getByID(studentId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.header(HttpHeaders.CONTENT_TYPE, file.getFileType()).body(file.getFileData());
	}

}
