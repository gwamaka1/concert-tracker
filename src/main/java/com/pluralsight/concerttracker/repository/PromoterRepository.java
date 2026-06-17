package com.pluralsight.concerttracker.repository;

import com.pluralsight.concerttracker.model.Promoter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PromoterRepository extends JpaRepository<Promoter, Long> {
    List<Promoter> findByNameContainingIgnoreCase(String name);
}
