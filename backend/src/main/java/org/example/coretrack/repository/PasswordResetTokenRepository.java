package org.example.coretrack.repository;

import org.example.coretrack.model.auth.PasswordResetToken;
import org.example.coretrack.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    
    // Delete expired tokens
    void deleteByExpiryDateBefore(Date date);
    
    // Find by user
    Optional<PasswordResetToken> findByUser(User user);
    
    // Delete by token
    void deleteByToken(String token);
}
