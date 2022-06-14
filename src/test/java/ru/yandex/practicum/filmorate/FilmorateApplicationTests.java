package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.UserRecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.userfilmlikes.UserFilmLikesDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
class FilmorateApplicationTests {
	private final UserDbStorage userDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final FriendshipDbStorage friendshipDbStorage;
	private final UserFilmLikesDbStorage userFilmLikesDbStorage;
	private final UserService userService;

	@Test
	public void checkCreatedUserFindUserById() {
		User user = User.builder().email("").name("UserName")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		User userToCompare = User.builder().id(1L).email("").name("UserName")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin")
				.friends(new HashSet<>()).build();
		userDbStorage.create(user);
		User getUser = userDbStorage.getById(1L);
		assertThat(getUser, is(equalTo(userToCompare)));
	}

	@Test
	public void checkUpdatedUserFindUserById() {
		User user = User.builder().email("qwe@gmail.com").name("UserName")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user);
		userDbStorage.update(User.builder().id(1L).email("poiuy@yandex.ru").name("UserNameEdit")
				.birthday(LocalDate.of(1988, 3, 4)).login("UserLoginEdit").build());
		User userToCompare = User.builder().id(1L).email("poiuy@yandex.ru").name("UserNameEdit")
				.birthday(LocalDate.of(1988, 3, 4)).login("UserLoginEdit")
				.friends(new HashSet<>()).build();
		User getUser = userDbStorage.getById(1L);
		assertThat(getUser, is(equalTo(userToCompare)));
	}

	@Test
	public void checkUsersGetAllUsers() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		List<User> listToCompare = new ArrayList<>();
		User user1ToCompare = User.builder().id(1L).email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin")
				.friends(new HashSet<>()).build();
		User user2ToCompare = User.builder().id(2L).email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2")
				.friends(new HashSet<>()).build();
		listToCompare.add(user1ToCompare);
		listToCompare.add(user2ToCompare);
		assertThat(userDbStorage.getAll(), is(equalTo(listToCompare)));
	}

	@Test
	public void checkNoUsersAfterDeleteAllUsers() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		List<User> listToCompare = new ArrayList<>();
		userDbStorage.deleteAll();
		assertThat(userDbStorage.getAll(), is(equalTo(listToCompare)));
	}

	@Test
	public void checkCreatedFilmFindFilmById() {
		Film film = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film filmToCompare = Film.builder().id(1L).name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).name("PG-13").build()).likesFromUsers(new HashSet<>()).build();
		filmDbStorage.create(film);
		assertThat(filmDbStorage.getById(1L), is(equalTo(filmToCompare)));
	}

	@Test
	public void checkUpdatedFilmFindFilmById() {
		Film film = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film);
		filmDbStorage.update(Film.builder().id(1L).name("NameEdit").description("DescriptionEdit")
				.releaseDate(LocalDate.of(2003, 5, 12)).duration(118L)
				.mpa(Mpa.builder().id(1).build()).build());
		Film filmToCompare = Film.builder().id(1L).name("NameEdit").description("DescriptionEdit")
				.releaseDate(LocalDate.of(2003, 5, 12)).duration(118L)
				.mpa(Mpa.builder().id(1).name("G").build()).likesFromUsers(new HashSet<>()).build();
		assertThat(filmDbStorage.getById(1L), is(equalTo(filmToCompare)));
	}

	@Test
	public void checkFilmsGetAllFilms() {
		Film film1 = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film1);
		Film film2 = Film.builder().name("Name2").description("Description2")
				.releaseDate(LocalDate.of(2003, 2, 7)).duration(112L)
				.mpa(Mpa.builder().id(2).build()).build();
		filmDbStorage.create(film2);
		List<Film> listToCompare = new ArrayList<>();
		Film film1ToCompare = Film.builder().id(1L).name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).name("PG-13").build()).likesFromUsers(new HashSet<>()).build();
		Film film2ToCompare = Film.builder().id(2L).name("Name2").description("Description2")
				.releaseDate(LocalDate.of(2003, 2, 7)).duration(112L)
				.mpa(Mpa.builder().id(2).name("PG").build()).likesFromUsers(new HashSet<>()).build();
		listToCompare.add(film1ToCompare);
		listToCompare.add(film2ToCompare);
		assertThat(filmDbStorage.getAll(), is(equalTo(listToCompare)));
	}

	@Test
	public void checkNoFilmsAfterDeleteAllFilms() {
		Film film1 = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film1);
		Film film2 = Film.builder().name("Name2").description("Description2")
				.releaseDate(LocalDate.of(2003, 2, 7)).duration(112L)
				.mpa(Mpa.builder().id(2).build()).build();
		filmDbStorage.create(film2);
		List<Film> listToCompare = new ArrayList<>();
		filmDbStorage.deleteAll();
		assertThat(filmDbStorage.getAll(), is(equalTo(listToCompare)));
	}

	@Test
	public void addToFriendsCheckUserHasFriend() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		friendshipDbStorage.addToFriends(1L, 2L);
		List<User> listToCompare = new ArrayList<>();
		listToCompare.add(User.builder().id(2L).email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build());
		assertThat(friendshipDbStorage.getFriendsForUser(1L), is(equalTo(listToCompare)));
	}

	@Test
	public void getCommonFriendsForUsers() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		User user3 = User.builder().email("ghj@gmail.com").name("UserName3")
				.birthday(LocalDate.of(1976, 6, 9)).login("UserLogin3").build();
		userDbStorage.create(user3);
		friendshipDbStorage.addToFriends(1L, 2L);
		friendshipDbStorage.addToFriends(3L, 2L);
		List<User> listToCompare = new ArrayList<>();
		listToCompare.add(User.builder().id(2L).email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build());
		assertThat(friendshipDbStorage.getCommonFriends(1L, 3L), is(equalTo(listToCompare)));
	}

	@Test
	public void deleteFromFriendsCheckFriendList() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		friendshipDbStorage.addToFriends(1L, 2L);
		friendshipDbStorage.deleteFromFriends(1L, 2L);
		assertThat(friendshipDbStorage.getFriendsForUser(1L), is(equalTo(new ArrayList<>())));
	}

	@Test
	public void likeFilmCheckFilmInfo() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		Film film = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film);
		userFilmLikesDbStorage.saveLike(1L, 1L);
		Set<Long> idSet = new HashSet<>();
		idSet.add(1L);
		Film filmToCompare = Film.builder().id(1L).name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).name("PG-13").build()).likesFromUsers(idSet).build();
		assertThat(filmDbStorage.getById(1L), is(equalTo(filmToCompare)));
	}

	@Test
	public void getFilm1ByLikesCount() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("asd@gmail.com").name("UserName2")
				.birthday(LocalDate.of(1986, 1, 2)).login("UserLogin2").build();
		userDbStorage.create(user2);
		Film film1 = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film1);
		Film film2 = Film.builder().name("Name2").description("Description2")
				.releaseDate(LocalDate.of(1990, 11, 14)).duration(110L)
				.mpa(Mpa.builder().id(1).build()).build();
		filmDbStorage.create(film2);
		Film filmToCompare = Film.builder().id(1L).name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).name("PG-13").build()).build();
		List<Film> listToCompare = new ArrayList<>();
		listToCompare.add(filmToCompare);
		userFilmLikesDbStorage.saveLike(1L, 1L);
		userFilmLikesDbStorage.saveLike(1L, 2L);
		userFilmLikesDbStorage.saveLike(2L, 2L);
		assertThat(userFilmLikesDbStorage.getCount(1), is(equalTo(listToCompare)));
	}

	@Test
	public void removeLikeFilmCheckFilmInfo() {
		User user1 = User.builder().email("qwe@gmail.com").name("UserName1")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		userDbStorage.create(user1);
		Film film = Film.builder().name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).build()).build();
		filmDbStorage.create(film);
		userFilmLikesDbStorage.saveLike(1L, 1L);
		userFilmLikesDbStorage.removeLike(1L, 1L);
		Film filmToCompare = Film.builder().id(1L).name("Name").description("Description")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
				.mpa(Mpa.builder().id(3).name("PG-13").build()).likesFromUsers(new HashSet<>()).build();
		assertThat(filmDbStorage.getById(1L), is(equalTo(filmToCompare)));
	}

	/*@Test
	public void getRecommendation() throws ValidationException, UserNotFoundException, FilmNotFoundException {
		User user1 = User.builder().id(1L).email("qwerty@gmail.com").name("UserName")
				.birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
		User user2 = User.builder().id(2L).email("zxcvb@gmail.com").name("NewNameUser")
				.birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
		userDbStorage.create(user1);
		userDbStorage.create(user2);

		Film film1 = Film.builder().id(1L).name("Name1").description("Description1")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film film2 = Film.builder().id(2L).name("Name2").description("Description2")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film film3 = Film.builder().id(3L).name("Name3").description("Description3")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film film4 = Film.builder().id(4L).name("Name4").description("Description4")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film film5 = Film.builder().id(5L).name("Name5").description("Description5")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();
		Film film6 = Film.builder().id(6L).name("Name6").description("Description6")
				.releaseDate(LocalDate.of(1995, 12, 27)).duration(150L)
				.mpa(Mpa.builder().id(3).build()).build();

		filmDbStorage.create(film1);
		filmDbStorage.create(film2);
		filmDbStorage.create(film3);
		filmDbStorage.create(film4);
		filmDbStorage.create(film5);
		filmDbStorage.create(film6);

		userFilmLikesDbStorage.saveLike(1L, 1L);
		userFilmLikesDbStorage.saveLike(2L, 1L);
		userFilmLikesDbStorage.saveLike(3L, 1L);
		userFilmLikesDbStorage.saveLike(4L, 1L);

		userFilmLikesDbStorage.saveLike(1L, 2L);
		userFilmLikesDbStorage.saveLike(2L, 2L);
		userFilmLikesDbStorage.saveLike(3L, 2L);
		userFilmLikesDbStorage.saveLike(4L, 2L);
		userFilmLikesDbStorage.saveLike(6L, 2L);
		//Здесь ошибка !!!
		Long filmId = userService.getRecommendat(user1.getId()).get(0).getId();

		Film film = filmDbStorage.getById(filmId);

		assertEquals(film.getId(), 6);
	}*/
}
