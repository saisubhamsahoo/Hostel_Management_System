package com.hostel.management.controller;

import com.hostel.management.model.RoomTransferRequest;
import com.hostel.management.repository.RoomTransferRequestRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.repository.RoomRepository;
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
@RequestMapping("/api/room-transfer-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomTransferRequestController {

    @Autowired
    private RoomTransferRequestRepository transferRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // ADMIN & WARDEN: View all transfer requests
    // STUDENT: View own requests (filter on frontend)
    @GetMapping
    public ResponseEntity<?> getAllTransferRequests() {
        try {
            List<RoomTransferRequest> requests = transferRequestRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transfer requests: " + e.getMessage());
        }
    }

    // ALL ROLES: View specific transfer request
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransferRequestById(@PathVariable Long id) {
        try {
            Optional<RoomTransferRequest> request = transferRequestRepository.findById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transfer request: " + e.getMessage());
        }
    }

    // STUDENT: View own transfer requests
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getTransferRequestsByStudent(@PathVariable Long studentId) {
        try {
            List<RoomTransferRequest> requests = transferRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transfer requests: " + e.getMessage());
        }
    }

    // STUDENT: Create new room transfer request
    @PostMapping
    public ResponseEntity<?> createTransferRequest(@RequestBody RoomTransferRequest transferRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            transferRequest.setCreatedAt(LocalDateTime.now());
            transferRequest.setStatus(RoomTransferRequest.Status.PENDING);
            RoomTransferRequest savedRequest = transferRequestRepository.save(transferRequest);

            response.put("success", true);
            response.put("message", "Room transfer request submitted successfully");
            response.put("transferRequest", savedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating transfer request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ADMIN & WARDEN: Approve/Reject room transfer request
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveTransferRequest(@PathVariable Long id, @RequestBody Map<String, String> approvalData) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<RoomTransferRequest> requestOpt = transferRequestRepository.findById(id);

            if (requestOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Transfer request not found");
                return ResponseEntity.notFound().build();
            }

            RoomTransferRequest transferRequest = requestOpt.get();

            String action = approvalData.get("action"); // "APPROVED" or "REJECTED"
            String approverName = approvalData.get("approverName");
            String remarks = approvalData.get("remarks");

            transferRequest.setStatus(RoomTransferRequest.Status.valueOf(action));
            transferRequest.setApproverName(approverName);
            transferRequest.setApprovedAt(LocalDateTime.now());

            if (remarks != null && !remarks.isEmpty()) {
                transferRequest.setRemarks(remarks);
            }

            // If approved, update user's room assignment
            if ("APPROVED".equals(action)) {
                userRepository.findById(transferRequest.getStudentId()).ifPresent(user -> {
                    user.setRoomId(transferRequest.getRequestedRoomId());
                    userRepository.save(user);
                });
            }

            RoomTransferRequest updatedRequest = transferRequestRepository.save(transferRequest);

            response.put("success", true);
            response.put("message", "Room transfer request " + action.toLowerCase());
            response.put("transferRequest", updatedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing transfer request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ADMIN & WARDEN: General update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransferRequest(@PathVariable Long id, @RequestBody RoomTransferRequest requestDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<RoomTransferRequest> requestOpt = transferRequestRepository.findById(id);

            if (requestOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Transfer request not found");
                return ResponseEntity.notFound().build();
            }

            RoomTransferRequest transferRequest = requestOpt.get();

            if (requestDetails.getStatus() != null) {
                transferRequest.setStatus(requestDetails.getStatus());
                if (requestDetails.getStatus() != RoomTransferRequest.Status.PENDING) {
                    transferRequest.setApprovedAt(LocalDateTime.now());
                }
            }

            if (requestDetails.getApproverName() != null) {
                transferRequest.setApproverName(requestDetails.getApproverName());
            }

            if (requestDetails.getRemarks() != null) {
                transferRequest.setRemarks(requestDetails.getRemarks());
            }

            RoomTransferRequest updatedRequest = transferRequestRepository.save(transferRequest);

            response.put("success", true);
            response.put("message", "Transfer request updated successfully");
            response.put("transferRequest", updatedRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating transfer request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ADMIN: Delete transfer request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransferRequest(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!transferRequestRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Transfer request not found");
                return ResponseEntity.notFound().build();
            }

            transferRequestRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Transfer request deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting transfer request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
