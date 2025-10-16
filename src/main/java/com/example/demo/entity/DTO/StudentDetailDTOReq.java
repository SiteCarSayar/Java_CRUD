package com.example.demo.entity.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDetailDTOReq {

	private Long studentID;
	private List<StudentDetailDTO> marks;
}
