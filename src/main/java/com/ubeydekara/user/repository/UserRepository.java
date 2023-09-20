package com.ubeydekara.user.repository;

import com.ubeydekara.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository
        extends JpaRepository<User, UUID> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM users s " +
            "WHERE s.email = ?1"
    )
    Boolean selectExistsEmail(String email);
}
