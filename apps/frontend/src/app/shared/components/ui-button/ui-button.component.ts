import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { NgClass, NgIf } from '@angular/common';

@Component({
  selector: 'app-ui-button',
  standalone: true,
  imports: [NgClass, NgIf],
  template: `
    <button
      [type]="type"
      [disabled]="disabled || loading"
      [ngClass]="['btn', 'btn--' + variant, 'btn--' + size]"
      (click)="clicked.emit($event)">
      <span class="btn__spinner" *ngIf="loading" aria-hidden="true"></span>
      <ng-content></ng-content>
    </button>
  `,
  styleUrl: './ui-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiButtonComponent {
  @Input() variant: 'primary' | 'secondary' | 'ghost' | 'danger' = 'primary';
  @Input() size: 'sm' | 'md' = 'md';
  @Input() type: 'button' | 'submit' = 'button';
  @Input() loading = false;
  @Input() disabled = false;
  @Output() clicked = new EventEmitter<MouseEvent>();
}
