package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.*;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.exceptions.UserErrorMessages;
import com.elice.boardgame.common.exceptions.UserException;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.CommentRepository;
import com.elice.boardgame.post.repository.PostRepository;
import com.elice.boardgame.post.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final S3Uploader s3Uploader;

public UserInfoResponseDto getMyInfo(User user) {
    UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
    // 사용자의 정보를 UserInfoResponseDto로 매핑
    userInfoResponseDto.setId(user.getId());
    userInfoResponseDto.setUsername(user.getUsername());
    userInfoResponseDto.setRole(user.getRole());
    userInfoResponseDto.setAge(user.getAge());
    userInfoResponseDto.setPhonenumber(user.getPhonenumber());
    userInfoResponseDto.setName(user.getName());
    userInfoResponseDto.setProfileImageUrl(user.getProfileImageUrl()); // 프로필 사진 URL 설정

    return userInfoResponseDto;
}

    public Page<MyPostResponseDto> findMyPosts(User user, Pageable pageable) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<Post> posts = postRepository.findAllByUser_IdAndDeletedAtIsNullOrderByIdDesc(user.getId(), pageable);
        if (posts == null || posts.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_POSTS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<MyPostResponseDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            MyPostResponseDto postDto = new MyPostResponseDto();
            postDto.setPostId(post.getId());
            postDto.setTitle(post.getTitle());
            postDto.setContent(post.getContent());
            postDto.setCategory(post.getCategory());

            postDto.setCreatedAt(post.getCreatedAt().toString());
            postDto.setLikeCount(post.getLikeCount());

            postDtos.add(postDto);
        }

        Pageable pageRequest = PageRequest.of(posts.getNumber(), posts.getSize(), posts.getSort());
        Page<MyPostResponseDto> postDtoPage = new PageImpl<>(postDtos, pageRequest, posts.getTotalElements());

        return postDtoPage;
    }

    public Page<MyCommentResponseDto> findMyComments(User user, Pageable pageable) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<MyCommentResponseDto> comments = commentRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId(), pageable);
        if (comments == null || comments.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_COMMENTS_NOT_FOUNT, HttpStatus.NOT_FOUND);
        }
        /*List<MyCommentResponseDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            MyCommentResponseDto commentDto = new MyCommentResponseDto();
            commentDto.setContent(comment.getContent());
            commentDto.setId(comment.getId());
            commentDto.setCreatedAt(comment.getCreatedAt().toString());

            commentDtos.add(commentDto);
        }

        Pageable pageRequest = PageRequest.of(comments.getNumber(), comments.getSize(), comments.getSort());
        Page<MyCommentResponseDto> commentDtostoPage = new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());

        return commentDtostoPage;*/

        return comments;
    }

    public FriendInfoResponseDto findUserByUserId(Long userId) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional == null || userOptional.isEmpty() || userOptional.get().getId() == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        FriendInfoResponseDto userInfoResponseDto = new FriendInfoResponseDto();
        userInfoResponseDto.setId(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setProfileImageUrl(user.getProfileImageUrl());

        return userInfoResponseDto;
    }

    public void updateUser(User user, UpdateUserDTO updateUserDTO) {

        if (updateUserDTO.getAge() != null) {
            user.setAge(updateUserDTO.getAge());
        }

        if (updateUserDTO.getPhonenumber() != null) {
            user.setPhonenumber(updateUserDTO.getPhonenumber());
        }

        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }

        // 이미지 업로드 처리
        if (updateUserDTO.getProfileImage() != null && !updateUserDTO.getProfileImage().isEmpty()) {
            try {
                String imageUrl = s3Uploader.uploadFile(updateUserDTO.getProfileImage());
                user.setProfileImageUrl(imageUrl); // User 클래스에 profileImageUrl 필드가 필요함
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
            }
        }
        userRepository.save(user);
    }

    public CommonResponse<Page<SearchResponse>> searchUsersByKeyword(String keyword, Pageable pageable) {
        Page<SearchResponse> searchResults = userRepository.searchUsersByKeyword(keyword, pageable);

        return CommonResponse.<Page<SearchResponse>>builder()
            .payload(searchResults)
            .message("Search completed successfully")
            .status(200)
            .build();
    }

    public Page<MyPostResponseDto> findUserPosts(Long userId, Pageable pageable) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional == null || userOptional.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        Page<Post> posts = postRepository.findAllByUser_IdAndDeletedAtIsNullOrderByIdDesc(user.getId(), pageable);
        if (posts == null || posts.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_POSTS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<MyPostResponseDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            MyPostResponseDto postDto = new MyPostResponseDto();
            postDto.setPostId(post.getId());
            postDto.setTitle(post.getTitle());
            postDto.setContent(post.getContent());
            postDto.setCategory(post.getCategory());

            postDto.setCreatedAt(post.getCreatedAt().toString());
            postDto.setLikeCount(post.getLikeCount());

            postDtos.add(postDto);
        }

        Pageable pageRequest = PageRequest.of(posts.getNumber(), posts.getSize(), posts.getSort());
        Page<MyPostResponseDto> postDtoPage = new PageImpl<>(postDtos, pageRequest, posts.getTotalElements());

        return postDtoPage;
    }

    public Page<MyCommentResponseDto> findUserComments(Long userId, Pageable pageable) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional == null || userOptional.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        Page<MyCommentResponseDto> comments = commentRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId(), pageable);
        if (comments == null || comments.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_COMMENTS_NOT_FOUNT, HttpStatus.NOT_FOUND);
        }

        return comments;
    }

    public void withdrawUser(User user) {
        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // User의 상태를 'withdraw'로 변경
        user.setUserState("withdraw");

        userRepository.save(user);
    }
}
