import { Component } from '@angular/core';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';
import { UiButtonComponent } from '@shared/components/ui-button/ui-button.component';

@Component({
  selector: 'app-report-page',
  standalone: true,
  imports: [UiPageHeaderComponent, UiCardComponent, UiButtonComponent],
  template: `
    <app-ui-page-header title="レポート" subtitle="Reports">
      <app-ui-button variant="primary" (clicked)="onExportCsv()">
        <i class="pi pi-download"></i> CSV出力
      </app-ui-button>
    </app-ui-page-header>

    <app-ui-card title="勤怠月報" [padded]="true">
      <p class="placeholder-text">Chart.js で実装予定</p>
    </app-ui-card>

    <app-ui-card title="部署別集計" [padded]="true">
      <p class="placeholder-text">Chart.js で実装予定</p>
    </app-ui-card>
  `,
  styles: [`
    .placeholder-text {
      color: var(--color-text-muted);
      text-align: center;
      padding: var(--space-8);
    }
  `],
})
export class ReportPage {
  onExportCsv(): void {
    console.log('Export CSV');
  }
}
