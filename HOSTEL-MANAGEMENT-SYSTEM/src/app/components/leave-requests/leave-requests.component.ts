import { Component, OnInit } from '@angular/core';
import { LeaveRequestService } from '../../services/leave-request.service';
import { AuthService } from '../../services/auth.service';
import { LeaveRequest } from '../../models/leave-request.model';

@Component({
  selector: 'app-leave-requests',
  templateUrl: './leave-requests.component.html',
  styleUrls: ['./leave-requests.component.css']
})
export class LeaveRequestsComponent implements OnInit {
  requests: LeaveRequest[] = [];
  currentUser: any;
  showForm = false;
  dateError: string = '';
  newRequest: LeaveRequest = { studentId: 0, studentName: '', fromDate: '', toDate: '', reason: '', status: 'PENDING' };

  // ✅ Computes today's date as YYYY-MM-DD for [min] binding on date inputs
  get minDate(): string {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }

  constructor(private leaveService: LeaveRequestService, private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadRequests();
  }

  loadRequests() {
    if (this.currentUser.role === 'STUDENT') {
      this.leaveService.getLeaveRequestsByStudent(this.currentUser.id).subscribe(data => this.requests = data);
    } else {
      this.leaveService.getAllLeaveRequests().subscribe(data => this.requests = data);
    }
  }

  isValidDateRange(): boolean {
    this.dateError = '';

    if (!this.newRequest.fromDate || !this.newRequest.toDate) {
      this.dateError = 'Both From Date and To Date are required.';
      return false;
    }

    // ✅ Normalize today to midnight for accurate day-level comparison
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const fromDate = new Date(this.newRequest.fromDate);
    const toDate = new Date(this.newRequest.toDate);

    // ✅ Block past From Date
    if (fromDate < today) {
      this.dateError = 'From Date cannot be a past date.';
      return false;
    }

    // ✅ Block past To Date
    if (toDate < today) {
      this.dateError = 'To Date cannot be a past date.';
      return false;
    }

    // ✅ Block same-day selection
    if (fromDate.getTime() === toDate.getTime()) {
      this.dateError = 'From Date and To Date cannot be the same.';
      return false;
    }

    // ✅ Block reversed date range
    if (toDate < fromDate) {
      this.dateError = 'To Date cannot be before From Date.';
      return false;
    }

    return true;
  }

  createRequest() {
    if (!this.isValidDateRange()) {
      return;
    }

    this.newRequest.studentId = this.currentUser.id;
    this.newRequest.studentName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    this.leaveService.createLeaveRequest(this.newRequest).subscribe(() => {
      this.loadRequests();
      this.showForm = false;
      this.newRequest = { studentId: 0, studentName: '', fromDate: '', toDate: '', reason: '', status: 'PENDING' };
      this.dateError = '';
      alert('Leave request submitted!');
    });
  }

  approveRequest(req: LeaveRequest) {
    const approvalData = {
      action: 'APPROVED',
      approverName: this.currentUser.firstName + ' ' + this.currentUser.lastName,
      remarks: 'Approved'
    };
    this.leaveService.updateLeaveRequest(req.id!, { ...req, status: 'APPROVED', approverName: approvalData.approverName }).subscribe({
      next: () => { alert('Approved!'); this.loadRequests(); },
      error: (err) => alert('Error approving')
    });
  }

  rejectRequest(req: LeaveRequest) {
    const remarks = prompt('Rejection reason (optional):');
    this.leaveService.updateLeaveRequest(req.id!, {
      ...req,
      status: 'REJECTED',
      approverName: this.currentUser.firstName + ' ' + this.currentUser.lastName,
      remarks: remarks || ''
    }).subscribe({
      next: () => { alert('Rejected!'); this.loadRequests(); },
      error: (err) => alert('Error rejecting')
    });
  }

  deleteRequest(id: number) {
    if (confirm('Delete this leave request?')) {
      this.leaveService.deleteLeaveRequest(id).subscribe(() => {
        alert('Deleted!');
        this.loadRequests();
      });
    }
  }

  canApprove(): boolean { return this.currentUser.role === 'ADMIN' || this.currentUser.role === 'WARDEN'; }
  canDelete(): boolean { return this.currentUser.role === 'ADMIN'; }
}