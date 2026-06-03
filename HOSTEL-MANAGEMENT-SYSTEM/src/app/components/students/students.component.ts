import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit {
  students: User[] = [];
  loading = true;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.userService.getUsersByRole('STUDENT').subscribe({
      next: (data) => { this.students = data; this.loading = false; },
      error: (error) => { console.error(error); this.loading = false; }
    });
  }

  deleteStudent(id: number) {
    if(confirm('Delete this student?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => { this.loadStudents(); alert('Student deleted!'); },
        error: (error) => alert('Error deleting student')
      });
    }
  }
}
