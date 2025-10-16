package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentDetailId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4236558986193718802L;

	private Long studentID;
	private Integer subjectCode;
	
	// IMPORTANT: equals() and hashCode() must be implemented
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentDetailId)) return false;
        StudentDetailId that = (StudentDetailId) o;
        return Objects.equals(studentID, that.studentID) &&
               Objects.equals(subjectCode, that.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, subjectCode);
    }
}
