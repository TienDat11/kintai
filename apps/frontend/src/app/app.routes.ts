import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login.page').then((m) => m.LoginPage),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/main-layout.component').then(
        (m) => m.MainLayoutComponent
      ),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard.page').then(
            (m) => m.DashboardPage
          ),
      },
      {
        path: 'employees',
        loadComponent: () =>
          import('./features/employee/pages/employee-list.page').then(
            (m) => m.EmployeeListPage
          ),
      },
      {
        path: 'attendance',
        loadComponent: () =>
          import('./features/attendance/pages/attendance.page').then(
            (m) => m.AttendancePage
          ),
      },
      {
        path: 'leaves',
        loadComponent: () =>
          import('./features/leave/pages/leave.page').then(
            (m) => m.LeavePage
          ),
      },
      {
        path: 'payroll',
        loadComponent: () =>
          import('./features/payroll/pages/payroll.page').then(
            (m) => m.PayrollPage
          ),
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./features/report/pages/report.page').then(
            (m) => m.ReportPage
          ),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '' },
];
