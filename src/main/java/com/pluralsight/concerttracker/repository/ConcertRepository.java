package com.pluralsight.concerttracker.repository;

import com.pluralsight.concerttracker.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByYear(int year);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist a JOIN FETCH c.venue v JOIN FETCH c.promoter p " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Concert> findByArtistNameContaining(@Param("name") String name);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist JOIN FETCH c.venue v JOIN FETCH c.promoter " +
            "WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Concert> findByVenueName(@Param("name") String name);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist JOIN FETCH c.venue v JOIN FETCH c.promoter " +
            "WHERE LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<Concert> findByVenueCity(@Param("city") String city);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist JOIN FETCH c.venue JOIN FETCH c.promoter " +
            "WHERE c.ticketPrice <= :max")
    List<Concert> findByTicketPriceLessThanEqual(@Param("max") double max);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist JOIN FETCH c.venue JOIN FETCH c.promoter " +
            "WHERE c.ticketPrice BETWEEN :min AND :max")
    List<Concert> findByTicketPriceBetween(@Param("min") double min, @Param("max") double max);

    @Query("SELECT c FROM Concert c JOIN FETCH c.artist JOIN FETCH c.venue JOIN FETCH c.promoter " +
            "WHERE c.ticketPrice <= :maxPrice AND c.year >= :minYear")
    List<Concert> searchByMaxPriceAndMinYear(@Param("maxPrice") double maxPrice, @Param("minYear") int minYear);

    @Query("SELECT v.name, SUM(c.ticketPrice * c.ticketsSold) " +
            "FROM Concert c JOIN c.venue v " +
            "GROUP BY v.id, v.name")
    List<Object[]> revenuePerVenue();

    @Query("SELECT v.name, COUNT(c) " +
            "FROM Concert c JOIN c.venue v " +
            "GROUP BY v.id, v.name " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> busiestVenues();

    @Query("SELECT a.name, COUNT(c) " +
            "FROM Concert c JOIN c.artist a " +
            "GROUP BY a.id, a.name " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> busiestArtists();

    @Query("SELECT c.year, AVG(c.ticketPrice) " +
            "FROM Concert c " +
            "GROUP BY c.year " +
            "ORDER BY c.year")
    List<Object[]> avgPriceByYear();

    @Query("SELECT c FROM Concert c JOIN FETCH c.venue JOIN FETCH c.artist JOIN FETCH c.promoter")
    List<Concert> findAllWithAssociations();
}
