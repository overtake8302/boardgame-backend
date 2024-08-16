package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserDTO {
    private Integer age;
    private String phonenumber;
    private String name;
    private MultipartFile profileImage; // 프로필 이미지 필드 추가
}
