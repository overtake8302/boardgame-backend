package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.UpdateUserDTO;
import com.elice.boardgame.auth.dto.UserInfoResponseDto;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.service.UserService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> getMyInfo(
            @CurrentUser User user
            ) {

        UserInfoResponseDto userInfoResponseDto = userService.getMyInfo(user);

        CommonResponse<UserInfoResponseDto> response = CommonResponse.<UserInfoResponseDto>builder()
                .payload(userInfoResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my/posts")
    public ResponseEntity<CommonResponse<Page<PostDto>>> getMyPosts(
            @CurrentUser User user,
            @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<PostDto> myPosts = userService.findMyPosts(user, pageable);

        CommonResponse<Page<PostDto>> response = CommonResponse.<Page<PostDto>>builder()
                .payload(myPosts)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my/comments")
    public ResponseEntity<CommonResponse<Page<CommentDto>>> getMyComments(
            @CurrentUser User user,
            @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<CommentDto> myComments = userService.findMyComments(user, pageable);

        CommonResponse<Page<CommentDto>> response = CommonResponse.<Page<CommentDto>>builder()
                .payload(myComments)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> getUserInfo(@PathVariable Long userId) {

        UserInfoResponseDto userInfoResponseDto = userService.findUserByUserId(userId);

        CommonResponse<UserInfoResponseDto> response = CommonResponse.<UserInfoResponseDto>builder()
                .payload(userInfoResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/my")
    public ResponseEntity<CommonResponse<String>> updateUser(
            @CurrentUser User user,
            @RequestBody UpdateUserDTO updateUserDTO) {

        try {
            userService.updateUser(user, updateUserDTO);
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("User information updated successfully")
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("Failed to update user information")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public CommonResponse<Page<SearchResponse>> searchUsers(@ModelAttribute SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        String keyword = searchRequest.getKeyword();
        return userService.searchUsersByKeyword(keyword, pageable);
    }
}
