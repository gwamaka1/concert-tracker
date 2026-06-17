package com.pluralsight.concerttracker.service;

import com.pluralsight.concerttracker.model.Artist;
import com.pluralsight.concerttracker.model.Concert;
import com.pluralsight.concerttracker.model.Promoter;
import com.pluralsight.concerttracker.model.Venue;
import com.pluralsight.concerttracker.repository.ArtistRepository;
import com.pluralsight.concerttracker.repository.ConcertRepository;
import com.pluralsight.concerttracker.repository.PromoterRepository;
import com.pluralsight.concerttracker.repository.VenueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final PromoterRepository promoterRepository;

    public ConcertService(ConcertRepository concertRepository,
                          ArtistRepository artistRepository,
                          VenueRepository venueRepository,
                          PromoterRepository promoterRepository) {
        this.concertRepository = concertRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.promoterRepository = promoterRepository;
    }

    // ── Venue ────────────────────────────────────────────────────────────────

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No venue with id " + id));
    }

    public Venue addVenue(String name, String city, int capacity) {
        return venueRepository.save(new Venue(name, city, capacity));
    }

    public List<Venue> findVenuesByCity(String city) {
        return venueRepository.findByCityIgnoreCase(city);
    }

    public List<Venue> findVenuesByName(String name) {
        return venueRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Venue> findVenuesByMinCapacity(int minCapacity) {
        return venueRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    public Venue updateVenueCapacity(Long id, int newCapacity) {
        Venue venue = getVenueById(id);
        venue.setCapacity(newCapacity);
        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new RuntimeException("No venue with id " + id);
        }
        venueRepository.deleteById(id);
    }

    // ── Artist ───────────────────────────────────────────────────────────────

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No artist with id " + id));
    }

    public Artist addArtist(String name, String genre) {
        return artistRepository.save(new Artist(name, genre));
    }

    public List<Artist> findArtistsByGenre(String genre) {
        return artistRepository.findByGenreIgnoreCase(genre);
    }

    public List<Artist> findArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }

    public Artist updateArtistGenre(Long id, String newGenre) {
        Artist artist = getArtistById(id);
        artist.setGenre(newGenre);
        return artistRepository.save(artist);
    }

    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("No artist with id " + id);
        }
        artistRepository.deleteById(id);
    }

    // ── Promoter ─────────────────────────────────────────────────────────────

    public List<Promoter> getAllPromoters() {
        return promoterRepository.findAll();
    }

    public Promoter getPromoterById(Long id) {
        return promoterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No promoter with id " + id));
    }

    public Promoter addPromoter(String name) {
        return promoterRepository.save(new Promoter(name));
    }

    public List<Promoter> findPromotersByName(String name) {
        return promoterRepository.findByNameContainingIgnoreCase(name);
    }

    public void deletePromoter(Long id) {
        if (!promoterRepository.existsById(id)) {
            throw new RuntimeException("No promoter with id " + id);
        }
        promoterRepository.deleteById(id);
    }

    // ── Concert ───────────────────────────────────────────────────────────────

    public List<Concert> getAllConcerts() {
        return concertRepository.findAllWithAssociations();
    }

    public Concert getConcertById(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No concert with id " + id));
    }

    public Concert addConcert(int year, double ticketPrice, int ticketsSold,
                              Long artistId, Long venueId, Long promoterId) {
        if (ticketPrice < 0) throw new IllegalArgumentException("Ticket price cannot be negative.");
        if (ticketsSold < 0) throw new IllegalArgumentException("Tickets sold cannot be negative.");
        Venue venue = getVenueById(venueId);
        if (ticketsSold > venue.getCapacity()) {
            throw new IllegalArgumentException("Tickets sold (" + ticketsSold
                    + ") exceeds venue capacity (" + venue.getCapacity() + ").");
        }
        Artist artist = getArtistById(artistId);
        Promoter promoter = getPromoterById(promoterId);
        return concertRepository.save(new Concert(year, ticketPrice, ticketsSold, artist, venue, promoter));
    }

    public Concert updateTicketPrice(Long id, double newPrice) {
        if (newPrice < 0) throw new IllegalArgumentException("Ticket price cannot be negative.");
        Concert concert = getConcertById(id);
        concert.setTicketPrice(newPrice);
        return concertRepository.save(concert);
    }

    public Concert updateTicketsSold(Long id, int newSold) {
        if (newSold < 0) throw new IllegalArgumentException("Tickets sold cannot be negative.");
        Concert concert = getConcertById(id);
        if (newSold > concert.getVenue().getCapacity()) {
            throw new IllegalArgumentException("Tickets sold (" + newSold
                    + ") exceeds venue capacity (" + concert.getVenue().getCapacity() + ").");
        }
        concert.setTicketsSold(newSold);
        return concertRepository.save(concert);
    }

    public void deleteConcert(Long id) {
        if (!concertRepository.existsById(id)) {
            throw new RuntimeException("No concert with id " + id);
        }
        concertRepository.deleteById(id);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public List<Concert> findConcertsByYear(int year) {
        return concertRepository.findByYear(year);
    }

    public List<Concert> findConcertsByArtistName(String name) {
        return concertRepository.findByArtistNameContaining(name);
    }

    public List<Concert> findConcertsByVenueName(String name) {
        return concertRepository.findByVenueName(name);
    }

    public List<Concert> findConcertsByVenueCity(String city) {
        return concertRepository.findByVenueCity(city);
    }

    public List<Concert> findConcertsByMaxPrice(double max) {
        return concertRepository.findByTicketPriceLessThanEqual(max);
    }

    public List<Concert> findConcertsByPriceRange(double min, double max) {
        return concertRepository.findByTicketPriceBetween(min, max);
    }

    public List<Concert> advancedSearch(double maxPrice, int minYear) {
        return concertRepository.searchByMaxPriceAndMinYear(maxPrice, minYear);
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public List<Object[]> getRevenuePerVenue() {
        return concertRepository.revenuePerVenue();
    }

    public List<Object[]> getBusiestVenues() {
        return concertRepository.busiestVenues();
    }

    public List<Object[]> getBusiestArtists() {
        return concertRepository.busiestArtists();
    }

    public List<Object[]> getAvgPriceByYear() {
        return concertRepository.avgPriceByYear();
    }

    public List<Concert> getCapacityReport() {
        return concertRepository.findAllWithAssociations();
    }

    // ── Seed ──────────────────────────────────────────────────────────────────

    public void seedData() {
        if (concertRepository.count() > 0) {
            return;
        }

        Venue msg      = venueRepository.save(new Venue("Madison Square Garden", "New York", 20000));
        Venue o2       = venueRepository.save(new Venue("The O2 Arena", "London", 20000));
        Venue redRocks = venueRepository.save(new Venue("Red Rocks Amphitheatre", "Morrison", 9525));

        Artist taylor    = artistRepository.save(new Artist("Taylor Swift", "Pop"));
        Artist metallica = artistRepository.save(new Artist("Metallica", "Metal"));
        Artist radiohead = artistRepository.save(new Artist("Radiohead", "Alternative"));
        Artist beyonce   = artistRepository.save(new Artist("Beyonce", "R&B"));

        Promoter liveNation  = promoterRepository.save(new Promoter("Live Nation"));
        Promoter aegPresents = promoterRepository.save(new Promoter("AEG Presents"));

        concertRepository.save(new Concert(2022, 185.00, 20000, taylor,    msg,      liveNation));
        concertRepository.save(new Concert(2023, 195.00, 19500, taylor,    o2,       aegPresents));
        concertRepository.save(new Concert(2021, 150.00,  9000, taylor,    redRocks, liveNation));
        concertRepository.save(new Concert(2023, 120.00, 20000, metallica, msg,      liveNation));
        concertRepository.save(new Concert(2022, 110.00, 18000, metallica, o2,       aegPresents));
        concertRepository.save(new Concert(2020,  95.00,  9525, metallica, redRocks, aegPresents));
        concertRepository.save(new Concert(2022,  75.00,  8000, radiohead, redRocks, liveNation));
        concertRepository.save(new Concert(2019,  80.00, 15000, radiohead, msg,      aegPresents));
        concertRepository.save(new Concert(2023,  90.00, 19000, radiohead, o2,       liveNation));
        concertRepository.save(new Concert(2023, 220.00, 20000, beyonce,   msg,      aegPresents));
        concertRepository.save(new Concert(2022, 200.00, 20000, beyonce,   o2,       liveNation));
        concertRepository.save(new Concert(2021, 175.00,  9000, beyonce,   redRocks, aegPresents));
    }
}
