package com.hostel.management.repository;

import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    List<User> findByRole(User.Role role);
    List<User> findByRoomId(Long roomId);
    boolean existsByEmail(String email);
}
