export interface EmployeeResponse {
  id: number;
  code: string;
  fullName: string;
  fullNameKana: string | null;
  departmentName: string | null;
  email: string | null;
  phone: string | null;
  status: string;
}

export interface EmployeeRequest {
  code: string;
  fullName: string;
  fullNameKana: string | null;
  departmentId: number;
  email: string | null;
  phone: string | null;
  postalCode: string | null;
}

export interface EmployeeSearchForm {
  keyword: string | null;
  departmentId: number | null;
  status: string | null;
  page: number;
  size: number;
}
