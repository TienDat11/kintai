import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '@env/environment';
import { API_PATH } from '@shared/constant/api-path.constant';
import {
  EmployeeRequest,
  EmployeeResponse,
  EmployeeSearchForm,
} from '@shared/model/employee.model';
import { PageResult } from '@shared/model/page-result.model';
import { ApiResponse } from '@shared/model/api-response.model';

@Injectable({ providedIn: 'root' })
export class EmployeeApiService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiBaseUrl;

  search(form: EmployeeSearchForm): Observable<PageResult<EmployeeResponse>> {
    let params = new HttpParams()
      .set('page', form.page.toString())
      .set('size', form.size.toString());

    if (form.keyword) params = params.set('keyword', form.keyword);
    if (form.departmentId) params = params.set('departmentId', form.departmentId.toString());
    if (form.status) params = params.set('status', form.status);

    return this.http
      .get<ApiResponse<PageResult<EmployeeResponse>>>(
        `${this.baseUrl}${API_PATH.EMPLOYEES}`,
        { params }
      )
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<EmployeeResponse> {
    return this.http
      .get<ApiResponse<EmployeeResponse>>(`${this.baseUrl}${API_PATH.EMPLOYEES}/${id}`)
      .pipe(map((res) => res.data));
  }

  create(request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http
      .post<ApiResponse<EmployeeResponse>>(`${this.baseUrl}${API_PATH.EMPLOYEES}`, request)
      .pipe(map((res) => res.data));
  }

  update(id: number, request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http
      .put<ApiResponse<EmployeeResponse>>(`${this.baseUrl}${API_PATH.EMPLOYEES}/${id}`, request)
      .pipe(map((res) => res.data));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${API_PATH.EMPLOYEES}/${id}`);
  }
}
