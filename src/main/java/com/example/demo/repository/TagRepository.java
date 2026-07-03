package com.example.demo.repository;

import com.example.demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long>{
    
    // タグ名（例："Java"）でデータベースを検索する
    Optional<Tag> findByName(String name);
}
