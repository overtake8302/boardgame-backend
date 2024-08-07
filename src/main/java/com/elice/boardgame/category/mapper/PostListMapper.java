package com.elice.boardgame.category.mapper;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PostListMapper {

    public static Page<PostListResponseDto> toDtoPage(Page<Post> posts) {
        return posts.map(post -> new PostListResponseDto(
            post.getId(),
            post.getCategory().name(),
            post.getTitle(),
            post.getUser().getUsername(),
            post.getCreatedAt(),
            (long) post.getView().getViewCount(),
            post.getLikes(),
            (long) post.getComments().size()
        ));
    }
}
