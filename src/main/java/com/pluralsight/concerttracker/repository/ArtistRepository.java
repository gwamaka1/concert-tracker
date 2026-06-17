package com.pluralsight.concerttracker.repository;

import com.pluralsight.concerttracker.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByGenreIgnoreCase(String genre);
    List<Artist> findByNameContainingIgnoreCase(String name);
}
