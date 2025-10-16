package com.example.demo.controller;

import java.io.IOException;
import com.example.demo.Enum.Grade;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.demo.Enum.Gender;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.Period;

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
			@RequestParam(defaultValue = "10") int limit, HttpServletResponse response) throws IOException {

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

	@GetMapping("/searchByGender")
	public ResponseEntity<List<Student>> searchByGender(@RequestParam("keyword") String keyword) {
		List<Student> students = studentService.searchByGender(keyword);
		return ResponseEntity.ok(students);
	}

	@GetMapping("/checkEmail")
	public ResponseEntity<Optional<Student>> checkEmailExists(@RequestParam("email") String email) {
		Optional<Student> exists = studentService.checkEmailExists(email);
		return ResponseEntity.ok(exists);
	}

	@GetMapping("/checkNrcNo")
	public ResponseEntity<Optional<Student>> checkNrcNoExists(@RequestParam("nrcNo") String nrcNo) {
		Optional<Student> exists = studentService.checkNrcNoExists(nrcNo);
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
	public void exportAllStudent(HttpServletResponse response) throws IOException {
		List<Student> students = studentRepository.findAll(Sort.by("studentID").ascending());
		ExcelExporter.exportStudentList(response, students, "All Students");
	}

	@GetMapping("/export/{id}")
	public void exportStudentByID(@PathVariable Long id, HttpServletResponse response) throws IOException {
		Student student = studentRepository.findById(id).orElse(null);
		if (student == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("Student with this ID not found");
			return;
		}
		ExcelExporter.exportStudentList(response, List.of(student), "Student_ID-" + id);
	}

	@GetMapping("/exportBysearch")
	public void exportStudentBySearch(@RequestParam("keyword") String keyword, HttpServletResponse response)
			throws IOException {
		List<Student> students = studentService.searchStudents(keyword);

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=searched_students.xlsx");

		ExcelExporter.exportStudentList(response, students, "Searched Students");
	}

	// Import students from Excel
	@PostMapping("/import")
	public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Please upload a valid Excel file."));
		}

		try {
			List<Student> students = ExcelExporter.importStudentList(file);
			int importedCount = 0;
			List<String> messages = new ArrayList<>();

			for (Student student : students) {
				List<String> rowErrors = new ArrayList<>();

				// ---- Check ID existence ----
				if (student.getStudentID() != null && studentRepository.existsById(student.getStudentID())) {
					rowErrors.add("Student ID already exists: " + student.getStudentID());
					messages.add(String.join("; ", rowErrors));
					continue; // Skip further validation for this row
				}

				// ---- Required fields ----
				if (student.getStudentName() == null || student.getStudentName().isBlank()) {
					rowErrors.add("Student Name is required.");
				}
				if (student.getDateOfBirth() == null) {
					rowErrors.add("Date of Birth is required.");
				} else if (!isOver18(student.getDateOfBirth())) {
					rowErrors.add("Student must be over 5 years old.");
				}

				// ---- Gender validation ----
				String genderInput = student.getGenderString(); // raw Excel value

				if (genderInput == null || genderInput.isBlank()) {
					// Step 1: required check
					rowErrors.add("Gender is required");
				} else {
					// Step 2: invalid check
					Gender genderEnum = parseGender(genderInput);
					if (genderEnum == null) {
						rowErrors.add("Invalid Gender: " + genderInput);
					} else {
						student.setGender(genderEnum); // store enum in DB
					}
				}

				// ---- NRC validation ----
				if (student.getNrcNo() == null || student.getNrcNo().isBlank()) {
					rowErrors.add("NRC is required.");
				} else {
					// ---- Clean and uppercase NRC ----
					student.setNrcNo(cleanNrc(student.getNrcNo()).toUpperCase());
					if (!isValidNrc(student.getNrcNo())) {
						rowErrors.add("Invalid NRC format: " + student.getNrcNo());
					}
				}
				if (student.getEmail() == null || student.getEmail().isBlank()) {
					rowErrors.add("Email is required.");
				} else if (!isValidEmail(student.getEmail())) {
					rowErrors.add("Invalid Email format: " + student.getEmail());

				}

				// ---- Grade validation ----
				String gradeInput = student.getGradeString();
				if (gradeInput == null || gradeInput.isBlank()) {
					rowErrors.add("Grade is required");
				} else if (!isValidGrade(gradeInput)) {
					rowErrors.add("Invalid Grade: " + gradeInput);
				} else {
					student.setGrade(Grade.valueOf(normalizeGrade(gradeInput))); // convert to enum
				}

				// ---- Nationality validation ----
				String natInput = student.getNationality();
				if (natInput != null && !natInput.isBlank()) {
					String normalizedNat = normalizeNationality(natInput);
					if (normalizedNat == null) {
						rowErrors.add("Invalid Nationality: " + natInput);
					} else {
						student.setNationality(normalizedNat);
					}
				}
				// ---- Phone number validation ----
				if (student.getPhoneNumber() != null && !student.getPhoneNumber().isBlank()) {
					// Validate phone format first
					if (!isValidPhoneNumber(student.getPhoneNumber())) {
						rowErrors.add("Invalid Phone Number format: " + student.getPhoneNumber());
					} else {
						// Check duplicate only if valid
						Optional<Student> phoneExists = studentRepository.findByPhoneNumber(student.getPhoneNumber());
						if (phoneExists.isPresent() && (student.getStudentID() == null
								|| !phoneExists.get().getStudentID().equals(student.getStudentID()))) {

							rowErrors.add("Phone number already exists: " + student.getPhoneNumber());
						}
					}
				}

				// ---- Only check duplicates if no required/ID errors ---
				if (rowErrors.isEmpty()) {
					Optional<Student> nrcExists = studentRepository.findByNrcNo(student.getNrcNo());
					if (nrcExists.isPresent() && (student.getStudentID() == null
							|| !nrcExists.get().getStudentID().equals(student.getStudentID()))) {
						rowErrors.add("NRC already exists: " + student.getNrcNo());
					}

					Optional<Student> emailExists = studentRepository.findByEmail(student.getEmail());
					if (emailExists.isPresent() && (student.getStudentID() == null
							|| !emailExists.get().getStudentID().equals(student.getStudentID()))) {
						rowErrors.add("Email already exists: " + student.getEmail());
					}
				}
				// ---- Save or collect messages ----
				if (rowErrors.isEmpty()) {
//					if (student.getStudentID() != null && !studentRepository.existsById(student.getStudentID())) {
//					    // keep Excel ID
//					} else {
//					    student.setStudentID(null); // auto-generate
//					}
					student.setStudentID(null); // auto-generate
					studentService.setDefaultValue(student);
					studentRepository.save(student);
					importedCount++;
				} else {
					messages.add(String.join("; ", rowErrors));
				}
			}

			Map<String, Object> response = Map.of("imported", importedCount, "messages", messages);

			if (!messages.isEmpty()) {
				return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
			}

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error importing students: " + e.getMessage()));
		}
	}

	/* --------validation methods ---------------------------*/
	
	// -----email validation-----
	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
	}

	// ---- Helper method to remove spaces ----
	private String cleanNrc(String nrc) {
		if (nrc == null)
			return null;
		return nrc.trim().replaceAll("\\s+", "");
	}

	// ---- Strict Myanmar NRC validation ----
	private boolean isValidNrc(String nrc) {
		if (nrc == null)
			return false;
		String regex = "^(?:[1-9]|1[0-4])/([A-Z]{3})\\(N\\)[0-9]{6}$";
		return nrc.matches(regex);
	}

	/// ---- Validate Grade ----
	private boolean isValidGrade(String gradeStr) {
		if (gradeStr == null || gradeStr.isBlank()) {
			return false;
		}
		String normalized = normalizeGrade(gradeStr);
		try {
			Grade.valueOf(normalized);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private String normalizeGrade(String gradeStr) {
		if (gradeStr == null)
			return null;
		return gradeStr.trim().toUpperCase().replaceAll("\\s+", "_").replace("-", "_");
	}

	// ---- Normalize Nationality
	private String normalizeNationality(String nationality) {
		if (nationality == null)
			return null;
		nationality = nationality.trim().toLowerCase();
		switch (nationality) {
		case "myanmar":
			return "Myanmar";
		case "thailand":
			return "Thailand";
		case "other":
			return "Other";
		default:
			return null; // invalid
		}

	}

	private Gender parseGender(String genderInput) {
		if (genderInput == null || genderInput.isBlank())
			return null;

		switch (genderInput.trim().toLowerCase()) {
		case "male":
			return Gender.MALE;
		case "female":
			return Gender.FEMALE;
		default:
			return null; // invalid
		}
	}

	// over 18 age validation
	private boolean isOver18(LocalDate dob) {
		return dob != null && Period.between(dob, LocalDate.now()).getYears() >= 5;
	}

	// --- phone number validation ---
	private boolean isValidPhoneNumber(String phone) {
		return phone != null && phone.matches("^09\\d{9}$");
	}

}
