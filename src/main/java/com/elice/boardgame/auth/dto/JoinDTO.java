package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class JoinDTO {
    private String username;
    private String password;
    private Integer age;
    private String phonenumber;
    private String name;
    private MultipartFile profileImage; // 새로 추가된 이미지 필드
}
