package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.UserInfoResponseDto;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.common.exceptions.UserErrorMessages;
import com.elice.boardgame.common.exceptions.UserException;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.CommentRepository;
import com.elice.boardgame.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserInfoResponseDto getMyInfo(User user, boolean wantPosts, boolean wantComments) {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setId(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setName(user.getName());
        userInfoResponseDto.setAge(user.getAge());
        userInfoResponseDto.setPhonenumber(user.getPhonenumber());
        userInfoResponseDto.setLocation(user.getLocation());
        userInfoResponseDto.setDetail_location(user.getDetail_location());
        userInfoResponseDto.setPost_code(user.getPost_code());
        userInfoResponseDto.setRole(user.getRole());

        if (wantPosts) {
            List<Post> posts = postRepository.findAllByUser_Id(user.getId());
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
                postDto.setImageName(post.getImageName());
                postDto.setImageUrl(post.getImageUrl());
                postDto.setCreatedAt(post.getCreatedAt());
                postDtos.add(postDto);
            }
            userInfoResponseDto.setPostDtos(postDtos);
        }

        if (wantComments) {
            List<Comment> comments = commentRepository.findAllByUser_Id(user.getId());
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
        }

        return userInfoResponseDto;
    }
}
