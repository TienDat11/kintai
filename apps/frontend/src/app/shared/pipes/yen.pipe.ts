import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'yen', standalone: true })
export class YenPipe implements PipeTransform {
  private fmt = new Intl.NumberFormat('ja-JP', {
    style: 'currency',
    currency: 'JPY',
  });

  transform(value: number | null | undefined): string {
    return value == null ? '' : this.fmt.format(value);
  }
}
