import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'zenkaku', standalone: true })
export class ZenkakuPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '';
    return value.normalize('NFKC');
  }
}
