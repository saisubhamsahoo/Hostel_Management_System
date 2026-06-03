package com.hostel.management.controller;

import com.hostel.management.model.*;
import com.hostel.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private RoomTransferRequestRepository transferRequestRepository;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @GetMapping("/statistics")
    public ResponseEntity<?> getDashboardStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalStudents", userRepository.findByRole(User.Role.STUDENT).size());
            stats.put("totalWardens", userRepository.findByRole(User.Role.WARDEN).size());
            stats.put("totalAdmins", userRepository.findByRole(User.Role.ADMIN).size());
            stats.put("totalUsers", userRepository.count());

            stats.put("totalRooms", roomRepository.count());
            stats.put("availableRooms", roomRepository.findByAvailable(true).size());
            stats.put("occupiedRooms", roomRepository.findByAvailable(false).size());

            long totalBeds = roomRepository.findAll().stream()
                    .mapToInt(Room::getTotalBeds)
                    .sum();
            long occupiedBeds = roomRepository.findAll().stream()
                    .mapToInt(Room::getOccupiedBeds)
                    .sum();
            stats.put("totalBeds", totalBeds);
            stats.put("occupiedBeds", occupiedBeds);
            stats.put("availableBeds", totalBeds - occupiedBeds);

            stats.put("totalComplaints", complaintRepository.count());
            stats.put("pendingComplaints", complaintRepository.findByStatus(Complaint.Status.PENDING).size());
            stats.put("resolvedComplaints", complaintRepository.findByStatus(Complaint.Status.RESOLVED).size());

            stats.put("totalLeaveRequests", leaveRequestRepository.count());
            stats.put("pendingLeaveRequests", leaveRequestRepository.findByStatus(LeaveRequest.Status.PENDING).size());
            stats.put("approvedLeaveRequests", leaveRequestRepository.findByStatus(LeaveRequest.Status.APPROVED).size());

            stats.put("totalTransferRequests", transferRequestRepository.count());
            stats.put("pendingTransferRequests", transferRequestRepository.findByStatus(RoomTransferRequest.Status.PENDING).size());

            stats.put("totalMaintenanceRequests", maintenanceRequestRepository.count());
            stats.put("pendingMaintenanceRequests", maintenanceRequestRepository.findByStatus(MaintenanceRequest.Status.PENDING).size());
            stats.put("resolvedMaintenanceRequests", maintenanceRequestRepository.findByStatus(MaintenanceRequest.Status.RESOLVED).size());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching dashboard statistics: " + e.getMessage());
        }
    }
}