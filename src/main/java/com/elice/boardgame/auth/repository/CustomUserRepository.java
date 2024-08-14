package com.elice.boardgame.auth.repository;

import com.elice.boardgame.common.dto.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<SearchResponse> searchUsersByKeyword(String keyword, Pageable pageable);
}
