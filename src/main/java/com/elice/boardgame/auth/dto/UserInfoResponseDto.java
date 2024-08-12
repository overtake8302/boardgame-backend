package com.elice.boardgame.auth.dto;

import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
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

    private String location;

    private String detail_location;

    private Integer post_code;

    private String name;

    List<CommentDto> commentDtos = new ArrayList<>();
    List<PostDto> postDtos = new ArrayList<>();
}
