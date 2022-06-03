package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс с тестами для класса FilmController
 */
@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController filmController;
    @Autowired
    UserController userController;

    @AfterEach
    public void afterEach() {
        Film.setCounter(new AtomicLong(0));
        filmController.getFilmService().getFilmStorage().deleteAll();
        User.setCounter(new AtomicLong(0));
        userController.getUserService().getUserStorage().deleteAll();
    }

    @Test
    public void createFilmWithoutNameThrowsValidationException() {
        Film film = Film.builder().description("Description")
                .releaseDate(LocalDate.of(2000, 2, 26)).duration(125L).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void createFilmNameConsistsOfSpacesThrowsValidationException() {
        Film film = Film.builder().name(" ").description("Description")
                .releaseDate(LocalDate.of(2000, 2, 26)).duration(125L).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void createValidFilmNameSuccess() throws ValidationException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(2000, 2, 26)).duration(125L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmDescriptionHas201SymbolsThrowsValidationException() {
        Film film = Film.builder().name("Name")
                .description("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription"
                        + "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeery"
                        + "LongDescriptionVeeryLongDescription1")
                .releaseDate(LocalDate.of(2000, 2, 26)).duration(125L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Описание фильма должно содержать символы и не может быть больше 200 символов",
                ex.getMessage());
    }

    @Test
    public void createFilmDescriptionHas200SymbolsSuccess() throws ValidationException {
        Film film = Film.builder().name("Name")
                .description("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription"
                        + "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeery"
                        + "LongDescriptionVeeryLongDescription")
                .releaseDate(LocalDate.of(2000, 2, 26)).duration(125L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmReleaseDate27December1895ThrowsValidationException() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1895, 12, 27)).duration(125L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void createFilmReleaseDate29December1895Success() throws ValidationException {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1895, 12, 29)).duration(125L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmDurationIsZeroThrowsValidationException() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(0L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void createFilmDurationIsNegativeThrowsValidationException() {
        Film film = Film.builder().name("Name").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(-150L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void updateFilmNewNameIsEmptyThrowsValidationException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().id(1L).name("").description("Description2")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void updateFilmNewDescriptionHas201SymbolsThrowsValidationException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().id(1L).name("Name2")
                .description("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription"
                        + "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeery"
                        + "LongDescriptionVeeryLongDescription1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Описание фильма должно содержать символы и не может быть больше 200 символов",
                ex.getMessage());
    }

    @Test
    public void updateFilmNewReleaseDateIs27December1895ThrowsValidationException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().id(1L).name("Name2").description("Description2")
                .releaseDate(LocalDate.of(1895, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void updateFilmNewDurationIsZeroThrowsValidationException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().id(1L).name("Name2").description("Description2")
                .releaseDate(LocalDate.of(2005, 12, 27)).duration(0L)
                .mpa(Mpa.builder().id(3).build()).build();
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void updateFilmSuccess() throws ValidationException, FilmNotFoundException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().id(1L).name("Name2").description("Description2")
                .releaseDate(LocalDate.of(2005, 12, 27)).duration(180L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.update(film2);
        assertEquals(1, filmController.getAll().size());
        assertEquals(film2, filmController.getAll().get(0));
    }

    @Test
    public void createAndGetTwoFilms() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().name("Name2").description("Description2")
                .releaseDate(LocalDate.of(2005, 12, 27)).duration(180L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film2);
        assertEquals(2, filmController.getAll().size());
        assertEquals(film1, filmController.getAll().get(0));
        assertEquals(film2, filmController.getAll().get(1));
    }

    @Test
    public void createAndGetFilmById() throws ValidationException, FilmNotFoundException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        assertEquals(film1, filmController.getById(1L));
    }

    @Test
    public void filmLikedByNonExistedUserThrowUserNotFoundException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> filmController.likeFilm(1L, 3L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void userLikesNonExistedFilmThrowFilmNotFoundException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, ()
                -> filmController.likeFilm(1L, 1L));
        assertEquals("Film with id = 1 not found", ex.getMessage());
    }

    @Test
    public void userLikesFilmSuccess() throws ValidationException, UserNotFoundException, FilmNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        filmController.likeFilm(1L, 1L);
        Set<Long> setForTest = new HashSet<>();
        setForTest.add(1L);
        assertEquals(setForTest, filmController.getById(1L).getLikesFromUsers());
    }

    @Test
    public void deleteLikeForFilmByNonExistedUserThrowUserNotFoundException() throws ValidationException {
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> filmController.deleteLike(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void deleteLikeForNonExistedFilmThrowFilmNotFoundException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, ()
                -> filmController.deleteLike(1L, 1L));
        assertEquals("Film with id = 1 not found", ex.getMessage());
    }

    @Test
    public void deleteLikeForFilmSuccess() throws ValidationException, UserNotFoundException, FilmNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        Film film1 = Film.builder().name("Name1").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        filmController.likeFilm(1L, 1L);
        filmController.deleteLike(1L, 1L);
        assertEquals(0, filmController.getById(1L).getLikesFromUsers().size());
    }

    @Test
    public void getCountAllFilmsWithoutRates() throws ValidationException {
        Film film1 = Film.builder().name("Name2").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().name("Name1").description("Description2")
                .releaseDate(LocalDate.of(2000, 1, 2)).duration(140L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film2);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }

    @Test
    public void getCountOneFilmAllFilmsWithoutRates() throws ValidationException {
        Film film1 = Film.builder().name("Name2").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().name("Name1").description("Description2")
                .releaseDate(LocalDate.of(2000, 1, 2)).duration(140L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film2);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        assertEquals(filmsToTest, filmController.getCountFilms(1));
    }

    @Test
    public void getCountFilmsWithRatingsDifferentRatings()
            throws ValidationException, UserNotFoundException, FilmNotFoundException {
        Film film1 = Film.builder().name("Name2").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().name("Name1").description("Description2")
                .releaseDate(LocalDate.of(2000, 1, 2)).duration(140L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film2);
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        User user2 = User.builder().email("vbscvb@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1985, 2, 9)).login("UserLogin2").build();
        userController.create(user2);
        User user3 = User.builder().email("lkfldsf@gmail.com").name("UserName3")
                .birthday(LocalDate.of(1991, 2, 26)).login("UserLogin3").build();
        userController.create(user3);
        filmController.likeFilm(2L, 1L);
        filmController.likeFilm(2L, 2L);
        filmController.likeFilm(1L, 3L);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }

    @Test
    public void getCountFilmsWithRatingsSameRatingsSortByName()
            throws ValidationException, UserNotFoundException, FilmNotFoundException {
        Film film1 = Film.builder().name("Name2").description("Description1")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film1);
        Film film2 = Film.builder().name("Name1").description("Description2")
                .releaseDate(LocalDate.of(2000, 1, 2)).duration(140L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmController.create(film2);
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        User user2 = User.builder().email("vbscvb@gmail.com").name("UserName2")
                .birthday(LocalDate.of(1985, 2, 9)).login("UserLogin2").build();
        userController.create(user2);
        User user3 = User.builder().email("lkfldsf@gmail.com").name("UserName3")
                .birthday(LocalDate.of(1991, 2, 26)).login("UserLogin3").build();
        userController.create(user3);
        filmController.likeFilm(2L, 1L);
        filmController.likeFilm(2L, 2L);
        filmController.likeFilm(1L, 3L);
        filmController.likeFilm(1L, 2L);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }
}