package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.StudentDetail;
import com.example.demo.entity.StudentDetailId;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, StudentDetailId> {

	public void deleteByStudentIDAndSubjectCode(Long studentID, Integer subjectCode);

	public List<StudentDetail> findByStudentID(Long studentID);
	
	public void deleteByStudentID(Long studentID);
}
