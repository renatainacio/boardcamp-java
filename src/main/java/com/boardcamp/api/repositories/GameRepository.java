package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.Game;
import java.util.List;


@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByName(String name);
}
