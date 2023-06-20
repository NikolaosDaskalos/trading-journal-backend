package gr.aueb.cf3.tradingjournalapp.repository;

import gr.aueb.cf3.tradingjournalapp.model.Token;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT T FROM Token T JOIN User U on T.user.id = U.id WHERE U.id = :userId AND (T.expired = false AND T.revoked = false) AND T.tokenType = :tokenType")
    List<Token> findAllValidTokensByUser(Long userId, TokenType tokenType);

    Optional<Token> findByToken(String Token);
}
