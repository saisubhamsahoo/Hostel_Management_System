import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MaintenanceRequest } from '../models/maintenance-request.model';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {
  private apiUrl = 'http://localhost:8080/api/maintenance-requests';

  constructor(private http: HttpClient) {}

  getAllMaintenanceRequests(): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(this.apiUrl);
  }

  getMaintenanceRequestById(id: number): Observable<MaintenanceRequest> {
    return this.http.get<MaintenanceRequest>(`${this.apiUrl}/${id}`);
  }

  getMaintenanceRequestsByStudent(studentId: number): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(`${this.apiUrl}/student/${studentId}`);
  }

  createMaintenanceRequest(request: MaintenanceRequest): Observable<any> {
    return this.http.post(this.apiUrl, request);
  }

  updateMaintenanceRequest(id: number, request: MaintenanceRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, request);
  }

  deleteMaintenanceRequest(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
