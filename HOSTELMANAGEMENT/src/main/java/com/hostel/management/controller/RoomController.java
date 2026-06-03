package com.hostel.management.controller;

import com.hostel.management.model.Room;
import com.hostel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching rooms: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
        try {
            Optional<Room> room = roomRepository.findById(id);
            if (room.isPresent()) {
                return ResponseEntity.ok(room.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching room: " + e.getMessage());
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRooms() {
        try {
            List<Room> rooms = roomRepository.findByAvailable(true);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching available rooms: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
                response.put("success", false);
                response.put("message", "Room number already exists");
                return ResponseEntity.badRequest().body(response);
            }

            Room savedRoom = roomRepository.save(room);

            response.put("success", true);
            response.put("message", "Room created successfully");
            response.put("room", savedRoom);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Room> roomOpt = roomRepository.findById(id);

            if (roomOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Room not found");
                return ResponseEntity.notFound().build();
            }

            Room room = roomOpt.get();
            room.setRoomNumber(roomDetails.getRoomNumber());
            room.setRoomType(roomDetails.getRoomType());
            room.setTotalBeds(roomDetails.getTotalBeds());
            room.setOccupiedBeds(roomDetails.getOccupiedBeds());
            room.setFloor(roomDetails.getFloor());
            room.setAvailable(roomDetails.getAvailable());

            Room updatedRoom = roomRepository.save(room);

            response.put("success", true);
            response.put("message", "Room updated successfully");
            response.put("room", updatedRoom);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!roomRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Room not found");
                return ResponseEntity.notFound().build();
            }

            roomRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Room deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}