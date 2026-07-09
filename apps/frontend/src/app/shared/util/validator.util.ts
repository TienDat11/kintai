import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * 日本の電話番号バリデーション
 */
export function phoneJpValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    const pattern = /^0\d{1,4}-?\d{1,4}-?\d{3,4}$/;
    return pattern.test(control.value) ? null : { phoneJp: true };
  };
}

/**
 * 郵便番号バリデーション
 */
export function postalCodeValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    const pattern = /^\d{3}-?\d{4}$/;
    return pattern.test(control.value) ? null : { postalCode: true };
  };
}
