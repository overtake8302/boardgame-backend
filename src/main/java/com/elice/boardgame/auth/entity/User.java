package com.elice.boardgame.auth.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity(name = "user")
//@Getter
//@Setter
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    //로그인시 id,email
//    @Column(unique = true)
//    private String username;
//    private String password;
//    private String role;
//
//    @Column(nullable = true)
//    private Integer age;
//
//    @Column(nullable = true)
//    private String phonenumber;
//
//    @Column(nullable = true)
//    private String name;
//
//    @Column(nullable = true)
//    private String userState;
//}

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String role;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private String phonenumber;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String userState;

    @Column(nullable = true)
    private String profileImageUrl; // 새로 추가된 프로필 이미지 URL 필드
}
