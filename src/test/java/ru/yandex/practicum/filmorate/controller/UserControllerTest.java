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
        User user = new User();
        user.setEmail("");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserInvalidEmailThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty.wsx");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserValidEmailSuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserEmptyLoginThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserLoginHasSpacesThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("User Login");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserValidLoginSuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserInvalidBirthdayThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.now().plusDays(1));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void createUserValidBirthdaySuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.now().minusDays(1));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserNameIsEmptyNameEqualsLogin() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void createUserNameHasOnlySpacesNameEqualsLogin() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        user.setName("   ");
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserNewEmptyEmailThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewInvalidEmailThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewEmptyLoginThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewLoginHasSpacesThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("Log in");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewBirthdayIsAfterNowThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.now().plusDays(1));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void updateUserNewNameIsEmptyNameEqualsLogin() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        userController.update(user2);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.update(user2);
        assertEquals(1, userController.getAll().size());
        assertEquals(user2, userController.getAll().get(0));
    }

    @Test
    public void createAndGetTwoUsers() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        assertEquals(2, userController.getAll().size());
        assertEquals(user1, userController.getAll().get(0));
        assertEquals(user2, userController.getAll().get(1));
    }

    @Test
    public void createAndGetUserById() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
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
    public void addFriendToNonExistedUserThrowNotFoundUserException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.addToFriends(3L, 1L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void addNonExistedFriendToUserThrowNotFoundUserException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.addToFriends(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void addFriendToUserSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        assertEquals(List.of(user2), userController.getFriendsForUser(1L));
    }

    @Test
    public void deleteFriendFromNonExistedUserThrowNotFoundUserException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.deleteFromFriends(3L, 1L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void deleteNonExistedFriendFromUserThrowNotFoundUserException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.deleteFromFriends(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void deleteFriendFromUserSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        userController.deleteFromFriends(1L, 2L);
        assertEquals(0, userController.getFriendsForUser(1L).size());
    }

    @Test
    public void getFriendsForUserNoFriends() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        assertEquals(0,userController.getFriendsForUser(1L).size());
    }

    @Test
    public void getFriendsForUserOneFriend() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1L, 2L);
        assertEquals(1,userController.getFriendsForUser(1L).size());
        assertEquals(1,userController.getFriendsForUser(2L).size());
    }

    @Test
    public void getFriendsForNonExistedUserThrowUserNotFoundException() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getFriendsForUser(1L));
        assertEquals("User with id = 1 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsNoFriends() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        assertEquals(0, userController.getCommonFriends(1L, 2L).size());
    }

    @Test
    public void getCommonFriendsFroNonExistedUserThrowUserNotFoundException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getCommonFriends(2L, 1L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsFroNonExistedOtherUser() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> userController.getCommonFriends(1L, 3L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void getCommonFriendsOneFriend() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        User user3 = new User();
        user3.setEmail("test@gmail.com");
        user3.setName("User2Name");
        user3.setBirthday(LocalDate.of(1996, 1, 1));
        user3.setLogin("User3Login");
        userController.create(user3);
        userController.addToFriends(1L, 3L);
        userController.addToFriends(2L, 3L);
        assertEquals(1, userController.getCommonFriends(1L, 2L).size());
        assertEquals(List.of(user3), userController.getCommonFriends(1L, 2L));
    }
}