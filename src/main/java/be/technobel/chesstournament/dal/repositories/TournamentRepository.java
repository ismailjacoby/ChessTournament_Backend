package be.technobel.chesstournament.dal.repositories;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.enums.Status;
import be.technobel.chesstournament.pl.models.dtos.TournamentDto;
import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity,Long> {
    List<TournamentEntity> findTop10ByStatusInOrderByUpdateDateDesc(List<Status> notCompleted);

    @Query("SELECT t FROM TournamentEntity t LEFT JOIN FETCH t.participants WHERE t.id = :id")
    Optional<TournamentEntity> findByIdWithParticipants(@Param("id") Long id);

}
