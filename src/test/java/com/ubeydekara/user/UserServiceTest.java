package com.ubeydekara.user;

import com.ubeydekara.user.enums.Gender;
import com.ubeydekara.user.domain.User;
import com.ubeydekara.user.repository.UserRepository;
import com.ubeydekara.user.service.UserService;
import com.ubeydekara.user.exception.BadRequestException;
import com.ubeydekara.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
    }

    @Test
    void canGetAllUsers() {
        // when
        underTest.getAllUsers();
        // then
        verify(userRepository).findAll();
    }

    @Test
    void canAddUser() {
        // given
        User user = User.builder()
                .name("Maria")
                .email("maria@gmail.com")
                .gender(Gender.FEMALE)
                .build();

        // when
        underTest.addUser(user);

        // then
        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        User user = User.builder()
                .name("Maria")
                .email("maria@gmail.com")
                .gender(Gender.FEMALE)
                .build();

        given(userRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + user.getEmail() + " taken");

        verify(userRepository, never()).save(any());

    }

    @Test
    void canDeleteUser() {
        // given
        UUID id = UUID.randomUUID();
        given(userRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteUser(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteUserNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(userRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteUser(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id " + id + " does not exists");

        verify(userRepository, never()).deleteById(any());
    }
}