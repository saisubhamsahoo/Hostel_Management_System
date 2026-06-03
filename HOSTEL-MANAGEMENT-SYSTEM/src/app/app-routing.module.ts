import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingComponent } from './components/landing/landing.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { VerifyEmailComponent } from './components/auth/verify-email/verify-email.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RoomsComponent } from './components/rooms/rooms.component';
import { StudentsComponent } from './components/students/students.component';
import { ComplaintsComponent } from './components/complaints/complaints.component';
import { LeaveRequestsComponent } from './components/leave-requests/leave-requests.component';
import { RoomTransfersComponent } from './components/room-transfers/room-transfers.component';
import { MaintenanceComponent } from './components/maintenance/maintenance.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'verify-email', component: VerifyEmailComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'rooms', component: RoomsComponent, canActivate: [AuthGuard] },
  { path: 'students', component: StudentsComponent, canActivate: [AuthGuard] },
  { path: 'complaints', component: ComplaintsComponent, canActivate: [AuthGuard] },
  { path: 'leave-requests', component: LeaveRequestsComponent, canActivate: [AuthGuard] },
  { path: 'room-transfers', component: RoomTransfersComponent, canActivate: [AuthGuard] },
  { path: 'maintenance', component: MaintenanceComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
