package com.elice.boardgame.post.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private Long id;
    private String imagePath;
    private String description;

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }
}