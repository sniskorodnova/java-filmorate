package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, описывающий сущность отзыв
 */
@Data
@Builder
public class Review {
    private Long id;
    private String content;
    private boolean isPositive;
    private Long userId;
    private Long filmId;
    private Long useful;
}
