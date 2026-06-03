export interface MaintenanceRequest {
  id?: number;
  studentId: number;
  studentName: string;
  roomId: number;
  roomNumber: string;
  issueType: string;
  description: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED' | 'REJECTED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  createdAt?: string;
  resolvedAt?: string;
  resolutionNotes?: string;
  resolvedBy?: string;  
}
