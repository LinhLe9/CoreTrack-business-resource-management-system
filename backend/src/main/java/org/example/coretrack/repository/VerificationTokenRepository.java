package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
This class help service access to database class verificationtoken to find the instance that
including this token after user click on link (includes token) and send back to service to verify
 */

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    Optional<VerificationToken> findByUser (User user);
}
