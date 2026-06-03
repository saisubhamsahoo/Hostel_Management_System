package com.hostel.management.controller;

import com.hostel.management.model.Complaint;
import com.hostel.management.repository.ComplaintRepository;
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
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping
    public ResponseEntity<?> getAllComplaints() {
        try {
            List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching complaints: " + e.getMessage());
        }
    }
  
    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        try {
            Optional<Complaint> complaint = complaintRepository.findById(id);
            if (complaint.isPresent()) {
                return ResponseEntity.ok(complaint.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching complaint: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getComplaintsByStudent(@PathVariable Long studentId) {
        try {
            List<Complaint> complaints = complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching complaints: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody Complaint complaint) {
        Map<String, Object> response = new HashMap<>();

        try {
            complaint.setCreatedAt(LocalDateTime.now());
            complaint.setStatus(Complaint.Status.PENDING);
            Complaint savedComplaint = complaintRepository.save(complaint);

            response.put("success", true);
            response.put("message", "Complaint submitted successfully");
            response.put("complaint", savedComplaint);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating complaint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComplaint(@PathVariable Long id, @RequestBody Complaint complaintDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Complaint> complaintOpt = complaintRepository.findById(id);

            if (complaintOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Complaint not found");
                return ResponseEntity.notFound().build();
            }

            Complaint complaint = complaintOpt.get();

            if (complaintDetails.getStatus() != null) {
                complaint.setStatus(complaintDetails.getStatus());
            }

            if (complaintDetails.getPriority() != null) {
                complaint.setPriority(complaintDetails.getPriority());
            }

            if (complaintDetails.getStatus() == Complaint.Status.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
                if (complaintDetails.getResolvedBy() != null) {
                    complaint.setResolvedBy(complaintDetails.getResolvedBy());
                }
            }

            if (complaintDetails.getResolutionNotes() != null) {
                complaint.setResolutionNotes(complaintDetails.getResolutionNotes());
            }

            Complaint updatedComplaint = complaintRepository.save(complaint);

            response.put("success", true);
            response.put("message", "Complaint updated successfully");
            response.put("complaint", updatedComplaint);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating complaint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!complaintRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Complaint not found");
                return ResponseEntity.notFound().build();
            }

            complaintRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Complaint deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting complaint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
