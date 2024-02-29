package be.technobel.chesstournament.dal.repositories;

import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity,Long> {
    Optional<ScoreEntity> findByPlayer(UserEntity player);

    List<ScoreEntity> findByMatch_Tournament_Id(Long tournamentid);
}
