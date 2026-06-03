package com.hostel.management.controller;

import com.hostel.management.model.MaintenanceRequest;
import com.hostel.management.repository.MaintenanceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/maintenance-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class MaintenanceRequestController {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @GetMapping
    public ResponseEntity<?> getAllMaintenanceRequests() {
        try {
            List<MaintenanceRequest> requests = maintenanceRequestRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching maintenance requests: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMaintenanceRequestById(@PathVariable Long id) {
        try {
            Optional<MaintenanceRequest> request = maintenanceRequestRepository.findById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching maintenance request: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getMaintenanceRequestsByStudent(@PathVariable Long studentId) {
        try {
            List<MaintenanceRequest> requests = maintenanceRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching maintenance requests: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createMaintenanceRequest(@RequestBody MaintenanceRequest maintenanceRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            maintenanceRequest.setCreatedAt(LocalDateTime.now());
            maintenanceRequest.setStatus(MaintenanceRequest.Status.PENDING);
            MaintenanceRequest savedRequest = maintenanceRequestRepository.save(maintenanceRequest);

            response.put("success", true);
            response.put("message", "Maintenance request submitted successfully");
            response.put("maintenanceRequest", savedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating maintenance request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaintenanceRequest(@PathVariable Long id, @RequestBody MaintenanceRequest requestDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<MaintenanceRequest> requestOpt = maintenanceRequestRepository.findById(id);

            if (requestOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Maintenance request not found");
                return ResponseEntity.notFound().build();
            }

            MaintenanceRequest maintenanceRequest = requestOpt.get();

            if (requestDetails.getStatus() != null) {
                maintenanceRequest.setStatus(requestDetails.getStatus());
            }

            if (requestDetails.getPriority() != null) {
                maintenanceRequest.setPriority(requestDetails.getPriority());
            }

            if (requestDetails.getStatus() == MaintenanceRequest.Status.RESOLVED) {
                maintenanceRequest.setResolvedAt(LocalDateTime.now());
                if (requestDetails.getResolvedBy() != null) {
                    maintenanceRequest.setResolvedBy(requestDetails.getResolvedBy());
                }
            }

            if (requestDetails.getResolutionNotes() != null) {
                maintenanceRequest.setResolutionNotes(requestDetails.getResolutionNotes());
            }

            MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(maintenanceRequest);

            response.put("success", true);
            response.put("message", "Maintenance request updated successfully");
            response.put("maintenanceRequest", updatedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating maintenance request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaintenanceRequest(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!maintenanceRequestRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Maintenance request not found");
                return ResponseEntity.notFound().build();
            }

            maintenanceRequestRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Maintenance request deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting maintenance request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
