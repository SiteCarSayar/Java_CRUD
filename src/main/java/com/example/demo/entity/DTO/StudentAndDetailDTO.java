package com.example.demo.entity.DTO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentAndDetailDTO {

	private String studentName;
    private String fatherName;
    private String nrcNo;
    private LocalDate dateOfBirth;
    private String gender; // "MALE" / "FEMALE"
    private String grade; 
    private String nationality;
    private String phoneNumber;
    private String email;
    private String address;
    private MultipartFile file; // optional
    private List<StudentDetailDTO> marks;
}
