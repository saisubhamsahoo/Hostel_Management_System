import { Component, OnInit } from '@angular/core';
import { RoomTransferService } from '../../services/room-transfer.service';
import { RoomService } from '../../services/room.service';
import { AuthService } from '../../services/auth.service';
import { RoomTransferRequest } from '../../models/room-transfer-request.model';
import { Room } from '../../models/room.model';

@Component({
  selector: 'app-room-transfers',
  templateUrl: './room-transfers.component.html',
  styleUrls: ['./room-transfers.component.css']
})
export class RoomTransfersComponent implements OnInit {
  requests: RoomTransferRequest[] = [];
  availableRooms: Room[] = [];
  currentUser: any;
  showForm = false;
  newRequest: RoomTransferRequest = { 
    studentId: 0, studentName: '', currentRoomId: 0, currentRoomNumber: '',
    requestedRoomId: 0, requestedRoomNumber: '', reason: '', status: 'PENDING'
  };

  constructor(
    private transferService: RoomTransferService, 
    private roomService: RoomService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadRequests();
    this.loadAvailableRooms();
  }

  loadRequests() {
    if (this.currentUser.role === 'STUDENT') {
      this.transferService.getTransferRequestsByStudent(this.currentUser.id).subscribe(data => this.requests = data);
    } else {
      this.transferService.getAllTransferRequests().subscribe(data => this.requests = data);
    }
  }

  loadAvailableRooms() {
    this.roomService.getAvailableRooms().subscribe(data => this.availableRooms = data);
  }

  createRequest() {
    this.newRequest.studentId = this.currentUser.id;
    this.newRequest.studentName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    
    const selectedRoom = this.availableRooms.find(r => r.id === this.newRequest.requestedRoomId);
    if (selectedRoom) {
      this.newRequest.requestedRoomNumber = selectedRoom.roomNumber;
    }

    this.transferService.createTransferRequest(this.newRequest).subscribe({
      next: () => {
        this.loadRequests();
        this.showForm = false;
        alert('Transfer request submitted!');
      },
      error: (err) => alert('Error creating request: ' + err.message)
    });
  }

  approveRequest(req: RoomTransferRequest) {
    if(confirm('Approve this room transfer?')) {
      const update: RoomTransferRequest = {
        ...req,
        status: 'APPROVED' as any,
        approverName: this.currentUser.firstName + ' ' + this.currentUser.lastName
      };
      this.transferService.updateTransferRequest(req.id!, update).subscribe({
        next: () => { alert('Approved and room assigned!'); this.loadRequests(); },
        error: (err) => alert('Error approving')
      });
    }
  }

  rejectRequest(req: RoomTransferRequest) {
    const remarks = prompt('Rejection reason:');
    if(remarks) {
      const update: RoomTransferRequest = {
        ...req,
        status: 'REJECTED' as any,
        approverName: this.currentUser.firstName + ' ' + this.currentUser.lastName,
        remarks: remarks
      };
      this.transferService.updateTransferRequest(req.id!, update).subscribe({
        next: () => { alert('Rejected!'); this.loadRequests(); },
        error: (err) => alert('Error rejecting')
      });
    }
  }

  deleteRequest(id: number) {
    if(confirm('Delete?')) {
      this.transferService.deleteTransferRequest(id).subscribe(() => { alert('Deleted!'); this.loadRequests(); });
    }
  }

  canApprove(): boolean { return this.currentUser.role === 'ADMIN' || this.currentUser.role === 'WARDEN'; }
  canDelete(): boolean { return this.currentUser.role === 'ADMIN'; }
}