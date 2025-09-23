package com.example.demo.serviceImp;

import java.util.List;


import org.springframework.stereotype.Service;


import com.example.demo.entity.Student;

import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentServiceImp implements StudentService {
	
	private final StudentRepository studentRepository;
	
	
	public StudentServiceImp(StudentRepository studentRepository) {
		this.studentRepository=studentRepository;
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
	public Student updateStudent(Student st,Long id) {
		// TODO Auto-generated method stub
		   if (!studentRepository.existsById(id)) {
		        throw new RuntimeException("Student not found with id " + id);
		    }
		   st.setStudentID(id);
		   return studentRepository.save(st);
		
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		Student deleteStudent=studentRepository.findById(id)
			    .orElseThrow(() -> new RuntimeException("Student with this id not found"));
				
		studentRepository.delete(deleteStudent);
		
	}


}
