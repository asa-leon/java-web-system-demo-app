package com.example.demo.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.Bill;

//MARK: JpaSpecificationExecutor を継承することで findAll(spec, sort) が使えるようになる
public interface BillRepository extends JpaRepository<Bill, Long>/* , JpaSpecificationExecutor<Post> */ {
    // 基本的なCRUD操作はこれだけで自動実装される

    // 簡単な一行繋ぎで検索条件を定義する方法 
    // 投稿日時（createdAt）の最新順（降順）で全件取得するメソッドを追加
    List<Bill> findAllByOrderByCreatedAtDesc();

    // 投稿内容（content）にキーワードが含まれる（Containing）投稿を最新順（OrderByCreatedAtDesc順）で検索するメソッドを追加
    // 条件が（And や Or）で複数の条件が足される場合は、後述の方法が良い。
    List<Bill> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    // 特定のユーザーID（user.id）に一致する投稿を、最新順（OrderByCreatedAtDesc）で取得するメソッド
    List<Bill> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 指定されたユーザーIDのリスト（コレクション）に含まれる投稿だけを、新着順に取得する
    List<Bill> findByUserIdInOrderByCreatedAtDesc(Collection<Long> userIds);
    
    // 紐づいているタグの名前（tags.name）を条件に、投稿一覧を検索する
    List<Bill> findByTagsName(String tagName);

}