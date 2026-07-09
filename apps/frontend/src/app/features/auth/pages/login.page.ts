import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '@core/auth/auth.service';
import { UiButtonComponent } from '@shared/components/ui-button/ui-button.component';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule, UiButtonComponent],
  template: `
    <div class="login-page">
      <div class="login-card">
        <h1 class="login-card__title">勤怠管理システム</h1>
        <p class="login-card__subtitle">Attendance Management System</p>

        <form (ngSubmit)="onLogin()" class="login-form">
          <div class="form-group">
            <label for="username">ユーザー名</label>
            <input
              id="username"
              type="text"
              [(ngModel)]="username"
              name="username"
              required
              placeholder="ユーザー名を入力"
            />
          </div>

          <div class="form-group">
            <label for="password">パスワード</label>
            <input
              id="password"
              type="password"
              [(ngModel)]="password"
              name="password"
              required
              placeholder="パスワードを入力"
            />
          </div>

          <div class="error-message" *ngIf="errorMessage">
            {{ errorMessage }}
          </div>

          <app-ui-button
            type="submit"
            variant="primary"
            [loading]="loading"
            [disabled]="!username || !password">
            ログイン
          </app-ui-button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--color-bg);
    }
    .login-card {
      width: 400px;
      padding: var(--space-8);
      background: var(--color-surface);
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow-md);
    }
    .login-card__title {
      font-size: var(--fs-2xl);
      font-weight: var(--fw-bold);
      text-align: center;
      color: var(--color-text);
    }
    .login-card__subtitle {
      text-align: center;
      color: var(--color-text-muted);
      margin-bottom: var(--space-6);
    }
    .form-group {
      margin-bottom: var(--space-4);
    }
    .form-group label {
      display: block;
      font-size: var(--fs-sm);
      font-weight: var(--fw-medium);
      color: var(--color-text);
      margin-bottom: var(--space-1);
    }
    .form-group input {
      width: 100%;
      height: 40px;
      padding: 0 var(--space-3);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      font-size: var(--fs-base);
    }
    .form-group input:focus {
      outline: none;
      border-color: var(--color-primary);
    }
    .error-message {
      color: var(--color-danger);
      font-size: var(--fs-sm);
      margin-bottom: var(--space-4);
    }
  `],
})
export class LoginPage {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = false;
  errorMessage = '';

  onLogin(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'ログインに失敗しました';
      },
    });
  }
}
