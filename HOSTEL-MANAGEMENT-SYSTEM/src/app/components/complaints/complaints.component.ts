import { Component, OnInit } from '@angular/core';
import { ComplaintService } from '../../services/complaint.service';
import { AuthService } from '../../services/auth.service';
import { Complaint } from '../../models/complaint.model';

@Component({
  selector: 'app-complaints',
  templateUrl: './complaints.component.html',
  styleUrls: ['./complaints.component.css']
})
export class ComplaintsComponent implements OnInit {
  complaints: Complaint[] = [];
  currentUser: any;
  showForm = false;
  newComplaint: Complaint = { 
    studentId: 0, 
    studentName: '', 
    title: '', 
    description: '', 
    status: 'PENDING', 
    priority: 'MEDIUM' 
  };

  constructor(
    private complaintService: ComplaintService, 
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadComplaints();
  }

  loadComplaints() {
    if (this.currentUser.role === 'STUDENT') {
      this.complaintService.getComplaintsByStudent(this.currentUser.id)
        .subscribe(data => this.complaints = data);
    } else {
      this.complaintService.getAllComplaints()
        .subscribe(data => this.complaints = data);
    }
  }

  createComplaint() {
    this.newComplaint.studentId = this.currentUser.id;
    this.newComplaint.studentName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    
    this.complaintService.createComplaint(this.newComplaint).subscribe({
      next: () => {
        this.loadComplaints();
        this.showForm = false;
        this.newComplaint = { 
          studentId: 0, 
          studentName: '', 
          title: '', 
          description: '', 
          status: 'PENDING', 
          priority: 'MEDIUM' 
        };
        alert('Complaint submitted successfully!');
      },
      error: (err) => {
        console.error('Error creating complaint:', err);
        alert('Error submitting complaint');
      }
    });
  }

  updateStatus(complaint: Complaint, status: string) {
    const update: Complaint = { 
      ...complaint, 
      status: status as any,
      resolvedBy: this.currentUser.firstName + ' ' + this.currentUser.lastName,
      resolvedAt: status === 'RESOLVED' ? new Date().toISOString() : complaint.resolvedAt
    };
    
    this.complaintService.updateComplaint(complaint.id!, update).subscribe({
      next: () => { 
        alert('Status updated successfully!'); 
        this.loadComplaints(); 
      },
      error: (err) => {
        console.error('Error updating status:', err);
        alert('Error updating status');
      }
    });
  }

  updatePriority(complaint: Complaint, priority: string) {
    const update: Complaint = { 
      ...complaint, 
      priority: priority as any 
    };
    
    this.complaintService.updateComplaint(complaint.id!, update).subscribe({
      next: () => { 
        alert('Priority updated successfully!'); 
        this.loadComplaints(); 
      },
      error: (err) => {
        console.error('Error updating priority:', err);
        alert('Error updating priority');
      }
    });
  }

  deleteComplaint(id: number) {
    if(confirm('Are you sure you want to delete this complaint?')) {
      this.complaintService.deleteComplaint(id).subscribe({
        next: () => { 
          alert('Complaint deleted successfully!'); 
          this.loadComplaints(); 
        },
        error: (err) => {
          console.error('Error deleting complaint:', err);
          alert('Error deleting complaint');
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