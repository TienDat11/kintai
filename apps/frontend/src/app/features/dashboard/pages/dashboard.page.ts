import { Component } from '@angular/core';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [UiPageHeaderComponent, UiCardComponent],
  template: `
    <app-ui-page-header title="ダッシュボード" subtitle="Dashboard"></app-ui-page-header>

    <div class="kpi-grid">
      <app-ui-card title="出勤率" [padded]="true">
        <div class="kpi-value">95.2%</div>
      </app-ui-card>
      <app-ui-card title="残業時間計" [padded]="true">
        <div class="kpi-value">120時間</div>
      </app-ui-card>
      <app-ui-card title="申請待ち" [padded]="true">
        <div class="kpi-value">5件</div>
      </app-ui-card>
      <app-ui-card title="欠勤" [padded]="true">
        <div class="kpi-value">2件</div>
      </app-ui-card>
    </div>

    <div class="chart-grid">
      <app-ui-card title="勤務時間" [padded]="true">
        <div class="chart-placeholder">Chart.js で実装予定</div>
      </app-ui-card>
      <app-ui-card title="ステータス比率" [padded]="true">
        <div class="chart-placeholder">Chart.js で実装予定</div>
      </app-ui-card>
    </div>
  `,
  styles: [`
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: var(--space-4);
      margin-bottom: var(--space-5);
    }
    .kpi-value {
      font-size: var(--fs-3xl);
      font-weight: var(--fw-bold);
      color: var(--color-primary);
    }
    .chart-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: var(--space-4);
    }
    .chart-placeholder {
      height: 200px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--color-text-muted);
      background: var(--color-bg);
      border-radius: var(--radius-md);
    }
  `],
})
export class DashboardPage {}
