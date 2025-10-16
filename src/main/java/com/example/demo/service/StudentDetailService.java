package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import com.example.demo.entity.Student;
import com.example.demo.entity.StudentDetail;
import com.example.demo.entity.DTO.StudentAndDetailDTO;
import com.example.demo.entity.DTO.StudentDetailDTOReq;
import com.example.demo.entity.DTO.StudentDetailDeleteReq;


public interface StudentDetailService {

	// Method to save student marks
    public List<StudentDetail> saveStudentMarks(StudentDetailDTOReq request);
	
	public Student createStudentWithMarks(StudentAndDetailDTO request) throws IOException;
	
	public List<StudentDetail> getStudentMarks(Long studentID);
	
	public List<StudentDetail> updateStudentMarks(StudentDetailDTOReq request);
	
	public void deleteStudentDetail(StudentDetailDeleteReq request);
}
