import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-ui-loading',
  standalone: true,
  imports: [NgIf],
  template: `
    <div class="loading" [class.loading--overlay]="overlay">
      <div class="loading__spinner"></div>
    </div>
  `,
  styles: [`
    .loading {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: var(--space-8);
    }
    .loading--overlay {
      position: fixed;
      inset: 0;
      background: rgba(255, 255, 255, 0.8);
      z-index: var(--z-modal);
    }
    .loading__spinner {
      width: 40px;
      height: 40px;
      border: 3px solid var(--color-border);
      border-top-color: var(--color-primary);
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiLoadingComponent {
  @Input() overlay = false;
}
