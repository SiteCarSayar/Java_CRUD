package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "subject")
public class Subject implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "subject_code")
    private Integer subjectCode;

	@Column(name = "subject_name")
    private String subjectName;
}
