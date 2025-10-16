package com.example.demo.serviceImp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Enum.Gender;
import com.example.demo.Enum.Grade;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentDetail;
import com.example.demo.entity.Subject;
import com.example.demo.entity.DTO.StudentAndDetailDTO;
import com.example.demo.entity.DTO.StudentDetailDTO;
import com.example.demo.entity.DTO.StudentDetailDTOReq;
import com.example.demo.entity.DTO.StudentDetailDeleteReq;
import com.example.demo.repository.StudentDetailRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.StudentDetailService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentDetailServiceImp implements StudentDetailService {

	private final StudentDetailRepository studentDetailRepository;
	private final SubjectRepository subjectRepository;
	private final StudentRepository studentRepository;
	private final StudentServiceImp st;

	public StudentDetailServiceImp(StudentDetailRepository studentDetailRepository, SubjectRepository subjectRepository,
			StudentRepository studentRepository, StudentServiceImp st) {
		this.studentDetailRepository = studentDetailRepository;
		this.studentRepository = studentRepository;
		this.subjectRepository = subjectRepository;
		this.st = st;
	}

	@Override
	public List<StudentDetail> saveStudentMarks(StudentDetailDTOReq request) {

		Long studentID = request.getStudentID();
	    if (studentID == null) {
	        throw new IllegalArgumentException("Student ID must not be null");
	    }

	    if (!studentRepository.existsById(studentID)) {
	        throw new RuntimeException("Student not found with ID: " + studentID);
	    }


	    List<StudentDetail> details = new ArrayList<>();

	    for (StudentDetailDTO markDTO : request.getMarks()) {

	        Integer subjectCode = subjectRepository.findBySubjectName(markDTO.getSubjectName())
	                .map(Subject::getSubjectCode)
	                .orElseThrow(() -> new RuntimeException("Invalid subject name: " + markDTO.getSubjectName()));

	        StudentDetail detail = new StudentDetail(
	                request.getStudentID(), // studentID from parent DTO
	                subjectCode,
	                markDTO.getMark()
	        );

	        details.add(detail);
	    }

	    return studentDetailRepository.saveAll(details);
	}


	@Override
	public void deleteStudentDetail(StudentDetailDeleteReq request) {
		// TODO Auto-generated method stub
		// Convert subject name to subject code
		Integer subjectCode = subjectRepository.findBySubjectName(request.getSubjectName()).map(Subject::getSubjectCode)
				.orElseThrow(() -> new RuntimeException("Invalid subject name: " + request.getSubjectName()));

		// Delete
		studentDetailRepository.deleteByStudentIDAndSubjectCode(request.getStudentID(), subjectCode);
	}

	// Method to get student marks by student ID
	@Override
	public List<StudentDetail> getStudentMarks(Long studentID) {
		return studentDetailRepository.findByStudentID(studentID);
	}

	@Override
	public Student createStudentWithMarks(StudentAndDetailDTO request) throws IOException {

	    // 1️⃣ Map DTO -> Student entity
	    Student student = new Student();
	    student.setStudentName(request.getStudentName());
	    student.setFatherName(request.getFatherName());
	    student.setDateOfBirth(request.getDateOfBirth());
	    student.setGender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null);
	    student.setNrcNo(request.getNrcNo());
	    student.setGrade(request.getGrade() != null ? Grade.valueOf(request.getGrade()) : null);
	    student.setNationality(request.getNationality());
	    student.setPhoneNumber(request.getPhoneNumber());
	    student.setEmail(request.getEmail());
	    student.setAddress(request.getAddress());

	    if (request.getFile() != null && !request.getFile().isEmpty()) {
	        student.setFileName(request.getFile().getOriginalFilename());
	        student.setFileType(request.getFile().getContentType());
	        student.setFileData(request.getFile().getBytes());
	    }

	    // 2️⃣ Save student
	    Student savedStudent = st.saveStudent(student);

	    // 3️⃣ Save marks
	    if (request.getMarks() != null && !request.getMarks().isEmpty()) {
	        // Wrap marks into a StudentDetailDTOReq
	        StudentDetailDTOReq markRequest = new StudentDetailDTOReq();
	        markRequest.setStudentID(savedStudent.getStudentID());
	        markRequest.setMarks(request.getMarks());

	        // Call your method
	        this.saveStudentMarks(markRequest);
	    }

	    return savedStudent;
	}

	@Override
	public List<StudentDetail> updateStudentMarks(StudentDetailDTOReq request) {
		// TODO Auto-generated method stub
		   StudentDetailDeleteReq deleteReq = new StudentDetailDeleteReq();
		    deleteReq.setStudentID(request.getStudentID());

		    // Optional: delete one by one OR implement delete by studentID in repository
		    studentDetailRepository.deleteByStudentID(request.getStudentID());

		    // Save new marks
		    return saveStudentMarks(request);
	}

}
