package be.technobel.chesstournament.dal.repositories;

import be.technobel.chesstournament.dal.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String login);
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT u.email FROM UserEntity u")
    List<String> getAllPlayerEmails();

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'PLAYER'")
    List<UserEntity> getAllPlayers();
}
