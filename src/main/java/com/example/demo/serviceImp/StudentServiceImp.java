package com.example.demo.serviceImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.PageResponse;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentServiceImp implements StudentService {

	private final StudentRepository studentRepository;

	public StudentServiceImp(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Override
	public List<Student> getAllStudents() {
		// TODO Auto-generated method stub
		List<Student> students = studentRepository.findAll();
		return students;
	}

	@Override
	public Student getByID(Long id) {
		// TODO Auto-generated method stub
		Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Sudent not found"));
		return student;
	}

	@Override
	public Student saveStudent(Student st) {
		// TODO Auto-generated method stub

		if (studentRepository.existsById(st.getStudentID())) {
			throw new IllegalArgumentException("Student with this id exist");
		}
		return studentRepository.save(st);
	}

	@Override
	public Student updateStudent(Student st, Long id, MultipartFile file) {
		// TODO Auto-generated method stub
		if (!studentRepository.existsById(id)) {
			throw new RuntimeException("Student not found with id " + id);
		}
		st.setStudentID(id);
		// If a file is provided, set file details
		try {
			if (file != null && !file.isEmpty()) {
				st.setFileName(file.getOriginalFilename());
				st.setFileType(file.getContentType());
				st.setFileData(file.getBytes());
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to read file data", e);
		}

		return studentRepository.save(st);

	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		Student deleteStudent = studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student with this id not found"));

		studentRepository.delete(deleteStudent);

	}

	// Pagination method
	@Override
	public PageResponse<Student> getStudentsWithOffset(int offset, int limit) {
		// TODO Auto-generated method stub
		int pageNumber = offset / limit;
		Pageable pageable = PageRequest.of(pageNumber, limit ,Sort.by("studentID").descending());
		Page<Student> studentPage = studentRepository.findAll(pageable);

		return new PageResponse<>(studentPage.getContent(), studentPage.getTotalElements());
	}

	@Override
	public List<Student> searchStudents(String keyword) {
		// TODO Auto-generated method stub
		return studentRepository.searchByKeyword(keyword);
	}

	@Override
	public boolean checkEmailExists(String email) {
		// TODO Auto-generated method stub
		return studentRepository.existsByEmail(email);
	}

	@Override
	public boolean checkNrcNoExists(String nrcNo) {
		// TODO Auto-generated method stub
		return studentRepository.existsByNrcNo(nrcNo);
	}
}
