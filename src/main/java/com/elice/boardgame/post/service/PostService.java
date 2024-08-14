package com.elice.boardgame.post.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.PostLike;
import com.elice.boardgame.post.entity.PostLikePK;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.View;
import com.elice.boardgame.post.repository.PostLikeRepository;
import com.elice.boardgame.post.repository.PostRepository;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.auth.service.AuthService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;
    private final BoardGameRepository boardGameRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostLikeRepository postLikeRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //  게시글 생성
    @Transactional
    public Post createPost(PostDto postDto, MultipartFile[] files) throws Exception {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        postDto.setUserName(currentUser.getUsername());

        BoardGame boardGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(postDto.getGameId());
        if (boardGame == null) {
            new RuntimeException("게임을 찾을 수 없습니다.");
        }

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(postDto.getCategory());
        post.setGameName(boardGame.getName());
        post.setBoardGame(boardGame);
        post.setUser(currentUser);

        List<GameProfilePic> pics = boardGame.getGameProfilePics();
        if (pics != null && pics.contains(0)) {
            post.setGameImageUrl(boardGame.getGameProfilePics().get(0).getPicAddress());
        }

        return postRepository.save(post);
    }

    //  카테고리별로 게시글 조회
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다!"));
    }

    @Transactional
    public PostDto getPostDtoById(Long id) {
        Post post = getPostById(id);

        View view = post.getView();
        if (view == null) {
            view = new View();
            view.setPost(post);
            view.setViewCount(0);
            post.setView(view);
        }

        PostDto postDto = new PostDto();
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCategory(post.getCategory());
        postDto.setGameName(post.getGameName());
        postDto.setUserId(post.getUserId());

        if (post.getUser() != null) {
            postDto.setUserName(post.getUser().getUsername());
        } else {
            postDto.setUserName("비회원");
        }

        postDto.setComments(post.getComments().stream().map(comment -> {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent(comment.getContent());
            if (comment.getUser() != null) {
                commentDto.setUserId(comment.getUser().getId());
                commentDto.setUserName(comment.getUser().getUsername());
            } else {
                commentDto.setUserId(null);
                commentDto.setUserName("비회원");
            }
            commentDto.setCreatedAt(comment.getCreatedAt().format(formatter));
            return commentDto;
        }).collect(Collectors.toList()));
        postDto.setGameId(post.getBoardGame().getGameId());

        postDto.setGameImageUrl(post.getGameImageUrl());

        postDto.setViewCount(post.getView().getViewCount()+1);
        postDto.setCreatedAt(post.getCreatedAt().format(formatter));
        postDto.setLikeCount(postLikeRepository.countLikesByPostId(post.getId()));

        return postDto;
    }

    //  조회수
    @Transactional
    public PostDto incrementViewAndGetPost(Long id) {
        Post post = getPostById(id);
        if (post == null) {
            throw new NoSuchElementException("게시글을 찾을 수 없습니다!");
        }

        View view = post.getView();
        if (view == null) {
            view = new View(post, 0);
            post.setView(view);
        } else {
            view.setViewCount(view.getViewCount() + 1);
        }

        postRepository.save(post);

        return getPostDtoById(id);
    }

    //  좋아요~
    @Transactional
    public ClickLikeResponseDto clickLike(Long postId) {

        Post targetPost = postRepository.findByIdAndDeletedAtIsNull(postId);
        User currentUser = authService.getCurrentUser();
        PostLikePK postLikePK = new PostLikePK(currentUser.getId(), postId);

        Optional<PostLike> target = postLikeRepository.findById(postLikePK);

        ClickLikeResponseDto clickLikeResponseDto = new ClickLikeResponseDto();

        if (target.isPresent()) {
            postLikeRepository.delete(target.get());
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_REMOVED.getMessage());
        } else {
            PostLike postLike = new PostLike(postLikePK, targetPost, currentUser);
            postLikeRepository.save(postLike);
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_ADDED.getMessage());
        }

        long likeCount = postLikeRepository.countLikesByPostId(postId);
        clickLikeResponseDto.setLikeCount((int) likeCount);

        return clickLikeResponseDto;
    }

    //  카테고리별로 게시글 수정
    @Transactional
    public Post updatePost(Long id, PostDto postDetails) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 게시글을 삭제할 수 있습니다!");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setCategory(postDetails.getCategory());

        return postRepository.save(post);
    }

    //  카테고리별로 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 게시글을 삭제할 수 있습니다!");
        }

        postRepository.delete(post);
    }

    public CommonResponse<Page<SearchResponse>> searchPostsByKeyword(String keyword, Pageable pageable) {
        Page<SearchResponse> results = postRepository.searchPostsByKeyword(keyword, pageable);

        return CommonResponse.<Page<SearchResponse>>builder()
            .payload(results)
            .message("검색 성공")
            .status(200)
            .build();
    }
}