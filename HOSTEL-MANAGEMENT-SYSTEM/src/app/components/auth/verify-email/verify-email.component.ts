import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {
  message = '';
  success = false;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.authService.verifyEmail(token).subscribe({
        next: (response) => {
          this.loading = false;
          this.success = response.success;
          this.message = response.message;

          if (response.success) {
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 3000);
          }
        },
        error: (error) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.message || 'Verification failed.';
        }
      });
    } else {
      this.loading = false;
      this.success = false;
      this.message = 'Invalid verification token.';
    }
  }
}
