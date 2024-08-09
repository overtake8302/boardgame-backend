package com.elice.boardgame.post.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
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
        User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        postDto.setUserName(currentUser.getUsername());

        BoardGame boardGame = boardGameRepository.findById(postDto.getGameId())
            .orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다."));

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(postDto.getCategory());
        post.setGameName(boardGame.getName());
        post.setBoardGame(boardGame);
        post.setUser(currentUser);

        List<String> gameImageUrls = boardGame.getGameProfilePics().stream()
            .map(GameProfilePic::getPicAddress)
            .collect(Collectors.toList());
        post.setGameImageUrls(gameImageUrls);

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String imageUrl = s3Uploader.uploadFile(file);
                post.addImageUrl(imageUrl);
                post.addImageName(file.getOriginalFilename());
            }
        }

        return postRepository.save(post);
    }
    //  테스트
//    @Transactional
//    public Post createPost(PostDto postDto, MultipartFile[] files) throws Exception {
//        BoardGame boardGame = boardGameRepository.findById(postDto.getGameId())
//                .orElseThrow(() -> new RuntimeException("Game not found"));
//
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setContent(postDto.getContent());
//        post.setCategory(postDto.getCategory());
//        post.setGameName(boardGame.getName());
//        post.setBoardGame(boardGame);
//
//        List<String> gameImageUrls = boardGame.getGameProfilePics().stream()
//                .map(GameProfilePic::getPicAddress)
//                .collect(Collectors.toList());
//        post.setGameImageUrls(gameImageUrls);
//
//        if (files != null && files.length > 0) {
//            for (MultipartFile file : files) {
//                String imageUrl = s3Uploader.uploadFile(file);
//                post.addImageUrl(imageUrl);
//                post.addImageName(file.getOriginalFilename());
//            }
//        }
//
//        return postRepository.save(post);
//    }

    //  카테고리별로 게시글 조회
    public Post getPostByCategoryAndId(String category, Long id) {
        return postRepository.findByCategoryAndId(category, id).orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다!"));
    }

    @Transactional
    public PostDto getPostDtoByCategoryAndId(String category, Long id) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        log.info("currentUser {}",currentUser.getName());
        User user = userRepository.findByUsername(currentUser.getName())
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Post post = getPostByCategoryAndId(category, id);

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
        postDto.setImageUrls(post.getImageUrls());
        postDto.setImageNames(post.getImageNames());
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

        List<String> gameImageUrls = post.getBoardGame().getGameProfilePics().stream()
            .map(GameProfilePic::getPicAddress)
            .collect(Collectors.toList());
        postDto.setGameImageUrls(gameImageUrls);

        postDto.setViewCount(post.getView().getViewCount()+1);
        postDto.setCreatedAt(post.getCreatedAt().format(formatter));
        postDto.setLikeCount(postLikeRepository.countLikesByPostId(post.getId()));

        return postDto;
    }
    //  유저없을때 테스트용
//    @Transactional
//    public PostDto getPostDtoByCategoryAndId(String category, Long id) {
//        Post post = getPostByCategoryAndId(category, id);
//        if (post == null) {
//            return null;
//        }
//
//        View view = post.getView();
//        if (view == null) {
//            view = new View();
//            view.setPost(post);
//            view.setViewCount(0);
//            post.setView(view);
//        }
//
//        PostDto postDto = new PostDto();
//        postDto.setTitle(post.getTitle());
//        postDto.setContent(post.getContent());
//        postDto.setCategory(post.getCategory());
//        postDto.setImageUrls(post.getImageUrls());
//        postDto.setImageNames(post.getImageNames());
//        postDto.setGameName(post.getGameName());
//        postDto.setUserId(post.getUserId());
//
//        if (post.getUser() != null) {
//            postDto.setUserName(post.getUser().getUsername());
//        } else {
//            postDto.setUserName("Unknown");
//        }
//
//        postDto.setComments(post.getComments().stream().map(comment -> {
//            CommentDto commentDto = new CommentDto();
//            commentDto.setContent(comment.getContent());
//            commentDto.setCreatedAt(comment.getCreatedAt().format(formatter));
//            return commentDto;
//        }).collect(Collectors.toList()));
//        postDto.setGameId(post.getBoardGame().getGameId());
//
//        List<String> gameImageUrls = post.getBoardGame().getGameProfilePics().stream()
//                .map(GameProfilePic::getPicAddress)
//                .collect(Collectors.toList());
//        postDto.setGameImageUrls(gameImageUrls);
//
//        postDto.setViewCount(post.getView().getViewCount()+1);
//        postDto.setCreatedAt(post.getCreatedAt().format(formatter));
//
//        return postDto;
//    }
    //  조회수
    @Transactional
    public PostDto incrementViewAndGetPost(String category, Long id) {
        Post post = getPostByCategoryAndId(category, id);
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

        return getPostDtoByCategoryAndId(category, id);
    }

    //  좋아요~
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public PostDto clickLike(Long postId) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));


        Post targetPost = postRepository.findByIdAndDeletedAtIsNull(postId);
        PostLikePK postLikePK = new PostLikePK(currentUser.getId(), postId);

        Optional<PostLike> target = postLikeRepository.findById(postLikePK);

        PostDto postDto = new PostDto();

        if (target.isPresent()) {
            postLikeRepository.delete(target.get());
        } else {
            PostLike postLike = new PostLike(postLikePK, targetPost, currentUser);
            postLikeRepository.save(postLike);
        }

        Long likeCount = postLikeRepository.countLikesByPostId(postId);
        postDto.setLikeCount(likeCount);

        return postDto;
    }

    //  카테고리별로 게시글 수정
    @Transactional
    public Post updatePostByCategory(Long id, String category, PostDto postDetails) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("currentUserName {}",currentUserName);  //  anonymousUser
        User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Post post = postRepository.findByCategoryAndId(category, id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 게시글을 삭제할 수 있습니다!");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setCategory(postDetails.getCategory());
        post.setImageUrls(postDetails.getImageUrls());
        post.setImageNames(postDetails.getImageNames());

        return postRepository.save(post);
    }
    //  유저없을때 테스트용
//    @Transactional
//    public Post updatePostByCategory(Long id, String category, PostDto postDetails) {
//        Post post = postRepository.findByCategoryAndId(category, id)
//                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));
//
//        post.setTitle(postDetails.getTitle());
//        post.setContent(postDetails.getContent());
//        post.setCategory(postDetails.getCategory());
//        post.setImageUrls(postDetails.getImageUrls());
//        post.setImageNames(postDetails.getImageNames());
//
//        return postRepository.save(post);
//    }

    //  카테고리별로 게시글 삭제
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @Transactional
    public void deletePostByCategory(Long id, String category) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Post post = postRepository.findByCategoryAndId(category, id)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 게시글을 삭제할 수 있습니다!");
        }

        postRepository.delete(post);
    }
    //  유저없을때 테스트용
//    @Transactional
//    public void deletePostByCategory(Long id, String category) {
//        Post post = postRepository.findByCategoryAndId(category, id)
//                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));
//
//        postRepository.delete(post);
//    }

    public CommonResponse<Page<SearchPostResponse>> searchPostsByKeyword(String keyword, Pageable pageable) {
        Page<SearchPostResponse> results = postRepository.searchPostsByKeyword(keyword, pageable);

        return CommonResponse.<Page<SearchPostResponse>>builder()
            .payload(results)
            .message("검색 성공")
            .status(200)
            .build();
    }
}