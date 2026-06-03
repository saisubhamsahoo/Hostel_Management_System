package com.hostel.management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_transfer_requests")
public class RoomTransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private Long currentRoomId;

    @Column(nullable = false)
    private String currentRoomNumber;

    @Column(nullable = false)
    private Long requestedRoomId;

    @Column(nullable = false)
    private String requestedRoomNumber;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String approverName; 
    private LocalDateTime approvedAt;
    private String remarks;

    public enum Status { PENDING, APPROVED, REJECTED }

    public RoomTransferRequest() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Long getCurrentRoomId() {
		return currentRoomId;
	}

	public void setCurrentRoomId(Long currentRoomId) {
		this.currentRoomId = currentRoomId;
	}

	public String getCurrentRoomNumber() {
		return currentRoomNumber;
	}

	public void setCurrentRoomNumber(String currentRoomNumber) {
		this.currentRoomNumber = currentRoomNumber;
	}

	public Long getRequestedRoomId() {
		return requestedRoomId;
	}

	public void setRequestedRoomId(Long requestedRoomId) {
		this.requestedRoomId = requestedRoomId;
	}

	public String getRequestedRoomNumber() {
		return requestedRoomNumber;
	}

	public void setRequestedRoomNumber(String requestedRoomNumber) {
		this.requestedRoomNumber = requestedRoomNumber;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}


