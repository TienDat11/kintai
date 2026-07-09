import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  const userRole = authService.getRole();

  if (!requiredRoles || !userRole) {
    return false;
  }

  if (requiredRoles.includes(userRole)) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
