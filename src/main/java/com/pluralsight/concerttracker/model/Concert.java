package com.pluralsight.concerttracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "concerts")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private double ticketPrice;

    @Column(nullable = false)
    private int ticketsSold;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne(optional = false)
    @JoinColumn(name = "promoter_id", nullable = false)
    private Promoter promoter;

    public Concert() {}

    public Concert(int year, double ticketPrice, int ticketsSold,
                   Artist artist, Venue venue, Promoter promoter) {
        this.year = year;
        this.ticketPrice = ticketPrice;
        this.ticketsSold = ticketsSold;
        this.artist = artist;
        this.venue = venue;
        this.promoter = promoter;
    }

    public Long getId() { return id; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    public int getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(int ticketsSold) { this.ticketsSold = ticketsSold; }
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }
    public Promoter getPromoter() { return promoter; }
    public void setPromoter(Promoter promoter) { this.promoter = promoter; }

    @Override
    public String toString() {
        return id + " | " + year + " | " + artist.getName()
                + " @ " + venue.getName()
                + " | $" + ticketPrice
                + " | sold: " + ticketsSold;
    }
}
