export interface LeaveRequest {
  id?: number;
  studentId: number;
  studentName: string;
  fromDate: string;
  toDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt?: string;
  approverName?: string;
  approvedAt?: string;
  remarks?: string;
}
