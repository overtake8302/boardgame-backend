package com.elice.boardgame.post.service;

import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.Post.Category;
import com.elice.boardgame.post.repository.PostRepository;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private S3Uploader s3Uploader;

    //  게시글 생성
    public Post createPost(Post post, String imageUrl) {
        if (imageUrl != null) {
            post.setImageUrl(imageUrl);
        }
        return postRepository.save(post);
    }

    //  카테고리별로 게시글 조회
    public Post getPostByCategoryAndId(Category category, Long id) {
        return postRepository.findByCategoryAndId(category, id).orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다!"));
    }

    //  카테고리별로 게시글 수정
    public Post updatePostByCategory(Long id, Category category, Post postDetails, Long userId) {
        Post post = postRepository.findByCategoryAndId(category, id)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("수정및 삭제는 작성자만 가능합니다!");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setCategory(postDetails.getCategory());
        post.setImageUrl(postDetails.getImageUrl());

        return postRepository.save(post);
    }

    //  카테고리별로 게시글 삭제
    public void deletePostByCategory(Long id, Category category, Long userId) {
        Post post = postRepository.findByCategoryAndId(category, id)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("수정및 삭제는 작성자만 가능합니다!");
        }

        postRepository.delete(post);
    }
}