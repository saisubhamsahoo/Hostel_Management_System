package com.hostel.management.repository;

import com.hostel.management.model.RoomTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomTransferRequestRepository extends JpaRepository<RoomTransferRequest, Long> {
    List<RoomTransferRequest> findByStudentId(Long studentId);
    List<RoomTransferRequest> findByStatus(RoomTransferRequest.Status status);
    List<RoomTransferRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<RoomTransferRequest> findAllByOrderByCreatedAtDesc();
}
