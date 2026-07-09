import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'wareki', standalone: true })
export class WarekiPipe implements PipeTransform {
  private fmt = new Intl.DateTimeFormat('ja-JP-u-ca-japanese', {
    era: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  transform(value: Date | string | null | undefined): string {
    if (!value) return '';
    return this.fmt.format(typeof value === 'string' ? new Date(value) : value);
  }
}
