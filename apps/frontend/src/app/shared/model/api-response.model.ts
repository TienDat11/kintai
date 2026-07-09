export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error: ApiError | null;
  timestamp: string;
}

export interface ApiError {
  code: string;
  message: string;
  fieldErrors: FieldError[];
}

export interface FieldError {
  field: string;
  message: string;
}
