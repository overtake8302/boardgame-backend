package com.elice.boardgame.post.service;

import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.post.repository.PostRepository;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private S3Uploader s3Uploader;

    //  게시글 생성
    public Post createPost(Post post, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Uploader.uploadFile(file);
            post.setImageUrl(imageUrl);
            post.setImageName(file.getOriginalFilename());
        }
        return postRepository.save(post);
    }

    //  카테고리별로 게시글 조회
    public Post getPostByCategoryAndId(Enums.Category category, Long id) {
        return postRepository.findByCategoryAndId(category, id).orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다!"));
    }

    //  카테고리별로 게시글 수정
    public Post updatePostByCategory(Long id, Enums.Category category, PostDto postDetails, Long userId) {
        Post post = postRepository.findByCategoryAndId(category, id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("수정및 삭제는 작성자만 가능합니다!");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setCategory(postDetails.getCategory());
        post.setImageUrl(postDetails.getImageUrl());
        post.setImageName(postDetails.getImageName());

        return postRepository.save(post);
    }

    //  카테고리별로 게시글 삭제
    public void deletePostByCategory(Long id, Enums.Category category, Long userId) {
        Post post = postRepository.findByCategoryAndId(category, id)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("수정및 삭제는 작성자만 가능합니다!");
        }

        postRepository.delete(post);
    }
}