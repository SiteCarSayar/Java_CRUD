package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.example.demo.Enum.*;

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
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "student_ID", nullable = false)
	private long studentID;

	@Column(name = "student_name", length = 100, nullable = false)
	private String studentName;

	@Column(name = "father_name", length = 100, nullable = false)
	private String fatherName;

	private LocalDate dateOfBirth;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "nrc_no", length = 100, nullable = false)
	private String nrcNo;

	@Enumerated(EnumType.STRING)
	private Grade grade;

	private String nationality;

	@Pattern(regexp = "\\d+", message = "Phone number must contain digits only")
	@Column(length = 15)
	private String phoneNumber;

	@Column(name = "email", unique = true, nullable = false, length = 100)
	@Email
	private String email;

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
