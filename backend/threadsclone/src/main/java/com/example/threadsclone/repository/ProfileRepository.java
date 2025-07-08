package com.example.threadsclone.repository;

import com.example.threadsclone.entity.Profile;
import com.example.threadsclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUser_Username(String username);
}
