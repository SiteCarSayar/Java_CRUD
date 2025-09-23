package com.example.demo.service;

import java.util.List;


import com.example.demo.entity.Student;

public interface StudentService {

	public List<Student> getAllStudents();
	public Student getByID(Long id);
	public Student saveStudent(Student st);
	public Student updateStudent(Student st,Long id);
	public void deleteById(Long id);
	
	
}
