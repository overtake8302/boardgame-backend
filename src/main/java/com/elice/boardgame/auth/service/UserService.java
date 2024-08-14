package com.elice.boardgame.auth.service;

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
    private final UserRepository userRepository;

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

        /*if (wantPosts) {
            List<Post> posts = postRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId());
            if (posts == null || posts.isEmpty()) {
                throw new UserException(UserErrorMessages.USER_POSTS_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            List<PostDto> postDtos = new ArrayList<>();
            for (Post post : posts) {
                PostDto postDto = new PostDto();
                postDto.setPostId(post.getId());
                postDto.setTitle(post.getTitle());
                postDto.setContent(post.getContent());
                postDto.setCategory(post.getCategory());

                postDto.setCreatedAt(post.getCreatedAt().toString());
                postDto.setGameId(post.getId());
                postDto.setGameName(post.getGameName());
                postDto.setGameImageUrl(post.getGameImageUrl());
//                postDto.setImageUrls(post.getImageUrls());
//                postDto.setImageNames(post.getImageNames());
                postDto.setLikeCount(post.getLikeCount());

                postDtos.add(postDto);
            }
            userInfoResponseDto.setPostDtos(postDtos);
        }

        if (wantComments) {
            List<Comment> comments = commentRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId());
            if (comments == null || comments.isEmpty()) {
                throw new UserException(UserErrorMessages.USER_COMMENTS_NOT_FOUNT, HttpStatus.NOT_FOUND);
            }
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment comment : comments) {
                CommentDto commentDto = new CommentDto();
                commentDto.setId(comment.getId());
                commentDto.setContent(comment.getContent());
                commentDtos.add(commentDto);
            }
            userInfoResponseDto.setCommentDtos(commentDtos);
        }*/

        return userInfoResponseDto;
    }

    public Page<PostDto> findMyPosts(User user, Pageable pageable) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<Post> posts = postRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId(), pageable);
        if (posts == null || posts.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_POSTS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            PostDto postDto = new PostDto();
            postDto.setPostId(post.getId());
            postDto.setTitle(post.getTitle());
            postDto.setContent(post.getContent());
            postDto.setCategory(post.getCategory());

            postDto.setCreatedAt(post.getCreatedAt().toString());
            postDto.setGameId(post.getId());
            postDto.setGameName(post.getGameName());
            postDto.setGameImageUrl(post.getGameImageUrl());
            postDto.setImageUrls(post.getImageUrls());
            postDto.setImageNames(post.getImageNames());
            postDto.setLikeCount(post.getLikeCount());

            postDtos.add(postDto);
        }

        Pageable pageRequest = PageRequest.of(posts.getNumber(), posts.getSize(), posts.getSort());
        Page<PostDto> postDtoPage = new PageImpl<>(postDtos, pageRequest, posts.getTotalElements());

        return postDtoPage;
    }

    public Page<CommentDto> findMyComments(User user, Pageable pageable) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<Comment> comments = commentRepository.findAllByUser_IdAndDeletedAtIsNull(user.getId(), pageable);
        if (comments == null || comments.isEmpty()) {
            throw new UserException(UserErrorMessages.USER_COMMENTS_NOT_FOUNT, HttpStatus.NOT_FOUND);
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent(comment.getContent());

            commentDto.setCreatedAt(comment.getCreatedAt().toString());

            commentDtos.add(commentDto);
        }

        Pageable pageRequest = PageRequest.of(comments.getNumber(), comments.getSize(), comments.getSort());
        Page<CommentDto> commentDtostoPage = new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());

        return commentDtostoPage;
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
