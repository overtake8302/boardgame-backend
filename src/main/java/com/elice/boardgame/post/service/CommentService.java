package com.elice.boardgame.post.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.entity.*;
import com.elice.boardgame.post.repository.CommentLikeRepository;
import com.elice.boardgame.post.repository.CommentRepository;
import com.elice.boardgame.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final CommentLikeRepository commentLikeRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss");

    //  댓글생성
    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (currentUser != null && currentUser.isAuthenticated() && !"anonymousUser".equals(currentUser.getName())) {
            user = userRepository.findByUsername(currentUser.getName());
        }

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setContent(commentDto.getContent());
        comment.setPost(postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다!")));
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        CommentDto responseDto = new CommentDto();
        responseDto.setId(savedComment.getId());
        responseDto.setContent(savedComment.getContent());
        responseDto.setUserId(user.getId());
        responseDto.setUserName(user.getUsername());
        responseDto.setCreatedAt(savedComment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return responseDto;
    }

    //  댓글 수정
    @Transactional
    public Comment updateComment(Long id, Comment comment) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);
        Comment existingComment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        if (!existingComment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 댓글을 수정할 수 있습니다!");
        }

        existingComment.setContent(comment.getContent());
        return commentRepository.save(existingComment);
    }

    //  댓글 삭제
    @Transactional
    public Page<CommentDto> deleteComment(Long id, Pageable pageable) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        if (!currentUser.getRole().equals("ROLE_ADMIN")) {
            if (!existingComment.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("관리자 또는 작성자만 댓글을 삭제할 수 있습니다!");
            }
        }

        commentRepository.delete(existingComment);

        // 현재 페이지에 댓글이 남아있는지 확인
        Page<Comment> commentsPage = getCommentsByPost(existingComment.getPost(), pageable);
        if (commentsPage.isEmpty() && pageable.getPageNumber() > 0) {
            // 이전 페이지로 이동
            Pageable previousPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
            commentsPage = getCommentsByPost(existingComment.getPost(), previousPageable);
        }

        // Dto로 변환
        Page<CommentDto> commentDtos = commentsPage.map(this::convertToDto);
        return commentDtos;
    }

    //  Dto로 변환
    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUserName(comment.getUser().getUsername());
        }
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setComInComs(
            comment.getComInComs().stream().map(this::convertToDto).collect(Collectors.toList()));
        dto.setReplies(
            comment.getComInComs().stream().map(this::convertToDto).collect(Collectors.toList()));
        if (comment.getCreatedAt() != null) {
            dto.setCreatedAt(comment.getCreatedAt().format(formatter));
        }
        dto.setLikeCount(commentLikeRepository.countLikesByCommentId(comment.getId()));
        dto.setReplyLikeCount(commentLikeRepository.countLikesByCommentId(comment.getId()));
        return dto;
    }

    //  대댓글 조회
    public List<CommentDto> getReplies(Long parentId) {
        List<Comment> comments = commentRepository.findAllByParentId(parentId);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    //  대댓글 생성
    @Transactional
    public Comment createReply(Long parentId, Comment comment) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        if (currentUser == null) {
            throw new RuntimeException("비회원은 대댓글을 작성할 수 없습니다!");
        }

        Comment parentComment = commentRepository.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글의 ID가 올바르지 않습니다!"));
        comment.setParent(parentComment);
        comment.setUser(currentUser);
        return commentRepository.save(comment);
    }

    //  대댓글 수정
    @Transactional
    public Comment updateReply(Long replyId, Comment comment) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        if (currentUser == null) {
            throw new RuntimeException("비회원은 대댓글을 수정할 수 없습니다!");
        }

        Comment existingReply = commentRepository.findById(replyId)
            .orElseThrow(() -> new IllegalArgumentException("댓글의 ID가 올바르지 않습니다!"));

        if (!existingReply.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("작성자만 대댓글을 수정할 수 있습니다!");
        }

        existingReply.setContent(comment.getContent());
        return commentRepository.save(existingReply);
    }

    //  대댓글 삭제
    @Transactional
    public Page<CommentDto> deleteReply(Long commentId, Pageable pageable) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName);

        if (currentUser == null) {
            throw new RuntimeException("비회원은 대댓글을 삭제할 수 없습니다!");
        }

        Comment reply = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글의 ID가 올바르지 않습니다!"));

        if (!currentUser.getRole().equals("ROLE_ADMIN")) {
            if (!reply.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("작성자만 대댓글을 삭제할 수 있습니다!");
            }
        }

        commentRepository.delete(reply);

        Page<Comment> commentsPage = getCommentsByPost(reply.getPost(), pageable);
        if (commentsPage.getTotalElements() < pageable.getPageSize() && pageable.getPageNumber() > 0) {
            // 다음 페이지의 댓글을 가져와서 합침
            Pageable nextPageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
            Page<Comment> nextPageComments = getCommentsByPost(reply.getPost(), nextPageable);
            List<Comment> combinedComments = new ArrayList<>(commentsPage.getContent());
            combinedComments.addAll(nextPageComments.getContent());
            commentsPage = new PageImpl<>(combinedComments, pageable, commentsPage.getTotalElements() + nextPageComments.getTotalElements());
        }

        Page<CommentDto> commentDtos = commentsPage.map(this::convertToDto);
        return commentDtos;
    }

    //  댓글 페이지네이션
    public Page<Comment> getCommentsByPost(Post post, Pageable pageable) {
        List<Comment> comments = commentRepository.findByPost(post);
        List<Comment> allComments = new ArrayList<>();

        for (Comment comment : comments) {
            allComments.add(comment);
            allComments.addAll(comment.getComInComs());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allComments.size());
        List<Comment> paginatedComments = allComments.subList(start, end);

        List<Comment> filteredComments = paginatedComments.stream()
            .filter(comment -> comment.getParent() == null)
            .collect(Collectors.toList());

        return new PageImpl<>(filteredComments, pageable, allComments.size());
    }

    //  좋아요
    @Transactional
    public ClickLikeResponseDto clickLike(Long commentId) {
        Comment targetComment = commentRepository.findByIdAndDeletedAtIsNull(commentId);
        User currentUser = authService.getCurrentUser();
        CommentLikePK commentLikePK = new CommentLikePK(currentUser.getId(), commentId);

        Optional<CommentLike> target = commentLikeRepository.findById(commentLikePK);

        ClickLikeResponseDto clickLikeResponseDto = new ClickLikeResponseDto();

        if (target.isPresent()) {
            commentLikeRepository.delete(target.get());
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_REMOVED.getMessage());
        } else {
            CommentLike commentLike = new CommentLike(commentLikePK, targetComment, currentUser);
            commentLikeRepository.save(commentLike);
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_ADDED.getMessage());
        }

        long likeCount = commentLikeRepository.countLikesByCommentId(commentId);
        clickLikeResponseDto.setLikeCount((int) likeCount);

        return clickLikeResponseDto;
    }

    // 대댓글 페이지네이션
    public Page<CommentDto> getReplies(Long commentId, Pageable pageable) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글의 ID가 올바르지 않습니다!"));

        List<Comment> replies = parentComment.getComInComs();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), replies.size());
        List<Comment> paginatedReplies = replies.subList(start, end);

        List<CommentDto> replyDtos = paginatedReplies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(replyDtos, pageable, replies.size());
    }
}