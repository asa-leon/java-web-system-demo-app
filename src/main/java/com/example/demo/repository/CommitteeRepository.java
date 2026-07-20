package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Committee;

public interface CommitteeRepository extends JpaRepository<Committee, Long> {
	
}
