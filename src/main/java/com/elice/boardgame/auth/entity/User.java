package com.elice.boardgame.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //로그인시 id,email
    @Column(unique = true)
    private String username;
    private String password;

    private String role;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private String phonenumber;

    @Column(nullable = true)
    private String location;

    @Column(nullable = true)
    private String detail_location;

    @Column(nullable = true)
    private Integer post_code;

    @Column(nullable = true)
    private String name;
}
