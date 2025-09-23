package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;

@RestController
@RequestMapping("student")
public class StudentFileController {

	private final StudentService studentService;

	public StudentFileController(StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping("/{id}/file")
	public ResponseEntity<byte[]> getStudentFile(@PathVariable Long id) {
		Student student = studentService.getByID(id);
		if (student == null || student.getFileData() == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(student.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + student.getFileName() + "\"")
				.body(student.getFileData());
	}

}
