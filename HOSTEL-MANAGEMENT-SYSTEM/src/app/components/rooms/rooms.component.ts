import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/room.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  loading = true;
  newRoom: Room = { roomNumber: '', roomType: 'SINGLE', totalBeds: 1, occupiedBeds: 0, floor: '', available: true };
  showForm = false;

  constructor(private roomService: RoomService) {}

  ngOnInit() {
    this.loadRooms();
  }

  loadRooms() {
    this.roomService.getAllRooms().subscribe({
      next: (data) => { this.rooms = data; this.loading = false; },
      error: (error) => { console.error(error); this.loading = false; }
    });
  }

  createRoom() {
    this.roomService.createRoom(this.newRoom).subscribe({
      next: () => { this.loadRooms(); this.showForm = false; this.resetForm(); alert('Room created successfully!'); },
      error: (error) => alert('Error creating room')
    });
  }

  deleteRoom(id: number) {
    if(confirm('Delete this room?')) {
      this.roomService.deleteRoom(id).subscribe({
        next: () => { this.loadRooms(); alert('Room deleted!'); },
        error: (error) => alert('Error deleting room')
      });
    }
  }

  resetForm() {
    this.newRoom = { roomNumber: '', roomType: 'SINGLE', totalBeds: 1, occupiedBeds: 0, floor: '', available: true };
  }
}
