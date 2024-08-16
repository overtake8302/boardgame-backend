package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserInfoResponseDto {

    private Long id;
    private String username;
    private String role;
    private Integer age;
    private String phonenumber;
    private String name;
    private String profileImageUrl; // 프로필 사진 URL 필드 추가
}
