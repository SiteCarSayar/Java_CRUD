package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

	Optional<Subject> findBySubjectName(String subjectName);
}
