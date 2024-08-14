package com.elice.boardgame.post.repository;

import com.elice.boardgame.auth.dto.MyCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCommentRepository {

    Page<MyCommentResponseDto> findAllByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable);
}
