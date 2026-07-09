import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiPageHeaderComponent } from '@shared/components/ui-page-header/ui-page-header.component';
import { UiCardComponent } from '@shared/components/ui-card/ui-card.component';
import { UiButtonComponent } from '@shared/components/ui-button/ui-button.component';
import { UiStatusBadgeComponent } from '@shared/components/ui-status-badge/ui-status-badge.component';
import { UiEmptyStateComponent } from '@shared/components/ui-empty-state/ui-empty-state.component';
import { UiLoadingComponent } from '@shared/components/ui-loading/ui-loading.component';
import { EmployeeFacade } from '../facade/employee.facade';
import { EmployeeSearchForm } from '@shared/model/employee.model';
import { PAGE_SIZE } from '@shared/constant/app.constant';

@Component({
  selector: 'app-employee-list-page',
  standalone: true,
  imports: [
    FormsModule,
    UiPageHeaderComponent,
    UiCardComponent,
    UiButtonComponent,
    UiStatusBadgeComponent,
    UiEmptyStateComponent,
    UiLoadingComponent,
  ],
  template: `
    <app-ui-page-header title="社員一覧" subtitle="Employee List">
      <app-ui-button variant="primary" (clicked)="onCreate()">
        <i class="pi pi-plus"></i> 新規登録
      </app-ui-button>
    </app-ui-page-header>

    <app-ui-card>
      <!-- フィルター -->
      <div class="filter-bar">
        <input
          type="text"
          class="filter-input"
          placeholder="キーワード検索..."
          [(ngModel)]="keyword"
          (keyup.enter)="onSearch()"
        />
        <app-ui-button variant="secondary" size="sm" (clicked)="onSearch()">
          検索
        </app-ui-button>
        <app-ui-button variant="ghost" size="sm" (clicked)="onClear()">
          クリア
        </app-ui-button>
      </div>

      <!-- テーブル -->
      <app-ui-loading *ngIf="facade.loading()"></app-ui-loading>

      <div *ngIf="!facade.loading() && facade.employees().length === 0">
        <app-ui-empty-state message="データがありません"></app-ui-empty-state>
      </div>

      <table class="data-table" *ngIf="!facade.loading() && facade.employees().length > 0">
        <thead>
          <tr>
            <th>コード</th>
            <th>氏名</th>
            <th>フリガナ</th>
            <th>部署</th>
            <th>メール</th>
            <th>ステータス</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let emp of facade.employees()">
            <td>{{ emp.code }}</td>
            <td>{{ emp.fullName }}</td>
            <td>{{ emp.fullNameKana }}</td>
            <td>{{ emp.departmentName }}</td>
            <td>{{ emp.email }}</td>
            <td>
              <app-ui-status-badge [status]="emp.status"></app-ui-status-badge>
            </td>
            <td>
              <app-ui-button variant="ghost" size="sm" (clicked)="onEdit(emp.id)">
                編集
              </app-ui-button>
              <app-ui-button variant="ghost" size="sm" (clicked)="onDelete(emp.id)">
                削除
              </app-ui-button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- ページネーション -->
      <div class="pagination" *ngIf="facade.total() > 0">
        <span class="pagination__info">
          全 {{ facade.total() }} 件中 {{ facade.page() * facade.size() + 1 }} -
          {{ Math.min((facade.page() + 1) * facade.size(), facade.total()) }} 件
        </span>
        <div class="pagination__buttons">
          <app-ui-button
            variant="secondary"
            size="sm"
            [disabled]="facade.page() === 0"
            (clicked)="onPageChange(facade.page() - 1)">
            前へ
          </app-ui-button>
          <app-ui-button
            variant="secondary"
            size="sm"
            [disabled]="(facade.page() + 1) * facade.size() >= facade.total()"
            (clicked)="onPageChange(facade.page() + 1)">
            次へ
          </app-ui-button>
        </div>
      </div>
    </app-ui-card>
  `,
  styles: [`
    .filter-bar {
      display: flex;
      gap: var(--space-2);
      margin-bottom: var(--space-4);
    }
    .filter-input {
      flex: 1;
      height: 36px;
      padding: 0 var(--space-3);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      font-size: var(--fs-base);
    }
    .filter-input:focus {
      outline: none;
      border-color: var(--color-primary);
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
    .data-table tr:hover {
      background: var(--color-primary-weak);
    }
    .pagination {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--space-4) 0;
    }
    .pagination__info {
      font-size: var(--fs-sm);
      color: var(--color-text-muted);
    }
    .pagination__buttons {
      display: flex;
      gap: var(--space-2);
    }
  `],
})
export class EmployeeListPage implements OnInit {
  facade = inject(EmployeeFacade);
  Math = Math;

  keyword = '';
  currentForm: EmployeeSearchForm = {
    keyword: null,
    departmentId: null,
    status: null,
    page: 0,
    size: PAGE_SIZE,
  };

  ngOnInit(): void {
    this.facade.search(this.currentForm);
  }

  onSearch(): void {
    this.currentForm = {
      ...this.currentForm,
      keyword: this.keyword || null,
      page: 0,
    };
    this.facade.search(this.currentForm);
  }

  onClear(): void {
    this.keyword = '';
    this.currentForm = {
      keyword: null,
      departmentId: null,
      status: null,
      page: 0,
      size: PAGE_SIZE,
    };
    this.facade.search(this.currentForm);
  }

  onPageChange(page: number): void {
    this.currentForm = { ...this.currentForm, page };
    this.facade.search(this.currentForm);
  }

  onCreate(): void {
    // TODO: open create dialog
    console.log('Create employee');
  }

  onEdit(id: number): void {
    // TODO: open edit dialog
    console.log('Edit employee', id);
  }

  onDelete(id: number): void {
    // TODO: confirm and delete
    console.log('Delete employee', id);
  }
}
