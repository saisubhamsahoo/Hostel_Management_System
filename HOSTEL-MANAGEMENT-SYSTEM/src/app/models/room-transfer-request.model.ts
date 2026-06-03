export interface RoomTransferRequest {
  id?: number;
  studentId: number;
  studentName: string;
  currentRoomId: number;
  currentRoomNumber: string;
  requestedRoomId: number;
  requestedRoomNumber: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt?: string;
  approverName?: string;
  approvedAt?: string;
  remarks?: string;
}
