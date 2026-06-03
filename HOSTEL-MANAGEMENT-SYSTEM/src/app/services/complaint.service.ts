import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Complaint } from '../models/complaint.model';

@Injectable({
  providedIn: 'root'
})
export class ComplaintService {
  private apiUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  getAllComplaints(): Observable<Complaint[]> {
    return this.http.get<Complaint[]>(this.apiUrl);
  }

  getComplaintById(id: number): Observable<Complaint> {
    return this.http.get<Complaint>(`${this.apiUrl}/${id}`);
  }

  getComplaintsByStudent(studentId: number): Observable<Complaint[]> {
    return this.http.get<Complaint[]>(`${this.apiUrl}/student/${studentId}`);
  }

  createComplaint(complaint: Complaint): Observable<any> {
    return this.http.post(this.apiUrl, complaint);
  }

  updateComplaint(id: number, complaint: Complaint): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, complaint);
  }

  deleteComplaint(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
