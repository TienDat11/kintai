import { Injectable, inject } from '@angular/core';
import { EmployeeStore } from '../store/employee.store';
import { EmployeeSearchForm } from '@shared/model/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeFacade {
  private store = inject(EmployeeStore);

  readonly employees = this.store.employees;
  readonly loading = this.store.loading;
  readonly total = this.store.total;
  readonly page = this.store.page;
  readonly size = this.store.size;

  search(form: EmployeeSearchForm): void {
    this.store.loadPage(form);
  }
}
