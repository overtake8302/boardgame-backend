package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.*;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
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
    public ResponseEntity<CommonResponse<Page<MyPostResponseDto>>> getMyPosts(
            @CurrentUser User user,
            @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyPostResponseDto> myPosts = userService.findMyPosts(user, pageable);

        CommonResponse<Page<MyPostResponseDto>> response = CommonResponse.<Page<MyPostResponseDto>>builder()
                .payload(myPosts)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my/comments")
    public ResponseEntity<CommonResponse<Page<MyCommentResponseDto>>> getMyComments(
            @CurrentUser User user,
            @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyCommentResponseDto> myComments = userService.findMyComments(user, pageable);

        CommonResponse<Page<MyCommentResponseDto>> response = CommonResponse.<Page<MyCommentResponseDto>>builder()
                .payload(myComments)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<FriendInfoResponseDto>> getFriendInfo(@PathVariable Long userId) {

        FriendInfoResponseDto friendInfoResponseDto = userService.findUserByUserId(userId);

        CommonResponse<FriendInfoResponseDto> response = CommonResponse.<FriendInfoResponseDto>builder()
                .payload(friendInfoResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateUser(
            @CurrentUser User user,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage){

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setAge(age);
        updateUserDTO.setPhonenumber(phonenumber);
        updateUserDTO.setName(name);
        updateUserDTO.setProfileImage(profileImage);

        System.out.println("cage : " + updateUserDTO.getAge());
        System.out.println("cphone : " + updateUserDTO.getPhonenumber());
        System.out.println("cname :" + updateUserDTO.getName());
        System.out.println("curl : " + updateUserDTO.getProfileImage());


        userService.updateUser(user, updateUserDTO);
        return ResponseEntity.ok("update ok");
    }

    @GetMapping("/user/search")
    public CommonResponse<Page<SearchResponse>> searchUsers(@ModelAttribute SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        String keyword = searchRequest.getKeyword();
        return userService.searchUsersByKeyword(keyword, pageable);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<CommonResponse<String>> withdrawUser(
            @CurrentUser User user) {
        try {
            userService.withdrawUser(user);
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("User successfully withdrawn")
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("Failed to withdraw user")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
