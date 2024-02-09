package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByName(String name);
}
