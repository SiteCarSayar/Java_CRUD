package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(StudentDetailId.class)
public class StudentDetail implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7377486591967230963L;

	@Id
	@Column(name = "student_ID", nullable = false)
	private Long studentID;

	@Id
	@Column(name = "subject_code")
	private Integer subjectCode;

	
	@Column(nullable = false)
	private Double mark;
	
	
}
