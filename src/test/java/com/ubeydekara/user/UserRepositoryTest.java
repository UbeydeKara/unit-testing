package com.ubeydekara.user;

import com.ubeydekara.user.enums.Gender;
import com.ubeydekara.user.domain.User;
import com.ubeydekara.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckWhenUserEmailExists() {
        // given
        String email = "maria@gmail.com";

        User user = User.builder()
                .name("Maria")
                .email(email)
                .gender(Gender.FEMALE)
                .build();

        underTest.save(user);

        // when
        boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldCheckWhenUserEmailDoesNotExists() {
        // given
        String email = "maria@gmail.com";

        // when
        boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isFalse();
    }
}