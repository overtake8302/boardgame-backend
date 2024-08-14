package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private Integer age;
    private String phonenumber;
    private String name;
}