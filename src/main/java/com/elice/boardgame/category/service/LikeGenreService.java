package com.elice.boardgame.category.service;

import com.elice.boardgame.category.entity.GenreEntity;
import com.elice.boardgame.category.entity.LikeGenreEntity;
import com.elice.boardgame.category.entity.LikeGenreId;
import com.elice.boardgame.category.entity.UserEntity;
import com.elice.boardgame.category.repository.GenreRepository;
import com.elice.boardgame.category.repository.LikeGenreRepository;
import com.elice.boardgame.category.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeGenreService {

    private final LikeGenreRepository likeGenreRepository;

    private final GenreRepository genreRepository;

    private final MemberRepository memberRepository;

    public void addLikeGenreScore(LikeGenreId id) {
        Optional<LikeGenreEntity> optionalEntity = likeGenreRepository.findById(id);
        if (optionalEntity.isPresent()) {
            likeGenreRepository.updateLikeGenreScore(id, 2L);
        } else {
            LikeGenreEntity newEntity = new LikeGenreEntity();
            newEntity.setId(id);
            newEntity.setScore(2L);

            UserEntity user = memberRepository.findById(id.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
            GenreEntity genre = genreRepository.findById(id.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

            newEntity.setUser(user);
            newEntity.setGenre(genre);

            likeGenreRepository.save(newEntity);
        }
    }


    public void subtractLikeGenreScore(LikeGenreId id) {
        likeGenreRepository.updateLikeGenreScore(id, -2L);
    }

    public void addRateGenreScore(LikeGenreId id, int rating) {
        if (rating > 3) {
            Optional<LikeGenreEntity> optionalEntity = likeGenreRepository.findById(id);
            if (optionalEntity.isPresent()) {
                long delta = 0L;
                if (rating == 5) {
                    delta = 2L;
                } else if (rating == 4) {
                    delta = 1L;
                }
                likeGenreRepository.updateLikeGenreScore(id, delta);
            } else {
                LikeGenreEntity newEntity = new LikeGenreEntity();
                newEntity.setId(id);
                if (rating == 5) {
                    newEntity.setScore(2L);
                } else if (rating == 4) {
                    newEntity.setScore(1L);
                }
                UserEntity user = memberRepository.findById(id.getUserId())
                    .orElseThrow(() -> new RuntimeException("Member not found"));
                GenreEntity genre = genreRepository.findById(id.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Genre not found"));

                newEntity.setUser(user);
                newEntity.setGenre(genre);
                likeGenreRepository.save(newEntity);
            }
        }
    }

    public void subtractRateGenreScore(LikeGenreId id, int rating) {
        if (rating == 5) {
            likeGenreRepository.updateLikeGenreScore(id, -2L);
        } else if (rating == 4) {
            likeGenreRepository.updateLikeGenreScore(id, -1L);
        }
    }
}
