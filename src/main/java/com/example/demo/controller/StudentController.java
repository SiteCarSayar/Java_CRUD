package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.demo.entity.StudentDetail;
import com.example.demo.entity.DTO.StudentAndDetailDTO;
import com.example.demo.entity.DTO.StudentDetailDTOReq;
import com.example.demo.entity.DTO.StudentDetailDeleteReq;
import com.example.demo.service.StudentDetailService;
import com.example.demo.service.StudentService;

@RestController
@RequestMapping("student")
public class StudentController {

	private final StudentService studentService;
	private final StudentDetailService studentDetailService;

	public StudentController(StudentService studentService, StudentDetailService studentDetailService) {
		this.studentService = studentService;
		this.studentDetailService = studentDetailService;

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

	// for student detail table get student marks and subject
	// GET all marks for a specific student
	@GetMapping("/student-mark/{studentID}")
	public ResponseEntity<List<StudentDetail>> getMarksByStudentID(@PathVariable Long studentID) {
		List<StudentDetail> marks = studentDetailService.getStudentMarks(studentID);
		return ResponseEntity.ok(marks);
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

	@PostMapping("/create")
	public ResponseEntity<Student> save(@RequestBody Student student) {
		Student saved = studentService.saveStudent(student);
		return ResponseEntity.status(201).body(saved);

	}

// for student detail table save student marks and subject-----------------------------
	@PostMapping("/create/detail")
	public ResponseEntity<?> saveStudentMarks(@RequestBody StudentDetailDTOReq request) {
		return ResponseEntity.ok(studentDetailService.saveStudentMarks(request));
	}
	
	@PostMapping(value = "/create-with-marks", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Student> createStudentWithMarks(@RequestPart("student") StudentAndDetailDTO request,
                                                          @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // If file is sent via separate requestPart, attach it to DTO
        if (file != null && !file.isEmpty()) {
            request.setFile(file);
        }

        Student savedStudent = studentDetailService.createStudentWithMarks(request);
        return ResponseEntity.ok(savedStudent);
    }
//--------------------------------------------------------------------------------------
	@PostMapping(value = "/creating", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Transactional
	public ResponseEntity<Student> createStudent(@RequestPart("student") Student student
			, @RequestPart(value = "file", required = false) MultipartFile file)
			throws IOException {

		// ✅ File info 
		if (file != null && !file.isEmpty()) {
		student.setFileName(file.getOriginalFilename());
		student.setFileType(file.getContentType());
		student.setFileData(file.getBytes());
		}
		// ✅ Save student first Student savedStudent =
		Student savedStudent= studentService.saveStudent(student);


		return ResponseEntity.ok(savedStudent);
	}

	@PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart Student student,
			@RequestPart(value = "file", required = false) MultipartFile file) {

		try {
			Student updatedStudent = studentService.updateStudent(student, id, file);
			return ResponseEntity.ok(updatedStudent);

		} catch (RuntimeException e) {

			return ResponseEntity.status(404).body(e.getMessage());
		}

	}
	
	@PutMapping("/student-detail/update-marks")
	public ResponseEntity<List<StudentDetail>> updateMarks(@RequestBody StudentDetailDTOReq request) {
	    List<StudentDetail> updated = studentDetailService.updateStudentMarks(request);
	    return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		studentService.deleteById(id);
		return ResponseEntity.ok("Deleted");
	}

	// for student detail table delete mark and subject
	@DeleteMapping("/delete/student-marks")
	public ResponseEntity<?> deleteStudentSubject(@RequestBody StudentDetailDeleteReq request) {
		studentDetailService.deleteStudentDetail(request);
		return ResponseEntity.ok("Deleted successfully");
	}

}
