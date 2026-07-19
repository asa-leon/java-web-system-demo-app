package com.example.demo.repository;

import com.example.demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>{
    
    // タグ名（例："Java"）でデータベースを検索する
    Optional<Tag> findByName(String name);

    // 中間テーブル（bill_tags）での登場回数が多い順に、上位5件のタグを抜き出すSQL
    @Query(value = "SELECT t.* FROM tags t " +
               "LEFT JOIN bill_tags bt ON t.id = bt.tag_id " +
               "LEFT JOIN bills b ON bt.bill_id = b.id " +
               "WHERE b.created_at >= DATE_SUB(NOW(), INTERVAL 3 DAY) " + // 💡 '3 DAYS' とシングルクォーテーションで囲む
               "GROUP BY t.id " +
               "ORDER BY COUNT(bt.bill_id) DESC " +
               "LIMIT 5", nativeQuery = true)
    List<Tag> findTop5Trends();
}
