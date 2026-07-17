package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 同じタグが何個もできないようにuniqueにする
    private String name;

    // このタグが付いている投稿の一覧（多対多）
    @ManyToMany(mappedBy = "tags")
    private List<Bill> posts = new ArrayList<>();
}
