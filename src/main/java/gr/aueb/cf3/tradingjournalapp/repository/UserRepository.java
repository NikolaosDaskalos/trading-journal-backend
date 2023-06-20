package gr.aueb.cf3.tradingjournalapp.repository;

import gr.aueb.cf3.tradingjournalapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.email = ?1")
    boolean isEmailExists(String email);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1")
    boolean isUsernameExists(String username);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1 AND U.password = ?2")
    boolean isUserValid(String username, String password);
}
