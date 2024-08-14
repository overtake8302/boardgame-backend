package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.MyCommentResponseDto;
import com.elice.boardgame.auth.dto.MyPostResponseDto;
import com.elice.boardgame.auth.dto.UpdateUserDTO;
import com.elice.boardgame.auth.dto.UserInfoResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserInfoResponseDto getMyInfo(User user) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setId(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setName(user.getName());
        userInfoResponseDto.setAge(user.getAge());
        userInfoResponseDto.setPhonenumber(user.getPhonenumber());
        userInfoResponseDto.setRole(user.getRole());

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

    public UserInfoResponseDto findUserByUserId(Long userId) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional == null || userOptional.isEmpty() || userOptional.get().getId() == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setId(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setName(user.getName());
        userInfoResponseDto.setAge(user.getAge());
        userInfoResponseDto.setPhonenumber(user.getPhonenumber());
        userInfoResponseDto.setRole(user.getRole());

        return userInfoResponseDto;
    }

    public void updateUser(User user, UpdateUserDTO updateUserDTO) {
        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (updateUserDTO.getAge() != null) {
            user.setAge(updateUserDTO.getAge());
        }

        if (updateUserDTO.getPhonenumber() != null) {
            user.setPhonenumber(updateUserDTO.getPhonenumber());
        }

        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
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
}
