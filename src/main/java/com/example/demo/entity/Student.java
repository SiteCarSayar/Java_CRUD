package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.example.demo.Enum.*;
import com.example.demo.excelExport.ExcelColumn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "student_table")
public class Student implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 3408891154767810940L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_ID", nullable = false)
	@ExcelColumn(name = "Student ID",order = 1)
	private long studentID;

	@ExcelColumn(name = "Student Name",order = 2)
	@Column(name = "student_name", length = 100, nullable = false)
	private String studentName;

	@ExcelColumn(name = "Father Name",order = 3)
	@Column(name = "father_name", length = 100)
	private String fatherName;

	@ExcelColumn(name = "DOB",order = 4)
	private LocalDate dateOfBirth;

	@ExcelColumn(name = "Gender",order = 5)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@ExcelColumn(name = "NRC No.",order = 6)
	@Column(name = "nrc_no", length = 100, nullable = false,unique = true)
	private String nrcNo;

	@ExcelColumn(name = "Grade",order = 7)
	@Enumerated(EnumType.STRING)
	private Grade grade;

	@ExcelColumn(name = "Nationality",order = 8)
	private String nationality;

	@ExcelColumn(name = "Phone Number",order = 9)
	@Pattern(regexp = "\\d+", message = "Phone number must contain digits only")
	@Column(length = 15)
	private String phoneNumber;

	@ExcelColumn(name = "Email",order = 10)
	@Column(name = "email", unique = true, nullable = false, length = 100)
	@Email
	private String email;

	@ExcelColumn(name = "Address",order = 11)
	@Column(length = 500)
	private String address;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Lob
	@Column(name = "file_data")
	private byte[] fileData;

}
