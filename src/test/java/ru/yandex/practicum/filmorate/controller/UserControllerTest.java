package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс с тестами для класса UserController
 */
@SpringBootTest
class UserControllerTest {
    @Autowired
    UserController userController;

    @AfterEach
    public void afterEach() {
        User.setCounter(new AtomicLong(0));
        userController.getUserService().getUserStorage().deleteAll();
    }

    @Test
    public void createUserEmptyEmailThrowsValidationException() {
        User user = User.builder().email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserInvalidEmailThrowsValidationException() {
        User user = User.builder().email("qwerty.wsx").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserValidEmailSuccess() throws ValidationException {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserEmptyLoginThrowsValidationException() {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("").build();
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserLoginHasSpacesThrowsValidationException() {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("User Login").build();
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserValidLoginSuccess() throws ValidationException {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserInvalidBirthdayThrowsValidationException() {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.now().plusDays(1)).login("UserLogin").build();
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void createUserValidBirthdaySuccess() throws ValidationException {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.now().minusDays(1)).login("UserLogin").build();
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserNameIsEmptyNameEqualsLogin() throws ValidationException {
        User user = User.builder().email("qwerty@gmail.com")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void createUserNameHasOnlySpacesNameEqualsLogin() throws ValidationException {
        User user = User.builder().email("qwerty@gmail.com").name("   ")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserNewEmptyEmailThrowsValidationException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewInvalidEmailThrowsValidationException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewEmptyLoginThrowsValidationException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("").build();
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewLoginHasSpacesThrowsValidationException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("Log in").build();
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewBirthdayIsAfterNowThrowsValidationException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.now().plusDays(1)).login("UserLogin").build();
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void updateUserNewNameIsEmptyNameEqualsLogin() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("qwerty@gmail.com").name("")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        userController.update(user2);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserSuccess() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().id(1L).email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.update(user2);
        assertEquals(1, userController.getAll().size());
        assertEquals(user2, userController.getAll().get(0));
    }

    @Test
    public void createAndGetTwoUsers() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        assertEquals(2, userController.getAll().size());
        assertEquals(user1, userController.getAll().get(0));
        assertEquals(user2, userController.getAll().get(1));
    }

    @Test
    public void createAndGetUserById() throws ValidationException, UserNotFoundException {
        User user = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user);
        User getUser = userController.getById(1L);
        assertEquals(getUser, user);
    }

    @Test
    public void getUserByNonExistedIdThrowNotFoundUserException() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userController.getById(1L));
        assertEquals("User with id = 1 not found", ex.getMessage());
    }

    @Test
    public void addFriendToNonExistedUserThrowNotFoundUserException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.addToFriends(3L, 1L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void addNonExistedFriendToUserThrowNotFoundUserException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.addToFriends(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void addFriendToUserSuccess() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        assertEquals(List.of(user2), userController.getFriendsForUser(1L));
    }

    @Test
    public void deleteFriendFromNonExistedUserThrowNotFoundUserException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.deleteFromFriends(3L, 1L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void deleteNonExistedFriendFromUserThrowNotFoundUserException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.deleteFromFriends(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void deleteFriendFromUserSuccess() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        userController.deleteFromFriends(1L, 2L);
        assertEquals(0, userController.getFriendsForUser(1L).size());
    }

    @Test
    public void getFriendsForUserNoFriends() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        assertEquals(0,userController.getFriendsForUser(1L).size());
    }

    @Test
    public void getFriendsForUserOneFriend() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        assertEquals(1,userController.getFriendsForUser(1L).size());
        assertEquals(0,userController.getFriendsForUser(2L).size());
    }

    @Test
    public void getFriendsForNonExistedUserThrowUserNotFoundException() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getFriendsForUser(1L));
        assertEquals("User with id = 1 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsNoFriends() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        assertEquals(0, userController.getCommonFriends(1L, 2L).size());
    }

    @Test
    public void getCommonFriendsFroNonExistedUserThrowUserNotFoundException() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getCommonFriends(2L, 1L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsFroNonExistedOtherUser() throws ValidationException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getCommonFriends(1L, 3L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsOneFriend() throws ValidationException, UserNotFoundException {
        User user1 = User.builder().email("qwerty@gmail.com").name("UserName")
                .birthday(LocalDate.of(1990, 6, 9)).login("UserLogin").build();
        User user2 = User.builder().email("zxcvb@gmail.com").name("NewNameUser")
                .birthday(LocalDate.of(1988, 5, 9)).login("NewUserLogin").build();
        userController.create(user1);
        userController.create(user2);
        User user3 = User.builder().email("test@gmail.com").name("User2Name")
                .birthday(LocalDate.of(1996, 1, 1)).login("User3Login").build();
        userController.create(user3);
        userController.addToFriends(1L, 3L);
        userController.addToFriends(2L, 3L);
        assertEquals(1, userController.getCommonFriends(1L, 2L).size());
        assertEquals(List.of(user3), userController.getCommonFriends(1L, 2L));
    }
}