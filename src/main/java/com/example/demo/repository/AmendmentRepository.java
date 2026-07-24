package com.example.demo.repository;

import com.example.demo.model.Amendment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmendmentRepository extends JpaRepository<Amendment, Long> {

	// 特定の法案に提出された修正案一覧を日時降順で取得
	List<Amendment> findByBillIdOrderByCreatedAtDesc(Long billId);

	// 特定の法案に対する修正案の件数取得
	long countByBillId(Long billId);
}