import { Injectable, inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { firstValueFrom } from 'rxjs';
import { EmployeeApiService } from '../facade/employee-api.service';
import { EmployeeResponse, EmployeeSearchForm } from '@shared/model/employee.model';
import { PAGE_SIZE } from '@shared/constant/app.constant';

interface EmployeeState {
  employees: EmployeeResponse[];
  total: number;
  page: number;
  size: number;
  loading: boolean;
  error: string | null;
}

const initialState: EmployeeState = {
  employees: [],
  total: 0,
  page: 0,
  size: PAGE_SIZE,
  loading: false,
  error: null,
};

export const EmployeeStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, api = inject(EmployeeApiService)) => ({
    async loadPage(form: EmployeeSearchForm) {
      patchState(store, { loading: true, error: null });
      try {
        const res = await firstValueFrom(api.search(form));
        patchState(store, {
          employees: res.content,
          total: res.totalElements,
          page: form.page,
          size: form.size,
          loading: false,
        });
      } catch (e) {
        console.error('[EmployeeStore] loadPage failed', e);
        patchState(store, { loading: false, error: 'employee.load_failed' });
      }
    },
  }))
);
