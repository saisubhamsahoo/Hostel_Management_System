import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoomTransferRequest } from '../models/room-transfer-request.model';

@Injectable({
  providedIn: 'root'
})
export class RoomTransferService {
  private apiUrl = 'http://localhost:8080/api/room-transfer-requests';

  constructor(private http: HttpClient) {}

  getAllTransferRequests(): Observable<RoomTransferRequest[]> {
    return this.http.get<RoomTransferRequest[]>(this.apiUrl);
  }

  getTransferRequestById(id: number): Observable<RoomTransferRequest> {
    return this.http.get<RoomTransferRequest>(`${this.apiUrl}/${id}`);
  }

  getTransferRequestsByStudent(studentId: number): Observable<RoomTransferRequest[]> {
    return this.http.get<RoomTransferRequest[]>(`${this.apiUrl}/student/${studentId}`);
  }

  createTransferRequest(request: RoomTransferRequest): Observable<any> {
    return this.http.post(this.apiUrl, request);
  }

  updateTransferRequest(id: number, request: RoomTransferRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, request);
  }

  deleteTransferRequest(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
