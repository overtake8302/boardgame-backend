package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.*;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.service.UserService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.dto.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "내 정보 조회", description = "현재 인증된 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> getMyInfo(
            @CurrentUser User user) {

        UserInfoResponseDto userInfoResponseDto = userService.getMyInfo(user);

        CommonResponse<UserInfoResponseDto> response = CommonResponse.<UserInfoResponseDto>builder()
                .payload(userInfoResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "내 게시물 조회", description = "현재 인증된 사용자의 게시물을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 게시물 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyPostResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    @GetMapping("/my/posts")
    public ResponseEntity<CommonResponse<Page<MyPostResponseDto>>> getMyPosts(
            @CurrentUser User user,
            @Parameter(description = "페이지네이션 정보", required = true) @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyPostResponseDto> myPosts = userService.findMyPosts(user, pageable);

        CommonResponse<Page<MyPostResponseDto>> response = CommonResponse.<Page<MyPostResponseDto>>builder()
                .payload(myPosts)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "내 댓글 조회", description = "현재 인증된 사용자의 댓글을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 댓글 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyCommentResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    @GetMapping("/my/comments")
    public ResponseEntity<CommonResponse<Page<MyCommentResponseDto>>> getMyComments(
            @CurrentUser User user,
            @Parameter(description = "페이지네이션 정보", required = true) @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyCommentResponseDto> myComments = userService.findMyComments(user, pageable);

        CommonResponse<Page<MyCommentResponseDto>> response = CommonResponse.<Page<MyCommentResponseDto>>builder()
                .payload(myComments)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "친구 정보 조회", description = "주어진 사용자 ID로 친구의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendInfoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "친구를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<FriendInfoResponseDto>> getFriendInfo(
            @PathVariable Long userId) {

        FriendInfoResponseDto friendInfoResponseDto = userService.findUserByUserId(userId);

        CommonResponse<FriendInfoResponseDto> response = CommonResponse.<FriendInfoResponseDto>builder()
                .payload(friendInfoResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "사용자 정보 업데이트",
            description = "현재 인증된 사용자의 정보를 업데이트합니다.",
            parameters = {
                    @Parameter(name = "age", description = "사용자의 나이", required = false, schema = @Schema(type = "integer", example = "20")),
                    @Parameter(name = "phonenumber", description = "사용자의 전화번호", required = false, schema = @Schema(type = "string", example = "010-0000-0000")),
                    @Parameter(name = "name", description = "사용자의 이름", required = false, schema = @Schema(type = "string", example = "elice2")),
                    @Parameter(name = "profileImage", description = "사용자의 프로필 이미지", required = false, schema = @Schema(type = "string", format = "binary"))
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(
            @CurrentUser User user,
            @Parameter(description = "사용자의 나이", required = false) @RequestParam(value = "age", required = false) Integer age,
            @Parameter(description = "사용자의 전화번호", required = false) @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @Parameter(description = "사용자의 이름", required = false) @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "사용자의 프로필 이미지", required = false) @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setAge(age);
        updateUserDTO.setPhonenumber(phonenumber);
        updateUserDTO.setName(name);
        updateUserDTO.setProfileImage(profileImage);

        userService.updateUser(user, updateUserDTO);
        return ResponseEntity.ok("update ok");
    }

    @Operation(summary = "사용자 검색", description = "주어진 검색 조건에 맞는 사용자를 페이지네이션하여 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @GetMapping("/user/search")
    public CommonResponse<Page<SearchResponse>> searchUsers(
            @Parameter(description = "검색 요청 정보", required = true) @ModelAttribute SearchRequest searchRequest) {

        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        String keyword = searchRequest.getKeyword();
        return userService.searchUsersByKeyword(keyword, pageable);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 인증된 사용자를 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
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

    @Operation(summary = "특정 사용자의 게시물 조회", description = "주어진 사용자 ID로 해당 사용자의 게시물을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 게시물 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyPostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{userId}/posts")
    public ResponseEntity<CommonResponse<Page<MyPostResponseDto>>> getUserPosts(
            @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보", required = true) @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyPostResponseDto> userPosts = userService.findUserPosts(userId, pageable);

        CommonResponse<Page<MyPostResponseDto>> response = CommonResponse.<Page<MyPostResponseDto>>builder()
                .payload(userPosts)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "특정 사용자의 댓글 조회", description = "주어진 사용자 ID로 해당 사용자의 댓글을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 댓글 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyCommentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{userId}/comments")
    public ResponseEntity<CommonResponse<Page<MyCommentResponseDto>>> getUserComments(
            @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보", required = true) @ModelAttribute PaginationRequest paginationRequest) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size);

        Page<MyCommentResponseDto> userComments = userService.findUserComments(userId, pageable);

        CommonResponse<Page<MyCommentResponseDto>> response = CommonResponse.<Page<MyCommentResponseDto>>builder()
                .payload(userComments)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
