package com.example.demo.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.Post;

//MARK: JpaSpecificationExecutor を継承することで findAll(spec, sort) が使えるようになる
public interface PostRepository extends JpaRepository<Post, Long>/* , JpaSpecificationExecutor<Post> */ {
    // 基本的なCRUD操作はこれだけで自動実装される

    // 簡単な一行繋ぎで検索条件を定義する方法 
    // 投稿日時（createdAt）の最新順（降順）で全件取得するメソッドを追加
    List<Post> findAllByOrderByCreatedAtDesc();

    // 投稿内容（content）にキーワードが含まれる（Containing）投稿を最新順（OrderByCreatedAtDesc順）で検索するメソッドを追加
    // 条件が（And や Or）で複数の条件が足される場合は、後述の方法が良い。
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    /* コントローラー側でドット繋ぎ且つ（and や or）が含まれる場合はここにパーツを用意しておく（Controller側でドット繋ぎできるようにする準備）
    // ドット繋ぎのパーツ（大文字小文字を無視したあいまい検索）を定義
    static Specification<Post> containsContent(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction(); //キーワードが空なら「条件なし」にする安全装置
            }
            
            // SQLのWHERE LOWER(content) LIKE LOWER('%キーワード%')を組み立てている部分
            return cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
        };
    }
    */

    // 特定のユーザーID（user.id）に一致する投稿を、最新順（OrderByCreatedAtDesc）で取得するメソッド
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 指定されたユーザーIDのリスト（コレクション）に含まれる投稿だけを、新着順に取得する
    List<Post> findByUserIdInOrderByCreatedAtDesc(Collection<Long> userIds);
    
}