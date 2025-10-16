package com.example.demo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentDetailDTO {
	private Long studentID;
	private String subjectName;
    private Double mark;

}
