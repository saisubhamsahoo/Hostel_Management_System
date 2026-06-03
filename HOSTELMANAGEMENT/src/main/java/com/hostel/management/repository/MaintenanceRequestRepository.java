package com.hostel.management.repository;

import com.hostel.management.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStudentId(Long studentId);
    List<MaintenanceRequest> findByRoomId(Long roomId);
    List<MaintenanceRequest> findByStatus(MaintenanceRequest.Status status);
    List<MaintenanceRequest> findByPriority(MaintenanceRequest.Priority priority);
    List<MaintenanceRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<MaintenanceRequest> findAllByOrderByCreatedAtDesc();
}
