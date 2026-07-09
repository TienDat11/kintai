import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-ui-page-header',
  standalone: true,
  imports: [NgIf],
  template: `
    <div class="page-header">
      <div class="page-header__left">
        <h1 class="page-header__title">{{ title }}</h1>
        <p class="page-header__subtitle" *ngIf="subtitle">{{ subtitle }}</p>
      </div>
      <div class="page-header__right">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--space-5);
    }
    .page-header__title {
      font-size: var(--fs-2xl);
      font-weight: var(--fw-bold);
      color: var(--color-text);
    }
    .page-header__subtitle {
      font-size: var(--fs-sm);
      color: var(--color-text-muted);
      margin-top: var(--space-1);
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiPageHeaderComponent {
  @Input() title: string = '';
  @Input() subtitle: string = '';
}
