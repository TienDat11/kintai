import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-ui-status-badge',
  standalone: true,
  imports: [NgClass],
  template: `
    <span class="badge" [ngClass]="'badge--' + getStatusClass()">
      {{ getStatusLabel() }}
    </span>
  `,
  styles: [`
    .badge {
      display: inline-flex;
      align-items: center;
      padding: 2px 8px;
      border-radius: 9999px;
      font-size: var(--fs-xs);
      font-weight: var(--fw-medium);
    }
    .badge--success {
      background: #dcfce7;
      color: #166534;
    }
    .badge--warning {
      background: #fef3c7;
      color: #92400e;
    }
    .badge--danger {
      background: #fee2e2;
      color: #991b1b;
    }
    .badge--info {
      background: #e0f2fe;
      color: #075985;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiStatusBadgeComponent {
  @Input() status: string = '';

  getStatusClass(): string {
    switch (this.status) {
      case 'ACTIVE':
      case 'APPROVED':
      case 'PRESENT':
        return 'success';
      case 'PENDING':
      case 'LATE':
        return 'warning';
      case 'INACTIVE':
      case 'RETIRED':
      case 'REJECTED':
      case 'ABSENT':
        return 'danger';
      default:
        return 'info';
    }
  }

  getStatusLabel(): string {
    const labels: Record<string, string> = {
      ACTIVE: '有効',
      INACTIVE: '無効',
      RETIRED: '退職',
      PENDING: '承認待ち',
      APPROVED: '承認済み',
      REJECTED: '却下',
      PRESENT: '出勤',
      LATE: '遅刻',
      ABSENT: '欠勤',
    };
    return labels[this.status] || this.status;
  }
}
