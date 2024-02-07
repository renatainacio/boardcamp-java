package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.boardcamp.api.models.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query(value="SELECT COUNT(id) FROM rentals WHERE game_Id = :gameId AND return_date IS NULL", nativeQuery = true)
    int countUnavailableUnits(@Param("gameId") Long gameId);

}
