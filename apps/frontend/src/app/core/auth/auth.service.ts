import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { API_PATH } from '@shared/constant/api-path.constant';
import { environment } from '@env/environment';

interface AuthResponse {
  success: boolean;
  data: {
    accessToken: string;
    refreshToken: string;
    role: string;
    username: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly ROLE_KEY = 'user_role';

  private isAuthenticated = signal<boolean>(this.hasToken());
  readonly loggedIn = computed(() => this.isAuthenticated());

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiBaseUrl}${API_PATH.AUTH_LOGIN}`, {
        username,
        password,
      })
      .pipe(
        tap((res) => {
          if (res.success) {
            localStorage.setItem(this.ACCESS_TOKEN_KEY, res.data.accessToken);
            localStorage.setItem(this.REFRESH_TOKEN_KEY, res.data.refreshToken);
            localStorage.setItem(this.ROLE_KEY, res.data.role);
            this.isAuthenticated.set(true);
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.ROLE_KEY);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  isLoggedIn(): boolean {
    return this.hasToken();
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }
}
