import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="layout">
      <aside class="sidebar">
        <div class="sidebar__logo">
          <h1>勤怠管理</h1>
        </div>
        <nav class="sidebar__nav">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            <i class="pi pi-home"></i>
            <span>ダッシュボード</span>
          </a>
          <a routerLink="/employees" routerLinkActive="active" class="nav-item">
            <i class="pi pi-users"></i>
            <span>社員一覧</span>
          </a>
          <a routerLink="/attendance" routerLinkActive="active" class="nav-item">
            <i class="pi pi-clock"></i>
            <span>勤怠管理</span>
          </a>
          <a routerLink="/leaves" routerLinkActive="active" class="nav-item">
            <i class="pi pi-calendar"></i>
            <span>有給管理</span>
          </a>
          <a routerLink="/payroll" routerLinkActive="active" class="nav-item">
            <i class="pi pi-wallet"></i>
            <span>給与</span>
          </a>
          <a routerLink="/reports" routerLinkActive="active" class="nav-item">
            <i class="pi pi-chart-bar"></i>
            <span>レポート</span>
          </a>
        </nav>
      </aside>
      <div class="main">
        <header class="header">
          <div class="header__left">
            <span class="header__breadcrumb">ホーム</span>
          </div>
          <div class="header__right">
            <span class="header__user">{{ authService.getRole() }}</span>
            <button class="header__logout" (click)="authService.logout()">
              <i class="pi pi-sign-out"></i>
              ログアウト
            </button>
          </div>
        </header>
        <main class="content">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .layout {
      display: flex;
      min-height: 100vh;
    }
    .sidebar {
      width: 240px;
      background: var(--color-surface);
      border-right: 1px solid var(--color-border);
      display: flex;
      flex-direction: column;
    }
    .sidebar__logo {
      height: 56px;
      display: flex;
      align-items: center;
      padding: 0 var(--space-4);
      border-bottom: 1px solid var(--color-border);
    }
    .sidebar__logo h1 {
      font-size: var(--fs-lg);
      font-weight: var(--fw-bold);
      color: var(--color-text);
    }
    .sidebar__nav {
      padding: var(--space-2) 0;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      padding: var(--space-3) var(--space-4);
      color: var(--color-text-muted);
      text-decoration: none;
      transition: background-color 0.15s;
    }
    .nav-item:hover {
      background: var(--color-primary-weak);
    }
    .nav-item.active {
      color: var(--color-primary);
      background: var(--color-primary-weak);
    }
    .main {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .header {
      height: 56px;
      background: var(--color-surface);
      border-bottom: 1px solid var(--color-border);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 var(--space-5);
    }
    .header__right {
      display: flex;
      align-items: center;
      gap: var(--space-4);
    }
    .header__logout {
      background: none;
      border: none;
      cursor: pointer;
      color: var(--color-text-muted);
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }
    .content {
      flex: 1;
      padding: var(--space-5);
      background: var(--color-bg);
    }
  `],
})
export class MainLayoutComponent {
  authService = inject(AuthService);
}
