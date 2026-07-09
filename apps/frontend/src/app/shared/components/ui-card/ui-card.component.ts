import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-ui-card',
  standalone: true,
  imports: [NgIf],
  template: `
    <div class="card" [class.card--padded]="padded">
      <div class="card__header" *ngIf="title">
        <h3 class="card__title">{{ title }}</h3>
      </div>
      <div class="card__body">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styles: [`
    .card {
      background: var(--color-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-sm);
      border: 1px solid var(--color-border);
    }
    .card--padded .card__body {
      padding: var(--space-4);
    }
    .card__header {
      padding: var(--space-4);
      border-bottom: 1px solid var(--color-border);
    }
    .card__title {
      font-size: var(--fs-base);
      font-weight: var(--fw-bold);
      color: var(--color-text);
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiCardComponent {
  @Input() title: string = '';
  @Input() padded = true;
}
