package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;


import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // これだけで save(), findAll(), delete()が使えるようになる。
    User findByEmail(String email);

    // ユーザーIDまたはメールアドレスで検索する（安全のためOptionalで返す）
    Optional<User> findByUserIdOrEmail(String userId, String email);

    // 中間テーブルから指定したユーザーIDのフォロー・被フォロー関係をすべて削除する
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_follows WHERE user_id = :id OR follow_id = :id", nativeQuery=true)
    void deleteFollowRelationsByUserId(@Param("id") Long id);

    // 削除処理の直前にSQLで自分自身の投稿に紐づく post_tags のデータを一掃する
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_tags WHERE post_id IN (SELECT id FROM posts WHERE user_id = :id)", nativeQuery = true)
    void deletePostTagsByUserId(@Param("id") Long idLong);
}
