package com.hostel.management.controller;

import com.hostel.management.model.LeaveRequest;
import com.hostel.management.repository.LeaveRequestRepository;
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
@RequestMapping("/api/leave-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @GetMapping
    public ResponseEntity<?> getAllLeaveRequests() {
        try {
            List<LeaveRequest> requests = leaveRequestRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching leave requests: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Long id) {
        try {
            Optional<LeaveRequest> request = leaveRequestRepository.findById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching leave request: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getLeaveRequestsByStudent(@PathVariable Long studentId) {
        try {
            List<LeaveRequest> requests = leaveRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching leave requests: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            leaveRequest.setCreatedAt(LocalDateTime.now());
            leaveRequest.setStatus(LeaveRequest.Status.PENDING);
            LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

            response.put("success", true);
            response.put("message", "Leave request submitted successfully");
            response.put("leaveRequest", savedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating leave request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable Long id, @RequestBody Map<String, String> approvalData) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<LeaveRequest> requestOpt = leaveRequestRepository.findById(id);

            if (requestOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Leave request not found");
                return ResponseEntity.notFound().build();
            }

            LeaveRequest leaveRequest = requestOpt.get();

            String action = approvalData.get("action"); // "APPROVED" or "REJECTED"
            String approverName = approvalData.get("approverName");
            String remarks = approvalData.get("remarks");

            leaveRequest.setStatus(LeaveRequest.Status.valueOf(action));
            leaveRequest.setApproverName(approverName);
            leaveRequest.setApprovedAt(LocalDateTime.now());

            if (remarks != null && !remarks.isEmpty()) {
                leaveRequest.setRemarks(remarks);
            }

            LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);

            response.put("success", true);
            response.put("message", "Leave request " + action.toLowerCase());
            response.put("leaveRequest", updatedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing leave request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest requestDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<LeaveRequest> requestOpt = leaveRequestRepository.findById(id);

            if (requestOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Leave request not found");
                return ResponseEntity.notFound().build();
            }

            LeaveRequest leaveRequest = requestOpt.get();

            if (requestDetails.getStatus() != null) {
                leaveRequest.setStatus(requestDetails.getStatus());
                if (requestDetails.getStatus() != LeaveRequest.Status.PENDING) {
                    leaveRequest.setApprovedAt(LocalDateTime.now());
                }
            }

            if (requestDetails.getApproverName() != null) {
                leaveRequest.setApproverName(requestDetails.getApproverName());
            }

            if (requestDetails.getRemarks() != null) {
                leaveRequest.setRemarks(requestDetails.getRemarks());
            }

            LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);

            response.put("success", true);
            response.put("message", "Leave request updated successfully");
            response.put("leaveRequest", updatedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating leave request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!leaveRequestRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Leave request not found");
                return ResponseEntity.notFound().build();
            }

            leaveRequestRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Leave request deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting leave request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}