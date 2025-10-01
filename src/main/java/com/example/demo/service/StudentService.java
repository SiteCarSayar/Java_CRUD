package com.example.demo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.PageResponse;
import com.example.demo.entity.Student;

public interface StudentService {

	public List<Student> getAllStudents();
	public Student getByID(Long id);
	public Student saveStudent(Student st);
	public Student updateStudent(Student st,Long id,MultipartFile file);
	public void deleteById(Long id);
	
	// Pagination using offset and limit
	public PageResponse<Student> getStudentsWithOffset(int offset, int limit);
	
	//search student by name or studentId
	 public List<Student> searchStudents(String keyword);
	
	 // Check if email exists
	 public boolean checkEmailExists(String email);
	 // Check if NRC number exists
	 public boolean checkNrcNoExists(String nrcNo);
}
