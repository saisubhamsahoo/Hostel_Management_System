package com.hostel.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private Integer totalBeds;

    @Column(nullable = false)
    private Integer occupiedBeds = 0;

    @Column(nullable = false)
    private String floor;

    @Column(nullable = false)
    private Boolean available = true;

    public enum RoomType {
        SINGLE, DOUBLE, TRIPLE, FOUR_SHARING
    }

    public Room() {}

    public Room(String roomNumber, RoomType roomType, Integer totalBeds, String floor) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.totalBeds = totalBeds;
        this.floor = floor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }

    public Integer getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(Integer occupiedBeds) { this.occupiedBeds = occupiedBeds; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getAvailableBeds() { return totalBeds - occupiedBeds; }
}
