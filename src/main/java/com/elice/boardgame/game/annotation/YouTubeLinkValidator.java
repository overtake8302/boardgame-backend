package com.elice.boardgame.game.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YouTubeLinkValidator implements ConstraintValidator<YouTubeLink, String> {


    private static final String YOUTUBE_EMBED_URL_PATTERN =
            "<iframe[^>]*\\ssrc=[\"']https:\\/\\/www\\.youtube\\.com\\/embed\\/[A-Za-z0-9_-]+(\\?[^\"']*)?[\"'][^>]*><\\/iframe>";

    @Override
    public void initialize(YouTubeLink constraintAnnotation) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.isEmpty()) {
            return true;
        }
        return url.matches(YOUTUBE_EMBED_URL_PATTERN);
    }
}


