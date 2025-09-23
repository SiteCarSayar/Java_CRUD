package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;

@RestController
@RequestMapping("student")
public class StudentController {

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;

	}

	@GetMapping("/all")
	public ResponseEntity<List<Student>> getAll() {
		List<Student> students = studentService.getAllStudents();
		return ResponseEntity.ok(students);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {

		try {
			Student student = studentService.getByID(id);
			return ResponseEntity.ok(student);
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}

	@PostMapping("/create")
	public ResponseEntity<Student> save(@RequestBody Student student) {
		Student saved = studentService.saveStudent(student);
		return ResponseEntity.status(201).body(saved);

	}

	@PostMapping(value = "/creating", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Student> createStudent(@RequestPart("student") Student student,
			@RequestPart("file") MultipartFile file) throws IOException {

		// set file name (string)
		student.setFileName(file.getOriginalFilename());
		student.setFileType(file.getContentType());
		// set file data (bytes)
		student.setFileData(file.getBytes());

		// save to DB
		Student savedStudent = studentService.saveStudent(student);

		return ResponseEntity.ok(savedStudent);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Student student) {
		try {
			Student updated = studentService.updateStudent(student, id);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		studentService.deleteById(id);
		return ResponseEntity.ok("Deleted");
	}

}
