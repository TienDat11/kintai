import { Component } from '@angular/core';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';
import { UiButtonComponent } from '@shared/components/ui-button/ui-button.component';
import { UiStatusBadgeComponent } from '@shared/components/ui-status-badge/ui-status-badge.component';

@Component({
  selector: 'app-leave-page',
  standalone: true,
  imports: [UiPageHeaderComponent, UiCardComponent, UiButtonComponent, UiStatusBadgeComponent],
  template: `
    <app-ui-page-header title="有給管理" subtitle="Leave Management">
      <app-ui-button variant="primary" (clicked)="onApply()">
        <i class="pi pi-plus"></i> 有給申請
      </app-ui-button>
    </app-ui-page-header>

    <app-ui-card title="有給残数" [padded]="true">
      <div class="leave-balance">
        <div class="balance-item">
          <span class="balance-label">年次有給休暇</span>
          <span class="balance-value">10日</span>
        </div>
        <div class="balance-item">
          <span class="balance-label">使用済み</span>
          <span class="balance-value">3日</span>
        </div>
        <div class="balance-item">
          <span class="balance-label">残り</span>
          <span class="balance-value balance-value--highlight">7日</span>
        </div>
      </div>
    </app-ui-card>

    <app-ui-card title="申請履歴" [padded]="true">
      <table class="data-table">
        <thead>
          <tr>
            <th>申請日</th>
            <th>期間</th>
            <th>種別</th>
            <th>理由</th>
            <th>ステータス</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>2024-06-10</td>
            <td>2024-06-15</td>
            <td>有給</td>
            <td>私用</td>
            <td><app-ui-status-badge status="APPROVED"></app-ui-status-badge></td>
          </tr>
          <tr>
            <td>2024-06-12</td>
            <td>2024-06-20</td>
            <td>有給</td>
            <td>旅行</td>
            <td><app-ui-status-badge status="PENDING"></app-ui-status-badge></td>
          </tr>
        </tbody>
      </table>
    </app-ui-card>
  `,
  styles: [`
    .leave-balance {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: var(--space-4);
    }
    .balance-item {
      display: flex;
      flex-direction: column;
      gap: var(--space-1);
    }
    .balance-label {
      font-size: var(--fs-sm);
      color: var(--color-text-muted);
    }
    .balance-value {
      font-size: var(--fs-xl);
      font-weight: var(--fw-bold);
    }
    .balance-value--highlight {
      color: var(--color-primary);
    }
    .data-table {
      width: 100%;
      border-collapse: collapse;
    }
    .data-table th,
    .data-table td {
      padding: var(--space-3) var(--space-4);
      text-align: left;
      border-bottom: 1px solid var(--color-border);
    }
    .data-table th {
      font-weight: var(--fw-medium);
      color: var(--color-text-muted);
      font-size: var(--fs-sm);
    }
  `],
})
export class LeavePage {
  onApply(): void {
    console.log('Apply leave');
  }
}
