package ru.yandex.practicum.filmorate.storage.recommendations;

import java.util.List;

public interface UserRecommendationStorage {
    List<Long> getRecommendation(Long id);
}
