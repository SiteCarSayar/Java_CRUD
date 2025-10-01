package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.example.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s " +
           "WHERE LOWER(s.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR CAST(s.studentID AS string) LIKE CONCAT('%', :keyword, '%') " +
           "ORDER BY s.studentName ASC")
	List<Student> searchByKeyword(@Param("keyword") String keyword);
    
    boolean existsByEmail(String email);
    
    boolean existsByNrcNo(String nrcNo);
 
}
