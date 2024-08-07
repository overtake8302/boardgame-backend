package com.elice.boardgame.post.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.View;
import com.elice.boardgame.post.repository.PostRepository;
import com.elice.boardgame.game.entity.GameProfilePic;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    @Autowired
//    private ViewService viewService;

    //  게시글 생성
//    @Transactional
//    public Post createPost(PostDto postDto, MultipartFile file, Long userId) throws Exception {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("로그인 후 진행해 주세요."));
//
//        BoardGame boardGame = boardGameRepository.findById(postDto.getGameId())
//            .orElseThrow(() -> new RuntimeException("Game not found"));
//
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setContent(postDto.getContent());
//        post.setCategory(postDto.getCategory());
//        post.setGameName(boardGame.getName());
//        post.setBoardGame(boardGame);
//        post.setUser(user);
//
//        if (file != null) {
//            String imageUrl = s3Uploader.uploadFile(file);
//            post.setImageUrl(imageUrl);
//            post.setImageName(file.getOriginalFilename());
//        }
//
//        return postRepository.save(post);
//    }
    //  테스트
    @Transactional
    public Post createPost(PostDto postDto, MultipartFile[] files) throws Exception {
        BoardGame boardGame = boardGameRepository.findById(postDto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(postDto.getCategory());
        post.setGameName(boardGame.getName());
        post.setBoardGame(boardGame);

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

    //  카테고리별로 게시글 조회
    public Post getPostByCategoryAndId(String category, Long id) {
        return postRepository.findByCategoryAndId(category, id).orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다!"));
    }

//    public PostDto getPostDtoByCategoryAndId(String category, Long id) {
//        Post post = getPostByCategoryAndId(category, id);
//        if (post == null) {
//            return null;
//        }
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
//            commentDto.setUserId(comment.getUser().getId());
//            commentDto.setUserName(comment.getUser().getUsername());
//            return commentDto;
//        }).collect(Collectors.toList()));
//        postDto.setGameId(post.getBoardGame().getGameId());
//
//        return postDto;
//    }
    //  유저없을때 테스트용
    @Transactional
    public PostDto getPostDtoByCategoryAndId(String category, Long id) {
        Post post = getPostByCategoryAndId(category, id);
        if (post == null) {
            return null;
        }

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
            postDto.setUserName("Unknown");
        }

        postDto.setComments(post.getComments().stream().map(comment -> {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent(comment.getContent());
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

        return postDto;
    }

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

    //  카테고리별로 게시글 수정
//    @Transactional
//    public Post updatePostByCategory(Long id, String category, PostDto postDetails, Long userId) {
//        Post post = postRepository.findByCategoryAndId(category, id)
//                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));
//
//        if (!post.getUserId().equals(userId)) {
//            throw new RuntimeException("수정 및 삭제는 작성자만 가능합니다!");
//        }
//
//        post.setTitle(postDetails.getTitle());
//        post.setContent(postDetails.getContent());
//        post.setCategory(postDetails.getCategory());
//        post.setImageUrls(postDetails.getImageUrls());
//        post.setImageNames(postDetails.getImageNames());
//
//        return postRepository.save(post);
//    }
    //  유저없을때 테스트용
    @Transactional
    public Post updatePostByCategory(Long id, String category, PostDto postDetails) {
        Post post = postRepository.findByCategoryAndId(category, id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setCategory(postDetails.getCategory());
        post.setImageUrls(postDetails.getImageUrls());
        post.setImageNames(postDetails.getImageNames());

        return postRepository.save(post);
    }

    //  카테고리별로 게시글 삭제
//    @Transactional
//    public void deletePostByCategory(Long id, String category, Long userId) {
//        Post post = postRepository.findByCategoryAndId(category, id)
//            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));
//
//        if (!post.getUserId().equals(userId)) {
//            throw new RuntimeException("수정및 삭제는 작성자만 가능합니다!");
//        }
//
//        postRepository.delete(post);
//    }
    //  유저없을때 테스트용
    @Transactional
    public void deletePostByCategory(Long id, String category) {
        Post post = postRepository.findByCategoryAndId(category, id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!"));

        postRepository.delete(post);
    }
}