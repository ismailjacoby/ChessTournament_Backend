package be.technobel.chesstournament.dal.repositories;

import be.technobel.chesstournament.dal.models.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity,Long> {
}
