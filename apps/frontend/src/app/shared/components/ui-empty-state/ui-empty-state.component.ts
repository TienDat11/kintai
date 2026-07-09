import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { UiButtonComponent } from '../ui-button/ui-button.component';

@Component({
  selector: 'app-ui-empty-state',
  standalone: true,
  imports: [UiButtonComponent],
  template: `
    <div class="empty-state">
      <i class="empty-state__icon pi pi-inbox"></i>
      <p class="empty-state__message">{{ message }}</p>
      <app-ui-button
        *ngIf="actionLabel"
        variant="primary"
        size="sm"
        (clicked)="action.emit()">
        {{ actionLabel }}
      </app-ui-button>
    </div>
  `,
  styles: [`
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: var(--space-8) var(--space-4);
      color: var(--color-text-muted);
    }
    .empty-state__icon {
      font-size: 48px;
      margin-bottom: var(--space-4);
      opacity: 0.5;
    }
    .empty-state__message {
      font-size: var(--fs-base);
      margin-bottom: var(--space-4);
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiEmptyStateComponent {
  @Input() message: string = 'データがありません';
  @Input() actionLabel: string = '';
  @Output() action = new EventEmitter<void>();
}
