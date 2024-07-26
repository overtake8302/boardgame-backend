package com.elice.boardgame.post.service;

import com.elice.boardgame.post.entity.View;
import com.elice.boardgame.post.repository.ViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewService {
    @Autowired
    private ViewRepository viewRepository;

    @Transactional
    public void incrementViewCount(Long postId) {
        View view = viewRepository.findByPostId(postId);
        if (view != null) {
            view.setViewCount(view.getViewCount() + 1);
            viewRepository.save(view);
        }
    }
}