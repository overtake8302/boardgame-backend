package com.elice.boardgame.auth.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDTO {

    private String username;
    private String password;
    private Integer age;
    private String phonenumber;
    private String location;
    private String detail_location;
    private Integer post_code;
    private String name;
}
