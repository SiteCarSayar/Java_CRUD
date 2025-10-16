package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("SELECT s FROM Student s " +
		       "WHERE LOWER(s.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR CAST(s.studentID AS string) LIKE CONCAT('%', :keyword, '%') " +
		       "OR LOWER(s.gender)  = LOWER(:keyword) " +
		       "ORDER BY s.studentID ASC")
	List<Student> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Student s WHERE LOWER(s.gender) = LOWER(:keyword)")
    List<Student> findByGender(@Param("keyword") String keyword);
    
    
    Optional<Student> findByEmail(String email);  
    Optional<Student> findByNrcNo(String nrcNo);
    Optional<Student> findByPhoneNumber(String phoneNumber);
  
    boolean existsByEmail(String email);         
    boolean existsByNrcNo(String nrcNo); 
 
}
