import { Component } from '@angular/core';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';
import { UiButtonComponent } from '@shared/components/ui-button/ui-button.component';

@Component({
  selector: 'app-attendance-page',
  standalone: true,
  imports: [UiPageHeaderComponent, UiCardComponent, UiButtonComponent],
  template: `
    <app-ui-page-header title="勤怠管理" subtitle="Attendance Management">
      <div class="action-buttons">
        <app-ui-button variant="primary" (clicked)="onCheckIn()">
          <i class="pi pi-play"></i> 出勤
        </app-ui-button>
        <app-ui-button variant="danger" (clicked)="onCheckOut()">
          <i class="pi pi-stop"></i> 退勤
        </app-ui-button>
      </div>
    </app-ui-page-header>

    <app-ui-card title="今日の打刻" [padded]="true">
      <div class="today-info">
        <div class="info-item">
          <span class="info-label">出勤時間</span>
          <span class="info-value">--:--</span>
        </div>
        <div class="info-item">
          <span class="info-label">退勤時間</span>
          <span class="info-value">--:--</span>
        </div>
        <div class="info-item">
          <span class="info-label">休憩時間</span>
          <span class="info-value">60分</span>
        </div>
        <div class="info-item">
          <span class="info-label">勤務時間</span>
          <span class="info-value">--時間--分</span>
        </div>
      </div>
    </app-ui-card>

    <app-ui-card title="勤怠履歴" [padded]="true">
      <p class="placeholder-text">月別の打刻履歴テーブル（PrimeNG Table で実装予定）</p>
    </app-ui-card>
  `,
  styles: [`
    .action-buttons {
      display: flex;
      gap: var(--space-2);
    }
    .today-info {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: var(--space-4);
    }
    .info-item {
      display: flex;
      flex-direction: column;
      gap: var(--space-1);
    }
    .info-label {
      font-size: var(--fs-sm);
      color: var(--color-text-muted);
    }
    .info-value {
      font-size: var(--fs-xl);
      font-weight: var(--fw-bold);
      color: var(--color-text);
    }
    .placeholder-text {
      color: var(--color-text-muted);
      text-align: center;
      padding: var(--space-8);
    }
  `],
})
export class AttendancePage {
  onCheckIn(): void {
    console.log('Check in');
  }

  onCheckOut(): void {
    console.log('Check out');
  }
}
