package com.example.demo.repository;

import com.example.demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>{
    
    // タグ名（例："Java"）でデータベースを検索する
    Optional<Tag> findByName(String name);

    // 中間テーブル（post_tags）での登場回数が多い順に、上位5件のタグを抜き出すSQL
    @Query(value = "SELECT t.* FROM tags t LEFT JOIN post_tags pt ON t.id = pt.tag_id GROUP BY t.id ORDER BY COUNT(pt.post_id) DESC LIMIT 5", nativeQuery = true)
    List<Tag> findTop5Trends();
}
