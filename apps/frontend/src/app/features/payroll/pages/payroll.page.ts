import { Component } from '@angular/core';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';
import { YenPipe } from '@shared/pipes/yen.pipe';

@Component({
  selector: 'app-payroll-page',
  standalone: true,
  imports: [UiPageHeaderComponent, UiCardComponent, YenPipe],
  template: `
    <app-ui-page-header title="給与" subtitle="Payroll"></app-ui-page-header>

    <app-ui-card title="今月の給与プレビュー" [padded]="true">
      <table class="payroll-table">
        <thead>
          <tr>
            <th>項目</th>
            <th>金額</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>基本給</td>
            <td class="amount">{{ baseSalary | yen }}</td>
          </tr>
          <tr>
            <td>残業代</td>
            <td class="amount">{{ overtimePay | yen }}</td>
          </tr>
          <tr>
            <td>深夜手当</td>
            <td class="amount">{{ nightPay | yen }}</td>
          </tr>
          <tr>
            <td>通勤手当</td>
            <td class="amount">{{ commuteAllowance | yen }}</td>
          </tr>
          <tr class="total-row">
            <td><strong>支給合計</strong></td>
            <td class="amount"><strong>{{ totalSalary | yen }}</strong></td>
          </tr>
        </tbody>
      </table>
    </app-ui-card>
  `,
  styles: [`
    .payroll-table {
      width: 100%;
      border-collapse: collapse;
    }
    .payroll-table th,
    .payroll-table td {
      padding: var(--space-3) var(--space-4);
      border-bottom: 1px solid var(--color-border);
    }
    .payroll-table th {
      text-align: left;
      font-weight: var(--fw-medium);
      color: var(--color-text-muted);
    }
    .payroll-table td {
      text-align: right;
    }
    .amount {
      font-variant-numeric: tabular-nums;
    }
    .total-row {
      border-top: 2px solid var(--color-border);
    }
    .total-row td {
      padding-top: var(--space-4);
    }
  `],
})
export class PayrollPage {
  baseSalary = 300000;
  overtimePay = 45000;
  nightPay = 12000;
  commuteAllowance = 15000;
  totalSalary = 472000;
}
