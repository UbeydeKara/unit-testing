package com.ubeydekara.user.integration;

import com.ubeydekara.user.enums.Gender;
import com.ubeydekara.user.domain.User;
import com.ubeydekara.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.yml"
)
@AutoConfigureMockMvc
public class UserIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final Faker faker = new Faker();

    @Test
    void canRegisterNewUser() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        User user = User.builder()
                .name(name)
                .email(String.format("%s@gmail.com",
                        StringUtils.trimAllWhitespace(name.trim().toLowerCase())))
                .gender(Gender.FEMALE)
                .build();

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)));

        // then
        resultActions.andExpect(status().isOk());
        List<User> users = userRepository.findAll();
        assertThat(users)
                .usingElementComparatorIgnoringFields("id")
                .contains(user);
    }

    @Test
    void canDeleteUser() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@gmail.com",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        User user = User.builder()
                .name(name)
                .email(email)
                .gender(Gender.FEMALE)
                .build();

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        MvcResult getUsersResult = mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getUsersResult
                .getResponse()
                .getContentAsString();

        List<User> users = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        UUID id = users.stream()
                .filter(s -> s.getEmail().equals(user.getEmail()))
                .map(User::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "user with email: " + email + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/users/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = userRepository.existsById(id);
        assertThat(exists).isFalse();
    }
}
