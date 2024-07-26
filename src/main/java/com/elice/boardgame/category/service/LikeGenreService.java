package com.elice.boardgame.category.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.LikeGenreId;
import com.elice.boardgame.category.repository.GenreRepository;
import com.elice.boardgame.category.repository.LikeGenreRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeGenreService {

    private final LikeGenreRepository likeGenreRepository;

    private final GenreRepository genreRepository;

    private final UserRepository userRepository;

    public void addLikeGenreScore(LikeGenreId id) {
        Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
        if (optionalEntity.isPresent()) {
            likeGenreRepository.updateLikeGenreScore(id, 2L);
        } else {
            LikeGenre newEntity = new LikeGenre();
            newEntity.setId(id);
            newEntity.setScore(2L);

            User user = userRepository.findById(id.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
            Genre genre = genreRepository.findById(id.getGenreId())
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
            Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
            if (optionalEntity.isPresent()) {
                long delta = 0L;
                if (rating == 5) {
                    delta = 2L;
                } else if (rating == 4) {
                    delta = 1L;
                }
                likeGenreRepository.updateLikeGenreScore(id, delta);
            } else {
                LikeGenre newEntity = new LikeGenre();
                newEntity.setId(id);
                if (rating == 5) {
                    newEntity.setScore(2L);
                } else if (rating == 4) {
                    newEntity.setScore(1L);
                }
                User user = userRepository.findById(id.getUserId())
                    .orElseThrow(() -> new RuntimeException("Member not found"));
                Genre genre = genreRepository.findById(id.getGenreId())
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
