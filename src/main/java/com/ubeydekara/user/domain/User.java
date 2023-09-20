package com.ubeydekara.user.domain;

import com.ubeydekara.user.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @NonNull
    @Column(nullable = false)
    private String name;

    @Email
    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
}
