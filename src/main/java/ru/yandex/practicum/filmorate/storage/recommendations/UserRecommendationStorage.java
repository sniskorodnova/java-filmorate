package ru.yandex.practicum.filmorate.storage.recommendations;

import java.util.List;

public interface UserRecommendationStorage {
    public List<Long> getUserFilms(Long id);

    public List<Long> getListOfOtherUser(Long id);

    public List<Long> getFilmsOfOtherUser(List<Long> listOfUsers, int i);
}
