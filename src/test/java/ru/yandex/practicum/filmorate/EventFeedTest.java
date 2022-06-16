package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventFeedTest {
    @Autowired
    private final UserService userService;
    @Autowired
    private final FilmService filmService;

    private User user;
    private User otherUser;
    private Film film;

    @BeforeEach
    public void init() {
        user = User.builder()
                .email("qwerty@gmail.com")
                .name("UserName")
                .birthday(LocalDate.of(1990, 6, 9))
                .login("UserLogin")
                .build();
        otherUser = User.builder()
                .email("qwertSecond@gmail.com")
                .name("UserNameSecond")
                .birthday(LocalDate.of(1992, 6, 9))
                .login("UserLoginSecond")
                .build();
        film = Film.builder()
                .name("name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(150L)
                .mpa(Mpa.builder()
                        .id(3)
                        .build())
                .build();
    }

    @Test
    void test1_shouldGetTrueWhenFriendshipEventByAdd() throws ValidationException, UserNotFoundException {
        userService.create(user);
        userService.create(otherUser);
        userService.addToFriends(1L, 2L);

        List<Feed> resultFeed = userService.getEventFeedById(1L);

        Assertions.assertTrue(resultFeed.size() == 1
                && resultFeed.get(0).getEventType().equals("FRIEND")
                && resultFeed.get(0).getOperation().equals("ADD"));
    }

    @Test
    void test2_shouldGetTrueWhenLikeEventByAdd() throws ValidationException, FilmNotFoundException,
            UserNotFoundException {
        userService.create(user);
        filmService.create(film);
        filmService.likeFilm(1L, 1L);

        List<Feed> resultFeed = userService.getEventFeedById(1L);

        Assertions.assertTrue(resultFeed.size() == 1
                && resultFeed.get(0).getEventType().equals("LIKE")
                && resultFeed.get(0).getOperation().equals("ADD"));
    }

    @Test
    void test3_shouldGetTrueWhenLikeEventByDelete() throws ValidationException, FilmNotFoundException,
            UserNotFoundException {
        userService.create(user);
        filmService.create(film);
        filmService.likeFilm(1L, 1L);
        filmService.deleteLike(1L, 1L);

        List<Feed> resultFeed = userService.getEventFeedById(1L);
        Assertions.assertTrue(resultFeed.size() == 2
                && resultFeed.get(0).getEventType().equals("LIKE")
                && resultFeed.get(1).getOperation().equals("REMOVE"));
    }
}
