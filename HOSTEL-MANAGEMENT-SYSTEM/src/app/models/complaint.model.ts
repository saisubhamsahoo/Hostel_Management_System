export interface Complaint {
  id?: number;
  studentId: number;
  studentName: string;
  title: string;
  description: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED' | 'REJECTED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  createdAt?: string;
  resolvedAt?: string;
  resolutionNotes?: string;
  resolvedBy?: string; 
}
