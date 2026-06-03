import { Component, OnInit } from '@angular/core';
import { MaintenanceService } from '../../services/maintenance.service';
import { AuthService } from '../../services/auth.service';
import { MaintenanceRequest } from '../../models/maintenance-request.model';

@Component({
  selector: 'app-maintenance',
  templateUrl: './maintenance.component.html',
  styleUrls: ['./maintenance.component.css']
})
export class MaintenanceComponent implements OnInit {
  requests: MaintenanceRequest[] = [];
  currentUser: any;
  showForm = false;
  newRequest: MaintenanceRequest = {
    studentId: 0, 
    studentName: '', 
    roomId: 0, 
    roomNumber: '',
    issueType: '', 
    description: '', 
    status: 'PENDING', 
    priority: 'MEDIUM'
  };

  constructor(
    private maintenanceService: MaintenanceService, 
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadRequests();
  }

  loadRequests() {
    if (this.currentUser.role === 'STUDENT') {
      this.maintenanceService.getMaintenanceRequestsByStudent(this.currentUser.id)
        .subscribe(data => this.requests = data);
    } else {
      this.maintenanceService.getAllMaintenanceRequests()
        .subscribe(data => this.requests = data);
    }
  }

  createRequest() {
    this.newRequest.studentId = this.currentUser.id;
    this.newRequest.studentName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    this.newRequest.roomId = this.currentUser.roomId || 0;
    
    this.maintenanceService.createMaintenanceRequest(this.newRequest).subscribe({
      next: () => { 
        this.loadRequests(); 
        this.showForm = false; 
        this.newRequest = {
          studentId: 0, 
          studentName: '', 
          roomId: 0, 
          roomNumber: '',
          issueType: '', 
          description: '', 
          status: 'PENDING', 
          priority: 'MEDIUM'
        };
        alert('Maintenance request submitted successfully!'); 
      },
      error: (err) => {
        console.error('Error creating maintenance request:', err);
        alert('Error submitting request');
      }
    });
  }

  updateStatus(req: MaintenanceRequest, status: string) {
    const update: MaintenanceRequest = { 
      ...req, 
      status: status as any, 
      resolvedBy: this.currentUser.firstName + ' ' + this.currentUser.lastName,
      resolvedAt: status === 'RESOLVED' ? new Date().toISOString() : req.resolvedAt
    };
    
    this.maintenanceService.updateMaintenanceRequest(req.id!, update).subscribe({
      next: () => { 
        alert('Status updated successfully!'); 
        this.loadRequests(); 
      },
      error: (err) => {
        console.error('Error updating status:', err);
        alert('Error updating status');
      }
    });
  }

  updatePriority(req: MaintenanceRequest, priority: string) {
    const update: MaintenanceRequest = { 
      ...req, 
      priority: priority as any 
    };
    
    this.maintenanceService.updateMaintenanceRequest(req.id!, update).subscribe({
      next: () => { 
        alert('Priority updated successfully!'); 
        this.loadRequests(); 
      },
      error: (err) => {
        console.error('Error updating priority:', err);
        alert('Error updating priority');
      }
    });
  }

  deleteRequest(id: number) {
    if(confirm('Are you sure you want to delete this maintenance request?')) {
      this.maintenanceService.deleteMaintenanceRequest(id).subscribe({
        next: () => { 
          alert('Maintenance request deleted successfully!'); 
          this.loadRequests(); 
        },
        error: (err) => {
          console.error('Error deleting maintenance request:', err);
          alert('Error deleting request');
        }
      });
    }
  }

  canManage(): boolean { 
    return this.currentUser.role === 'ADMIN' || this.currentUser.role === 'WARDEN'; 
  }
  
  canDelete(): boolean { 
    return this.currentUser.role === 'ADMIN'; 
  }
}