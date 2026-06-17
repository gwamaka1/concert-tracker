package com.pluralsight.concerttracker.runner;

import com.pluralsight.concerttracker.model.Artist;
import com.pluralsight.concerttracker.model.Concert;
import com.pluralsight.concerttracker.model.Promoter;
import com.pluralsight.concerttracker.model.Venue;
import com.pluralsight.concerttracker.service.ConcertService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class StartupRunner implements CommandLineRunner {

    private final ConcertService service;
    private final Scanner scanner = new Scanner(System.in);

    public StartupRunner(ConcertService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.seedData();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> concertsMenu();
                case 2 -> searchMenu();
                case 3 -> artistsMenu();
                case 4 -> venuesMenu();
                case 5 -> promotersMenu();
                case 6 -> reportsMenu();
                case 0 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ── Main menu ─────────────────────────────────────────────────────────────

    private void printMainMenu() {
        System.out.println("\n=== Concert Tracker ===");
        System.out.println("1) Concerts");
        System.out.println("2) Search concerts");
        System.out.println("3) Artists");
        System.out.println("4) Venues");
        System.out.println("5) Promoters");
        System.out.println("6) Reports");
        System.out.println("0) Quit");
        System.out.print("Choose: ");
    }

    // ── Concerts screen ───────────────────────────────────────────────────────

    private void concertsMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Concerts ---");
            System.out.println("1) List all concerts");
            System.out.println("2) View one concert by id");
            System.out.println("3) Add a concert");
            System.out.println("4) Update ticket price");
            System.out.println("5) Update tickets sold");
            System.out.println("6) Delete a concert");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> listAllConcerts();
                    case 2 -> viewConcertById();
                    case 3 -> addConcert();
                    case 4 -> updateConcertPrice();
                    case 5 -> updateTicketsSold();
                    case 6 -> deleteConcert();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAllConcerts() {
        List<Concert> concerts = service.getAllConcerts();
        if (concerts.isEmpty()) { System.out.println("No concerts found."); return; }
        System.out.println("\nAll concerts:");
        for (Concert c : concerts) {
            System.out.println(c.getId() + " | " + c.getYear()
                    + " | " + c.getArtist().getName()
                    + " @ " + c.getVenue().getName()
                    + " | $" + c.getTicketPrice()
                    + " | sold: " + c.getTicketsSold());
        }
    }

    private void viewConcertById() {
        long id = readLong("Concert id: ");
        Concert c = service.getConcertById(id);
        System.out.println("\nid:           " + c.getId());
        System.out.println("Year:         " + c.getYear());
        System.out.println("Artist:       " + c.getArtist().getName());
        System.out.println("Venue:        " + c.getVenue().getName() + " (" + c.getVenue().getCity() + ")");
        System.out.println("Promoter:     " + c.getPromoter().getName());
        System.out.println("Ticket price: $" + c.getTicketPrice());
        System.out.println("Tickets sold: " + c.getTicketsSold());
    }

    private void addConcert() {
        int year         = readInt("Year: ");
        double price     = readDouble("Ticket price: ");
        int sold         = readInt("Tickets sold: ");

        System.out.println("Artists:");
        for (Artist a : service.getAllArtists()) System.out.println("  " + a);
        long artistId    = readLong("Artist id: ");

        System.out.println("Venues:");
        for (Venue v : service.getAllVenues()) System.out.println("  " + v);
        long venueId     = readLong("Venue id: ");

        System.out.println("Promoters:");
        for (Promoter p : service.getAllPromoters()) System.out.println("  " + p);
        long promoterId  = readLong("Promoter id: ");

        service.addConcert(year, price, sold, artistId, venueId, promoterId);
        System.out.println("Concert added.");
    }

    private void updateConcertPrice() {
        long id      = readLong("Concert id: ");
        double price = readDouble("New ticket price: ");
        service.updateTicketPrice(id, price);
        System.out.println("Price updated.");
    }

    private void updateTicketsSold() {
        long id  = readLong("Concert id: ");
        int sold = readInt("New tickets sold: ");
        service.updateTicketsSold(id, sold);
        System.out.println("Tickets sold updated.");
    }

    private void deleteConcert() {
        long id = readLong("Concert id: ");
        service.deleteConcert(id);
        System.out.println("Concert deleted.");
    }

    // ── Search screen ─────────────────────────────────────────────────────────

    private void searchMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Search Concerts ---");
            System.out.println("1) By year");
            System.out.println("2) By artist name");
            System.out.println("3) By venue name");
            System.out.println("4) By city");
            System.out.println("5) By maximum price");
            System.out.println("6) By price range");
            System.out.println("7) Advanced (max price + earliest year)");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> searchByYear();
                    case 2 -> searchByArtistName();
                    case 3 -> searchByVenueName();
                    case 4 -> searchByCity();
                    case 5 -> searchByMaxPrice();
                    case 6 -> searchByPriceRange();
                    case 7 -> advancedSearch();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printConcerts(List<Concert> concerts) {
        if (concerts.isEmpty()) { System.out.println("No concerts found."); return; }
        for (Concert c : concerts) {
            System.out.println(c.getId() + " | " + c.getYear()
                    + " | " + c.getArtist().getName()
                    + " @ " + c.getVenue().getName()
                    + " | $" + c.getTicketPrice());
        }
    }

    private void searchByYear() {
        int year = readInt("Year: ");
        printConcerts(service.findConcertsByYear(year));
    }

    private void searchByArtistName() {
        String name = readLine("Artist name contains: ");
        printConcerts(service.findConcertsByArtistName(name));
    }

    private void searchByVenueName() {
        String name = readLine("Venue name contains: ");
        printConcerts(service.findConcertsByVenueName(name));
    }

    private void searchByCity() {
        String city = readLine("City: ");
        printConcerts(service.findConcertsByVenueCity(city));
    }

    private void searchByMaxPrice() {
        double max = readDouble("Maximum price: ");
        printConcerts(service.findConcertsByMaxPrice(max));
    }

    private void searchByPriceRange() {
        double min = readDouble("Minimum price: ");
        double max = readDouble("Maximum price: ");
        printConcerts(service.findConcertsByPriceRange(min, max));
    }

    private void advancedSearch() {
        double maxPrice = readDouble("Maximum ticket price: ");
        int minYear     = readInt("Earliest year: ");
        printConcerts(service.advancedSearch(maxPrice, minYear));
    }

    // ── Artists screen ────────────────────────────────────────────────────────

    private void artistsMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Artists ---");
            System.out.println("1) List all artists");
            System.out.println("2) Add an artist");
            System.out.println("3) Find by genre");
            System.out.println("4) Find by name");
            System.out.println("5) Update genre");
            System.out.println("6) Delete an artist");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> listAllArtists();
                    case 2 -> addArtist();
                    case 3 -> findArtistsByGenre();
                    case 4 -> findArtistsByName();
                    case 5 -> updateArtistGenre();
                    case 6 -> deleteArtist();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAllArtists() {
        List<Artist> artists = service.getAllArtists();
        if (artists.isEmpty()) { System.out.println("No artists found."); return; }
        for (Artist a : artists) System.out.println(a);
    }

    private void addArtist() {
        String name  = readLine("Name: ");
        String genre = readLine("Genre: ");
        service.addArtist(name, genre);
        System.out.println("Artist added.");
    }

    private void findArtistsByGenre() {
        String genre = readLine("Genre: ");
        List<Artist> result = service.findArtistsByGenre(genre);
        if (result.isEmpty()) { System.out.println("No artists found."); return; }
        for (Artist a : result) System.out.println(a);
    }

    private void findArtistsByName() {
        String name = readLine("Name contains: ");
        List<Artist> result = service.findArtistsByName(name);
        if (result.isEmpty()) { System.out.println("No artists found."); return; }
        for (Artist a : result) System.out.println(a);
    }

    private void updateArtistGenre() {
        long id      = readLong("Artist id: ");
        String genre = readLine("New genre: ");
        service.updateArtistGenre(id, genre);
        System.out.println("Genre updated.");
    }

    private void deleteArtist() {
        long id = readLong("Artist id: ");
        service.deleteArtist(id);
        System.out.println("Artist deleted.");
    }

    // ── Venues screen ─────────────────────────────────────────────────────────

    private void venuesMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Venues ---");
            System.out.println("1) List all venues");
            System.out.println("2) Add a venue");
            System.out.println("3) Find by city");
            System.out.println("4) Find by name");
            System.out.println("5) Find by minimum capacity");
            System.out.println("6) Update capacity");
            System.out.println("7) Delete a venue");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> listAllVenues();
                    case 2 -> addVenue();
                    case 3 -> findVenuesByCity();
                    case 4 -> findVenuesByName();
                    case 5 -> findVenuesByMinCapacity();
                    case 6 -> updateVenueCapacity();
                    case 7 -> deleteVenue();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAllVenues() {
        List<Venue> venues = service.getAllVenues();
        if (venues.isEmpty()) { System.out.println("No venues found."); return; }
        for (Venue v : venues) System.out.println(v);
    }

    private void addVenue() {
        String name  = readLine("Name: ");
        String city  = readLine("City: ");
        int capacity = readInt("Capacity: ");
        service.addVenue(name, city, capacity);
        System.out.println("Venue added.");
    }

    private void findVenuesByCity() {
        String city = readLine("City: ");
        List<Venue> result = service.findVenuesByCity(city);
        if (result.isEmpty()) { System.out.println("No venues found."); return; }
        for (Venue v : result) System.out.println(v);
    }

    private void findVenuesByName() {
        String name = readLine("Name contains: ");
        List<Venue> result = service.findVenuesByName(name);
        if (result.isEmpty()) { System.out.println("No venues found."); return; }
        for (Venue v : result) System.out.println(v);
    }

    private void findVenuesByMinCapacity() {
        int min = readInt("Minimum capacity: ");
        List<Venue> result = service.findVenuesByMinCapacity(min);
        if (result.isEmpty()) { System.out.println("No venues found."); return; }
        for (Venue v : result) System.out.println(v);
    }

    private void updateVenueCapacity() {
        long id      = readLong("Venue id: ");
        int capacity = readInt("New capacity: ");
        service.updateVenueCapacity(id, capacity);
        System.out.println("Capacity updated.");
    }

    private void deleteVenue() {
        long id = readLong("Venue id: ");
        service.deleteVenue(id);
        System.out.println("Venue deleted.");
    }

    // ── Promoters screen ──────────────────────────────────────────────────────

    private void promotersMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Promoters ---");
            System.out.println("1) List all promoters");
            System.out.println("2) Add a promoter");
            System.out.println("3) Find by name");
            System.out.println("4) Delete a promoter");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> listAllPromoters();
                    case 2 -> addPromoter();
                    case 3 -> findPromotersByName();
                    case 4 -> deletePromoter();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAllPromoters() {
        List<Promoter> promoters = service.getAllPromoters();
        if (promoters.isEmpty()) { System.out.println("No promoters found."); return; }
        for (Promoter p : promoters) System.out.println(p);
    }

    private void addPromoter() {
        String name = readLine("Name: ");
        service.addPromoter(name);
        System.out.println("Promoter added.");
    }

    private void findPromotersByName() {
        String name = readLine("Name contains: ");
        List<Promoter> result = service.findPromotersByName(name);
        if (result.isEmpty()) { System.out.println("No promoters found."); return; }
        for (Promoter p : result) System.out.println(p);
    }

    private void deletePromoter() {
        long id = readLong("Promoter id: ");
        service.deletePromoter(id);
        System.out.println("Promoter deleted.");
    }

    // ── Reports screen ────────────────────────────────────────────────────────

    private void reportsMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Reports ---");
            System.out.println("1) Revenue per venue");
            System.out.println("2) Busiest venue and artist");
            System.out.println("3) Average ticket price by year");
            System.out.println("4) Capacity report");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> showRevenuePerVenue();
                case 2 -> showBusiestVenueAndArtist();
                case 3 -> showAvgPriceByYear();
                case 4 -> showCapacityReport();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showRevenuePerVenue() {
        List<Object[]> rows = service.getRevenuePerVenue();
        if (rows.isEmpty()) { System.out.println("No data."); return; }
        System.out.println("\nRevenue per venue:");
        for (Object[] row : rows) {
            String venue   = (String) row[0];
            Double revenue = (Double) row[1];
            System.out.printf("  %-35s $%,.2f%n", venue, revenue);
        }
    }

    private void showBusiestVenueAndArtist() {
        List<Object[]> venues  = service.getBusiestVenues();
        List<Object[]> artists = service.getBusiestArtists();
        if (!venues.isEmpty()) {
            String name  = (String) venues.get(0)[0];
            Long   count = (Long)   venues.get(0)[1];
            System.out.println("\nBusiest venue:  " + name + " (" + count + " concerts)");
        }
        if (!artists.isEmpty()) {
            String name  = (String) artists.get(0)[0];
            Long   count = (Long)   artists.get(0)[1];
            System.out.println("Busiest artist: " + name + " (" + count + " concerts)");
        }
        if (venues.isEmpty() && artists.isEmpty()) System.out.println("No data.");
    }

    private void showAvgPriceByYear() {
        List<Object[]> rows = service.getAvgPriceByYear();
        if (rows.isEmpty()) { System.out.println("No data."); return; }
        System.out.println("\nAverage ticket price by year:");
        for (Object[] row : rows) {
            Integer year = (Integer) row[0];
            Double avg   = (Double) row[1];
            System.out.printf("  %d  $%,.2f%n", year, avg);
        }
    }

    private void showCapacityReport() {
        List<Concert> concerts = service.getCapacityReport();
        if (concerts.isEmpty()) { System.out.println("No data."); return; }
        System.out.println("\nCapacity report:");
        for (Concert c : concerts) {
            int sold     = c.getTicketsSold();
            int capacity = c.getVenue().getCapacity();
            double pct   = sold * 100.0 / capacity;
            String flag  = (sold >= capacity) ? " *** SOLD OUT ***" : "";
            System.out.printf("  %-30s @ %-28s %5.1f%%%s%n",
                    c.getArtist().getName(), c.getVenue().getName(), pct, flag);
        }
    }

    // ── Input helpers ─────────────────────────────────────────────────────────

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return readInt();
    }

    private long readLong(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
