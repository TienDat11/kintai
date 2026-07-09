const API = '/api/v1';

export const API_PATH = {
  AUTH_LOGIN: `${API}/auth/login`,
  AUTH_REFRESH: `${API}/auth/refresh`,
  EMPLOYEES: `${API}/employees`,
  ATTENDANCE: `${API}/attendance`,
  LEAVES: `${API}/leaves`,
  PAYROLL: `${API}/payroll`,
  REPORTS: `${API}/reports`,
  DEPARTMENTS: `${API}/departments`,
  USERS: `${API}/users`,
} as const;
