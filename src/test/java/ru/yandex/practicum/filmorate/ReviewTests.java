package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.LikeRecordAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Класс с интеграционными тестами для проверки работы отзывов
 */
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class ReviewTests {
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewService reviewService;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void checkCreatedReviewFindReviewById() {
        User user = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        userDbStorage.create(user);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(0L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkUpdatedReviewFindReviewById() {
        User user = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        userDbStorage.create(user);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        Review updateReview = Review.builder().id(1L).content("Film is not very good").isPositive(false)
                .userId(1L).filmId(1L).build();
        reviewDbStorage.update(updateReview);
        Review reviewToCompare = Review.builder().id(1L).content("Film is not very good").isPositive(false)
                .userId(1L).filmId(1L).useful(0L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkDeletedReviewFindReviewById() {
        User user = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        userDbStorage.create(user);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewDbStorage.deleteById(1L);
        assertNull(reviewDbStorage.getById(1L));
    }

    @Test
    public void checkReviewsForFilmZeroReviews() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmDbStorage.create(film);
        assertThat(reviewDbStorage.getReviewsForFilm(1L, 10).size(), is(equalTo(0)));
    }

    @Test
    public void checkReviewsForFilm1ReviewCountDefault() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userDbStorage.create(user);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        assertThat(reviewDbStorage.getReviewsForFilm(1L, 10).size(), is(equalTo(1)));
    }

    @Test
    public void checkReviewsForFilm2ReviewsCount1() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film);
        Review review1 = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review1);
        Review review2 = Review.builder().content("Film is a bad film").isPositive(false).userId(2L).filmId(1L).build();
        reviewDbStorage.create(review2);
        assertThat(reviewDbStorage.getReviewsForFilm(1L, 1).size(), is(equalTo(1)));
    }

    @Test
    public void checkReviewAfterAddingLike() throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewService.addLikeToReview(1L, 2L);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(1L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkReviewAfterAddingLikeAndDislike() throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        User user3 = User.builder().email("hjgg@gmail.com").name("UserName3")
                .birthday(LocalDate.of(1985, 4, 3)).login("UserLogin3").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        userDbStorage.create(user3);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewService.addLikeToReview(1L, 2L);
        reviewService.addDislikeToReview(1L, 3L);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(0L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkReviewAfterAddingLikeAndRemovingLike() throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewService.addLikeToReview(1L, 2L);
        reviewService.removeLikeFromReview(1L, 2L);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(0L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkReviewAfterAddingDislike() throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewService.addDislikeToReview(1L, 2L);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(-1L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }

    @Test
    public void checkReviewAfterAddingDislikeAndRemovingDislike() throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        User user1 = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("asd@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film);
        Review review = Review.builder().content("Film is very good").isPositive(true).userId(1L).filmId(1L).build();
        reviewDbStorage.create(review);
        reviewService.addDislikeToReview(1L, 2L);
        reviewService.removeDislikeFromReview(1L, 2L);
        Review reviewToCompare = Review.builder().id(1L).content("Film is very good").isPositive(true)
                .userId(1L).filmId(1L).useful(0L).build();
        assertThat(reviewDbStorage.getById(1L), is(equalTo(reviewToCompare)));
    }
}
